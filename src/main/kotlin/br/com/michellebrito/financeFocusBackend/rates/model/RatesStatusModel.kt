package br.com.michellebrito.financeFocusBackend.rates.model

data class RatesStatusModel(
    val amount: String,
    val rateValue: String,
    val status: String,
    val lastRates: List<RateResponseModel>,
    val partValue: String,
    val totalValueWithRate: String,
    val totalRate: String
)