package br.com.michellebrito.financeFocusBackend.rates.repository

import br.com.michellebrito.financeFocusBackend.rates.model.RateResponseModel
import com.google.cloud.firestore.Firestore
import com.google.firebase.cloud.FirestoreClient
import org.springframework.stereotype.Repository
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForObject
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.springframework.http.client.SimpleClientHttpRequestFactory

@Repository
class RatesRepository {
    private val firestore: Firestore = FirestoreClient.getFirestore()
    private val gson = Gson()
    private val restTemplate = RestTemplate().apply {
        requestFactory = SimpleClientHttpRequestFactory().apply {
            setConnectTimeout(3000)
            setReadTimeout(3000)
        }
    }

    fun getLastRateById(id: String): List<RateResponseModel> {
        return try {
            val ratesDB: String? = restTemplate.getForObject(
                "https://api.bcb.gov.br/dados/serie/bcdata.sgs.${id}/dados?formato=json"
            )

            val listType = object : TypeToken<List<RateResponseModel>>() {}.type
            val rateResponseModelList = ratesDB?.let { gson.fromJson<List<RateResponseModel>>(it, listType) }

            rateResponseModelList?.let { saveDataContingency(id, it) }

            if (rateResponseModelList.isNullOrEmpty()) {
                getLastMonthRateFromContingency(id).takeLast(LAST_THREE_REGISTERS)
            } else {
                rateResponseModelList.takeLast(LAST_THREE_REGISTERS)
            }
        } catch (e: Exception) {
            getLastMonthRateFromContingency(id).takeLast(LAST_THREE_REGISTERS)
        }
    }

    private fun getLastMonthRateFromContingency(id: String): List<RateResponseModel> {
        val document = firestore.collection(CONTINGENCY).document(id).get().get()
        val data = document.get("data") as List<Map<String, Any>>
        return data.map { map ->
            RateResponseModel(
                date = map["date"] as? String ?: "",
                value = (map["value"] as? Double)?.toDouble() ?: 0.0
            )
        }
    }

    private fun saveDataContingency(id: String, response: List<RateResponseModel>) {
        val ref = firestore.collection(CONTINGENCY).document(id)
        ref.set(mapOf("data" to response))
    }

    private companion object {
        const val LAST_THREE_REGISTERS = 3
        const val CONTINGENCY = "contingency"
    }
}