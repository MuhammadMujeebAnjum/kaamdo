package com.example.data

import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

class KaamGoRepository {

    // Current logged-in user profile
    private val _currentUser = MutableStateFlow(
        UserProfile(
            id = "usr_001",
            name = "Hamza Farooq",
            phone = "+92 300 1234567",
            email = "hamza.farooq@example.com",
            role = UserRole.CUSTOMER,
            cnic = "35202-4829103-5",
            isCnicVerified = true,
            rating = 4.9f,
            reviewCount = 42,
            completedTasks = 27,
            completionRate = 99.1f,
            trustScore = 98,
            vehicle = "Honda CG 125 (Motorbike)",
            cityZone = "Johar Town, Lahore",
            walletBalance = 3250.0,
            pendingEarnings = 850.0
        )
    )
    val currentUser: StateFlow<UserProfile> = _currentUser.asStateFlow()

    // Task list
    private val _tasks = MutableStateFlow<List<TaskRequest>>(createInitialTasks())
    val tasks: StateFlow<List<TaskRequest>> = _tasks.asStateFlow()

    // Chat messages keyed by taskId
    private val _taskChats = MutableStateFlow<Map<String, List<ChatMessage>>>(createInitialChats())
    val taskChats: StateFlow<Map<String, List<ChatMessage>>> = _taskChats.asStateFlow()

    // General user transactions
    private val _transactions = MutableStateFlow<List<WalletTransaction>>(createInitialTransactions())
    val transactions: StateFlow<List<WalletTransaction>> = _transactions.asStateFlow()

    // Notifications
    private val _notifications = MutableStateFlow<List<NotificationItem>>(createInitialNotifications())
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    // Reviews list
    private val _reviews = MutableStateFlow<List<ErrandReview>>(createInitialReviews())
    val reviews: StateFlow<List<ErrandReview>> = _reviews.asStateFlow()

    // Runner route filter ("I'm Going There Anyway")
    private val _runnerRouteOrigin = MutableStateFlow("Johar Town, Lahore")
    val runnerRouteOrigin: StateFlow<String> = _runnerRouteOrigin.asStateFlow()

    private val _runnerRouteDestination = MutableStateFlow("Gulberg III, Lahore")
    val runnerRouteDestination: StateFlow<String> = _runnerRouteDestination.asStateFlow()

    private val _isRunnerOnline = MutableStateFlow(true)
    val isRunnerOnline: StateFlow<Boolean> = _isRunnerOnline.asStateFlow()

    // =========================================================================
    // KAAMGO PAYMENT SYSTEM (Documentation Sections 1 to 18)
    // =========================================================================

    // 1. payments table (Section 13)
    private val _payments = MutableStateFlow<List<PaymentRecord>>(createInitialPayments())
    val payments: StateFlow<List<PaymentRecord>> = _payments.asStateFlow()

    // 2. commissions table (15% Owner Commission - Section 5 & 13)
    private val _commissions = MutableStateFlow<List<CommissionRecord>>(createInitialCommissions())
    val commissions: StateFlow<List<CommissionRecord>> = _commissions.asStateFlow()

    // 3. runner_earnings table (85% Runner Earning - Section 5 & 13)
    private val _runnerEarnings = MutableStateFlow<List<RunnerEarningRecord>>(createInitialRunnerEarnings())
    val runnerEarnings: StateFlow<List<RunnerEarningRecord>> = _runnerEarnings.asStateFlow()

    // 4. owner_wallet_ledger table (Section 6 & 13)
    private val _ownerLedger = MutableStateFlow<List<OwnerWalletLedgerRecord>>(createInitialOwnerLedger())
    val ownerLedger: StateFlow<List<OwnerWalletLedgerRecord>> = _ownerLedger.asStateFlow()

    // 5. payouts table (Section 8, 9 & 13)
    private val _payouts = MutableStateFlow<List<PayoutRecord>>(createInitialPayouts())
    val payouts: StateFlow<List<PayoutRecord>> = _payouts.asStateFlow()

    // 6. refunds table (Section 12 & 13)
    private val _refunds = MutableStateFlow<List<RefundRecord>>(createInitialRefunds())
    val refunds: StateFlow<List<RefundRecord>> = _refunds.asStateFlow()

    // Owner Wallet Balances (Section 6)
    private val _ownerWalletBalance = MutableStateFlow(14250.0) // Available Commission
    val ownerWalletBalance: StateFlow<Double> = _ownerWalletBalance.asStateFlow()

    private val _ownerTotalCommission = MutableStateFlow(28500.0) // Total historical 15%
    val ownerTotalCommission: StateFlow<Double> = _ownerTotalCommission.asStateFlow()

    private val _ownerPendingBalance = MutableStateFlow(1280.0) // Unsettled active tasks
    val ownerPendingBalance: StateFlow<Double> = _ownerPendingBalance.asStateFlow()

    // Duplicate Payment Protection Set (Section 11)
    private val _processedTransactionIds = mutableSetOf<String>(
        "EP-942817", "JC-551029", "EP-882194"
    )

    // Dedicated Owner Receiving/Settlement Account (Owner Profit Payouts)
    private val _ownerSettlementAccount = MutableStateFlow(
        OwnerSettlementAccount(
            accountTitle = "Mujeeb Anjum (App Owner)",
            bankOrWalletName = "EasyPaisa",
            accountNumber = "0300 1234567",
            ibanNumber = "PK12MEZN0001234567890101",
            autoTransferDaily = false,
            isVerified = true
        )
    )
    val ownerSettlementAccount: StateFlow<OwnerSettlementAccount> = _ownerSettlementAccount.asStateFlow()

