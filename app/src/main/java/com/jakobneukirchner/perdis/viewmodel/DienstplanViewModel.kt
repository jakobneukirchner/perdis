package com.jakobneukirchner.perdis.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jakobneukirchner.perdis.data.DienstplanRepository
import com.jakobneukirchner.perdis.model.DienstplanState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DienstplanViewModel(
    private val dienstplanRepository: DienstplanRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DienstplanState())
    val state: StateFlow<DienstplanState> = _state

    fun loadDienstplan() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            try {
                val dienste = dienstplanRepository.loadRoster()
                _state.value = _state.value.copy(
                    isLoading = false,
                    dienste = dienste
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Fehler beim Laden des Dienstplans"
                )
            }
        }
    }
}
