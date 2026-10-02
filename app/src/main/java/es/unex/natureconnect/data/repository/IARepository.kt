package es.unex.natureconnect.data.repository

import es.unex.natureconnect.data.models.modeloResponse
import es.unex.natureconnect.network.natureconectAPI
import okhttp3.MultipartBody

class IARepository(private val api: natureconectAPI) {
    suspend fun detectarAve(imagen: MultipartBody.Part):modeloResponse{
        return api.detectarAve(imagen)
    }
}