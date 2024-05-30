package br.com.michellebrito.financeFocusBackend.utils.extension

fun String?.ifEmptyOrBlank(callback: (String?) -> Unit): Any? {
    return this?.takeIf { it.isEmpty() && it.isBlank() }.also(callback)
}

fun String?.orFalse(): Boolean {
    return this != null
}
