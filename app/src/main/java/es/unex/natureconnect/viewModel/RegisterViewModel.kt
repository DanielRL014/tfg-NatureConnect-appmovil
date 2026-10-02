package es.unex.natureconnect.viewModel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import es.unex.natureconnect.data.models.LoginResponse
import es.unex.natureconnect.data.models.Usuarios
import es.unex.natureconnect.data.repository.UsuaraiosRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(private val usuariosRepository: UsuaraiosRepository, application: Application) : AndroidViewModel(
application
) {
    private val sharedPreferences = application.getSharedPreferences("NatureConnectPrefs",  Context.MODE_PRIVATE)
    private val _user = MutableStateFlow<Usuarios?>(null)
    val user: StateFlow<Usuarios?> get() = _user

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

    fun Create(name: String, password: String,Email:String) {
        viewModelScope.launch {
            try {
                val response: LoginResponse = usuariosRepository.registerUser(name, password,Email)


                if (response.success && response.data != null) {

                    _user.value = response.data
                    val editor = sharedPreferences.edit()
                    editor.putString("id", _user.value?.id.toString())
                    editor.putString("nombre", _user.value?.nombreUsuario)
                    editor.putString("email", _user.value?.emailUsuario)
                    editor.apply()

                } else {
                    _error.value = "Credenciales incorrectas o error del servidor"
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
            }
        }
    }
    fun clearError() {
        _error.value = null
    }
}