package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.OtdrCalculator
import com.example.model.FaultTicket
import com.example.model.StaffMember
import com.example.model.TicketStatus
import com.example.ui.theme.BorderDark
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.FaultRed
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.ui.theme.WarningAmber

@Composable
fun AdminPanelScreen(
    totalRoutesCount: Int,
    faultTickets: List<FaultTicket>,
    staffMembers: List<StaffMember>,
    onToggleTicket: (String) -> Unit,
    onAddStaff: (String, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var newName by remember { mutableStateOf("") }
    var newPhone by remember { mutableStateOf("") }
    var newRole by remember { mutableStateOf("Field Splicer") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF030712))
            .padding(14.dp)
            .testTag("admin_panel_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 4 Key Stat Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AdminStatCard(
                    title = "ROUTES",
                    value = "$totalRoutesCount",
                    sub = "OFC Lines",
                    accent = NeonEmerald,
                    modifier = Modifier.weight(1f)
                )
                AdminStatCard(
                    title = "OPEN CUTS",
                    value = "${faultTickets.count { it.status != TicketStatus.RESOLVED }}",
                    sub = "Active Tickets",
                    accent = FaultRed,
                    modifier = Modifier.weight(1f)
                )
                AdminStatCard(
                    title = "TEAM",
                    value = "${staffMembers.size}",
                    sub = "Splicers/Drivers",
                    accent = CyberCyan,
                    modifier = Modifier.weight(1f)
                )
                AdminStatCard(
                    title = "CLOUD RTDB",
                    value = "ott-app",
                    sub = "96be5 live",
                    accent = WarningAmber,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Debug APK Ready Card
        item {
            val downloadUrl = "https://ais-dev-wpsjzxqnnllidchxwl47qv-389843737005.asia-east1.run.app/FiberGrid-Pro-debug.apk"
            val clipboard = androidx.compose.ui.platform.LocalClipboardManager.current

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(SurfaceDark)
                    .border(1.dp, NeonEmerald.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                    .padding(14.dp)
                    .testTag("admin_apk_ready_card")
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = "📦", fontSize = 22.sp)
                            Column {
                                Text(
                                    text = "Debug APK Ready (ನೇರ ಇನ್‌ಸ್ಟಾಲ್)",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    color = TextWhite
                                )
                                Text(
                                    text = "FiberGrid-Pro-debug.apk • 23 MB • Ready",
                                    fontSize = 11.sp,
                                    color = NeonEmerald
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(NeonEmerald.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "READY ✓",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = NeonEmerald
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "1) ZIP ಡೌನ್‌ಲೋಡ್ ಮಾಡಿದಾಗ 'apk/FiberGrid-Pro-debug.apk' ಫೈಲ್ ನೇರವಾಗಿ ಸಿಗುತ್ತದೆ.\n2) GitHub ನಲ್ಲಿ 'Actions' ಟ್ಯಾಬ್ ಕ್ಲಿಕ್ ಮಾಡಿ 'FiberGrid-Pro-debug-apk' ಡೌನ್‌ಲೋಡ್ ಮಾಡಬಹುದು.",
                        fontSize = 10.sp,
                        color = Color(0xFFCBD5E1),
                        lineHeight = 14.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(downloadUrl))
                                context.startActivity(intent)
                            },
                            modifier = Modifier.weight(1.3f).height(40.dp).testTag("download_apk_browser_button"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NeonEmerald)
                        ) {
                            Text("📲 Phone Browser Download ↗", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color.Black)
                        }

                        OutlinedButton(
                            onClick = {
                                clipboard.setText(androidx.compose.ui.text.AnnotatedString(downloadUrl))
                            },
                            modifier = Modifier.weight(0.9f).height(40.dp).testTag("copy_apk_link_button"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWhite)
                        ) {
                            Text("📋 Copy Link", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Live Fault Tickets Section
        item {
            Text(
                text = "🚨 Live OTDR Fault Cut Tickets",
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                color = TextWhite
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "ಸ್ಪ್ಲೈಸರ್ ಮೆಸೇಜ್ ಕಳಿಸಿದ ತಕ್ಷಣ ಇಲ್ಲಿ ಲೈವ್ ಟಿಕೆಟ್ ಕ್ರಿಯೇಟ್ ಆಗುತ್ತದೆ",
                fontSize = 11.sp,
                color = TextMuted
            )
        }

        items(faultTickets, key = { it.id }) { tkt ->
            val isResolved = tkt.status == TicketStatus.RESOLVED
            val mapsUrl = OtdrCalculator.buildGoogleMapsUrl(tkt.lat, tkt.lng)
            val svUrl = OtdrCalculator.buildStreetViewUrl(tkt.lat, tkt.lng, tkt.heading)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(SurfaceDark)
                    .border(
                        1.dp,
                        if (isResolved) NeonEmerald.copy(alpha = 0.4f) else FaultRed.copy(alpha = 0.5f),
                        RoundedCornerShape(18.dp)
                    )
                    .padding(14.dp)
                    .testTag("ticket_item_${tkt.id}")
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = tkt.id,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = if (isResolved) NeonEmerald else FaultRed
                            )
                            Text(
                                text = "• Cut @ ${tkt.otdrMeters}m",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    when (tkt.status) {
                                        TicketStatus.OPEN_CUT -> FaultRed.copy(alpha = 0.2f)
                                        TicketStatus.IN_PROGRESS -> WarningAmber.copy(alpha = 0.2f)
                                        TicketStatus.RESOLVED -> NeonEmerald.copy(alpha = 0.2f)
                                    }
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = tkt.status.name.replace("_", " "),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = when (tkt.status) {
                                    TicketStatus.OPEN_CUT -> FaultRed
                                    TicketStatus.IN_PROGRESS -> WarningAmber
                                    TicketStatus.RESOLVED -> NeonEmerald
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${tkt.routeName} — Near ${tkt.landmark} (${tkt.metersAway}m away)",
                        fontSize = 11.sp,
                        color = Color(0xFFCBD5E1)
                    )
                    Text(
                        text = "Reported: ${tkt.time} by ${tkt.reportedBy}",
                        fontSize = 9.sp,
                        color = TextMuted
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(mapsUrl))) },
                            modifier = Modifier.weight(1f).height(34.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NeonEmerald),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(2.dp)
                        ) {
                            Text("📍 Maps", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        }

                        Button(
                            onClick = { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(svUrl))) },
                            modifier = Modifier.weight(1f).height(34.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = WarningAmber),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(2.dp)
                        ) {
                            Text("🧍 StreetView", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        }

                        OutlinedButton(
                            onClick = { onToggleTicket(tkt.id) },
                            modifier = Modifier.weight(1f).height(34.dp).testTag("toggle_ticket_${tkt.id}"),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = if (isResolved) NeonEmerald else TextWhite
                            ),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(2.dp)
                        ) {
                            Text(if (isResolved) "✓ Done" else "Mark Done", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Staff & Splicers Section
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "👥 Field Splicers & Surveyors",
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                color = TextWhite
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "ಹೊಸ ಸ್ಪ್ಲೈಸರ್ ಅಥವಾ ಡ್ರೈವರ್ ಸಿಬ್ಬಂದಿ ಸೇರಿಸಿ",
                fontSize = 11.sp,
                color = TextMuted
            )
        }

        // Add Staff Form
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(SurfaceDark)
                    .border(1.dp, BorderDark, RoundedCornerShape(18.dp))
                    .padding(12.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = newName,
                            onValueChange = { newName = it },
                            placeholder = { Text("Name (e.g. Anand)", fontSize = 11.sp, color = TextMuted) },
                            modifier = Modifier.weight(1f).testTag("staff_name_input"),
                            shape = RoundedCornerShape(10.dp),
                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp, color = TextWhite),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = SurfaceCard,
                                unfocusedContainerColor = SurfaceCard,
                                focusedBorderColor = CyberCyan,
                                unfocusedBorderColor = BorderDark
                            ),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = newPhone,
                            onValueChange = { newPhone = it },
                            placeholder = { Text("Phone (e.g. 9845...)", fontSize = 11.sp, color = TextMuted) },
                            modifier = Modifier.weight(1f).testTag("staff_phone_input"),
                            shape = RoundedCornerShape(10.dp),
                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp, color = TextWhite),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = SurfaceCard,
                                unfocusedContainerColor = SurfaceCard,
                                focusedBorderColor = CyberCyan,
                                unfocusedBorderColor = BorderDark
                            ),
                            singleLine = true
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            RoleChip(label = "Splicer", isSelected = newRole == "Field Splicer") { newRole = "Field Splicer" }
                            RoleChip(label = "Driver", isSelected = newRole == "GPS Driver") { newRole = "GPS Driver" }
                            RoleChip(label = "Admin", isSelected = newRole == "Admin") { newRole = "Admin" }
                        }

                        Button(
                            onClick = {
                                if (newName.isNotBlank() && newPhone.isNotBlank()) {
                                    onAddStaff(newName, newPhone, newRole)
                                    newName = ""
                                    newPhone = ""
                                }
                            },
                            modifier = Modifier.height(38.dp).testTag("add_staff_button"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan)
                        ) {
                            Text("+ Add", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                    }
                }
            }
        }

        items(staffMembers, key = { it.id }) { st ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(SurfaceDark)
                    .border(1.dp, BorderDark, RoundedCornerShape(14.dp))
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(text = st.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                            Text(
                                text = st.role,
                                fontSize = 9.sp,
                                color = CyberCyan,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(SurfaceCard)
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                        Text(text = "📞 ${st.phone}", fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = TextMuted)
                    }

                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${st.phone}"))
                            context.startActivity(intent)
                        },
                        modifier = Modifier.height(32.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SurfaceCard),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp)
                    ) {
                        Text("Call 📞", fontSize = 10.sp, color = NeonEmerald, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminStatCard(
    title: String,
    value: String,
    sub: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceDark)
            .border(1.dp, BorderDark, RoundedCornerShape(16.dp))
            .padding(10.dp)
    ) {
        Column {
            Text(text = title, fontSize = 8.sp, fontWeight = FontWeight.Black, color = TextMuted)
            Text(
                text = value,
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                color = accent
            )
            Text(text = sub, fontSize = 8.sp, color = TextMuted, maxLines = 1)
        }
    }
}

@Composable
private fun RoleChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) CyberCyan.copy(alpha = 0.2f) else SurfaceCard)
            .border(1.dp, if (isSelected) CyberCyan else BorderDark, RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) CyberCyan else TextMuted
        )
    }
}
