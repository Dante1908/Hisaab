package com.example.hisaab.Data

data class TransactionData(
    val Expense:Boolean = false,
    val amount:Double = 0.0,
    val title:String = "",
    val category:String = "",
    val date:String = "",
    val Note:String = "",
    val id:String = ""
)