package es.unex.natureconnect.viewModel

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.google.android.gms.common.internal.FallbackServiceBroker
import es.unex.natureconnect.data.models.Publicacion
import es.unex.natureconnect.data.repository.PublicacionesRespository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * View model that backs the publication detail screen.
 *
 * Loads a single publication, either for the signed-in user or as a guest, and
 * exposes the like operations.
 *
 * @param repository Repository used to reach the publication endpoints.
 * @param application Application used to access shared preferences.
 * @param navController Controller used to read the publication identifier.
 */
class PublicacionViewModel(private val repository: PublicacionesRespository,application: Application,navController: NavController) :
    AndroidViewModel(application){
    private val _textoBusqueda = MutableStateFlow("")
    /** Text currently entered in the search box. */
    val textoBusqueda: StateFlow<String> = _textoBusqueda
    private val _publicacion = MutableStateFlow<Publicacion?>(null)
    /** Publication shown on the screen, or `null` while it is loading. */
    val publicacion: StateFlow<Publicacion?> = _publicacion
    private val _error = MutableStateFlow<String?>(null)
    /** Last error message, or `null` when there is none. */
    val error: StateFlow<String?> = _error
    /** Back stack entry used to read the navigation arguments. */
    val backStackEntry: NavBackStackEntry? = navController.currentBackStackEntry

    /** Identifier of the publication received through navigation. */
    val id = backStackEntry?.arguments?.getString("idPublicacion")

    private val _iniciado = MutableStateFlow<Boolean>(false)
    /** Whether the screen was opened by a signed-in user rather than a guest. */
    val iniciado: StateFlow<Boolean> = _iniciado

    private val sharedPreferences: SharedPreferences = application.getSharedPreferences("NatureConnectPrefs", Context.MODE_PRIVATE)


    /** Identifier of the signed-in user read from shared preferences. */
    val idU: String? = sharedPreferences.getString("id", null)
    init {
        if(idU != null){
             _iniciado.value = true
            cargarPublicacion()
        }else{
            cargarPublicacionInvitado()
        }
    }
    /** Loads the publication as seen by the signed-in user. */
    fun cargarPublicacion() {

        viewModelScope.launch {
            try {

                    val response = repository.getPublicacion(id!!, idU!!)

                if (response.success) {
                    _publicacion.value = response.data
                } else {
                    _error.value = response.message
                }
            } catch (e: Exception) {
                _error.value = "Error al cargar las publicaciones: ${e.message}"
            }
        }

    }
    /** Loads the publication without requiring an authenticated user. */
    fun cargarPublicacionInvitado() {

        viewModelScope.launch {
            try {

                val response = repository.getPublicacionInvitado(id!!)

                if (response.success) {
                    _publicacion.value = response.data
                } else {
                    _error.value = response.message
                }
            } catch (e: Exception) {
                _error.value = "Error al cargar las publicaciones: ${e.message}"
            }
        }

    }

    /** Adds a like from the signed-in user to the publication. */
    fun darLike(){
        viewModelScope.launch {
            try {

                val response = repository.darLike(id!!, idU!!)

                if (response.success) {

                } else {
                    _error.value = response.message
                }
            } catch (e: Exception) {
                _error.value = "Error al dar like la publicacion: ${e.message}"
            }
        }
    }

    /** Removes the like given by the signed-in user and updates the counter. */
    fun quitarLike(){
        viewModelScope.launch {
            try {

                val response = repository.quitarLike(id!!, idU!!)
                publicacion.value?.hasLiked = false
                publicacion.value?.meGustas = publicacion.value?.meGustas?.minus(1)!!


                if (response.success) {

                } else {
                    _error.value = response.message
                }
            } catch (e: Exception) {
                _error.value = "Error al quitar like la publicacion: ${e.message}"
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
     * Navigates to the home screen, guest or not, with the current search text.
     *
     * @param navController Controller used to perform the navigation.
     */
    fun buscarAve(navController: NavController){
        if(iniciado.value) {
            navController.navigate("home/${textoBusqueda.value}")
        }else{
            navController.navigate("homeInvitado/${textoBusqueda.value}")
        }

    }

    /** Clears the current error message. */
    fun limpiarError() {
        _error.value = null
    }
}