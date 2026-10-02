package es.unex.natureconnect.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import es.unex.natureconnect.data.models.ListarPublicacionesResponse
import es.unex.natureconnect.data.models.Publicacion
import es.unex.natureconnect.data.models.familiaResponse
import es.unex.natureconnect.data.repository.PublicacionesRespository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * View model that backs the main feed screen.
 *
 * Loads the publications to show, honouring the search text received through
 * the navigation arguments, together with the families used as filters.
 *
 * @param repository Repository used to reach the publication endpoints.
 * @param navController Controller used to read the incoming search text.
 */
class PrincipalViewModel(private val repository: PublicacionesRespository,navController: NavController) : ViewModel() {
    /** Placeholder for the selected image; not implemented yet. */
    val selectedImageUri: Any
        get() {
            TODO()
        }
    private val _publicaciones = MutableStateFlow<List<Publicacion>>(emptyList())
    /** Publications currently shown in the feed. */
    val publicaciones: StateFlow<List<Publicacion>> = _publicaciones

    private val _familias=MutableStateFlow<List<String>>(emptyList())
    /** Names of the bird families available as filters. */
    val familias: StateFlow<List<String>> = _familias

    private val _textoBusqueda = MutableStateFlow("")
    /** Text currently entered in the search box. */
    val textoBusqueda: StateFlow<String> = _textoBusqueda

    /** Back stack entry used to read the navigation arguments. */
    val backStackEntry: NavBackStackEntry? = navController.currentBackStackEntry

    /** Search text received from the previous screen, or `null`. */
    val textoRecibido = backStackEntry?.arguments?.getString("texto")

    private val _error = MutableStateFlow<String?>(null)
    /** Last error message, or `null` when there is none. */
    val error: StateFlow<String?> = _error
    init {
        cargarPublicaciones()
        cargarFamilias()
    }

    /**
     * Loads the feed, applying the incoming search text when present.
     */
    fun cargarPublicaciones() {
        viewModelScope.launch {
            try {
                val response:ListarPublicacionesResponse
                if(textoRecibido!=null){
                    response = repository.getPublicacionesBusqueda("$textoRecibido")
                }else {
                    response = repository.listarPlublicaciones()
                }
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
    /** Loads the bird families used to filter the feed. */
    fun cargarFamilias() {
        viewModelScope.launch {
            try {
                val response:familiaResponse

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
    /** Replaces the feed with the publications matching the search text. */
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
     * Replaces the feed with the publications belonging to a family.
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