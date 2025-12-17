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
                            var userField = document.querySelector('input[type="text"]');
                            var passField = document.querySelector('input[type="password"]');
                            if (userField && passField) {
                                userField.value = '${credentials.username}';
                                passField.value = '${credentials.password}';
                                var form = userField.closest('form');
                                if (form) form.submit();
                            }
                        })();
                    """.trimIndent()
                    view.evaluateJavascript(js, null)
                } else if ((url?.contains("shift", ignoreCase = true) == true || 
                           url?.contains("WebComm", ignoreCase = true) == true) && !scraped) {
                    scraped = true
                    view.evaluateJavascript(
                        "(function(){return document.documentElement.outerHTML;})();"
                    ) { html ->
                        cont.resume(html?.replace("\"", "") ?: "")
                    }
                }
            }
        }

        webView.loadUrl("https://perdisweb.verkehrs-ag.de/WebComm/default.aspx")

        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            if (!scraped) {
                cont.resume("")
            }
        }, 15000)
    }

    fun getShiftUrl(date: String): String {
        return "https://perdisweb.verkehrs-ag.de/WebComm/shift.aspx?$date"
    }

    fun getRosterUrl(date: String): String {
        return "https://perdisweb.verkehrs-ag.de/WebComm/roster.aspx?$date"
    }

    fun getPdfUrl(date: String): String {
        return "https://perdisweb.verkehrs-ag.de/WebComm/shiprint.aspx?$date"
    }

    fun clearCookies() {
        CookieManager.getInstance().removeAllCookies(null)
        CookieManager.getInstance().flush()
    }
}
