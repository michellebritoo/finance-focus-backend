package br.com.michellebrito.financeFocusBackend.rates.model

import com.google.gson.annotations.SerializedName

data class RateResponseModel(
    @SerializedName("data")
    val date: String,
    @SerializedName("valor")
    val value: Double
)
