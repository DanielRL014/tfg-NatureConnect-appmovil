package es.unex.natureconnect.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.unex.natureconnect.data.models.Publicacion
import es.unex.natureconnect.data.models.familiaResponse
import es.unex.natureconnect.data.repository.PublicacionesRespository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MapViewModel(private val repository: PublicacionesRespository) : ViewModel() {
    private val _publicaciones = MutableStateFlow<List<Publicacion>>(emptyList())
    val publicaciones: StateFlow<List<Publicacion>> = _publicaciones

    private val _familias=MutableStateFlow<List<String>>(emptyList())
    val familias: StateFlow<List<String>> = _familias

    private val _textoBusqueda = MutableStateFlow("")
    val textoBusqueda: StateFlow<String> = _textoBusqueda

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error
    init {
        cargarPublicaciones()
        cargarFamilias()
    }

    fun cargarPublicaciones() {
        viewModelScope.launch {
            try {
                val response = repository.listarPlublicaciones()
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
    fun cargarFamilias() {
        viewModelScope.launch {
            try {
                val response: familiaResponse

                response = repository.getFamilia()

                if (response.success) {
                    _familias.value = response.data.map { it.nombreFamilia }

                } else {
                    _error.value = response.message
                }
            } catch (e: Exception) {
                _error.value = "Error al cargar las familias: ${e.message}"
            }
        }
    }
    fun actualizarTextoBusqueda(nuevoTexto: String) {
        _textoBusqueda.value = nuevoTexto
    }
    fun buscarAve(){
        viewModelScope.launch {
            try {
                val response = repository.getPublicacionesBusqueda(_textoBusqueda.value)
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

    fun filtrarPorFamilia(familia: String) {

        viewModelScope.launch {
            try {
                val response = repository.getPublicacionesFamilia(familia)
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

    fun limpiarError() {
        _error.value = null
    }
}

