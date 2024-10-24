package com.example.hisaab.Screens.Main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.navigation.NavController
import co.yml.charts.common.components.accessibility.AccessibilityBottomSheetDialog
import co.yml.charts.common.model.AccessibilityConfig
import co.yml.charts.common.model.PlotType
import co.yml.charts.ui.piechart.charts.PieChart
import co.yml.charts.ui.piechart.models.PieChartConfig
import co.yml.charts.ui.piechart.models.PieChartData
import com.example.hisaab.Data.Categories
import com.example.hisaab.R
import com.example.hisaab.ViewModel.AuthViewModel
import com.example.hisaab.ViewModel.TransactionViewModel
import com.google.firebase.auth.FirebaseAuth
import com.maxkeppeker.sheets.core.models.base.rememberSheetState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import java.time.LocalDate
import java.time.YearMonth
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel,viewModel: TransactionViewModel) {
    val currentUserEmail = FirebaseAuth.getInstance().getCurrentUser()?.email
    val currentUser = currentUserEmail?.split("@")?.get(0)
    val calendarState = rememberSheetState()
    val selectedStartDate = remember { mutableStateOf<LocalDate?>(null) }
    val selectedEndDate = remember { mutableStateOf<LocalDate?>(null) }
    val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
    LaunchedEffect(Unit) {
        viewModel.loadTransactions(uid)
    }
    val transactions by viewModel.transactions.observeAsState()
    val incomeCategories = Categories.incomeCategory()
    val expenseCategories = Categories.expenseCategory()
    val totalIncome:Float = (transactions?.filter { incomeCategories.contains(it.category)}?.sumOf { it.amount } ?: 0.0).toFloat()
    val totalExpense = (transactions?.filter { expenseCategories.contains(it.category) }?.sumOf { it.amount } ?: 0.0).toFloat()
    val formattedDate = remember {
        derivedStateOf {
            if (selectedStartDate.value == null && selectedEndDate.value == null) "This Month"
            else "${selectedStartDate.value} - ${selectedEndDate.value}"
        }
    }
    val initialIncomeSums = incomeCategories.associateWith { 0.0 }.toMutableMap()
    transactions?.filter { incomeCategories.contains(it.category) }
        ?.groupBy { it.category }
        ?.forEach { (category, transactions) ->
            initialIncomeSums[category] = transactions.sumOf { it.amount }
        }
    val incomeTransactions = initialIncomeSums.toList()
    val initialExpenseSums = expenseCategories.associateWith { 0.0 }.toMutableMap()
    transactions?.filter { expenseCategories.contains(it.category) }
        ?.groupBy { it.category }
        ?.forEach { (category, transactions) ->
            initialExpenseSums[category] = transactions.sumOf { it.amount }
        }
    val expenseTransactions = initialExpenseSums.toList()
    var showBarChart by remember { mutableStateOf(false) }
    var showIncome by remember { mutableStateOf(false) }
    CalendarDialog(state = calendarState, config = CalendarConfig(monthSelection = true, yearSelection = true), selection = CalendarSelection.Period { start, end ->
            selectedStartDate.value = start
            selectedEndDate.value = end
        })
    Column(modifier = Modifier.height(2000.dp)) {
        Box(modifier = Modifier.fillMaxWidth().height(100.dp)) {
            Row{
                Text(text = " ${currentUserEmail}\n ${currentUser}", modifier = Modifier.padding(10.dp).fillMaxWidth(0.85f), fontSize = 20.sp)
                IconButton(onClick = { authViewModel.signout() }){ Image(painter = painterResource(id = R.drawable.logout_logo),contentDescription = "Logout",modifier = Modifier.size(width = 100.dp, height = 100.dp)) }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "All time transactions", modifier = Modifier.padding(horizontal=15.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Budget(modifier = Modifier.padding(horizontal=15.dp),totalIncome,totalExpense)
        Spacer(modifier = Modifier.height((-4).dp))
        Text(text = "Expense - Income Report", modifier = Modifier.padding(horizontal=15.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Box(modifier = Modifier.clip(shape = RoundedCornerShape(8.dp)).padding(12.dp).background(color = Color.DarkGray, shape = RoundedCornerShape(15.dp))) {
            Column(modifier = Modifier.fillMaxWidth()) {
                val shape3= RoundedCornerShape(8.dp)
                Spacer(modifier = Modifier.size(4.dp))
                Button(onClick = { calendarState.show() },shape = shape3, modifier = Modifier.fillMaxWidth().padding(horizontal=10.dp), colors= ButtonDefaults.buttonColors(containerColor = Color.White)) { Text(text = formattedDate.value) }
                Row(modifier = Modifier.padding(horizontal=10.dp)) {
                    Button(onClick = { showBarChart =true },shape = shape3,modifier=Modifier.fillMaxWidth(0.5f), colors= ButtonDefaults.buttonColors(containerColor = Color.White)){ Text(text = "Amount") }
                    Spacer(modifier = Modifier.size(10.dp))
                    Button(onClick = {showBarChart = false},modifier= Modifier.fillMaxWidth(),shape = shape3, colors= ButtonDefaults.buttonColors(containerColor = Color.White)) { Text(text = "Ratio") }
                }
                Row(modifier = Modifier.padding(horizontal=10.dp)) {
                    Button(onClick = { showIncome = true },modifier=Modifier.fillMaxWidth(0.5f),shape = shape3, colors= ButtonDefaults.buttonColors(containerColor = Color.White)){ Text(text = "Income") }
                    Spacer(modifier = Modifier.size(10.dp))
                    Button(onClick = {showIncome = false}, Modifier.fillMaxWidth(),shape = shape3, colors= ButtonDefaults.buttonColors(containerColor = Color.White)) { Text(text = "Expense") }
                }
                Box(modifier = Modifier.fillMaxSize().weight(1f).padding(10.dp).background(color = Color.DarkGray), contentAlignment = Alignment.Center) {
                    if (showBarChart) {
                        if (showIncome) {
                            BarChart(incomeTransactions)
                        } else {
                            BarChart(expenseTransactions)
                        }
                    } else if (!showBarChart) {
                        if (showIncome) {
                            PieChartElement(incomeTransactions)
                        } else {
                            PieChartElement(expenseTransactions)
                        }
                    }
                }
            }
        }
    }
}
val colors = listOf(Color(0xFF6200EE), Color(0xFF03DAC5), Color(0xFFFF5722), Color(0xFFFFC107), Color(0xFF8BC34A), Color(0xFF2196F3), Color(0xFFE91E63), Color(0xFF9C27B0), Color(0xFFFFEB3B), Color(0xFF00BCD4))
@Composable
fun BarChart(Transactions: List<Pair<String, Double>>) {
    val maxAmount = Transactions.maxOfOrNull { it.second } ?: 1.0
    val maxBarHeight = 200.dp
    val spacing = 8.dp
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(spacing), modifier = Modifier.fillMaxWidth()) {
            Transactions.forEachIndexed { index, (category, amount) ->
                val barColor = colors[index % colors.size]
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Bottom, modifier = Modifier.weight(1f).requiredHeight(300.dp)) {
                    Box(modifier = Modifier.fillMaxWidth(0.5f).height((amount / maxAmount * maxBarHeight).coerceAtLeast(4.dp)).background(barColor))
                    Spacer(modifier = Modifier.height(8.dp)) // Space between bar and text
                    Text(text = category, textAlign = TextAlign.Center, style = MaterialTheme.typography.bodySmall, modifier = Modifier.fillMaxWidth(), color = Color.Black)
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(spacing), modifier = Modifier.fillMaxWidth()) {
            Transactions.forEachIndexed { index, (category, _) ->
                val legendColor = colors[index % colors.size]
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                    Box(modifier = Modifier.size(20.dp).background(legendColor))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = category, style = MaterialTheme.typography.bodySmall, color = Color.Black, textAlign = TextAlign.Center)
                }
            }
        }
    }
}

@Composable
fun PieChartElement(Transactions: List<Pair<String, Double>>){
    val slices = Transactions.mapIndexed { index, transaction ->
        PieChartData.Slice(label = transaction.first, value = transaction.second.toFloat(), color = colors[index % colors.size])
    }
    val pieChartData = PieChartData(slices = slices, plotType = PlotType.Pie)
    val pieChartConfig = PieChartConfig(backgroundColor= Color.DarkGray,isAnimationEnable = true, showSliceLabels = true, animationDuration = 500,labelType = PieChartConfig.LabelType.VALUE)
    PieChart(modifier = Modifier.size(300.dp), pieChartData,pieChartConfig)
}

@Composable
fun Budget(modifier: Modifier = Modifier, totalIncome:Float, totalExpense:Float) {
    Box(modifier = modifier
        .fillMaxWidth()
        .requiredHeight(height = 120.dp),) {
        val rupeeSymbol = "\u20B9"
        val daysInMonth = YearMonth.now().lengthOfMonth()
        val daysleft = daysInMonth - LocalDate.now().dayOfMonth
        val percentage = if (totalIncome > 0) { (totalExpense / totalIncome * 100).coerceIn(0f, 100f) } else { 0f }
        Box(modifier = Modifier.fillMaxWidth().requiredHeight(height = 97.dp).clip(shape = RoundedCornerShape(9.dp)).background(color = Color.White))
        Text(text = "${daysleft} days left", color = Color(0xff707070), textAlign = TextAlign.End, lineHeight = 13.18.em, style = TextStyle(fontSize = 11.sp), modifier = Modifier.align(alignment = Alignment.BottomEnd).padding(end = 16.dp,bottom = 30.dp))
        Text(text = "Remaining balance - $rupeeSymbol ${totalIncome-totalExpense}", color = Color(0xff707070), lineHeight = 13.18.em, style = TextStyle(fontSize = 11.sp), modifier = Modifier.align(alignment = Alignment.BottomStart).padding(start = 16.dp,bottom = 30.dp))
        Box(modifier = Modifier.align(alignment = Alignment.TopStart).offset(x = 12.dp, y = 63.dp).fillMaxWidth().height(4.dp)) {
            Box(modifier = Modifier.align(alignment = Alignment.TopStart).fillMaxWidth(0.93f).height(6.dp).clip(shape = RoundedCornerShape(2.dp)).background(color = Color(0xffe0e0e0)))
            Box( modifier = Modifier.align(alignment = Alignment.TopStart).fillMaxWidth((percentage / 100)*0.93f).height(6.dp).clip(shape = RoundedCornerShape(2.dp)).background(color = Color(0xff8557a0)))
        }
        Text(text = "${percentage.toInt()}%", color = Color(0xff707070), textAlign = TextAlign.End, lineHeight = 13.18.em, style = TextStyle(fontSize = 11.sp), modifier = Modifier.align(alignment = Alignment.TopEnd).offset(x = (-12).dp, y = 39.dp))
        Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.align(alignment = Alignment.TopStart).offset(x = 11.999999475463369.dp, y = 31.dp)) {
            Text(text = "$rupeeSymbol ${totalExpense}", color = Color(0xff2836b5), lineHeight = 9.38.em, style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold))
            Text(text = "/ $rupeeSymbol${totalIncome}", color = Color(0xff838486), lineHeight = 13.64.em, style = TextStyle(fontSize = 11.sp))
        }
        Row(modifier = Modifier.align(alignment = Alignment.TopStart) .padding(start = 12.dp, top = 10.dp).requiredWidth(width = 334.dp)) {
            val currentMonth = LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("MMMM"))
            Text(text = "${currentMonth} expenses", color = Color(0xff242424), lineHeight = 8.12.em, style = TextStyle(fontSize = 16.sp))
        }
    }
}