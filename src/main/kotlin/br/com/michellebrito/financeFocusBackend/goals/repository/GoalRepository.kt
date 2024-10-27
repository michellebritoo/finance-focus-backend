package br.com.michellebrito.financeFocusBackend.goals.repository

import br.com.michellebrito.financeFocusBackend.goals.model.CreateGoalRequest
import br.com.michellebrito.financeFocusBackend.goals.model.UpdateGoalRequest
import com.google.api.core.ApiFuture
import com.google.cloud.firestore.DocumentSnapshot
import com.google.cloud.firestore.Firestore
import com.google.firebase.cloud.FirestoreClient
import com.google.gson.Gson
import org.springframework.stereotype.Repository
import java.util.concurrent.ExecutionException

@Repository
class GoalRepository {
    private val firestore: Firestore = FirestoreClient.getFirestore()

    @Throws(ExecutionException::class, InterruptedException::class)
    fun createGoal(goalModel: CreateGoalRequest) {
        val collectionsFuture = firestore.collection(GOALS_COLLECTION)
        collectionsFuture.document(goalModel.id).set(goalModel)
    }

    fun getGoal(id: String): String? {
        val documentReference = firestore.collection(GOALS_COLLECTION).document(id)
        val collectionFuture: ApiFuture<DocumentSnapshot> = documentReference.get()
        val document: DocumentSnapshot = collectionFuture.get()

        if (document.exists()) {
            return Gson().toJson(document.data)
        }
        return null
    }

    fun getGoalsByUser(userId: String): List<CreateGoalRequest>? {
        val list = firestore.collection(GOALS_COLLECTION).whereEqualTo("userUID", userId).get().get()
        return if (list.documents.isNotEmpty()) {
            list.documents.map { document ->
                Gson().fromJson(Gson().toJson(document.data), CreateGoalRequest::class.java)
            }
        } else {
            listOf()
        }
    }

    fun updateGoal(goalModel: UpdateGoalRequest) {
        firestore.collection(GOALS_COLLECTION).document(goalModel.id).update(goalModel.toMap())
    }

    fun incrementGoal(id: String, remainingValue: Float, concluded: Boolean) {
        firestore.collection(GOALS_COLLECTION).document(id).update(
            mapOf(
                "remainingValue" to remainingValue,
                "concluded" to concluded
            )
        )
    }

    fun UpdateGoalRequest.toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "name" to name,
            "description" to description,
        ).filterValues { it != null }
    }

    fun deleteGoal(id: String) {
        val documentReference = firestore.collection(GOALS_COLLECTION).document(id)

        documentReference.listCollections().forEach {
            it.get().get().documents.forEach { it.reference.delete().get() }
        }

        documentReference.delete().get()
    }

    private companion object {
        const val GOALS_COLLECTION = "goals"
    }
}
