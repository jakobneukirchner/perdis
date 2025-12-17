package com.jakobneukirchner.perdis.data.util

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.jakobneukirchner.perdis.model.Credentials

class CredentialsManager(context: Context) {

    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

    private val prefs = EncryptedSharedPreferences.create(
        "perdis_credentials",
        masterKeyAlias,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveCredentials(credentials: Credentials) {
        prefs.edit()
            .putString("username", credentials.username)
            .putString("password", credentials.password)
            .apply()
    }

    fun loadCredentials(): Credentials? {
        val username = prefs.getString("username", null)
        val password = prefs.getString("password", null)
        return if (!username.isNullOrEmpty() && !password.isNullOrEmpty()) {
            Credentials(username, password)
        } else null
    }

    fun clearCredentials() {
        prefs.edit().clear().apply()
    }

    fun hasCredentials(): Boolean {
        return prefs.getString("username", null) != null
    }
}
