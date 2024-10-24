package com.example.hisaab.Screens

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.hisaab.Data.UserData
import com.example.hisaab.R
import com.example.hisaab.ViewModel.AuthState
import com.example.hisaab.ViewModel.AuthViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun LoginScreen(modifier: Modifier = Modifier,navController: NavController, authViewModel: AuthViewModel){
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val authState = authViewModel.authState.observeAsState()
    val context = LocalContext.current
    var user by remember { mutableStateOf(Firebase.auth.currentUser) }
    val token = stringResource(id = R.string.web_client_id)
    val launcher = rememberFirebaseAuthLauncher(onAuthComplete = {result->user = result.user
        navController.navigate("Home")}, onAuthError = {user = null})
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
                user?.let {
                    val user_Data_unknown = UserData(user!!.email!!,user!!.uid)
                    addUsertoFirestore(user_Data_unknown)
                }
                Toast.makeText(context, (authState.value as AuthState.Error).message, Toast.LENGTH_SHORT).show()
            }
            is AuthState.Error->{
                Toast.makeText(context, (authState.value as AuthState.Error).message, Toast.LENGTH_SHORT).show()
            }
            else -> Unit
        }
    }
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(token).requestEmail().build()
    val googleSignInClient = GoogleSignIn.getClient(context,gso)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(painter = painterResource(id = R.drawable.login_image), contentDescription = "Login Image", modifier = Modifier.size(250.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Sign In", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        TextField(value = email, onValueChange = {email=it}, label = {
            Text(text = "Email")
        })
        Spacer(modifier = Modifier.height(16.dp))
        TextField(value = password, onValueChange = { password=it }, label = {
            Text(text = "Password")
        }, visualTransformation = PasswordVisualTransformation())
        TextButton(onClick = { navController.navigate("ForgotPass") }) {
            Text(text = "Forgot Password?", fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {authViewModel.login(email,password)}) {
            Text(text = "Sign In")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Or", fontSize = 14.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { launcher.launch(googleSignInClient.signInIntent) }) {
            Image(painter = painterResource(id = R.drawable.google_logo), contentDescription = "Google Logo")
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "  Sign In with Google")
        }
        Spacer(modifier = Modifier.height(30.dp))
        TextButton(onClick = { navController.navigate("SignUp") }) {
            Text(text = "Don't have an account? Sign Up")
        }
    }
}

@Composable
fun rememberFirebaseAuthLauncher(onAuthComplete:(AuthResult)-> Unit, onAuthError:(ApiException)-> Unit): ManagedActivityResultLauncher<Intent, ActivityResult> {
    val scope = rememberCoroutineScope()
    return rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()){
        result->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            Log.d("GoogleAuth","account $account")
            val credential = GoogleAuthProvider.getCredential(account.idToken!!,null)
            scope.launch {val authResult = Firebase.auth.signInWithCredential(credential).await()
                        onAuthComplete(authResult)
            }
        }catch (e:ApiException){
            Log.d("GoogleAuth",e.toString())
            onAuthError(e)
        }
    }
}