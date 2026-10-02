package es.unex.natureconnect.data.models

/**
 * Envelope returned by the API when listing tags.
 *
 * @property success Whether the request completed successfully.
 * @property message Human-readable message describing the result.
 * @property data List of tags returned by the endpoint.
 */
data class ListarEtiquetasResponse(
    val success:Boolean,
    val message:String,
    val data:List<Etiqueta>
)
