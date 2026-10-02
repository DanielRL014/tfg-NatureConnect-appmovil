package es.unex.natureconnect.data.models

/**
 * Result of running the bird recognition model on a photograph.
 *
 * Contains both the object-detection output (bounding box and confidence) and
 * the classification output (predicted class and confidence) for a single
 * detected bird.
 *
 * @property bbox Pixel coordinates that delimit the detected bird in the image.
 * @property confianza_deteccion Confidence score reported by the detection stage, between 0 and 1.
 * @property clase Class label predicted by the model.
 * @property confianza_clasificacion Confidence score reported by the classification stage, between 0 and 1.
 */
data class AveDetectada(
    val bbox: List<Int>,
    val confianza_deteccion: Float,
    val clase: String,
    val confianza_clasificacion: Float
)
