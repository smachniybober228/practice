package ci.nsu.mobile.main.Views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import ci.nsu.mobile.main.Components.AnimatedButton
import ci.nsu.mobile.main.ViewModels.NewCalculationViewModel

@Composable
fun MainInputForNewCalculation(
    viewModel: NewCalculationViewModel,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
    ) {
        OutlinedTextField(
            value = viewModel.startAmount.value,
            onValueChange = viewModel::updateStartAmount,
            label = { Text("Стартовый взнос") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = viewModel.termMonths.value,
            onValueChange = viewModel::updateTermMonths,
            label = { Text("Срок вклада (месяцы)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(32.dp))
        AnimatedButton(
            onClick = onNext,
            enabled = viewModel.startAmount.value.isNotBlank() && viewModel.termMonths.value.isNotBlank(),
            text = "Далее",
            modifier = Modifier.fillMaxWidth()
        )
    }
}