package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.OtdrCalculator
import com.example.model.AppScreen
import com.example.model.ChatMessage
import com.example.model.FaultResult
import com.example.model.OfcRoute
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
fun BotScreen(
    activeRoute: OfcRoute,
    faultResult: FaultResult,
    chatMessages: List<ChatMessage>,
    direction: String,
    slackMode: String,
    onSendMessage: (String) -> Unit,
    onDirectionChange: (String) -> Unit,
    onSlackModeChange: (String) -> Unit,
    onSliderChange: (Int) -> Unit,
    onClearChat: () -> Unit,
    onNavigate: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(chatMessages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF030712))
            .testTag("bot_screen")
    ) {
        // Controls bar: Direction, Slack Mode & Clear
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceDark)
                .border(1.dp, BorderDark)
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Direction Toggle
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(SurfaceCard)
                        .clickable {
                            onDirectionChange(if (direction == "forward") "reverse" else "forward")
                        }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("bot_direction_toggle")
                ) {
                    Text(
                        text = if (direction == "forward") "OLT → End" else "End → OLT (Reverse)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonEmerald
                    )
                }

                // Slack Mode Toggle
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(SurfaceCard)
                        .clickable {
                            onSlackModeChange(if (slackMode == "optical") "ground" else "optical")
                        }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("bot_slack_toggle")
                ) {
                    Text(
                        text = if (slackMode == "optical") "Optical (+Loops)" else "Pure Ground",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberCyan
                    )
                }
            }

            IconButton(
                onClick = onClearChat,
                modifier = Modifier.size(32.dp).testTag("clear_chat_button")
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteSweep,
                    contentDescription = "Clear Chat",
                    tint = TextMuted
                )
            }
        }

        // Quick Suggestion Chips
        val samples = listOf(
            "1450 meter cut agide",
            "OTDR 820m fault",
            "2.65 km cut from OLT",
            "Reverse 950m cut"
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF070B14))
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Try:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
            samples.forEach { s ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceCard)
                        .border(1.dp, NeonEmerald.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .clickable { onSendMessage(s) }
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                        .testTag("sample_chip_${s.take(6)}")
                ) {
                    Text(text = s, fontSize = 11.sp, color = NeonEmerald, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        // Messages List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(chatMessages, key = { it.id }) { msg ->
                if (msg.isUser) {
                    UserChatBubble(msg)
                } else {
                    BotChatBubble(
                        msg = msg,
                        context = context,
                        onCopy = { clipboardManager.setText(AnnotatedString(it)) },
                        onNavigate = onNavigate
                    )
                }
            }
        }

        // Interactive Distance Slider
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceDark)
                .border(1.dp, BorderDark)
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "OTDR Scrubber:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted
                )
                Text(
                    text = "${faultResult.clampedMeters} m (${(faultResult.clampedMeters / 1000.0).let { "%.2f".format(it) }} km)",
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black,
                    color = NeonEmerald
                )
            }
            Slider(
                value = faultResult.clampedMeters.toFloat(),
                onValueChange = { onSliderChange(it.toInt()) },
                valueRange = 0f..faultResult.maxRouteMeters.toFloat().coerceAtLeast(100f),
                colors = SliderDefaults.colors(
                    thumbColor = NeonEmerald,
                    activeTrackColor = NeonEmerald,
                    inactiveTrackColor = BorderDark
                ),
                modifier = Modifier.testTag("otdr_slider")
            )
        }

        // Bottom Input Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF030712))
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = {
                    Text(
                        text = "1450 meter cut agide ಅಥವಾ 1.8km...",
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .testTag("chat_input_field"),
                shape = RoundedCornerShape(16.dp),
                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 13.sp, color = TextWhite),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = SurfaceDark,
                    unfocusedContainerColor = SurfaceDark,
                    focusedBorderColor = NeonEmerald,
                    unfocusedBorderColor = BorderDark
                ),
                singleLine = true
            )

            Button(
                onClick = {
                    if (inputText.isNotBlank()) {
                        onSendMessage(inputText)
                        inputText = ""
                    }
                },
                modifier = Modifier.height(50.dp).testTag("chat_send_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NeonEmerald)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = Color.Black
                )
            }
        }
    }
}

