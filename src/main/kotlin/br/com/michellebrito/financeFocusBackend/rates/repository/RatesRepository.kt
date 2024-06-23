package br.com.michellebrito.financeFocusBackend.rates.repository

import br.com.michellebrito.financeFocusBackend.rates.model.CodeRatesMonth
import br.com.michellebrito.financeFocusBackend.rates.model.RateResponseModel
import org.springframework.stereotype.Repository
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForObject

@Repository
class RatesRepository {
    private val restTemplate = RestTemplate()

    fun getLastMonthRate(code: CodeRatesMonth): List<RateResponseModel>? {
        val ratesDB: List<RateResponseModel>? = restTemplate.getForObject("https://api.bcb.gov.br/dados/serie/bcdata.sgs.${code.id}/dados?formato=json")
        return ratesDB?.takeLast(LAST_THREE_REGISTERS)
    }
    
    private companion object {
        const val LAST_THREE_REGISTERS = 3
    }
}
