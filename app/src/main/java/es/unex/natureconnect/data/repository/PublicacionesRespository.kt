package es.unex.natureconnect.data.repository

import es.unex.natureconnect.data.models.ListarAvesResponse
import es.unex.natureconnect.data.models.ListarEtiquetasResponse
import es.unex.natureconnect.data.models.ListarPublicacionesResponse
import es.unex.natureconnect.data.models.NewPublicacionResponse
import es.unex.natureconnect.data.models.familiaResponse
import es.unex.natureconnect.data.models.getPublicacionResponse
import es.unex.natureconnect.data.models.notificacionesResponse

import es.unex.natureconnect.network.natureconectAPI
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response

class PublicacionesRespository(private val api: natureconectAPI) {
    suspend fun listarPlublicaciones(): ListarPublicacionesResponse {
        return api.getPublicaciones()
    }
    suspend fun getPublicacion(idPublicacion: String,idUsuario: String): getPublicacionResponse {
     return api.getPublicacion(idPublicacion,idUsuario)
    }
    suspend fun getAves(): ListarAvesResponse {
        return api.getAves()
    }
    suspend fun getEtiquetas(): ListarEtiquetasResponse {
        return api.getEtiquetas()
    }
/*
    suspend fun uploadImage(image: MultipartBody.Part): Response<ResponseBody> {
        return api.uploadImage(image)
    }
*/
    suspend fun newPublicacion(idUsuario: String,latitud: String,longitud: String,image: MultipartBody.Part): NewPublicacionResponse {
        api.uploadImage(image)
        return api.newPublicacion(idUsuario,latitud,longitud)

    }
    suspend fun newAvePublicacion(idPublicacion: String,idAve: String): Response<ResponseBody> {
        return api.newAvePublicacion(idPublicacion,idAve)
    }
    suspend fun newEtiquetaPublicacion(idPublicacion: String,idEtiqueta: String): Response<ResponseBody> {
        return api.newEtiquetaPublicacion(idPublicacion,idEtiqueta)
    }
    suspend fun getMisPublicaciones(idUsuario: String): ListarPublicacionesResponse {
        return api.getMisPublicaciones(idUsuario)
    }
    suspend fun getPublicacionInvitado(idPublicacion: String): getPublicacionResponse {
        return api.getPublicacionInvitado(idPublicacion)
    }

    suspend fun darLike(idPublicacion: String,idUsuario: String): getPublicacionResponse {
        return api.darLike(idPublicacion,idUsuario)
    }
    suspend fun quitarLike(idPublicacion: String,idUsuario: String): getPublicacionResponse {
        return api.quitarLike(idPublicacion,idUsuario)

    }
    suspend fun getNotificaciones(idUsuario: String,fecha: String): notificacionesResponse {
        return api.getNotificaciones(idUsuario,fecha)

    }
    suspend fun getPublicacionesBusqueda(texto: String): ListarPublicacionesResponse {
        return api.getPublicacionesBusqueda(texto)
    }
    suspend fun getFamilia(): familiaResponse {
        return api.getFamilias()
    }
    suspend fun getPublicacionesFamilia(familia: String): ListarPublicacionesResponse {
        return api.getPublicacionesFamilia(familia)

    }
    suspend fun getAvesBuscar(texto: String): ListarAvesResponse {
        return api.getAvesBuscar(texto)

    }
    suspend fun getAvesFamilia(familia: String): ListarAvesResponse {
        return api.getAvesFamilia(familia)
    }
    suspend fun getEtiquetasBuscar(texto: String): ListarEtiquetasResponse {
        return api.getEtiquetasBuscar(texto)

    }
}