    fun updateOwnerSettlementAccount(title: String, bank: String, accNum: String, iban: String, autoTransfer: Boolean) {
        _ownerSettlementAccount.value = OwnerSettlementAccount(
            accountTitle = title,
            bankOrWalletName = bank,
            accountNumber = accNum,
            ibanNumber = iban,
            autoTransferDaily = autoTransfer,
            isVerified = true
        )
    }

    fun withdrawOwnerProfitToSavedAccount(amount: Double): Boolean {
        val account = _ownerSettlementAccount.value
        val method = when {
            account.bankOrWalletName.contains("Jazz", ignoreCase = true) -> PaymentMethod.JAZZCASH
            account.bankOrWalletName.contains("Easy", ignoreCase = true) -> PaymentMethod.EASYPAISA
            else -> PaymentMethod.CARD_PAYMENT
        }
        val details = "${account.accountTitle} (${account.bankOrWalletName} - ${account.accountNumber})"
        return requestOwnerPayout(amount, method, details)
    }

    fun switchRole(role: UserRole) {
        _currentUser.update { it.copy(role = role) }
    }

    fun setRunnerOnline(online: Boolean) {
        _isRunnerOnline.value = online
    }

    fun updateRunnerRoute(origin: String, destination: String) {
        _runnerRouteOrigin.value = origin
        _runnerRouteDestination.value = destination
    }

    // Customer Task Creation with Payment Integration
    fun createTask(
        title: String,
        description: String,
        category: TaskCategory,
        pickupShopName: String,
        pickupAddress: String,
        deliveryAddress: String,
        productBudget: Double,
        helperReward: Double,
        deadlineTime: String,
        specialNotes: String,
        paymentMethod: PaymentMethod = PaymentMethod.EASYPAISA
    ): String {
        val newId = "TASK-${(1000..9999).random()}"
        val otp = (1000..9999).random().toString()
        val totalCost = productBudget + helperReward

        // Backend Payment Verification Simulation (Section 10)
        val prefix = when (paymentMethod) {
            PaymentMethod.EASYPAISA -> "EP"
            PaymentMethod.JAZZCASH -> "JC"
            PaymentMethod.CARD_PAYMENT -> "CARD"
            PaymentMethod.CASH_ON_DELIVERY -> "COD"
            PaymentMethod.KAAMGO_WALLET -> "KW"
        }
        val gatewayTxnId = "$prefix-${(100000..999999).random()}"

        val newTask = TaskRequest(
            id = newId,
            title = title,
            description = description,
            category = category,
            customerId = _currentUser.value.id,
            customerName = _currentUser.value.name,
            customerPhone = _currentUser.value.phone,
            customerRating = _currentUser.value.rating,
            pickupShopName = pickupShopName,
            pickupAddress = pickupAddress,
            pickupLocationX = 0.22f + ((0..40).random() / 100f),
            pickupLocationY = 0.28f + ((0..40).random() / 100f),
            deliveryAddress = deliveryAddress,
            deliveryLocationX = 0.65f + ((0..30).random() / 100f),
            deliveryLocationY = 0.60f + ((0..30).random() / 100f),
            productBudget = productBudget,
            helperReward = helperReward,
            deadlineTime = deadlineTime,
            deliveryOtp = otp,
            distanceKm = 2.0 + ((5..45).random() / 10.0),
            routeMatchPercent = (82..98).random(),
            status = TaskStatus.POSTED,
            specialNotes = specialNotes,
            createdAtFormatted = "Just now",
            paymentStatus = PaymentStatus.PAID,
            paymentMethod = paymentMethod,
            gatewayTransactionId = gatewayTxnId,
            isCommissionSettled = false
        )

        _tasks.update { listOf(newTask) + it }

        // Record in payments table (Section 13)
        val paymentRecord = PaymentRecord(
            id = "PAY-${(100000..999999).random()}",
            taskId = newId,
            customerId = _currentUser.value.id,
            customerName = _currentUser.value.name,
            amount = totalCost,
            paymentMethod = paymentMethod,
            status = PaymentStatus.PAID,
            gatewayTransactionId = gatewayTxnId,
            createdAt = "Just now",
            updatedAt = "Just now"
        )
        _payments.update { listOf(paymentRecord) + it }

        // Deduct from customer wallet if KaamGo Wallet selected
        if (paymentMethod == PaymentMethod.KAAMGO_WALLET) {
            _currentUser.update { it.copy(walletBalance = maxOf(0.0, it.walletBalance - totalCost)) }
        }

        // Add initial system chat message
        _taskChats.update { current ->
            val chatList = listOf(
                ChatMessage(
                    id = UUID.randomUUID().toString(),
                    taskId = newId,
                    senderName = "KaamGo Security",
                    senderRole = UserRole.ADMIN,
                    messageText = "Payment of Rs. ${totalCost.toInt()} verified via ${paymentMethod.displayName} (Ref: $gatewayTxnId). Funds held safely in Escrow until OTP delivery confirmation.",
                    time = "Just now",
                    isFromMe = false,
                    isSystem = true
                )
            )
            current + (newId to chatList)
        }

        // Notification
        _notifications.update {
            listOf(
                NotificationItem(
                    id = UUID.randomUUID().toString(),
                    title = "Payment Verified & Errand Posted",
                    body = "Rs. ${totalCost.toInt()} escrow secured via ${paymentMethod.displayName}. Matching nearby runners.",
                    timeAgo = "Just now",
                    category = "PAYMENT",
                    relatedTaskId = newId
                )
            ) + it
        }

        return newId
    }

