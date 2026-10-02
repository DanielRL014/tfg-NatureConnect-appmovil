package es.unex.natureconnect.data.models

/**
 * A registered app user.
 *
 * @property id Unique identifier of the user.
 * @property nombreUsuario Display name of the user.
 * @property emailUsuario Email address used to sign in.
 */
data class Usuarios(
    val id: Int,
    val nombreUsuario: String,
    val emailUsuario: String
)

