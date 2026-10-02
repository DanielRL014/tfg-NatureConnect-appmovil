package es.unex.natureconnect.data.models

/**
 * Envelope returned by the API when fetching a single publication.
 *
 * @property success Whether the request completed successfully.
 * @property message Human-readable message describing the result.
 * @property data Publication returned by the endpoint.
 */
data class getPublicacionResponse(
    val success:Boolean,
    val message:String,
    val data:Publicacion
)
