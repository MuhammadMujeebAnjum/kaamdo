package com.example.ui.screens.admin

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import com.example.model.*
import com.example.ui.components.PakistaniPaymentBadge
import com.example.ui.components.PaymentStatusBadge
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

data class PendingRunnerVerification(
    val id: String,
    val name: String,
    val phone: String,
    val cnic: String,
    val vehicle: String,
    val zone: String,
    val status: String = "Pending CNIC Review"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    tasks: List<TaskRequest>,
    payments: List<PaymentRecord> = emptyList(),
    commissions: List<CommissionRecord> = emptyList(),
    ownerLedger: List<OwnerWalletLedgerRecord> = emptyList(),
    payouts: List<PayoutRecord> = emptyList(),
    refunds: List<RefundRecord> = emptyList(),
    ownerSettlementAccount: OwnerSettlementAccount = OwnerSettlementAccount(),
    ownerAvailableBalance: Double = 14250.0,
    ownerTotalCommission: Double = 28500.0,
    onSelectTask: (String) -> Unit,
    onTestDuplicateCallback: (String) -> String = { "Idempotency test" },
    onProcessRefund: (String, String) -> Boolean = { _, _ -> true },
    onUpdateOwnerAccount: (String, String, String, String, Boolean) -> Unit = { _, _, _, _, _ -> },
    onWithdrawOwnerProfit: (Double) -> Boolean = { true },
    modifier: Modifier = Modifier
) {
    var pendingRunners by remember {
        mutableStateOf(
            listOf(
                PendingRunnerVerification("h_101", "Zubair Ahmed", "+92 313 4567890", "35201-1289456-1", "Honda CD 70", "DHA Phase 5, Lahore"),
                PendingRunnerVerification("h_102", "Kamran Shah", "+92 322 8901234", "35202-9988123-7", "Suzuki 110", "Gulberg III, Lahore")
            )
        )
    }

    var selectedTab by remember { mutableStateOf(0) } // 0: Owner Vault & Bank, 1: Payment Engine, 2: 15% Ledger, 3: CNIC, 4: Tasks
    var duplicateTestResult by remember { mutableStateOf<String?>(null) }
    var showRefundModal by remember { mutableStateOf(false) }
    var selectedPaymentForRefund by remember { mutableStateOf<PaymentRecord?>(null) }
    var refundReasonInput by remember { mutableStateOf("Item not available in store") }

    // Owner Account Edit Dialog state
    var showEditAccountDialog by remember { mutableStateOf(false) }
    var editTitle by remember(ownerSettlementAccount) { mutableStateOf(ownerSettlementAccount.accountTitle) }
    var editBank by remember(ownerSettlementAccount) { mutableStateOf(ownerSettlementAccount.bankOrWalletName) }
    var editAccNum by remember(ownerSettlementAccount) { mutableStateOf(ownerSettlementAccount.accountNumber) }
    var editIban by remember(ownerSettlementAccount) { mutableStateOf(ownerSettlementAccount.ibanNumber) }
    var editAutoDaily by remember(ownerSettlementAccount) { mutableStateOf(ownerSettlementAccount.autoTransferDaily) }

    // Owner Withdraw Dialog state
    var showOwnerWithdrawDialog by remember { mutableStateOf(false) }
    var withdrawAmountInput by remember { mutableStateOf("5000") }
    var withdrawSuccessMsg by remember { mutableStateOf<String?>(null) }
    var withdrawErrorMsg by remember { mutableStateOf<String?>(null) }

    val totalGmv = tasks.sumOf { it.totalCustomerCost }
    val calculatedOwner15Percent = totalGmv * 0.15
    val calculatedRunner85Percent = totalGmv * 0.85

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Admin Executive Header (Owner Mode)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = KaamNavy),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = KaamGreen, modifier = Modifier.size(20.dp))
                            Text(
                                text = "KAAMGO OWNER EXECUTIVE PANEL",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = KaamGreen
                        ) {
                            Text(
                                text = "👑 App Owner",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Total GMV", fontSize = 11.sp, color = Color(0xFF94A3B8))
                            Text("Rs. ${totalGmv.toInt()}", fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color.White)
                        }

                        Column {
                            Text("Your 15% Profit", fontSize = 11.sp, color = Color(0xFF94A3B8))
                            Text("Rs. ${ownerAvailableBalance.toInt()}", fontSize = 20.sp, fontWeight = FontWeight.Black, color = KaamGreen)
                        }

                        Column {
                            Text("Runner (85%)", fontSize = 11.sp, color = Color(0xFF94A3B8))
                            Text("Rs. ${calculatedRunner85Percent.toInt()}", fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color(0xFF38BDF8))
                        }
                    }
                }
            }
        }

        // Sub Navigation Tabs
        item {
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = SurfaceLight,
                contentColor = KaamNavy,
                edgePadding = 0.dp
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("👑 Owner Vault & Bank", fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Payment Split (15/85)", fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Ledger History", fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal) }
                )
                Tab(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    text = { Text("CNIC (${pendingRunners.size})", fontWeight = if (selectedTab == 3) FontWeight.Bold else FontWeight.Normal) }
                )
                Tab(
                    selected = selectedTab == 4,
                    onClick = { selectedTab = 4 },
                    text = { Text("Tasks (${tasks.size})", fontWeight = if (selectedTab == 4) FontWeight.Bold else FontWeight.Normal) }
                )
            }
        }

        // =========================================================================
        // TAB 0: OWNER VAULT & SETTLEMENT BANK ACCOUNT (UNIQUE FOR OWNER)
        // =========================================================================
        if (selectedTab == 0) {
            // 1. Owner 15% Profit Withdrawal Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceLight),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "OWNER 15% PROFIT VAULT",
                                color = TextMuted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Surface(shape = RoundedCornerShape(6.dp), color = KaamGreenLight) {
                                Text(
                                    "Ready to Withdraw",
                                    color = KaamGreenDark,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Column {
                            Text(
                                text = "Rs. ${ownerAvailableBalance.toInt()}",
                                fontSize = 34.sp,
                                fontWeight = FontWeight.Black,
                                color = KaamNavy
                            )
                            Text(
                                text = "Lifetime Platform Commission Earned: Rs. ${ownerTotalCommission.toInt()}",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }

                        Button(
                            onClick = {
                                withdrawAmountInput = ownerAvailableBalance.toInt().coerceAtLeast(1000).toString()
                                withdrawErrorMsg = null
                                withdrawSuccessMsg = null
                                showOwnerWithdrawDialog = true
                            },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = KaamGreen),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.AccountBalance, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Withdraw Profit to My Account", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }

            // 2. Owner Receiving Payment Method Card (Unique Settlement Account)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceLight),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = KaamNavy, modifier = Modifier.size(20.dp))
                                Text(
                                    text = "YOUR RECEIVING ACCOUNT (مالک کا اکاؤنٹ)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = KaamNavy,
                                    letterSpacing = 0.5.sp
                                )
                            }
                            IconButton(onClick = { showEditAccountDialog = true }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = KaamGreenDark, modifier = Modifier.size(20.dp))
                            }
                        }

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = SurfaceVariantLight
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Account Title:", fontSize = 12.sp, color = TextSecondary)
                                    Text(ownerSettlementAccount.accountTitle, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Bank / Provider:", fontSize = 12.sp, color = TextSecondary)
                                    Text(ownerSettlementAccount.bankOrWalletName, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = KaamGreenDark)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Account / Mobile #:", fontSize = 12.sp, color = TextSecondary)
                                    Text(ownerSettlementAccount.accountNumber, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                }
                                if (ownerSettlementAccount.ibanNumber.isNotBlank()) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("IBAN:", fontSize = 12.sp, color = TextSecondary)
                                        Text(ownerSettlementAccount.ibanNumber, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                                    }
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Verification Status:", fontSize = 12.sp, color = TextSecondary)
                                    Text("✓ Verified Owner Destination", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = KaamGreenDark)
                                }
                            }
                        }

                        OutlinedButton(
                            onClick = { showEditAccountDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Change Receiving Account (اکاؤنٹ تبدیل کریں)", fontSize = 12.sp)
                        }
                    }
                }
            }

            // 3. Calm Step-by-Step Explanation in Roman Urdu (خاص رہنمائی)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = KaamGreenLight.copy(alpha = 0.6f)),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.Lightbulb, contentDescription = null, tint = KaamGreenDark, modifier = Modifier.size(20.dp))
                            Text(
                                text = "Sukoon Sa Samjhein — Owner Ka Simple Flow",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = KaamGreenDark
                            )
                        }

                        Text(
                            text = "1. Customer Payment:\nJab customer koi samaan mangwata hai aur EasyPaisa, JazzCash ya Card se payment karta hai, to customer ki payment KaamGo ke safe escrow system mein aati hai.\n\n" +
                                   "2. 15% Automatic Commission:\nJaise hi Runner samaan pohancha kar customer se OTP verify karwata hai, system bina kisi manual hisaab ke 15% seedha aapke is Owner Vault mein daal deta hai aur 85% Runner ke wallet mein.\n\n" +
                                   "3. Withdraw Lena:\nAapka flow runner jaisa nahi hai. Aap app ke Maalik (Owner) hain. Aapne upar apna EasyPaisa/JazzCash/Bank account ek baar save kar lena hai. Phir 'Withdraw Profit' dabate hi aapka 15% munafa aapke account mein transfer ho jata hai.",
                            fontSize = 12.sp,
                            color = TextPrimary,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }

        // =========================================================================
        // TAB 1: PAYMENT SPLIT ENGINE (15% / 85%) & TESTING
        // =========================================================================
        if (selectedTab == 1) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceLight),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("AUTOMATIC 15% / 85% SPLIT VERIFICATION", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            color = KaamNavyLight.copy(alpha = 0.08f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("Customer Payment (PAID verified via EasyPaisa/JazzCash)", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = KaamNavy)
                                Text("↓ Order complete on OTP delivery", fontSize = 11.sp, color = TextMuted)
                                Text("Owner Profit (15%): Automatically credited to your Owner Vault", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = KaamGreenDark)
                                Text("Runner Share (85%): Automatically credited to Runner wallet", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = KaamNavy)
                            }
                        }
                    }
                }
            }

            // Duplicate Payment Protection Test
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceLight),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.Shield, contentDescription = null, tint = KaamGreenDark, modifier = Modifier.size(18.dp))
                            Text("DUPLICATE PAYMENT PROTECTION (IDEMPOTENCY)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                        }
                        Button(
                            onClick = { duplicateTestResult = onTestDuplicateCallback("EP-942817") },
                            colors = ButtonDefaults.buttonColors(containerColor = KaamNavy),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Test Duplicate Webhook Callback (EP-942817)", fontSize = 12.sp)
                        }
                        duplicateTestResult?.let { res ->
                            Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp), color = KaamGreenLight) {
                                Text(res, color = KaamGreenDark, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(10.dp))
                            }
                        }
                    }
                }
            }

            // Payments Database Table
            item {
                Text("ALL CUSTOMER PAYMENTS (payments table)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextMuted)
            }

            items(payments) { p ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceLight),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Payment #${p.id} • ${p.customerName}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            PaymentStatusBadge(status = p.status)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            PakistaniPaymentBadge(method = p.paymentMethod)
                            Text("Rs. ${p.amount.toInt()}", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = KaamNavy)
                        }
                        if (p.status == PaymentStatus.PAID) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                TextButton(
                                    onClick = {
                                        selectedPaymentForRefund = p
                                        showRefundModal = true
                                    },
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text("Simulate Refund & Reversal", fontSize = 11.sp, color = KaamRed)
                                }
                            }
                        }
                    }
                }
            }
        }

        // =========================================================================
        // TAB 2: 15% OWNER LEDGER HISTORY
        // =========================================================================
        if (selectedTab == 2) {
            item {
                Text("15% OWNER COMMISSION & PAYOUT LEDGER", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextMuted)
            }

            items(ownerLedger) { entry ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceLight),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(entry.description, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text("Type: ${entry.type} • Txn: ${entry.transactionId}", fontSize = 10.sp, color = TextMuted)
                        }
                        Text(
                            text = if (entry.amount >= 0) "+ Rs. ${entry.amount.toInt()}" else "- Rs. ${(-entry.amount).toInt()}",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp,
                            color = if (entry.amount >= 0) KaamGreenDark else KaamRed
                        )
                    }
                }
            }
        }

        // =========================================================================
        // TAB 3: RUNNER CNIC VERIFICATION
        // =========================================================================
        if (selectedTab == 3) {
            items(pendingRunners) { runner ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceLight),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(runner.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Surface(shape = RoundedCornerShape(6.dp), color = KaamAmberLight) {
                                Text("Pending NADRA", color = Color(0xFFB45309), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(6.dp, 2.dp))
                            }
                        }
                        Text("CNIC: ${runner.cnic} • Vehicle: ${runner.vehicle}", fontSize = 12.sp, color = TextSecondary)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { pendingRunners = pendingRunners.filter { it.id != runner.id } },
                                colors = ButtonDefaults.buttonColors(containerColor = KaamGreen),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Approve Runner", fontSize = 12.sp)
                            }
                            OutlinedButton(
                                onClick = { pendingRunners = pendingRunners.filter { it.id != runner.id } },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Reject", fontSize = 12.sp, color = KaamRed)
                            }
                        }
                    }
                }
            }
        }

        // =========================================================================
        // TAB 4: TASKS OVERVIEW
        // =========================================================================
        if (selectedTab == 4) {
            items(tasks) { t ->
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { onSelectTask(t.id) },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceLight),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("#${t.id}: ${t.title}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            StatusBadge(status = t.status)
                        }
                        Text("Total: Rs. ${t.totalCustomerCost.toInt()} (Runner: Rs. ${t.runnerEarningAmount.toInt()} | Owner: Rs. ${t.ownerCommissionAmount.toInt()})", fontSize = 11.sp, color = KaamGreenDark, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }

    // DIALOG: EDIT OWNER RECEIVING ACCOUNT (مالک کا اکاؤنٹ تبدیل کریں)
    if (showEditAccountDialog) {
        AlertDialog(
            onDismissRequest = { showEditAccountDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.AccountBalance, contentDescription = null, tint = KaamNavy)
                    Text("Owner Receiving Account", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "Enter the account where you want to receive your 15% platform profit.",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )

                    OutlinedTextField(
                        value = editTitle,
                        onValueChange = { editTitle = it },
                        label = { Text("Account Title / Name") },
                        placeholder = { Text("Mujeeb Anjum") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("Select Bank / Wallet Provider", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("EasyPaisa", "JazzCash", "Meezan Bank", "HBL").forEach { p ->
                            FilterChip(
                                selected = editBank.equals(p, ignoreCase = true),
                                onClick = { editBank = p },
                                label = { Text(p, fontSize = 11.sp) }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = editAccNum,
                        onValueChange = { editAccNum = it },
                        label = { Text("Account / Mobile Number") },
                        placeholder = { Text("0300 1234567") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = editIban,
                        onValueChange = { editIban = it },
                        label = { Text("IBAN (Optional for Bank Transfer)") },
                        placeholder = { Text("PK12MEZN0001234567890101") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateOwnerAccount(editTitle, editBank, editAccNum, editIban, editAutoDaily)
                        showEditAccountDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = KaamNavy)
                ) {
                    Text("Save Account")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditAccountDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // DIALOG: OWNER PROFIT WITHDRAWAL (15% منافع نکالیں)
    if (showOwnerWithdrawDialog) {
        AlertDialog(
            onDismissRequest = { showOwnerWithdrawDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.PriceCheck, contentDescription = null, tint = KaamGreenDark)
                    Text("Withdraw Owner 15% Profit", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = KaamGreenLight
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("Transfer Destination:", fontSize = 11.sp, color = TextSecondary)
                            Text(
                                "${ownerSettlementAccount.accountTitle} (${ownerSettlementAccount.bankOrWalletName})",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = KaamNavy
                            )
                            Text(ownerSettlementAccount.accountNumber, fontSize = 12.sp, color = TextPrimary)
                        }
                    }

                    OutlinedTextField(
                        value = withdrawAmountInput,
                        onValueChange = {
                            withdrawAmountInput = it
                            withdrawErrorMsg = null
                        },
                        label = { Text("Withdrawal Amount (PKR)") },
                        leadingIcon = { Text("Rs.", fontWeight = FontWeight.Bold, fontSize = 13.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        "Available Balance: Rs. ${ownerAvailableBalance.toInt()}",
                        fontSize = 11.sp,
                        color = KaamGreenDark,
                        fontWeight = FontWeight.Bold
                    )

                    withdrawErrorMsg?.let { err ->
                        Text(err, color = KaamRed, fontSize = 11.sp)
                    }

                    withdrawSuccessMsg?.let { msg ->
                        Text(msg, color = KaamGreenDark, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = withdrawAmountInput.toDoubleOrNull() ?: 0.0
                        if (amt <= 0) {
                            withdrawErrorMsg = "Please enter a valid amount."
                        } else if (amt > ownerAvailableBalance) {
                            withdrawErrorMsg = "Amount exceeds available balance (Rs. ${ownerAvailableBalance.toInt()})."
                        } else {
                            val success = onWithdrawOwnerProfit(amt)
                            if (success) {
                                showOwnerWithdrawDialog = false
                            } else {
                                withdrawErrorMsg = "Transaction failed. Please retry."
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = KaamGreen)
                ) {
                    Text("Confirm Transfer")
                }
            },
            dismissButton = {
                TextButton(onClick = { showOwnerWithdrawDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // REFUND REVERSAL DIALOG
    if (showRefundModal && selectedPaymentForRefund != null) {
        val p = selectedPaymentForRefund!!
        AlertDialog(
            onDismissRequest = { showRefundModal = false },
            title = { Text("Process Customer Refund (Sec 12)", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Payment ID: ${p.id} • Rs. ${p.amount.toInt()}", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text("Reverses the 15% commission in Owner Ledger and returns funds to customer.", fontSize = 11.sp, color = TextSecondary)
                    OutlinedTextField(
                        value = refundReasonInput,
                        onValueChange = { refundReasonInput = it },
                        label = { Text("Refund Reason") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onProcessRefund(p.id, refundReasonInput)
                        showRefundModal = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = KaamRed)
                ) {
                    Text("Execute Reversal")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRefundModal = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
