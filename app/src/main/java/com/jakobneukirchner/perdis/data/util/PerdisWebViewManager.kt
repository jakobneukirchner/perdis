package com.jakobneukirchner.perdis.data.util

import android.annotation.SuppressLint
import android.content.Context
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import com.jakobneukirchner.perdis.model.Credentials
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class PerdisWebViewManager(private val context: Context) {

    private var persistentWebView: WebView? = null
    private var isLoggedIn = false

    @SuppressLint("SetJavaScriptEnabled")
    fun initializePersistentWebView(credentials: Credentials) {
        if (persistentWebView != null) return

        persistentWebView = WebView(context).apply {
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                databaseEnabled = false
                cacheMode = android.webkit.WebSettings.LOAD_DEFAULT
            }
            
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    
                    // Auto-login when on default page
                    if (url?.contains("default.aspx") == true && !isLoggedIn) {
                        performAutoLogin(credentials)
                    }
                    // Mark as logged in when we reach roster
                    else if (url?.contains("roster.aspx") == true) {
                        isLoggedIn = true
                    }
                }
            }
        }

        // Start initial load
        persistentWebView?.loadUrl("https://perdisweb.verkehrs-ag.de/WebComm/default.aspx")
    }

    private fun performAutoLogin(credentials: Credentials) {
        val js = """
            (function() {
                var userField = document.getElementsByName('ctl00\${'$'}cntMainBody\${'$'}lgnView\${'$'}lgnLogin\${'$'}UserName')[0];
                var passField = document.getElementsByName('ctl00\${'$'}cntMainBody\${'$'}lgnView\${'$'}lgnLogin\${'$'}Password')[0];
                var submitBtn = document.getElementById('ctl00_cntMainBody_lgnView_lgnLogin_LoginButton');
                
                if (userField && passField) {
                    userField.value = '${credentials.username}';
                    passField.value = '${credentials.password}';
                    if (submitBtn) submitBtn.click();
                }
            })();
        """.trimIndent()
        persistentWebView?.evaluateJavascript(js, null)
    }

    suspend fun getRosterHtml(): String = suspendCancellableCoroutine { cont ->
        if (!isLoggedIn) {
            cont.resume("")
            return@suspendCancellableCoroutine
        }

        persistentWebView?.evaluateJavascript(
            "(function(){return document.documentElement.outerHTML;})();"
        ) { html ->
            cont.resume(html?.replace("\"", "") ?: "")
        }
    }

    suspend fun getShiftHtml(date: String): String = suspendCancellableCoroutine { cont ->
        if (!isLoggedIn) {
            cont.resume("")
            return@suspendCancellableCoroutine
        }

        persistentWebView?.loadUrl("https://perdisweb.verkehrs-ag.de/WebComm/shift.aspx?$date")
        
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            persistentWebView?.evaluateJavascript(
                "(function(){return document.documentElement.outerHTML;})();"
            ) { html ->
                cont.resume(html?.replace("\"", "") ?: "")
            }
        }, 3000)
    }

    fun isSessionValid(): Boolean = isLoggedIn

    fun logout() {
        isLoggedIn = false
        persistentWebView?.loadUrl("https://perdisweb.verkehrs-ag.de/WebComm/logout.aspx")
        persistentWebView?.clearCache(true)
        CookieManager.getInstance().removeAllCookies(null)
        CookieManager.getInstance().flush()
    }

    fun destroy() {
        persistentWebView?.destroy()
        persistentWebView = null
        isLoggedIn = false
    }
}
