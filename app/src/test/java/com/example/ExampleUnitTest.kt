package com.example

import com.example.data.HealthCard
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testHealthCardFormatting() {
        val card = HealthCard(
            id = 1,
            cardId = "HP-1234-5678-9012-3456",
            fullName = "Pritam Ghosh",
            age = 28,
            gender = "Male",
            mobileNumber = "9876543210",
            aadhaarNumber = "123456789012",
            address = "Kolkata, West Bengal"
        )

        assertEquals("HP  1234  5678  9012  3456", card.formattedCardNumber())
        assertEquals("+91 98765 43210", card.formattedMobile())
        assertEquals("XXXX  XXXX  9012", card.formattedAadhaar(masked = true))
        assertEquals("1234  5678  9012", card.formattedAadhaar(masked = false))
    }
}
