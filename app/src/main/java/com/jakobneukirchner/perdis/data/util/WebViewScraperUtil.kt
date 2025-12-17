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
                            // Find all input fields
                            var inputs = document.getElementsByTagName('input');
                            var username = null;
                            var password = null;
                            
                            // Try to find username and password fields by type or name
                            for (var i = 0; i < inputs.length; i++) {
                                var type = inputs[i].type;
                                var name = inputs[i].name || '';
                                
                                if (type === 'text' && !password) {
                                    username = inputs[i];
                                } else if (type === 'password') {
                                    password = inputs[i];
                                }
                            }
                            
                            // Fill in credentials
                            if (username && password) {
                                username.value = '${credentials.username}';
                                password.value = '${credentials.password}';
                                
                                // Try to find and click submit button
                                var buttons = document.getElementsByTagName('button');
                                for (var j = 0; j < buttons.length; j++) {
                                    buttons[j].click();
                                    break;
                                }
                                
                                // If no button, try form submit
                                if (buttons.length === 0) {
                                    var form = username.closest('form');
                                    if (form) form.submit();
                                }
                            }
                        })();
                    """.trimIndent()
                    view.evaluateJavascript(js, null)
                }
                // Scrape on shift or roster page
                else if (!scraped && (url?.contains("shift.aspx", ignoreCase = true) == true || 
                         url?.contains("roster.aspx", ignoreCase = true) == true)) {
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
