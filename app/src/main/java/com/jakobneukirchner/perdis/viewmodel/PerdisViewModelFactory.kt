package com.jakobneukirchner.perdis.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jakobneukirchner.perdis.data.DienstplanRepository
import com.jakobneukirchner.perdis.data.LoginRepository
import com.jakobneukirchner.perdis.data.util.CredentialsManager
import com.jakobneukirchner.perdis.data.util.WebViewScraperUtil

class PerdisViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val credentialsManager = CredentialsManager(context)
        val webViewScraperUtil = WebViewScraperUtil(context)
        val loginRepository = LoginRepository(credentialsManager, webViewScraperUtil)
        val dienstplanRepository = DienstplanRepository(webViewScraperUtil)

        return when (modelClass) {
            LoginViewModel::class.java ->
                LoginViewModel(loginRepository) as T
            DienstplanViewModel::class.java ->
                DienstplanViewModel(dienstplanRepository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel: $modelClass")
        }
    }
}
