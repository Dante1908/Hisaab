package com.example.hisaab.Screens.Main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
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
import androidx.compose.ui.input.key.Key.Companion.I
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import co.yml.charts.axis.AxisData
import co.yml.charts.axis.DataCategoryOptions
import co.yml.charts.common.model.PlotType
import co.yml.charts.common.model.Point
import co.yml.charts.common.utils.DataUtils
import co.yml.charts.ui.barchart.BarChart
import co.yml.charts.ui.barchart.models.BarChartData
import co.yml.charts.ui.barchart.models.BarChartType
import co.yml.charts.ui.barchart.models.BarData
import co.yml.charts.ui.combinedchart.CombinedChart
import co.yml.charts.ui.combinedchart.model.CombinedChartData
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.GridLines
import co.yml.charts.ui.linechart.model.IntersectionPoint
import co.yml.charts.ui.linechart.model.Line
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import co.yml.charts.ui.linechart.model.LineStyle
import co.yml.charts.ui.linechart.model.SelectionHighlightPoint
import co.yml.charts.ui.linechart.model.SelectionHighlightPopUp
import co.yml.charts.ui.linechart.model.ShadowUnderLine
import co.yml.charts.ui.piechart.charts.PieChart
import co.yml.charts.ui.piechart.models.PieChartConfig
import co.yml.charts.ui.piechart.models.PieChartData
import com.example.hisaab.Data.Categories
import com.example.hisaab.Data.TransactionData
import com.example.hisaab.R
import com.example.hisaab.ViewModel.AuthViewModel
import com.example.hisaab.ViewModel.TransactionViewModel
import com.example.hisaab.ui.theme.PurpleGrey40
import com.google.firebase.auth.FirebaseAuth
import com.maxkeppeker.sheets.core.models.base.rememberSheetState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import java.time.LocalDate
import kotlin.math.expm1
import kotlin.math.max
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
    val totalIncome = transactions?.filter { incomeCategories.contains(it.category)}?.sumOf { it.amount } ?: 0.0
    val totalExpense = transactions?.filter { expenseCategories.contains(it.category) }?.sumOf { it.amount } ?: 0.0
    val netAmount = totalIncome - totalExpense
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
        }
    )
    Column(modifier = Modifier) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)) {
            val shape = RoundedCornerShape(2.dp)
            Image(painter = painterResource(id = R.drawable.account_back), contentDescription = "Header background", modifier = Modifier.fillMaxSize().clip(shape = shape), contentScale = ContentScale.FillWidth, alignment = Alignment.TopCenter)
            Row(){
                Text(text = " ${currentUserEmail}\n ${currentUser}", modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth(0.85f), fontSize = 20.sp)
                IconButton(onClick = {
                    authViewModel.signout()
                }){
                    Image(painter = painterResource(id = R.drawable.logout_logo),contentDescription = "Logout",modifier = Modifier.size(width = 100.dp, height = 100.dp))
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
        Spacer(modifier = Modifier.size(16.dp))
        Text(text = "All time transactions", modifier = Modifier.padding(15.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Box(modifier = Modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(2.dp))
            .padding(15.dp)
            .background(color = Color(0xFF837a8e), shape = RoundedCornerShape(15.dp))) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = "Net Amount: ${netAmount}", modifier = Modifier.padding(5.dp), fontSize = 18.sp, color = Color.Black)
                Spacer(modifier = Modifier.size(16.dp))
                Row {
                    Text(text = "Expense: ${totalExpense}", modifier = Modifier
                        .padding(5.dp)
                        .fillMaxWidth(0.5f), fontSize = 18.sp, color = Color.Black)
                    Text(text = "Income: ${totalIncome}", modifier = Modifier
                        .padding(5.dp)
                        .fillMaxWidth(), fontSize = 18.sp, color = Color.Black)
                }
            }
        }
        Text(text = "Expense - Income Report", modifier = Modifier.padding(15.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Box(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .clip(shape = RoundedCornerShape(2.dp))
            .padding(12.dp)
            .background(color = Color(0xFF837a8e), shape = RoundedCornerShape(15.dp))) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Button(onClick = { calendarState.show() }, modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)) {
                    Text(text = formattedDate.value)
                }
                Row(modifier = Modifier.padding(10.dp)) {
                    Button(onClick = { showBarChart =true },modifier=Modifier.fillMaxWidth(0.5f)){
                        Text(text = "Amount")
                    }
                    Spacer(modifier = Modifier.size(10.dp))
                    Button(onClick = {showBarChart = false}, Modifier.fillMaxWidth()) {
                        Text(text = "Ratio")
                    }
                }
                Row(modifier = Modifier.padding(10.dp)) {
                    Button(onClick = { showIncome = true },modifier=Modifier.fillMaxWidth(0.5f)){
                        Text(text = "Income")
                    }
                    Spacer(modifier = Modifier.size(10.dp))
                    Button(onClick = {showIncome = false}, Modifier.fillMaxWidth()) {
                        Text(text = "Expense")
                    }
                }
                Box(modifier = Modifier.fillMaxSize().weight(1f).padding(10.dp)) {
                    if (showBarChart) {
                        if (showIncome) {
                            BarChartElement(incomeTransactions)
                        } else {
                            BarChartElement(expenseTransactions)
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

fun generateRandomColor(): Color {
    val baseIntensity = 0.85f
    val colorOptions = listOf(
        Color(red = baseIntensity, green = Random.nextFloat(), blue = Random.nextFloat(), alpha = 1.0f),
        Color(red = Random.nextFloat(), green = baseIntensity, blue = Random.nextFloat(), alpha = 1.0f),
        Color(red = Random.nextFloat(), green = Random.nextFloat(), blue = baseIntensity, alpha = 1.0f)
    )
    return colorOptions.random()
}
@Composable
fun BarChartElement(Transactions: List<Pair<String, Double>>) {
    val BarData = Transactions.mapIndexed { index, transaction -> BarData(point = Point(x = index.toFloat(), y = transaction.second.toFloat()), label = transaction.first,color = generateRandomColor()) }
    val XAxisData = AxisData.Builder().axisStepSize(30.dp).steps(BarData.size - 1).bottomPadding(40.dp).axisLabelAngle(20f).labelData { index -> BarData[index].label }.build()
    val YStepSize = 1000
    val MaxRange = BarData.maxOfOrNull { it.point.y }?.toInt() ?: 1000
    val YAxisData = AxisData.Builder().steps(YStepSize).labelAndAxisLinePadding(20.dp).axisOffset(20.dp).labelData { index -> (index * (MaxRange / YStepSize)).toString() }.build()
    val BarChartData = BarChartData(chartData = BarData, xAxisData = XAxisData, backgroundColor = PurpleGrey40, tapPadding = 12.dp,horizontalExtraSpace= 12.dp)
    BarChart(modifier = Modifier.height(800.dp), barChartData = BarChartData)
}

@Composable
fun PieChartElement(Transactions: List<Pair<String, Double>>){
    val slices = Transactions.mapIndexed { index, transaction ->
        PieChartData.Slice(
            label = transaction.first,
            value = transaction.second.toFloat(),
            color = generateRandomColor()
        )
    }
    //val slices = listOf(PieChartData.Slice("Income", incomeTotal, Color(0xFF66CC66)), PieChartData.Slice("Expense", expenseTotal, Color(0xFFCC6666)))
    val pieChartData = PieChartData(slices = slices, plotType = PlotType.Pie)
    val pieChartConfig = PieChartConfig(isAnimationEnable = true, showSliceLabels = true, animationDuration = 500,labelType = PieChartConfig.LabelType.VALUE)
    PieChart(modifier = Modifier.size(300.dp).background(color = Color(0xFF837a8e)), pieChartData,pieChartConfig)
}