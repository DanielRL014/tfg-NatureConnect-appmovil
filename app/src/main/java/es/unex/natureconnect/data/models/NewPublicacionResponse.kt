package es.unex.natureconnect.data.models

data class NewPublicacionResponse(
    val success:Boolean,
    val message:String,
    val data:Publicacion?
)
