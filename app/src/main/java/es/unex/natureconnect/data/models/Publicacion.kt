package es.unex.natureconnect.data.models

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
