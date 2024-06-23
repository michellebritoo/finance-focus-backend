package br.com.michellebrito.financeFocusBackend.rates.service

import br.com.michellebrito.financeFocusBackend.rates.model.CodeRatesMonth
import br.com.michellebrito.financeFocusBackend.rates.model.RateResponseModel
import br.com.michellebrito.financeFocusBackend.rates.model.RatesMonthModel
import br.com.michellebrito.financeFocusBackend.rates.model.RatesStatusModel
import br.com.michellebrito.financeFocusBackend.rates.repository.RatesRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class RatesService {
    @Autowired
    private lateinit var repository: RatesRepository

    fun calculateRatesByMonth(model: RatesMonthModel): RatesStatusModel {
        val rates: List<RateResponseModel> = repository.getLastMonthRate(
            CodeRatesMonth.fromIndex(model.index)
        )

        val status = getStatusBasedOnInterest(model.rateValue, rates)

        return RatesStatusModel(
            amount = model.amount,
            rateValue = formatRateValueToPercent(model.rateValue),
            status = status,
            lastRates = rates,
        )
    }

    private fun getStatusBasedOnInterest(rateValue: Double, rateResponseModelList: List<RateResponseModel>): String {
        val referenceValue = rateResponseModelList.map { it.valor }.average().div(100.0)
        val tolerance = 0.01
        return when {
            rateValue > referenceValue + tolerance -> "A taxa de juros apresentada está acima da média dos últimos 3 registros do Banco Central do Brasil"
            rateValue < referenceValue - tolerance -> "A taxa de juros apresentada está abaixo da média dos últimos 3 registros do Banco Central do Brasil"
            else -> "A taxa de juros apresentada está na faixa média dos últimos 3 registros do Banco Central do Brasil"
        }
    }

    private fun formatRateValueToPercent(value: Double) = "${value.times(100)} %"
}