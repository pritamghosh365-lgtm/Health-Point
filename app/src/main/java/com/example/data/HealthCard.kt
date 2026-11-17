package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "health_cards")
data class HealthCard(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val cardId: String,
    val fullName: String,
    val age: Int,
    val gender: String,
    val mobileNumber: String,
    val aadhaarNumber: String,
    val address: String,
    val bloodGroup: String = "O+",
    val emergencyContact: String = "",
    val emergencyRelation: String = "Guardian",
    val cardTier: String = "Green Gold",
    val validThru: String = "10/31",
    val issueDate: String = "Oct 2026",
    val photoUri: String? = null,
    val avatarColorIndex: Int = 0,
    val isDefault: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) {
    /**
     * Formats card ID with ATM spacing e.g. "HP 4892 7104 9283 5164"
     */
    fun formattedCardNumber(): String {
        val clean = cardId.replace("-", "").replace(" ", "").removePrefix("HP")
        val padded = clean.padEnd(16, '0').take(16)
        val chunked = padded.chunked(4).joinToString("  ")
        return "HP  $chunked"
    }

    /**
     * Formats Aadhaar with spacing e.g. "XXXX XXXX 3821" or "5482 9104 3821"
     */
    fun formattedAadhaar(masked: Boolean = true): String {
        val clean = aadhaarNumber.replace(" ", "").replace("-", "")
        if (clean.length < 12) return aadhaarNumber
        return if (masked) {
            "XXXX  XXXX  ${clean.takeLast(4)}"
        } else {
            "${clean.substring(0, 4)}  ${clean.substring(4, 8)}  ${clean.substring(8, 12)}"
        }
    }

    /**
     * Formats mobile number e.g. "+91 98765 43210"
     */
    fun formattedMobile(): String {
        val clean = mobileNumber.replace(" ", "").replace("+91", "").trim()
        return if (clean.length == 10) {
            "+91 ${clean.substring(0, 5)} ${clean.substring(5)}"
        } else {
            mobileNumber
        }
    }
}
