package es.unex.natureconnect.data.models

/**
 * Join record between a [Publicacion] and an [Etiqueta].
 *
 * Represents one tag associated with a publication.
 *
 * @property id Unique identifier of the association.
 * @property idEtiqueta Tag attached to the publication.
 */
data class etiquetaPublicacions(
    val id: Int,
    val idEtiqueta: Etiqueta

)
