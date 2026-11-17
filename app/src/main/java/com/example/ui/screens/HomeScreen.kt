package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Flip
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.HealthCard
import com.example.ui.HealthCardViewModel
import com.example.ui.components.AtmHealthCard
import com.example.ui.components.CreateHealthCardSheet
import com.example.ui.theme.BloodRed
import com.example.ui.theme.GeruaBright
import com.example.ui.theme.GeruaDeep
import com.example.ui.theme.GeruaGold
import com.example.ui.theme.GeruaPrimary
import com.example.ui.theme.HealthGreenDark
import com.example.ui.theme.HealthGreenLight
import com.example.ui.theme.HealthGreenPrimary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HealthCardViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var cardToDelete by remember { mutableStateOf<HealthCard?>(null) }

    // Show snackbar when user message changes
    LaunchedEffect(uiState.userMessage) {
        uiState.userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearUserMessage()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            HealthPointTopBar(
                cardCount = uiState.cards.size,
                onAddNewClick = { viewModel.openCreateSheet() }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.openCreateSheet() },
                containerColor = GeruaPrimary,
                contentColor = Color.White,
                icon = { Icon(Icons.Default.Add, contentDescription = "Add Card") },
                text = { Text("নতুন কার্ড (Add Card)", fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("fab_add_card")
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            // TOP BANNER & SEARCH SECTION
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    // Search Bar
                    OutlinedTextField(
                        value = uiState.searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        placeholder = { Text("নাম, মোবাইল বা আধার দিয়ে খুঁজুন...", fontSize = 13.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = "Search", tint = HealthGreenPrimary)
                        },
                        trailingIcon = {
                            if (uiState.searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear search")
                                }
                            }
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("search_card_input"),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = HealthGreenPrimary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Blood Group Filter chips
                    BloodGroupFilterRow(
                        selectedFilter = uiState.selectedBloodFilter,
                        onSelectFilter = { viewModel.setBloodFilter(it) }
                    )
                }
            }

            // ACTIVE ATM CARD HERO DISPLAY
            item {
                val currentCard = uiState.selectedCard
                if (currentCard != null) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        AtmHealthCard(
                            card = currentCard,
                            isFlippedInitially = uiState.isCardFlipped,
                            onFlip = { flipped -> viewModel.setCardFlipped(flipped) },
                            onEmergencyCall = { number ->
                                makePhoneCall(context, number)
                            }
                        )

                        // Card Flip Hint & Action Chips
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "👆 কার্ডে ট্যাপ করে উল্টান (Tap to flip card)",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Medium
                            )

                            // Quick flip button
                            TextButton(
                                onClick = { viewModel.toggleCardFlip() },
                                modifier = Modifier.testTag("toggle_flip_text_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Flip,
                                    contentDescription = null,
                                    tint = GeruaPrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (uiState.isCardFlipped) "সামনে দেখুন" else "পিছনে দেখুন",
                                    color = GeruaPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Hero Card Control Buttons
                        HeroCardActionRow(
                            card = currentCard,
                            onShare = { shareCardDetails(context, currentCard) },
                            onCallEmergency = {
                                if (currentCard.emergencyContact.isNotBlank()) {
                                    makePhoneCall(context, currentCard.emergencyContact)
                                }
                            },
                            onEdit = { viewModel.openCreateSheet(currentCard) },
                            onDelete = { cardToDelete = currentCard },
                            onSetDefault = { viewModel.setDefaultCard(currentCard) }
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                    }
                } else {
                    // Empty State Card
                    EmptyCardPlaceholder(onAddNew = { viewModel.openCreateSheet() })
                }
            }

            // MULTI-CARD LIST SECTION (মাল্টি আইডি তালিকা)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = HealthGreenPrimary,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.CreditCard,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                        Text(
                            text = "আপনার সকল হেলথ কার্ড (All Cards)",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = HealthGreenPrimary.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = "${uiState.filteredCards.size} টি কার্ড",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = HealthGreenPrimary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Cards Horizontal / Vertical Switcher List
            items(uiState.filteredCards, key = { it.id }) { card ->
                val isSelected = uiState.selectedCard?.id == card.id
                CompactCardListItem(
                    card = card,
                    isSelected = isSelected,
                    onClick = { viewModel.selectCard(card) },
                    onEdit = { viewModel.openCreateSheet(card) },
                    onDelete = { cardToDelete = card },
                    onSetDefault = { viewModel.setDefaultCard(card) }
                )
            }
        }
    }

    // MODAL BOTTOM SHEET: Add New / Edit Card
    if (uiState.showCreateSheet) {
        CreateHealthCardSheet(
            onDismiss = { viewModel.closeCreateSheet() },
            onSave = { name, age, gender, mobile, aadhaar, address, blood, emergency, relation, tier, existingCardId, existingId, isDefault ->
                viewModel.saveCard(
                    name = name,
                    age = age,
                    gender = gender,
                    mobile = mobile,
                    aadhaar = aadhaar,
                    address = address,
                    bloodGroup = blood,
                    emergencyContact = emergency,
                    emergencyRelation = relation,
                    cardTier = tier,
                    existingCardId = existingCardId,
                    existingId = existingId,
                    isDefault = isDefault
                )
            },
            editingCard = uiState.editingCard
        )
    }

    // DELETE CONFIRMATION DIALOG
    cardToDelete?.let { card ->
        AlertDialog(
            onDismissRequest = { cardToDelete = null },
            icon = {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = BloodRed
                )
            },
            title = {
                Text(text = "কার্ড মুছে ফেলবেন?")
            },
            text = {
                Text(
                    text = "${card.fullName} এর হেলথ কার্ড (আইডি: ${card.cardId}) মুছে ফেলতে চান? এটি পুনরুদ্ধার করা যাবে না।"
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteCard(card)
                        cardToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BloodRed)
                ) {
                    Text("মুছে ফেলুন (Delete)", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { cardToDelete = null }) {
                    Text("বাতিল (Cancel)")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthPointTopBar(
    cardCount: Int,
    onAddNewClick: () -> Unit
) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color.White,
                    shadowElevation = 2.dp,
                    modifier = Modifier.size(38.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.health_point_logo),
                        contentDescription = "Health Point App Logo",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "HEALTH POINT",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = GeruaGold
                        ) {
                            Text(
                                text = "ATM CARD",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                    Text(
                        text = "স্মার্ট হেলথ আইডি কার্ড জেনারেটর",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.85f),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        },
        actions = {
            FilledTonalButton(
                onClick = onAddNewClick,
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = GeruaPrimary,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .padding(end = 8.dp)
                    .testTag("top_add_card_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "নতুন কার্ড", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = HealthGreenPrimary,
            titleContentColor = Color.White
        )
    )
}

@Composable
fun BloodGroupFilterRow(
    selectedFilter: String?,
    onSelectFilter: (String) -> Unit
) {
    val filters = listOf("All", "O+", "A+", "B+", "AB+", "O-", "A-", "B-", "AB-")

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        contentPadding = PaddingValues(vertical = 2.dp)
    ) {
        items(filters) { filter ->
            val isSelected = (selectedFilter == null && filter == "All") || (selectedFilter == filter)
            FilterChip(
                selected = isSelected,
                onClick = { onSelectFilter(filter) },
                label = {
                    Text(
                        text = if (filter == "All") "সকল গ্রুপ (All)" else filter,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = if (filter == "All") HealthGreenPrimary else BloodRed,
                    selectedLabelColor = Color.White,
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    }
}

@Composable
fun HeroCardActionRow(
    card: HealthCard,
    onShare: () -> Unit,
    onCallEmergency: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onSetDefault: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Share Action
        OutlinedButton(
            onClick = onShare,
            modifier = Modifier
                .weight(1f)
                .height(40.dp)
                .testTag("action_share_card"),
            shape = RoundedCornerShape(10.dp),
            contentPadding = PaddingValues(horizontal = 6.dp)
        ) {
            Icon(Icons.Default.Share, contentDescription = "Share", modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("শেয়ার", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        }

        // Emergency Call Action
        if (card.emergencyContact.isNotBlank()) {
            OutlinedButton(
                onClick = onCallEmergency,
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .testTag("action_emergency_call"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = GeruaPrimary),
                border = androidx.compose.foundation.BorderStroke(1.dp, GeruaPrimary),
                contentPadding = PaddingValues(horizontal = 6.dp)
            ) {
                Icon(Icons.Default.Call, contentDescription = "Call", modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("জরুরি কল", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Set Default / Primary Action
        OutlinedButton(
            onClick = onSetDefault,
            modifier = Modifier
                .weight(1f)
                .height(40.dp)
                .testTag("action_set_default"),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = if (card.isDefault) HealthGreenPrimary else MaterialTheme.colorScheme.onSurfaceVariant
            ),
            contentPadding = PaddingValues(horizontal = 6.dp)
        ) {
            Icon(
                imageVector = if (card.isDefault) Icons.Default.Star else Icons.Default.StarOutline,
                contentDescription = "Default",
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(if (card.isDefault) "প্রধান" else "ডিফল্ট", fontSize = 11.sp)
        }

        // Edit Action
        OutlinedButton(
            onClick = onEdit,
            modifier = Modifier
                .weight(0.9f)
                .height(40.dp)
                .testTag("action_edit_card"),
            shape = RoundedCornerShape(10.dp),
            contentPadding = PaddingValues(horizontal = 6.dp)
        ) {
            Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(3.dp))
            Text("এডিট", fontSize = 11.sp)
        }
    }
}

@Composable
fun CompactCardListItem(
    card: HealthCard,
    isSelected: Boolean,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onSetDefault: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .testTag("compact_card_${card.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (isSelected) HealthGreenPrimary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = if (isSelected) 3.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = if (isSelected) 1.5.dp else 0.5.dp,
                    color = if (isSelected) HealthGreenPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(14.dp)
                )
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left Mini Card Icon & Info
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                // Mini ATM card icon representation
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when (card.cardTier) {
                        "Emerald Premium" -> HealthGreenDark
                        "Gerua Elite" -> GeruaDeep
                        else -> HealthGreenPrimary
                    },
                    modifier = Modifier.size(width = 44.dp, height = 30.dp),
                    shadowElevation = 2.dp
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = card.bloodGroup,
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = card.fullName,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (card.isDefault) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = HealthGreenPrimary
                            ) {
                                Text(
                                    text = "ডিফল্ট",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }

                    Text(
                        text = "${card.cardId} • ${card.formattedMobile()}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontFamily = FontFamily.Monospace
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "বয়স: ${card.age}Y",
                            fontSize = 10.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(text = "•", fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
                        Text(
                            text = "আধার: ${card.formattedAadhaar(masked = true)}",
                            fontSize = 10.5.sp,
                            color = GeruaPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Right Quick Actions
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.size(34.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Card",
                        tint = HealthGreenPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(34.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Card",
                        tint = BloodRed.copy(alpha = 0.8f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyCardPlaceholder(onAddNew: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = HealthGreenPrimary.copy(alpha = 0.15f),
                modifier = Modifier.size(56.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.CreditCard,
                        contentDescription = null,
                        tint = HealthGreenPrimary,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }

            Text(
                text = "কোনো হেলথ কার্ড পাওয়া যায়নি",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "এটিএম কার্ড ডিজাইনে আপনার প্রথম হেলথ কার্ড তৈরি করুন। স্বয়ংক্রিয় কার্ড আইডি, কিউআর কোড ও স্বাস্থ্য বিবরণ যুক্ত থাকবে।",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Button(
                onClick = onAddNew,
                colors = ButtonDefaults.buttonColors(containerColor = HealthGreenPrimary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("প্রথম হেলথ কার্ড তৈরি করুন", fontWeight = FontWeight.Bold)
            }
        }
    }
}

/**
 * Triggers native Android Share Sheet with Health Card text summary
 */
private fun shareCardDetails(context: Context, card: HealthCard) {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(
            Intent.EXTRA_TEXT,
            """
            🏥 HEALTH POINT SMART HEALTH CARD 🏥
            ━━━━━━━━━━━━━━━━━━━━━
            🏷️ কার্ড আইডি (Card ID): ${card.cardId}
            👤 নাম (Name): ${card.fullName}
            🎂 বয়স (Age): ${card.age} বছর (${card.gender})
            🩸 রক্তের গ্রুপ (Blood Group): ${card.bloodGroup}
            📱 মোবাইল (Mobile): ${card.formattedMobile()}
            🛡️ আধার (Aadhaar): ${card.formattedAadhaar(masked = false)}
            📍 ঠিকানা (Address): ${card.address}
            🚨 জরুরি নম্বর (Emergency): ${card.emergencyContact} (${card.emergencyRelation})
            📅 মেয়াদ (Valid Thru): ${card.validThru} (ইস্যু: ${card.issueDate})
            ━━━━━━━━━━━━━━━━━━━━━
            হেলথ পয়েন্ট ডিজিটাল স্মার্ট হেলথ কার্ড
            """.trimIndent()
        )
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, "Health Point কার্ড শেয়ার করুন")
    context.startActivity(shareIntent)
}

/**
 * Launches Phone Dialer for emergency contact
 */
private fun makePhoneCall(context: Context, phoneNumber: String) {
    val clean = phoneNumber.replace(" ", "").replace("-", "")
    val intent = Intent(Intent.ACTION_DIAL).apply {
        data = Uri.parse("tel:$clean")
    }
    context.startActivity(intent)
}
