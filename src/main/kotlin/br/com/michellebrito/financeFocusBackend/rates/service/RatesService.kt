package br.com.michellebrito.financeFocusBackend.rates.service

import br.com.michellebrito.financeFocusBackend.rates.model.CodeRatesMonth
import br.com.michellebrito.financeFocusBackend.rates.model.RateResponseModel
import br.com.michellebrito.financeFocusBackend.rates.model.RatesMonthModel
import br.com.michellebrito.financeFocusBackend.rates.model.RatesStatusModel
import br.com.michellebrito.financeFocusBackend.rates.repository.RatesRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import kotlin.math.pow
import kotlin.math.sqrt

@Service
class RatesService {
    @Autowired
    private lateinit var repository: RatesRepository

    fun calculateRatesByMonth(model: RatesMonthModel): RatesStatusModel {
        val rates: List<RateResponseModel> = repository.getLastMonthRate(
            CodeRatesMonth.fromIndex(model.index)
        )

        return RatesStatusModel(
            amount = model.amount,
            rateValue = formatRateValueToPercent(model.rateValue),
            status = getStatusByRate(model.rateValue, rates),
            lastRates = rates,
        )
    }

    fun getStatusByRate(rateValue: Double, ratesList: List<RateResponseModel>): String {
        val meanValue = ratesList.map { it.value }.average()
        val standardDeviation = sqrt(ratesList.map { (it.value - meanValue).pow(2.0) }.average())
        val generalLimit = 1.5 * meanValue

        return when {
            rateValue > standardDeviation + generalLimit -> "A taxa de juros apresentada está significativamente mais alta que os últimos 3 registros do Banco Central do Brasil"
            rateValue > meanValue + standardDeviation -> "A taxa de juros apresentada está acima da média dos últimos 3 registros do Banco Central do Brasil"
            rateValue < meanValue - standardDeviation -> "A taxa de juros apresentada está abaixo da média dos últimos 3 registros do Banco Central do Brasil"
            else -> "A taxa de juros apresentada está na faixa média dos últimos 3 registros do Banco Central do Brasil"
        }
    }

    private fun formatRateValueToPercent(value: Double) = "$value %"
}