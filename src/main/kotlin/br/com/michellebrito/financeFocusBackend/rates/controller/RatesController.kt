package br.com.michellebrito.financeFocusBackend.rates.controller

import br.com.michellebrito.financeFocusBackend.rates.controller.RatesController.Companion.RATES
import br.com.michellebrito.financeFocusBackend.rates.model.RatesRequestModel
import br.com.michellebrito.financeFocusBackend.rates.model.RatesStatusModel
import br.com.michellebrito.financeFocusBackend.rates.service.RatesService
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(RATES)
class RatesController {
    @Autowired
    private lateinit var service: RatesService

    @PostMapping(CALCULATE_MONTH)
    fun calculateRatesByMonth(@Valid @RequestBody model: RatesRequestModel): ResponseEntity<RatesStatusModel> {
        return ResponseEntity.ok(service.calculateRatesByMonth(model))
    }

    @PostMapping(CALCULATE_YEAR)
    fun calculateRatesByYear(@Valid @RequestBody model: RatesRequestModel): ResponseEntity<RatesStatusModel> {
        return ResponseEntity.ok(service.calculateRatesByYear(model))
    }

    companion object {
        const val RATES = "/rates"
        const val CALCULATE_MONTH = "/calculate/month"
        const val CALCULATE_YEAR = "/calculate/year"
    }
}
