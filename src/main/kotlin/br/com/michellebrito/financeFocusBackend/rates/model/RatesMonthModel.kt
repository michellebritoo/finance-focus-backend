package br.com.michellebrito.financeFocusBackend.rates.model

data class RatesMonthModel(
    val amount: Double,
    val rateValue: Double,
    val time: Int,
    val index: Int
)