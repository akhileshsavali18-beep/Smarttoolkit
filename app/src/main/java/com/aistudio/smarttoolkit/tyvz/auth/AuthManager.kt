package com.aistudio.smarttoolkit.tyvz.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest

/**
 * Email/password authentication only. Subscription entitlement must never be
 * inferred from this class; the payment backend remains authoritative.
 */
object AuthManager {
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    val currentUser: FirebaseUser?
        get() = auth.currentUser

    fun signIn(email: String, password: String, onResult: (Result<FirebaseUser>) -> Unit) {
        auth.signInWithEmailAndPassword(email.trim(), password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user == null) {
                        onResult(Result.failure(IllegalStateException("ಪ್ರವೇಶ ವಿಫಲವಾಗಿದೆ. ಮತ್ತೆ ಪ್ರಯತ್ನಿಸಿ.")))
                    } else if (!user.isEmailVerified) {
                        auth.signOut()
                        onResult(Result.failure(IllegalStateException("ಮುಂದುವರಿಯುವ ಮೊದಲು ನಿಮ್ಮ ಇಮೇಲ್ ಪರಿಶೀಲಿಸಿ.")))
                    } else {
                        onResult(Result.success(user))
                    }
                } else {
                    onResult(Result.failure(task.exception ?: Exception("ಪ್ರವೇಶ ವಿಫಲವಾಗಿದೆ.")))
                }
            }
    }

    fun register(fullName: String, email: String, password: String, onResult: (Result<FirebaseUser>) -> Unit) {
        auth.createUserWithEmailAndPassword(email.trim(), password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        val profile = UserProfileChangeRequest.Builder()
                            .setDisplayName(fullName.trim())
                            .build()
                        user.updateProfile(profile).addOnCompleteListener { profileTask ->
                            if (!profileTask.isSuccessful) {
                                onResult(Result.failure(profileTask.exception ?: Exception("ಹೆಸರು ಉಳಿಸಲಾಗಲಿಲ್ಲ.")))
                            } else {
                                user.sendEmailVerification().addOnCompleteListener { verificationTask ->
                                    if (verificationTask.isSuccessful) {
                                        auth.signOut()
                                        onResult(Result.success(user))
                                    } else {
                                        onResult(Result.failure(
                                            verificationTask.exception ?: Exception("ಪರಿಶೀಲನಾ ಇಮೇಲ್ ಕಳುಹಿಸಲಾಗಲಿಲ್ಲ.")
                                        ))
                                    }
                                }
                            }
                        }
                    } else {
                        onResult(Result.failure(IllegalStateException("ಖಾತೆ ರಚನೆಯಾಗಲಿಲ್ಲ.")))
                    }
                } else {
                    onResult(Result.failure(task.exception ?: Exception("ಖಾತೆ ರಚನೆ ವಿಫಲವಾಗಿದೆ.")))
                }
            }
    }

    fun sendPasswordReset(email: String, onResult: (Result<Unit>) -> Unit) {
        auth.sendPasswordResetEmail(email.trim()).addOnCompleteListener { task ->
            if (task.isSuccessful) onResult(Result.success(Unit))
            else onResult(Result.failure(task.exception ?: Exception("ಮರುಹೊಂದಿಸುವ ಇಮೇಲ್ ಕಳುಹಿಸಲಾಗಲಿಲ್ಲ.")))
        }
    }

    fun signOut() = auth.signOut()
}
