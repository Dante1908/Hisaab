package com.example.hisaab.Screens

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.hisaab.ViewModel.AuthState
import com.example.hisaab.ViewModel.AuthViewModel

@Composable
fun ForgotPassword(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel){
    var email by remember { mutableStateOf("") }
    val context = LocalContext.current
    Column(modifier = Modifier.fillMaxSize().wrapContentSize(align = Alignment.Center).padding(16.dp)) {
        Text(text = "Forgot Password", modifier = Modifier.align(Alignment.CenterHorizontally), fontSize = 18.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "The Information regarding your password rest is sent to your Email !!\n(Please check SPAM if you don't see it.)", textAlign  = androidx.compose.ui.text.style.TextAlign.Center, modifier = Modifier.padding(16.dp))
        TextField(value = email, onValueChange = {email=it}, label = {
            Text(text = "Email")
        }, modifier = Modifier.align(Alignment.CenterHorizontally).fillMaxWidth().padding(16.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { authViewModel.forgotPassword(email) },modifier = Modifier.align(Alignment.CenterHorizontally).padding(16.dp)) {
            Text(text = "Submit")
        }
    }
    if(authViewModel.observeAuthState().value is AuthState.Error || authViewModel.observeAuthState().value is AuthState.Loading || authViewModel.observeAuthState().value is AuthState.Unauthenticated){
        Toast.makeText(context, (authViewModel.observeAuthState().value as AuthState.Error).message, Toast.LENGTH_SHORT).show()
    }else{
        Toast.makeText(context, "Password Reset Email Sent", Toast.LENGTH_SHORT).show()
        navController.navigate("Login")
    }
}