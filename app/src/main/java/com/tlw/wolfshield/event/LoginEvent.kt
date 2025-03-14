package com.tlw.wolfshield.event

sealed class LoginEvent {
    data object ShowLoader: LoginEvent()
    data object HideLoader: LoginEvent()
    data class ShowSnackBar(val message: String): LoginEvent()
}