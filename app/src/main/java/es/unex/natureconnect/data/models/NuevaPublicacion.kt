package es.unex.natureconnect.data.models

import android.net.Uri

data class NuevaPublicacion(
    val id_usuario: Int?,
    var foto: Uri?,
    val me_gustas: Int?,
    var latitud: String?,
    var longitud: String?,
    val etiquetas: List<Etiqueta>?,
    val aves: List<Ave>?,
)
