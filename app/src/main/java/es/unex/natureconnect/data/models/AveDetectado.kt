package es.unex.natureconnect.data.models

data class AveDetectada(
    val bbox: List<Int>,
    val confianza_deteccion: Float,
    val clase: String,
    val confianza_clasificacion: Float
)
