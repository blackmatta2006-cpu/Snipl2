package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.WifiTethering
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppScreen
import com.example.model.OfcRoute
import com.example.model.UserRole
import com.example.ui.theme.BorderDark
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.EmeraldGlow
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

@Composable
fun TopHeaderBar(
    currentScreen: AppScreen,
    currentRole: UserRole,
    routes: List<OfcRoute>,
    activeRouteId: String,
    onRouteSelected: (String) -> Unit,
    onBackToHub: () -> Unit,
    onOpenRoleDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    var routeDropdownExpanded by remember { mutableStateOf(false) }
    val activeRoute = routes.find { it.id == activeRouteId } ?: routes.firstOrNull()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(SurfaceDark)
            .border(width = 1.dp, color = BorderDark)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f, fill = false)
            ) {
                if (currentScreen != AppScreen.HUB) {
                    IconButton(
                        onClick = onBackToHub,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(SurfaceCard)
                            .testTag("top_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "ಹಿಂದೆ ಹೋಗಿ (Back)",
                            tint = NeonEmerald
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(EmeraldGlow)
                            .border(1.dp, NeonEmerald.copy(alpha = 0.5f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.WifiTethering,
                            contentDescription = "FiberGrid",
                            tint = NeonEmerald,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "FiberGrid Pro",
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp,
                            color = TextWhite
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (currentRole == UserRole.ADMIN) Color(0x3306B6D4) else EmeraldGlow
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (currentRole == UserRole.ADMIN) CyberCyan.copy(alpha = 0.4f) else NeonEmerald.copy(alpha = 0.4f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = currentRole.badge,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (currentRole == UserRole.ADMIN) CyberCyan else NeonEmerald
                            )
                        }
                    }
                    Text(
                        text = "Next-Gen OFC GIS & OTDR OS",
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                }
            }

            // Switch User Button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceCard)
                        .border(1.dp, BorderDark, RoundedCornerShape(12.dp))
                        .clickable { onOpenRoleDialog() }
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                        .testTag("switch_role_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Switch User",
                            tint = CyberCyan,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Switch",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                    }
                }
            }
        }

        // Active Route Selector Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF070B14))
                    .border(1.dp, BorderDark, RoundedCornerShape(10.dp))
                    .clickable { routeDropdownExpanded = true }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .testTag("route_selector_button")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "🛣️ ${activeRoute?.name ?: "Select Cable Route"}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonEmerald,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Dropdown",
                        tint = TextMuted
                    )
                }

                DropdownMenu(
                    expanded = routeDropdownExpanded,
                    onDismissRequest = { routeDropdownExpanded = false },
                    modifier = Modifier.background(SurfaceDark).border(1.dp, BorderDark)
                ) {
                    routes.forEach { r ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(
                                        text = r.name,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (r.id == activeRouteId) NeonEmerald else TextWhite
                                    )
                                    Text(
                                        text = "${r.fiberType} • ${r.points.size} Nodes",
                                        fontSize = 10.sp,
                                        color = TextMuted
                                    )
                                }
                            },
                            onClick = {
                                onRouteSelected(r.id)
                                routeDropdownExpanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}