    fun acceptTask(taskId: String) {
        val user = _currentUser.value
        _tasks.update { list ->
            list.map { task ->
                if (task.id == taskId) {
                    task.copy(
                        helperId = user.id,
                        helperName = user.name,
                        helperPhone = user.phone,
                        helperRating = user.rating,
                        helperVehicle = user.vehicle,
                        status = TaskStatus.ACCEPTED,
                        runnerCurrentX = task.pickupLocationX - 0.08f,
                        runnerCurrentY = task.pickupLocationY - 0.05f
                    )
                } else task
            }
        }

        sendChatMessage(taskId, "As-salamu alaykum! I have accepted your task and am heading to ${getTask(taskId)?.pickupShopName ?: "the shop"}.", false)

        _notifications.update {
            listOf(
                NotificationItem(
                    id = UUID.randomUUID().toString(),
                    title = "Task Accepted!",
                    body = "Runner ${user.name} is on their way to pick up the item.",
                    timeAgo = "Just now",
                    category = "ORDER",
                    relatedTaskId = taskId
                )
            ) + it
        }
    }

    fun advanceTaskStatus(taskId: String) {
        val currentTask = getTask(taskId) ?: return
        val nextStatus = when (currentTask.status) {
            TaskStatus.POSTED -> TaskStatus.ACCEPTED
            TaskStatus.OFFERS -> TaskStatus.ACCEPTED
            TaskStatus.ACCEPTED -> TaskStatus.AT_PICKUP
            TaskStatus.AT_PICKUP -> TaskStatus.PURCHASED
            TaskStatus.PURCHASED -> TaskStatus.ON_THE_WAY
            TaskStatus.ON_THE_WAY -> TaskStatus.DELIVERED
            TaskStatus.DELIVERED -> TaskStatus.COMPLETED
            TaskStatus.COMPLETED -> TaskStatus.COMPLETED
            TaskStatus.CANCELLED -> TaskStatus.CANCELLED
        }

        _tasks.update { list ->
            list.map { task ->
                if (task.id == taskId) {
                    val actualAmount = if (nextStatus == TaskStatus.PURCHASED && task.actualReceiptAmount == null) {
                        task.productBudget
                    } else task.actualReceiptAmount

                    val (newX, newY) = when (nextStatus) {
                        TaskStatus.AT_PICKUP -> Pair(task.pickupLocationX, task.pickupLocationY)
                        TaskStatus.PURCHASED -> Pair(task.pickupLocationX + 0.02f, task.pickupLocationY + 0.02f)
                        TaskStatus.ON_THE_WAY -> Pair(
                            (task.pickupLocationX + task.deliveryLocationX) / 2f,
                            (task.pickupLocationY + task.deliveryLocationY) / 2f
                        )
                        TaskStatus.DELIVERED -> Pair(task.deliveryLocationX, task.deliveryLocationY)
                        TaskStatus.COMPLETED -> Pair(task.deliveryLocationX, task.deliveryLocationY)
                        else -> Pair(task.runnerCurrentX, task.runnerCurrentY)
                    }

                    task.copy(
                        status = nextStatus,
                        actualReceiptAmount = actualAmount,
                        runnerCurrentX = newX,
                        runnerCurrentY = newY
                    )
                } else task
            }
        }

        when (nextStatus) {
            TaskStatus.AT_PICKUP -> {
                sendChatMessage(taskId, "I have arrived at ${currentTask.pickupShopName}. Finding your items now.", false)
            }
            TaskStatus.PURCHASED -> {
                sendChatMessage(taskId, "Items purchased! Receipt total verified at Rs. ${currentTask.productBudget.toInt()}.", false, isReceiptProof = true, receiptAmount = currentTask.productBudget)
            }
            TaskStatus.ON_THE_WAY -> {
                sendChatMessage(taskId, "Leaving shop now. Riding towards your delivery address.", false)
            }
            TaskStatus.DELIVERED -> {
                sendChatMessage(taskId, "I have arrived at your door! Please share your 4-digit OTP: ${currentTask.deliveryOtp} to complete delivery.", false)
            }
            TaskStatus.COMPLETED -> {
                // Trigger Automatic Commission Calculation (15% Owner / 85% Runner)
                settleCommissionOnTaskCompletion(taskId)
            }
            else -> {}
        }
    }

