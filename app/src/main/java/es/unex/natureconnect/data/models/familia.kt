package es.unex.natureconnect.data.models

/**
 * Taxonomic family of a bird species.
 *
 * @property id Unique identifier of the family.
 * @property nombreFamilia Name of the family.
 */
data class familia(
    val id: Int,
    val nombreFamilia: String,
)
