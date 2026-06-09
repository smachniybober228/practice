package ci.nsu.mobile.main.ViewModels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import ci.nsu.mobile.main.Repository.AuthRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val repository: AuthRepository
) : ViewModel() {
    private val KEY_LOGIN = "login"
    private val KEY_PASSWORD = "password"

    private val _login = MutableStateFlow(savedStateHandle.get<String>(KEY_LOGIN) ?: "")
    val login: StateFlow<String> = _login.asStateFlow()

    private val _password = MutableStateFlow(savedStateHandle.get<String>(KEY_PASSWORD) ?: "")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _loginSuccess = MutableSharedFlow<Unit>()
    val loginSuccess: SharedFlow<Unit> = _loginSuccess.asSharedFlow()

    fun updateLogin(value: String) {
        _login.update { value }
        savedStateHandle[KEY_LOGIN] = value
    }

    fun updatePassword(value: String) {
        _password.update { value }
        savedStateHandle[KEY_PASSWORD] = value
    }

    fun onLoginClick() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val result = repository.login(login.value, password.value)
            if (result.isSuccess) {
                _loginSuccess.emit(Unit)
            } else {
                _errorMessage.value = result.exceptionOrNull()?.message ?: "Ошибка входа"
            }
            _isLoading.value = false
        }
    }

    companion object {
        fun provideFactory(repository: AuthRepository): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val savedStateHandle = SavedStateHandle()
                return LoginViewModel(savedStateHandle, repository) as T
            }
        }
    }
}