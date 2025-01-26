package temu.monitorzdrowia.viewmodel

import temu.monitorzdrowia.model.entities.User
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
    val showMissingDataMessage: Boolean = false,
    val isEditDialogVisible: Boolean = false,
    val fieldBeingEdited: ProfileField? = null,
    val tempValue: String = "",
    val tempDate: LocalDate? = null
)
