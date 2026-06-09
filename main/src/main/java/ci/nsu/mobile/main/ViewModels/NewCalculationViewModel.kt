package ci.nsu.mobile.main.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import ci.nsu.mobile.main.Auth.UserPreferences
import ci.nsu.mobile.main.Data.Entity.CalculationEntity
import ci.nsu.mobile.main.Data.Repository.CalculationRepository
import ci.nsu.mobile.main.Views.CalculationStep
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NewCalculationViewModel(
    private val repository: CalculationRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {
    private val _currentStep = MutableStateFlow(CalculationStep.INITIAL)
    val currentStep: StateFlow<CalculationStep> = _currentStep.asStateFlow()

    // Данные первого экрана
    private val _startAmount = MutableStateFlow("")
    val startAmount: StateFlow<String> = _startAmount.asStateFlow()
    private val _termMonths = MutableStateFlow("")
    val termMonths: StateFlow<String> = _termMonths.asStateFlow()

    // Данные второго экрана
    private val _rate = MutableStateFlow<Double?>(null)
    val rate: StateFlow<Double?> = _rate.asStateFlow()
    private val _currency = MutableStateFlow("Рубли (RUB)")
    val currency: StateFlow<String> = _currency.asStateFlow()

    // Результаты
    private val _interest = MutableStateFlow(0.0)
    val interest: StateFlow<Double> = _interest.asStateFlow()
    private val _total = MutableStateFlow(0.0)
    val total: StateFlow<Double> = _total.asStateFlow()

    fun updateStartAmount(value: String) {
        if (value.isEmpty() || value.toDoubleOrNull() != null)
            _startAmount.value = value
    }
    fun updateTermMonths(value: String) {
        if (value.isEmpty() || value.all { it.isDigit() })
            _termMonths.value = value
    }
    fun updateRate(value: Double?) { _rate.value = value }
    fun updateCurrency(value: String) { _currency.value = value }

    fun goToSecondStep() {
        if (_startAmount.value.isNotBlank() && _termMonths.value.isNotBlank())
            _currentStep.value = CalculationStep.SECOND
    }
    fun goToResultStep() {
        val start = _startAmount.value.toDoubleOrNull() ?: 0.0
        val term = _termMonths.value.toIntOrNull() ?: 0
        val rate = _rate.value ?: 0.0
        val interestAmount = start * (rate / 100.0) * (term / 12.0)
        _interest.value = interestAmount
        _total.value = start + interestAmount
        _currentStep.value = CalculationStep.RESULT
    }
    fun goToInitialStep() { _currentStep.value = CalculationStep.INITIAL }
    fun resetToInitial() {
        _startAmount.value = ""
        _termMonths.value = ""
        _rate.value = null
        _currency.value = "Рубли (RUB)"
        _currentStep.value = CalculationStep.INITIAL
    }
    fun saveAndReset() {
        viewModelScope.launch {
            val calculation = CalculationEntity(
                userId = userPreferences.userId,   // добавляем userId из сохранённых данных
                startAmount = _startAmount.value.toDoubleOrNull() ?: 0.0,
                termMonths = _termMonths.value.toIntOrNull() ?: 0,
                rate = _rate.value ?: 0.0,
                currency = _currency.value,
                interest = _interest.value,
                total = _total.value
            )
            repository.insertCalculation(calculation)
            resetToInitial()
        }
    }

    companion object {
        fun provideFactory(
            repository: CalculationRepository,
            userPreferences: UserPreferences
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return NewCalculationViewModel(repository, userPreferences) as T
            }
        }
    }
}