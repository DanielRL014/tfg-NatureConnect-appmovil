package es.unex.natureconnect.data.models

data class LoginResponse(
    val success:Boolean,
    val message:String,
    val data:Usuarios?

)
