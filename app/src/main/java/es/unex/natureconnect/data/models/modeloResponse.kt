package es.unex.natureconnect.data.models

/**
 * Raw response returned by the bird recognition model.
 *
 * @property objetos Birds detected in the analysed image.
 */
data class modeloResponse(
    val objetos: List<AveDetectada>
)
