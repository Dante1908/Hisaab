package com.example.hisaab.Data

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class TransactionData(
    val Expense:Boolean = false,
    val amount:Double = 0.0,
    val title:String = "",
    val category:String = "",
    val date:String = "",
    val Note:String = "",
    val id:String = ""
)