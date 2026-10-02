package es.unex.natureconnect.data.models

data class ListarEtiquetasResponse(
    val success:Boolean,
    val message:String,
    val data:List<Etiqueta>
)
