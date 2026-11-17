package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Flip
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.HealthCard
import com.example.ui.theme.BloodRed
import com.example.ui.theme.CardDarkStripe
import com.example.ui.theme.GeruaBright
import com.example.ui.theme.GeruaDeep
import com.example.ui.theme.GeruaGold
import com.example.ui.theme.GeruaPrimary
import com.example.ui.theme.HealthGreenDark
import com.example.ui.theme.HealthGreenLight
import com.example.ui.theme.HealthGreenPrimary

/**
 * An interactive, ATM-style Health Card with 3D Flip animation between front and back.
 * Features Green and Gerua gradient, metallic EMV chip, contactless wave, embossed card ID,
 * Aadhaar masking/unmasking, blood group pill, and full cardholder details.
 */
@Composable
fun AtmHealthCard(
    card: HealthCard,
    modifier: Modifier = Modifier,
    isFlippedInitially: Boolean = false,
    onFlip: ((Boolean) -> Unit)? = null,
    onEmergencyCall: ((String) -> Unit)? = null
) {
    var isFlipped by remember { mutableStateOf(isFlippedInitially) }
    var isAadhaarMasked by remember { mutableStateOf(true) }

    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 500),
        label = "cardFlipAnimation"
    )

    // Vibrant Green to Gerua gradient for authentic Bengali healthcare theme
    val cardBrush = remember(card.cardTier) {
        when (card.cardTier) {
            "Emerald Premium" -> Brush.linearGradient(
                colors = listOf(
                    Color(0xFF044728),
                    Color(0xFF0E7A44),
                    Color(0xFF138A4B),
                    GeruaDeep
                ),
                start = Offset(0f, 0f),
                end = Offset(1000f, 800f)
            )
            "Gerua Elite" -> Brush.linearGradient(
                colors = listOf(
                    GeruaDeep,
                    GeruaPrimary,
                    Color(0xFFD97706),
                    HealthGreenDark
                ),
                start = Offset(0f, 0f),
                end = Offset(1000f, 800f)
            )
            else -> Brush.linearGradient(
                // Green to Gerua (Saffron) signature theme
                colors = listOf(
                    HealthGreenDark,
                    HealthGreenPrimary,
                    GeruaDeep,
                    GeruaPrimary
                ),
                start = Offset(0f, 0f),
                end = Offset(1100f, 750f)
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("atm_health_card_${card.id}")
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                isFlipped = !isFlipped
                onFlip?.invoke(isFlipped)
            }
    ) {
        if (rotation <= 90f) {
            // FRONT OF CARD
            AtmCardFront(
                card = card,
                cardBrush = cardBrush,
                isAadhaarMasked = isAadhaarMasked,
                onToggleAadhaar = { isAadhaarMasked = !isAadhaarMasked },
                onFlipClick = {
                    isFlipped = true
                    onFlip?.invoke(true)
                }
            )
        } else {
            // BACK OF CARD (Mirrored so text is not reversed)
            Box(
                modifier = Modifier.graphicsLayer {
                    rotationY = 180f
                }
            ) {
                AtmCardBack(
                    card = card,
                    cardBrush = cardBrush,
                    onEmergencyCall = onEmergencyCall,
                    onFlipClick = {
                        isFlipped = false
                        onFlip?.invoke(false)
                    }
                )
            }
        }
    }
}

