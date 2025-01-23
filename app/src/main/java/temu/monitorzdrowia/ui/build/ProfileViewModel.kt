package temu.monitorzdrowia.ui.build

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import temu.monitorzdrowia.data.local.MoodDao
import temu.monitorzdrowia.data.models.User
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModel(
    private val dao: MoodDao
) : ViewModel() {

    private fun LocalDate.calculateAge(): Int {
        val now = LocalDate.now()
        return ChronoUnit.YEARS.between(this, now).toInt()
    }

    private val _state = MutableStateFlow(ProfileState())
    private val userFlow: Flow<User?> = dao.getUser()

    val state: StateFlow<ProfileState> = combine(
        _state,
        userFlow
    ) { currentState, userFromDb ->
        val showDialog = userFromDb == null
        val calculatedAge = userFromDb?.birthDate?.calculateAge()
        currentState.copy(
            user = userFromDb,
            isDialogVisible = showDialog,
            age = calculatedAge
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        ProfileState()
    )

    fun onEvent(event: ProfileEvent) {
        when(event) {
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
                    // Wyczyść formularz i schowaj dialog
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
            is ProfileEvent.UpdatePhoto -> {
                val currentUser = _state.value.user
                if (currentUser != null) {
                    val updatedUser = currentUser.copy(photo = event.photo)
                    viewModelScope.launch {
                        dao.updateUser(updatedUser)
                    }
                }
            }
        }
    }
}


