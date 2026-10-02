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


class NuevaPublicacionViewModel(private val repository: PublicacionesRespository, application: Application) :
    AndroidViewModel(application) {
    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedImageUri: StateFlow<Uri?> = _selectedImageUri

    private val _familias=MutableStateFlow<List<String>>(emptyList())
    val familias: StateFlow<List<String>> = _familias

    private val sharedPreferences: SharedPreferences = application.getSharedPreferences("NatureConnectPrefs", Context.MODE_PRIVATE)

    val idU: String? = sharedPreferences.getString("id", null)

    private val _Location = MutableStateFlow<Location?>(null)
    val Location: StateFlow<Location?> = _Location

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _PublicacionNueva=MutableStateFlow<NuevaPublicacion?>( NuevaPublicacion(null,null,null,null,null,emptyList(),emptyList()))
    val PublicacionNueva: StateFlow<NuevaPublicacion?> = _PublicacionNueva

    private val _Aves = MutableStateFlow<List<Ave>>(emptyList())
    val Aves: StateFlow<List<Ave>> = _Aves

    private val _Etiquetas = MutableStateFlow<List<Etiqueta>>(emptyList())
    val Etiquetas: StateFlow<List<Etiqueta>> = _Etiquetas

    init {
        cargarAves()
        cargarEtiquetas()
        cargarFamilias()
    }

    private val _textoBusqueda = MutableStateFlow("")
    val textoBusqueda: StateFlow<String> = _textoBusqueda

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

    fun updateSelectedImage(uri: Uri?) {
        _selectedImageUri.value = uri
        if (uri != null) {
            _PublicacionNueva.value?.foto  = uri
        }
    }


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
    fun guardarUbicacionManual(ubicacion: LatLng) {
        val location = Location("manual")
        location.latitude = ubicacion.latitude
        location.longitude = ubicacion.longitude
        _PublicacionNueva.value?.latitud = ubicacion.latitude.toString()
        _PublicacionNueva.value?.longitud = ubicacion.longitude.toString()
        _Location.value =location

    }

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


