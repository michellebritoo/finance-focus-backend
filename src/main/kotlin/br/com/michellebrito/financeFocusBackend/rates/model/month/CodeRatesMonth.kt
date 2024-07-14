package br.com.michellebrito.financeFocusBackend.rates.model.month

enum class CodeRatesMonth(val id: String) {
    CREDIT("25435"),
    OTHER_ITEMS("25472"),
    VEHICLE_CREDIT("25471"),
    HOUSE_CREDIT("25499");

    companion object {
        fun fromIndex(index: Int): String {
            return when (index) {
                1 -> VEHICLE_CREDIT.id
                2 -> HOUSE_CREDIT.id
                3 -> OTHER_ITEMS.id
                else -> CREDIT.id
            }
        }
    }
}