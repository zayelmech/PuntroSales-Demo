package com.imecatro.demosales.domain.core.model

enum class Languages(val tag: String, val displayName: String) {
    English("en", "English"),
    Spanish("es", "Spanish");

    companion object {
        fun fromTag(tag: String): Languages = entries.find { it.tag == tag } ?: English
        fun fromDisplayName(name: String): Languages = entries.find { it.displayName == name } ?: English
    }
}
