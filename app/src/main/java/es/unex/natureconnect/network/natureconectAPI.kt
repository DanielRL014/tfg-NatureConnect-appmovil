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

interface natureconectAPI{
    @FormUrlEncoded
    @POST("/api/usuarios/login")
    suspend fun loginUser(
        @Field("nombre") Nombre: String,
        @Field("password") password: String
    ): LoginResponse

    @GET("/api/publicacion/listar")
    suspend fun getPublicaciones(): ListarPublicacionesResponse

    @FormUrlEncoded
    @POST("/api/publicacion/ver")
    suspend fun getPublicacion( @Field("id_publicacion")id_publicacion: String,@Field("id_usuario")id_usuario: String): getPublicacionResponse

    @GET("/api/ave/listar")
    suspend fun getAves(): ListarAvesResponse

    @GET("/api/etiqueta/listar")
    suspend fun getEtiquetas(): ListarEtiquetasResponse

    @Multipart
    @POST("/api/publicacion/Subir")
    suspend fun uploadImage(
        @Part imagen: MultipartBody.Part
    ): Response<ResponseBody>


    @FormUrlEncoded
    @POST("/api/publicacion/crearP")
    suspend fun newPublicacion(@Field("id_usuario")id_usuario: String,@Field("latitud") latitud: String,@Field("longitud") longitud: String): NewPublicacionResponse

    @FormUrlEncoded
    @POST("/api/publicacion/nave")
    suspend fun newAvePublicacion(@Field("id_publicacion")id_publicacion: String,@Field("id_ave") id_ave: String): Response<ResponseBody>

    @FormUrlEncoded
    @POST("/api/publicacion/nEtiqueta")
    suspend fun newEtiquetaPublicacion(@Field("id_publicacion")id_publicacion: String,@Field("id_etiqueta") id_etiqueta: String): Response<ResponseBody>


    @FormUrlEncoded
    @POST("/api/usuarios/registrar")
    suspend fun registerUser(
        @Field("nombre") Nombre: String,
        @Field("password") password: String,
        @Field("email") email: String
    ): LoginResponse


    @FormUrlEncoded
    @POST("/api/publicacion/misPublicaciones")
    suspend fun getMisPublicaciones(@Field("id_usuario") id_usuario: String,): ListarPublicacionesResponse

    @FormUrlEncoded
    @POST("/api/publicacion/verInvitado")
    suspend fun getPublicacionInvitado( @Field("id_publicacion")id_publicacion: String): getPublicacionResponse

    @FormUrlEncoded
    @POST("/api/publicacion/dar")
    suspend fun darLike( @Field("id_publicacion")id_publicacion: String,@Field("id_usuario")id_usuario: String): getPublicacionResponse

    @FormUrlEncoded
    @POST("/api/publicacion/quitar")
    suspend fun quitarLike( @Field("id_publicacion")id_publicacion: String,@Field("id_usuario")id_usuario: String): getPublicacionResponse

    @FormUrlEncoded
    @POST("/api/notificaciones/notificaciones")
    suspend fun getNotificaciones( @Field("id_usuario")id_usuario: String,@Field("fecha")fecha: String): notificacionesResponse

    @FormUrlEncoded
    @POST("/api/publicacion/Buscar")
    suspend fun getPublicacionesBusqueda(@Field("texto")texto: String): ListarPublicacionesResponse

    @GET("/api/familia/listar")
    suspend fun getFamilias(): familiaResponse

    @FormUrlEncoded
    @POST("/api/publicacion/filtrarFamilia")
    suspend fun getPublicacionesFamilia(@Field("familia")texto: String): ListarPublicacionesResponse

    @FormUrlEncoded
    @POST("/api/ave/buscarAve")
    suspend fun getAvesBuscar(@Field("texto")texto: String): ListarAvesResponse

    @FormUrlEncoded
    @POST("/api/ave/filtrarFamilia")
    suspend fun getAvesFamilia(@Field("texto")texto: String): ListarAvesResponse

    @FormUrlEncoded
    @POST("/api/etiqueta/buscarEtiqueta")
    suspend fun getEtiquetasBuscar(@Field("texto")texto: String): ListarEtiquetasResponse

    @Multipart
    @POST("/api/deteccion/clasificar")
    suspend fun detectarAve(
        @Part imagen: MultipartBody.Part
    ): modeloResponse
}