package br.com.michellebrito.financeFocusBackend.rates.repository

import br.com.michellebrito.financeFocusBackend.rates.model.CodeRatesMonth
import br.com.michellebrito.financeFocusBackend.rates.model.RateResponseModel
import org.springframework.stereotype.Repository
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForObject
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Repository
class RatesRepository {
    private val restTemplate = RestTemplate()
    private val gson = Gson()

    fun getLastMonthRate(code: CodeRatesMonth): List<RateResponseModel> {
        val ratesDB: String? = restTemplate.getForObject(
            "https://api.bcb.gov.br/dados/serie/bcdata.sgs.${code.id}/dados?formato=json"
        )

        val listType = object : TypeToken<List<RateResponseModel>>() {}.type
        val rateResponseModelList = ratesDB?.let { gson.fromJson<List<RateResponseModel>>(it, listType) }

        return rateResponseModelList?.takeLast(LAST_THREE_REGISTERS) ?: emptyList()
    }

    private companion object {
        const val LAST_THREE_REGISTERS = 3
    }
}