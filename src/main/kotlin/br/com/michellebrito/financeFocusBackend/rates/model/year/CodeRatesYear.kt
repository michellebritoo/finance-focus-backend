package br.com.michellebrito.financeFocusBackend.rates.model.year

enum class CodeRatesYear(val id: String) {
    CREDIT("20740"),
    OTHER_ITEMS("20750"),
    VEHICLE_CREDIT("20749"),
    HOUSE_CREDIT("20774");

    companion object {
        fun fromIndex(index: Int): String {
            return when(index) {
                1 -> CREDIT.id
                2 -> OTHER_ITEMS.id
                3 -> VEHICLE_CREDIT.id
                else -> HOUSE_CREDIT.id
            }
        }
    }
}
