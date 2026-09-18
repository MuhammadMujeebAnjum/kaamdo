package com.example.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import com.example.model.ChatMessage
import com.example.model.TaskRequest
import com.example.model.UserRole
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskChatScreen(
    task: TaskRequest,
    currentRole: UserRole,
    messages: List<ChatMessage>,
    onSendMessage: (String, Boolean, Double?) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var textInput by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val quickReplies = when (currentRole) {
        UserRole.CUSTOMER -> listOf(
            "Please check expiry date",
            "Get printed receipt please",
            "Ring doorbell twice",
            "Leave with gate security"
        )
        UserRole.RUNNER -> listOf(
            "Arrived at the shop",
            "Item found and purchased!",
            "Riding towards your location",
            "Standing outside your gate"
        )
        UserRole.ADMIN -> listOf(
            "Support ticket opened",
            "Verifying transaction proof"
        )
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(KaamNavy),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (currentRole == UserRole.CUSTOMER) Icons.Default.TwoWheeler else Icons.Default.Person,
                                contentDescription = null,
                                tint = KaamGreen,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Column {
                            Text(
                                text = if (currentRole == UserRole.CUSTOMER) (task.helperName ?: "Helper") else task.customerName,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Task #${task.id} • ${task.pickupShopName}",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Phone, contentDescription = "Call", tint = KaamGreenDark)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceLight,
                    titleContentColor = TextPrimary
                )
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
                    .navigationBarsPadding(),
                color = SurfaceLight,
                shadowElevation = 8.dp
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    // Quick reply chips
                    LazyRow(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(quickReplies) { reply ->
                            SuggestionChip(
                                onClick = {
                                    onSendMessage(reply, false, null)
                                },
                                label = { Text(reply, fontSize = 11.sp) }
                            )
                        }
                    }

                    // Input Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(
                            onClick = {
                                // Simulate sending receipt photo proof
                                onSendMessage("Receipt photo attached: Store purchase confirmed at Rs. ${task.productBudget.toInt()}", true, task.productBudget)
                            }
                        ) {
                            Icon(Icons.Default.ReceiptLong, contentDescription = "Attach Receipt", tint = KaamGreenDark)
                        }

                        OutlinedTextField(
                            value = textInput,
                            onValueChange = { textInput = it },
                            placeholder = { Text("Write a message...") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(24.dp)
                        )

                        IconButton(
                            onClick = {
                                if (textInput.isNotBlank()) {
                                    onSendMessage(textInput.trim(), false, null)
                                    textInput = ""
                                }
                            },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(KaamNavy)
                        ) {
                            Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        },
        containerColor = BackgroundLight
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Masked Number & Trust Notice Banner
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = KaamGreenLight
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = KaamGreenDark, modifier = Modifier.size(14.dp))
                    Text(
                        text = "Masked in-app chat. All communications and receipts are logged for your safety.",
                        color = KaamGreenDark,
                        fontSize = 11.sp
                    )
                }
            }

            // Message List
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(messages) { msg ->
                    if (msg.isSystem) {
                        // System pill
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = SurfaceVariantLight
                            ) {
                                Text(
                                    text = msg.messageText,
                                    fontSize = 11.sp,
                                    color = TextSecondary,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                )
                            }
                        }
                    } else {
                        val isMe = msg.isFromMe
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                        ) {
                            Column(
                                horizontalAlignment = if (isMe) Alignment.End else Alignment.Start,
                                modifier = Modifier.widthIn(max = 280.dp)
                            ) {
                                Text(
                                    text = msg.senderName,
                                    fontSize = 10.sp,
                                    color = TextMuted,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )

                                Surface(
                                    shape = RoundedCornerShape(
                                        topStart = 14.dp,
                                        topEnd = 14.dp,
                                        bottomStart = if (isMe) 14.dp else 2.dp,
                                        bottomEnd = if (isMe) 2.dp else 14.dp
                                    ),
                                    color = if (isMe) KaamNavy else SurfaceLight,
                                    shadowElevation = 1.dp
                                ) {
                                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        if (msg.isReceiptProof) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                Icon(
                                                    Icons.Default.Receipt,
                                                    contentDescription = null,
                                                    tint = if (isMe) KaamGreen else KaamGreenDark,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Text(
                                                    text = "RECEIPT PROOF ATTACHED",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isMe) KaamGreen else KaamGreenDark
                                                )
                                            }
                                        }

                                        Text(
                                            text = msg.messageText,
                                            color = if (isMe) Color.White else TextPrimary,
                                            fontSize = 13.sp,
                                            lineHeight = 18.sp
                                        )

                                        Text(
                                            text = msg.time,
                                            color = if (isMe) Color(0xFF94A3B8) else TextMuted,
                                            fontSize = 9.sp,
                                            modifier = Modifier.align(Alignment.End)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
