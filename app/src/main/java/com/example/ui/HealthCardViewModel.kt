package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.HealthCard
import com.example.data.HealthCardDatabase
import com.example.data.HealthCardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.random.Random

data class HealthCardUiState(
    val cards: List<HealthCard> = emptyList(),
    val filteredCards: List<HealthCard> = emptyList(),
    val selectedCard: HealthCard? = null,
    val searchQuery: String = "",
    val selectedBloodFilter: String? = null,
    val showCreateSheet: Boolean = false,
    val editingCard: HealthCard? = null,
    val isCardFlipped: Boolean = false,
    val userMessage: String? = null
)

private data class FilterState(
    val query: String = "",
    val bloodFilter: String? = null,
    val selectedCardId: Long? = null,
    val showCreateSheet: Boolean = false,
    val editingCard: HealthCard? = null,
    val isCardFlipped: Boolean = false,
    val userMessage: String? = null
)

class HealthCardViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: HealthCardRepository
    private val _filterState = MutableStateFlow(FilterState())

    init {
        val database = HealthCardDatabase.getDatabase(application)
        repository = HealthCardRepository(database.healthCardDao())

        // Seed initial Bengali health cards if database is fresh
        viewModelScope.launch {
            if (repository.getCardCount() == 0) {
                seedInitialSampleCards()
            }
        }
    }

    val uiState: StateFlow<HealthCardUiState> = combine(
        repository.allCards,
        _filterState
    ) { allCards, filter ->
        val filtered = allCards.filter { card ->
            val matchesQuery = filter.query.isBlank() ||
                    card.fullName.contains(filter.query, ignoreCase = true) ||
                    card.mobileNumber.contains(filter.query) ||
                    card.aadhaarNumber.contains(filter.query) ||
                    card.cardId.contains(filter.query, ignoreCase = true) ||
                    card.address.contains(filter.query, ignoreCase = true)

            val matchesBlood = filter.bloodFilter == null || 
                    filter.bloodFilter == "All" || 
                    card.bloodGroup.equals(filter.bloodFilter, ignoreCase = true)

            matchesQuery && matchesBlood
        }

        // Determine active card for hero display
        val selected = when {
            filter.selectedCardId != null -> allCards.find { it.id == filter.selectedCardId } ?: allCards.firstOrNull()
            allCards.isNotEmpty() -> allCards.firstOrNull { it.isDefault } ?: allCards.first()
            else -> null
        }

        HealthCardUiState(
            cards = allCards,
            filteredCards = filtered,
            selectedCard = selected,
            searchQuery = filter.query,
            selectedBloodFilter = filter.bloodFilter,
            showCreateSheet = filter.showCreateSheet,
            editingCard = filter.editingCard,
            isCardFlipped = filter.isCardFlipped,
            userMessage = filter.userMessage
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HealthCardUiState()
    )

    fun selectCard(card: HealthCard) {
        _filterState.update { it.copy(selectedCardId = card.id, isCardFlipped = false) }
    }

    fun toggleCardFlip() {
        _filterState.update { it.copy(isCardFlipped = !it.isCardFlipped) }
    }

    fun setCardFlipped(flipped: Boolean) {
        _filterState.update { it.copy(isCardFlipped = flipped) }
    }

    fun setSearchQuery(query: String) {
        _filterState.update { it.copy(query = query) }
    }

    fun setBloodFilter(filter: String?) {
        val normalized = if (filter == "All") null else filter
        _filterState.update { it.copy(bloodFilter = normalized) }
    }

    fun openCreateSheet(cardToEdit: HealthCard? = null) {
        _filterState.update { it.copy(showCreateSheet = true, editingCard = cardToEdit) }
    }

    fun closeCreateSheet() {
        _filterState.update { it.copy(showCreateSheet = false, editingCard = null) }
    }

    fun clearUserMessage() {
        _filterState.update { it.copy(userMessage = null) }
    }

    /**
     * Generates a realistic unique ATM Smart Card Number
     * Format: HP-XXXX-XXXX-XXXX-XXXX
     */
    fun generateNewCardNumber(): String {
        val part1 = Random.nextInt(1000, 9999)
        val part2 = Random.nextInt(1000, 9999)
        val part3 = Random.nextInt(1000, 9999)
        val part4 = Random.nextInt(1000, 9999)
        return "HP-$part1-$part2-$part3-$part4"
    }

    /**
     * Generates standard 5-year validity string like "10/31"
     */
    fun generateValidThru(): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.YEAR, 5)
        val month = String.format(Locale.US, "%02d", cal.get(Calendar.MONTH) + 1)
        val year = (cal.get(Calendar.YEAR) % 100).toString()
        return "$month/$year"
    }

    /**
     * Generates issue date string like "Oct 2026"
     */
    fun generateIssueDate(): String {
        val sdf = SimpleDateFormat("MMM yyyy", Locale.US)
        return sdf.format(Calendar.getInstance().time)
    }

    fun saveCard(
        name: String,
        age: Int,
        gender: String,
        mobile: String,
        aadhaar: String,
        address: String,
        bloodGroup: String,
        emergencyContact: String,
        emergencyRelation: String,
        cardTier: String,
        existingCardId: String? = null,
        existingId: Long = 0,
        isDefault: Boolean = false
    ) {
        viewModelScope.launch {
            val autoCardId = existingCardId ?: generateNewCardNumber()
            val validThru = generateValidThru()
            val issueDate = generateIssueDate()

            val card = HealthCard(
                id = existingId,
                cardId = autoCardId,
                fullName = name.trim(),
                age = age,
                gender = gender,
                mobileNumber = mobile.trim(),
                aadhaarNumber = aadhaar.trim(),
                address = address.trim(),
                bloodGroup = bloodGroup,
                emergencyContact = emergencyContact.trim(),
                emergencyRelation = emergencyRelation.trim().ifBlank { "Guardian" },
                cardTier = cardTier,
                validThru = validThru,
                issueDate = issueDate,
                isDefault = isDefault
            )

            if (existingId == 0L) {
                val newId = repository.insertCard(card)
                _filterState.update { 
                    it.copy(
                        selectedCardId = newId, 
                        showCreateSheet = false, 
                        editingCard = null,
                        userMessage = "নতুন হেলথ কার্ড সফলভাবে তৈরি হয়েছে! (New Health Card Created)"
                    ) 
                }
            } else {
                repository.updateCard(card)
                _filterState.update { 
                    it.copy(
                        selectedCardId = existingId, 
                        showCreateSheet = false, 
                        editingCard = null,
                        userMessage = "হেলথ কার্ড আপডেট হয়েছে! (Card Updated)"
                    ) 
                }
            }
        }
    }

    fun deleteCard(card: HealthCard) {
        viewModelScope.launch {
            repository.deleteCard(card)
            _filterState.update {
                val newSelected = if (it.selectedCardId == card.id) null else it.selectedCardId
                it.copy(
                    selectedCardId = newSelected,
                    userMessage = "কার্ড মুছে ফেলা হয়েছে (Card Deleted)"
                )
            }
        }
    }

    fun setDefaultCard(card: HealthCard) {
        viewModelScope.launch {
            repository.setDefaultCard(card.id)
            _filterState.update {
                it.copy(
                    selectedCardId = card.id,
                    userMessage = "${card.fullName} এর কার্ড প্রধান হিসেবে সেট করা হয়েছে (Set as Primary Card)"
                )
            }
        }
    }

    private suspend fun seedInitialSampleCards() {
        val sample1 = HealthCard(
            cardId = "HP-7284-9102-3841-5920",
            fullName = "Pritam Ghosh",
            age = 28,
            gender = "Male",
            mobileNumber = "9830145892",
            aadhaarNumber = "548291043821",
            address = "42 Rashbehari Avenue, Kolkata, West Bengal - 700026",
            bloodGroup = "O+",
            emergencyContact = "9830145899",
            emergencyRelation = "Father",
            cardTier = "Green Gold",
            validThru = "10/31",
            issueDate = "Oct 2026",
            isDefault = true
        )

        val sample2 = HealthCard(
            cardId = "HP-3819-4720-6581-2094",
            fullName = "Ananya Ghosh",
            age = 26,
            gender = "Female",
            mobileNumber = "9830554123",
            aadhaarNumber = "619283740192",
            address = "Flat 4B, Salt Lake Sector 2, Kolkata - 700091",
            bloodGroup = "B+",
            emergencyContact = "9830145892",
            emergencyRelation = "Spouse",
            cardTier = "Gerua Elite",
            validThru = "10/31",
            issueDate = "Oct 2026",
            isDefault = false
        )

        val sample3 = HealthCard(
            cardId = "HP-6102-8394-1182-4753",
            fullName = "Subhash Chandra Ghosh",
            age = 58,
            gender = "Male",
            mobileNumber = "9433128945",
            aadhaarNumber = "482019485721",
            address = "Shantiniketan Road, Bolpur, Birbhum - 731204",
            bloodGroup = "AB+",
            emergencyContact = "9830145892",
            emergencyRelation = "Son",
            cardTier = "Emerald Premium",
            validThru = "10/31",
            issueDate = "Oct 2026",
            isDefault = false
        )

        repository.insertCard(sample1)
        repository.insertCard(sample2)
        repository.insertCard(sample3)
    }
}
