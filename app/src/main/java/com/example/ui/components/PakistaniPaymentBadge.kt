package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PaymentMethod
import com.example.model.PaymentStatus
import com.example.ui.theme.*

@Composable
fun PakistaniPaymentBadge(method: PaymentMethod, modifier: Modifier = Modifier) {
    val (bgColor, textColor, shortName) = when (method) {
        PaymentMethod.EASYPAISA -> Triple(Color(0xFFE8F5E9), EasyPaisaGreen, "Easypaisa")
        PaymentMethod.JAZZCASH -> Triple(Color(0xFFFFEBEE), JazzCashRed, "JazzCash")
        PaymentMethod.CARD_PAYMENT -> Triple(Color(0xFFE0F2FE), KaamBlue, "Debit/Credit Card")
        PaymentMethod.CASH_ON_DELIVERY -> Triple(Color(0xFFFEF3C7), Color(0xFFB45309), "Cash on Delivery")
        PaymentMethod.KAAMGO_WALLET -> Triple(KaamGreenLight, KaamGreenDark, "KaamGo Wallet")
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = shortName,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun PaymentStatusBadge(status: PaymentStatus, modifier: Modifier = Modifier) {
    val (bgColor, textColor) = when (status) {
        PaymentStatus.PAID -> Pair(KaamGreenLight, KaamGreenDark)
        PaymentStatus.PROCESSING -> Pair(KaamAmberLight, Color(0xFFB45309))
        PaymentStatus.PENDING -> Pair(SurfaceVariantLight, TextSecondary)
        PaymentStatus.FAILED -> Pair(Color(0xFFFFEBEE), KaamRed)
        PaymentStatus.CANCELLED -> Pair(SurfaceVariantLight, TextMuted)
        PaymentStatus.REFUNDED -> Pair(Color(0xFFF3E8FF), Color(0xFF6B21A8))
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(6.dp),
        color = bgColor
    ) {
        Text(
            text = status.label,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}
