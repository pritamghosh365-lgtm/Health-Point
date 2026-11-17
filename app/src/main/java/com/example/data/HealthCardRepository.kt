package com.example.data

import kotlinx.coroutines.flow.Flow

class HealthCardRepository(private val healthCardDao: HealthCardDao) {
    val allCards: Flow<List<HealthCard>> = healthCardDao.getAllCards()

    fun searchCards(query: String): Flow<List<HealthCard>> {
        return if (query.isBlank()) {
            healthCardDao.getAllCards()
        } else {
            healthCardDao.searchCards(query.trim())
        }
    }

    suspend fun insertCard(card: HealthCard): Long {
        return healthCardDao.insertCard(card)
    }

    suspend fun updateCard(card: HealthCard) {
        healthCardDao.updateCard(card)
    }

    suspend fun deleteCard(card: HealthCard) {
        healthCardDao.deleteCard(card)
    }

    suspend fun deleteCardById(id: Long) {
        healthCardDao.deleteCardById(id)
    }

    suspend fun setDefaultCard(id: Long) {
        healthCardDao.setDefaultCard(id)
    }

    suspend fun getCardCount(): Int {
        return healthCardDao.getCardCount()
    }
}
