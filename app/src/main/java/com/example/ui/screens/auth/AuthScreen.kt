package com.example.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.UserRole
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    currentRole: UserRole,
    onLoginSuccess: (UserRole) -> Unit,
    modifier: Modifier = Modifier
) {
    var isSignUp by remember { mutableStateOf(false) }
    var selectedRole by remember { mutableStateOf(currentRole) }
    var phone by remember { mutableStateOf("+92 300 1234567") }
    var fullName by remember { mutableStateOf("Hamza Farooq") }
    var cnic by remember { mutableStateOf("35202-4829103-5") }
    var cityZone by remember { mutableStateOf("Lahore (Johar Town)") }
    var vehicleType by remember { mutableStateOf("Motorbike (Honda 125)") }
    var showOtpDialog by remember { mutableStateOf(false) }
    var enteredOtp by remember { mutableStateOf("7294") }

    val scrollState = rememberScrollState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BackgroundLight
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // KaamGo Brand Hero
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(KaamNavy),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "K",
                    color = KaamGreen,
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Black
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Kaam",
                    color = KaamNavy,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "Go",
                    color = KaamGreen,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black
                )
            }

            Text(
                text = "Community-Powered Errand & Delivery App",
                color = TextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Role Selector Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceLight),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "SELECT YOUR ACCOUNT ROLE",
                        color = TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        UserRole.values().forEach { role ->
                            val isSelected = selectedRole == role
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { selectedRole = role },
                                color = if (isSelected) KaamNavy else SurfaceVariantLight,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 6.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = when (role) {
                                            UserRole.CUSTOMER -> Icons.Default.ShoppingBag
                                            UserRole.RUNNER -> Icons.Default.TwoWheeler
                                            UserRole.ADMIN -> Icons.Default.AdminPanelSettings
                                        },
                                        contentDescription = role.label,
                                        tint = if (isSelected) KaamGreen else TextSecondary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = role.label,
                                        color = if (isSelected) Color.White else TextPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = when (selectedRole) {
                            UserRole.CUSTOMER -> "Post grocery runs, parcel pick-ups & small tasks to nearby trusted helpers."
                            UserRole.RUNNER -> "Going somewhere? Accept nearby tasks along your route and earn PKR."
                            UserRole.ADMIN -> "Monitor live tasks, verify CNIC documents, approve payouts & manage city zones."
                        },
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Form Inputs
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceLight),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    if (isSignUp) {
                        OutlinedTextField(
                            value = fullName,
                            onValueChange = { fullName = it },
                            label = { Text("Full Name (as per CNIC)") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = KaamGreen) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Mobile Number (Jazz / Telenor / Zong / Ufone)") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = KaamGreen) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    if (isSignUp || selectedRole == UserRole.RUNNER) {
                        OutlinedTextField(
                            value = cnic,
                            onValueChange = { cnic = it },
                            label = { Text("CNIC Number (NADRA Verified)") },
                            leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null, tint = KaamGreen) },
                            placeholder = { Text("35202-XXXXXXX-X") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    if (selectedRole == UserRole.RUNNER) {
                        OutlinedTextField(
                            value = vehicleType,
                            onValueChange = { vehicleType = it },
                            label = { Text("Vehicle Type") },
                            leadingIcon = { Icon(Icons.Default.TwoWheeler, contentDescription = null, tint = KaamGreen) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    OutlinedTextField(
                        value = cityZone,
                        onValueChange = { cityZone = it },
                        label = { Text("Operating City & Zone") },
                        leadingIcon = { Icon(Icons.Default.LocationCity, contentDescription = null, tint = KaamGreen) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // Trust Notice
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(KaamGreenLight)
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = null,
                            tint = KaamGreenDark,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "All KaamGo helpers and users are CNIC verified with masked mobile details for safety.",
                            color = KaamGreenDark,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Button(
                        onClick = { showOtpDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = KaamNavy),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = if (isSignUp) "Send OTP & Register" else "Continue as ${selectedRole.label}",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = null,
                                tint = KaamGreen,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isSignUp) "Already registered? " else "New to KaamGo? ",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                        Text(
                            text = if (isSignUp) "Log in directly" else "Create Account",
                            color = KaamGreenDark,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { isSignUp = !isSignUp }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Quick Demo Role Presets
            Text(
                text = "⚡ QUICK SWITCH DEMO PROFILES",
                color = TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { onLoginSuccess(UserRole.CUSTOMER) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Customer", fontSize = 11.sp)
                }
                OutlinedButton(
                    onClick = { onLoginSuccess(UserRole.RUNNER) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Runner", fontSize = 11.sp)
                }
                OutlinedButton(
                    onClick = { onLoginSuccess(UserRole.ADMIN) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Admin", fontSize = 11.sp)
                }
            }
        }
    }

    // OTP Verification Sheet/Dialog
    if (showOtpDialog) {
        AlertDialog(
            onDismissRequest = { showOtpDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Sms, contentDescription = null, tint = KaamGreen)
                    Text("Enter SMS Code", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "We sent a 4-digit verification code to $phone. (Mock code: 7294)",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                    OutlinedTextField(
                        value = enteredOtp,
                        onValueChange = { enteredOtp = it },
                        label = { Text("4-digit OTP") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showOtpDialog = false
                        onLoginSuccess(selectedRole)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = KaamGreen)
                ) {
                    Text("Verify & Enter", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showOtpDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
