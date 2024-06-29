package br.com.michellebrito.financeFocusBackend.rates.model

data class RatesStatusModel(
    val amount: Double,
    val rateValue: String,
    val status: String,
    val lastRates: List<RateResponseModel>,
    val totalRateValue: Double,
    val totalValueWithRate: Double
)