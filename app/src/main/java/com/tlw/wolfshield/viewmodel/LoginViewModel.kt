package com.tlw.wolfshield.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tlw.wolfshield.event.LoginEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class LoginViewModel: ViewModel() {
    // Emits room events to update in UI
    private val mutableUiEvent = MutableSharedFlow<LoginEvent>()
    val uiEvents = mutableUiEvent

    fun sendUIEvent(event: LoginEvent){
        viewModelScope.launch {
            mutableUiEvent.emit(event)
        }
    }
}