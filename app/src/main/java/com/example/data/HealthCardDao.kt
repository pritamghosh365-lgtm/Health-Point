package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface HealthCardDao {
    @Query("SELECT * FROM health_cards ORDER BY isDefault DESC, createdAt DESC")
    fun getAllCards(): Flow<List<HealthCard>>

    @Query("SELECT * FROM health_cards WHERE id = :id")
    fun getCardById(id: Long): Flow<HealthCard?>

    @Query("""
        SELECT * FROM health_cards 
        WHERE fullName LIKE '%' || :query || '%' 
           OR mobileNumber LIKE '%' || :query || '%' 
           OR aadhaarNumber LIKE '%' || :query || '%' 
           OR cardId LIKE '%' || :query || '%'
           OR address LIKE '%' || :query || '%'
        ORDER BY isDefault DESC, createdAt DESC
    """)
    fun searchCards(query: String): Flow<List<HealthCard>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: HealthCard): Long

    @Update
    suspend fun updateCard(card: HealthCard)

    @Delete
    suspend fun deleteCard(card: HealthCard)

    @Query("DELETE FROM health_cards WHERE id = :id")
    suspend fun deleteCardById(id: Long)

    @Query("UPDATE health_cards SET isDefault = 0")
    suspend fun clearDefaults()

    @Query("UPDATE health_cards SET isDefault = 1 WHERE id = :id")
    suspend fun markAsDefault(id: Long)

    @Transaction
    suspend fun setDefaultCard(id: Long) {
        clearDefaults()
        markAsDefault(id)
    }

    @Query("SELECT COUNT(*) FROM health_cards")
    suspend fun getCardCount(): Int
}
