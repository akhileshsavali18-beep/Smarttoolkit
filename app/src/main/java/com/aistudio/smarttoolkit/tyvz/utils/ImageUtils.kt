package com.aistudio.smarttoolkit.tyvz.utils

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

object ImageUtils {

    fun loadBitmapFromUri(context: Context, uri: Uri, maxDimension: Int = 2048): Bitmap? {
        return try {
            val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            context.contentResolver.openInputStream(uri)?.use {
                BitmapFactory.decodeStream(it, null, options)
            }

            var inSampleSize = 1
            if (options.outHeight > maxDimension || options.outWidth > maxDimension) {
                val halfHeight = options.outHeight / 2
                val halfWidth = options.outWidth / 2
                while ((halfHeight / inSampleSize) >= maxDimension && (halfWidth / inSampleSize) >= maxDimension) {
                    inSampleSize *= 2
                }
            }

            val decodeOptions = BitmapFactory.Options().apply {
                this.inSampleSize = inSampleSize
            }

            context.contentResolver.openInputStream(uri)?.use {
                BitmapFactory.decodeStream(it, null, decodeOptions)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getUriFileSize(context: Context, uri: Uri): Long {
        return try {
            var size: Long = -1
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val sizeIndex = it.getColumnIndex(OpenableColumns.SIZE)
                    if (sizeIndex != -1) {
                        size = it.getLong(sizeIndex)
                    }
                }
            }
            if (size <= 0) {
                context.contentResolver.openInputStream(uri)?.use {
                    size = it.available().toLong()
                }
            }
            size
        } catch (e: Exception) {
            e.printStackTrace()
            0L
        }
    }

    fun compressBitmap(bitmap: Bitmap, quality: Int): ByteArray {
        val stream = ByteArrayOutputStream()
        val safeQuality = quality.coerceIn(5, 100)
        bitmap.compress(Bitmap.CompressFormat.JPEG, safeQuality, stream)
        return stream.toByteArray()
    }

    fun resizeBitmap(bitmap: Bitmap, targetWidth: Int, targetHeight: Int): Bitmap {
        val safeWidth = targetWidth.coerceAtLeast(1)
        val safeHeight = targetHeight.coerceAtLeast(1)
        return Bitmap.createScaledBitmap(bitmap, safeWidth, safeHeight, true)
    }

    fun saveBytesToCacheFile(context: Context, data: ByteArray, fileName: String): File {
        val dir = File(context.cacheDir, "compressed_images")
        if (!dir.exists()) dir.mkdirs()
        val file = File(dir, fileName)
        FileOutputStream(file).use {
            it.write(data)
            it.flush()
        }
        return file
    }

    fun generatePdfFromBitmaps(context: Context, bitmaps: List<Bitmap>, outputFileName: String = "APS_Tools_Document.pdf"): File {
        val pdfDocument = PdfDocument()

        // Standard A4 size in PostScript points: 595 x 842 points
        val pageWidth = 595
        val pageHeight = 842

        for (index in bitmaps.indices) {
            val bitmap = bitmaps[index]
            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, index + 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas: Canvas = page.canvas

            // Fill white background
            canvas.drawColor(android.graphics.Color.WHITE)

            // Scale and center bitmap on page leaving 20pt margin
            val margin = 20f
            val availableWidth = pageWidth - (margin * 2)
            val availableHeight = pageHeight - (margin * 2)

            val widthRatio = availableWidth / bitmap.width.toFloat()
            val heightRatio = availableHeight / bitmap.height.toFloat()
            val scale = minOf(widthRatio, heightRatio)

            val scaledW = bitmap.width * scale
            val scaledH = bitmap.height * scale
            val left = margin + ((availableWidth - scaledW) / 2f)
            val top = margin + ((availableHeight - scaledH) / 2f)

            val matrix = Matrix().apply {
                postScale(scale, scale)
                postTranslate(left, top)
            }

            val paint = Paint(Paint.FILTER_BITMAP_FLAG)
            canvas.drawBitmap(bitmap, matrix, paint)

            pdfDocument.finishPage(page)
        }

        val outputDir = File(context.cacheDir, "pdf_exports")
        if (!outputDir.exists()) outputDir.mkdirs()
        val pdfFile = File(outputDir, outputFileName)
        FileOutputStream(pdfFile).use { fos ->
            pdfDocument.writeTo(fos)
        }
        pdfDocument.close()
        return pdfFile
    }

    fun shareFile(context: Context, file: File, mimeType: String, chooserTitle: String = "Share File") {
        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = mimeType
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, chooserTitle))
        } catch (e: Exception) {
            // Fallback for direct share
            val uri = Uri.fromFile(file)
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = mimeType
                putExtra(Intent.EXTRA_STREAM, uri)
            }
            context.startActivity(Intent.createChooser(intent, chooserTitle))
        }
    }

    /**
     * Saves an image byte array or bitmap directly to Android Gallery / Pictures/APSTools
     * using MediaStore without requiring broad legacy storage permissions.
     */
    fun saveImageToGallery(
        context: Context,
        imageBytes: ByteArray,
        displayName: String,
        mimeType: String = "image/jpeg",
        showToast: Boolean = true
    ): Uri? {
        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
            put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/APSTools")
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            }
        }

        var savedUri: Uri? = null
        try {
            val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            } else {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }

            savedUri = resolver.insert(collection, contentValues)
            if (savedUri != null) {
                resolver.openOutputStream(savedUri)?.use { outputStream ->
                    outputStream.write(imageBytes)
                    outputStream.flush()
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    contentValues.clear()
                    contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                    resolver.update(savedUri, contentValues, null, null)
                }

                if (showToast) {
                    Toast.makeText(context, "Image saved successfully to Gallery!", Toast.LENGTH_SHORT).show()
                }
            } else {
                if (showToast) {
                    Toast.makeText(context, "Failed to save image to Gallery", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            if (showToast) {
                Toast.makeText(context, "Error saving image: ${e.localizedMessage ?: "Unknown error"}", Toast.LENGTH_SHORT).show()
            }
        }
        return savedUri
    }

    /**
     * Convenience method to save a Bitmap to Gallery
     */
    fun saveBitmapToGallery(
        context: Context,
        bitmap: Bitmap,
        displayName: String,
        format: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG,
        quality: Int = 100,
        showToast: Boolean = true
    ): Uri? {
        val stream = ByteArrayOutputStream()
        bitmap.compress(format, quality, stream)
        val mimeType = if (format == Bitmap.CompressFormat.JPEG) "image/jpeg" else "image/png"
        return saveImageToGallery(context, stream.toByteArray(), displayName, mimeType, showToast)
    }

    /**
     * Saves a PDF file to Android Documents / APSTools using MediaStore
     */
    fun savePdfToDocuments(
        context: Context,
        pdfFile: File,
        displayName: String,
        showToast: Boolean = true
    ): Uri? {
        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
            put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS + "/APSTools")
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            }
        }

        var savedUri: Uri? = null
        try {
            val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            } else {
                MediaStore.Files.getContentUri("external")
            }

            savedUri = resolver.insert(collection, contentValues)
            if (savedUri != null) {
                resolver.openOutputStream(savedUri)?.use { outputStream ->
                    pdfFile.inputStream().use { inputStream ->
                        inputStream.copyTo(outputStream)
                    }
                    outputStream.flush()
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    contentValues.clear()
                    contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                    resolver.update(savedUri, contentValues, null, null)
                }

                if (showToast) {
                    Toast.makeText(context, "PDF saved successfully to Documents!", Toast.LENGTH_SHORT).show()
                }
            } else {
                if (showToast) {
                    Toast.makeText(context, "Failed to save PDF to Documents", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            if (showToast) {
                Toast.makeText(context, "Error saving PDF: ${e.localizedMessage ?: "Unknown error"}", Toast.LENGTH_SHORT).show()
            }
        }
        return savedUri
    }

    fun shareText(context: Context, text: String, title: String = "Share") {
        try {
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, text)
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, title)
            context.startActivity(shareIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
