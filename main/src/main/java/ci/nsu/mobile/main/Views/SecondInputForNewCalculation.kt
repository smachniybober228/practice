@file:OptIn(ExperimentalMaterial3Api::class)

package ci.nsu.mobile.main.Views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ci.nsu.mobile.main.Components.AnimatedButton
import ci.nsu.mobile.main.ViewModels.NewCalculationViewModel

@Composable
fun SecondInputForNewCalculation(
    viewModel: NewCalculationViewModel,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var expanded by remember { mutableStateOf(false) }
    var currencyExpanded by remember { mutableStateOf(false) }

    val allRates = listOf(15.0, 10.0, 5.0)
    val currencies = listOf("Рубли (RUB)", "Доллары (USD)", "Евро (EUR)")

    fun parseTerm(): Int? = viewModel.termMonths.value.toIntOrNull()
    val termInput = viewModel.termMonths.value

    LaunchedEffect(termInput) {
        val term = parseTerm()
        errorMessage = when {
            termInput.isBlank() -> "Укажите срок (в месяцах)"
            term == null -> "Введите корректное число"
            else -> null
        }
        if (errorMessage == null && term != null) {
            val recommendedRate = when {
                term < 6 -> 15.0
                term in 6..11 -> 10.0
                term >= 12 -> 5.0
                else -> null
            }
            if (viewModel.rate.value != recommendedRate) {
                viewModel.updateRate(recommendedRate)
            }
        } else {
            viewModel.updateRate(null)
        }
    }

    fun getTermForRate(rate: Double): Int = when (rate) {
        15.0 -> 5
        10.0 -> 10
        5.0 -> 12
        else -> 0
    }

    fun getConditionForRate(rate: Double): String = when (rate) {
        15.0 -> "для срока < 6 месяцев"
        10.0 -> "для срока от 6 до 11 месяцев"
        5.0 -> "для срока ≥ 12 месяцев"
        else -> ""
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
    ) {
        // Поле срока (только для чтения)
        OutlinedTextField(
            value = viewModel.termMonths.value,
            onValueChange = {},
            readOnly = true,
            label = { Text("Срок (месяцы)") },
            isError = errorMessage != null,
            supportingText = {
                if (errorMessage != null) {
                    Text(errorMessage!!, color = MaterialTheme.colorScheme.error)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        // Выпадающий список ставок
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = viewModel.rate.value?.let { "$it%" } ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Процентная ставка") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                allRates.forEach { rate ->
                    DropdownMenuItem(
                        text = {
                            Column {
                                Text("$rate%", style = MaterialTheme.typography.bodyLarge)
                                Text(
                                    text = getConditionForRate(rate),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        onClick = {
                            viewModel.updateRate(rate)
                            val newTerm = getTermForRate(rate).toString()
                            if (newTerm != viewModel.termMonths.value) {
                                viewModel.updateTermMonths(newTerm)
                            }
                            expanded = false
                        }
                    )
                }
            }
        }

        // Выпадающий список валют
        ExposedDropdownMenuBox(
            expanded = currencyExpanded,
            onExpandedChange = { currencyExpanded = it },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = viewModel.currency.value,
                onValueChange = {},
                readOnly = true,
                label = { Text("Валюта") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = currencyExpanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = currencyExpanded,
                onDismissRequest = { currencyExpanded = false }
            ) {
                currencies.forEach { currency ->
                    DropdownMenuItem(
                        text = { Text(currency) },
                        onClick = {
                            viewModel.updateCurrency(currency)
                            currencyExpanded = false
                        }
                    )
                }
            }
        }

        // Отображение выбранной ставки
        if (viewModel.rate.value != null && errorMessage == null) {
            Text(
                text = "Выбрана ставка: ${viewModel.rate.value}%",
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AnimatedButton(
                onClick = onBack,
                enabled = true,
                text = "Назад",
                modifier = Modifier.weight(1f)
            )
            AnimatedButton(
                onClick = onNext,
                enabled = viewModel.termMonths.value.isNotBlank() && viewModel.rate.value != null,
                text = "Рассчитать",
                modifier = Modifier.weight(1f)
            )
        }
    }
}