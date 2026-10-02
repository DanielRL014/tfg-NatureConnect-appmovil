package es.unex.natureconnect.data.models

/**
 * Join record between a [Publicacion] and an [Ave].
 *
 * Represents one bird associated with a publication.
 *
 * @property id Unique identifier of the association.
 * @property idAve Bird included in the publication.
 */
data class avesPublicacions(
    val id: Int,
    val idAve: Ave
)
