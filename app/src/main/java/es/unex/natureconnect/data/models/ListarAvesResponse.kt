package es.unex.natureconnect.data.models

/**
 * Envelope returned by the API when listing bird species.
 *
 * @property success Whether the request completed successfully.
 * @property message Human-readable message describing the result.
 * @property data List of birds returned by the endpoint.
 */
data class ListarAvesResponse(
    val success:Boolean,
    val message:String,
    val data:List<Ave>
)