    // =========================================================================
    // AUTOMATIC COMMISSION SETTLEMENT ENGINE (Sections 1, 3, 5, 6, 7, 11)
    // =========================================================================
    fun settleCommissionOnTaskCompletion(taskId: String): Boolean {
        val task = getTask(taskId) ?: return false

        // Section 4: Only backend-verified PAID payments are settled
        if (task.paymentStatus != PaymentStatus.PAID) {
            return false
        }

        // Section 11: Duplicate Payment Protection / Idempotency Check
        if (task.isCommissionSettled || _processedTransactionIds.contains(task.gatewayTransactionId)) {
            // Already processed -> Ignore to prevent duplicate 15% and 85%
            return false
        }

        _processedTransactionIds.add(task.gatewayTransactionId)

        val eligibleAmount = task.totalCustomerCost

        // Section 5: Automatic 15% / 85% Calculation Formula
        val ownerCommission = eligibleAmount * 0.15 // 15%
        val runnerEarning = eligibleAmount * 0.85    // 85%

        val timestamp = "Today, just now"
        val txnId = "TXN-${UUID.randomUUID().toString().take(8).uppercase()}"

        // 1. Record in commissions table (15% Owner - Section 13)
        val commissionRecord = CommissionRecord(
            id = "COMM-${(100000..999999).random()}",
            taskId = task.id,
            transactionId = txnId,
            eligibleAmount = eligibleAmount,
            commissionRate = 0.15,
            commissionAmount = ownerCommission,
            status = "SETTLED",
            createdAt = timestamp
        )
        _commissions.update { listOf(commissionRecord) + it }

        // 2. Record in runner_earnings table (85% Runner - Section 13)
        val runnerRecord = RunnerEarningRecord(
            id = "EARN-${(100000..999999).random()}",
            taskId = task.id,
            runnerId = task.helperId ?: "hlp_001",
            runnerName = task.helperName ?: "Runner Bilal",
            eligibleAmount = eligibleAmount,
            earningRate = 0.85,
            earningAmount = runnerEarning,
            status = "CREDITED",
            createdAt = timestamp
        )
        _runnerEarnings.update { listOf(runnerRecord) + it }

        // 3. Record in owner_wallet_ledger table (Section 6 & 13)
        val ledgerRecord = OwnerWalletLedgerRecord(
            id = "LEDGER-${(100000..999999).random()}",
            transactionId = txnId,
            taskId = task.id,
            amount = ownerCommission,
            type = "COMMISSION_CREDIT",
            status = "SETTLED",
            description = "15% platform commission on Errand #${task.id} (Total: Rs. ${eligibleAmount.toInt()})",
            createdAt = timestamp
        )
        _ownerLedger.update { listOf(ledgerRecord) + it }

        // 4. Update Owner Wallet (Section 6)
        _ownerWalletBalance.update { it + ownerCommission }
        _ownerTotalCommission.update { it + ownerCommission }

        // 5. Update Runner Wallet (Section 7)
        _currentUser.update {
            it.copy(
                walletBalance = it.walletBalance + runnerEarning,
                completedTasks = it.completedTasks + 1
            )
        }

        // 6. Record general transaction for runner ledger
        val runnerTx = WalletTransaction(
            id = txnId,
            title = "Task Earning (85% Split)",
            detail = "Errand #${task.id}: ${task.title}",
            amount = runnerEarning,
            isCredit = true,
            method = task.paymentMethod,
            date = timestamp,
            referenceCode = "PK-85-${task.gatewayTransactionId}"
        )
        _transactions.update { listOf(runnerTx) + it }

        // Mark task commission settled
        _tasks.update { list ->
            list.map {
                if (it.id == taskId) it.copy(isCommissionSettled = true, status = TaskStatus.COMPLETED) else it
            }
        }

        // Chat notification
        sendChatMessage(
            taskId,
            "🎉 Task Completed & Escrow Settled!\n• Runner Earning (85%): Rs. ${runnerEarning.toInt()}\n• KaamGo Fee (15%): Rs. ${ownerCommission.toInt()}",
            true,
            isSystem = true
        )

        // System notification
        _notifications.update {
            listOf(
                NotificationItem(
                    id = UUID.randomUUID().toString(),
                    title = "Automatic Settlement Complete 💰",
                    body = "Errand #${task.id} settled: 85% (Rs. ${runnerEarning.toInt()}) credited to Runner, 15% (Rs. ${ownerCommission.toInt()}) to Owner.",
                    timeAgo = "Just now",
                    category = "PAYMENT",
                    relatedTaskId = taskId
                )
            ) + it
        }

        return true
    }

    // Section 8: Owner Withdrawal Request
    fun requestOwnerPayout(amount: Double, paymentMethod: PaymentMethod, accountDetails: String): Boolean {
        if (_ownerWalletBalance.value < amount || amount <= 0) return false

        _ownerWalletBalance.update { it - amount }

        val timestamp = "Today, just now"
        val ref = "PAYOUT-OWNER-${UUID.randomUUID().toString().take(6).uppercase()}"

        val payoutRecord = PayoutRecord(
            id = "PO-${(100000..999999).random()}",
            userId = "admin_owner_01",
            userName = "KaamGo Platform Owner",
            role = UserRole.ADMIN,
            amount = amount,
            paymentMethod = paymentMethod,
            accountDetails = accountDetails,
            status = "PAID",
            transactionReference = ref,
            createdAt = timestamp,
            completedAt = timestamp
        )
        _payouts.update { listOf(payoutRecord) + it }

        val ledgerDebit = OwnerWalletLedgerRecord(
            id = "LEDGER-${(100000..999999).random()}",
            transactionId = ref,
            taskId = "PAYOUT",
            amount = -amount,
            type = "PAYOUT_DEBIT",
            status = "SETTLED",
            description = "Owner commission payout to $accountDetails (${paymentMethod.displayName})",
            createdAt = timestamp
        )
        _ownerLedger.update { listOf(ledgerDebit) + it }

        _notifications.update {
            listOf(
                NotificationItem(
                    id = UUID.randomUUID().toString(),
                    title = "Owner Payout Processed 🏦",
                    body = "Rs. ${amount.toInt()} transferred to $accountDetails via ${paymentMethod.displayName}.",
                    timeAgo = "Just now",
                    category = "PAYMENT"
                )
            ) + it
        }

        return true
    }

