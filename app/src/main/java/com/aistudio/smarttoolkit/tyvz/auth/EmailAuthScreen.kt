package com.aistudio.smarttoolkit.tyvz.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

private enum class AuthMode { SIGN_IN, REGISTER, RESET }

@Composable
fun EmailAuthScreen(
    onSignedIn: () -> Unit,
    onContinueWithoutAccount: () -> Unit
) {
    var mode by remember { mutableStateOf(AuthMode.SIGN_IN) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var busy by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }
    var error by remember { mutableStateOf(false) }

    fun complete(result: Result<*>, successMessage: String) {
        busy = false
        result.fold(
            onSuccess = {
                error = false
                message = successMessage
                if (mode != AuthMode.RESET) onSignedIn()
            },
            onFailure = {
                error = true
                message = it.localizedMessage ?: "ವಿನಂತಿ ಪೂರ್ಣಗೊಳ್ಳಲಿಲ್ಲ. ಮತ್ತೆ ಪ್ರಯತ್ನಿಸಿ."
            }
        )
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = when (mode) {
                AuthMode.SIGN_IN -> "APS TOOLS ಖಾತೆಗೆ ಪ್ರವೇಶಿಸಿ"
                AuthMode.REGISTER -> "ಹೊಸ ಖಾತೆ ತೆರೆಯಿರಿ"
                AuthMode.RESET -> "ಪಾಸ್‌ವರ್ಡ್ ಮರುಹೊಂದಿಸಿ"
            },
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(Modifier.height(8.dp))
        Text("PRO ಚಂದಾದಾರಿಕೆ ಮತ್ತು ಖಾತೆ ವಿವರಗಳನ್ನು ನಿರ್ವಹಿಸಲು ಇಮೇಲ್ ಬಳಸಿ.")
        Spacer(Modifier.height(20.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it; message = null },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("ಇಮೇಲ್") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        if (mode != AuthMode.RESET) {
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it; message = null },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("ಪಾಸ್‌ವರ್ಡ್") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )
        }
        if (message != null) {
            Spacer(Modifier.height(10.dp))
            Text(message!!, color = if (error) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary)
        }
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                val validEmail = android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()
                if (!validEmail) {
                    error = true
                    message = "ಸರಿಯಾದ ಇಮೇಲ್ ನಮೂದಿಸಿ."
                } else if (mode != AuthMode.RESET && password.length < 6) {
                    error = true
                    message = "ಪಾಸ್‌ವರ್ಡ್ ಕನಿಷ್ಠ 6 ಅಕ್ಷರಗಳಿರಬೇಕು."
                } else {
                    busy = true
                    message = null
                    when (mode) {
                        AuthMode.SIGN_IN -> AuthManager.signIn(email, password) {
                            complete(it, "ಪ್ರವೇಶ ಯಶಸ್ವಿಯಾಗಿದೆ.")
                        }
                        AuthMode.REGISTER -> AuthManager.register(email, password) {
                            complete(it, "ಖಾತೆ ರಚಿಸಲಾಗಿದೆ. ಇಮೇಲ್ ಪರಿಶೀಲಿಸಿ.")
                        }
                        AuthMode.RESET -> AuthManager.sendPasswordReset(email) {
                            complete(it, "ಪಾಸ್‌ವರ್ಡ್ ಮರುಹೊಂದಿಸುವ ಇಮೇಲ್ ಕಳುಹಿಸಲಾಗಿದೆ.")
                        }
                    }
                }
            },
            enabled = !busy,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (busy) "ದಯವಿಟ್ಟು ಕಾಯಿರಿ…" else when (mode) {
                AuthMode.SIGN_IN -> "ಪ್ರವೇಶಿಸಿ"
                AuthMode.REGISTER -> "ಖಾತೆ ರಚಿಸಿ"
                AuthMode.RESET -> "ಮರುಹೊಂದಿಸುವ ಇಮೇಲ್ ಕಳುಹಿಸಿ"
            })
        }
        if (mode == AuthMode.SIGN_IN) {
            TextButton(onClick = { mode = AuthMode.REGISTER; message = null }) {
                Text("ಹೊಸ ಖಾತೆ ತೆರೆಯಿರಿ")
            }
            TextButton(onClick = { mode = AuthMode.RESET; message = null }) {
                Text("ಪಾಸ್‌ವರ್ಡ್ ಮರೆತಿದ್ದೀರಾ?")
            }
        } else {
            TextButton(onClick = { mode = AuthMode.SIGN_IN; message = null }) {
                Text("ಪ್ರವೇಶ ಪರದೆಗೆ ಹಿಂತಿರುಗಿ")
            }
        }
        TextButton(onClick = onContinueWithoutAccount) {
            Text("ಈಗ ಖಾತೆಯಿಲ್ಲದೆ ಮುಂದುವರಿಯಿರಿ")
        }
    }
}
