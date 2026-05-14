package com.bloomcycle.app.data.security

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Availability status for biometric authentication on this device.
 */
enum class BiometricStatus {
    /** Biometric hardware present and enrolled */
    AVAILABLE,
    /** No biometric hardware on device */
    NO_HARDWARE,
    /** Hardware present but no biometrics enrolled */
    NOT_ENROLLED,
    /** Temporarily unavailable */
    UNAVAILABLE
}

/**
 * Wraps Android's BiometricPrompt / BiometricManager for the app.
 *
 * Usage:
 * 1. Check [getStatus] to see if biometric auth is available.
 * 2. Call [authenticate] from a FragmentActivity to show the prompt.
 */
@Singleton
class BiometricAuthManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val biometricManager = BiometricManager.from(context)

    /**
     * Checks if biometric authentication is available on this device.
     */
    fun getStatus(): BiometricStatus {
        val canAuth = biometricManager.canAuthenticate(
            BIOMETRIC_STRONG or BIOMETRIC_WEAK or DEVICE_CREDENTIAL
        )
        return when (canAuth) {
            BiometricManager.BIOMETRIC_SUCCESS -> BiometricStatus.AVAILABLE
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> BiometricStatus.NO_HARDWARE
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> BiometricStatus.NOT_ENROLLED
            else -> BiometricStatus.UNAVAILABLE
        }
    }

    /**
     * Shows the biometric authentication prompt.
     *
     * @param activity The FragmentActivity context required by BiometricPrompt.
     * @param onSuccess Called when authentication succeeds.
     * @param onError Called when authentication fails or is cancelled.
     */
    fun authenticate(
        activity: FragmentActivity,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val executor = ContextCompat.getMainExecutor(activity)

        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                onSuccess()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                // User pressed cancel or back — treat gracefully
                if (errorCode == BiometricPrompt.ERROR_USER_CANCELED ||
                    errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON ||
                    errorCode == BiometricPrompt.ERROR_CANCELED
                ) {
                    onError("Authentication cancelled")
                } else {
                    onError(errString.toString())
                }
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                // Biometric didn't match — prompt stays open for retry
            }
        }

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("BloomCycle Locked")
            .setSubtitle("Verify your identity to access your health data")
            .setAllowedAuthenticators(BIOMETRIC_STRONG or BIOMETRIC_WEAK or DEVICE_CREDENTIAL)
            .build()

        BiometricPrompt(activity, executor, callback).authenticate(promptInfo)
    }
}