    // Section 9: Runner Withdrawal Request
    fun requestRunnerPayout(amount: Double, paymentMethod: PaymentMethod, accountDetails: String): Boolean {
        if (_currentUser.value.walletBalance < amount || amount <= 0) return false

        _currentUser.update { it.copy(walletBalance = it.walletBalance - amount) }

        val timestamp = "Today, just now"
        val ref = "PAYOUT-RUNNER-${UUID.randomUUID().toString().take(6).uppercase()}"

        val payoutRecord = PayoutRecord(
            id = "PO-${(100000..999999).random()}",
            userId = _currentUser.value.id,
            userName = _currentUser.value.name,
            role = UserRole.RUNNER,
            amount = amount,
            paymentMethod = paymentMethod,
            accountDetails = accountDetails,
            status = "PAID",
            transactionReference = ref,
            createdAt = timestamp,
            completedAt = timestamp
        )
        _payouts.update { listOf(payoutRecord) + it }

        val newTx = WalletTransaction(
            id = "TX-${(100000..999999).random()}",
            title = "Withdrawal to ${paymentMethod.displayName}",
            detail = "Sent to $accountDetails",
            amount = amount,
            isCredit = false,
            method = paymentMethod,
            date = timestamp,
            referenceCode = ref
        )
        _transactions.update { listOf(newTx) + it }

        _notifications.update {
            listOf(
                NotificationItem(
                    id = UUID.randomUUID().toString(),
                    title = "Runner Withdrawal Successful 💸",
                    body = "Rs. ${amount.toInt()} sent to your ${paymentMethod.displayName} account ($accountDetails).",
                    timeAgo = "Just now",
                    category = "PAYMENT"
                )
            ) + it
        }

        return true
    }

    // Section 12: Refund & Financial Reversal
    fun processRefund(paymentId: String, reason: String): Boolean {
        val payment = _payments.value.find { it.id == paymentId } ?: return false
        if (payment.status == PaymentStatus.REFUNDED) return false

        val timestamp = "Today, just now"
        val refundRecord = RefundRecord(
            id = "REF-${(100000..999999).random()}",
            paymentId = payment.id,
            taskId = payment.taskId,
            amount = payment.amount,
            reason = reason,
            status = "COMPLETED",
            createdAt = timestamp,
            completedAt = timestamp
        )
        _refunds.update { listOf(refundRecord) + it }

        // Update payment status (original transaction is never deleted - audit intact)
        _payments.update { list ->
            list.map { if (it.id == paymentId) it.copy(status = PaymentStatus.REFUNDED) else it }
        }

        // Financial Reversal if commission was already distributed
        val commission = _commissions.value.find { it.taskId == payment.taskId }
        if (commission != null) {
            val reversalAmount = commission.commissionAmount
            _ownerWalletBalance.update { maxOf(0.0, it - reversalAmount) }

            val reversalLedger = OwnerWalletLedgerRecord(
                id = "LEDGER-${(100000..999999).random()}",
                transactionId = "REV-${payment.gatewayTransactionId}",
                taskId = payment.taskId,
                amount = -reversalAmount,
                type = "REFUND_REVERSAL",
                status = "REVERSED",
                description = "Reversal of 15% commission due to refund: $reason",
                createdAt = timestamp
            )
            _ownerLedger.update { listOf(reversalLedger) + it }
        }

        // Refund customer balance
        _currentUser.update { it.copy(walletBalance = it.walletBalance + payment.amount) }

        _notifications.update {
            listOf(
                NotificationItem(
                    id = UUID.randomUUID().toString(),
                    title = "Refund Processed: Rs. ${payment.amount.toInt()}",
                    body = "Reason: $reason. Returned to customer wallet balance.",
                    timeAgo = "Just now",
                    category = "PAYMENT",
                    relatedTaskId = payment.taskId
                )
            ) + it
        }

        return true
    }

    // Section 11: Test duplicate callback protection
    fun testDuplicateCallback(gatewayTxnId: String): String {
        return if (_processedTransactionIds.contains(gatewayTxnId)) {
            "Idempotency Protection Active: Transaction $gatewayTxnId was already processed. Duplicate webhook ignored. No duplicate 15% / 85% split distributed."
        } else {
            _processedTransactionIds.add(gatewayTxnId)
            "Transaction $gatewayTxnId received and registered."
        }
    }

    fun verifyDeliveryOtp(taskId: String, enteredOtp: String): Boolean {
        val task = getTask(taskId) ?: return false
        if (task.deliveryOtp.trim() == enteredOtp.trim()) {
            advanceTaskStatus(taskId)
            return true
        }
        return false
    }

