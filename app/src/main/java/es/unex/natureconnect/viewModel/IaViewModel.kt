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

/**
 * View model that drives the bird detection flow.
 *
 * Holds the selected image, the model results and the loading/error state
 * shared by the detection screens.
 *
 * @param repository Repository used to reach the recognition endpoint.
 * @param application Application used to access shared preferences.
 */
class IaViewModel(private val repository: IARepository, application: Application) :
    AndroidViewModel(application) {

    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    /** Image picked by the user for analysis, or `null` when none is selected. */
    val selectedImageUri: StateFlow<Uri?> = _selectedImageUri

    private val _error = MutableStateFlow<String?>(null)
    /** Last error message shown to the user, or `null` when there is none. */
    val error: StateFlow<String?> = _error

    private val _resultados = MutableStateFlow<modeloResponse?>(null)
    /** Birds detected in the analysed image, or `null` before the first run. */
    val resultados: StateFlow<modeloResponse?> = _resultados

    private val _isLoading = MutableStateFlow(false)
    /** Whether a detection request is currently in progress. */
    val isLoading: StateFlow<Boolean> = _isLoading
    private val _recivido = MutableStateFlow(false)
    /** Whether the model has already produced a result for the current image. */
    val recivido: StateFlow<Boolean> = _recivido

    private val sharedPreferences: SharedPreferences =
        application.getSharedPreferences("NatureConnectPrefs", Context.MODE_PRIVATE)

    /**
     * Stores the image that will be sent to the model.
     *
     * @param uri Content URI of the picked image, or `null` to clear the selection.
     */
    fun updateSelectedImage(uri: Uri?) {
        _selectedImageUri.value = uri
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
     * Sends the selected image to the recognition model and publishes the result.
     *
     * Updates [error] when no image is selected or the request fails.
     *
     * @param context Context used to read the image content.
     */
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
    /** Clears the result, loading and error state so a new detection can start. */
    fun resetEstado() {
        _recivido.value = false
        _isLoading.value = false
        _error.value = null
    }
}