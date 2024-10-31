package br.com.michellebrito.financeFocusBackend.utils.extension

import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun String?.ifEmptyOrBlank(callback: (String?) -> Unit): Any? {
    return this?.takeIf { it.isEmpty() && it.isBlank() }.also(callback)
}

fun String?.orFalse(): Boolean {
    return this != null
}

fun String?.isBlankOrEmpty(): Boolean {
    return this == "" || this == " "
}

fun Pair<String, String>.parseDates(): Pair<LocalDate, LocalDate> {
    val format = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    val date1 = LocalDate.parse(this.first, format)
    val date2 = LocalDate.parse(this.second, format)
    return Pair(date1, date2)
}
