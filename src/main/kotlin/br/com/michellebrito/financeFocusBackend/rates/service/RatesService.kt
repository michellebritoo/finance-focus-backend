package br.com.michellebrito.financeFocusBackend.rates.service

import br.com.michellebrito.financeFocusBackend.rates.model.CodeRatesMonth
import br.com.michellebrito.financeFocusBackend.rates.model.RateResponseModel
import br.com.michellebrito.financeFocusBackend.rates.model.RatesMonthModel
import br.com.michellebrito.financeFocusBackend.rates.model.RatesStatusModel
import br.com.michellebrito.financeFocusBackend.rates.repository.RatesRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.pow
import kotlin.math.sqrt

@Service
class RatesService {
    @Autowired
    private lateinit var repository: RatesRepository

    fun calculateRatesByMonth(model: RatesMonthModel): RatesStatusModel {
        validateModel(model)
        val rates = repository.getLastMonthRate(CodeRatesMonth.fromIndex(model.factor))

        val calculated = calculateRateMonth(model.amount, model.rateValue / 100, model.time)
        val totalRate = calculated.second - model.amount

        return RatesStatusModel(
            amount = model.amount,
            rateValue = formatRateValueToPercent(model.rateValue),
            status = getStatusByRate(model.rateValue, rates),
            lastRates = rates,
            partValue = calculated.first,
            totalValueWithRate = calculated.second,
            totalRate = formatTwoDecimals(totalRate)
        )
    }

    private fun calculateRateMonth(value: Double, rate: Double, time: Int): Pair<Double, Double> {
        val installment = value * (rate * Math.pow(1 + rate, time.toDouble())) / (Math.pow(1 + rate, time.toDouble()) - 1)
        val totalWithRate = installment * time

        return Pair(formatTwoDecimals(installment), formatTwoDecimals(totalWithRate))
    }

    private fun validateModel(model: RatesMonthModel) = with(model) {
        if (amount <= 0) throw IllegalArgumentException("O valor deve ser maior que zero")
        if (rateValue <= 0) throw IllegalArgumentException("A taxa de juros deve ser maior que zero")
        if (time <= 0) throw IllegalArgumentException("O numero de semanas deve ser maior que zero")
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

    private fun formatTwoDecimals(value: Double): Double {
        return BigDecimal(value).setScale(2, RoundingMode.HALF_UP).toDouble()
    }
}