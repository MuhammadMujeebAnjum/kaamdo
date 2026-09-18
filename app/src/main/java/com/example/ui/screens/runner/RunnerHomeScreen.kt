package com.example.ui.screens.runner

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.model.TaskRequest
import com.example.model.TaskStatus
import com.example.model.UserProfile
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun RunnerHomeScreen(
    user: UserProfile,
    isOnline: Boolean,
    onToggleOnline: (Boolean) -> Unit,
    routeOrigin: String,
    routeDestination: String,
    onUpdateRoute: (String, String) -> Unit,
    tasks: List<TaskRequest>,
    onAcceptTask: (String) -> Unit,
    onViewActiveTask: (String) -> Unit,
    onViewEarnings: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showRouteDialog by remember { mutableStateOf(false) }
    var originInput by remember { mutableStateOf(routeOrigin) }
    var destInput by remember { mutableStateOf(routeDestination) }

    val myActiveTask = tasks.firstOrNull { it.helperId == user.id && it.status != TaskStatus.COMPLETED && it.status != TaskStatus.CANCELLED }
    val availableTasks = tasks.filter { it.status == TaskStatus.POSTED || it.status == TaskStatus.OFFERS }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Online Availability Header Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceLight),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(if (isOnline) KaamGreen else TextMuted)
                        )
                        Column {
                            Text(
                                text = if (isOnline) "You are Online" else "You are Offline",
                                color = TextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isOnline) "Receiving nearby errands in Lahore" else "Turn on to see high match tasks",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Switch(
                        checked = isOnline,
                        onCheckedChange = onToggleOnline,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = KaamGreen
                        )
                    )
                }
            }
        }

        // Active Task Quick-Action Banner (if currently carrying an errand)
        if (myActiveTask != null) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onViewActiveTask(myActiveTask.id) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = KaamNavy),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = KaamGreen
                            ) {
                                Text(
                                    text = "ACTIVE ERRAND IN PROGRESS",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Text(
                                text = "Earn Rs. ${myActiveTask.helperReward.toInt()}",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = myActiveTask.title,
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Pickup: ${myActiveTask.pickupShopName} → ${myActiveTask.deliveryAddress}",
                            color = Color(0xFFCBD5E1),
                            fontSize = 12.sp
                        )

                        Button(
                            onClick = { onViewActiveTask(myActiveTask.id) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = KaamGreen),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Open Errand Workflow & Update Status", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // "I'm Going There Anyway" Route Match Feature Card (Differentiator from PDF)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = KaamGreenLight),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.Navigation, contentDescription = null, tint = KaamGreenDark, modifier = Modifier.size(18.dp))
                            Text(
                                text = "\"I'M GOING THERE ANYWAY\"",
                                color = KaamGreenDark,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }

                        TextButton(
                            onClick = { showRouteDialog = true },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("Change Route", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = KaamNavy)
                        }
                    }

                    Text(
                        text = "Turn your daily commute into PKR earnings without driving out of your way.",
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = SurfaceLight
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(KaamAmber))
                                Text("From: $routeOrigin", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(KaamGreen))
                                Text("To: $routeDestination", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                            }
                        }
                    }
                }
            }
        }

        // Runner Quick Summary
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { onViewEarnings() },
                    color = SurfaceLight,
                    shape = RoundedCornerShape(14.dp),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Today's Earnings", fontSize = 11.sp, color = TextMuted)
                        Text("Rs. ${user.walletBalance.toInt()}", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = KaamGreenDark)
                        Text("Tap to withdraw", fontSize = 10.sp, color = KaamBlue)
                    }
                }

                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp)),
                    color = SurfaceLight,
                    shape = RoundedCornerShape(14.dp),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Trust Rating", fontSize = 11.sp, color = TextMuted)
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = KaamAmber, modifier = Modifier.size(16.dp))
                            Text("${user.rating}", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary)
                        }
                        Text("${user.completedTasks} completed", fontSize = 10.sp, color = TextSecondary)
                    }
                }
            }
        }

        // Nearby Errands Feed
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "NEARBY ERRANDS AVAILABLE",
                    color = TextMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "${availableTasks.size} tasks",
                    color = KaamGreenDark,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (availableTasks.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceLight),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                        Text("No pending requests right now. New requests will appear here automatically.", color = TextSecondary, fontSize = 12.sp)
                    }
                }
            }
        }

        items(availableTasks) { task ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceLight),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Header: Match tag & Reward
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = KaamGreenLight
                        ) {
                            Text(
                                text = "${task.routeMatchPercent}% Route Match",
                                color = KaamGreenDark,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Earn Rs. ${task.helperReward.toInt()}",
                                color = KaamGreenDark,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = "+ Rs. ${task.productBudget.toInt()} item reimb.",
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                        }
                    }

                    Text(
                        text = task.title,
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = task.description,
                        color = TextSecondary,
                        fontSize = 12.sp,
                        maxLines = 2
                    )

                    // Route details: Pickup -> Drop-off
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = SurfaceVariantLight,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Default.Storefront, contentDescription = null, tint = KaamAmber, modifier = Modifier.size(14.dp))
                                Text(
                                    text = "Pickup: ${task.pickupShopName} (${task.pickupAddress})",
                                    fontSize = 11.sp,
                                    color = TextPrimary,
                                    maxLines = 1
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = KaamNavy, modifier = Modifier.size(14.dp))
                                Text(
                                    text = "Deliver: ${task.deliveryAddress}",
                                    fontSize = 11.sp,
                                    color = TextPrimary,
                                    maxLines = 1
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${task.distanceKm} km away • ${task.deadlineTime}",
                            color = TextMuted,
                            fontSize = 11.sp
                        )

                        Button(
                            onClick = { onAcceptTask(task.id) },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = KaamNavy),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text("Accept Errand", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }
    }

    // Change Route Dialog
    if (showRouteDialog) {
        AlertDialog(
            onDismissRequest = { showRouteDialog = false },
            title = { Text("Set Your Commute Route", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "KaamGo will prioritize errands that lie right on your route.",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                    OutlinedTextField(
                        value = originInput,
                        onValueChange = { originInput = it },
                        label = { Text("Starting from (e.g. Johar Town, Lahore)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = destInput,
                        onValueChange = { destInput = it },
                        label = { Text("Going to (e.g. Gulberg / DHA Lahore)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateRoute(originInput, destInput)
                        showRouteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = KaamGreen)
                ) {
                    Text("Save Route", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRouteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
