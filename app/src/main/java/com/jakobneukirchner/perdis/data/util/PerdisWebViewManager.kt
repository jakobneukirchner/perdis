package com.jakobneukirchner.perdis.data.util

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import com.jakobneukirchner.perdis.model.Credentials
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class PerdisWebViewManager(private val context: Context) {

    private var persistentWebView: WebView? = null
    private var isLoggedIn = false
    private val TAG = "PerdisWebView"

    @SuppressLint("SetJavaScriptEnabled")
    fun initializePersistentWebView(credentials: Credentials) {
        if (persistentWebView != null) {
            Log.d(TAG, "WebView already initialized")
            return
        }

        Log.d(TAG, "Initializing new WebView")
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
                    Log.d(TAG, "Page finished: $url")
                    
                    // Auto-login when on default page
                    if (url?.contains("default.aspx") == true && !isLoggedIn) {
                        Log.d(TAG, "On login page, attempting auto-login")
                        performAutoLogin(credentials)
                    }
                    // Mark as logged in when we reach roster
                    else if (url?.contains("roster.aspx") == true) {
                        isLoggedIn = true
                        Log.d(TAG, "Logged in successfully - reached roster.aspx")
                    }
                }
                
                override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    Log.d(TAG, "Page started: $url")
                }
            }
        }

        // Start initial load
        Log.d(TAG, "Loading default.aspx")
        persistentWebView?.loadUrl("https://perdisweb.verkehrs-ag.de/WebComm/default.aspx")
    }

    private fun performAutoLogin(credentials: Credentials) {
        Log.d(TAG, "Executing auto-login JavaScript")
        val js = """
            (function() {
                console.log('Starting auto-login');
                var userField = document.getElementsByName('ctl00\${'$'}cntMainBody\${'$'}lgnView\${'$'}lgnLogin\${'$'}UserName')[0];
                var passField = document.getElementsByName('ctl00\${'$'}cntMainBody\${'$'}lgnView\${'$'}lgnLogin\${'$'}Password')[0];
                var submitBtn = document.getElementById('ctl00_cntMainBody_lgnView_lgnLogin_LoginButton');
                
                console.log('User field: ' + (userField ? 'found' : 'not found'));
                console.log('Pass field: ' + (passField ? 'found' : 'not found'));
                console.log('Submit btn: ' + (submitBtn ? 'found' : 'not found'));
                
                if (userField && passField && submitBtn) {
                    userField.value = '${credentials.username}';
                    passField.value = '${credentials.password}';
                    console.log('Credentials filled, submitting form');
                    submitBtn.click();
                } else {
                    console.log('Form fields not found');
                }
            })();
        """.trimIndent()
        persistentWebView?.evaluateJavascript(js) { result ->
            Log.d(TAG, "JavaScript result: $result")
        }
    }

    suspend fun getRosterHtml(): String = suspendCancellableCoroutine { cont ->
        Log.d(TAG, "Getting roster HTML. Logged in: $isLoggedIn")
        if (!isLoggedIn) {
            Log.w(TAG, "Not logged in, returning empty")
            cont.resume("")
            return@suspendCancellableCoroutine
        }

        persistentWebView?.evaluateJavascript(
            "(function(){return document.documentElement.outerHTML;})();"
        ) { html ->
            val cleaned = html?.replace("\"", "") ?: ""
            Log.d(TAG, "Roster HTML scraped: ${cleaned.length} chars")
            cont.resume(cleaned)
        }
    }

    suspend fun getShiftHtml(date: String): String = suspendCancellableCoroutine { cont ->
        Log.d(TAG, "Getting shift HTML for date: $date. Logged in: $isLoggedIn")
        if (!isLoggedIn) {
            Log.w(TAG, "Not logged in, returning empty")
            cont.resume("")
            return@suspendCancellableCoroutine
        }

        persistentWebView?.loadUrl("https://perdisweb.verkehrs-ag.de/WebComm/shift.aspx?$date")
        
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            persistentWebView?.evaluateJavascript(
                "(function(){return document.documentElement.outerHTML;})();"
            ) { html ->
                val cleaned = html?.replace("\"", "") ?: ""
                Log.d(TAG, "Shift HTML scraped: ${cleaned.length} chars")
                cont.resume(cleaned)
            }
        }, 3000)
    }

    fun isSessionValid(): Boolean {
        Log.d(TAG, "Checking session validity: $isLoggedIn")
        return isLoggedIn
    }

    fun logout() {
        Log.d(TAG, "Logging out")
        isLoggedIn = false
        persistentWebView?.loadUrl("https://perdisweb.verkehrs-ag.de/WebComm/logout.aspx")
        persistentWebView?.clearCache(true)
        CookieManager.getInstance().removeAllCookies(null)
        CookieManager.getInstance().flush()
    }

    fun destroy() {
        Log.d(TAG, "Destroying WebView")
        persistentWebView?.destroy()
        persistentWebView = null
        isLoggedIn = false
    }
}
