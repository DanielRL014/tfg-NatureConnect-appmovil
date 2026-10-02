package es.unex.natureconnect.data.models

/**
 * Notification listing the likes received by a publication.
 *
 * @property id_publicacion Identifier of the publication that received the likes.
 * @property likes Users who liked the publication.
 */
data class notificacion(
    val id_publicacion: String,
    val likes: List<like>
)
