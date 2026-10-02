package es.unex.natureconnect.data.models

data class ListarAvesResponse(
    val success:Boolean,
    val message:String,
    val data:List<Ave>
)
