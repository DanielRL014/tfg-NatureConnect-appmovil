package es.unex.natureconnect.viewModel

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import es.unex.natureconnect.data.models.notificacion
import es.unex.natureconnect.data.models.notificacionesResponse
import es.unex.natureconnect.data.repository.PublicacionesRespository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

/**
 * View model that loads the likes received by the user's publications.
 *
 * Persists the last seen date in shared preferences so only recent
 * notifications are requested on the next launch.
 *
 * Requires API 26 or higher because it relies on `java.time.LocalDate`.
 *
 * @param repository Repository used to reach the notifications endpoint.
 * @param application Application used to access shared preferences.
 */
@RequiresApi(Build.VERSION_CODES.O)
class NotificacionesVIewModel (private val repository: PublicacionesRespository, application: Application) : ViewModel() {
    private val _textoBusqueda = MutableStateFlow("")
    /** Text currently entered in the search box. */
    val textoBusqueda: StateFlow<String> = _textoBusqueda
    private val _notificaciones = MutableStateFlow<List<notificacion>>(emptyList())
    /** Notifications loaded from the backend. */
    val notificaciones: StateFlow<List<notificacion>> = _notificaciones
    private val _error = MutableStateFlow<String?>(null)
    /** Last error message, or `null` when there is none. */
    val error: StateFlow<String?> = _error

    private val sharedPreferences: SharedPreferences = application.getSharedPreferences("NatureConnectPrefs", Context.MODE_PRIVATE)

    /** Identifier of the signed-in user read from shared preferences. */
    val idU: String? = sharedPreferences.getString("id", null)

    /** Timestamp of the last checked notification, or `null` if never checked. */
    val fecha: String? = sharedPreferences.getString("fecha", null)
    init {
        cargarNotificaciones()
    }

    /**
     * Reloads the notifications for the signed-in user.
     *
     * Stores today's date as the reference point so later runs only fetch new
     * items.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun cargarNotificaciones(){

        viewModelScope.launch {
            try {
                val response: notificacionesResponse
                if(fecha == null){
                    response = repository.getNotificaciones( idU!!,"2000-01-01")
                }else{
                     response = repository.getNotificaciones( idU!!,fecha)
                }


                if (response.success) {
                    _notificaciones.value = response.data
                } else {
                    _error.value = response.message
                }
                val editor = sharedPreferences.edit()
                editor.putString("fecha", LocalDate.now().toString())
                editor.apply()
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
}