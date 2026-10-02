package es.unex.natureconnect.data.models

/**
 * Envelope returned by the API when listing publications.
 *
 * @property success Whether the request completed successfully.
 * @property message Human-readable message describing the result.
 * @property data List of publications returned by the endpoint.
 */
data class ListarPublicacionesResponse(
    val success:Boolean,
    val message:String,
    val data:List<Publicacion>
)
