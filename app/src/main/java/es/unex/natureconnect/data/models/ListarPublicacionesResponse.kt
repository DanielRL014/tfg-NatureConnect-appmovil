package es.unex.natureconnect.data.models

data class ListarPublicacionesResponse(
    val success:Boolean,
    val message:String,
    val data:List<Publicacion>
)
