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

/**
 * Repository that groups every publication-related operation.
 *
 * Wraps [natureconectAPI] so that view models never access the network layer
 * directly.
 *
 * @param api Retrofit service used to reach the backend.
 */
class PublicacionesRespository(private val api: natureconectAPI) {
    /**
     * Fetches every publication in the feed.
     *
     * @return Envelope with the list of publications.
     */
    suspend fun listarPlublicaciones(): ListarPublicacionesResponse {
        return api.getPublicaciones()
    }
    /**
     * Fetches a single publication as seen by a given user.
     *
     * @param idPublicacion Identifier of the publication.
     * @param idUsuario Identifier of the user requesting it.
     * @return Envelope with the requested publication.
     */
    suspend fun getPublicacion(idPublicacion: String,idUsuario: String): getPublicacionResponse {
     return api.getPublicacion(idPublicacion,idUsuario)
    }
    /**
     * Fetches all bird species.
     *
     * @return Envelope with the list of birds.
     */
    suspend fun getAves(): ListarAvesResponse {
        return api.getAves()
    }
    /**
     * Fetches all tags.
     *
     * @return Envelope with the list of tags.
     */
    suspend fun getEtiquetas(): ListarEtiquetasResponse {
        return api.getEtiquetas()
    }
/*
    suspend fun uploadImage(image: MultipartBody.Part): Response<ResponseBody> {
        return api.uploadImage(image)
    }
*/
    /**
     * Uploads a photo and creates the publication that references it.
     *
     * @param idUsuario Identifier of the author.
     * @param latitud Latitude where the photo was taken.
     * @param longitud Longitude where the photo was taken.
     * @param image Multipart part containing the photo to publish.
     * @return Envelope with the newly created publication.
     */
    suspend fun newPublicacion(idUsuario: String,latitud: String,longitud: String,image: MultipartBody.Part): NewPublicacionResponse {
        api.uploadImage(image)
        return api.newPublicacion(idUsuario,latitud,longitud)

    }
    /**
     * Links a bird to an existing publication.
     *
     * @param idPublicacion Identifier of the publication.
     * @param idAve Identifier of the bird to link.
     * @return Raw HTTP response from the backend.
     */
    suspend fun newAvePublicacion(idPublicacion: String,idAve: String): Response<ResponseBody> {
        return api.newAvePublicacion(idPublicacion,idAve)
    }
    /**
     * Links a tag to an existing publication.
     *
     * @param idPublicacion Identifier of the publication.
     * @param idEtiqueta Identifier of the tag to link.
     * @return Raw HTTP response from the backend.
     */
    suspend fun newEtiquetaPublicacion(idPublicacion: String,idEtiqueta: String): Response<ResponseBody> {
        return api.newEtiquetaPublicacion(idPublicacion,idEtiqueta)
    }
    /**
     * Fetches the publications created by a user.
     *
     * @param idUsuario Identifier of the author.
     * @return Envelope with the user's publications.
     */
    suspend fun getMisPublicaciones(idUsuario: String): ListarPublicacionesResponse {
        return api.getMisPublicaciones(idUsuario)
    }
    /**
     * Fetches a publication without requiring an authenticated user.
     *
     * @param idPublicacion Identifier of the publication.
     * @return Envelope with the requested publication.
     */
    suspend fun getPublicacionInvitado(idPublicacion: String): getPublicacionResponse {
        return api.getPublicacionInvitado(idPublicacion)
    }

    /**
     * Adds a like from a user to a publication.
     *
     * @param idPublicacion Identifier of the publication.
     * @param idUsuario Identifier of the user giving the like.
     * @return Envelope with the updated publication.
     */
    suspend fun darLike(idPublicacion: String,idUsuario: String): getPublicacionResponse {
        return api.darLike(idPublicacion,idUsuario)
    }
    /**
     * Removes a like previously given by a user.
     *
     * @param idPublicacion Identifier of the publication.
     * @param idUsuario Identifier of the user withdrawing the like.
     * @return Envelope with the updated publication.
     */
    suspend fun quitarLike(idPublicacion: String,idUsuario: String): getPublicacionResponse {
        return api.quitarLike(idPublicacion,idUsuario)

    }
    /**
     * Fetches the notifications generated since a given date.
     *
     * @param idUsuario Identifier of the user being notified.
     * @param fecha Lower bound timestamp, formatted as a string.
     * @return Envelope with the matching notifications.
     */
    suspend fun getNotificaciones(idUsuario: String,fecha: String): notificacionesResponse {
        return api.getNotificaciones(idUsuario,fecha)

    }
    /**
     * Searches publications by free text.
     *
     * @param texto Text introduced by the user.
     * @return Envelope with the matching publications.
     */
    suspend fun getPublicacionesBusqueda(texto: String): ListarPublicacionesResponse {
        return api.getPublicacionesBusqueda(texto)
    }
    /**
     * Fetches all bird families.
     *
     * @return Envelope with the list of families.
     */
    suspend fun getFamilia(): familiaResponse {
        return api.getFamilias()
    }
    /**
     * Filters publications by bird family.
     *
     * @param familia Name of the family to filter by.
     * @return Envelope with the matching publications.
     */
    suspend fun getPublicacionesFamilia(familia: String): ListarPublicacionesResponse {
        return api.getPublicacionesFamilia(familia)

    }
    /**
     * Searches bird species by free text.
     *
     * @param texto Text introduced by the user.
     * @return Envelope with the matching birds.
     */
    suspend fun getAvesBuscar(texto: String): ListarAvesResponse {
        return api.getAvesBuscar(texto)

    }
    /**
     * Filters bird species by family.
     *
     * @param familia Name of the family to filter by.
     * @return Envelope with the matching birds.
     */
    suspend fun getAvesFamilia(familia: String): ListarAvesResponse {
        return api.getAvesFamilia(familia)
    }
    /**
     * Searches tags by free text.
     *
     * @param texto Text introduced by the user.
     * @return Envelope with the matching tags.
     */
    suspend fun getEtiquetasBuscar(texto: String): ListarEtiquetasResponse {
        return api.getEtiquetasBuscar(texto)

    }
}