package com.example.futview.ui.util

/** Formatea un entero con separador de miles chileno (47.347). */
fun Int.formatThousands(): String =
    toString().reversed().chunked(3).joinToString(".").reversed()
