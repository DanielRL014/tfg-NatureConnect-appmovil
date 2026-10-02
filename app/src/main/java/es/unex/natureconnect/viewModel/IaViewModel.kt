package es.unex.natureconnect.viewModel

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import es.unex.natureconnect.data.models.modeloResponse
import es.unex.natureconnect.data.repository.IARepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class IaViewModel(private val repository: IARepository, application: Application) :
    AndroidViewModel(application) {

    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedImageUri: StateFlow<Uri?> = _selectedImageUri

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _resultados = MutableStateFlow<modeloResponse?>(null)
    val resultados: StateFlow<modeloResponse?> = _resultados

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    private val _recivido = MutableStateFlow(false)
    val recivido: StateFlow<Boolean> = _recivido

    private val sharedPreferences: SharedPreferences =
        application.getSharedPreferences("NatureConnectPrefs", Context.MODE_PRIVATE)

    fun updateSelectedImage(uri: Uri?) {
        _selectedImageUri.value = uri
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

    fun detectarAves(context: Context) {
        val uri = selectedImageUri.value
        _isLoading.value = true
        if (uri == null) {
            _error.value = "No se ha seleccionado una imagen."
            return
        }

        viewModelScope.launch {
            val result = try {
            val imagePart = withContext(Dispatchers.IO) { prepareImagePart(context, uri) }
            val respuesta = withContext(Dispatchers.IO) { repository.detectarAve(imagePart) }
            respuesta
        } catch (e: Exception) {
            _error.value = "Error al procesar la imagen: ${e.localizedMessage}"
            null
        }

            _resultados.value = result
            _recivido.value = true
            _isLoading.value = false
        }
    }
    fun resetEstado() {
        _recivido.value = false
        _isLoading.value = false
        _error.value = null
    }
}