package com.jakobneukirchner.perdis.data

import com.jakobneukirchner.perdis.data.util.CredentialsManager
import com.jakobneukirchner.perdis.data.util.WebViewScraperUtil
import com.jakobneukirchner.perdis.model.Credentials

class LoginRepository(
    private val credentialsManager: CredentialsManager,
    private val webViewScraper: WebViewScraperUtil
) {

    suspend fun login(credentials: Credentials): Boolean {
        return try {
            val html = webViewScraper.loginAndScrapeRoster(credentials)
            val success = html.isNotEmpty() && 
                         (html.contains("roster", ignoreCase = true) || 
                          html.contains("dienst", ignoreCase = true))
            
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
        webViewScraper.clearCookies()
        credentialsManager.clearCredentials()
    }

    fun hasCredentials(): Boolean = credentialsManager.hasCredentials()
}
