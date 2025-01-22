package temu.monitorzdrowia.ui.build

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import temu.monitorzdrowia.data.local.MoodDao
import temu.monitorzdrowia.data.models.User
import java.time.LocalDate

class ProfileViewModel(
    private val dao: MoodDao
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())

    private val userFlow: Flow<User?> = dao.getUser()

    // Połączymy userFlow z naszym lokalnym stanem.
    val state: StateFlow<ProfileState> = combine(
        _state,
        userFlow
    ) { currentState, userFromDb ->
        // Jeśli user jest null i jeszcze nie ustawiliśmy dialogu na true,
        // włączamy okno dialogowe
        val shouldShowDialog = userFromDb == null

        currentState.copy(
            user = userFromDb,
            isDialogVisible = shouldShowDialog
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProfileState()
    )

    fun onEvent(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.SetName -> {
                _state.update { it.copy(name = event.name) }
            }
            is ProfileEvent.SetSubName -> {
                _state.update { it.copy(subname = event.subname) }
            }
            is ProfileEvent.SetBirthDate -> {
                _state.update { it.copy(birthDate = event.birthDate) }
            }
            ProfileEvent.ShowFillDataDialog -> {
                _state.update { it.copy(isDialogVisible = true) }
            }
            ProfileEvent.HideFillDataDialog -> {
                _state.update { it.copy(isDialogVisible = false) }
            }
            ProfileEvent.SaveUser -> {
                val currentName = _state.value.name
                val currentSubname = _state.value.subname
                val currentBirthDate = _state.value.birthDate

                // Sprawdzenie wypełnienia danych
                if (currentName.isNotBlank() && currentSubname.isNotBlank() && currentBirthDate != null) {
                    viewModelScope.launch {
                        dao.insertUser(
                            User(
                                name = currentName,
                                subname = currentSubname,
                                birthDate = currentBirthDate
                            )
                        )
                    }
                    // Po zapisaniu można zamknąć dialog i wyzerować pola
                    _state.update {
                        it.copy(
                            name = "",
                            subname = "",
                            birthDate = null,
                            isDialogVisible = false
                        )
                    }
                }
            }
        }
    }
}
