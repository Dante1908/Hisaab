package com.example.hisaab.ViewModel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.hisaab.Data.TransactionData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


class TransactionViewModel: ViewModel() {
    private val _isLoading = mutableStateOf(true)
    private val _transactions = MutableLiveData<List<TransactionData>>()
    val loading: State<Boolean> = _isLoading
    val transactions: LiveData<List<TransactionData>> = _transactions
    fun getTransactionsFromFirestore(userId: String, onSuccess: (List<TransactionData>) -> Unit, onFailure: (Exception) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(userId).collection("transactions")
            .orderBy("date", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                val transactions = result.map { document ->
                    document.toObject(TransactionData::class.java).copy(id= document.id)
                }
                onSuccess(transactions)
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }
    fun loadTransactions(userId:String){
        _isLoading.value=true

        getTransactionsFromFirestore(userId,onSuccess = {transactions->_transactions.value = transactions; _isLoading.value=false}, onFailure = {exception-> Log.e("TransactionViewModel","Error loading transactions",exception);_isLoading.value=false})
    }
    fun deleteTransaction(transactionId: String, userId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(userId).collection("transactions").document(transactionId).delete().addOnSuccessListener {
            _transactions.value = _transactions.value?.filter { it.id != transactionId }
            onSuccess()
        }.addOnFailureListener {e->
            onFailure(e)
        }
    }
}