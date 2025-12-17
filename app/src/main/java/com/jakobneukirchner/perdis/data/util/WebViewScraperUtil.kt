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
        }

        CookieManager.getInstance().removeAllCookies(null)
        CookieManager.getInstance().flush()

        var loginAttempted = false
        var scraped = false

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String?) {
                // Try login on default.aspx page
                if (!loginAttempted && url?.contains("default.aspx", ignoreCase = true) == true) {
                    loginAttempted = true
                    val js = """
                        (function() {
                            // Use exact ASP.NET field names from HTML
                            var userField = document.getElementsByName('ctl00\$cntMainBody\$lgnView\$lgnLogin\$UserName')[0];
                            var passField = document.getElementsByName('ctl00\$cntMainBody\$lgnView\$lgnLogin\$Password')[0];
                            var submitBtn = document.getElementById('ctl00_cntMainBody_lgnView_lgnLogin_LoginButton');
                            
                            if (userField && passField) {
                                userField.value = '${credentials.username}';
                                passField.value = '${credentials.password}';
                                
                                // Click submit button
                                if (submitBtn) {
                                    submitBtn.click();
                                } else {
                                    // Fallback: submit the form
                                    var form = userField.closest('form');
                                    if (form) form.submit();
                                }
                            }
                        })();
                    """.trimIndent()
                    view.evaluateJavascript(js, null)
                }
                // Scrape on roster page (after login redirect)
                else if (!scraped && url?.contains("roster.aspx", ignoreCase = true) == true) {
                    scraped = true
                    view.evaluateJavascript(
                        "(function(){return document.documentElement.outerHTML;})();"
                    ) { html ->
                        cont.resume(html?.replace("\"", "") ?: "")
                    }
                }
                // Scrape on shift page as fallback
                else if (!scraped && url?.contains("shift.aspx", ignoreCase = true) == true) {
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

        // Extended timeout - 30 seconds for login process
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            if (!scraped) {
                cont.resume("")
            }
        }, 30000)
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
