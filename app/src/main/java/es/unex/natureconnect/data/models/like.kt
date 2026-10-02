package es.unex.natureconnect.data.models

/**
 * A like given to a publication by a user.
 *
 * @property idUsuario User who liked the publication.
 * @property nombre Display name of the user who liked the publication.
 * @property fecha Timestamp of the like, formatted as a string.
 */
data class like(
    val idUsuario: Usuarios,
    val nombre: String,
    val fecha:String

)
