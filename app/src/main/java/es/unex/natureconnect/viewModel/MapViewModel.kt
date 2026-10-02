package es.unex.natureconnect.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.unex.natureconnect.data.models.Publicacion
import es.unex.natureconnect.data.models.familiaResponse
import es.unex.natureconnect.data.repository.PublicacionesRespository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * View model backing the map and the search screens.
 *
 * Loads the publications to plot, the bird families used as filters and the
 * current search text.
 *
 * @param repository Repository used to reach the publication endpoints.
 */
class MapViewModel(private val repository: PublicacionesRespository) : ViewModel() {
    private val _publicaciones = MutableStateFlow<List<Publicacion>>(emptyList())
    /** Publications currently shown on the map. */
    val publicaciones: StateFlow<List<Publicacion>> = _publicaciones

    private val _familias=MutableStateFlow<List<String>>(emptyList())
    /** Names of the bird families available as filters. */
    val familias: StateFlow<List<String>> = _familias

    private val _textoBusqueda = MutableStateFlow("")
    /** Text currently entered in the search box. */
    val textoBusqueda: StateFlow<String> = _textoBusqueda

    private val _error = MutableStateFlow<String?>(null)
    /** Last error message, or `null` when there is none. */
    val error: StateFlow<String?> = _error
    init {
        cargarPublicaciones()
        cargarFamilias()
    }

    /** Reloads every publication from the backend. */
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
    /** Loads the bird families used to filter publications. */
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
    /**
     * Updates the search text.
     *
     * @param nuevoTexto Text introduced by the user.
     */
    fun actualizarTextoBusqueda(nuevoTexto: String) {
        _textoBusqueda.value = nuevoTexto
    }
    /** Replaces the visible publications with the ones matching the search text. */
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

    /**
     * Replaces the visible publications with the ones belonging to a family.
     *
     * @param familia Name of the family to filter by.
     */
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

    /** Clears the current error message. */
    fun limpiarError() {
        _error.value = null
    }
}

