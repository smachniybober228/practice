package ci.nsu.mobile.main.Data.Repository

import ci.nsu.mobile.main.Auth.UserPreferences
import ci.nsu.mobile.main.Data.DAO.CalculationDao
import ci.nsu.mobile.main.Data.Entity.CalculationEntity
import kotlinx.coroutines.flow.Flow

class CalculationRepository(
    private val dao: CalculationDao,
    private val userPreferences: UserPreferences
) {
    fun getCalculations(): Flow<List<CalculationEntity>> = dao.getCalculationsForUser(userPreferences.userId)

    suspend fun insertCalculation(calculation: CalculationEntity) {
        val calculationWithUser = calculation.copy(userId = userPreferences.userId)
        dao.insert(calculationWithUser)
    }

    suspend fun deleteCalculation(id: Long) = dao.deleteByIdForUser(userPreferences.userId, id)

    fun getCalculationById(id: Long): Flow<CalculationEntity?> = dao.getCalculationByIdForUser(userPreferences.userId, id)
}