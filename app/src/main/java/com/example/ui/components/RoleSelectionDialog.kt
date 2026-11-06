package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.UserRole
import com.example.ui.theme.BorderDark
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.ui.theme.WarningAmber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleSelectionDialog(
    currentRole: UserRole,
    onRoleSelected: (UserRole) -> Unit,
    onDismiss: () -> Unit
) {
    BasicAlertDialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(SurfaceDark)
                .border(1.dp, BorderDark, RoundedCornerShape(24.dp))
                .padding(20.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Select Workspace Role",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = TextWhite
                        )
                        Text(
                            text = "ಯೂಸರ್ ಅಥವಾ ಅಡ್ಮಿನ್ ಲಾಗಿನ್ ಆಯ್ಕೆ ಮಾಡಿ",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.testTag("close_role_dialog")) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Role 1: Splicer / User
                RoleOptionCard(
                    icon = "🔧",
                    title = "User / Splicer Panel (ಯೂಸರ್ ಪ್ಯಾನೆಲ್)",
                    desc = "OTDR Cut Bot, Giant Map, 360° Street View, OTDR Trace & Links",
                    badge = "USER / SPLICER",
                    isSelected = currentRole == UserRole.SPLICER,
                    accentColor = NeonEmerald,
                    onClick = {
                        onRoleSelected(UserRole.SPLICER)
                        onDismiss()
                    },
                    testTag = "select_role_splicer"
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Role 2: Super Admin
                RoleOptionCard(
                    icon = "🛡️",
                    title = "Super Admin Panel (ಅಡ್ಮಿನ್ ಪ್ಯಾನೆಲ್)",
                    desc = "Auto-Drive GPS Route Add, Manual Draw, KML Import, Staff & Fault Tickets",
                    badge = "SUPER ADMIN",
                    isSelected = currentRole == UserRole.ADMIN,
                    accentColor = CyberCyan,
                    onClick = {
                        onRoleSelected(UserRole.ADMIN)
                        onDismiss()
                    },
                    testTag = "select_role_admin"
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Role 3: Field Surveyor (Auto-Drive GPS)
                RoleOptionCard(
                    icon = "🚗",
                    title = "Field Surveyor (Auto-Drive GPS Mode)",
                    desc = "Directly launch Auto-Drive GPS tracking & route recording while driving",
                    badge = "DRIVE GPS",
                    isSelected = currentRole == UserRole.SURVEYOR,
                    accentColor = WarningAmber,
                    onClick = {
                        onRoleSelected(UserRole.SURVEYOR)
                        onDismiss()
                    },
                    testTag = "select_role_surveyor"
                )
            }
        }
    }
}

@Composable
private fun RoleOptionCard(
    icon: String,
    title: String,
    desc: String,
    badge: String,
    isSelected: Boolean,
    accentColor: Color,
    onClick: () -> Unit,
    testTag: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) accentColor.copy(alpha = 0.15f) else SurfaceCard)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) accentColor else BorderDark,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(14.dp)
            .testTag(testTag)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = icon, fontSize = 24.sp)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(accentColor.copy(alpha = 0.2f))
                        .border(1.dp, accentColor.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = badge,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        color = accentColor
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = desc,
                fontSize = 11.sp,
                color = TextMuted,
                lineHeight = 15.sp
            )
        }
    }
}
