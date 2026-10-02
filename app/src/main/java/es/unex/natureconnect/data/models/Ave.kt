package es.unex.natureconnect.data.models

data class Ave(
    val id: Int,
    val nombreComun: String,
    val nombreCientifico: String,
    //val proteccion: String,
    val idFamilizaAve: familia,
    var selecionada: Boolean
)