    fun sendChatMessage(
        taskId: String,
        text: String,
        isFromMe: Boolean,
        isSystem: Boolean = false,
        isReceiptProof: Boolean = false,
        receiptAmount: Double? = null
    ) {
        val message = ChatMessage(
            id = UUID.randomUUID().toString(),
            taskId = taskId,
            senderName = if (isSystem) "KaamGo Security" else if (isFromMe) _currentUser.value.name else (getTask(taskId)?.helperName ?: "Runner Bilal"),
            senderRole = if (isSystem) UserRole.ADMIN else if (isFromMe) _currentUser.value.role else UserRole.RUNNER,
            messageText = text,
            time = "Now",
            isFromMe = isFromMe,
            isSystem = isSystem,
            isReceiptProof = isReceiptProof,
            receiptAmount = receiptAmount
        )

        _taskChats.update { currentMap ->
            val list = currentMap[taskId] ?: emptyList()
            currentMap + (taskId to (list + message))
        }
    }

    fun addFundsToWallet(amount: Double, method: PaymentMethod) {
        _currentUser.update { it.copy(walletBalance = it.walletBalance + amount) }
        val newTx = WalletTransaction(
            id = "TX-${(100000..999999).random()}",
            title = "Wallet Top-up",
            detail = "Added funds via ${method.displayName}",
            amount = amount,
            isCredit = true,
            method = method,
            date = "Today, just now",
            referenceCode = "PK-${UUID.randomUUID().toString().take(8).uppercase()}"
        )
        _transactions.update { listOf(newTx) + it }
    }

    fun withdrawFunds(amount: Double, method: PaymentMethod): Boolean {
        return requestRunnerPayout(amount, method, "0300-1234567")
    }

    fun submitReview(taskId: String, rating: Float, comment: String, tags: List<String>) {
        val review = ErrandReview(
            id = UUID.randomUUID().toString(),
            reviewerName = _currentUser.value.name,
            reviewerRole = _currentUser.value.role.label,
            rating = rating,
            comment = comment,
            tags = tags,
            date = "Just now"
        )
        _reviews.update { listOf(review) + it }
    }

    fun getTask(taskId: String): TaskRequest? = _tasks.value.find { it.id == taskId }

    private fun createInitialTasks(): List<TaskRequest> {
        return listOf(
            TaskRequest(
                id = "TASK-8041",
                title = "Panadol CF & Surgical Mask",
                description = "2 packs of Panadol CF and a box of face masks. Please check expiry date.",
                category = TaskCategory.PHARMACY,
                customerId = "usr_001",
                customerName = "Hamza Farooq",
                customerPhone = "+92 300 1234567",
                customerRating = 4.9f,
                helperId = "hlp_002",
                helperName = "Bilal Tariq",
                helperPhone = "+92 321 9876543",
                helperRating = 4.95f,
                helperVehicle = "Honda CG 125 (Bike)",
                pickupShopName = "Servaid Pharmacy, G-Block",
                pickupAddress = "Main Boulevard, Johar Town, Lahore",
                pickupLocationX = 0.25f,
                pickupLocationY = 0.35f,
                deliveryAddress = "House 42, Block J-2, Johar Town, Lahore",
                deliveryLocationX = 0.72f,
                deliveryLocationY = 0.68f,
                runnerCurrentX = 0.48f,
                runnerCurrentY = 0.52f,
                productBudget = 420.0,
                actualReceiptAmount = 420.0,
                helperReward = 150.0,
                deadlineTime = "Within 30 mins",
                deliveryOtp = "4892",
                distanceKm = 2.6,
                routeMatchPercent = 96,
                status = TaskStatus.ON_THE_WAY,
                specialNotes = "Ring bell twice, leave with guard if on call",
                receiptPhotoNote = "Printed Servaid receipt attached: Rs. 420 paid",
                createdAtFormatted = "18 mins ago",
                paymentStatus = PaymentStatus.PAID,
                paymentMethod = PaymentMethod.EASYPAISA,
                gatewayTransactionId = "EP-942817",
                isCommissionSettled = false
            ),
            TaskRequest(
                id = "TASK-5120",
                title = "Al-Fatah Fresh Bread & Milk",
                description = "1 Dawn Jumbo Bran Bread, 2 Olper's Full Cream Milk packs (1L each).",
                category = TaskCategory.FOOD_GROCERY,
                customerId = "usr_104",
                customerName = "Ayesha Khan",
                customerPhone = "+92 333 4455667",
                customerRating = 4.85f,
                pickupShopName = "Al-Fatah Department Store",
                pickupAddress = "Shaukat Khanum Chowk, Johar Town",
                pickupLocationX = 0.20f,
                pickupLocationY = 0.40f,
                deliveryAddress = "Plaza 18, Civic Center, Phase 2",
                deliveryLocationX = 0.80f,
                deliveryLocationY = 0.30f,
                runnerCurrentX = 0.20f,
                runnerCurrentY = 0.40f,
                productBudget = 680.0,
                helperReward = 180.0,
                deadlineTime = "Within 50 mins",
                deliveryOtp = "7129",
                distanceKm = 3.8,
                routeMatchPercent = 91,
                status = TaskStatus.POSTED,
                specialNotes = "Check milk expiry must be next week",
                createdAtFormatted = "12 mins ago",
                paymentStatus = PaymentStatus.PAID,
                paymentMethod = PaymentMethod.JAZZCASH,
                gatewayTransactionId = "JC-551029",
                isCommissionSettled = false
            ),
            TaskRequest(
                id = "TASK-9934",
                title = "Dry Clean Suit Pick-up",
                description = "Pick up 2-piece formal suit from clean counter. Slip ticket #440.",
                category = TaskCategory.PICK_UP,
                customerId = "usr_205",
                customerName = "Omer Sheikh",
                customerPhone = "+92 345 8899112",
                customerRating = 5.0f,
                pickupShopName = "Champion Dry Cleaners",
                pickupAddress = "Mini Market, Gulberg II, Lahore",
                pickupLocationX = 0.35f,
                pickupLocationY = 0.20f,
                deliveryAddress = "Sector Y, Phase 3, DHA Lahore",
                deliveryLocationX = 0.88f,
                deliveryLocationY = 0.82f,
                productBudget = 350.0,
                helperReward = 260.0,
                deadlineTime = "Today by 6:30 PM",
                deliveryOtp = "3361",
                distanceKm = 6.4,
                routeMatchPercent = 89,
                status = TaskStatus.POSTED,
                specialNotes = "Do not fold the jacket, keep on hanger",
                createdAtFormatted = "35 mins ago",
                paymentStatus = PaymentStatus.PAID,
                paymentMethod = PaymentMethod.CARD_PAYMENT,
                gatewayTransactionId = "CARD-339182",
                isCommissionSettled = false
            )
        )
    }

