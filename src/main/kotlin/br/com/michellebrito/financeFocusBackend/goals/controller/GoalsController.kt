package br.com.michellebrito.financeFocusBackend.goals.controller

import br.com.michellebrito.financeFocusBackend.goals.model.Goal
import br.com.michellebrito.financeFocusBackend.goals.service.GoalsService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class GoalsController {
    val service = GoalsService()

    @PostMapping("/goal/create")
    fun createGoal(@RequestBody goalModel: Goal) {
        service.createGoal(goalModel)
    }

    @GetMapping("/goal")
    fun getGoals(@RequestParam id: String): ResponseEntity<String> {
        return ResponseEntity.ok(service.getGoal(id).toString())
    }

    @PostMapping("/goal/update")
    fun updateGoal(@RequestBody goalModel: Goal) {
        service.updateGoal(goalModel)
    }

    @DeleteMapping("/goal/delete")
    fun deleteGoal(@RequestParam id: String): ResponseEntity<String> {
        return ResponseEntity.ok(service.deleteGoal(id))
    }
}