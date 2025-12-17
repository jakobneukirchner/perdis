package com.jakobneukirchner.perdis.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jakobneukirchner.perdis.data.LoginRepository
import com.jakobneukirchner.perdis.model.Credentials
import com.jakobneukirchner.perdis.model.LoginState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginRepository: LoginRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state

    init {
        tryAutoLogin()
    }

    fun tryAutoLogin() {
        val creds = loginRepository.loadSavedCredentials()
        if (creds != null) {
            login(creds.username, creds.password)
        }
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            val success = try {
                loginRepository.login(Credentials(username, password))
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Unbekannter Fehler"
                )
                return@launch
            }
            _state.value = _state.value.copy(
                isLoading = false,
                isLoggedIn = success,
                errorMessage = if (!success) "Login fehlgeschlagen" else null
            )
        }
    }

    fun logout() {
        loginRepository.logout()
        _state.value = LoginState()
    }
}
