package br.com.michellebrito.financeFocusBackend.deposit.repository

import br.com.michellebrito.financeFocusBackend.deposit.model.DepositModel
import br.com.michellebrito.financeFocusBackend.deposit.model.ExpectedDeposit
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
            depositsRef.document(deposit.id).set(deposit)
        }
    }

    fun getDeposit(goalId: String): List<ExpectedDeposit> {
        val depositsRef = firestore.collection(GOALS_COLLECTION).document(goalId).collection(DEPOSIT_COLLECTION)
        val depositList = mutableListOf<ExpectedDeposit>()

        val documents = depositsRef.get().get().documents
        for (document in documents) {
            val json = Gson().toJson(document.data)
            val deposit = Gson().fromJson(json, ExpectedDeposit::class.java)
            depositList.add(deposit)
        }
        return depositList
    }

    fun updateDepositsUnderGoal(goalId: String, depositList: List<ExpectedDeposit>) {
        //todo: igual ao save
//        val depositsRef = firestore.collection(GOALS_COLLECTION).document(goalId).collection(DEPOSIT_COLLECTION)
//
//        depositList.forEach { deposit ->
//            depositsRef.document(deposit.id).set(deposit)
//        }
    }

    fun updateExpectedDeposit(goalId: String, deposit: ExpectedDeposit) {
        val depositsRef = firestore.collection(GOALS_COLLECTION).document(goalId).collection(DEPOSIT_COLLECTION)
        depositsRef.document(deposit.id).set(deposit)
    }

    fun updateDeposit(deposit: DepositModel) {
        val documentReference = firestore.collection(DEPOSIT_COLLECTION).document(deposit.id)
        documentReference.set(deposit)
    }

    private companion object {
        const val GOALS_COLLECTION = "goals"
        const val DEPOSIT_COLLECTION = "deposits"
    }
}
