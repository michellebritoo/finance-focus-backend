package br.com.michellebrito.financeFocusBackend.goals.controller

import br.com.michellebrito.financeFocusBackend.deposit.model.ExpectedDeposit
import br.com.michellebrito.financeFocusBackend.goals.controller.GoalsController.Routes.GOAL
import br.com.michellebrito.financeFocusBackend.goals.model.CreateGoalRequest
import br.com.michellebrito.financeFocusBackend.goals.model.IncrementGoalRequest
import br.com.michellebrito.financeFocusBackend.goals.model.UpdateGoalRequest
import br.com.michellebrito.financeFocusBackend.goals.service.GoalsService
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(GOAL)
class GoalsController {
    @Autowired
    lateinit var service: GoalsService

    @PostMapping(CREATE)
    fun createGoal(@Valid @RequestBody goalModel: CreateGoalRequest) {
        service.createGoal(goalModel)
    }

    @GetMapping
    fun getGoal(@NotBlank @RequestHeader id: String): ResponseEntity<String> {
        return ResponseEntity.ok(service.getGoal(id))
    }

    @GetMapping(LIST)
    fun getGoalsByUser(): ResponseEntity<String> {
        return ResponseEntity.ok(service.getGoalsByUser())
    }

    @PostMapping(UPDATE)
    fun updateGoal(@Valid @RequestBody updateGoalRequestModel: UpdateGoalRequest) {
        service.updateGoal(updateGoalRequestModel)
    }

    @GetMapping(PRE_INCREMENT)
    fun preIncrement(@Valid @RequestHeader id: String): ResponseEntity<List<ExpectedDeposit>> {
        return ResponseEntity.ok(service.preIncrement(id))
    }

    @PostMapping(INCREMENT)
    fun incrementGoal(@Valid @RequestBody incrementGoalRequest: IncrementGoalRequest) {
        service.incrementGoal(incrementGoalRequest)
    }

    @DeleteMapping(DELETE)
    fun deleteGoal(@NotBlank @RequestHeader id: String) {
        service.deleteGoal(id)
    }

    companion object Routes {
        const val GOAL = "/goal"
        const val LIST = "/list"
        const val UPDATE = "/update"
        const val INCREMENT = "/increment"
        const val PRE_INCREMENT = "/pre/increment"
        const val CREATE = "/create"
        const val DELETE = "/delete"
    }
}
