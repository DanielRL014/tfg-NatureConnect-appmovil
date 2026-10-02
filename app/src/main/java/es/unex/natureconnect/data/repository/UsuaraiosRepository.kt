package es.unex.natureconnect.data.repository

import es.unex.natureconnect.data.models.LoginResponse
import es.unex.natureconnect.network.natureconectAPI


/**
 * Repository that groups the user account operations.
 *
 * Wraps [natureconectAPI] so that view models never access the network layer
 * directly.
 *
 * @param api Retrofit service used to reach the backend.
 */
class UsuaraiosRepository (private val api: natureconectAPI){


    /**
     * Authenticates a user against the backend.
     *
     * @param nombre Username used to sign in.
     * @param password Password associated with the username.
     * @return Envelope containing the authenticated user, or `null` data on failure.
     */
    suspend fun loginUser(nombre: String, password: String): LoginResponse {
        return api.loginUser(nombre, password)
    }
    /**
     * Creates a new user account.
     *
     * @param nombre Username for the new account.
     * @param password Password for the new account.
     * @param email Email address for the new account.
     * @return Envelope containing the created user, or `null` data on failure.
     */
    suspend fun registerUser(nombre: String, password: String, email: String): LoginResponse {
        return api.registerUser(nombre, password, email)

    }
}