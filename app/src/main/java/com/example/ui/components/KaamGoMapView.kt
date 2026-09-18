package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TaskRequest
import com.example.model.TaskStatus
import com.example.ui.theme.*

@Composable
fun KaamGoMapView(
    task: TaskRequest,
    modifier: Modifier = Modifier,
    heightDp: Int = 220,
    showDetails: Boolean = true
) {
    // Pulse animation for runner GPS beacon
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = 10f,
        targetValue = 28f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseRadius"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseAlpha"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(heightDp.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Map Canvas
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // Background city map tone
                drawRect(Color(0xFFE2E8F0))

                // Green sector parks (simulated urban zoning)
                drawRoundRect(
                    color = Color(0xFFD1FAE5),
                    topLeft = Offset(w * 0.05f, h * 0.1f),
                    size = androidx.compose.ui.geometry.Size(w * 0.22f, h * 0.22f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(16f, 16f)
                )
                drawRoundRect(
                    color = Color(0xFFD1FAE5),
                    topLeft = Offset(w * 0.72f, h * 0.70f),
                    size = androidx.compose.ui.geometry.Size(w * 0.22f, h * 0.22f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(16f, 16f)
                )

                // Canal / Boulevard diagonal
                val waterPath = Path().apply {
                    moveTo(0f, h * 0.85f)
                    cubicTo(w * 0.35f, h * 0.75f, w * 0.65f, h * 0.35f, w, h * 0.15f)
                }
                drawPath(
                    path = waterPath,
                    color = Color(0xFFBAE6FD),
                    style = Stroke(width = 14f, cap = StrokeCap.Round)
                )

                // Secondary roads grid (White street blocks)
                val roadColor = Color.White
                // Horizontals
                drawLine(roadColor, Offset(0f, h * 0.28f), Offset(w, h * 0.28f), strokeWidth = 8f)
                drawLine(roadColor, Offset(0f, h * 0.52f), Offset(w, h * 0.52f), strokeWidth = 10f)
                drawLine(roadColor, Offset(0f, h * 0.76f), Offset(w, h * 0.76f), strokeWidth = 8f)

                // Verticals
                drawLine(roadColor, Offset(w * 0.25f, 0f), Offset(w * 0.25f, h), strokeWidth = 8f)
                drawLine(roadColor, Offset(w * 0.50f, 0f), Offset(w * 0.50f, h), strokeWidth = 10f)
                drawLine(roadColor, Offset(w * 0.75f, 0f), Offset(w * 0.75f, h), strokeWidth = 8f)

                // Active Route Polyline from Pickup -> Delivery
                val pickup = Offset(w * task.pickupLocationX, h * task.pickupLocationY)
                val delivery = Offset(w * task.deliveryLocationX, h * task.deliveryLocationY)

                val routePath = Path().apply {
                    moveTo(pickup.x, pickup.y)
                    lineTo(pickup.x, (pickup.y + delivery.y) / 2f)
                    lineTo(delivery.x, (pickup.y + delivery.y) / 2f)
                    lineTo(delivery.x, delivery.y)
                }

                // Route shadow & active line
                drawPath(
                    path = routePath,
                    color = Color(0x3300B159),
                    style = Stroke(width = 12f, cap = StrokeCap.Round)
                )
                drawPath(
                    path = routePath,
                    color = KaamGreen,
                    style = Stroke(width = 6f, cap = StrokeCap.Round)
                )

                // Pickup Marker (Amber Store Pin)
                drawCircle(color = Color.White, radius = 14f, center = pickup)
                drawCircle(color = KaamAmber, radius = 10f, center = pickup)

                // Delivery Marker (Navy Destination Pin)
                drawCircle(color = Color.White, radius = 14f, center = delivery)
                drawCircle(color = KaamNavy, radius = 10f, center = delivery)

                // Runner Marker (Only if assigned)
                if (task.status != TaskStatus.POSTED && task.status != TaskStatus.OFFERS) {
                    val runnerPos = Offset(w * task.runnerCurrentX, h * task.runnerCurrentY)

                    // Pulse ring
                    drawCircle(
                        color = KaamGreen.copy(alpha = pulseAlpha),
                        radius = pulseRadius,
                        center = runnerPos
                    )
                    // Core runner dot
                    drawCircle(color = Color.White, radius = 12f, center = runnerPos)
                    drawCircle(color = KaamGreen, radius = 8f, center = runnerPos)
                }
            }

            // Top-left: Location indicator chip
            Surface(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp),
                shape = RoundedCornerShape(20.dp),
                color = KaamNavy.copy(alpha = 0.9f),
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = KaamGreen,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "Live Route • Lahore",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Top-right: Distance & ETA pill
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp),
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                shadowElevation = 3.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.TwoWheeler,
                        contentDescription = null,
                        tint = KaamGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Column {
                        Text(
                            text = "${task.distanceKm} km away",
                            color = TextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "ETA 12 mins",
                            color = KaamGreenDark,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Bottom overlay card with Pickup and Drop-off quick labels
            if (showDetails) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(10.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White.copy(alpha = 0.95f),
                    shadowElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(KaamAmber)
                                )
                                Text(
                                    text = "Pickup: ${task.pickupShopName}",
                                    color = TextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                            }
                            Text(
                                text = task.pickupAddress,
                                color = TextSecondary,
                                fontSize = 10.sp,
                                maxLines = 1
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .size(16.dp)
                        )

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(KaamNavy)
                                )
                                Text(
                                    text = "Drop-off",
                                    color = TextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                            }
                            Text(
                                text = task.deliveryAddress,
                                color = TextSecondary,
                                fontSize = 10.sp,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}
