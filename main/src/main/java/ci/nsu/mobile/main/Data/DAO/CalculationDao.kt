package ci.nsu.mobile.main.Data.DAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ci.nsu.mobile.main.Data.Entity.CalculationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CalculationDao {
    @Insert
    suspend fun insert(calculation: CalculationEntity)

    @Query("SELECT * FROM calculations WHERE userId = :userId ORDER BY timestamp DESC")
    fun getCalculationsForUser(userId: Long): Flow<List<CalculationEntity>>

    @Query("DELETE FROM calculations WHERE userId = :userId AND id = :id")
    suspend fun deleteByIdForUser(userId: Long, id: Long)

    @Query("SELECT * FROM calculations WHERE userId = :userId AND id = :id")
    fun getCalculationByIdForUser(userId: Long, id: Long): Flow<CalculationEntity?>
}