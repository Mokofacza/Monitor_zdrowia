package temu.monitorzdrowia.ui.build

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import temu.monitorzdrowia.data.local.MoodDao
import temu.monitorzdrowia.data.models.User
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class ProfileViewModel(
    private val dao: MoodDao // Upewnij się, że to DAO ma updateUser(...)
) : ViewModel() {

    // Pomocnicza funkcja – obliczanie wieku
    private fun LocalDate.calculateAge(): Int {
        val now = LocalDate.now()
        return ChronoUnit.YEARS.between(this, now).toInt()
    }

    // Stan wewnętrzny
    private val _state = MutableStateFlow(ProfileState())

    // Strumień pobierający usera z bazy
    private val userFlow: Flow<User?> = dao.getUser()

    // Połączenie (combine) wewn. stanu i userFlow
    val state: StateFlow<ProfileState> = combine(_state, userFlow) { currentState, userFromDb ->
        // Jeśli user == null -> pokaż dialog tworzenia profilu
        val showDialog = userFromDb == null
        val calculatedAge = userFromDb?.birthDate?.calculateAge()

        currentState.copy(
            user = userFromDb,
            isDialogVisible = showDialog,
            age = calculatedAge
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProfileState()
    )

    fun onEvent(event: ProfileEvent) {
        when (event) {

            // -------------------
            // 1) Tworzenie profilu
            // -------------------
            is ProfileEvent.SetName -> {
                _state.update { it.copy(name = event.name) }
            }
            is ProfileEvent.SetSubName -> {
                _state.update { it.copy(subname = event.subname) }
            }
            is ProfileEvent.SetBirthDate -> {
                _state.update { it.copy(birthDate = event.birthDate) }
            }
            is ProfileEvent.SetSex -> {
                _state.update { it.copy(sex = event.sex) }
            }
            is ProfileEvent.SetAddress -> {
                _state.update { it.copy(address = event.address) }
            }
            is ProfileEvent.SetCitySize -> {
                _state.update { it.copy(citySize = event.citySize) }
            }

            ProfileEvent.ShowFillDataDialog -> {
                _state.update { it.copy(isDialogVisible = true) }
            }
            ProfileEvent.HideFillDataDialog -> {
                _state.update { it.copy(isDialogVisible = false) }
            }

            ProfileEvent.SaveUser -> {
                val s = _state.value
                val canSave = s.name.isNotBlank() &&
                        s.subname.isNotBlank() &&
                        s.birthDate != null &&
                        s.sex.isNotBlank() &&
                        s.address.isNotBlank() &&
                        s.citySize.isNotBlank()

                if (canSave) {
                    viewModelScope.launch {
                        dao.insertUser(
                            User(
                                name = s.name,
                                subname = s.subname,
                                birthDate = s.birthDate,
                                sex = s.sex,
                                address = s.address,
                                citySize = s.citySize
                            )
                        )
                    }
                    // Czyścimy formularz
                    _state.update {
                        it.copy(
                            name = "",
                            subname = "",
                            birthDate = null,
                            sex = "",
                            address = "",
                            citySize = "",
                            isDialogVisible = false
                        )
                    }
                }
            }

            // -------------------
            // 2) Dodawanie/zmiana zdjęcia
            // -------------------
            is ProfileEvent.UpdatePhoto -> {
                val user = _state.value.user ?: return
                val updatedUser = user.copy(photo = event.photo)
                viewModelScope.launch {
                    dao.updateUser(updatedUser)
                }
            }

            // -------------------
            // 3) Edycja pojedynczych pól (3 kropki -> Edytuj)
            // -------------------
            is ProfileEvent.StartEdit -> {
                val user = _state.value.user ?: return
                when (event.field) {
                    ProfileField.Name -> _state.update {
                        it.copy(
                            isEditDialogVisible = true,
                            fieldBeingEdited = ProfileField.Name,
                            tempValue = user.name,
                            tempDate = null
                        )
                    }
                    ProfileField.Subname -> _state.update {
                        it.copy(
                            isEditDialogVisible = true,
                            fieldBeingEdited = ProfileField.Subname,
                            tempValue = user.subname,
                            tempDate = null
                        )
                    }
                    ProfileField.BirthDate -> _state.update {
                        it.copy(
                            isEditDialogVisible = true,
                            fieldBeingEdited = ProfileField.BirthDate,
                            tempValue = "",
                            tempDate = user.birthDate
                        )
                    }
                    ProfileField.Sex -> _state.update {
                        it.copy(
                            isEditDialogVisible = true,
                            fieldBeingEdited = ProfileField.Sex,
                            tempValue = user.sex ?: "",
                            tempDate = null
                        )
                    }
                    ProfileField.Address -> _state.update {
                        it.copy(
                            isEditDialogVisible = true,
                            fieldBeingEdited = ProfileField.Address,
                            tempValue = user.address ?: "",
                            tempDate = null
                        )
                    }
                    ProfileField.CitySize -> _state.update {
                        it.copy(
                            isEditDialogVisible = true,
                            fieldBeingEdited = ProfileField.CitySize,
                            tempValue = user.citySize ?: "",
                            tempDate = null
                        )
                    }
                }
            }

            is ProfileEvent.ChangeEditValue -> {
                _state.update { it.copy(tempValue = event.value) }
            }
            is ProfileEvent.ChangeEditDate -> {
                _state.update { it.copy(tempDate = event.date) }
            }
            ProfileEvent.ConfirmEdit -> {
                val user = _state.value.user ?: return
                val field = _state.value.fieldBeingEdited ?: return
                val tmpVal = _state.value.tempValue
                val tmpDate = _state.value.tempDate

                val updatedUser = when (field) {
                    ProfileField.Name -> user.copy(name = tmpVal)
                    ProfileField.Subname -> user.copy(subname = tmpVal)
                    ProfileField.BirthDate -> if (tmpDate != null) user.copy(birthDate = tmpDate) else user
                    ProfileField.Sex -> user.copy(sex = tmpVal)
                    ProfileField.Address -> user.copy(address = tmpVal)
                    ProfileField.CitySize -> user.copy(citySize = tmpVal)
                }

                // Zapis w bazie
                viewModelScope.launch {
                    dao.updateUser(updatedUser)
                }
                // Zamykamy dialog
                _state.update {
                    it.copy(
                        isEditDialogVisible = false,
                        fieldBeingEdited = null,
                        tempValue = "",
                        tempDate = null
                    )
                }
            }
            ProfileEvent.CancelEdit -> {
                _state.update {
                    it.copy(
                        isEditDialogVisible = false,
                        fieldBeingEdited = null,
                        tempValue = "",
                        tempDate = null
                    )
                }
            }
        }
    }
}
