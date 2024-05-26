package br.com.michellebrito.financeFocusBackend.goals.service

import br.com.michellebrito.financeFocusBackend.goals.model.Goal
import com.google.api.core.ApiFuture
import com.google.cloud.firestore.DocumentSnapshot
import com.google.cloud.firestore.Firestore
import com.google.firebase.cloud.FirestoreClient
import org.springframework.stereotype.Service
import java.util.concurrent.ExecutionException

@Service
class GoalsService {
    private val firestore: Firestore = FirestoreClient.getFirestore()

    @Throws(ExecutionException::class, InterruptedException::class)
    fun createGoal(goalModel: Goal) {
        val collectionsFuture = firestore.collection(GOALS_COLLECTION)
        collectionsFuture.document(goalModel.name).set(goalModel)
    }

    fun getGoal(id: String): Goal? {
        val documentReference = firestore.collection(GOALS_COLLECTION).document(id)
        val collectionFuture: ApiFuture<DocumentSnapshot> = documentReference.get()
        val document: DocumentSnapshot = collectionFuture.get()

        if (document.exists()) {
            return document.toObject(Goal::class.java)?.apply {
                this.documentId = document.id
            }
        }
        return null
    }

    fun updateGoal(goalModel: Goal) {
        firestore.collection(GOALS_COLLECTION).document(goalModel.name).set(goalModel)
    }

    fun deleteGoal(id: String): String {
        firestore.collection(GOALS_COLLECTION).document(id).delete()
        return "Objetivo $id eletado com sucesso!"
    }

    private companion object {
        const val GOALS_COLLECTION = "goals"
    }
}
