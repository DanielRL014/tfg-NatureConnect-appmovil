package es.unex.natureconnect.data.models

/**
 * A photo publication shared in the app feed.
 *
 * @property idPublicacion Unique identifier of the publication.
 * @property idUsuario Author of the publication.
 * @property idFoto Identifier of the stored photograph.
 * @property meGustas Number of likes the publication has received.
 * @property latitud Latitude where the photo was taken.
 * @property longitud Longitude where the photo was taken.
 * @property etiquetaPublicacions Tags attached to the publication.
 * @property avesPublicacions Birds featured in the publication.
 * @property hasLiked Whether the current user has liked the publication.
 */
data class Publicacion(
    val idPublicacion: Int,
    val idUsuario: Usuarios,
    val idFoto: String,
    var meGustas: Int,
    val latitud: String,
    val longitud: String,
    val etiquetaPublicacions: List<etiquetaPublicacions>,
    val avesPublicacions: List<avesPublicacions>,
    var hasLiked: Boolean
)
