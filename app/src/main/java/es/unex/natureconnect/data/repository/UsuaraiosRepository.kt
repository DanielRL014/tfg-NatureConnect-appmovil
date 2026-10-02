package es.unex.natureconnect.data.repository

import es.unex.natureconnect.data.models.LoginResponse
import es.unex.natureconnect.network.natureconectAPI


class UsuaraiosRepository (private val api: natureconectAPI){


    suspend fun loginUser(nombre: String, password: String): LoginResponse {
        return api.loginUser(nombre, password)
    }
    suspend fun registerUser(nombre: String, password: String, email: String): LoginResponse {
        return api.registerUser(nombre, password, email)

    }
}