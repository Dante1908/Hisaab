package com.example.hisaab.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class AuthViewModel:ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState
    init {
        checkAuthState()
    }
    fun observeAuthState():LiveData<AuthState>{
        return _authState
    }
    fun checkAuthState(){
        if(auth.currentUser==null){
            _authState.value = AuthState.Unauthenticated
        }else{
            _authState.value = AuthState.Authenticated
        }
    }
    fun login(email : String, password:String){
        if(email.isEmpty() || password.isEmpty()){
            _authState.value = AuthState.Error("Email and password cannot be empty")
            return
        }
        _authState.value = AuthState.Loading
        auth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener{
                    task-> if(task.isSuccessful){
                _authState.value = AuthState.Authenticated

            }else{
                _authState.value = AuthState.Error(task.exception?.message ?: "Something went Wrong")
            }
            }
    }
    fun signup(email : String, password:String){
        if(email.isEmpty() || password.isEmpty()){
            _authState.value = AuthState.Error("Email and password cannot be empty")
            return
        }
        _authState.value = AuthState.Loading
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener{
                    task-> if(task.isSuccessful){
                            _authState.value = AuthState.Authenticated
                        }else{
                            _authState.value = AuthState.Error(task.exception?.message ?: "Something went Wrong")
                        }
            }
    }
    fun signout(){
        auth.signOut()
        _authState.value = AuthState.Unauthenticated
    }
    fun forgotPassword(email: String){
        if(email.isEmpty()){
            _authState.value = AuthState.Error("Email cannot be empty")
            return
        }else{
            _authState.value = AuthState.Loading
            auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = AuthState.Authenticated
                }
            }
                .addOnFailureListener { e ->
                    _authState.value = AuthState.Error(e.message ?: "Something went wrong")
                }
        }
    }
}

sealed class AuthState{
    object Authenticated:AuthState()
    object Unauthenticated:AuthState()
    object Loading:AuthState()
    data class Error(val message:String):AuthState()
}