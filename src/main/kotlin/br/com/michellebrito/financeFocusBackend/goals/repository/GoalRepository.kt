package br.com.michellebrito.financeFocusBackend.goals.repository

import br.com.michellebrito.financeFocusBackend.goals.model.CreateGoalRequest
import br.com.michellebrito.financeFocusBackend.goals.model.UpdateGoalRequest
import com.google.api.core.ApiFuture
import com.google.cloud.firestore.DocumentSnapshot
import com.google.cloud.firestore.Firestore
import com.google.firebase.cloud.FirestoreClient
import org.springframework.stereotype.Repository
import java.util.UUID
import java.util.concurrent.ExecutionException

@Repository
class GoalRepository {
    private val firestore: Firestore = FirestoreClient.getFirestore()

    @Throws(ExecutionException::class, InterruptedException::class)
    fun createGoal(goalModel: CreateGoalRequest) {
        val collectionsFuture = firestore.collection(GOALS_COLLECTION)
        collectionsFuture.document(generateGoalID()).set(goalModel)
    }

    private fun generateGoalID(): String {
        return GOAL_PREXIF + UUID.randomUUID().toString()
    }

    fun getGoal(id: String): UpdateGoalRequest? {
        val documentReference = firestore.collection(GOALS_COLLECTION).document(id)
        val collectionFuture: ApiFuture<DocumentSnapshot> = documentReference.get()
        val document: DocumentSnapshot = collectionFuture.get()

        if (document.exists()) {
            return document.toObject(UpdateGoalRequest::class.java)
        }
        return null
    }

    fun updateGoal(goalModel: UpdateGoalRequest) {
        firestore.collection(GOALS_COLLECTION).document(goalModel.id).update(goalModel.toMap())
    }

    fun UpdateGoalRequest.toMap(): Map<String, Any?> {
        return mapOf(
            "name" to name,
            "description" to description,
            "value" to value,
            "gradualProgress" to gradualProgress,
            "monthFrequency" to monthFrequency,
            "initDate" to initDate,
            "finishDate" to finishDate
        ).filterValues { it != null}
    }

    fun deleteGoal(id: String): String {
        firestore.collection(GOALS_COLLECTION).document(id).delete()
        return "Objetivo $id eletado com sucesso!"
    }

    private companion object {
        const val GOALS_COLLECTION = "goals"
        const val GOAL_PREXIF = "GOAL:"
    }
}
