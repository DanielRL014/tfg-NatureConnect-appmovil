package es.unex.natureconnect.network

import es.unex.natureconnect.data.models.ListarAvesResponse
import es.unex.natureconnect.data.models.ListarEtiquetasResponse
import es.unex.natureconnect.data.models.ListarPublicacionesResponse
import es.unex.natureconnect.data.models.LoginResponse
import es.unex.natureconnect.data.models.NewPublicacionResponse
import es.unex.natureconnect.data.models.familiaResponse
import es.unex.natureconnect.data.models.getPublicacionResponse
import es.unex.natureconnect.data.models.modeloResponse
import es.unex.natureconnect.data.models.notificacionesResponse
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

/**
 * Retrofit contract for the NatureConnect REST API.
 *
 * Every endpoint is declared as a `suspend` function so callers can invoke it
 * from a coroutine without blocking the main thread.
 */
interface natureconectAPI{
    @FormUrlEncoded
    @POST("/api/usuarios/login")
    /**
     * Authenticates a user.
     *
     * @param Nombre Username used to sign in.
     * @param password Password associated with the username.
     * @return Envelope containing the authenticated user.
     */
    suspend fun loginUser(
        @Field("nombre") Nombre: String,
        @Field("password") password: String
    ): LoginResponse

    @GET("/api/publicacion/listar")
    /**
     * Lists every publication in the feed.
     *
     * @return Envelope with the publications.
     */
    suspend fun getPublicaciones(): ListarPublicacionesResponse

    @FormUrlEncoded
    @POST("/api/publicacion/ver")
    /**
     * Fetches a publication as seen by a given user.
     *
     * @param id_publicacion Identifier of the publication.
     * @param id_usuario Identifier of the user requesting it.
     * @return Envelope with the requested publication.
     */
    suspend fun getPublicacion( @Field("id_publicacion")id_publicacion: String,@Field("id_usuario")id_usuario: String): getPublicacionResponse

    @GET("/api/ave/listar")
    /**
     * Lists all bird species.
     *
     * @return Envelope with the birds.
     */
    suspend fun getAves(): ListarAvesResponse

    @GET("/api/etiqueta/listar")
    /**
     * Lists all tags.
     *
     * @return Envelope with the tags.
     */
    suspend fun getEtiquetas(): ListarEtiquetasResponse

    @Multipart
    @POST("/api/publicacion/Subir")
    /**
     * Uploads a photo to the backend storage.
     *
     * @param imagen Multipart part containing the photo.
     * @return Raw HTTP response from the backend.
     */
    suspend fun uploadImage(
        @Part imagen: MultipartBody.Part
    ): Response<ResponseBody>


    @FormUrlEncoded
    @POST("/api/publicacion/crearP")
    /**
     * Creates a publication for an uploaded photo.
     *
     * @param id_usuario Identifier of the author.
     * @param latitud Latitude where the photo was taken.
     * @param longitud Longitude where the photo was taken.
     * @return Envelope with the newly created publication.
     */
    suspend fun newPublicacion(@Field("id_usuario")id_usuario: String,@Field("latitud") latitud: String,@Field("longitud") longitud: String): NewPublicacionResponse

    @FormUrlEncoded
    @POST("/api/publicacion/nave")
    /**
     * Links a bird to a publication.
     *
     * @param id_publicacion Identifier of the publication.
     * @param id_ave Identifier of the bird.
     * @return Raw HTTP response from the backend.
     */
    suspend fun newAvePublicacion(@Field("id_publicacion")id_publicacion: String,@Field("id_ave") id_ave: String): Response<ResponseBody>

    @FormUrlEncoded
    @POST("/api/publicacion/nEtiqueta")
    /**
     * Links a tag to a publication.
     *
     * @param id_publicacion Identifier of the publication.
     * @param id_etiqueta Identifier of the tag.
     * @return Raw HTTP response from the backend.
     */
    suspend fun newEtiquetaPublicacion(@Field("id_publicacion")id_publicacion: String,@Field("id_etiqueta") id_etiqueta: String): Response<ResponseBody>


    @FormUrlEncoded
    @POST("/api/usuarios/registrar")
    /**
     * Registers a new user account.
     *
     * @param Nombre Username for the new account.
     * @param password Password for the new account.
     * @param email Email address for the new account.
     * @return Envelope containing the created user.
     */
    suspend fun registerUser(
        @Field("nombre") Nombre: String,
        @Field("password") password: String,
        @Field("email") email: String
    ): LoginResponse


