package temu.monitorzdrowia.ui.build
import temu.monitorzdrowia.SortType
import temu.monitorzdrowia.data.models.User
import java.time.LocalDate

sealed class ProfileEvent {
    data class SetName(val name: String) : ProfileEvent()
    data class SetSubName(val subname: String) : ProfileEvent()
    data class SetBirthDate(val birthDate: LocalDate) : ProfileEvent()
    object SaveUser : ProfileEvent()

    // Dodane do kontroli dialogu
    object ShowFillDataDialog : ProfileEvent()
    object HideFillDataDialog : ProfileEvent()
}
