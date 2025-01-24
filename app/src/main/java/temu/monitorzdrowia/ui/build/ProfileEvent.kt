package temu.monitorzdrowia.ui.build

import java.time.LocalDate

sealed class ProfileEvent {
    object ShowFillDataDialog : ProfileEvent()
    object HideFillDataDialog : ProfileEvent()
    object SaveUser : ProfileEvent()

    data class SetName(val name: String) : ProfileEvent()
    data class SetSubName(val subname: String) : ProfileEvent()
    data class SetBirthDate(val birthDate: LocalDate) : ProfileEvent()
    data class SetSex(val sex: String) : ProfileEvent()
    data class SetAddress(val address: String) : ProfileEvent()
    data class SetCitySize(val citySize: String) : ProfileEvent()

    data class UpdatePhoto(val photo: ByteArray) : ProfileEvent()

    // Edycja pojedynczych pól
    data class StartEdit(val field: ProfileField) : ProfileEvent()
    data class ChangeEditValue(val value: String) : ProfileEvent()
    data class ChangeEditDate(val date: LocalDate) : ProfileEvent()
    object ConfirmEdit : ProfileEvent()
    object CancelEdit : ProfileEvent()
    object ReopenDialog : ProfileEvent()
}
