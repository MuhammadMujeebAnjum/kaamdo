package com.example.model

data class ChatMessage(
    val id: String,
    val taskId: String,
    val senderName: String,
    val senderRole: UserRole,
    val messageText: String,
    val time: String,
    val isFromMe: Boolean,
    val isSystem: Boolean = false,
    val isReceiptProof: Boolean = false,
    val receiptAmount: Double? = null
)
