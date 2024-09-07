package com.example.hisaab.Screens.Main

import androidx.compose.foundation.Image
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
import com.google.firebase.auth.FirebaseAuth
import com.maxkeppeker.sheets.core.models.base.rememberSheetState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import java.time.LocalDate
import kotlin.math.expm1
import kotlin.math.max

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
    val incomeCateories = Categories.incomeCategory()
    val expenseCategories = Categories.expenseCategory()
    val incomeTransactionWithDates = transactions?.filter { incomeCateories.contains(it.category) }?.map{transaction->
        Triple(transaction.date,transaction.amount,transaction.category)
    }?: emptyList()
    val expenseTransactionWithDates = transactions?.filter { expenseCategories.contains(it.category) }?.map{transaction->
        Triple(transaction.date,transaction.amount,transaction.category)
    }?: emptyList()
    val totalIncome = transactions?.filter { incomeCateories.contains(it.category)}?.sumOf { it.amount } ?: 0.0
    val totalExpense = transactions?.filter { expenseCategories.contains(it.category) }?.sumOf { it.amount } ?: 0.0
    val netAmount = totalIncome - totalExpense
    val formattedDate = remember {
        derivedStateOf {
            if (selectedStartDate.value == null && selectedEndDate.value == null) "This Month"
            else "${selectedStartDate.value} - ${selectedEndDate.value}"
        }
    }
    var showBarChart by remember { mutableStateOf(false) }
    CalendarDialog(state = calendarState, config = CalendarConfig(monthSelection = true, yearSelection = true), selection = CalendarSelection.Period { start, end ->
            selectedStartDate.value = start
            selectedEndDate.value = end
        }
    )
    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)) {
            val shape = RoundedCornerShape(2.dp)
            Image(painter = painterResource(id = R.drawable.account_back), contentDescription = "Header background", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.FillWidth, alignment = Alignment.TopCenter)
            Row(){
                Text(text = " ${currentUserEmail}\n ${currentUser}", modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth(0.85f), fontSize = 20.sp)
                IconButton(onClick = {
                    authViewModel.signout()
                    navController.navigate("login")
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
            .background(Color.LightGray, shape = RoundedCornerShape(15.dp))) {
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
            .clip(shape = RoundedCornerShape(2.dp))
            .padding(15.dp)
            .background(Color.LightGray, shape = RoundedCornerShape(15.dp))) {
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
                        //TODO:Show Pie Chart
                        Text(text = "Ratio")
                    }
                }
                if(showBarChart){
                    LineChartElement(incomeTransactionWithDates)
                }
                else if(!showBarChart)
                {
                    PieChartElement(totalIncome.toFloat(),totalExpense.toFloat())
                }
            }
        }
    }
}

@Composable
fun LineChartElement(incomeTransactionWithDates: List<Triple<String, Double, String>>){
    val steps = 4
    val pointsData: List<Point> = incomeTransactionWithDates.mapIndexed{index, (transaction)->
        Point(x = index.toFloat(), y = transaction.toFloat())
    }
    val xAxisData = AxisData.Builder().axisStepSize(100.dp).backgroundColor(Color.LightGray).steps(pointsData.size - 1).labelData { i -> if(i in incomeTransactionWithDates.indices) incomeTransactionWithDates[i].first else i.toString() }.labelAndAxisLinePadding(15.dp).build()
    val yAxisData = AxisData.Builder().steps(steps).backgroundColor(Color.LightGray).labelAndAxisLinePadding(20.dp).labelData { i ->
        val maxAmount = incomeTransactionWithDates.maxOfOrNull { it.second }?:100
        val yScale = maxAmount.toFloat() / steps
            (i * yScale).toString()
        }.build()
    val lineChartData1 = LineChartData(linePlotData = LinePlotData(
        lines = listOf(Line(dataPoints = pointsData, LineStyle(), IntersectionPoint(), SelectionHighlightPoint(), ShadowUnderLine(), SelectionHighlightPopUp())),),
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        backgroundColor = Color.LightGray
    )
    LineChart(
        modifier = Modifier
            .fillMaxSize(),
        lineChartData = lineChartData1
    )

}

@Composable
fun PieChartElement(incomeTotal :Float, expenseTotal :Float){
    val slices = listOf(PieChartData.Slice("Income", incomeTotal, Color(0xFF66CC66)), PieChartData.Slice("Expense", expenseTotal, Color(0xFFCC6666)))
    val pieChartData = PieChartData(slices = slices, plotType = PlotType.Pie)
    val pieChartConfig = PieChartConfig(isAnimationEnable = true, showSliceLabels = true, animationDuration = 500,labelType = PieChartConfig.LabelType.VALUE)
    PieChart(modifier = Modifier.fillMaxSize(), pieChartData,pieChartConfig)
}

@Preview
@Composable
fun AccountScreenPreview(){
    AccountScreen(navController = NavController(LocalContext.current), authViewModel = AuthViewModel(), viewModel = TransactionViewModel())
}