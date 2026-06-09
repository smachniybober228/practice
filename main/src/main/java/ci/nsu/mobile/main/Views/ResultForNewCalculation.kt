package ci.nsu.mobile.main.Views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ci.nsu.mobile.main.Components.AnimatedButton
import ci.nsu.mobile.main.ViewModels.NewCalculationViewModel

@Composable
fun ResultForNewCalculation(
    viewModel: NewCalculationViewModel,
    onSave: () -> Unit,
    onNew: () -> Unit
) {
    val startAmountStr by viewModel.startAmount.collectAsState()
    val termMonthsStr by viewModel.termMonths.collectAsState()
    val rateValue by viewModel.rate.collectAsState()
    val interestValue by viewModel.interest.collectAsState()
    val totalValue by viewModel.total.collectAsState()
    val currencyValue by viewModel.currency.collectAsState()

    val startAmount = startAmountStr.toDoubleOrNull() ?: 0.0
    val term = termMonthsStr.toIntOrNull() ?: 0
    val rate = rateValue ?: 0.0
    val interest = interestValue
    val total = totalValue
    val currency = currencyValue

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        ResultCard(
            startAmount = startAmount,
            term = term,
            rate = rate,
            interest = interest,
            total = total,
            currency = currency,
            modifier = Modifier.weight(1f)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AnimatedButton(
                onClick = onSave,
                enabled = true,
                text = "Сохранить",
                modifier = Modifier.weight(1f)
            )
            AnimatedButton(
                onClick = onNew,
                enabled = true,
                text = "Новый расчёт",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ResultCard(
    startAmount: Double,
    term: Int,
    rate: Double,
    interest: Double,
    total: Double,
    currency: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Детали вклада",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            HorizontalDivider()
            InfoRow("Стартовый взнос:", formatMoney(startAmount, getCurrencySymbol(currency)))
            InfoRow("Срок вклада:", "$term месяцев")
            InfoRow("Процентная ставка:", "${String.format("%.2f", rate)}%")
            InfoRow("Начисленные проценты:", formatMoney(interest, getCurrencySymbol(currency)))
            HorizontalDivider()
            InfoRow(
                "Итоговая сумма:",
                formatMoney(total, getCurrencySymbol(currency)),
                textStyle = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String, textStyle: TextStyle = MaterialTheme.typography.bodyLarge) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
        Text(text = value, style = textStyle)
    }
}

private fun getCurrencySymbol(currency: String): String = when (currency) {
    "Рубли (RUB)" -> "₽"
    "Доллары (USD)" -> "$"
    "Евро (EUR)" -> "€"
    else -> ""
}

private fun formatMoney(amount: Double, symbol: String): String = String.format("%.2f %s", amount, symbol)