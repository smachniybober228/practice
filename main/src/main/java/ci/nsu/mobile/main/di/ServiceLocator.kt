package ci.nsu.mobile.main.di

import android.content.Context
import ci.nsu.mobile.main.Auth.TokenManager
import ci.nsu.mobile.main.Auth.UserPreferences
import ci.nsu.mobile.main.Data.DAO.CalculationDao
import ci.nsu.mobile.main.Data.Database.AppDatabase
import ci.nsu.mobile.main.Data.Repository.CalculationRepository
import ci.nsu.mobile.main.Network.ApiService
import ci.nsu.mobile.main.Network.RetrofitClient
import ci.nsu.mobile.main.Repository.AuthRepository

class ServiceLocator(private val context: Context) {
    val database: AppDatabase by lazy { AppDatabase.getInstance(context) }
    val calculationDao: CalculationDao by lazy { database.calculationDao() }
    val tokenManager: TokenManager by lazy { TokenManager(context) }
    val userPreferences: UserPreferences by lazy { UserPreferences(context) }
    val apiService: ApiService by lazy { RetrofitClient.getApiService(tokenManager) }
    val authRepository: AuthRepository by lazy { AuthRepository(apiService, tokenManager, userPreferences) }
    val calculationRepository: CalculationRepository by lazy { CalculationRepository(calculationDao, userPreferences) }
}