package com.aistudio.smarttoolkit.tyvz.auth

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.aistudio.smarttoolkit.tyvz.R
import com.aistudio.smarttoolkit.tyvz.model.AppLanguage

private enum class AuthMode { SIGN_IN, REGISTER, RESET }

private fun authCopy(language: AppLanguage, key: String): String {
    val rows = mapOf(
        AppLanguage.ENGLISH to listOf("Welcome back","Create your account","Reset password","Sign in to manage your APS TOOLS account and PRO access.","Full name","Email address","Password","Confirm password","Sign in","Create account","Send reset email","Create a new account","Forgot password?","Back to sign in","Continue without an account","Enter your full name.","Enter a valid email address.","Password must be at least 6 characters.","Passwords do not match.","Please wait…","Password reset email sent. Check your inbox.","Account created. Verify your email before signing in."),
        AppLanguage.KANNADA to listOf("ಮತ್ತೆ ಸ್ವಾಗತ","ಹೊಸ ಖಾತೆ ರಚಿಸಿ","ಪಾಸ್‌ವರ್ಡ್ ಮರುಹೊಂದಿಸಿ","ನಿಮ್ಮ APS TOOLS ಖಾತೆ ಮತ್ತು PRO ಪ್ರವೇಶ ನಿರ್ವಹಿಸಲು ಪ್ರವೇಶಿಸಿ.","ಪೂರ್ಣ ಹೆಸರು","ಇಮೇಲ್ ವಿಳಾಸ","ಪಾಸ್‌ವರ್ಡ್","ಪಾಸ್‌ವರ್ಡ್ ದೃಢೀಕರಿಸಿ","ಪ್ರವೇಶಿಸಿ","ಖಾತೆ ರಚಿಸಿ","ಮರುಹೊಂದಿಸುವ ಇಮೇಲ್ ಕಳುಹಿಸಿ","ಹೊಸ ಖಾತೆ ರಚಿಸಿ","ಪಾಸ್‌ವರ್ಡ್ ಮರೆತಿರಾ?","ಪ್ರವೇಶಕ್ಕೆ ಹಿಂತಿರುಗಿ","ಖಾತೆಯಿಲ್ಲದೆ ಮುಂದುವರಿಯಿರಿ","ನಿಮ್ಮ ಪೂರ್ಣ ಹೆಸರನ್ನು ನಮೂದಿಸಿ.","ಸರಿಯಾದ ಇಮೇಲ್ ವಿಳಾಸ ನಮೂದಿಸಿ.","ಪಾಸ್‌ವರ್ಡ್ ಕನಿಷ್ಠ 6 ಅಕ್ಷರಗಳಿರಬೇಕು.","ಎರಡೂ ಪಾಸ್‌ವರ್ಡ್‌ಗಳು ಒಂದೇ ಆಗಿರಬೇಕು.","ದಯವಿಟ್ಟು ಕಾಯಿರಿ…","ಮರುಹೊಂದಿಸುವ ಇಮೇಲ್ ಕಳುಹಿಸಲಾಗಿದೆ. ಇನ್‌ಬಾಕ್ಸ್ ಪರಿಶೀಲಿಸಿ.","ಖಾತೆ ರಚಿಸಲಾಗಿದೆ. ಪ್ರವೇಶಿಸುವ ಮೊದಲು ಇಮೇಲ್ ಪರಿಶೀಲಿಸಿ."),
        AppLanguage.TELUGU to listOf("తిరిగి స్వాగతం","మీ ఖాతాను సృష్టించండి","పాస్‌వర్డ్ రీసెట్","మీ APS TOOLS ఖాతా మరియు PRO యాక్సెస్ నిర్వహించడానికి సైన్ ఇన్ చేయండి.","పూర్తి పేరు","ఈమెయిల్ చిరునామా","పాస్‌వర్డ్","పాస్‌వర్డ్ నిర్ధారించండి","సైన్ ఇన్","ఖాతా సృష్టించండి","రీసెట్ ఈమెయిల్ పంపండి","కొత్త ఖాతా సృష్టించండి","పాస్‌వర్డ్ మర్చిపోయారా?","సైన్ ఇన్‌కు తిరిగి","ఖాతా లేకుండా కొనసాగండి","మీ పూర్తి పేరు నమోదు చేయండి.","చెల్లుబాటు అయ్యే ఈమెయిల్ ఇవ్వండి.","పాస్‌వర్డ్ కనీసం 6 అక్షరాలు ఉండాలి.","పాస్‌వర్డ్‌లు సరిపోలలేదు.","దయచేసి వేచి ఉండండి…","రీసెట్ ఈమెయిల్ పంపబడింది. ఇన్‌బాక్స్ చూడండి.","ఖాతా సృష్టించబడింది. సైన్ ఇన్‌కు ముందు ఈమెయిల్ ధృవీకరించండి."),
        AppLanguage.TAMIL to listOf("மீண்டும் வரவேற்கிறோம்","உங்கள் கணக்கை உருவாக்கவும்","கடவுச்சொல்லை மீட்டமை","உங்கள் APS TOOLS கணக்கு மற்றும் PRO அணுகலை நிர்வகிக்க உள்நுழையவும்.","முழுப் பெயர்","மின்னஞ்சல் முகவரி","கடவுச்சொல்","கடவுச்சொல்லை உறுதிப்படுத்தவும்","உள்நுழை","கணக்கை உருவாக்கு","மீட்டமைப்பு மின்னஞ்சல் அனுப்பு","புதிய கணக்கை உருவாக்கு","கடவுச்சொல் மறந்துவிட்டதா?","உள்நுழைவுக்குத் திரும்பு","கணக்கின்றி தொடரவும்","முழுப் பெயரை உள்ளிடவும்.","சரியான மின்னஞ்சலை உள்ளிடவும்.","கடவுச்சொல் குறைந்தது 6 எழுத்துகள் இருக்க வேண்டும்.","கடவுச்சொற்கள் பொருந்தவில்லை.","தயவுசெய்து காத்திருக்கவும்…","மீட்டமைப்பு மின்னஞ்சல் அனுப்பப்பட்டது. இன்பாக்ஸைப் பார்க்கவும்.","கணக்கு உருவாக்கப்பட்டது. உள்நுழைவதற்கு முன் மின்னஞ்சலைச் சரிபார்க்கவும்."),
        AppLanguage.MARATHI to listOf("पुन्हा स्वागत आहे","तुमचे खाते तयार करा","पासवर्ड रीसेट करा","तुमचे APS TOOLS खाते आणि PRO प्रवेश व्यवस्थापित करण्यासाठी साइन इन करा.","पूर्ण नाव","ईमेल पत्ता","पासवर्ड","पासवर्डची पुष्टी करा","साइन इन","खाते तयार करा","रीसेट ईमेल पाठवा","नवीन खाते तयार करा","पासवर्ड विसरलात?","साइन इनकडे परत","खात्याशिवाय पुढे जा","पूर्ण नाव टाका.","वैध ईमेल पत्ता टाका.","पासवर्ड किमान 6 अक्षरांचा असावा.","पासवर्ड जुळत नाहीत.","कृपया थांबा…","रीसेट ईमेल पाठवला. इनबॉक्स तपासा.","खाते तयार झाले. साइन इनपूर्वी ईमेल सत्यापित करा."),
        AppLanguage.HINDI to listOf("वापसी पर स्वागत है","अपना खाता बनाएं","पासवर्ड रीसेट करें","अपने APS TOOLS खाते और PRO एक्सेस को प्रबंधित करने के लिए साइन इन करें।","पूरा नाम","ईमेल पता","पासवर्ड","पासवर्ड की पुष्टि करें","साइन इन","खाता बनाएं","रीसेट ईमेल भेजें","नया खाता बनाएं","पासवर्ड भूल गए?","साइन इन पर वापस जाएं","खाते के बिना जारी रखें","अपना पूरा नाम दर्ज करें।","मान्य ईमेल पता दर्ज करें।","पासवर्ड कम से कम 6 अक्षरों का होना चाहिए।","पासवर्ड मेल नहीं खाते।","कृपया प्रतीक्षा करें…","रीसेट ईमेल भेजा गया। इनबॉक्स देखें।","खाता बन गया। साइन इन से पहले ईमेल सत्यापित करें।")
    )
    val keys = listOf("signIn","register","reset","subtitle","name","email","password","confirm","submitSignIn","submitRegister","submitReset","newAccount","forgot","back","guest","nameError","emailError","passwordError","confirmError","busy","resetSuccess","registerSuccess")
    val i = keys.indexOf(key)
    return if (i < 0) "" else rows[language]?.get(i) ?: rows[AppLanguage.ENGLISH]!![i]
}

