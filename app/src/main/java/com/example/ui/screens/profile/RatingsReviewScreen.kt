package com.example.ui.screens.profile

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
import com.example.model.ErrandReview
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RatingsReviewScreen(
    reviews: List<ErrandReview>,
    onAddReview: (Float, String, List<String>) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var userRating by remember { mutableStateOf(5f) }
    var commentInput by remember { mutableStateOf("") }
    var selectedTags by remember { mutableStateOf(setOf("Fast Delivery", "Honest & Polite")) }
    var showSuccessSnackbar by remember { mutableStateOf(false) }

    val availableTags = listOf(
        "Fast Delivery",
        "Honest & Polite",
        "Good Communication",
        "Verified Receipt",
        "Careful Handling"
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Ratings & Trust Score", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceLight,
                    titleContentColor = TextPrimary
                )
            )
        },
        containerColor = BackgroundLight
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Overall Rating Scorecard
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
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("4.9", fontSize = 38.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                repeat(5) {
                                    Icon(Icons.Default.Star, contentDescription = null, tint = KaamAmber, modifier = Modifier.size(16.dp))
                                }
                            }
                            Text("Based on 38 community tasks", fontSize = 11.sp, color = TextSecondary)
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Punctuality: 99%", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = KaamGreenDark)
                            Text("Item Accuracy: 100%", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = KaamGreenDark)
                            Text("Receipt Verification: 98%", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = KaamGreenDark)
                        }
                    }
                }
            }

            // Submit a Rating Form
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceLight),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "RATE YOUR RECENT ERRAND HELPER",
                            color = TextMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )

                        // Star selector
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            (1..5).forEach { starIndex ->
                                IconButton(onClick = { userRating = starIndex.toFloat() }) {
                                    Icon(
                                        imageVector = if (starIndex <= userRating) Icons.Default.Star else Icons.Default.StarBorder,
                                        contentDescription = "$starIndex stars",
                                        tint = KaamAmber,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }
                        }

                        // Tags multi-selection
                        Text("Select Experience Tags:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            availableTags.take(3).forEach { tag ->
                                val isSelected = selectedTags.contains(tag)
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        selectedTags = if (isSelected) selectedTags - tag else selectedTags + tag
                                    },
                                    label = { Text(tag, fontSize = 10.sp) }
                                )
                            }
                        }

                        OutlinedTextField(
                            value = commentInput,
                            onValueChange = { commentInput = it },
                            label = { Text("Write feedback for this errand...") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2
                        )

                        Button(
                            onClick = {
                                if (commentInput.isNotBlank()) {
                                    onAddReview(userRating, commentInput, selectedTags.toList())
                                    commentInput = ""
                                    showSuccessSnackbar = true
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = KaamNavy),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Submit Errand Review", color = Color.White, fontWeight = FontWeight.Bold)
                        }

                        if (showSuccessSnackbar) {
                            Text("Review submitted to helper's trust profile!", color = KaamGreenDark, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Reviews List
            item {
                Text(
                    text = "COMMUNITY REVIEWS",
                    color = TextMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            items(reviews) { rev ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceLight),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(rev.reviewerName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("${rev.reviewerRole} • ${rev.date}", fontSize = 10.sp, color = TextMuted)
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = KaamAmber, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(2.dp))
                                Text("${rev.rating}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }

                        Text(rev.comment, fontSize = 12.sp, color = TextSecondary)

                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            rev.tags.forEach { tag ->
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = SurfaceVariantLight
                                ) {
                                    Text(tag, fontSize = 9.sp, color = TextSecondary, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
