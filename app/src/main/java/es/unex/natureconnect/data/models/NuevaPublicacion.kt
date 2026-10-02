package es.unex.natureconnect.data.models

import android.net.Uri

/**
 * Payload used to create a new publication.
 *
 * @property id_usuario Identifier of the author, or `null` for guest users.
 * @property foto URI of the photograph to publish.
 * @property me_gustas Initial number of likes.
 * @property latitud Latitude where the photo was taken.
 * @property longitud Longitude where the photo was taken.
 * @property etiquetas Tags attached to the publication.
 * @property aves Birds featured in the publication.
 */
data class NuevaPublicacion(
    val id_usuario: Int?,
    var foto: Uri?,
    val me_gustas: Int?,
    var latitud: String?,
    var longitud: String?,
    val etiquetas: List<Etiqueta>?,
    val aves: List<Ave>?,
)
