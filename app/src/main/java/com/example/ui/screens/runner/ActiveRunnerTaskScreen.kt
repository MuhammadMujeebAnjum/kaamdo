package com.example.ui.screens.runner

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TaskRequest
import com.example.model.TaskStatus
import com.example.ui.components.KaamGoMapView
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveRunnerTaskScreen(
    task: TaskRequest,
    onBack: () -> Unit,
    onAdvanceStatus: (String) -> Unit,
    onVerifyOtp: (String, String) -> Boolean,
    onOpenChat: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var enteredOtp by remember { mutableStateOf("") }
    var otpError by remember { mutableStateOf(false) }
    var receiptAmountInput by remember { mutableStateOf("${task.productBudget.toInt()}") }
    var showReceiptDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Active Errand #${task.id}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Reward: Rs. ${task.helperReward.toInt()} + Reimbursement",
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
            // Live Route Map
            KaamGoMapView(
                task = task,
                heightDp = 200,
                showDetails = true
            )

            // Current Status Action Card
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StatusBadge(status = task.status)
                        Text(
                            text = "Next step in flow",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }

                    when (task.status) {
                        TaskStatus.ACCEPTED -> {
                            Text("1. Head to Pickup Store", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("Navigate to ${task.pickupShopName} (${task.pickupAddress}). Once there, tap below to notify customer.", color = TextSecondary, fontSize = 12.sp)
                            Button(
                                onClick = { onAdvanceStatus(task.id) },
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = KaamNavy),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("I Have Arrived at Store", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                        TaskStatus.AT_PICKUP -> {
                            Text("2. Buy / Collect Items & Upload Receipt", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("Purchase the requested items from ${task.pickupShopName}. Enter the verified printed receipt amount.", color = TextSecondary, fontSize = 12.sp)

                            OutlinedTextField(
                                value = receiptAmountInput,
                                onValueChange = { receiptAmountInput = it },
                                label = { Text("Actual Receipt Total (PKR)") },
                                leadingIcon = { Text("Rs.", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = KaamGreenDark) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Button(
                                onClick = { onAdvanceStatus(task.id) },
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = KaamGreen),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Confirm Purchase & Attach Receipt", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                        TaskStatus.PURCHASED -> {
                            Text("3. Start Delivery Trip", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("Ride to customer's address: ${task.deliveryAddress}.", color = TextSecondary, fontSize = 12.sp)
                            Button(
                                onClick = { onAdvanceStatus(task.id) },
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = KaamNavy),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Start Driving to Customer", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                        TaskStatus.ON_THE_WAY -> {
                            Text("4. Arrived at Customer Doorstep", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("When you reach ${task.deliveryAddress}, notify the customer to meet and hand over the item.", color = TextSecondary, fontSize = 12.sp)
                            Button(
                                onClick = { onAdvanceStatus(task.id) },
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = KaamAmber),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("I Have Arrived at Doorstep", color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                        }
                        TaskStatus.DELIVERED -> {
                            Text("5. Enter Customer 4-digit OTP", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = KaamNavy)
                            Text("Ask customer for the 4-digit delivery code to complete order & unlock your Rs. ${task.helperReward.toInt()} payment.", color = TextSecondary, fontSize = 12.sp)

                            OutlinedTextField(
                                value = enteredOtp,
                                onValueChange = {
                                    enteredOtp = it
                                    otpError = false
                                },
                                label = { Text("Customer 4-Digit OTP") },
                                placeholder = { Text("e.g. ${task.deliveryOtp}") },
                                isError = otpError,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth()
                            )

                            if (otpError) {
                                Text("Incorrect OTP code. Please verify with customer.", color = KaamRed, fontSize = 11.sp)
                            }

                            Button(
                                onClick = {
                                    val success = onVerifyOtp(task.id, enteredOtp)
                                    if (!success) {
                                        otpError = true
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = KaamGreen),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Verify OTP & Release Rs. ${(task.helperReward + (task.actualReceiptAmount ?: task.productBudget)).toInt()}", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                        TaskStatus.COMPLETED -> {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = KaamGreenLight,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = KaamGreenDark, modifier = Modifier.size(36.dp))
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text("Errand Completed Successfully!", fontWeight = FontWeight.Bold, color = KaamGreenDark, fontSize = 15.sp)
                                    Text("Funds of Rs. ${(task.helperReward + (task.actualReceiptAmount ?: task.productBudget)).toInt()} credited to your KaamGo Wallet.", color = TextSecondary, fontSize = 12.sp)
                                }
                            }
                        }
                        else -> {}
                    }
                }
            }

            // Customer Contact & Details
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
                    Text(
                        text = "CUSTOMER CONTACT",
                        color = TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(task.customerName, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimary)
                            Text("Masked Phone: ${task.customerPhone}", color = TextSecondary, fontSize = 12.sp)
                            Text("Rating: ${task.customerRating} ⭐", color = KaamAmber, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }

                        Button(
                            onClick = { onOpenChat(task.id) },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = KaamNavy),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Icon(Icons.Default.Chat, contentDescription = null, tint = KaamGreen, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Chat", color = Color.White, fontSize = 12.sp)
                        }
                    }

                    HorizontalDivider(color = BorderLight)

                    Text("Task Instructions:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    Text(task.specialNotes, fontSize = 12.sp, color = TextSecondary)
                }
            }
        }
    }
}
