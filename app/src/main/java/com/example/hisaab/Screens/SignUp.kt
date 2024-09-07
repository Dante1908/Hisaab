package com.example.hisaab.Screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.hisaab.Data.UserData
import com.example.hisaab.R
import com.example.hisaab.ViewModel.AuthState
import com.example.hisaab.ViewModel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun SignUpScreen(modifier: Modifier = Modifier,navController: NavController,authViewModel: AuthViewModel){
    var email by remember { mutableStateOf("") }
    var password1 by remember { mutableStateOf("") }
    var password2 by remember { mutableStateOf("") }
    val authState = authViewModel.authState.observeAsState()
    val context = LocalContext.current
    fun addUsertoFirestore(userData: UserData){
        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        currentUser?.let {
            val userRef = db.collection("users").document(it.uid)
            userRef.set(userData).addOnSuccessListener { Log.d("Firestore", "User added to Firestore")}
                .addOnFailureListener { e -> Log.w("Firestore", "Error adding user to Firestore", e) }
        }
    }
    LaunchedEffect(authState.value) {
        when(authState.value){
            is AuthState.Authenticated ->{
                val auth = FirebaseAuth.getInstance()
                val currentUser = auth.currentUser
                currentUser?.let{
                    val userData = UserData(email,currentUser.uid)
                    addUsertoFirestore(userData)
                }
                navController.navigate("Home")}
            is AuthState.Error -> {
                Toast.makeText(context, (authState.value as AuthState.Error).message, Toast.LENGTH_SHORT).show()
            }
            else -> Unit
        }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(10.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally){
        Image(painter = painterResource(id = R.drawable.signup_image), contentDescription = "SignUp Image", modifier = Modifier.size(250.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Sign Up", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        TextField(value = email, onValueChange = {email=it},label = { Text(text = "Email")})
        Spacer(modifier = Modifier.height(16.dp))
        TextField(value = password1, onValueChange = {password1=it},label = { Text(text = "Password")}, visualTransformation = PasswordVisualTransformation())
        Spacer(modifier = Modifier.height(16.dp))
        TextField(value = password2, onValueChange = {password2=it},label = { Text(text = "Confirm Password")}, visualTransformation = PasswordVisualTransformation())
        Spacer(modifier = Modifier.height(16.dp))
        if(password1==password2){ Button(onClick = {authViewModel.signup(email,password1) }){ Text(text = "Sign Up") }
        }else{
            Text(text = "Passwords Don't Match", color = MaterialTheme.colorScheme.error)
        }
        Spacer(modifier = Modifier.height(30.dp))
        TextButton(onClick = { navController.navigate("Login") }) {
            Text(text = "Already have and Account! Login")
        }
    }
}