package com.example

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.data.KaamGoRepository
import com.example.model.*
import com.example.ui.components.KaamGoHeader
import com.example.ui.screens.admin.AdminDashboardScreen
import com.example.ui.screens.auth.AuthScreen
import com.example.ui.screens.chat.TaskChatScreen
import com.example.ui.screens.customer.CreateRequestScreen
import com.example.ui.screens.customer.CustomerHomeScreen
import com.example.ui.screens.customer.RequestTrackingScreen
import com.example.ui.screens.notifications.NotificationsScreen
import com.example.ui.screens.profile.ProfileScreen
import com.example.ui.screens.profile.RatingsReviewScreen
import com.example.ui.screens.runner.ActiveRunnerTaskScreen
import com.example.ui.screens.runner.RunnerEarningsScreen
import com.example.ui.screens.runner.RunnerHomeScreen
import com.example.ui.screens.wallet.WalletScreen
import com.example.ui.theme.*

sealed class AppDestination {
    object Main : AppDestination()
    object CreateRequest : AppDestination()
    data class RequestTracking(val taskId: String) : AppDestination()
    data class ActiveRunnerTask(val taskId: String) : AppDestination()
    data class TaskChat(val taskId: String) : AppDestination()
    object RatingsReviews : AppDestination()
    object RunnerEarnings : AppDestination()
    object Notifications : AppDestination()
    object Auth : AppDestination()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KaamGoApp(
    repository: KaamGoRepository = remember { KaamGoRepository() }
) {
    val currentUser by repository.currentUser.collectAsState()
    val tasks by repository.tasks.collectAsState()
    val taskChats by repository.taskChats.collectAsState()
    val transactions by repository.transactions.collectAsState()
    val notifications by repository.notifications.collectAsState()
    val reviews by repository.reviews.collectAsState()
    val isRunnerOnline by repository.isRunnerOnline.collectAsState()
    val runnerRouteOrigin by repository.runnerRouteOrigin.collectAsState()
    val runnerRouteDestination by repository.runnerRouteDestination.collectAsState()

    // Payment System State Flows (15% Owner / 85% Runner Engine)
    val payments by repository.payments.collectAsState()
    val commissions by repository.commissions.collectAsState()
    val runnerEarnings by repository.runnerEarnings.collectAsState()
    val ownerWalletLedger by repository.ownerLedger.collectAsState()
    val payouts by repository.payouts.collectAsState()
    val refunds by repository.refunds.collectAsState()
    val ownerWalletBalance by repository.ownerWalletBalance.collectAsState()
    val ownerTotalCommission by repository.ownerTotalCommission.collectAsState()
    val ownerPendingBalance by repository.ownerPendingBalance.collectAsState()
    val ownerSettlementAccount by repository.ownerSettlementAccount.collectAsState()

    var currentDestination by remember { mutableStateOf<AppDestination>(AppDestination.Main) }
    var selectedBottomNavIndex by remember { mutableStateOf(0) }
    var showRoleSwitchDialog by remember { mutableStateOf(false) }

    // Floating or modal dialog for quick switching roles
    if (showRoleSwitchDialog) {
        AlertDialog(
            onDismissRequest = { showRoleSwitchDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.SwapHoriz, contentDescription = null, tint = KaamGreen)
                    Text("Switch KaamGo Role", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Experience KaamGo from all 3 perspectives:",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )

                    UserRole.values().forEach { role ->
                        val isSelected = currentUser.role == role
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    repository.switchRole(role)
                                    selectedBottomNavIndex = 0
                                    currentDestination = AppDestination.Main
                                    showRoleSwitchDialog = false
                                },
                            color = if (isSelected) KaamNavy else SurfaceVariantLight
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = when (role) {
                                        UserRole.CUSTOMER -> Icons.Default.ShoppingBag
                                        UserRole.RUNNER -> Icons.Default.TwoWheeler
                                        UserRole.ADMIN -> Icons.Default.AdminPanelSettings
                                    },
                                    contentDescription = null,
                                    tint = if (isSelected) KaamGreen else TextSecondary
                                )
                                Column {
                                    Text(
                                        text = role.label,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = if (isSelected) Color.White else TextPrimary
                                    )
                                    Text(
                                        text = role.badge,
                                        fontSize = 11.sp,
                                        color = if (isSelected) Color(0xFFCBD5E1) else TextMuted
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showRoleSwitchDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (currentDestination == AppDestination.Main) {
                KaamGoHeader(
                    currentRole = currentUser.role,
                    unreadNotificationCount = notifications.count { !it.isRead },
                    onRoleSwitchClick = { showRoleSwitchDialog = true },
                    onNotificationClick = { currentDestination = AppDestination.Notifications }
                )
            }
        },
        bottomBar = {
            if (currentDestination == AppDestination.Main) {
                NavigationBar(
                    containerColor = SurfaceLight,
                    tonalElevation = 8.dp,
                    windowInsets = WindowInsets.navigationBars
                ) {
                    when (currentUser.role) {
                        UserRole.CUSTOMER -> {
                            NavigationBarItem(
                                selected = selectedBottomNavIndex == 0,
                                onClick = { selectedBottomNavIndex = 0 },
                                icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                                label = { Text("Home", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = KaamNavy,
                                    selectedTextColor = KaamNavy,
                                    indicatorColor = KaamGreenLight
                                )
                            )
                            NavigationBarItem(
                                selected = selectedBottomNavIndex == 1,
                                onClick = { currentDestination = AppDestination.CreateRequest },
                                icon = {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(KaamNavy),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = "Post Errand", tint = KaamGreen, modifier = Modifier.size(20.dp))
                                    }
                                },
                                label = { Text("Post", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                            )
                            NavigationBarItem(
                                selected = selectedBottomNavIndex == 2,
                                onClick = { selectedBottomNavIndex = 2 },
                                icon = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = "Wallet") },
                                label = { Text("Wallet", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = KaamNavy,
                                    selectedTextColor = KaamNavy,
                                    indicatorColor = KaamGreenLight
                                )
                            )
                            NavigationBarItem(
                                selected = selectedBottomNavIndex == 3,
                                onClick = { selectedBottomNavIndex = 3 },
                                icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                                label = { Text("Profile", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = KaamNavy,
                                    selectedTextColor = KaamNavy,
                                    indicatorColor = KaamGreenLight
                                )
                            )
                        }
                        UserRole.RUNNER -> {
                            NavigationBarItem(
                                selected = selectedBottomNavIndex == 0,
                                onClick = { selectedBottomNavIndex = 0 },
                                icon = { Icon(Icons.Default.Explore, contentDescription = "Errands") },
                                label = { Text("Errands", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = KaamNavy,
                                    selectedTextColor = KaamNavy,
                                    indicatorColor = KaamGreenLight
                                )
                            )
                            NavigationBarItem(
                                selected = selectedBottomNavIndex == 1,
                                onClick = {
                                    val active = tasks.firstOrNull { it.helperId == currentUser.id && it.status != TaskStatus.COMPLETED && it.status != TaskStatus.CANCELLED }
                                    if (active != null) {
                                        currentDestination = AppDestination.ActiveRunnerTask(active.id)
                                    } else {
                                        selectedBottomNavIndex = 0
                                    }
                                },
                                icon = { Icon(Icons.Default.TwoWheeler, contentDescription = "Active") },
                                label = { Text("Active Run", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = KaamNavy,
                                    selectedTextColor = KaamNavy,
                                    indicatorColor = KaamGreenLight
                                )
                            )
                            NavigationBarItem(
                                selected = selectedBottomNavIndex == 2,
                                onClick = { selectedBottomNavIndex = 2 },
                                icon = { Icon(Icons.Default.Payments, contentDescription = "Earnings") },
                                label = { Text("Earnings", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = KaamNavy,
                                    selectedTextColor = KaamNavy,
                                    indicatorColor = KaamGreenLight
                                )
                            )
                            NavigationBarItem(
                                selected = selectedBottomNavIndex == 3,
                                onClick = { selectedBottomNavIndex = 3 },
                                icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                                label = { Text("Profile", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = KaamNavy,
                                    selectedTextColor = KaamNavy,
                                    indicatorColor = KaamGreenLight
                                )
                            )
                        }
                        UserRole.ADMIN -> {
                            NavigationBarItem(
                                selected = selectedBottomNavIndex == 0,
                                onClick = { selectedBottomNavIndex = 0 },
                                icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
                                label = { Text("Ops Panel", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = KaamNavy,
                                    selectedTextColor = KaamNavy,
                                    indicatorColor = KaamGreenLight
                                )
                            )
                            NavigationBarItem(
                                selected = selectedBottomNavIndex == 1,
                                onClick = { selectedBottomNavIndex = 1 },
                                icon = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = "Wallet") },
                                label = { Text("Financials", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = KaamNavy,
                                    selectedTextColor = KaamNavy,
                                    indicatorColor = KaamGreenLight
                                )
                            )
                            NavigationBarItem(
                                selected = selectedBottomNavIndex == 2,
                                onClick = { selectedBottomNavIndex = 2 },
                                icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                                label = { Text("Profile", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = KaamNavy,
                                    selectedTextColor = KaamNavy,
                                    indicatorColor = KaamGreenLight
                                )
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val dest = currentDestination) {
                is AppDestination.Main -> {
                    when (currentUser.role) {
                        UserRole.CUSTOMER -> {
                            when (selectedBottomNavIndex) {
                                0 -> CustomerHomeScreen(
                                    user = currentUser,
                                    tasks = tasks,
                                    onCreateTaskClick = { currentDestination = AppDestination.CreateRequest },
                                    onTrackTaskClick = { taskId -> currentDestination = AppDestination.RequestTracking(taskId) },
                                    onChatTaskClick = { taskId -> currentDestination = AppDestination.TaskChat(taskId) }
                                )
                                2 -> WalletScreen(
                                    user = currentUser,
                                    transactions = transactions,
                                    ownerLedger = ownerWalletLedger,
                                    payouts = payouts,
                                    commissions = commissions,
                                    runnerEarnings = runnerEarnings,
                                    ownerBalance = ownerWalletBalance,
                                    ownerTotalCommission = ownerTotalCommission,
                                    ownerPendingBalance = ownerPendingBalance,
                                    onAddFunds = { amt, method -> repository.addFundsToWallet(amt, method) },
                                    onWithdraw = { amt, method -> repository.withdrawFunds(amt, method) },
                                    onRunnerPayoutRequest = { amt, method, acc -> repository.requestRunnerPayout(amt, method, acc) },
                                    onOwnerPayoutRequest = { amt, method, acc -> repository.requestOwnerPayout(amt, method, acc) }
                                )
                                3 -> ProfileScreen(
                                    user = currentUser,
                                    onSwitchRole = { role -> repository.switchRole(role) },
                                    onViewReviews = { currentDestination = AppDestination.RatingsReviews },
                                    onViewEarnings = { currentDestination = AppDestination.RunnerEarnings },
                                    onLogout = { currentDestination = AppDestination.Auth }
                                )
                            }
                        }
                        UserRole.RUNNER -> {
                            when (selectedBottomNavIndex) {
                                0 -> RunnerHomeScreen(
                                    user = currentUser,
                                    isOnline = isRunnerOnline,
                                    onToggleOnline = { repository.setRunnerOnline(it) },
                                    routeOrigin = runnerRouteOrigin,
                                    routeDestination = runnerRouteDestination,
                                    onUpdateRoute = { o, d -> repository.updateRunnerRoute(o, d) },
                                    tasks = tasks,
                                    onAcceptTask = { taskId ->
                                        repository.acceptTask(taskId)
                                        currentDestination = AppDestination.ActiveRunnerTask(taskId)
                                    },
                                    onViewActiveTask = { taskId -> currentDestination = AppDestination.ActiveRunnerTask(taskId) },
                                    onViewEarnings = { currentDestination = AppDestination.RunnerEarnings }
                                )
                                2 -> WalletScreen(
                                    user = currentUser,
                                    transactions = transactions,
                                    ownerLedger = ownerWalletLedger,
                                    payouts = payouts,
                                    commissions = commissions,
                                    runnerEarnings = runnerEarnings,
                                    ownerBalance = ownerWalletBalance,
                                    ownerTotalCommission = ownerTotalCommission,
                                    ownerPendingBalance = ownerPendingBalance,
                                    onAddFunds = { amt, method -> repository.addFundsToWallet(amt, method) },
                                    onWithdraw = { amt, method -> repository.withdrawFunds(amt, method) },
                                    onRunnerPayoutRequest = { amt, method, acc -> repository.requestRunnerPayout(amt, method, acc) },
                                    onOwnerPayoutRequest = { amt, method, acc -> repository.requestOwnerPayout(amt, method, acc) }
                                )
                                3 -> ProfileScreen(
                                    user = currentUser,
                                    onSwitchRole = { role -> repository.switchRole(role) },
                                    onViewReviews = { currentDestination = AppDestination.RatingsReviews },
                                    onViewEarnings = { currentDestination = AppDestination.RunnerEarnings },
                                    onLogout = { currentDestination = AppDestination.Auth }
                                )
                            }
                        }
                        UserRole.ADMIN -> {
                            when (selectedBottomNavIndex) {
                                0 -> AdminDashboardScreen(
                                    tasks = tasks,
                                    payments = payments,
                                    commissions = commissions,
                                    ownerLedger = ownerWalletLedger,
                                    payouts = payouts,
                                    refunds = refunds,
                                    ownerSettlementAccount = ownerSettlementAccount,
                                    ownerAvailableBalance = ownerWalletBalance,
                                    ownerTotalCommission = ownerTotalCommission,
                                    onSelectTask = { taskId -> currentDestination = AppDestination.RequestTracking(taskId) },
                                    onTestDuplicateCallback = { ref -> repository.testDuplicateCallback(ref) },
                                    onProcessRefund = { pid, rsn -> repository.processRefund(pid, rsn) },
                                    onUpdateOwnerAccount = { title, bank, acc, iban, auto ->
                                        repository.updateOwnerSettlementAccount(title, bank, acc, iban, auto)
                                    },
                                    onWithdrawOwnerProfit = { amt ->
                                        repository.withdrawOwnerProfitToSavedAccount(amt)
                                    }
                                )
                                1 -> WalletScreen(
                                    user = currentUser,
                                    transactions = transactions,
                                    ownerLedger = ownerWalletLedger,
                                    payouts = payouts,
                                    commissions = commissions,
                                    runnerEarnings = runnerEarnings,
                                    ownerBalance = ownerWalletBalance,
                                    ownerTotalCommission = ownerTotalCommission,
                                    ownerPendingBalance = ownerPendingBalance,
                                    onAddFunds = { amt, method -> repository.addFundsToWallet(amt, method) },
                                    onWithdraw = { amt, method -> repository.withdrawFunds(amt, method) },
                                    onRunnerPayoutRequest = { amt, method, acc -> repository.requestRunnerPayout(amt, method, acc) },
                                    onOwnerPayoutRequest = { amt, method, acc -> repository.requestOwnerPayout(amt, method, acc) }
                                )
                                2 -> ProfileScreen(
                                    user = currentUser,
                                    onSwitchRole = { role -> repository.switchRole(role) },
                                    onViewReviews = { currentDestination = AppDestination.RatingsReviews },
                                    onViewEarnings = { currentDestination = AppDestination.RunnerEarnings },
                                    onLogout = { currentDestination = AppDestination.Auth }
                                )
                            }
                        }
                    }
                }
                is AppDestination.CreateRequest -> {
                    CreateRequestScreen(
                        onBack = { currentDestination = AppDestination.Main },
                        onSubmitTask = { title, desc, cat, shop, pAddr, dAddr, budget, reward, deadline, notes, payMethod ->
                            val newTaskId = repository.createTask(title, desc, cat, shop, pAddr, dAddr, budget, reward, deadline, notes, payMethod)
                            currentDestination = AppDestination.RequestTracking(newTaskId)
                        }
                    )
                }
                is AppDestination.RequestTracking -> {
                    val task = tasks.find { it.id == dest.taskId } ?: tasks.first()
                    RequestTrackingScreen(
                        task = task,
                        onBack = { currentDestination = AppDestination.Main },
                        onOpenChat = { taskId -> currentDestination = AppDestination.TaskChat(taskId) },
                        onAdvanceStatus = { taskId -> repository.advanceTaskStatus(taskId) },
                        onVerifyOtp = { taskId, otp -> repository.verifyDeliveryOtp(taskId, otp) }
                    )
                }
                is AppDestination.ActiveRunnerTask -> {
                    val task = tasks.find { it.id == dest.taskId } ?: tasks.first()
                    ActiveRunnerTaskScreen(
                        task = task,
                        onBack = { currentDestination = AppDestination.Main },
                        onAdvanceStatus = { taskId -> repository.advanceTaskStatus(taskId) },
                        onVerifyOtp = { taskId, otp -> repository.verifyDeliveryOtp(taskId, otp) },
                        onOpenChat = { taskId -> currentDestination = AppDestination.TaskChat(taskId) }
                    )
                }
                is AppDestination.TaskChat -> {
                    val task = tasks.find { it.id == dest.taskId } ?: tasks.first()
                    val chatList = taskChats[task.id] ?: emptyList()
                    TaskChatScreen(
                        task = task,
                        currentRole = currentUser.role,
                        messages = chatList,
                        onSendMessage = { text, isReceipt, amt ->
                            repository.sendChatMessage(task.id, text, isFromMe = true, isReceiptProof = isReceipt, receiptAmount = amt)
                        },
                        onBack = { currentDestination = AppDestination.Main }
                    )
                }
                is AppDestination.RatingsReviews -> {
                    RatingsReviewScreen(
                        reviews = reviews,
                        onAddReview = { rating, comment, tags ->
                            repository.submitReview("TASK-8041", rating, comment, tags)
                        },
                        onBack = { currentDestination = AppDestination.Main }
                    )
                }
                is AppDestination.RunnerEarnings -> {
                    RunnerEarningsScreen(
                        user = currentUser,
                        runnerEarnings = runnerEarnings,
                        onBack = { currentDestination = AppDestination.Main },
                        onWithdrawClick = {
                            selectedBottomNavIndex = 2
                            currentDestination = AppDestination.Main
                        }
                    )
                }
                is AppDestination.Notifications -> {
                    NotificationsScreen(
                        notifications = notifications,
                        onNotificationClick = { taskId ->
                            if (taskId != null) {
                                currentDestination = AppDestination.RequestTracking(taskId)
                            }
                        },
                        onBack = { currentDestination = AppDestination.Main }
                    )
                }
                is AppDestination.Auth -> {
                    AuthScreen(
                        currentRole = currentUser.role,
                        onLoginSuccess = { role ->
                            repository.switchRole(role)
                            currentDestination = AppDestination.Main
                        }
                    )
                }
            }
        }
    }
}
