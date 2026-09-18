package com.example.model

enum class UserRole(val label: String, val badge: String) {
    CUSTOMER("Customer", "Post Errands"),
    RUNNER("Runner", "Earn on the Way"),
    ADMIN("Admin", "Platform Ops")
}

data class UserProfile(
    val id: String,
    val name: String,
    val phone: String,
    val email: String,
    val role: UserRole,
    val cnic: String = "35201-9481920-3",
    val isCnicVerified: Boolean = true,
    val rating: Float = 4.9f,
    val reviewCount: Int = 38,
    val completedTasks: Int = 54,
    val completionRate: Float = 98.2f,
    val trustScore: Int = 96,
    val vehicle: String = "Honda CD 70 (Motorbike)",
    val cityZone: String = "Johar Town, Lahore",
    val walletBalance: Double = 1450.0,
    val pendingEarnings: Double = 320.0
)
