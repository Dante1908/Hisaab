package com.example.hisaab


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.hisaab.Screens.ForgotPassword
import com.example.hisaab.Screens.LoginScreen
import com.example.hisaab.Screens.Main.AccountScreen
import com.example.hisaab.Screens.Main.HomeScreen
import com.example.hisaab.Screens.Main.TransactionScreen
import com.example.hisaab.Screens.SignUpScreen
import com.example.hisaab.ViewModel.AuthState
import com.example.hisaab.ViewModel.AuthViewModel
import com.example.hisaab.ViewModel.TransactionViewModel
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
                    MyAppNavigation(
                        modifier = Modifier.padding(innerPadding),
                        authViewModel = authViewModel
                    )
                }
            }
        }
    }
}

@Composable
fun MyAppNavigation(modifier: Modifier = Modifier, authViewModel: AuthViewModel) {
    val navController = rememberNavController()
    val authstate = authViewModel.observeAuthState().observeAsState()
    if (authstate.value == AuthState.Unauthenticated) {
        NavHost(navController = navController,
            startDestination = "Login",) {
            composable("Login") { LoginScreen(modifier, navController, authViewModel) }
            composable("ForgotPass") { ForgotPassword(modifier, navController, authViewModel) }
            composable("SignUp") { SignUpScreen(modifier, navController, authViewModel) }
        }
    } else if (authstate.value == AuthState.Authenticated) {
        val transactionViewModel = TransactionViewModel()
        Scaffold(bottomBar = { BottomNavigationBar(navController = navController) }
        ) { paddingValues ->
            NavHost(navController = navController, startDestination = "Home", modifier = Modifier.padding(paddingValues)) {
                composable("Home") { HomeScreen(navController = navController, authViewModel = authViewModel, modifier = Modifier, viewModel = transactionViewModel) }
                composable("Transaction") { TransactionScreen(navController = navController, authViewModel = authViewModel, modifier = Modifier) }
                composable("Account") { AccountScreen(navController = navController, authViewModel = authViewModel, modifier = Modifier, viewModel = transactionViewModel) }
            }
        }
    }
}


@Composable
fun BottomNavigationBar(navController: NavController) {
    val selected = rememberSaveable { mutableStateOf("Home") }
    BottomAppBar {
        IconButton(
            onClick = {
                if (selected.value != "Home") {
                    selected.value = "Home"
                    navController.navigate("Home") {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            },
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                Icons.Default.Home,
                contentDescription = null,
                modifier = Modifier.size(26.dp),
                tint = if (selected.value == "Home") Color.White else Color.Gray
            )
        }
        Box(modifier = Modifier.weight(1f).padding(16.dp), contentAlignment = Alignment.Center) {
            FloatingActionButton(onClick = {
                if (selected.value != "Transaction") {
                    selected.value = "Transaction"
                    navController.navigate("Transaction") {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }) { Icon(Icons.Default.Add, contentDescription = null, tint = Color.White) }
        }
        IconButton(
            onClick = {
                if (selected.value != "Account") {
                    selected.value = "Account"
                    navController.navigate("Account") {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            },
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                Icons.Default.AccountBox,
                contentDescription = null,
                modifier = Modifier.size(26.dp),
                tint = if (selected.value == "Account") Color.White else Color.Gray
            )
        }
    }
}
