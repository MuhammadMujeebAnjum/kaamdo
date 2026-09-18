package com.example.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.UserProfile
import com.example.model.UserRole
import com.example.ui.theme.*

@Composable
fun ProfileScreen(
    user: UserProfile,
    onSwitchRole: (UserRole) -> Unit,
    onViewReviews: () -> Unit,
    onViewEarnings: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Profile Header Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceLight),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(KaamNavy),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = user.name.take(1),
                            color = KaamGreen,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = user.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = KaamGreenLight
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Default.Verified, contentDescription = null, tint = KaamGreenDark, modifier = Modifier.size(14.dp))
                                Text("NADRA CNIC Verified", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = KaamGreenDark)
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = KaamAmberLight
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = KaamAmber, modifier = Modifier.size(14.dp))
                                Text("${user.rating}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF92400E))
                            }
                        }
                    }

                    Text(
                        text = "${user.phone} • ${user.cityZone}",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }
        }

        // Active Role Switcher Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceLight),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "SWITCH CURRENT ROLE",
                        color = TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        UserRole.values().forEach { role ->
                            val isSelected = user.role == role
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { onSwitchRole(role) },
                                color = if (isSelected) KaamNavy else SurfaceVariantLight
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = role.label,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = if (isSelected) Color.White else TextPrimary
                                    )
                                    Text(
                                        text = role.badge,
                                        fontSize = 9.sp,
                                        color = if (isSelected) KaamGreen else TextMuted
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Trust & Verification Details
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceLight),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "TRUST & CREDENTIALS",
                        color = TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("CNIC Number", fontSize = 12.sp, color = TextSecondary)
                        Text(user.cnic, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Vehicle Registered", fontSize = 12.sp, color = TextSecondary)
                        Text(user.vehicle, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Errands Completion Rate", fontSize = 12.sp, color = TextSecondary)
                        Text("${user.completionRate}%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = KaamGreenDark)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Trust Score", fontSize = 12.sp, color = TextSecondary)
                        Text("${user.trustScore} / 100", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = KaamGreenDark)
                    }
                }
            }
        }

        // Quick Navigation Menu
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceLight),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column {
                    ListItem(
                        headlineContent = { Text("Ratings & Reviews", fontWeight = FontWeight.SemiBold) },
                        supportingContent = { Text("Check trust score & feedback") },
                        leadingContent = { Icon(Icons.Default.Star, contentDescription = null, tint = KaamAmber) },
                        trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) },
                        modifier = Modifier.clickable { onViewReviews() }
                    )

                    HorizontalDivider(color = BorderSubtle)

                    ListItem(
                        headlineContent = { Text("Runner Earnings & Commute", fontWeight = FontWeight.SemiBold) },
                        supportingContent = { Text("View weekly breakdown & payouts") },
                        leadingContent = { Icon(Icons.Default.Payments, contentDescription = null, tint = KaamGreenDark) },
                        trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) },
                        modifier = Modifier.clickable { onViewEarnings() }
                    )

                    HorizontalDivider(color = BorderSubtle)

                    ListItem(
                        headlineContent = { Text("Log Out / Change Account", color = KaamRed, fontWeight = FontWeight.SemiBold) },
                        leadingContent = { Icon(Icons.Default.Logout, contentDescription = null, tint = KaamRed) },
                        modifier = Modifier.clickable { onLogout() }
                    )
                }
            }
        }
    }
}
