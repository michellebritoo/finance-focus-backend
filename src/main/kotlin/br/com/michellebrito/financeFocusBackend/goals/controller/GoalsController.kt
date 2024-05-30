package br.com.michellebrito.financeFocusBackend.goals.controller

import br.com.michellebrito.financeFocusBackend.goals.model.CreateGoalRequest
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
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class GoalsController {
    @Autowired
    lateinit var service: GoalsService

    @PostMapping(GOAL + CREATE)
    fun createGoal(@Valid @RequestBody goalModel: CreateGoalRequest) {
        service.createGoal(goalModel)
    }

    @GetMapping(GOAL)
    fun getGoals(@NotBlank @RequestParam id: String): ResponseEntity<String> {
        return ResponseEntity.ok(service.getGoal(id).toString())
    }

    @PostMapping(GOAL + UPDATE)
    fun updateGoal(@Valid @RequestBody updateGoalRequestModel: UpdateGoalRequest) {
        service.updateGoal(updateGoalRequestModel)
    }

    @DeleteMapping(GOAL + DELETE)
    fun deleteGoal(@NotBlank @RequestParam id: String) {
        service.deleteGoal(id)
    }

    private companion object Routes {
        const val GOAL = "/goal"
        const val UPDATE = "/update"
        const val CREATE = "/create"
        const val DELETE = "/delete"
    }
}
