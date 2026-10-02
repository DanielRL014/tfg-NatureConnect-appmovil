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

/**
 * View model that handles authentication and registration.
 *
 * Persists the signed-in user in shared preferences so the session survives
 * app restarts.
 *
 * @param usuariosRepository Repository used to reach the user endpoints.
 * @param application Application used to access shared preferences.
 */
class LoginViewModel(private val usuariosRepository: UsuaraiosRepository, application: Application) : AndroidViewModel(
    application
) {

    private val sharedPreferences = application.getSharedPreferences("NatureConnectPrefs",  Context.MODE_PRIVATE)
    private val _user = MutableStateFlow<Usuarios?>(null)
    /** Currently signed-in user, or `null` when nobody is authenticated. */
    val user: StateFlow<Usuarios?> get() = _user

    private val _error = MutableStateFlow<String?>(null)
    /** Last authentication error message, or `null` when there is none. */
    val error: StateFlow<String?> get() = _error

    /**
     * Signs in a user and stores the session in shared preferences.
     *
     * Updates [error] when the credentials are rejected or the request fails.
     *
     * @param email Username sent to the backend.
     * @param password Password sent to the backend.
     */
    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val response: LoginResponse = usuariosRepository.loginUser(email, password)


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
    /** Clears the current error message. */
    fun clearError() {
        _error.value = null
    }
}
