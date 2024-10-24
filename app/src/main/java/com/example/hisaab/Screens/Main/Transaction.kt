package com.example.hisaab.Screens.Main

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.hisaab.Data.Categories.expenseCategory
import com.example.hisaab.Data.Categories.incomeCategory
import com.example.hisaab.Data.TransactionData
import com.example.hisaab.ViewModel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.maxkeppeker.sheets.core.models.base.rememberSheetState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreen(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel){
    var isExpense by remember { mutableStateOf(true) }
    var amount by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    val date = remember { mutableStateOf<LocalDate?>(null) }
    var Category by remember { mutableStateOf("") }
    var isExpanded by remember { mutableStateOf(false) }
    val calenderState = rememberSheetState()
    val context = LocalContext.current
    val formattedDate = remember {
        derivedStateOf {
            if (date.value == null) "Select Date"
            else "${date.value}"
        }
    }
    val user = FirebaseAuth.getInstance().currentUser
    fun addTransaction(userId:String,transactionData: TransactionData){
        val db = FirebaseFirestore.getInstance()
        val transactionRef = db.collection("users").document(userId).collection("transactions").document()
        transactionRef.set(transactionData).addOnSuccessListener {
            Log.d("Firestore","Transaction Added")
            Toast.makeText(context,"Transaction Added",Toast.LENGTH_SHORT).show()
        }
            .addOnFailureListener { Log.d("Firestore","Transaction Failed")
                Toast.makeText(context,"Transaction Failed",Toast.LENGTH_SHORT).show()
            }
    }
    CalendarDialog(state = calenderState, config = CalendarConfig(monthSelection = true, yearSelection = true), selection = CalendarSelection.Date { selected->
        date.value = selected
    })
    Column(modifier = Modifier
        .padding(12.dp)
        .fillMaxSize()) {
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = "Add \nTransaction",fontSize = 30.sp)
        Spacer(modifier = Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically, modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp)) {
            Tab(selected = false, onClick = { isExpense=!isExpense }, selectedContentColor = if(isExpense) Color.Red else Color.Gray,
                text = {
                    Text(text = "Expense", textAlign = TextAlign.Center, lineHeight = 1.43.em, style = MaterialTheme.typography.labelLarge, modifier = Modifier.wrapContentHeight(align = Alignment.CenterVertically))
                }, modifier = Modifier
                    .requiredHeight(height = 48.dp)
                    .weight(weight = 0.5f)
                    .padding(vertical = 8.dp, horizontal = 4.dp)
                    .border(width = 1.dp, color = Color.Gray, shape = RoundedCornerShape(8.dp)))
            Tab(selected = false, onClick = { isExpense=!isExpense },selectedContentColor = if(!isExpense) Color.Green else Color.Gray, text = {
                    Text(text = "Income", textAlign = TextAlign.Center, lineHeight = 1.43.em, style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.wrapContentHeight(align = Alignment.CenterVertically))
                }, modifier = Modifier
                .requiredHeight(height = 48.dp)
                .weight(weight = 0.5f)
                .padding(vertical = 8.dp, horizontal = 4.dp)
                .border(width = 1.dp, color = Color.Gray, shape = RoundedCornerShape(8.dp)))
        }
        TextField(value = amount, onValueChange = {amount = it.filter{char ->char .isDigit()|| char == '.'}}, label = {Text("Amount")},keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number), modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp, vertical = 8.dp))
        TextField(value = title, onValueChange = {title = it}, label = {Text("Title")}, modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp, vertical = 8.dp))
        TextField(value = note, onValueChange = {note = it}, label = {Text("Note (Optional)")}, modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp, vertical = 8.dp))
        ExposedDropdownMenuBox(expanded = isExpanded, onExpandedChange = {isExpanded=it},modifier = Modifier
            .padding(horizontal = 15.dp, vertical = 8.dp)
            .fillMaxWidth()) {
            TextField(value = if(Category.isEmpty()) "Select Category" else Category,
                onValueChange = {Category=it},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
                colors = ExposedDropdownMenuDefaults.textFieldColors(),
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            val categories = if (isExpense) expenseCategory() else incomeCategory()
            ExposedDropdownMenu(expanded = isExpanded, onDismissRequest = { isExpanded=false }) {
                categories.forEach {item->
                    DropdownMenuItem(text = { Text(text = item) }, onClick = { Category=item; isExpanded=false })
                }
            }
        }
        Button(onClick = { calenderState.show() },modifier = Modifier.padding(horizontal = 15.dp, vertical = 4.dp).fillMaxWidth(), shape = RoundedCornerShape(8.dp), colors= ButtonDefaults.buttonColors(containerColor = Color.White)) { Text(text = formattedDate.value) }
        Button(onClick = {
            if(amount!="" && title!="" && Category!="" && date.value!=null){addTransaction(user!!.uid,TransactionData(isExpense,amount.toDouble(),title,Category,date.value.toString(),note))
                navController.navigate("Home")
            }
            else {Toast.makeText(context,"Please fill all the fields",Toast.LENGTH_SHORT).show()} },modifier = Modifier
            .padding(horizontal = 45.dp, vertical = 8.dp)
            .fillMaxWidth(), shape = RoundedCornerShape(8.dp)) {
            Text("Submit")
        }
    }
}