    @FormUrlEncoded
    @POST("/api/publicacion/misPublicaciones")
    /**
     * Lists the publications created by a user.
     *
     * @param id_usuario Identifier of the author.
     * @return Envelope with the user's publications.
     */
    suspend fun getMisPublicaciones(@Field("id_usuario") id_usuario: String,): ListarPublicacionesResponse

    @FormUrlEncoded
    @POST("/api/publicacion/verInvitado")
    /**
     * Fetches a publication without requiring an authenticated user.
     *
     * @param id_publicacion Identifier of the publication.
     * @return Envelope with the requested publication.
     */
    suspend fun getPublicacionInvitado( @Field("id_publicacion")id_publicacion: String): getPublicacionResponse

    @FormUrlEncoded
    @POST("/api/publicacion/dar")
    /**
     * Adds a like from a user to a publication.
     *
     * @param id_publicacion Identifier of the publication.
     * @param id_usuario Identifier of the user giving the like.
     * @return Envelope with the updated publication.
     */
    suspend fun darLike( @Field("id_publicacion")id_publicacion: String,@Field("id_usuario")id_usuario: String): getPublicacionResponse

    @FormUrlEncoded
    @POST("/api/publicacion/quitar")
    /**
     * Removes a like previously given by a user.
     *
     * @param id_publicacion Identifier of the publication.
     * @param id_usuario Identifier of the user withdrawing the like.
     * @return Envelope with the updated publication.
     */
    suspend fun quitarLike( @Field("id_publicacion")id_publicacion: String,@Field("id_usuario")id_usuario: String): getPublicacionResponse

    @FormUrlEncoded
    @POST("/api/notificaciones/notificaciones")
    /**
     * Fetches the notifications generated since a given date.
     *
     * @param id_usuario Identifier of the user being notified.
     * @param fecha Lower bound timestamp, formatted as a string.
     * @return Envelope with the matching notifications.
     */
    suspend fun getNotificaciones( @Field("id_usuario")id_usuario: String,@Field("fecha")fecha: String): notificacionesResponse

    @FormUrlEncoded
    @POST("/api/publicacion/Buscar")
    /**
     * Searches publications by free text.
     *
     * @param texto Text introduced by the user.
     * @return Envelope with the matching publications.
     */
    suspend fun getPublicacionesBusqueda(@Field("texto")texto: String): ListarPublicacionesResponse

    @GET("/api/familia/listar")
    /**
     * Lists all bird families.
     *
     * @return Envelope with the families.
     */
    suspend fun getFamilias(): familiaResponse

    @FormUrlEncoded
    @POST("/api/publicacion/filtrarFamilia")
    /**
     * Filters publications by bird family.
     *
     * @param texto Name of the family to filter by.
     * @return Envelope with the matching publications.
     */
    suspend fun getPublicacionesFamilia(@Field("familia")texto: String): ListarPublicacionesResponse

    @FormUrlEncoded
    @POST("/api/ave/buscarAve")
    /**
     * Searches bird species by free text.
     *
     * @param texto Text introduced by the user.
     * @return Envelope with the matching birds.
     */
    suspend fun getAvesBuscar(@Field("texto")texto: String): ListarAvesResponse

    @FormUrlEncoded
    @POST("/api/ave/filtrarFamilia")
    /**
     * Filters bird species by family.
     *
     * @param texto Name of the family to filter by.
     * @return Envelope with the matching birds.
     */
    suspend fun getAvesFamilia(@Field("texto")texto: String): ListarAvesResponse

    @FormUrlEncoded
    @POST("/api/etiqueta/buscarEtiqueta")
    /**
     * Searches tags by free text.
     *
     * @param texto Text introduced by the user.
     * @return Envelope with the matching tags.
     */
    suspend fun getEtiquetasBuscar(@Field("texto")texto: String): ListarEtiquetasResponse

    @Multipart
    @POST("/api/deteccion/clasificar")
    /**
     * Runs the recognition model on an uploaded photo.
     *
     * @param imagen Multipart part containing the photo to analyse.
     * @return Detections reported by the model.
     */
    suspend fun detectarAve(
        @Part imagen: MultipartBody.Part
    ): modeloResponse
}