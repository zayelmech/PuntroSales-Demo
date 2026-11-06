package com.imecatro.products.data.paging

import android.icu.lang.UCharacter

/** Android SQLite's ICU LIKE compares simple case-folded code points (not full string folds). */
fun productSearchKey(text: String): String = buildString(text.length) {
    var offset = 0
    while (offset < text.length) {
        val point = Character.codePointAt(text, offset)
        appendCodePoint(UCharacter.foldCase(point, true))
        offset += Character.charCount(point)
    }
}

internal fun prefixUpperBound(prefix: String): String? {
    var end = prefix.length
    while (end > 0) {
        val start = Character.offsetByCodePoints(prefix, end, -1)
        val point = Character.codePointAt(prefix, start)
        if (point < Character.MAX_CODE_POINT) {
            val next = if (point + 1 in 0xD800..0xDFFF) 0xE000 else point + 1
            return prefix.substring(0, start) + String(Character.toChars(next))
        }
        end = start
    }
    return null
}
