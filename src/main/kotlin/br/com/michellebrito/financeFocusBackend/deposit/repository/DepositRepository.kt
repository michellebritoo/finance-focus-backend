package br.com.michellebrito.financeFocusBackend.deposit.repository

import br.com.michellebrito.financeFocusBackend.deposit.model.DepositModel
import br.com.michellebrito.financeFocusBackend.deposit.model.ExpectedDeposit
import com.google.cloud.firestore.Firestore
import com.google.firebase.cloud.FirestoreClient
import org.springframework.stereotype.Repository

@Repository
class DepositRepository {
    private val firestore: Firestore = FirestoreClient.getFirestore()
    fun createDeposit(model: DepositModel) {
        val collectionsFuture = firestore.collection(DEPOSIT_COLLECTION)
        collectionsFuture.document(model.id).set(model)
    }

    fun createExpectedDeposit(model: ExpectedDeposit) {
        val collectionsFuture = firestore.collection(EXPECTED_DEPOSITS)
        collectionsFuture.document(model.id).set(model)
    }

    private companion object {
        const val DEPOSIT_COLLECTION = "deposits"
        const val EXPECTED_DEPOSITS = "expectedDeposits"
    }
}
