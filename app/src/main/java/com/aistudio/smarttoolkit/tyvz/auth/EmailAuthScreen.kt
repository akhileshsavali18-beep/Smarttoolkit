package com.aistudio.smarttoolkit.tyvz.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.aistudio.smarttoolkit.tyvz.R
import com.aistudio.smarttoolkit.tyvz.model.AppLanguage

private enum class AuthMode { SIGN_IN, REGISTER, RESET }

private fun authCopy(language: AppLanguage, key: String): String {
    val copy = mapOf(
        AppLanguage.ENGLISH to listOf("Welcome back","Create your account","Reset password","Sign in to manage your APS TOOLS account and PRO access.","Email address","Password","Sign in","Create account","Send reset email","Create a new account","Forgot password?","Back to sign in","Continue without an account","Enter a valid email address.","Password must be at least 6 characters."),
        AppLanguage.KANNADA to listOf("ಮತ್ತೆ ಸ್ವಾಗತ","ಹೊಸ ಖಾತೆ ರಚಿಸಿ","ಪಾಸ್‌ವರ್ಡ್ ಮರುಹೊಂದಿಸಿ","APS TOOLS ಖಾತೆ ಮತ್ತು PRO ಪ್ರವೇಶ ನಿರ್ವಹಿಸಲು ಪ್ರವೇಶಿಸಿ.","ಇಮೇಲ್ ವಿಳಾಸ","ಪಾಸ್‌ವರ್ಡ್","ಪ್ರವೇಶಿಸಿ","ಖಾತೆ ರಚಿಸಿ","ಮರುಹೊಂದಿಸುವ ಇಮೇಲ್ ಕಳುಹಿಸಿ","ಹೊಸ ಖಾತೆ ರಚಿಸಿ","ಪಾಸ್‌ವರ್ಡ್ ಮರೆತಿರಾ?","ಪ್ರವೇಶಕ್ಕೆ ಹಿಂತಿರುಗಿ","ಖಾತೆಯಿಲ್ಲದೆ ಮುಂದುವರಿಯಿರಿ","ಸರಿಯಾದ ಇಮೇಲ್ ವಿಳಾಸ ನಮೂದಿಸಿ.","ಪಾಸ್‌ವರ್ಡ್ ಕನಿಷ್ಠ 6 ಅಕ್ಷರಗಳಿರಬೇಕು."),
        AppLanguage.TELUGU to listOf("తిరిగి స్వాగతం","ఖాతా సృష్టించండి","పాస్‌వర్డ్ రీసెట్","మీ APS TOOLS ఖాతాను నిర్వహించడానికి సైన్ ఇన్ చేయండి.","ఈమెయిల్ చిరునామా","పాస్‌వర్డ్","సైన్ ఇన్","ఖాతా సృష్టించండి","రీసెట్ ఈమెయిల్ పంపండి","కొత్త ఖాతా సృష్టించండి","పాస్‌వర్డ్ మర్చిపోయారా?","సైన్ ఇన్‌కు తిరిగి","ఖాతా లేకుండా కొనసాగండి","చెల్లుబాటు అయ్యే ఈమెయిల్ ఇవ్వండి.","పాస్‌వర్డ్ కనీసం 6 అక్షరాలు ఉండాలి."),
        AppLanguage.TAMIL to listOf("மீண்டும் வரவேற்கிறோம்","கணக்கை உருவாக்கவும்","கடவுச்சொல்லை மீட்டமை","உங்கள் APS TOOLS கணக்கை நிர்வகிக்க உள்நுழையவும்.","மின்னஞ்சல் முகவரி","கடவுச்சொல்","உள்நுழை","கணக்கை உருவாக்கு","மீட்டமைப்பு மின்னஞ்சல் அனுப்பு","புதிய கணக்கை உருவாக்கு","கடவுச்சொல் மறந்துவிட்டதா?","உள்நுழைவுக்குத் திரும்பு","கணக்கின்றி தொடரவும்","சரியான மின்னஞ்சலை உள்ளிடவும்.","கடவுச்சொல் குறைந்தது 6 எழுத்துகள் இருக்க வேண்டும்."),
        AppLanguage.MARATHI to listOf("पुन्हा स्वागत आहे","खाते तयार करा","पासवर्ड रीसेट करा","तुमचे APS TOOLS खाते व्यवस्थापित करण्यासाठी साइन इन करा.","ईमेल पत्ता","पासवर्ड","साइन इन","खाते तयार करा","रीसेट ईमेल पाठवा","नवीन खाते तयार करा","पासवर्ड विसरलात?","साइन इनकडे परत","खात्याशिवाय पुढे जा","वैध ईमेल पत्ता टाका.","पासवर्ड किमान 6 अक्षरांचा असावा."),
        AppLanguage.HINDI to listOf("वापसी पर स्वागत है","अपना खाता बनाएं","पासवर्ड रीसेट करें","अपने APS TOOLS खाते को प्रबंधित करने के लिए साइन इन करें।","ईमेल पता","पासवर्ड","साइन इन","खाता बनाएं","रीसेट ईमेल भेजें","नया खाता बनाएं","पासवर्ड भूल गए?","साइन इन पर वापस जाएं","खाते के बिना जारी रखें","मान्य ईमेल पता दर्ज करें।","पासवर्ड कम से कम 6 अक्षरों का होना चाहिए।")
    )
    val keys = listOf("signIn","register","reset","subtitle","email","password","submitSignIn","submitRegister","submitReset","newAccount","forgot","back","guest","invalidEmail","shortPassword")
    val index = keys.indexOf(key)
    return if (index < 0) "" else copy[language]?.get(index) ?: copy[AppLanguage.ENGLISH]!!.get(index)
}

