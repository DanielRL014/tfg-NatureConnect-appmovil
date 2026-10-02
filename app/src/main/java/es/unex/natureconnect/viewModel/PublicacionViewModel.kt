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

class PublicacionViewModel(private val repository: PublicacionesRespository,application: Application,navController: NavController) :
    AndroidViewModel(application){
    private val _textoBusqueda = MutableStateFlow("")
    val textoBusqueda: StateFlow<String> = _textoBusqueda
    private val _publicacion = MutableStateFlow<Publicacion?>(null)
    val publicacion: StateFlow<Publicacion?> = _publicacion
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error
    val backStackEntry: NavBackStackEntry? = navController.currentBackStackEntry
    val id = backStackEntry?.arguments?.getString("idPublicacion")

    private val _iniciado = MutableStateFlow<Boolean>(false)
    val iniciado: StateFlow<Boolean> = _iniciado

    private val sharedPreferences: SharedPreferences = application.getSharedPreferences("NatureConnectPrefs", Context.MODE_PRIVATE)


    val idU: String? = sharedPreferences.getString("id", null)
    init {
        if(idU != null){
             _iniciado.value = true
            cargarPublicacion()
        }else{
            cargarPublicacionInvitado()
        }
    }
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
    fun actualizarTextoBusqueda(nuevoTexto: String) {
        _textoBusqueda.value = nuevoTexto
    }
    fun buscarAve(navController: NavController){
        if(iniciado.value) {
            navController.navigate("home/${textoBusqueda.value}")
        }else{
            navController.navigate("homeInvitado/${textoBusqueda.value}")
        }

    }

    fun limpiarError() {
        _error.value = null
    }
}