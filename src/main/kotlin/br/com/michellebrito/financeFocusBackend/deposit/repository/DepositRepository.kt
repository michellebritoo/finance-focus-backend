package br.com.michellebrito.financeFocusBackend.deposit.repository

import br.com.michellebrito.financeFocusBackend.deposit.model.Deposit
import br.com.michellebrito.financeFocusBackend.deposit.model.DepositModel
import br.com.michellebrito.financeFocusBackend.deposit.model.ExpectedDeposit
import com.google.api.core.ApiFuture
import com.google.cloud.firestore.DocumentSnapshot
import com.google.cloud.firestore.Firestore
import com.google.firebase.cloud.FirestoreClient
import com.google.gson.Gson
import org.springframework.stereotype.Repository

@Repository
class DepositRepository {
    private val firestore: Firestore = FirestoreClient.getFirestore()

    fun saveDepositsUnderGoal(goalId: String, depositList: MutableList<ExpectedDeposit>) {
        val goalRef = firestore.collection(GOALS_COLLECTION).document(goalId)
        val depositsRef = goalRef.collection(DEPOSIT_COLLECTION)

        depositList.forEach { deposit ->
            depositsRef.add(deposit)
        }
    }

    fun createDeposit(model: DepositModel) {
        val collectionsFuture = firestore.collection(DEPOSIT_COLLECTION)
        collectionsFuture.document(model.id).set(model)
    }

    fun createExpectedDeposit(model: ExpectedDeposit) {
        val collectionsFuture = firestore.collection(EXPECTED_DEPOSITS)
        collectionsFuture.document(model.id).set(model)
    }

    fun getDeposit(id: String): String? {
        val documentReference = firestore.collection(DEPOSIT_COLLECTION).document(id)
        val collectionFuture: ApiFuture<DocumentSnapshot> = documentReference.get()
        val document: DocumentSnapshot = collectionFuture.get()

        if (document.exists()) {
            return Gson().toJson(document.data)
        }
        return null
    }

    fun deleteDeposit(id: String) {
        firestore.collection(DEPOSIT_COLLECTION).document(id).delete()
    }

    fun deleteExpectedDepositByDepositId(id: String) {
        val list = firestore.collection(EXPECTED_DEPOSITS).whereEqualTo("depositId", id).get().get()
        list.documents.forEach {
            firestore.collection(EXPECTED_DEPOSITS).document(it.id).delete()
        }
    }

    fun updateExpectedDeposit(deposit: ExpectedDeposit) {
        val documentReference = firestore.collection(EXPECTED_DEPOSITS).document(deposit.id)
        documentReference.set(deposit)
    }

    fun updateDeposit(deposit: DepositModel) {
        val documentReference = firestore.collection(DEPOSIT_COLLECTION).document(deposit.id)
        documentReference.set(deposit)
    }

    private companion object {
        const val GOALS_COLLECTION = "goals"
        const val DEPOSIT_COLLECTION = "deposits"
        const val EXPECTED_DEPOSITS = "expectedDeposits"
    }
}