@Composable
fun EmailAuthScreen(
    language: AppLanguage = AppLanguage.ENGLISH,
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
                if (mode == AuthMode.SIGN_IN) onSignedIn()
            },
            onFailure = {
                error = true
                message = it.localizedMessage ?: "Request could not be completed. Please try again."
            }
        )
    }

    Box(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher),
                    contentDescription = "APS TOOLS logo",
                    modifier = Modifier.size(72.dp).clip(RoundedCornerShape(18.dp))
                )
                Spacer(Modifier.height(14.dp))
                Text("APS TOOLS", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
                Spacer(Modifier.height(18.dp))
                Text(
                    authCopy(language, when (mode) {
                        AuthMode.SIGN_IN -> "signIn"
                        AuthMode.REGISTER -> "register"
                        AuthMode.RESET -> "reset"
                    }),
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(8.dp))
                Text(authCopy(language, "subtitle"), textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(20.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it; message = null },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(authCopy(language, "email")) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )
                if (mode != AuthMode.RESET) {
                    Spacer(Modifier.height(10.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it; message = null },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(authCopy(language, "password")) },
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
                            message = authCopy(language, "invalidEmail")
                        } else if (mode != AuthMode.RESET && password.length < 6) {
                            error = true
                            message = authCopy(language, "shortPassword")
                        } else {
                            busy = true
                            message = null
                            when (mode) {
                                AuthMode.SIGN_IN -> AuthManager.signIn(email, password) { complete(it, "Signed in successfully.") }
                                AuthMode.REGISTER -> AuthManager.register(email, password) { complete(it, "Account created. Check your email to verify it.") }
                                AuthMode.RESET -> AuthManager.sendPasswordReset(email) { complete(it, "Password reset email sent.") }
                            }
                        }
                    },
                    enabled = !busy,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (busy) "Please wait…" else authCopy(language, when (mode) {
                        AuthMode.SIGN_IN -> "submitSignIn"
                        AuthMode.REGISTER -> "submitRegister"
                        AuthMode.RESET -> "submitReset"
                    }))
                }
                if (mode == AuthMode.SIGN_IN) {
                    TextButton(onClick = { mode = AuthMode.REGISTER; message = null }) { Text(authCopy(language, "newAccount")) }
                    TextButton(onClick = { mode = AuthMode.RESET; message = null }) { Text(authCopy(language, "forgot")) }
                } else {
                    TextButton(onClick = { mode = AuthMode.SIGN_IN; message = null }) { Text(authCopy(language, "back")) }
                }
                TextButton(onClick = onContinueWithoutAccount) { Text(authCopy(language, "guest")) }
            }
        }
    }
}
