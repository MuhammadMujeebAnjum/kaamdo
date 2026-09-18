package com.example.model

enum class PaymentStatus(val label: String) {
    PENDING("Pending Verification"),
    PROCESSING("Processing Provider"),
    PAID("Paid & Verified"),
    FAILED("Failed"),
    CANCELLED("Cancelled"),
    REFUNDED("Refunded")
}

enum class PaymentMethod(val displayName: String, val subtitle: String) {
    EASYPAISA("Easypaisa Merchant", "Direct mobile wallet debit / USSD push"),
    JAZZCASH("JazzCash Business", "Merchant API / MPIN payment request"),
    CARD_PAYMENT("Debit / Credit Card", "Visa & MasterCard 3D Secure"),
    CASH_ON_DELIVERY("Cash on Delivery (COD)", "Hand cash at doorstep if enabled"),
    KAAMGO_WALLET("KaamGo Escrow Wallet", "In-app prefunded balance")
}

// 1. payments table (Section 13)
data class PaymentRecord(
    val id: String,
    val taskId: String,
    val customerId: String,
    val customerName: String,
    val amount: Double,
    val paymentMethod: PaymentMethod,
    val status: PaymentStatus,
    val gatewayTransactionId: String,
    val createdAt: String,
    val updatedAt: String,
    val failureReason: String? = null
)

// 2. commissions table (15% to Owner - Section 13)
data class CommissionRecord(
    val id: String,
    val taskId: String,
    val transactionId: String,
    val eligibleAmount: Double,
    val commissionRate: Double = 0.15, // 15% Owner Commission
    val commissionAmount: Double, // eligibleAmount * 0.15
    val status: String = "SETTLED",
    val createdAt: String
)

// 3. runner_earnings table (85% to Runner - Section 13)
data class RunnerEarningRecord(
    val id: String,
    val taskId: String,
    val runnerId: String,
    val runnerName: String,
    val eligibleAmount: Double,
    val earningRate: Double = 0.85, // 85% Runner Earning
    val earningAmount: Double, // eligibleAmount * 0.85
    val status: String = "CREDITED",
    val createdAt: String
)

// 4. owner_wallet_ledger table (Section 13)
data class OwnerWalletLedgerRecord(
    val id: String,
    val transactionId: String,
    val taskId: String,
    val amount: Double,
    val type: String, // "COMMISSION_CREDIT", "PAYOUT_DEBIT", "REFUND_REVERSAL"
    val status: String = "SETTLED",
    val description: String,
    val createdAt: String
)

// 5. payouts table (Section 13)
data class PayoutRecord(
    val id: String,
    val userId: String,
    val userName: String,
    val role: UserRole, // OWNER or RUNNER
    val amount: Double,
    val paymentMethod: PaymentMethod,
    val accountDetails: String, // e.g. "03001234567 (Easypaisa)"
    val status: String = "PAID", // PENDING, PROCESSING, PAID, FAILED
    val transactionReference: String,
    val createdAt: String,
    val completedAt: String
)

// 6. refunds table (Section 13)
data class RefundRecord(
    val id: String,
    val paymentId: String,
    val taskId: String,
    val amount: Double,
    val reason: String,
    val status: String = "COMPLETED", // PENDING, COMPLETED, REJECTED
    val createdAt: String,
    val completedAt: String
)

// Legacy / UI general wallet transaction for balance view
data class WalletTransaction(
    val id: String,
    val title: String,
    val detail: String,
    val amount: Double,
    val isCredit: Boolean,
    val method: PaymentMethod,
    val date: String,
    val referenceCode: String,
    val status: String = "Completed"
)

data class ErrandReview(
    val id: String,
    val reviewerName: String,
    val reviewerRole: String,
    val rating: Float,
    val comment: String,
    val tags: List<String>,
    val date: String
)

// Dedicated Owner Payout & Bank Account Setup
data class OwnerSettlementAccount(
    val accountTitle: String = "Mujeeb Anjum (Owner)",
    val bankOrWalletName: String = "EasyPaisa", // "EasyPaisa", "JazzCash", "Meezan Bank", "HBL", "Nayapay"
    val accountNumber: String = "0300 1234567",
    val ibanNumber: String = "PK12MEZN0001234567890101",
    val autoTransferDaily: Boolean = false,
    val isVerified: Boolean = true
)

