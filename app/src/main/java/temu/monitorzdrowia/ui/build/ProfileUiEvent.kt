package temu.monitorzdrowia.ui.build

sealed class ProfileUiEvent {
    data class ShowToast(val message: String) : ProfileUiEvent()
}