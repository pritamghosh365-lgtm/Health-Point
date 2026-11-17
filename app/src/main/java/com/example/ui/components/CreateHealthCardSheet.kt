package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.HealthCard
import com.example.ui.theme.BloodRed
import com.example.ui.theme.GeruaBright
import com.example.ui.theme.GeruaDeep
import com.example.ui.theme.GeruaGold
import com.example.ui.theme.GeruaPrimary
import com.example.ui.theme.HealthGreenDark
import com.example.ui.theme.HealthGreenPrimary
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CreateHealthCardSheet(
    onDismiss: () -> Unit,
    onSave: (
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
        existingCardId: String?,
        existingId: Long,
        isDefault: Boolean
    ) -> Unit,
    editingCard: HealthCard? = null,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    fun generateRandomCardId(): String {
        val p1 = Random.nextInt(1000, 9999)
        val p2 = Random.nextInt(1000, 9999)
        val p3 = Random.nextInt(1000, 9999)
        val p4 = Random.nextInt(1000, 9999)
        return "HP-$p1-$p2-$p3-$p4"
    }

    var cardId by remember { mutableStateOf(editingCard?.cardId ?: generateRandomCardId()) }
    var fullName by remember { mutableStateOf(editingCard?.fullName ?: "") }
    var ageText by remember { mutableStateOf(editingCard?.age?.toString() ?: "") }
    var selectedGender by remember { mutableStateOf(editingCard?.gender ?: "Male") }
    var mobileNumber by remember { mutableStateOf(editingCard?.mobileNumber ?: "") }
    var aadhaarNumber by remember { mutableStateOf(editingCard?.aadhaarNumber ?: "") }
    var address by remember { mutableStateOf(editingCard?.address ?: "") }
    var selectedBloodGroup by remember { mutableStateOf(editingCard?.bloodGroup ?: "O+") }
    var emergencyContact by remember { mutableStateOf(editingCard?.emergencyContact ?: "") }
    var emergencyRelation by remember { mutableStateOf(editingCard?.emergencyRelation ?: "Family") }
    var selectedTier by remember { mutableStateOf(editingCard?.cardTier ?: "Green Gold") }
    var isDefault by remember { mutableStateOf(editingCard?.isDefault ?: false) }

    // Error states
    var nameError by remember { mutableStateOf(false) }
    var ageError by remember { mutableStateOf(false) }
    var mobileError by remember { mutableStateOf(false) }
    var aadhaarError by remember { mutableStateOf(false) }
    var addressError by remember { mutableStateOf(false) }

    val bloodGroups = listOf("O+", "A+", "B+", "AB+", "O-", "A-", "B-", "AB-")
    val genders = listOf("Male" to "পুরুষ (Male)", "Female" to "মহিলা (Female)", "Other" to "অন্যান্য (Other)")
    val tiers = listOf("Green Gold", "Emerald Premium", "Gerua Elite")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // HEADER BAR
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = HealthGreenPrimary,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.CreditCard,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    Column {
                        Text(
                            text = if (editingCard == null) "নতুন হেলথ কার্ড তৈরি" else "হেলথ কার্ড সম্পাদনা",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = HealthGreenDark
                        )
                        Text(
                            text = "HEALTH POINT • ATM SMART CARD",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = GeruaPrimary,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_sheet_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // AUTO-GENERATED CARD ID SECTION
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = HealthGreenDark,
                border = androidx.compose.foundation.BorderStroke(1.dp, GeruaGold.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = GeruaGold,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "স্বয়ংক্রিয় কার্ড আইডি (Auto-Generated ID):",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Text(
                            text = cardId,
                            color = Color.White,
                            fontSize = 15.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        )
                    }

                    if (editingCard == null) {
                        OutlinedButton(
                            onClick = { cardId = generateRandomCardId() },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = GeruaGold),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GeruaGold),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("regenerate_id_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Regenerate ID",
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("নতুন আইডি", fontSize = 11.sp)
                        }
                    }
                }
            }

            // NAME FIELD
            OutlinedTextField(
                value = fullName,
                onValueChange = {
                    fullName = it
                    nameError = it.isBlank()
                },
                label = { Text("কার্ডধারীর পুরো নাম (Full Name) *") },
                placeholder = { Text("যেমন: প্রীতম ঘোষ / Pritam Ghosh") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = HealthGreenPrimary)
                },
                isError = nameError,
                supportingText = {
                    if (nameError) Text("নাম লিখুন (Name is required)", color = BloodRed)
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_full_name"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = HealthGreenPrimary,
                    focusedLabelColor = HealthGreenPrimary
                )
            )

            // AGE & GENDER ROW
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = ageText,
                    onValueChange = {
                        val filtered = it.filter { char -> char.isDigit() }.take(3)
                        ageText = filtered
                        ageError = filtered.toIntOrNull() == null || filtered.toInt() <= 0
                    },
                    label = { Text("বয়স (Age) *") },
                    placeholder = { Text("28") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = ageError,
                    supportingText = {
                        if (ageError) Text("সঠিক বয়স দিন", color = BloodRed)
                    },
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("input_age"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = HealthGreenPrimary,
                        focusedLabelColor = HealthGreenPrimary
                    )
                )

                // Gender selector dropdown/chips
                Column(modifier = Modifier.weight(2f)) {
                    Text(
                        text = "লিঙ্গ (Gender):",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        genders.forEach { (genderCode, label) ->
                            val isSelected = selectedGender.equals(genderCode, ignoreCase = true)
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) HealthGreenPrimary else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedGender = genderCode }
                                    .padding(vertical = 2.dp)
                            ) {
                                Box(
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = genderCode,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // MOBILE NUMBER FIELD
            OutlinedTextField(
                value = mobileNumber,
                onValueChange = {
                    val digits = it.filter { char -> char.isDigit() }.take(10)
                    mobileNumber = digits
                    mobileError = digits.length != 10
                },
                label = { Text("মোবাইল নাম্বার (10 Digits Mobile) *") },
                placeholder = { Text("9876543210") },
                prefix = { Text("+91 ", fontWeight = FontWeight.Bold, color = HealthGreenDark) },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Phone, contentDescription = null, tint = HealthGreenPrimary)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                isError = mobileError,
                supportingText = {
                    if (mobileError) Text("সঠিক ১০ সংখ্যার মোবাইল দিন (10 digits required)", color = BloodRed)
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_mobile"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = HealthGreenPrimary,
                    focusedLabelColor = HealthGreenPrimary
                )
            )

            // AADHAAR NUMBER FIELD
            OutlinedTextField(
                value = aadhaarNumber,
                onValueChange = {
                    val digits = it.filter { char -> char.isDigit() }.take(12)
                    aadhaarNumber = digits
                    aadhaarError = digits.length != 12
                },
                label = { Text("আধার নাম্বার (12 Digits Aadhaar) *") },
                placeholder = { Text("1234 5678 9012") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = GeruaPrimary)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = aadhaarError,
                supportingText = {
                    if (aadhaarError) {
                        Text("সঠিক ১২ সংখ্যার আধার দিন (12 digits required)", color = BloodRed)
                    } else if (aadhaarNumber.length == 12) {
                        Text("কার্ডে সুরক্ষিতভাবে সংরক্ষিত থাকবে", color = HealthGreenPrimary)
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_aadhaar"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GeruaPrimary,
                    focusedLabelColor = GeruaPrimary
                )
            )

            // ADDRESS FIELD
            OutlinedTextField(
                value = address,
                onValueChange = {
                    address = it
                    addressError = it.isBlank()
                },
                label = { Text("ঠিকানা (Full Address - Vill/City, Dist, PIN) *") },
                placeholder = { Text("গ্রাম/শহর, থানা, জেলা, পিন কোড") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = HealthGreenPrimary)
                },
                isError = addressError,
                supportingText = {
                    if (addressError) Text("ঠিকানা আবশ্যক (Address is required)", color = BloodRed)
                },
                maxLines = 3,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_address"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = HealthGreenPrimary,
                    focusedLabelColor = HealthGreenPrimary
                )
            )

            // BLOOD GROUP SELECTOR
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Bloodtype,
                        contentDescription = null,
                        tint = BloodRed,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "রক্তের গ্রুপ (Blood Group):",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    bloodGroups.forEach { bg ->
                        val isSelected = selectedBloodGroup == bg
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedBloodGroup = bg },
                            label = { Text(bg, fontWeight = FontWeight.Bold) },
                            leadingIcon = if (isSelected) {
                                {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            } else null,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BloodRed,
                                selectedLabelColor = Color.White,
                                selectedLeadingIconColor = Color.White
                            )
                        )
                    }
                }
            }

            // EMERGENCY CONTACT ROW
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = emergencyContact,
                    onValueChange = {
                        emergencyContact = it.filter { char -> char.isDigit() }.take(10)
                    },
                    label = { Text("জরুরি নম্বর (Emergency Phone)") },
                    placeholder = { Text("9876543211") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier.weight(1.3f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GeruaPrimary,
                        focusedLabelColor = GeruaPrimary
                    )
                )

                OutlinedTextField(
                    value = emergencyRelation,
                    onValueChange = { emergencyRelation = it },
                    label = { Text("সম্পর্ক (Relation)") },
                    placeholder = { Text("বাবা/মা/স্ত্রী") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GeruaPrimary,
                        focusedLabelColor = GeruaPrimary
                    )
                )
            }

            // CARD TIER SELECTION
            Column {
                Text(
                    text = "কার্ডের থিম ও ক্যাটাগরি (Card Theme):",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    tiers.forEach { tier ->
                        val isSelected = selectedTier == tier
                        val chipColor = when (tier) {
                            "Emerald Premium" -> HealthGreenDark
                            "Gerua Elite" -> GeruaDeep
                            else -> HealthGreenPrimary
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) chipColor else MaterialTheme.colorScheme.surfaceVariant,
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, GeruaGold) else null,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedTier = tier }
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = tier,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // SET AS DEFAULT CHECKBOX
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isDefault = !isDefault }
            ) {
                Checkbox(
                    checked = isDefault,
                    onCheckedChange = { isDefault = it },
                    colors = CheckboxDefaults.colors(checkedColor = HealthGreenPrimary)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "এই কার্ডটিকে প্রধান (Default) কার্ড হিসেবে সেট করুন",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // ACTION BUTTONS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Text("বাতিল (Cancel)")
                }

                Button(
                    onClick = {
                        val validName = fullName.isNotBlank()
                        val ageInt = ageText.toIntOrNull()
                        val validAge = ageInt != null && ageInt in 1..125
                        val validMobile = mobileNumber.length == 10
                        val validAadhaar = aadhaarNumber.length == 12
                        val validAddress = address.isNotBlank()

                        nameError = !validName
                        ageError = !validAge
                        mobileError = !validMobile
                        aadhaarError = !validAadhaar
                        addressError = !validAddress

                        if (validName && validAge && validMobile && validAadhaar && validAddress) {
                            onSave(
                                fullName,
                                ageInt ?: 25,
                                selectedGender,
                                mobileNumber,
                                aadhaarNumber,
                                address,
                                selectedBloodGroup,
                                emergencyContact,
                                emergencyRelation,
                                selectedTier,
                                if (editingCard != null) editingCard.cardId else cardId,
                                editingCard?.id ?: 0L,
                                isDefault
                            )
                        }
                    },
                    modifier = Modifier
                        .weight(1.5f)
                        .height(48.dp)
                        .testTag("save_card_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = HealthGreenPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (editingCard == null) "কার্ড তৈরি করুন (Create)" else "আপডেট করুন (Update)",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
