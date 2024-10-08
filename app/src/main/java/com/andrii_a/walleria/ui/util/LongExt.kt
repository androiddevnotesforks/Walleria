package com.andrii_a.walleria.ui.util

import java.util.Locale
import kotlin.math.ln
import kotlin.math.pow

val Long.abbreviatedNumberString: String
    get() {
        if (this < 1000) return "$this"
        val exp = (ln(this.toDouble()) / ln(1000.0)).toInt()
        return String.format(
            Locale.ROOT,
            "%.1f%c",
            this / 1000.0.pow(exp.toDouble()),
            "KMGTPE"[exp - 1]
        )
    }