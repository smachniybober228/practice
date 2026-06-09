package ci.nsu.mobile.main.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import ci.nsu.mobile.main.Data.Entity.CalculationEntity
import ci.nsu.mobile.main.Data.Repository.CalculationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MyCalculationsViewModel(private val repository: CalculationRepository) : ViewModel() {
    private val _calculations = MutableStateFlow<List<CalculationEntity>>(emptyList())
    val calculations: StateFlow<List<CalculationEntity>> = _calculations.asStateFlow()
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadCalculations()
    }
    private fun loadCalculations() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getCalculations().collect { list ->
                _calculations.value = list
                _isLoading.value = false
            }
        }
    }
    fun deleteCalculation(id: Long) {
        viewModelScope.launch {
            repository.deleteCalculation(id)
        }
    }
    companion object {
        fun provideFactory(repo: CalculationRepository): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T = MyCalculationsViewModel(repo) as T
        }
    }
}