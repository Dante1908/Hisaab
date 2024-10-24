package com.example.hisaab.Screens.Main

import android.widget.Toast
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.hisaab.Data.Categories
import com.example.hisaab.Data.TransactionData
import com.example.hisaab.ViewModel.AuthViewModel
import com.example.hisaab.ViewModel.TransactionViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun HomeScreen(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel,viewModel: TransactionViewModel){
    val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email
    val currentUser = currentUserEmail?.split("@")?.get(0)
    val userId = FirebaseAuth.getInstance().currentUser?.uid.toString()
    val transactions by viewModel.transactions.observeAsState()
    val isLoading by viewModel.loading
    LaunchedEffect(Unit) {
        viewModel.loadTransactions(userId)
    }
    Column(modifier = Modifier.fillMaxSize()){
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)){
            Text(text = " Hi, ${currentUser} !", fontWeight = FontWeight.Bold, fontSize = 25.sp,modifier = Modifier.padding(12.dp))
        }
        Spacer(modifier = Modifier.height(10.dp))
        if(isLoading){
            for(i in 1..10){
                Box(modifier = Modifier
                    .padding(10.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .shimmerEffect()){
                    Row {
                        Column(modifier=Modifier.weight(1f)) {
                            for(i in 1..3){
                                Box(modifier = modifier
                                    .padding(10.dp)
                                    .fillMaxWidth()
                                    .height(20.dp)
                                    .shimmerEffect())
                                Spacer(modifier = Modifier.height(10.dp))
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
        else{
            if(transactions.isNullOrEmpty()){
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()){
                    Text(text = "No Transactions", fontSize = 20.sp)
                }
            }else{
                TransactionList(transactions = transactions!!,navController)
            }
        }

    }
}
@Composable
fun TransactionList(transactions: List<TransactionData>,navController: NavController){
        LazyColumn{
            items(transactions){transaction->
                TransactionItem(transaction = transaction,transactionViewModel = TransactionViewModel(), navController = navController)
            }
        }
}

@Composable
fun TransactionItem(transaction: TransactionData,transactionViewModel: TransactionViewModel,navController: NavController){
    val uid = FirebaseAuth.getInstance().uid
    val incomeCategory = Categories.incomeCategory()
    val cat = transaction.category
    val transactions by transactionViewModel.transactions.observeAsState()
    var showMenu by remember { mutableStateOf(false) }
    val context=LocalContext.current
    Box(modifier = Modifier
        .padding(10.dp, vertical = 8.dp)
        .fillMaxWidth()
        .clip(RoundedCornerShape(12.dp))
        .background(
            color = if (incomeCategory.contains(cat)) Color.Green.copy(alpha = 0.1f) else Color.Red.copy(
                alpha = 0.1f
            )
        ) ){
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween) {
                Text(text = transaction.title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(text = "${transaction.amount} ₹", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Box(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text(text = "Date: ${transaction.date}", fontSize = 14.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Category: ${transaction.category}", fontSize = 14.sp, color = Color.Gray,modifier = Modifier.fillMaxWidth(0.9f))
                }
                IconButton(onClick = { showMenu = !showMenu },modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(4.dp)
                    .size(30.dp)) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Edit and Delete Options")
                }
                DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu=false },modifier = Modifier.align(Alignment.CenterEnd)) {
                    DropdownMenuItem(text = {Text("Edit") }, onClick = {  })
                    DropdownMenuItem(text = {Text("Delete")}, onClick = {
                        transactionViewModel.deleteTransaction(userId = uid.toString(), transactionId = transaction.id, onSuccess = { Toast.makeText(context,"Transaction Deleted !",Toast.LENGTH_SHORT).show()
                            transactionViewModel.loadTransactions(uid.toString())},
                        onFailure = { Toast.makeText(context,"Transaction Deletion Failed !",Toast.LENGTH_SHORT).show() })
                        showMenu = false
                        navController.navigate("Home")
                    })
                }
            }
        }
    }
}


fun Modifier.shimmerEffect(): Modifier = composed{
    var size by remember{
        mutableStateOf(IntSize.Zero)
    }
    val transition  = rememberInfiniteTransition()
    val startOffSetX by transition.animateFloat(
        initialValue = -2* size.width.toFloat(),
        targetValue = 2* size.width.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1000)
        ), label = ""
    )
    background(
        brush = Brush.linearGradient(colors = listOf(Color(0xFFB8B5B5),Color(0xFF8F8B8B),Color(0xFFB8B5B5))
            ,start = Offset(startOffSetX,0f),
            end = Offset(startOffSetX + size.width.toFloat(),size.height.toFloat())
        )
    ).onGloballyPositioned {
        size = it.size
    }
}