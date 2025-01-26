package temu.monitorzdrowia.viewmodel

sealed class ProfileUiEvent {
    data class ShowToast(val message: String) : ProfileUiEvent()
}