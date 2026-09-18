package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TaskStatus
import com.example.ui.theme.*

@Composable
fun StatusBadge(status: TaskStatus, modifier: Modifier = Modifier) {
    val (bgColor, textColor) = when (status) {
        TaskStatus.POSTED -> Pair(KaamBlueLight, KaamBlue)
        TaskStatus.OFFERS -> Pair(KaamAmberLight, KaamAmber)
        TaskStatus.ACCEPTED -> Pair(KaamGreenLight, KaamGreenDark)
        TaskStatus.AT_PICKUP -> Pair(KaamAmberLight, KaamAmber)
        TaskStatus.PURCHASED -> Pair(KaamBlueLight, KaamBlue)
        TaskStatus.ON_THE_WAY -> Pair(KaamGreenLight, KaamGreenDark)
        TaskStatus.DELIVERED -> Pair(KaamAmberLight, KaamAmber)
        TaskStatus.COMPLETED -> Pair(KaamGreenLight, KaamGreenDark)
        TaskStatus.CANCELLED -> Pair(KaamRedLight, KaamRed)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = status.label,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
