package com.example.model

data class NotificationItem(
    val id: String,
    val title: String,
    val body: String,
    val timeAgo: String,
    val category: String, // "ORDER", "PAYMENT", "SYSTEM", "SECURITY"
    val isRead: Boolean = false,
    val relatedTaskId: String? = null
)
