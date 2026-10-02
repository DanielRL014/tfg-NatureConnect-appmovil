package es.unex.natureconnect.data.repository

import es.unex.natureconnect.data.models.modeloResponse
import es.unex.natureconnect.network.natureconectAPI
import okhttp3.MultipartBody

/**
 * Repository that talks to the bird recognition endpoints.
 *
 * Wraps [natureconectAPI] so that view models never access the network layer
 * directly.
 *
 * @param api Retrofit service used to reach the backend.
 */
class IARepository(private val api: natureconectAPI) {
    /**
     * Sends an image to the model and returns the birds found in it.
     *
     * @param imagen Multipart part containing the photo to analyse.
     * @return Detections reported by the recognition model.
     */
    suspend fun detectarAve(imagen: MultipartBody.Part):modeloResponse{
        return api.detectarAve(imagen)
    }
}