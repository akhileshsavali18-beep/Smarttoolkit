package com.aistudio.smarttoolkit.tyvz.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

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
                    if (user != null) onResult(Result.success(user))
                    else onResult(Result.failure(IllegalStateException("ಪ್ರವೇಶ ವಿಫಲವಾಗಿದೆ. ಮತ್ತೆ ಪ್ರಯತ್ನಿಸಿ.")))
                } else {
                    onResult(Result.failure(task.exception ?: Exception("ಪ್ರವೇಶ ವಿಫಲವಾಗಿದೆ.")))
                }
            }
    }

    fun register(email: String, password: String, onResult: (Result<FirebaseUser>) -> Unit) {
        auth.createUserWithEmailAndPassword(email.trim(), password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        user.sendEmailVerification().addOnCompleteListener {
                            onResult(Result.success(user))
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
