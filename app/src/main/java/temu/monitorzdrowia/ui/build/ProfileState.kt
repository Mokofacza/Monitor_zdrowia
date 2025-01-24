package temu.monitorzdrowia.ui.build

import temu.monitorzdrowia.data.models.User
import java.time.LocalDate

data class ProfileState(
    val user: User? = null,
    val name: String = "",
    val subname: String = "",
    val birthDate: LocalDate? = null,
    val sex: String = "",
    val address: String = "",
    val citySize: String = "",
    val isDialogVisible: Boolean = false,
    val age: Int? = null,
    val hasCancelled: Boolean = false,
    val showMissingDataMessage: Boolean = false, // Nowa flaga

    // Nowe pola do "edycji jednego elementu"
    val isEditDialogVisible: Boolean = false,
    val fieldBeingEdited: ProfileField? = null,
    val tempValue: String = "",     // Tekst tymczasowy (np. dla name, subname, sex, address, citySize)
    val tempDate: LocalDate? = null // Tymczasowa data (dla birthDate)
)
