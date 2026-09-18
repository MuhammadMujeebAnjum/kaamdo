package com.example.ui.screens.customer

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import com.example.model.PaymentMethod
import com.example.model.TaskCategory
import com.example.ui.components.PakistaniPaymentBadge
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateRequestScreen(
    onBack: () -> Unit,
    onSubmitTask: (
        title: String,
        description: String,
        category: TaskCategory,
        pickupShop: String,
        pickupAddress: String,
        deliveryAddress: String,
        budget: Double,
        reward: Double,
        deadline: String,
        notes: String,
        paymentMethod: PaymentMethod
    ) -> Unit,
    modifier: Modifier = Modifier
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(TaskCategory.BUY_FOR_ME) }
    var pickupShop by remember { mutableStateOf("Al-Fatah Supermarket") }
    var pickupAddress by remember { mutableStateOf("Main Boulevard, Johar Town, Lahore") }
    var deliveryAddress by remember { mutableStateOf("House 14, Block G, Johar Town, Lahore") }
    var productBudgetInput by remember { mutableStateOf("700") }
    var helperRewardInput by remember { mutableStateOf("300") } // Total = 1000 for perfect 150 / 850 split example
    var deadline by remember { mutableStateOf("Within 45 mins") }
    var specialNotes by remember { mutableStateOf("Please check expiry dates and get printed receipt.") }
    var selectedPaymentMethod by remember { mutableStateOf(PaymentMethod.EASYPAISA) }
    var mobileAccountInput by remember { mutableStateOf("0300 1234567") }

    var showPaymentModal by remember { mutableStateOf(false) }
    var isVerifyingPayment by remember { mutableStateOf(false) }
    var paymentVerifiedSuccessfully by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    val budget = productBudgetInput.toDoubleOrNull() ?: 0.0
    val reward = helperRewardInput.toDoubleOrNull() ?: 0.0
    val totalCost = budget + reward
    val owner15Percent = totalCost * 0.15
    val runner85Percent = totalCost * 0.85

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Post New Errand", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceLight)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(BackgroundLight)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Category Selection
            Text("Select Category", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TaskCategory.values().take(3).forEach { cat ->
                    val isSelected = selectedCategory == cat
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedCategory = cat },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) KaamNavy else SurfaceLight
                        ),
                        border = if (isSelected) null else CardDefaults.outlinedCardBorder()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = cat.displayName,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else TextPrimary
                            )
                        }
                    }
                }
            }

            // Task Details Card
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
                    Text("What do you need?", fontWeight = FontWeight.Bold, fontSize = 15.sp)

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Task Title (e.g. Panadol CF & Milk)") },
                        placeholder = { Text("e.g. 2 packs bread & cold drinks") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Detailed Items Description") },
                        placeholder = { Text("Brand names, quantities, sizes...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                }
            }

            // Locations Card
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
                    Text("Locations in Pakistan", fontWeight = FontWeight.Bold, fontSize = 15.sp)

                    OutlinedTextField(
                        value = pickupShop,
                        onValueChange = { pickupShop = it },
                        label = { Text("Store / Shop / Pickup Place") },
                        leadingIcon = { Icon(Icons.Default.Storefront, contentDescription = null, tint = KaamGreenDark) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = pickupAddress,
                        onValueChange = { pickupAddress = it },
                        label = { Text("Pickup Area / Landmark") },
                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = KaamGreenDark) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = deliveryAddress,
                        onValueChange = { deliveryAddress = it },
                        label = { Text("Delivery Address (House / Office)") },
                        leadingIcon = { Icon(Icons.Default.Home, contentDescription = null, tint = KaamNavy) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }

            // Budget & Pricing Breakdown
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
                    Text("Pricing & Budget (PKR)", fontWeight = FontWeight.Bold, fontSize = 15.sp)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = productBudgetInput,
                            onValueChange = { productBudgetInput = it },
                            label = { Text("Item Cost (PKR)") },
                            leadingIcon = { Text("Rs.", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = KaamNavy) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = helperRewardInput,
                            onValueChange = { helperRewardInput = it },
                            label = { Text("Helper Reward (PKR)") },
                            leadingIcon = { Text("Rs.", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = KaamGreenDark) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    HorizontalDivider(color = BorderLight)

                    // Pricing breakdown summary
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Estimated Product Cost", fontSize = 12.sp, color = TextSecondary)
                            Text("Rs. ${budget.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Helper Errand Reward", fontSize = 12.sp, color = TextSecondary)
                            Text("Rs. ${reward.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }

                        HorizontalDivider(color = BorderLight)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Total Amount to Pay", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("Rs. ${totalCost.toInt()}", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = KaamGreenDark)
                        }

                        // Automatic 15% / 85% Split Notice as per doc Section 1 & 5
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            color = KaamGreenLight
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "⚡ Automatic Commission Split (On Task Completion)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = KaamGreenDark
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("• Runner (85%): Rs. ${runner85Percent.toInt()}", fontSize = 11.sp, color = KaamNavy)
                                    Text("• KaamGo Platform (15%): Rs. ${owner15Percent.toInt()}", fontSize = 11.sp, color = KaamNavy)
                                }
                            }
                        }
                    }
                }
            }

            // Payment Method Selection (Section 2)
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
                    Text("Select Payment Method", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text("Secure backend provider integration with instant verification", fontSize = 12.sp, color = TextSecondary)

                    listOf(
                        PaymentMethod.EASYPAISA,
                        PaymentMethod.JAZZCASH,
                        PaymentMethod.CARD_PAYMENT,
                        PaymentMethod.KAAMGO_WALLET,
                        PaymentMethod.CASH_ON_DELIVERY
                    ).forEach { method ->
                        val isSelected = selectedPaymentMethod == method
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { selectedPaymentMethod = method }
                                .border(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) KaamGreenDark else BorderLight,
                                    shape = RoundedCornerShape(10.dp)
                                ),
                            color = if (isSelected) KaamGreenLight.copy(alpha = 0.35f) else SurfaceLight
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { selectedPaymentMethod = method },
                                        colors = RadioButtonDefaults.colors(selectedColor = KaamGreenDark)
                                    )
                                    Column {
                                        Text(method.displayName, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                                        Text(method.subtitle, fontSize = 11.sp, color = TextMuted)
                                    }
                                }
                                PakistaniPaymentBadge(method = method)
                            }
                        }
                    }
                }
            }

            // Delivery Details & Notes
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
                    OutlinedTextField(
                        value = deadline,
                        onValueChange = { deadline = it },
                        label = { Text("Delivery Deadline") },
                        leadingIcon = { Icon(Icons.Default.Schedule, contentDescription = null, tint = KaamBlue) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = specialNotes,
                        onValueChange = { specialNotes = it },
                        label = { Text("Special notes for helper") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                }
            }

            // Post & Pay Button
            Button(
                onClick = {
                    showPaymentModal = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = KaamNavy),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Payment, contentDescription = null, tint = KaamGreen)
                    Text(
                        text = "Pay Rs. ${totalCost.toInt()} & Post Errand",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    // Payment Verification Modal (Section 3 & 10)
    if (showPaymentModal) {
        AlertDialog(
            onDismissRequest = { if (!isVerifyingPayment) showPaymentModal = false },
            title = {
                Text(
                    text = "Verify Payment (${selectedPaymentMethod.displayName})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Amount: Rs. ${totalCost.toInt()}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = KaamGreenDark
                    )

                    if (selectedPaymentMethod == PaymentMethod.EASYPAISA || selectedPaymentMethod == PaymentMethod.JAZZCASH) {
                        OutlinedTextField(
                            value = mobileAccountInput,
                            onValueChange = { mobileAccountInput = it },
                            label = { Text("Mobile Account Number") },
                            placeholder = { Text("0300 1234567") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Text(
                            text = "A payment approval prompt will be initiated to your ${selectedPaymentMethod.displayName} account.",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    } else if (selectedPaymentMethod == PaymentMethod.CARD_PAYMENT) {
                        OutlinedTextField(
                            value = "4242 •••• •••• 1928",
                            onValueChange = {},
                            label = { Text("Card Number") },
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        Text(
                            text = "Payable at delivery or from KaamGo Escrow Balance.",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }

                    if (isVerifyingPayment) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = KaamNavy)
                            Text("Backend verifying transaction...", fontSize = 12.sp, color = KaamNavy)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        isVerifyingPayment = true
                        // Simulate backend verification
                        val finalTitle = if (title.isBlank()) "Everyday Errand Items" else title
                        val finalDesc = if (description.isBlank()) "Standard errands delivery" else description
                        onSubmitTask(
                            finalTitle,
                            finalDesc,
                            selectedCategory,
                            pickupShop,
                            pickupAddress,
                            deliveryAddress,
                            budget,
                            reward,
                            deadline,
                            specialNotes,
                            selectedPaymentMethod
                        )
                        showPaymentModal = false
                        isVerifyingPayment = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = KaamGreenDark),
                    enabled = !isVerifyingPayment
                ) {
                    Text("Confirm & Authorize")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showPaymentModal = false },
                    enabled = !isVerifyingPayment
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}
