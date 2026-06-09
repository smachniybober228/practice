@file:OptIn(ExperimentalMaterial3Api::class)

package ci.nsu.mobile.main.Views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ci.nsu.mobile.main.Data.Entity.CalculationEntity
import ci.nsu.mobile.main.MyApp
import ci.nsu.mobile.main.ViewModels.MyCalculationsViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Форматтер даты (один экземпляр для всего файла)
private val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())

@Composable
fun MyCalculationsScreen() {
    val viewModel: MyCalculationsViewModel = viewModel(
        factory = MyCalculationsViewModel.provideFactory(MyApp.serviceLocator.calculationRepository)
    )
    val calculations by viewModel.calculations.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Мои расчёты") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (errorMessage != null) {
                Text(errorMessage!!, modifier = Modifier.align(Alignment.Center))
            } else if (calculations.isEmpty()) {
                Text("Нет сохранённых расчётов", modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize().padding(16.dp)
                ) {
                    items(calculations) { calc ->
                        CalculationHistoryItem(calculation = calc, onDelete = {
                            viewModel.deleteCalculation(calc.id)
                        })
                    }
                }
            }
        }
    }
}

@Composable
fun CalculationHistoryItem(calculation: CalculationEntity, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = dateFormat.format(Date(calculation.timestamp)),
                    style = MaterialTheme.typography.labelMedium
                )
                Text(
                    text = "Сумма: ${String.format("%.2f", calculation.total)} ${getCurrencySymbol(calculation.currency)}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Удалить")
            }
        }
    }
}

private fun getCurrencySymbol(currency: String): String = when (currency) {
    "Рубли (RUB)" -> "₽"
    "Доллары (USD)" -> "$"
    "Евро (EUR)" -> "€"
    else -> ""
}