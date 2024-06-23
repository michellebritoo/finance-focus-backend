package br.com.michellebrito.financeFocusBackend.rates.service

import br.com.michellebrito.financeFocusBackend.rates.model.CodeRatesMonth
import br.com.michellebrito.financeFocusBackend.rates.model.RateResponseModel
import br.com.michellebrito.financeFocusBackend.rates.model.RatesMonthModel
import br.com.michellebrito.financeFocusBackend.rates.repository.RatesRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class RatesService {
    @Autowired
    private lateinit var repository: RatesRepository

    fun calculateRatesByMonth(model: RatesMonthModel): List<RateResponseModel>? {
        val rates = repository.getLastMonthRate(
            CodeRatesMonth.fromIndex(model.index)
        )
        return rates
    }
}