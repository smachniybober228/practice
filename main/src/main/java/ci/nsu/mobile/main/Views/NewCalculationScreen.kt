package ci.nsu.mobile.main.Views

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ci.nsu.mobile.main.ViewModels.NewCalculationViewModel

enum class CalculationStep { INITIAL, SECOND, RESULT }

@Composable
fun NewCalculationScreen(viewModel: NewCalculationViewModel) {
    val currentStep by viewModel.currentStep.collectAsState()

    when (currentStep) {
        CalculationStep.INITIAL -> {
            MainInputForNewCalculation(
                viewModel = viewModel,
                onNext = { viewModel.goToSecondStep() }
            )
        }
        CalculationStep.SECOND -> {
            SecondInputForNewCalculation(
                viewModel = viewModel,
                onNext = { viewModel.goToResultStep() },
                onBack = { viewModel.goToInitialStep() }
            )
        }
        CalculationStep.RESULT -> {
            ResultForNewCalculation(
                viewModel = viewModel,
                onSave = { viewModel.saveAndReset() },
                onNew = { viewModel.resetToInitial() }
            )
        }
    }
}