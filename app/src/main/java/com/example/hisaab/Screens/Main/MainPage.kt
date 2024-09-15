package com.example.hisaab.Screens.Main

import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.hisaab.Screens.LoginScreen
import com.example.hisaab.ViewModel.AuthViewModel
import com.example.hisaab.ViewModel.TransactionViewModel

@Composable
fun MainScreen(){
    val authViewModel = AuthViewModel()
    val transactionViewModel = viewModel<TransactionViewModel>()
    //MainScreenNavigation(authViewModel = authViewModel, transactionViewModel = transactionViewModel)
}
/*
@Composable
fun MainScreenNavigation(authViewModel: AuthViewModel, transactionViewModel: TransactionViewModel){
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ){ paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "Home",
            modifier = Modifier.padding(paddingValues)
        ){
            composable("Home"){
                HomeScreen(navController = navController, authViewModel = authViewModel, modifier = Modifier,viewModel = transactionViewModel)
            }
            composable("Transaction"){
                TransactionScreen(navController = navController, authViewModel = authViewModel, modifier = Modifier)
            }
            composable("Account"){
                AccountScreen(navController = navController, authViewModel = authViewModel, modifier = Modifier,viewModel = transactionViewModel)
            }
            composable("Login"){
                LoginScreen(navController = navController, authViewModel = authViewModel, modifier = Modifier)
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController){
    val selected = remember { mutableStateOf("Home") }
    BottomAppBar {
        IconButton(
            onClick = {
                selected.value = "Home"
                navController.navigate("Home") {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
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
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            FloatingActionButton(onClick = {
                navController.navigate("Transaction")
            }) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.Blue)
            }
        }
        IconButton(
            onClick = {
                selected.value = "Account"
                navController.navigate("Account") {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
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
*/