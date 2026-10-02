package es.unex.natureconnect.data.models

/**
 * A bird species available in the NatureConnect catalogue.
 *
 * Groups the descriptive fields returned by the API together with the selection
 * state used by the bird-picking screens.
 *
 * @property id Unique identifier of the bird species.
 * @property nombreComun Common (Spanish) name of the species.
 * @property nombreCientifico Scientific name of the species.
 * @property idFamilizaAve Family the species belongs to.
 * @property selecionada Whether the species has been selected by the current user.
 */
data class Ave(
    val id: Int,
    val nombreComun: String,
    val nombreCientifico: String,
    //val proteccion: String,
    val idFamilizaAve: familia,
    var selecionada: Boolean
)
