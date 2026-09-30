package com.aistudio.smarttoolkit.tyvz.update

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.core.content.FileProvider
import com.aistudio.smarttoolkit.tyvz.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.File

data class UpdateInfo(
    val versionName: String,
    val downloadUrl: String,
    val sizeBytes: Long
)

object UpdateManager {
    private const val LATEST_RELEASE_API =
        "https://api.github.com/repos/akhileshsavali18-beep/Smarttoolkit/releases/latest"

    private val client = OkHttpClient()

    suspend fun checkForUpdate(): UpdateInfo? = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url(LATEST_RELEASE_API)
                .header("Accept", "application/vnd.github+json")
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@withContext null
                val body = response.body?.string() ?: return@withContext null
                val json = JSONObject(body)
                val tag = json.optString("tag_name").removePrefix("v")
                val asset = json.optJSONArray("assets")
                    ?.let { assets ->
                        (0 until assets.length())
                            .map { assets.getJSONObject(it) }
                            .firstOrNull { it.optString("name").equals("APSTool.apk", true) }
                    }

                val downloadUrl = asset?.optString("browser_download_url").orEmpty()
                val sizeBytes = asset?.optLong("size", 0L) ?: 0L
                if (tag.isBlank() || downloadUrl.isBlank()) return@withContext null
                if (!isNewerVersion(tag, BuildConfig.VERSION_NAME)) return@withContext null

                UpdateInfo(tag, downloadUrl, sizeBytes)
            }
        } catch (_: Throwable) {
            null
        }
    }

    private fun isNewerVersion(latest: String, current: String): Boolean {
        val a = latest.split(".").mapNotNull { it.toIntOrNull() }
        val b = current.split(".").mapNotNull { it.toIntOrNull() }
        val size = maxOf(a.size, b.size)
        for (i in 0 until size) {
            val av = a.getOrElse(i) { 0 }
            val bv = b.getOrElse(i) { 0 }
            if (av != bv) return av > bv
        }
        return false
    }

    suspend fun downloadAndInstall(
        context: Context,
        updateInfo: UpdateInfo,
        onProgress: (Int) -> Unit = {}
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder().url(updateInfo.downloadUrl).build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext Result.failure(
                        Exception("Download failed: HTTP " + response.code)
                    )
                }

                val body = response.body
                    ?: return@withContext Result.failure(Exception("Empty APK download"))

                val totalBytes = body.contentLength().takeIf { it > 0L } ?: updateInfo.sizeBytes
                val apkFile = File(context.cacheDir, "APSTool-" + updateInfo.versionName + ".apk")
                var downloadedBytes = 0L
                var lastProgress = -1

                body.byteStream().use { input ->
                    apkFile.outputStream().use { output ->
                        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                        while (true) {
                            val read = input.read(buffer)
                            if (read == -1) break
                            output.write(buffer, 0, read)
                            downloadedBytes += read

                            if (totalBytes > 0L) {
                                val progress = ((downloadedBytes * 100L) / totalBytes)
                                    .coerceIn(0L, 100L).toInt()
                                if (progress != lastProgress) {
                                    lastProgress = progress
                                    withContext(Dispatchers.Main) {
                                        onProgress(progress)
                                    }
                                }
                            }
                        }
                    }
                }

                withContext(Dispatchers.Main) {
                    onProgress(100)
                    installApk(context, apkFile)
                }
                Result.success(Unit)
            }
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }

    private fun installApk(context: Context, apkFile: File) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O &&
            !context.packageManager.canRequestPackageInstalls()
        ) {
            val settingsIntent = Intent(
                Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                Uri.parse("package:" + context.packageName)
            ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(settingsIntent)
            return
        }

        val apkUri = FileProvider.getUriForFile(
            context,
            context.packageName + ".fileprovider",
            apkFile
        )
        val installIntent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(apkUri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(installIntent)
    }
}
