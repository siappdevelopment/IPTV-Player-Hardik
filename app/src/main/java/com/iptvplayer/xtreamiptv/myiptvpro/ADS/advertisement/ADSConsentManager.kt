package com.iptvplayer.xtreamiptv.myiptvpro.ADS.advertisement

import android.app.Activity
import android.content.Context
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.FormError
import com.google.android.ump.UserMessagingPlatform
import java.util.concurrent.atomic.AtomicBoolean

object ADSConsentManager {

    /** From Logcat: UserMessagingPlatform → addTestDeviceHashedId(...) */
    private const val DEBUG_TEST_DEVICE_HASHED_ID = "AAD713CC5B545272A1448524BCCF0D05"

    /**
     * @param showForm When false, only refreshes consent info (no form). Use on splash when
     *                 Firebase targets the language screen.
     * @param onComplete Called with true when the consent form was shown and dismissed.
     */
    @JvmStatic
    fun gatherConsent(activity: Activity, showForm: Boolean, onComplete: (Boolean) -> Unit) {
        val completed = AtomicBoolean(false)
        val finish: (Boolean) -> Unit = { formShown ->
            if (completed.compareAndSet(false, true)) {
                onComplete(formShown)
            }
        }

        if (activity.isFinishing || activity.isDestroyed) {
            finish(false)
            return
        }

        // Safe point: UMP needs the network, so offline it can never deliver a callback.
        if (!ADSUtilitis.IsNetworkConnected(activity)) {
            finish(false)
            return
        }

        val consentInformation = UserMessagingPlatform.getConsentInformation(activity)
        val paramsBuilder = ConsentRequestParameters.Builder()

        try {
            consentInformation.requestConsentInfoUpdate(
                activity,
                paramsBuilder.build(),
                {
                    logConsentState(consentInformation, "after requestConsentInfoUpdate")

                    if (activity.isFinishing || activity.isDestroyed) {
                        finish(false)
                        return@requestConsentInfoUpdate
                    }

                    if (!showForm) {
                        finish(false)
                        return@requestConsentInfoUpdate
                    }

                    if (!consentInformation.isConsentFormAvailable) {
                        finish(false)
                        return@requestConsentInfoUpdate
                    }

                    val consentRequired =
                        consentInformation.consentStatus == ConsentInformation.ConsentStatus.REQUIRED
                    if (!consentRequired) {
                        finish(false)
                        return@requestConsentInfoUpdate
                    }

                    // Safe point: a throw here would otherwise leave the caller waiting forever.
                    try {
                        UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) { formError ->
                            if (formError != null) {
                                logFormError("loadAndShowConsentFormIfRequired", formError)
                                finish(false)
                            } else {
                                logConsentState(consentInformation, "after consent form")
                                if (consentInformation.canRequestAds()) {
                                    ADSUtilitis.trackPermissionAllowOnce(activity, "ConsentIn_Allow")
                                }
                                finish(true)
                            }
                        }
                    } catch (t: Throwable) {
                        t.printStackTrace()
                        finish(false)
                    }
                },
                { formError ->
                    logFormError("requestConsentInfoUpdate", formError)
                    finish(false)
                }
            )
        } catch (t: Throwable) {
            // Safe point: consent SDK failed to even start the request.
            t.printStackTrace()
            finish(false)
        }
    }

    /** Splash was configured but the form did not appear — retry on language. */
    @JvmStatic
    fun shouldRetryConsentOnLanguage(context: Context): Boolean {
        if (canRequestAds(context)) {
            return false
        }
        val consentInformation = UserMessagingPlatform.getConsentInformation(context)
        return consentInformation.consentStatus == ConsentInformation.ConsentStatus.REQUIRED ||
            consentInformation.isConsentFormAvailable
    }

    @JvmStatic
    fun canRequestAds(context: Context): Boolean {
        return UserMessagingPlatform.getConsentInformation(context).canRequestAds()
    }

    private fun logConsentState(consentInformation: ConsentInformation, stage: String) {

    }

    private fun logFormError(stage: String, formError: FormError) {

    }
}