    private fun createInitialPayments(): List<PaymentRecord> {
        return listOf(
            PaymentRecord(
                id = "PAY-1001",
                taskId = "TASK-8041",
                customerId = "usr_001",
                customerName = "Hamza Farooq",
                amount = 570.0,
                paymentMethod = PaymentMethod.EASYPAISA,
                status = PaymentStatus.PAID,
                gatewayTransactionId = "EP-942817",
                createdAt = "18 mins ago",
                updatedAt = "18 mins ago"
            ),
            PaymentRecord(
                id = "PAY-1002",
                taskId = "TASK-5120",
                customerId = "usr_104",
                customerName = "Ayesha Khan",
                amount = 860.0,
                paymentMethod = PaymentMethod.JAZZCASH,
                status = PaymentStatus.PAID,
                gatewayTransactionId = "JC-551029",
                createdAt = "12 mins ago",
                updatedAt = "12 mins ago"
            ),
            PaymentRecord(
                id = "PAY-1003",
                taskId = "TASK-9934",
                customerId = "usr_205",
                customerName = "Omer Sheikh",
                amount = 610.0,
                paymentMethod = PaymentMethod.CARD_PAYMENT,
                status = PaymentStatus.PAID,
                gatewayTransactionId = "CARD-339182",
                createdAt = "35 mins ago",
                updatedAt = "35 mins ago"
            ),
            PaymentRecord(
                id = "PAY-1000",
                taskId = "TASK-7001",
                customerId = "usr_001",
                customerName = "Hamza Farooq",
                amount = 1000.0, // Matching Section 1 Example: Rs. 1,000 -> 15% Rs. 150 | 85% Rs. 850
                paymentMethod = PaymentMethod.EASYPAISA,
                status = PaymentStatus.PAID,
                gatewayTransactionId = "EP-882194",
                createdAt = "Yesterday",
                updatedAt = "Yesterday"
            )
        )
    }

    private fun createInitialCommissions(): List<CommissionRecord> {
        return listOf(
            // Example from doc: Rs. 1,000 payment -> Owner: Rs. 150 (15%)
            CommissionRecord(
                id = "COMM-1001",
                taskId = "TASK-7001",
                transactionId = "TXN-7001-A",
                eligibleAmount = 1000.0,
                commissionRate = 0.15,
                commissionAmount = 150.0,
                status = "SETTLED",
                createdAt = "Yesterday"
            ),
            // Example from doc: Rs. 5,000 payment -> Owner: Rs. 750 (15%)
            CommissionRecord(
                id = "COMM-1002",
                taskId = "TASK-6500",
                transactionId = "TXN-6500-B",
                eligibleAmount = 5000.0,
                commissionRate = 0.15,
                commissionAmount = 750.0,
                status = "SETTLED",
                createdAt = "2 days ago"
            )
        )
    }

    private fun createInitialRunnerEarnings(): List<RunnerEarningRecord> {
        return listOf(
            // Example from doc: Rs. 1,000 payment -> Runner: Rs. 850 (85%)
            RunnerEarningRecord(
                id = "EARN-1001",
                taskId = "TASK-7001",
                runnerId = "hlp_002",
                runnerName = "Bilal Tariq",
                eligibleAmount = 1000.0,
                earningRate = 0.85,
                earningAmount = 850.0,
                status = "CREDITED",
                createdAt = "Yesterday"
            ),
            // Example from doc: Rs. 5,000 payment -> Runner: Rs. 4,250 (85%)
            RunnerEarningRecord(
                id = "EARN-1002",
                taskId = "TASK-6500",
                runnerId = "hlp_002",
                runnerName = "Bilal Tariq",
                eligibleAmount = 5000.0,
                earningRate = 0.85,
                earningAmount = 4250.0,
                status = "CREDITED",
                createdAt = "2 days ago"
            )
        )
    }

