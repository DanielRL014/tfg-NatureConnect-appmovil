package es.unex.natureconnect.data.models

/**
 * Envelope returned by the API when listing bird families.
 *
 * @property success Whether the request completed successfully.
 * @property message Human-readable message describing the result.
 * @property data List of families returned by the endpoint.
 */
data class familiaResponse(
    val success:Boolean,
    val message:String,
    val data:List<familia>
)
