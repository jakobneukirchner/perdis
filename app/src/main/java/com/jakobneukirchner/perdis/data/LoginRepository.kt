package com.jakobneukirchner.perdis.data

import com.jakobneukirchner.perdis.data.util.CredentialsManager
import com.jakobneukirchner.perdis.data.util.PerdisWebViewManager
import com.jakobneukirchner.perdis.model.Credentials
import kotlinx.coroutines.delay

class LoginRepository(
    private val credentialsManager: CredentialsManager,
    private val webViewManager: PerdisWebViewManager
) {

    suspend fun login(credentials: Credentials): Boolean {
        return try {
            // Initialize persistent WebView with credentials
            webViewManager.initializePersistentWebView(credentials)
            
            // Wait for login to complete (max 30 seconds)
            var attempts = 0
            while (!webViewManager.isSessionValid() && attempts < 30) {
                delay(1000)
                attempts++
            }
            
            val success = webViewManager.isSessionValid()
            if (success) {
                credentialsManager.saveCredentials(credentials)
            }
            success
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun loadSavedCredentials(): Credentials? = credentialsManager.loadCredentials()

    fun logout() {
        webViewManager.logout()
        credentialsManager.clearCredentials()
    }

    fun hasCredentials(): Boolean = credentialsManager.hasCredentials()
    
    fun isSessionValid(): Boolean = webViewManager.isSessionValid()
}
