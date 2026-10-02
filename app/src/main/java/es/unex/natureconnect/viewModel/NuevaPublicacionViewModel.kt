package es.unex.natureconnect.vi

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.location.Location
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import es.unex.natureconnect.data.models.Ave
import es.unex.natureconnect.data.models.Etiqueta
import es.unex.natureconnect.data.models.ListarAvesResponse
import es.unex.natureconnect.data.models.ListarEtiquetasResponse
import es.unex.natureconnect.data.models.NuevaPublicacion
import es.unex.natureconnect.data.models.familiaResponse
import es.unex.natureconnect.data.repository.PublicacionesRespository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File


/**
 * View model that drives the new publication flow.
 *
 * Keeps the draft publication, the selectable birds and tags, the photo and
 * the location until the user publishes it.
 *
 * @param repository Repository used to reach the publication endpoints.
 * @param application Application used to access shared preferences.
 */
class NuevaPublicacionViewModel(private val repository: PublicacionesRespository, application: Application) :
    AndroidViewModel(application) {
    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    /** Photo picked for the new publication, or `null` when none is selected. */
    val selectedImageUri: StateFlow<Uri?> = _selectedImageUri

    private val _familias=MutableStateFlow<List<String>>(emptyList())
    /** Names of the bird families available as filters. */
    val familias: StateFlow<List<String>> = _familias

    private val sharedPreferences: SharedPreferences = application.getSharedPreferences("NatureConnectPrefs", Context.MODE_PRIVATE)

    /** Identifier of the signed-in user read from shared preferences. */
    val idU: String? = sharedPreferences.getString("id", null)

    private val _Location = MutableStateFlow<Location?>(null)
    /** Location attached to the publication, either GPS-provided or manual. */
    val Location: StateFlow<Location?> = _Location

    private val _error = MutableStateFlow<String?>(null)
    /** Last error message, or `null` when there is none. */
    val error: StateFlow<String?> = _error

    private val _PublicacionNueva=MutableStateFlow<NuevaPublicacion?>( NuevaPublicacion(null,null,null,null,null,emptyList(),emptyList()))
    /** Draft publication being built by the user. */
    val PublicacionNueva: StateFlow<NuevaPublicacion?> = _PublicacionNueva

    private val _Aves = MutableStateFlow<List<Ave>>(emptyList())
    /** Birds offered by the picker, each one carrying its selection flag. */
    val Aves: StateFlow<List<Ave>> = _Aves

    private val _Etiquetas = MutableStateFlow<List<Etiqueta>>(emptyList())
    /** Tags offered by the picker, each one carrying its selection flag. */
    val Etiquetas: StateFlow<List<Etiqueta>> = _Etiquetas

    init {
        cargarAves()
        cargarEtiquetas()
        cargarFamilias()
    }

    private val _textoBusqueda = MutableStateFlow("")
    /** Text shared by the bird and tag search boxes. */
    val textoBusqueda: StateFlow<String> = _textoBusqueda

    /** Loads the bird families used to filter the bird picker. */
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
     * Updates the search text and refreshes the bird list accordingly.
     *
     * An empty [nuevoTexto] restores the full bird list.
     *
     * @param nuevoTexto Text introduced by the user.
     */
    fun actualizarTextoBusquedaA(nuevoTexto: String) {
        _textoBusqueda.value = nuevoTexto
        viewModelScope.launch {
            try {
                var response: ListarAvesResponse
                if (nuevoTexto.isEmpty()) {
                     response = repository.getAves()
                }else{
                     response = repository.getAvesBuscar(nuevoTexto)
                }

                if (response.success) {
                    _Aves.value = response.data ?: emptyList()
                } else {
                    _error.value = response.message
                }
            } catch (e: Exception) {
                _error.value = "Error al cargar las publicaciones: ${e.message}"
            }
        }

    }
    /**
     * Updates the search text and refreshes the tag list accordingly.
     *
     * An empty [nuevoTexto] restores the full tag list.
     *
     * @param nuevoTexto Text introduced by the user.
     */
    fun actualizarTextoBusquedaE(nuevoTexto: String) {
        _textoBusqueda.value = nuevoTexto
        viewModelScope.launch {
            try {
                val response: ListarEtiquetasResponse
                if (nuevoTexto.isEmpty()) {
                    response = repository.getEtiquetas()
                } else {
                    response = repository.getEtiquetasBuscar(nuevoTexto)
                }
                if (response.success) {
                    _Etiquetas.value = response.data ?: emptyList()
                } else {
                    _error.value = response.message
                }
            } catch (e: Exception) {
                _error.value = "Error al cargar las Etiquetas: ${e.message}"
            }
        }

    }


    /**
     * Adds or removes a bird from the draft publication.
     *
     * @param ave Bird to update.
     * @param seleccionado `true` to select the bird, `false` to deselect it.
     */
    fun actualizarSeleccion(ave: Ave, seleccionado: Boolean) {
        val avesActualizadas = _PublicacionNueva.value?.aves?.toMutableList() ?: mutableListOf()
        if (seleccionado) {

            val listaAvesActualizada = _Aves.value.map { aveL ->
                if (aveL.id == ave.id) {
                    aveL.copy(selecionada = true)
                } else {
                    aveL
                }
            }
            _Aves.value = listaAvesActualizada
            avesActualizadas.add(ave)
        } else {
            var aveAux = Ave(ave.id,ave.nombreComun,ave.nombreCientifico,ave.idFamilizaAve,false)
            avesActualizadas.remove(aveAux)
            val listaAvesActualizada = _Aves.value.map { aveL ->
                if (aveL.id == ave.id) {
                    aveL.copy(selecionada = false)
                } else {
                    aveL
                }
            }
            _Aves.value = listaAvesActualizada

        }
        _PublicacionNueva.value = _PublicacionNueva.value?.copy(aves = avesActualizadas)

    }

    /**
     * Adds or removes a tag from the draft publication.
     *
     * @param etiqueta Tag to update.
     * @param seleccionado `true` to select the tag, `false` to deselect it.
     */
    fun actualizarSeleccionEtiquta(etiqueta: Etiqueta, seleccionado: Boolean){
        val EtiqtasActualizadas = _PublicacionNueva.value?.etiquetas?.toMutableList() ?: mutableListOf()
        if (seleccionado) {

            val listaEtiqutasActualizada = _Etiquetas.value.map { EtiqutaL ->
                if (EtiqutaL.id == etiqueta.id) {
                    EtiqutaL.copy(seleccionada = true)
                } else {
                    EtiqutaL
                }
            }
            _Etiquetas.value = listaEtiqutasActualizada

            EtiqtasActualizadas.add(etiqueta)
        } else {
            var etiquetaAux = Etiqueta(etiqueta.id,etiqueta.nombre,false)
            EtiqtasActualizadas.remove(etiquetaAux)
            val listaEtiqutasActualizada = _Etiquetas.value.map { EtiqutaL ->
                if (EtiqutaL.id == etiqueta.id) {
                    EtiqutaL.copy(seleccionada = false)
                } else {
                    EtiqutaL
                }
            }
            _Etiquetas.value = listaEtiqutasActualizada

        }
        _PublicacionNueva.value = _PublicacionNueva.value?.copy(etiquetas = EtiqtasActualizadas)

    }

    /** Loads every bird species offered by the picker. */
    fun cargarAves() {
        viewModelScope.launch {
            try {
                val response = repository.getAves()
                if (response.success) {
                    _Aves.value = response.data ?: emptyList()
                } else {
                    _error.value = response.message
                }
            } catch (e: Exception) {
                _error.value = "Error al cargar las publicaciones: ${e.message}"
            }
        }
    }
    /** Loads every tag offered by the picker. */
    fun cargarEtiquetas() {
        viewModelScope.launch {
            try {
                val response = repository.getEtiquetas()
                if (response.success) {
                    _Etiquetas.value = response.data ?: emptyList()
                } else {
                    _error.value = response.message
                }
            } catch (e: Exception) {
                _error.value = "Error al cargar las publicaciones: ${e.message}"
            }
        }
    }

    /**
     * Stores the picked photo and attaches it to the draft publication.
     *
     * @param uri Content URI of the picked photo, or `null` to clear it.
     */
    fun updateSelectedImage(uri: Uri?) {
        _selectedImageUri.value = uri
        if (uri != null) {
            _PublicacionNueva.value?.foto  = uri
        }
    }


    /**
     * Reads the last known device location and attaches it to the draft.
     *
     * Updates [error] when the location is unavailable or permission is denied.
     *
     * @param context Context used to reach the fused location provider.
     */
    fun obtenerUbicacionActual(context: Context) {
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        viewModelScope.launch {
            try {
                val lastLocation = fusedLocationProviderClient.lastLocation.await()
                if (lastLocation != null) {
                    _Location.value = lastLocation
                    _PublicacionNueva.value?.latitud  =lastLocation.latitude.toString()
                    _PublicacionNueva.value?.longitud  =lastLocation.longitude.toString()
                    _error.value  = null
                } else {
                    _error.value = "No se pudo obtener la ubicación actual."
                }
            } catch (e: SecurityException) {
                _error.value = "Error al acceder a la ubicación: ${e.message}"
            }
        }
    }
    /**
     * Attaches a location chosen on the map to the draft publication.
     *
     * @param ubicacion Coordinates selected by the user.
     */
    fun guardarUbicacionManual(ubicacion: LatLng) {
        val location = Location("manual")
        location.latitude = ubicacion.latitude
        location.longitude = ubicacion.longitude
        _PublicacionNueva.value?.latitud = ubicacion.latitude.toString()
        _PublicacionNueva.value?.longitud = ubicacion.longitude.toString()
        _Location.value =location

    }

    /**
     * Uploads the photo, creates the publication and links its birds and tags.
     *
     * Updates [error] when any of the steps fails.
     *
     * @param context Context used to read the photo content.
     */
    fun publicar(context: Context) {
        val imagePart = _selectedImageUri.value?.let { prepareImagePart(context, it) }
        var idPublicacion:String=""
        viewModelScope.launch {
            try {

                val response = idU?.let { repository.newPublicacion(it,"${_PublicacionNueva.value?.latitud}","${_PublicacionNueva.value?.longitud}", imagePart!! )}
                if (response != null) {
                    if (response.success) {
                        idPublicacion = response.data?.idPublicacion.toString()
                    } else {
                        _error.value = response.message
                    }
                }
            } catch (e: Exception) {
                _error.value = "Error al crear la publicacion: ${e.message}"
            }
            
            _PublicacionNueva.value?.aves?.forEach {
                try{
                    val response = repository.newAvePublicacion(idPublicacion, it.id.toString())
                    if (response.isSuccessful)  {

                    } else {
                        _error.value = response.errorBody()?.string() ?: "Error al añadir un ave"
                    }
                } catch (e: Exception) {
                    _error.value = "Error al añadir un ave: ${e.message}"
                }
            }
            _PublicacionNueva.value?.etiquetas?.forEach {
                try{
                    val response = repository.newEtiquetaPublicacion(idPublicacion, it.id.toString())
                    if (response.isSuccessful)  {

                    } else {
                        _error.value = response.errorBody()?.string() ?: "Error al añadir una etiqueta"
                    }
                } catch (e: Exception) {
                    _error.value = "Error al añadir una etiqueta: ${e.message}"
                }
            }




        }
        

    }

    /**
     * Copies the image into the cache directory and wraps it as a multipart part.
     *
     * @param context Context used to read the content URI.
     * @param uri Content URI of the image to upload.
     * @return Multipart part named `imagen` containing the JPEG data.
     */
    fun prepareImagePart(context: Context, uri: Uri): MultipartBody.Part {
        val file = File(context.cacheDir, "image.jpg").apply {
            context.contentResolver.openInputStream(uri)?.use { input ->
                outputStream().use { output ->
                    input.copyTo(output)
                }
            }
        }

        val requestFile = file
            .asRequestBody("image/jpeg".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("imagen", file.name, requestFile)
    }
    /**
     * Replaces the bird list with the ones belonging to a family.
     *
     * @param familia Name of the family to filter by.
     */
    fun filtrarPorFamilia(familia: String) {

        viewModelScope.launch {
            try {
                val response = repository.getAvesFamilia(familia)
                if (response.success) {
                    _Aves.value = response.data ?: emptyList()
                } else {
                    _error.value = response.message
                }
            } catch (e: Exception) {
                _error.value = "Error al cargar las publicaciones: ${e.message}"
            }
        }
    }

}


