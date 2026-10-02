package es.unex.natureconnect.data.models

/**
 * A tag that can be attached to a publication.
 *
 * @property id Unique identifier of the tag.
 * @property nombre Display name of the tag.
 * @property seleccionada Whether the tag has been selected by the current user.
 */
data class Etiqueta(
    val id: Int,
    val nombre: String,
    val seleccionada: Boolean

)
