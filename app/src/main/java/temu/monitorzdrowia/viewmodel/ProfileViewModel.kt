package temu.monitorzdrowia.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import temu.monitorzdrowia.model.data.local.MoodDao
import temu.monitorzdrowia.model.entities.User
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class ProfileViewModel(
    private val dao: MoodDao
) : ViewModel() {

    // Pomocnicza funkcja – obliczanie wieku
    private fun LocalDate.calculateAge(): Int {
        val now = LocalDate.now()
        return ChronoUnit.YEARS.between(this, now).toInt()
    }

    // Stan wewnętrzny
    private val _state = MutableStateFlow(ProfileState())

    // Publiczny StateFlow do obserwacji przez UI
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    // Kanał do emitowania zdarzeń UI
    private val _uiEvent = Channel<ProfileUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        // Obserwacja zmian w userFlow
        viewModelScope.launch {
            dao.getUser().collect { userFromDb ->
                _state.update { currentState ->
                    val calculatedAge = userFromDb?.birthDate?.calculateAge()
                    currentState.copy(
                        user = userFromDb,
                        age = calculatedAge
                        // isDialogVisible i showMissingDataMessage zarządzane przez zdarzenia
                    )
                }
            }
        }

        // Inicjalne sprawdzenie: jeśli użytkownik nie istnieje, otwórz dialog
        viewModelScope.launch {
            val user = dao.getUser().firstOrNull()
            if (user == null && !_state.value.hasCancelled) {
                _state.update { it.copy(isDialogVisible = true) }
            }
        }
    }

    fun onEvent(event: ProfileEvent) {
        when (event) {

            // Tworzenie profilu
            is ProfileEvent.SetName -> {
                _state.update { it.copy(name = event.name) }
            }
            is ProfileEvent.SetSubName -> {
                _state.update { it.copy(subname = event.subname) }
            }
            is ProfileEvent.SetBirthDate -> {
                // Walidacja daty
                if (event.birthDate.isAfter(LocalDate.now())) {
                    viewModelScope.launch {
                        _uiEvent.send(ProfileUiEvent.ShowToast("Data urodzenia nie może być w przyszłości"))
                    }
                } else {
                    _state.update { it.copy(birthDate = event.birthDate) }
                }
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
            is ProfileEvent.UpdatePhoto -> {
                val user = _state.value.user ?: return
                val updatedUser = user.copy(photo = event.photo)
                viewModelScope.launch {
                    try {
                        dao.updateUser(updatedUser)
                        Log.d("ProfileViewModel", "Photo updated for user: ${updatedUser.name}")
                        _uiEvent.send(ProfileUiEvent.ShowToast("Zdjęcie zaktualizowane pomyślnie"))
                    } catch (e: Exception) {
                        Log.e("ProfileViewModel", "Error updating photo: ${e.message}", e)
                        _uiEvent.send(ProfileUiEvent.ShowToast("Błąd podczas aktualizacji zdjęcia"))
                    }
                }
            }

            is ProfileEvent.ShowFillDataDialog -> {
                _state.update { it.copy(isDialogVisible = true, showMissingDataMessage = false) }
            }
            is ProfileEvent.HideFillDataDialog -> {
                _state.update {
                    it.copy(
                        isDialogVisible = false,
                        hasCancelled = true,
                        showMissingDataMessage = true
                    )
                }
            }
            is ProfileEvent.ReopenDialog -> {
                _state.update {
                    it.copy(
                        isDialogVisible = true,
                        showMissingDataMessage = false
                    )
                }
            }

            is ProfileEvent.SaveUser -> {
                val s = _state.value
                val canSave = s.name.isNotBlank() &&
                        s.subname.isNotBlank() &&
                        s.birthDate != null &&
                        s.sex.isNotBlank() &&
                        s.address.isNotBlank() &&
                        s.citySize.isNotBlank()

                if (canSave) {
                    // Dodatkowa walidacja daty urodzenia
                    if (s.birthDate!!.isAfter(LocalDate.now())) {
                        viewModelScope.launch {
                            _uiEvent.send(ProfileUiEvent.ShowToast("Data urodzenia nie może być w przyszłości"))
                        }
                        return
                    }

                    viewModelScope.launch {
                        try {
                            dao.insertUser(
                                User(
                                    name = s.name,
                                    subname = s.subname,
                                    birthDate = s.birthDate,
                                    sex = s.sex,
                                    address = s.address,
                                    citySize = s.citySize,
                                    photo = s.user?.photo
                                )
                            )
                            Log.d("ProfileViewModel", "User saved: ${s.name} ${s.subname}")
                            _uiEvent.send(ProfileUiEvent.ShowToast("Profil zapisany pomyślnie"))
                        } catch (e: Exception) {
                            Log.e("ProfileViewModel", "Error saving user: ${e.message}", e)
                            _uiEvent.send(ProfileUiEvent.ShowToast("Błąd podczas zapisywania profilu"))
                        }
                    }
                    // Czyścimy formularz i resetujemy flagi
                    _state.update {
                        it.copy(
                            name = "",
                            subname = "",
                            birthDate = null,
                            sex = "",
                            address = "",
                            citySize = "",
                            isDialogVisible = false,
                            hasCancelled = false,
                            showMissingDataMessage = false
                        )
                    }
                } else {
                    Log.d("ProfileViewModel", "Cannot save user: missing fields")
                    viewModelScope.launch {
                        _uiEvent.send(ProfileUiEvent.ShowToast("Proszę uzupełnić wszystkie wymagane pola"))
                    }
                }
            }

            // Edycja pojedynczych pól
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
                            tempValue = user.sex.toString(),
                            tempDate = null
                        )
                    }
                    ProfileField.CitySize -> _state.update {
                        it.copy(
                            isEditDialogVisible = true,
                            fieldBeingEdited = ProfileField.CitySize,
                            tempValue = user.citySize.toString(),
                            tempDate = null
                        )
                    }
                    ProfileField.Address -> _state.update {
                        it.copy(
                            isEditDialogVisible = true,
                            fieldBeingEdited = ProfileField.Address,
                            tempValue = user.address.toString(),
                            tempDate = null
                        )
                    }
                }
            }

            is ProfileEvent.ChangeEditValue -> {
                _state.update { it.copy(tempValue = event.value) }
            }
            is ProfileEvent.ChangeEditDate -> {
                // Walidacja daty dla edycji
                if (event.date.isAfter(LocalDate.now())) {
                    viewModelScope.launch {
                        _uiEvent.send(ProfileUiEvent.ShowToast("Data urodzenia nie może być w przyszłości"))
                    }
                } else {
                    _state.update { it.copy(tempDate = event.date) }
                }
            }
            is ProfileEvent.ConfirmEdit -> {
                val user = _state.value.user ?: return
                val field = _state.value.fieldBeingEdited ?: return
                val tmpVal = _state.value.tempValue
                val tmpDate = _state.value.tempDate

                // Dodatkowa walidacja, jeśli edytujemy datę urodzenia
                if (field == ProfileField.BirthDate && tmpDate != null && tmpDate.isAfter(LocalDate.now())) {
                    viewModelScope.launch {
                        _uiEvent.send(ProfileUiEvent.ShowToast("Data urodzenia nie może być w przyszłości"))
                    }
                    return
                }

                val updatedUser = when (field) {
                    ProfileField.Name -> user.copy(name = tmpVal)
                    ProfileField.Subname -> user.copy(subname = tmpVal)
                    ProfileField.BirthDate -> tmpDate?.let { user.copy(birthDate = it) } ?: user
                    ProfileField.Sex -> user.copy(sex = tmpVal)
                    ProfileField.CitySize -> user.copy(citySize = tmpVal)
                    ProfileField.Address -> user.copy(address = tmpVal)
                }

                // Zapis w bazie danych
                viewModelScope.launch {
                    try {
                        dao.updateUser(updatedUser)
                        Log.d("ProfileViewModel", "User updated: ${updatedUser.name} ${updatedUser.subname}")
                        _uiEvent.send(ProfileUiEvent.ShowToast("Profil zaktualizowany pomyślnie"))
                    } catch (e: Exception) {
                        Log.e("ProfileViewModel", "Error updating user: ${e.message}", e)
                        _uiEvent.send(ProfileUiEvent.ShowToast("Błąd podczas aktualizacji profilu"))
                    }
                }

                // Zamykamy dialog edycji
                _state.update {
                    it.copy(
                        isEditDialogVisible = false,
                        fieldBeingEdited = null,
                        tempValue = "",
                        tempDate = null
                    )
                }
            }
            is ProfileEvent.CancelEdit -> {
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
