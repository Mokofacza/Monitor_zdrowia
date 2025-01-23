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
    val age: Int? = null
)
