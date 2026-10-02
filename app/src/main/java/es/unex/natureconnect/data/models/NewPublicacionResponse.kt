package es.unex.natureconnect.data.models

/**
 * Envelope returned by the API after creating a publication.
 *
 * @property success Whether the publication was created.
 * @property message Human-readable message describing the result.
 * @property data Newly created publication, or `null` on failure.
 */
data class NewPublicacionResponse(
    val success:Boolean,
    val message:String,
    val data:Publicacion?
)
