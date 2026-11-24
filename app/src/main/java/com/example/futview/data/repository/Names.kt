package com.example.futview.data.repository

import java.text.Normalizer
import java.util.Locale

/**
 * Normaliza nombres de equipos y estadios para poder emparejar los nombres de ESPN
 * (en inglés, sin tildes o con sufijos como "CD") con los documentos ya sembrados
 * en Firestore, sin duplicar equipos.
 *
 * Ejemplos:
 * - "Colo Colo" == "Colo-Colo"
 * - "Everton CD" == "Everton"
 * - "La Serena" == "Deportes La Serena"
 * - "Estadio Nacional Julio Martínez Prádanos" == "Nacional Julio Martínez Prádanos"
 */
internal fun normalizeEntityName(raw: String): String {
    val withoutAccents = Normalizer.normalize(raw, Normalizer.Form.NFD)
        .replace(Regex("\\p{M}+"), "")
        .lowercase(Locale.ROOT)
    return withoutAccents
        .split(Regex("[^a-z0-9]+"))
        .filter { it.isNotBlank() }
        .filterNot { it in IGNORED_NAME_TOKENS }
        .joinToString("")
}

private val IGNORED_NAME_TOKENS = setOf(
    "cd",        // Everton CD
    "club",
    "deportes",  // Deportes La Serena / Deportes Concepción
    "de",
    "estadio",   // Estadio Nacional / Estadio Sausalito
    "fc",
    "ca"
)