@Composable
private fun UserChatBubble(msg: ChatMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .clip(RoundedCornerShape(18.dp))
                .background(NeonEmerald)
                .padding(12.dp)
        ) {
            Column {
                Text(
                    text = msg.text,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = msg.timestamp,
                    fontSize = 9.sp,
                    color = Color(0x99000000),
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}

@Composable
private fun BotChatBubble(
    msg: ChatMessage,
    context: Context,
    onCopy: (String) -> Unit,
    onNavigate: (AppScreen) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .clip(RoundedCornerShape(18.dp))
                .background(SurfaceDark)
                .border(1.dp, BorderDark, RoundedCornerShape(18.dp))
                .padding(14.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🤖 FiberGrid Bot",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonEmerald
                    )
                    Text(text = msg.timestamp, fontSize = 9.sp, color = TextMuted)
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(text = msg.text, fontSize = 12.sp, color = TextWhite, lineHeight = 17.sp)

                // If result is present, show structured action card
                msg.faultResult?.let { res ->
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF070B14))
                            .border(1.dp, BorderDark, RoundedCornerShape(14.dp))
                            .padding(10.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "📍 GPS: ${res.lat}, ${res.lng}",
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = CyberCyan
                                )
                                Text(
                                    text = "🧭 ${res.heading}° ${res.compassDir}",
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = WarningAmber
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Action buttons inside chat
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Button(
                                    onClick = {
                                        val mapsUri = Uri.parse(OtdrCalculator.buildGoogleMapsUrl(res.lat, res.lng))
                                        val intent = Intent(Intent.ACTION_VIEW, mapsUri)
                                        context.startActivity(intent)
                                    },
                                    modifier = Modifier.weight(1f).height(36.dp).testTag("open_maps_from_chat"),
                                    colors = ButtonDefaults.buttonColors(containerColor = NeonEmerald),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(4.dp)
                                ) {
                                    Text(text = "📍 Maps", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                }

                                Button(
                                    onClick = {
                                        val svUri = Uri.parse(OtdrCalculator.buildStreetViewUrl(res.lat, res.lng, res.heading))
                                        val intent = Intent(Intent.ACTION_VIEW, svUri)
                                        context.startActivity(intent)
                                    },
                                    modifier = Modifier.weight(1f).height(36.dp).testTag("open_sv_from_chat"),
                                    colors = ButtonDefaults.buttonColors(containerColor = WarningAmber),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(4.dp)
                                ) {
                                    Text(text = "🧍 StreetView", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                }

                                OutlinedButton(
                                    onClick = { onNavigate(AppScreen.GIS_MAP) },
                                    modifier = Modifier.weight(1f).height(36.dp).testTag("view_cut_on_map_btn"),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(4.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberCyan)
                                ) {
                                    Text(text = "🗺️ Map", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                IconButton(
                                    onClick = {
                                        val shareText = "🚨 OFC Cut Alert: ${res.clampedMeters}m\nLandmark: ${res.refLandmark.label} (${res.metersFromLandmark}m away)\nGPS: ${res.lat}, ${res.lng}\nMaps: ${OtdrCalculator.buildGoogleMapsUrl(res.lat, res.lng)}"
                                        val intent = Intent(Intent.ACTION_SEND).apply {
                                            type = "text/plain"
                                            putExtra(Intent.EXTRA_TEXT, shareText)
                                        }
                                        context.startActivity(Intent.createChooser(intent, "Share OFC Cut Link"))
                                    },
                                    modifier = Modifier.size(36.dp).testTag("share_cut_btn")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = "Share",
                                        tint = NeonEmerald
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
