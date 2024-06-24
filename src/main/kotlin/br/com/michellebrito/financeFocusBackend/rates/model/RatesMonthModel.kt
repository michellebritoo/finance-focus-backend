package br.com.michellebrito.financeFocusBackend.rates.model

import org.jetbrains.annotations.NotNull

data class RatesMonthModel(
    @field:NotNull("Informe o valor")
    val amount: Double,
    @field:NotNull("Informe a taxa de juros")
    val rateValue: Double,
    @field:NotNull("Informe o numero de meses")
    val time: Int,
    @field:NotNull("Informe o fator de busca")
    val factor: Int
)