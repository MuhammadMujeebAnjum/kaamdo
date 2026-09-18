package com.example.model

enum class TaskCategory(val displayName: String, val tagline: String) {
    BUY_FOR_ME("Buy for Me", "Store purchases & mart shopping"),
    PICK_UP("Pick up for Me", "Parcels, laundry, documents & keys"),
    DELIVER_SOMETHING("Deliver Something", "Drop off items to clients or friends"),
    PHARMACY("Pharmacy & Health", "Emergency medicines & clinic pick up"),
    FOOD_GROCERY("Fresh Food & Bakery", "Warm meals, naan, milk & fruits")
}

enum class TaskStatus(val label: String, val stepIndex: Int, val description: String) {
    POSTED("Finding Runners", 0, "Looking for verified runners nearby"),
    OFFERS("Offers Received", 1, "Runners submitted proposals"),
    ACCEPTED("Runner Assigned", 2, "Runner confirmed and heading to pickup"),
    AT_PICKUP("At Pickup Store", 3, "Runner arrived at the shop/origin"),
    PURCHASED("Item Collected", 4, "Receipt uploaded and verified"),
    ON_THE_WAY("On the Way", 5, "Runner is riding to delivery address"),
    DELIVERED("Arrived at Door", 6, "Provide 4-digit OTP to complete"),
    COMPLETED("Completed", 7, "Delivery successful, funds released"),
    CANCELLED("Cancelled", -1, "Task was cancelled")
}

data class TaskRequest(
    val id: String,
    val title: String,
    val description: String,
    val category: TaskCategory,
    val customerId: String,
    val customerName: String,
    val customerPhone: String,
    val customerRating: Float = 4.8f,
    val helperId: String? = null,
    val helperName: String? = null,
    val helperPhone: String? = null,
    val helperRating: Float = 4.9f,
    val helperVehicle: String? = "Motorbike (Honda 125)",
    val pickupShopName: String,
    val pickupAddress: String,
    val pickupLocationX: Float = 0.25f,
    val pickupLocationY: Float = 0.35f,
    val deliveryAddress: String,
    val deliveryLocationX: Float = 0.75f,
    val deliveryLocationY: Float = 0.65f,
    val runnerCurrentX: Float = 0.45f,
    val runnerCurrentY: Float = 0.48f,
    val productBudget: Double, // Estimated item cost in PKR
    val actualReceiptAmount: Double? = null, // After purchase
    val helperReward: Double, // Task service fee
    val platformFee: Double = 0.0, // Commission calculated via 15% rule
    val deadlineTime: String = "Within 45 mins",
    val deliveryOtp: String = "5921",
    val distanceKm: Double = 3.2,
    val routeMatchPercent: Int = 94,
    val status: TaskStatus = TaskStatus.POSTED,
    val specialNotes: String = "Please request printed sales receipt with date",
    val receiptPhotoNote: String? = "Receipt attached: Rs. 380 total paid",
    val createdAtFormatted: String = "10 mins ago",
    // Payment System Integration (Section 1 to 5)
    val paymentStatus: PaymentStatus = PaymentStatus.PAID,
    val paymentMethod: PaymentMethod = PaymentMethod.EASYPAISA,
    val gatewayTransactionId: String = "EP-942817",
    val isCommissionSettled: Boolean = false
) {
    // Total amount paid by customer
    val totalCustomerCost: Double
        get() = (actualReceiptAmount ?: productBudget) + helperReward

    // Eligible amount for 15% / 85% split
    // When customer pays total amount for task (e.g. Rs. 1,000):
    // 15% Owner = Rs. 150
    // 85% Runner = Rs. 850
    val ownerCommissionAmount: Double
        get() = totalCustomerCost * 0.15

    val runnerEarningAmount: Double
        get() = totalCustomerCost * 0.85
}
