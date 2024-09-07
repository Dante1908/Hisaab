package com.example.hisaab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.hisaab.Screens.ForgotPassword
import com.example.hisaab.Screens.LoginScreen
import com.example.hisaab.Screens.Main.MainScreen
import com.example.hisaab.Screens.SignUpScreen
import com.example.hisaab.ViewModel.AuthViewModel
import com.example.hisaab.ui.theme.HisaabTheme
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        FirebaseApp.initializeApp(this)
        val authViewModel = AuthViewModel()
        setContent {
            HisaabTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MyAppNavigation(modifier = Modifier.padding(innerPadding),authViewModel = authViewModel)
                }
            }
        }
    }
}
@Composable
fun MyAppNavigation(modifier: Modifier = Modifier,authViewModel: AuthViewModel){
    val navController = rememberNavController()
    val authstate = authViewModel.observeAuthState().observeAsState()
    NavHost(navController= navController, startDestination = "Login", builder = {
        composable("Login"){ LoginScreen(modifier,navController,authViewModel) }
        composable("ForgotPass"){ ForgotPassword(modifier,navController,authViewModel)}
        composable("SignUp"){ SignUpScreen(modifier,navController,authViewModel) }
        composable("Home"){ MainScreen() }
    })
}
