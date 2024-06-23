package br.com.michellebrito.financeFocusBackend.rates.model

enum class CodeRatesMonth(val id: String) {
    CREDIT("25435"),
    OTHER_CREDIT("25472"),
    VEHICLE_CREDIT("25471"),
    HOUSE_CREDIT("25499");

    companion object {
        fun fromIndex(index: Int): CodeRatesMonth {
            return when (index) {
                1 -> CREDIT
                2 -> OTHER_CREDIT
                3 -> VEHICLE_CREDIT
                else -> HOUSE_CREDIT
            }
        }
    }
}