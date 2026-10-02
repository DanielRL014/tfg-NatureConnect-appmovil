package es.unex.natureconnect.viewModel

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import es.unex.natureconnect.data.models.Publicacion
import es.unex.natureconnect.data.repository.PublicacionesRespository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * View model that backs the user profile screen.
 *
 * Loads the publications created by the signed-in user, whose identity is
 * read from shared preferences.
 *
 * @param repository Repository used to reach the publication endpoints.
 * @param application Application used to access shared preferences.
 */
class PerfilViewModel (private val repository: PublicacionesRespository, application: Application) : ViewModel() {
    /** Placeholder for the selected image; not implemented yet. */
    val selectedImageUri: Any
        get() {
            TODO()
        }
    private val _textoBusqueda = MutableStateFlow("")
    /** Text currently entered in the search box. */
    val textoBusqueda: StateFlow<String> = _textoBusqueda
    private val sharedPreferences: SharedPreferences = application.getSharedPreferences("NatureConnectPrefs", Context.MODE_PRIVATE)

    /** Identifier of the signed-in user read from shared preferences. */
    val idU: String? = sharedPreferences.getString("id", null)

    /** Display name of the signed-in user read from shared preferences. */
    private val _nombre: String? = sharedPreferences.getString("nombre",  null)

    /** Display name of the signed-in user. */
    val nombre: String? = _nombre

    private val _publicaciones = MutableStateFlow<List<Publicacion>>(emptyList())
    /** Publications created by the signed-in user. */
    val publicaciones: StateFlow<List<Publicacion>> = _publicaciones


    private val _error = MutableStateFlow<String?>(null)
    /** Last error message, or `null` when there is none. */
    val error: StateFlow<String?> = _error
    init {
        cargarPublicaciones()
    }

    /** Loads the publications created by the signed-in user. */
    fun cargarPublicaciones() {
        viewModelScope.launch {
            try {
                val response = repository.getMisPublicaciones(idU.toString())
                if (response.success) {
                    _publicaciones.value = response.data ?: emptyList()
                } else {
                    _error.value = response.message
                }
            } catch (e: Exception) {
                _error.value = "Error al cargar las publicaciones: ${e.message}"
            }
        }
    }

    /**
     * Updates the search text.
     *
     * @param nuevoTexto Text introduced by the user.
     */
    fun actualizarTextoBusqueda(nuevoTexto: String) {
        _textoBusqueda.value = nuevoTexto
    }
    /**
     * Navigates to the home screen with the current search text.
     *
     * @param navController Controller used to perform the navigation.
     */
    fun buscarAve(navController: NavController){
        navController.navigate("home/${textoBusqueda.value}")

    }



    /** Clears the current error message. */
    fun limpiarError() {
        _error.value = null
    }
}