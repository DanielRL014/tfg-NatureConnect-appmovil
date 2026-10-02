package es.unex.natureconnect.data.models

/**
 * Envelope returned by the API when listing notifications.
 *
 * @property success Whether the request completed successfully.
 * @property message Human-readable message describing the result.
 * @property data Notifications returned by the endpoint.
 */
data class notificacionesResponse(
    val success:Boolean,
    val message:String,
    val data:List<notificacion>
)
