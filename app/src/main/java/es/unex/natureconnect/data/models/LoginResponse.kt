package es.unex.natureconnect.data.models

/**
 * Envelope returned by the API after an authentication attempt.
 *
 * @property success Whether the credentials were accepted.
 * @property message Human-readable message describing the result.
 * @property data Authenticated user, or `null` when authentication fails.
 */
data class LoginResponse(
    val success:Boolean,
    val message:String,
    val data:Usuarios?

)