@Composable
fun EmailAuthScreen(
    language: AppLanguage = AppLanguage.ENGLISH,
    onBack: () -> Unit,
    onSignedIn: () -> Unit,
    onContinueWithoutAccount: () -> Unit
) {
    var mode by remember { mutableStateOf(AuthMode.SIGN_IN) }
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmVisible by remember { mutableStateOf(false) }
    var busy by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }
    var error by remember { mutableStateOf(false) }

    BackHandler(enabled = true) {
        when (mode) {
            AuthMode.SIGN_IN -> onBack()
            else -> { mode = AuthMode.SIGN_IN; message = null }
        }
    }

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

    @Composable
    fun PasswordField(value: String, label: String, visible: Boolean, onValue: (String) -> Unit, onToggle: () -> Unit) {
        OutlinedTextField(
            value = value, onValueChange = onValue, modifier = Modifier.fillMaxWidth(),
            label = { Text(label) }, singleLine = true,
            visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                IconButton(onClick = onToggle) {
                    Icon(if (visible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (visible) "Hide password" else "Show password")
                }
            }
        )
    }

    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(16.dp), contentAlignment = Alignment.Center) {
        Card(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.94f),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().weight(1f, fill = false).verticalScroll(rememberScrollState()).padding(horizontal = 22.dp, vertical = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = {
                        if (mode == AuthMode.SIGN_IN) onBack() else { mode = AuthMode.SIGN_IN; message = null }
                    }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
                    Spacer(Modifier.weight(1f))
                    Text("APS TOOLS", fontWeight = FontWeight.Black, style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.weight(1f))
                    Spacer(Modifier.size(48.dp))
                }
                Image(
                    painter = painterResource(id = R.drawable.aps_tools_logo),
                    contentDescription = "APS TOOLS logo",
                    modifier = Modifier.size(84.dp).clip(RoundedCornerShape(20.dp))
                )
                Spacer(Modifier.height(16.dp))
                Text(authCopy(language, when (mode) {
                    AuthMode.SIGN_IN -> "signIn"
                    AuthMode.REGISTER -> "register"
                    AuthMode.RESET -> "reset"
                }), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                Spacer(Modifier.height(8.dp))
                Text(authCopy(language, "subtitle"), textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(22.dp))

                if (mode == AuthMode.REGISTER) {
                    OutlinedTextField(
                        value = fullName, onValueChange = { fullName = it; message = null },
                        modifier = Modifier.fillMaxWidth(), label = { Text(authCopy(language, "name")) },
                        singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                    )
                    Spacer(Modifier.height(12.dp))
                }
                OutlinedTextField(
                    value = email, onValueChange = { email = it.trimStart(); message = null },
                    modifier = Modifier.fillMaxWidth(), label = { Text(authCopy(language, "email")) },
                    singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )
                if (mode != AuthMode.RESET) {
                    Spacer(Modifier.height(12.dp))
                    PasswordField(password, authCopy(language, "password"), passwordVisible,
                        { password = it; message = null }, { passwordVisible = !passwordVisible })
                }
                if (mode == AuthMode.REGISTER) {
                    Spacer(Modifier.height(12.dp))
                    PasswordField(confirmPassword, authCopy(language, "confirm"), confirmVisible,
                        { confirmPassword = it; message = null }, { confirmVisible = !confirmVisible })
                }

                if (message != null) {
                    Spacer(Modifier.height(12.dp))
                    Text(message!!, color = if (error) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center)
                }
                Spacer(Modifier.height(20.dp))
                Button(
                    onClick = {
                        val validEmail = android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()
                        when {
                            mode == AuthMode.REGISTER && fullName.isBlank() -> {
                                error = true; message = authCopy(language, "nameError")
                            }
                            !validEmail -> { error = true; message = authCopy(language, "emailError") }
                            mode != AuthMode.RESET && password.length < 6 -> {
                                error = true; message = authCopy(language, "passwordError")
                            }
                            mode == AuthMode.REGISTER && password != confirmPassword -> {
                                error = true; message = authCopy(language, "confirmError")
                            }
                            else -> {
                                busy = true; error = false; message = null
                                when (mode) {
                                    AuthMode.SIGN_IN -> AuthManager.signIn(email, password) {
                                        complete(it, "Signed in successfully.")
                                    }
                                    AuthMode.REGISTER -> AuthManager.register(fullName, email, password) {
                                        complete(it, authCopy(language, "registerSuccess"))
                                    }
                                    AuthMode.RESET -> AuthManager.sendPasswordReset(email) {
                                        complete(it, authCopy(language, "resetSuccess"))
                                    }
                                }
                            }
                        }
                    },
                    enabled = !busy,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(authCopy(language, when (mode) {
                        AuthMode.SIGN_IN -> "submitSignIn"
                        AuthMode.REGISTER -> "submitRegister"
                        AuthMode.RESET -> "submitReset"
                    }), fontWeight = FontWeight.Bold)
                }

                if (mode == AuthMode.SIGN_IN) {
                    TextButton(onClick = { mode = AuthMode.RESET; message = null }) { Text(authCopy(language, "forgot")) }
                    TextButton(onClick = { mode = AuthMode.REGISTER; message = null }) { Text(authCopy(language, "newAccount")) }
                    TextButton(onClick = onContinueWithoutAccount) { Text(authCopy(language, "guest")) }
                } else if (mode == AuthMode.REGISTER) {
                    TextButton(onClick = { mode = AuthMode.SIGN_IN; message = null }) { Text(authCopy(language, "back")) }
                } else {
                    TextButton(onClick = { mode = AuthMode.SIGN_IN; message = null }) { Text(authCopy(language, "back")) }
                }
            }
        }
    }
}
