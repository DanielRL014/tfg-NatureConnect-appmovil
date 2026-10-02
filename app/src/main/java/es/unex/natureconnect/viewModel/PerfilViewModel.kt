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

class PerfilViewModel (private val repository: PublicacionesRespository, application: Application) : ViewModel() {
    val selectedImageUri: Any
        get() {
            TODO()
        }
    private val _textoBusqueda = MutableStateFlow("")
    val textoBusqueda: StateFlow<String> = _textoBusqueda
    private val sharedPreferences: SharedPreferences = application.getSharedPreferences("NatureConnectPrefs", Context.MODE_PRIVATE)

    val idU: String? = sharedPreferences.getString("id", null)
    private val _nombre: String? = sharedPreferences.getString("nombre",  null)
    val nombre: String? = _nombre

    private val _publicaciones = MutableStateFlow<List<Publicacion>>(emptyList())
    val publicaciones: StateFlow<List<Publicacion>> = _publicaciones


    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error
    init {
        cargarPublicaciones()
    }

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

    fun actualizarTextoBusqueda(nuevoTexto: String) {
        _textoBusqueda.value = nuevoTexto
    }
    fun buscarAve(navController: NavController){
        navController.navigate("home/${textoBusqueda.value}")

    }



    fun limpiarError() {
        _error.value = null
    }
}