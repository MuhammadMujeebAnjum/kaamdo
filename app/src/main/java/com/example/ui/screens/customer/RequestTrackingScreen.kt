package com.example.ui.screens.customer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TaskRequest
import com.example.model.TaskStatus
import com.example.ui.components.KaamGoMapView
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestTrackingScreen(
    task: TaskRequest,
    onBack: () -> Unit,
    onOpenChat: (String) -> Unit,
    onAdvanceStatus: (String) -> Unit,
    onVerifyOtp: (String, String) -> Boolean,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var otpInput by remember { mutableStateOf("") }
    var otpError by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Tracking Errand #${task.id}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = task.status.label,
                            color = KaamGreenDark,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { onOpenChat(task.id) }) {
                        Icon(Icons.Default.Chat, contentDescription = "Chat", tint = KaamNavy)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceLight,
                    titleContentColor = TextPrimary
                )
            )
        },
        containerColor = BackgroundLight
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Live Interactive City Map
            KaamGoMapView(
                task = task,
                heightDp = 220,
                showDetails = true
            )

            // Payment & Commission Settlement Card (Section 1, 3, 4, 5)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceLight),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.Payments, contentDescription = null, tint = KaamGreenDark, modifier = Modifier.size(18.dp))
                            Text(
                                text = "PAYMENT & ESCROW STATUS",
                                color = TextMuted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                        com.example.ui.components.PaymentStatusBadge(status = task.paymentStatus)
                    }

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        color = SurfaceVariantLight
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Payment Method", fontSize = 12.sp, color = TextSecondary)
                                com.example.ui.components.PakistaniPaymentBadge(method = task.paymentMethod)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Gateway Reference", fontSize = 12.sp, color = TextSecondary)
                                Text(task.gatewayTransactionId, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = KaamNavy)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Customer Paid Total", fontSize = 12.sp, color = TextSecondary)
                                Text("Rs. ${task.totalCustomerCost.toInt()}", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = KaamGreenDark)
                            }
                        }
                    }

                    // Automatic 15% Owner / 85% Runner Split Card
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        color = if (task.isCommissionSettled) KaamGreenLight else KaamAmberLight.copy(alpha = 0.5f)
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (task.isCommissionSettled) "✓ Commission Settled Automatically" else "⚡ Automatic Split (Pending OTP)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (task.isCommissionSettled) KaamGreenDark else Color(0xFFB45309)
                                )
                                Text(
                                    text = if (task.isCommissionSettled) "SETTLED" else "HELD IN ESCROW",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (task.isCommissionSettled) KaamGreenDark else Color(0xFFB45309)
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("• Runner Share (85%):", fontSize = 12.sp, color = TextPrimary)
                                Text("Rs. ${task.runnerEarningAmount.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = KaamGreenDark)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("• KaamGo Platform Fee (15%):", fontSize = 12.sp, color = TextPrimary)
                                Text("Rs. ${task.ownerCommissionAmount.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = KaamNavy)
                            }
                        }
                    }
                }
            }

            // OTP Delivery Shield Card (Critical Security Feature from PDF)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceLight),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = KaamAmber, modifier = Modifier.size(20.dp))
                            Text(
                                text = "DELIVERY VERIFICATION OTP",
                                color = TextMuted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = KaamGreenLight
                        ) {
                            Text(
                                text = "Safe Escrow",
                                color = KaamGreenDark,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    // Large OTP Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(KaamNavy)
                            .padding(vertical = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = task.deliveryOtp,
                                color = Color.White,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 8.sp
                            )
                            Text(
                                text = "Share with runner only upon doorstep arrival",
                                color = Color(0xFFCBD5E1),
                                fontSize = 11.sp
                            )
                        }
                    }

                    Text(
                        text = "Once the helper delivers your item and you verify the receipt, give them this 4-digit code to release escrow funds.",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }
            }

            // Runner Information Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceLight),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "ASSIGNED HELPER / RUNNER",
                        color = TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(KaamNavy),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.TwoWheeler,
                                    contentDescription = null,
                                    tint = KaamGreen,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = task.helperName ?: "Searching nearby runner...",
                                    color = TextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(Icons.Default.Star, contentDescription = null, tint = KaamAmber, modifier = Modifier.size(14.dp))
                                    Text(
                                        text = "${task.helperRating} (38 errands) • Verified CNIC",
                                        color = TextSecondary,
                                        fontSize = 11.sp
                                    )
                                }
                                Text(
                                    text = task.helperVehicle ?: "Motorbike",
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        // Chat Action Button
                        Button(
                            onClick = { onOpenChat(task.id) },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = KaamNavy),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp), tint = KaamGreen)
                                Text("Chat", fontSize = 12.sp, color = Color.White)
                            }
                        }
                    }
                }
            }

            // Receipt & Item Proof Card (if available)
            if (task.actualReceiptAmount != null || task.status.stepIndex >= TaskStatus.PURCHASED.stepIndex) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceLight),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = KaamGreen, modifier = Modifier.size(18.dp))
                                Text(
                                    text = "PURCHASE RECEIPT PROOF",
                                    color = TextMuted,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            }
                            Text(
                                text = "Verified",
                                color = KaamGreenDark,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            color = SurfaceVariantLight
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Store / Merchant", color = TextSecondary, fontSize = 12.sp)
                                    Text(task.pickupShopName, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Actual Receipt Total", color = TextSecondary, fontSize = 12.sp)
                                    Text("Rs. ${task.actualReceiptAmount?.toInt() ?: task.productBudget.toInt()}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = KaamGreenDark)
                                }
                                Text(
                                    text = task.receiptPhotoNote ?: "Receipt photo uploaded by runner.",
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }

            // Errand Lifecycle Timeline Steps
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceLight),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "ORDER PROGRESS TIMELINE",
                        color = TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    val steps = listOf(
                        Triple("Task Posted", "Finding nearby verified runner", 0),
                        Triple("Runner Accepted", "${task.helperName ?: "Helper"} confirmed errand", 2),
                        Triple("At Pickup Shop", "Arrived at ${task.pickupShopName}", 3),
                        Triple("Purchased & Receipt Attached", "Rs. ${task.productBudget.toInt()} collected", 4),
                        Triple("On the Way to You", "Riding along route to destination", 5),
                        Triple("Delivered at Doorstep", "Awaiting OTP confirmation", 6),
                        Triple("Completed & Funds Released", "Escrow settled successfully", 7)
                    )

                    steps.forEachIndexed { index, (stepTitle, stepSubtitle, stepIdx) ->
                        val isDone = task.status.stepIndex >= stepIdx
                        val isCurrent = task.status.stepIndex == stepIdx

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(if (isDone) KaamGreen else SurfaceVariantLight),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isDone) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                } else {
                                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(TextMuted))
                                }
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = stepTitle,
                                    color = if (isDone) TextPrimary else TextMuted,
                                    fontSize = 13.sp,
                                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium
                                )
                                Text(
                                    text = stepSubtitle,
                                    color = if (isCurrent) KaamGreenDark else TextMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }

            // Demo Simulation Controls (advance status)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = KaamNavyLight.copy(alpha = 0.08f)),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.Bolt, contentDescription = null, tint = KaamAmber, modifier = Modifier.size(18.dp))
                        Text(
                            text = "SIMULATE RUNNER PROGRESS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = KaamNavy
                        )
                    }
                    Text(
                        text = "Advance runner stages to test the complete lifecycle: At Pickup → Purchased → On the Way → Delivered → OTP Confirmation.",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )

                    Button(
                        onClick = { onAdvanceStatus(task.id) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = KaamNavy),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Advance Next Step (${task.status.label})", fontSize = 12.sp, color = Color.White)
                    }
                }
            }
        }
    }
}