    private fun createInitialOwnerLedger(): List<OwnerWalletLedgerRecord> {
        return listOf(
            OwnerWalletLedgerRecord(
                id = "LEDGER-01",
                transactionId = "TXN-7001-A",
                taskId = "TASK-7001",
                amount = 150.0, // 15% of Rs. 1,000
                type = "COMMISSION_CREDIT",
                status = "SETTLED",
                description = "15% platform commission on Errand #TASK-7001 (Rs. 1,000)",
                createdAt = "Yesterday"
            ),
            OwnerWalletLedgerRecord(
                id = "LEDGER-02",
                transactionId = "TXN-6500-B",
                taskId = "TASK-6500",
                amount = 750.0, // 15% of Rs. 5,000
                type = "COMMISSION_CREDIT",
                status = "SETTLED",
                description = "15% platform commission on Errand #TASK-6500 (Rs. 5,000)",
                createdAt = "2 days ago"
            )
        )
    }

    private fun createInitialPayouts(): List<PayoutRecord> {
        return listOf(
            PayoutRecord(
                id = "PO-501",
                userId = "admin_owner_01",
                userName = "KaamGo Platform Owner",
                role = UserRole.ADMIN,
                amount = 10000.0,
                paymentMethod = PaymentMethod.EASYPAISA,
                accountDetails = "0300-9876543 (KaamGo Merchant)",
                status = "PAID",
                transactionReference = "EP-WD-991240",
                createdAt = "3 days ago",
                completedAt = "3 days ago"
            ),
            PayoutRecord(
                id = "PO-502",
                userId = "hlp_002",
                userName = "Bilal Tariq",
                role = UserRole.RUNNER,
                amount = 2500.0,
                paymentMethod = PaymentMethod.JAZZCASH,
                accountDetails = "0321-9876543 (JazzCash)",
                status = "PAID",
                transactionReference = "JC-WD-441829",
                createdAt = "Yesterday",
                completedAt = "Yesterday"
            )
        )
    }

    private fun createInitialRefunds(): List<RefundRecord> {
        return emptyList()
    }

    private fun createInitialChats(): Map<String, List<ChatMessage>> {
        return mapOf(
            "TASK-8041" to listOf(
                ChatMessage(
                    id = "msg_1",
                    taskId = "TASK-8041",
                    senderName = "Bilal Tariq",
                    senderRole = UserRole.RUNNER,
                    messageText = "Salam Hamza bhai, I have accepted your Panadol & masks request. I am at Servaid now.",
                    time = "14:10",
                    isFromMe = false
                ),
                ChatMessage(
                    id = "msg_2",
                    taskId = "TASK-8041",
                    senderName = "Hamza Farooq",
                    senderRole = UserRole.CUSTOMER,
                    messageText = "Walaikum salam! Great, please ask for Panadol CF blue tablet pack, not normal Panadol.",
                    time = "14:11",
                    isFromMe = true
                ),
                ChatMessage(
                    id = "msg_3",
                    taskId = "TASK-8041",
                    senderName = "Bilal Tariq",
                    senderRole = UserRole.RUNNER,
                    messageText = "Understood! Purchased both packs. Total was Rs. 420. Uploading the receipt now.",
                    time = "14:15",
                    isFromMe = false,
                    isReceiptProof = true,
                    receiptAmount = 420.0
                ),
                ChatMessage(
                    id = "msg_4",
                    taskId = "TASK-8041",
                    senderName = "Bilal Tariq",
                    senderRole = UserRole.RUNNER,
                    messageText = "On my bike now, ETA 10 minutes to House 42.",
                    time = "14:18",
                    isFromMe = false
                )
            )
        )
    }

    private fun createInitialTransactions(): List<WalletTransaction> {
        return listOf(
            WalletTransaction(
                id = "TX-891024",
                title = "Task Earnings (85% Split)",
                detail = "Task #7001: Rs. 850 credited",
                amount = 850.0,
                isCredit = true,
                method = PaymentMethod.EASYPAISA,
                date = "Yesterday, 6:40 PM",
                referenceCode = "PK-EARN-7001"
            ),
            WalletTransaction(
                id = "TX-890412",
                title = "JazzCash Withdrawal",
                detail = "Sent to 0321-9876543",
                amount = 2500.0,
                isCredit = false,
                method = PaymentMethod.JAZZCASH,
                date = "Yesterday, 3:15 PM",
                referenceCode = "WD-JC-8904"
            )
        )
    }

    private fun createInitialNotifications(): List<NotificationItem> {
        return listOf(
            NotificationItem(
                id = "notif_1",
                title = "Payment Verified (EasyPaisa) 💳",
                body = "Task #TASK-8041: Rs. 570 verified via EasyPaisa Merchant gateway.",
                timeAgo = "18 mins ago",
                category = "PAYMENT",
                relatedTaskId = "TASK-8041"
            ),
            NotificationItem(
                id = "notif_2",
                title = "Runner is on the way! 🛵",
                body = "Bilal Tariq has collected items from Servaid Pharmacy and is en route.",
                timeAgo = "10 mins ago",
                category = "ORDER",
                relatedTaskId = "TASK-8041"
            )
        )
    }

    private fun createInitialReviews(): List<ErrandReview> {
        return listOf(
            ErrandReview(
                id = "rev_1",
                reviewerName = "Dr. Asim Munir",
                reviewerRole = "Customer",
                rating = 5.0f,
                comment = "Extremely punctual and polite runner! Brought the prescription safely and provided exact receipt.",
                tags = listOf("Fast Delivery", "Honest & Polite", "Good Communication"),
                date = "2 days ago"
            )
        )
    }
}
