package es.unex.natureconnect.data.models

data class notificacionesResponse(
    val success:Boolean,
    val message:String,
    val data:List<notificacion>
)
