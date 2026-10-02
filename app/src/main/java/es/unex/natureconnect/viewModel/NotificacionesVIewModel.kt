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

@RequiresApi(Build.VERSION_CODES.O)
class NotificacionesVIewModel (private val repository: PublicacionesRespository, application: Application) : ViewModel() {
    private val _textoBusqueda = MutableStateFlow("")
    val textoBusqueda: StateFlow<String> = _textoBusqueda
    private val _notificaciones = MutableStateFlow<List<notificacion>>(emptyList())
    val notificaciones: StateFlow<List<notificacion>> = _notificaciones
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val sharedPreferences: SharedPreferences = application.getSharedPreferences("NatureConnectPrefs", Context.MODE_PRIVATE)

    val idU: String? = sharedPreferences.getString("id", null)
    val fecha: String? = sharedPreferences.getString("fecha", null)
    init {
        cargarNotificaciones()
    }

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
    fun actualizarTextoBusqueda(nuevoTexto: String) {
        _textoBusqueda.value = nuevoTexto
    }
    fun buscarAve(navController: NavController){
        navController.navigate("home/${textoBusqueda.value}")

    }
}