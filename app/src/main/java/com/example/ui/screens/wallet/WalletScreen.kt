package com.example.ui.screens.wallet

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
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletScreen(
    user: UserProfile,
    transactions: List<WalletTransaction>,
    ownerLedger: List<OwnerWalletLedgerRecord> = emptyList(),
    payouts: List<PayoutRecord> = emptyList(),
    commissions: List<CommissionRecord> = emptyList(),
    runnerEarnings: List<RunnerEarningRecord> = emptyList(),
    ownerBalance: Double = 14250.0,
    ownerTotalCommission: Double = 28500.0,
    ownerPendingBalance: Double = 1280.0,
    onAddFunds: (Double, PaymentMethod) -> Unit = { _, _ -> },
    onWithdraw: (Double, PaymentMethod) -> Boolean = { _, _ -> true },
    onRunnerPayoutRequest: (Double, PaymentMethod, String) -> Boolean = { _, _, _ -> true },
    onOwnerPayoutRequest: (Double, PaymentMethod, String) -> Boolean = { _, _, _ -> true },
    modifier: Modifier = Modifier
) {
    var showTopupDialog by remember { mutableStateOf(false) }
    var showWithdrawDialog by remember { mutableStateOf(false) }
    var amountInput by remember { mutableStateOf("1000") }
    var accountDetailsInput by remember { mutableStateOf("0300 1234567") }
    var selectedMethod by remember { mutableStateOf(PaymentMethod.EASYPAISA) }
    var withdrawError by remember { mutableStateOf<String?>(null) }
    var withdrawSuccess by remember { mutableStateOf(false) }

    val isOwner = user.role == UserRole.ADMIN
    val isRunner = user.role == UserRole.RUNNER

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Hero Balance Card (Customized for Role)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = KaamNavy),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = when {
                                isOwner -> "KAAMGO OWNER COMMISSION WALLET (15%)"
                                isRunner -> "RUNNER EARNINGS WALLET (85%)"
                                else -> "CUSTOMER ESCROW BALANCE"
                            },
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = KaamGreen
                        ) {
                            Text(
                                text = "PKR • Verified",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Column {
                        val displayBalance = if (isOwner) ownerBalance else user.walletBalance
                        Text(
                            text = "Rs. ${displayBalance.toInt()}",
                            color = Color.White,
                            fontSize = 34.sp,
                            fontWeight = FontWeight.Black
                        )

                        if (isOwner) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Total Commission: Rs. ${ownerTotalCommission.toInt()}",
                                    color = KaamGreenLight,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Pending: Rs. ${ownerPendingBalance.toInt()}",
                                    color = KaamAmber,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        } else if (isRunner) {
                            Text(
                                text = "+ Rs. ${user.pendingEarnings.toInt()} in active errands pending OTP delivery",
                                color = KaamAmber,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        } else {
                            Text(
                                text = "Available for instant errand reservation without card OTP hassle",
                                color = Color(0xFFCBD5E1),
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Action Buttons (Top-Up / Withdraw)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (!isOwner) {
                            Button(
                                onClick = { showTopupDialog = true },
                                modifier = Modifier.weight(1f).height(46.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = KaamGreen),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Add Funds", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }

                        Button(
                            onClick = {
                                amountInput = if (isOwner) "5000" else "1000"
                                showWithdrawDialog = true
                            },
                            modifier = Modifier.weight(1f).height(46.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isOwner) KaamGreen else Color.White.copy(alpha = 0.15f)
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.ArrowOutward, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isOwner) "Owner Payout" else "Withdraw",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }

        // 2. Section 5 & 18 Commission Split Architecture Badge
        item {
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
                        Text(
                            text = "AUTOMATIC COMMISSION ENGINE",
                            color = TextMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "15% Owner / 85% Runner",
                            color = KaamGreenDark,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = SurfaceVariantLight
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Customer Payment (e.g. Rs. 1,000)", fontSize = 11.sp, color = TextSecondary)
                                Text("Verified backend PAID status", fontSize = 10.sp, color = TextMuted)
                            }
                            Icon(Icons.Default.ArrowForward, contentDescription = null, tint = KaamNavy, modifier = Modifier.size(16.dp))
                            Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1f)) {
                                Text("Runner (85%): Rs. 850", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = KaamGreenDark)
                                Text("Owner (15%): Rs. 150", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = KaamNavy)
                            }
                        }
                    }
                }
            }
        }

        // 3. Supported Providers in Pakistan (Section 2)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceLight),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "INTEGRATED PAYMENT GATEWAYS (PAKISTAN)",
                        color = TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            PaymentMethod.EASYPAISA,
                            PaymentMethod.JAZZCASH,
                            PaymentMethod.CARD_PAYMENT,
                            PaymentMethod.CASH_ON_DELIVERY
                        ).forEach { method ->
                            PakistaniPaymentBadge(method = method)
                        }
                    }

                    Text(
                        text = "Instant payout settlement to JazzCash and Easypaisa accounts. Idempotent duplicate callback protection active.",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // 4. Withdrawal / Payout History (Section 8 & 9)
        if (payouts.isNotEmpty()) {
            item {
                Text(
                    text = if (isOwner) "OWNER WITHDRAWALS & PAYOUTS" else "RUNNER PAYOUT HISTORY",
                    color = TextMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            items(payouts.take(3)) { po ->
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(KaamGreenLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = KaamGreenDark, modifier = Modifier.size(18.dp))
                            }
                            Column {
                                Text("Payout to ${po.accountDetails}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("Ref: ${po.transactionReference} • ${po.createdAt}", fontSize = 11.sp, color = TextMuted)
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text("- Rs. ${po.amount.toInt()}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = KaamGreenLight
                            ) {
                                Text(po.status, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = KaamGreenDark, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }
                    }
                }
            }
        }

        // 5. Owner Financial Ledger (Section 6 & 13) or Transaction Statement
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isOwner) "OWNER 15% COMMISSION LEDGER" else "TRANSACTION STATEMENT",
                    color = TextMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = if (isOwner) "${ownerLedger.size} ledger entries" else "${transactions.size} records",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
        }

        if (isOwner) {
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(if (entry.amount >= 0) KaamGreenLight else Color(0xFFFFEBEE)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    if (entry.amount >= 0) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                                    contentDescription = null,
                                    tint = if (entry.amount >= 0) KaamGreenDark else KaamRed,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Column {
                                Text(entry.description, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("Txn: ${entry.transactionId} • ${entry.createdAt}", fontSize = 10.sp, color = TextMuted)
                            }
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
        } else {
            items(transactions) { tx ->
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(if (tx.isCredit) KaamGreenLight else Color(0xFFF1F5F9)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    if (tx.isCredit) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                                    contentDescription = null,
                                    tint = if (tx.isCredit) KaamGreenDark else TextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Column {
                                Text(tx.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(tx.detail, fontSize = 11.sp, color = TextSecondary)
                                Text("${tx.date} • ${tx.referenceCode}", fontSize = 10.sp, color = TextMuted)
                            }
                        }

                        Text(
                            text = if (tx.isCredit) "+ Rs. ${tx.amount.toInt()}" else "- Rs. ${tx.amount.toInt()}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = if (tx.isCredit) KaamGreenDark else TextPrimary
                        )
                    }
                }
            }
        }
    }

    // Top-up Dialog
    if (showTopupDialog) {
        AlertDialog(
            onDismissRequest = { showTopupDialog = false },
            title = { Text("Add Funds to Escrow Wallet", fontWeight = FontWeight.Bold, fontSize = 17.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = amountInput,
                        onValueChange = { amountInput = it },
                        label = { Text("Amount (PKR)") },
                        leadingIcon = { Text("Rs.", fontSize = 13.sp, fontWeight = FontWeight.Bold) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("Select Payment Gateway", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    listOf(PaymentMethod.EASYPAISA, PaymentMethod.JAZZCASH, PaymentMethod.CARD_PAYMENT).forEach { method ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { selectedMethod = method },
                            color = if (selectedMethod == method) KaamGreenLight else SurfaceVariantLight
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(method.displayName, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                PakistaniPaymentBadge(method = method)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = amountInput.toDoubleOrNull() ?: 0.0
                        if (amt > 0) {
                            onAddFunds(amt, selectedMethod)
                            showTopupDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = KaamGreen)
                ) {
                    Text("Top-up Now")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTopupDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Withdrawal / Payout Dialog (Section 8 & 9)
    if (showWithdrawDialog) {
        AlertDialog(
            onDismissRequest = { showWithdrawDialog = false },
            title = {
                Text(
                    text = if (isOwner) "Owner Commission Payout" else "Withdraw Runner Earnings",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    val availableBal = if (isOwner) ownerBalance else user.walletBalance
                    Text(
                        text = "Available Balance: Rs. ${availableBal.toInt()}",
                        fontSize = 12.sp,
                        color = KaamGreenDark,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = amountInput,
                        onValueChange = {
                            amountInput = it
                            withdrawError = null
                        },
                        label = { Text("Withdrawal Amount (PKR)") },
                        leadingIcon = { Text("Rs.", fontSize = 13.sp, fontWeight = FontWeight.Bold) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = accountDetailsInput,
                        onValueChange = { accountDetailsInput = it },
                        label = { Text("Account Number / Mobile Number") },
                        placeholder = { Text("0300 1234567") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("Destination Provider", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    listOf(PaymentMethod.EASYPAISA, PaymentMethod.JAZZCASH).forEach { method ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { selectedMethod = method },
                            color = if (selectedMethod == method) KaamGreenLight else SurfaceVariantLight
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(method.displayName, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                PakistaniPaymentBadge(method = method)
                            }
                        }
                    }

                    withdrawError?.let { err ->
                        Text(err, color = KaamRed, fontSize = 11.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = amountInput.toDoubleOrNull() ?: 0.0
                        val availableBal = if (isOwner) ownerBalance else user.walletBalance
                        if (amt <= 0) {
                            withdrawError = "Please enter a valid amount."
                        } else if (amt > availableBal) {
                            withdrawError = "Insufficient balance. Available: Rs. ${availableBal.toInt()}"
                        } else {
                            val success = if (isOwner) {
                                onOwnerPayoutRequest(amt, selectedMethod, accountDetailsInput)
                            } else {
                                onRunnerPayoutRequest(amt, selectedMethod, accountDetailsInput)
                            }
                            if (success) {
                                showWithdrawDialog = false
                            } else {
                                withdrawError = "Payout request failed. Please check balance."
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = KaamNavy)
                ) {
                    Text("Confirm Payout")
                }
            },
            dismissButton = {
                TextButton(onClick = { showWithdrawDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
