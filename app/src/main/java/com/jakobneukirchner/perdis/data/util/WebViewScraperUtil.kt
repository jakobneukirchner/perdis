package com.jakobneukirchner.perdis.data.util

import android.annotation.SuppressLint
import android.content.Context
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import com.jakobneukirchner.perdis.model.Credentials
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class WebViewScraperUtil(private val context: Context) {

    @SuppressLint("SetJavaScriptEnabled")
    suspend fun loginAndScrapeRoster(
        credentials: Credentials
    ): String = suspendCancellableCoroutine { cont ->
        val webView = WebView(context)
        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            databaseEnabled = true
        }

        CookieManager.getInstance().removeAllCookies(null)
        CookieManager.getInstance().flush()

        var loginAttempted = false
        var scraped = false

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String?) {
                if (url?.contains("login", ignoreCase = true) == true && !loginAttempted) {
                    loginAttempted = true
                    val js = """
                        (function() {
                            var userField = document.querySelector('input[name="username"]') || 
                                            document.querySelector('input[name="benutzername"]') ||
                                            document.querySelector('input[type="text"]');
                            var passField = document.querySelector('input[name="password"]') ||
                                            document.querySelector('input[type="password"]');
                            if (userField && passField) {
                                userField.value = '${credentials.username}';
                                passField.value = '${credentials.password}';
                                var form = userField.closest('form') || document.querySelector('form');
                                if (form) form.submit();
                                else {
                                    var buttons = document.querySelectorAll('button[type="submit"], input[type="submit"]');
                                    if (buttons.length > 0) buttons[0].click();
                                }
                            }
                        })();
                    """.trimIndent()
                    view.evaluateJavascript(js, null)
                } else if ((url?.contains("roster", ignoreCase = true) == true || 
                           url?.contains("WebComm", ignoreCase = true) == true) && !scraped) {
                    scraped = true
                    view.evaluateJavascript(
                        "(function(){return document.documentElement.outerHTML;})();"
                    ) { html ->
                        cont.resume(html?.replace("\"", "") ?: "")
                    }
                } else if (url?.contains("default", ignoreCase = true) == true && !loginAttempted) {
                    // Zweiter Login-Versuch auf Startseite
                    loginAttempted = true
                    view.loadUrl("https://perdisweb.verkehrs-ag.de/WebComm/roster.aspx")
                }
            }
        }

        webView.loadUrl("https://perdisweb.verkehrs-ag.de/WebComm/default.aspx")

        // Timeout nach 15 Sekunden
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            if (!scraped) {
                cont.resume("")
            }
        }, 15000)
    }

    @SuppressLint("SetJavaScriptEnabled")
    suspend fun getPdfUrl(
        date: String
    ): String = suspendCancellableCoroutine { cont ->
        val url = "https://perdisweb.verkehrs-ag.de/WebComm/shiprint.aspx?$date"
        cont.resume(url)
    }

    fun clearCookies() {
        CookieManager.getInstance().removeAllCookies(null)
        CookieManager.getInstance().flush()
    }
}