@Composable
fun AtmCardFront(
    card: HealthCard,
    cardBrush: Brush,
    isAadhaarMasked: Boolean,
    onToggleAadhaar: () -> Unit,
    onFlipClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.586f)
            .shadow(elevation = 12.dp, shape = RoundedCornerShape(20.dp), spotColor = HealthGreenPrimary),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(cardBrush)
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.25f),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(14.dp)
        ) {
            // Subtle glossy diagonal sheen
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.12f),
                                Color.Transparent,
                                Color.White.copy(alpha = 0.05f)
                            ),
                            start = Offset(0f, 0f),
                            end = Offset(600f, 500f)
                        )
                    )
            )

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // TOP BAR: Logo + Title + NFC Contactless Waves + Flip Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Circular Health Point Logo
                        Surface(
                            modifier = Modifier.size(38.dp),
                            shape = CircleShape,
                            color = Color.White,
                            shadowElevation = 3.dp
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.health_point_logo),
                                contentDescription = "Health Point Logo",
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
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.2.sp
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                if (card.isDefault) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Default Card",
                                        tint = GeruaGold,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                            Text(
                                text = "HEALTH CARD • স্বাস্থ্য কার্ড",
                                color = GeruaGold,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    // Top Right: Contactless & Flip & Tier Badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color.Black.copy(alpha = 0.28f),
                            border = androidx.compose.foundation.BorderStroke(0.8.dp, GeruaGold.copy(alpha = 0.6f))
                        ) {
                            Text(
                                text = card.cardTier.uppercase(),
                                color = GeruaGold,
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        ContactlessWaves(
                            color = Color.White.copy(alpha = 0.9f),
                            size = 18.dp
                        )

                        IconButton(
                            onClick = onFlipClick,
                            modifier = Modifier
                                .size(28.dp)
                                .testTag("flip_to_back_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Flip,
                                contentDescription = "Flip Card",
                                tint = Color.White.copy(alpha = 0.9f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                // MIDDLE ROW: EMV Smart Chip & Blood Group Badge
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    EmvChip(width = 44.dp, height = 34.dp)

                    // Blood Group Badge
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = BloodRed,
                        shadowElevation = 4.dp
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bloodtype,
                                contentDescription = "Blood Group",
                                tint = Color.White,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = card.bloodGroup,
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }

                // CARD NUMBER (ATM EMBOSSED MONOSPACE LOOK)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    Text(
                        text = "SMART HEALTH ID / কার্ড আইডি",
                        color = Color.White.copy(alpha = 0.65f),
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.8.sp
                    )
                    Text(
                        text = card.formattedCardNumber(),
                        color = Color.White,
                        fontSize = 17.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp,
                        modifier = Modifier.shadow(
                            elevation = 1.dp,
                            spotColor = Color.Black
                        )
                    )
                }

                // BOTTOM SECTION: Name, Age, Mobile, Aadhaar & Valid Thru
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    // Left Column: Name & Details
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(1.dp)
                    ) {
                        Text(
                            text = card.fullName.uppercase(),
                            color = Color.White,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "AGE: ${card.age}Y • ${card.gender.take(1).uppercase()}",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "•",
                                color = Color.White.copy(alpha = 0.5f),
                                fontSize = 9.sp
                            )
                            Text(
                                text = card.formattedMobile(),
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        // Aadhaar with unmask button
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "AADHAAR: ${card.formattedAadhaar(isAadhaarMasked)}",
                                color = GeruaGold,
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Icon(
                                imageVector = if (isAadhaarMasked) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = "Toggle Aadhaar visibility",
                                tint = GeruaGold,
                                modifier = Modifier
                                    .size(12.dp)
                                    .clickable { onToggleAadhaar() }
                            )
                        }
                    }

                    // Right Column: Valid Thru
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(1.dp)
                    ) {
                        Text(
                            text = "VALID THRU",
                            color = Color.White.copy(alpha = 0.65f),
                            fontSize = 7.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = card.validThru,
                            color = Color.White,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AtmCardBack(
    card: HealthCard,
    cardBrush: Brush,
    onEmergencyCall: ((String) -> Unit)?,
    onFlipClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.586f)
            .shadow(elevation = 12.dp, shape = RoundedCornerShape(20.dp), spotColor = HealthGreenPrimary),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(cardBrush)
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.25f),
                    shape = RoundedCornerShape(20.dp)
                )
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // TOP: Black Magnetic Stripe (Signature ATM Card Element)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(34.dp)
                        .background(CardDarkStripe)
                        .padding(horizontal = 14.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "HEALTH POINT SECURE MEDICAL CHIP NETWORK",
                            color = Color.White.copy(alpha = 0.45f),
                            fontSize = 7.5.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 0.6.sp
                        )

                        IconButton(
                            onClick = onFlipClick,
                            modifier = Modifier
                                .size(24.dp)
                                .testTag("flip_to_front_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Flip,
                                contentDescription = "Flip Back to Front",
                                tint = Color.White.copy(alpha = 0.8f),
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // SIGNATURE & CVV PANEL
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // White signature strip with security hatching
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(28.dp)
                            .background(Color(0xFFEDEDED), RoundedCornerShape(3.dp))
                            .padding(horizontal = 8.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = card.fullName,
                            color = Color(0xFF1E293B),
                            fontSize = 11.sp,
                            fontStyle = FontStyle.Italic,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // CVV/Security code
                    Box(
                        modifier = Modifier
                            .height(28.dp)
                            .background(Color.White, RoundedCornerShape(3.dp))
                            .padding(horizontal = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        val cvv = (card.cardId.hashCode().let { if (it < 0) -it else it } % 900 + 100).toString()
                        Text(
                            text = "CVV: $cvv",
                            color = Color(0xFF0F172A),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // MIDDLE: QR Code & Full Address / Emergency Info
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    // Scannable Health QR Code
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.White,
                        shadowElevation = 2.dp,
                        modifier = Modifier.size(66.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            HealthQrCode(
                                seed = "${card.cardId}-${card.fullName}-${card.bloodGroup}",
                                size = 58.dp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    // Full Address & Emergency Contact
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Address",
                                tint = GeruaGold,
                                modifier = Modifier
                                    .size(12.dp)
                                    .padding(top = 1.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "ঠিকানা: ${card.address}",
                                color = Color.White,
                                fontSize = 8.5.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        if (card.emergencyContact.isNotBlank()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable {
                                    onEmergencyCall?.invoke(card.emergencyContact)
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Call,
                                    contentDescription = "Emergency Contact",
                                    tint = GeruaBright,
                                    modifier = Modifier.size(11.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "জরুরি: ${card.emergencyContact} (${card.emergencyRelation})",
                                    color = GeruaBright,
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Text(
                            text = "ইস্যু: ${card.issueDate} • হেল্পলাইন: 108 (অ্যাম্বুলেন্স)",
                            color = Color.White.copy(alpha = 0.75f),
                            fontSize = 7.5.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // BOTTOM DISCLAIMER & BARCODE
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.25f))
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "HEALTH POINT • কার্ডটি অবিলম্বে চিকিৎসা সহায়তার জন্য অনুমোদিত",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 7.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Text(
                        text = "||||| ||| |||| ||||||",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}
