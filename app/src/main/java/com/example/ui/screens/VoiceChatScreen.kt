package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.PlatformBadge
import com.example.ui.components.StatusDot
import com.example.ui.components.UserAvatar
import com.example.ui.theme.*
import com.example.ui.viewmodel.GamingViewModel

@Composable
fun VoiceChatScreen(
    viewModel: GamingViewModel,
    modifier: Modifier = Modifier
) {
    val commsTab by viewModel.commsSubTab.collectAsState()
    val chatMessages by viewModel.chatMessages.collectAsState()
    val voiceRooms by viewModel.voiceRooms.collectAsState()
    val activeVoiceRoom by viewModel.activeVoiceRoom.collectAsState()
    val isMicMuted by viewModel.isMicMuted.collectAsState()
    val isDeafened by viewModel.isDeafened.collectAsState()
    var messageInput by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("voice_chat_screen")
    ) {
        // Sub-tabs: Cross-Platform Chat vs Voice Channels
        Surface(
            color = DarkSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            TabRow(
                selectedTabIndex = commsTab,
                containerColor = Color.Transparent,
                contentColor = CyberCyan,
                divider = {}
            ) {
                Tab(
                    selected = commsTab == 0,
                    onClick = { viewModel.setCommsSubTab(0) },
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                            Text("Cross-Platform Chat", fontWeight = FontWeight.Bold)
                        }
                    },
                    modifier = Modifier.testTag("chat_tab")
                )
                Tab(
                    selected = commsTab == 1,
                    onClick = { viewModel.setCommsSubTab(1) },
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.Headset, contentDescription = null, modifier = Modifier.size(16.dp))
                            Text("Voice Rooms", fontWeight = FontWeight.Bold)
                            if (activeVoiceRoom != null) {
                                StatusDot(status = UserStatus.IN_VOICE)
                            }
                        }
                    },
                    modifier = Modifier.testTag("voice_rooms_tab")
                )
            }
        }

        if (commsTab == 0) {
            // CROSS-PLATFORM LIVE CHAT VIEW
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 110.dp)
            ) {
                // Tactical Quick Callout Pills
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val tacticalCallouts = listOf(
                        "Ready up! 🎮",
                        "Need +1 for Ranked 5v5",
                        "Hopping in Voice: Squad Alpha",
                        "Check out my highlight clip 🔥",
                        "Switching from PC to PS5"
                    )
                    items(tacticalCallouts) { callout ->
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = DarkSurfaceElevated,
                            border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                            modifier = Modifier
                                .clickable {
                                    viewModel.sendChatMessage(callout)
                                }
                                .testTag("callout_chip")
                        ) {
                            Text(
                                text = callout,
                                color = CyberCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }

                // Chat Messages Feed
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(chatMessages) { message ->
                        ChatMessageBubble(message = message)
                    }
                }

                // Chat Input Row
                Surface(
                    color = DarkSurfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = messageInput,
                            onValueChange = { messageInput = it },
                            placeholder = { Text("Cross-play team chat...", color = TextMuted, fontSize = 13.sp) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyberCyan,
                                unfocusedBorderColor = DarkCardBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                cursorColor = CyberCyan
                            ),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("chat_input_field")
                        )

                        IconButton(
                            onClick = {
                                if (messageInput.isNotBlank()) {
                                    viewModel.sendChatMessage(messageInput)
                                    messageInput = ""
                                }
                            },
                            enabled = messageInput.isNotBlank(),
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = if (messageInput.isNotBlank()) CyberCyan else DarkSurfaceElevated,
                                contentColor = if (messageInput.isNotBlank()) CyberCyanDark else TextMuted
                            ),
                            modifier = Modifier
                                .size(44.dp)
                                .testTag("send_chat_btn")
                        ) {
                            Icon(Icons.Default.Send, contentDescription = "Send", modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        } else {
            // VOICE ROOMS & CHANNELS VIEW
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 120.dp)
            ) {
                // Active Voice Room Control Center (if connected)
                if (activeVoiceRoom != null) {
                    item {
                        ActiveVoiceRoomDetailCard(
                            room = activeVoiceRoom!!,
                            isMicMuted = isMicMuted,
                            isDeafened = isDeafened,
                            onToggleMic = { viewModel.toggleMicMute() },
                            onToggleDeafen = { viewModel.toggleDeafen() },
                            onLeave = { viewModel.leaveVoiceRoom() },
                            onVolumeChange = { memberId, vol ->
                                viewModel.updateMemberVolume(memberId, vol)
                            }
                        )
                    }
                }

                // Available Voice Channels Header
                item {
                    Text(
                        text = "VOICE CHANNELS & LOBBIES",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                items(voiceRooms) { room ->
                    val isConnectedHere = activeVoiceRoom?.id == room.id
                    VoiceRoomCard(
                        room = room,
                        isConnected = isConnectedHere,
                        onConnect = {
                            if (isConnectedHere) viewModel.leaveVoiceRoom()
                            else viewModel.joinVoiceRoom(room)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ChatMessageBubble(message: ChatMessage) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("chat_msg_${message.id}"),
        horizontalArrangement = if (message.isSelf) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        if (!message.isSelf) {
            UserAvatar(profile = message.sender, size = 32.dp, showStatusDot = false)
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(
            modifier = Modifier.widthIn(max = 280.dp),
            horizontalAlignment = if (message.isSelf) Alignment.End else Alignment.Start
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = if (message.isSelf) "You" else message.sender.displayName,
                    color = if (message.isSelf) CyberCyan else TextPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                PlatformBadge(platform = message.platform, compact = true)
                Text(text = message.timestamp, color = TextMuted, fontSize = 10.sp)
            }

            Spacer(modifier = Modifier.height(3.dp))

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (message.isSelf) CyberCyanDark else DarkSurfaceElevated,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (message.isSelf) CyberCyan.copy(alpha = 0.5f) else DarkCardBorder
                )
            ) {
                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                    Text(
                        text = message.text,
                        color = if (message.isSelf) Color.White else TextPrimary,
                        fontSize = 13.sp
                    )

                    if (message.attachedClipTitle != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = DarkBg,
                            border = androidx.compose.foundation.BorderStroke(1.dp, RadiantGold.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.PlayCircle, contentDescription = null, tint = RadiantGold, modifier = Modifier.size(16.dp))
                                Column {
                                    Text(text = message.attachedClipTitle, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Text(text = message.attachedGame ?: "Game Highlight", color = TextSecondary, fontSize = 9.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VoiceRoomCard(
    room: VoiceRoom,
    isConnected: Boolean,
    onConnect: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .testTag("voice_room_${room.id}"),
        color = if (isConnected) DarkSurfaceElevated else DarkSurfaceVariant,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isConnected) CyberCyan else DarkCardBorder
        )
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(if (isConnected) CyberCyan.copy(alpha = 0.2f) else DarkSurface)
                            .border(1.dp, if (isConnected) CyberCyan else DarkCardBorder, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.HeadsetMic,
                            contentDescription = null,
                            tint = if (isConnected) CyberCyan else TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Text(text = room.name, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Text(text = "${room.gameTitle} • ${room.bitrate}", color = TextSecondary, fontSize = 11.sp)
                    }
                }

                Button(
                    onClick = onConnect,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isConnected) NeonRed else CyberCyan,
                        contentColor = if (isConnected) Color.White else CyberCyanDark
                    ),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("connect_voice_btn_${room.id}")
                ) {
                    Text(
                        text = if (isConnected) "Disconnect" else "Join Voice",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Members in room
            if (room.members.isNotEmpty()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy((-6).dp)) {
                        room.members.forEach { member ->
                            UserAvatar(profile = member.user, size = 26.dp, showStatusDot = false)
                        }
                    }
                    Text(
                        text = "${room.members.size}/${room.maxCapacity} connected (${room.pingMs}ms)",
                        color = NeonGreen,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ActiveVoiceRoomDetailCard(
    room: VoiceRoom,
    isMicMuted: Boolean,
    isDeafened: Boolean,
    onToggleMic: () -> Unit,
    onToggleDeafen: () -> Unit,
    onLeave: () -> Unit,
    onVolumeChange: (String, Float) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, CyberCyan, RoundedCornerShape(16.dp))
            .testTag("active_voice_detail_card"),
        color = DarkSurfaceElevated
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(shape = CircleShape, color = NeonGreen.copy(alpha = 0.2f)) {
                            Text(
                                text = "LIVE COMMS",
                                color = NeonGreen,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Text(text = room.name, color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    Text(text = "HQ Opus Audio (${room.bitrate}) • Latency ${room.pingMs}ms", color = TextSecondary, fontSize = 11.sp)
                }

                IconButton(onClick = onLeave, modifier = Modifier.testTag("leave_active_voice_btn")) {
                    Icon(Icons.Default.CallEnd, contentDescription = "Disconnect", tint = NeonRed)
                }
            }

            // Mic & Deafen Control Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onToggleMic,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isMicMuted) NeonRed.copy(alpha = 0.2f) else CyberCyan.copy(alpha = 0.2f),
                        contentColor = if (isMicMuted) NeonRed else CyberCyan
                    ),
                    modifier = Modifier.weight(1f).testTag("voice_detail_mic_btn")
                ) {
                    Icon(imageVector = if (isMicMuted) Icons.Default.MicOff else Icons.Default.Mic, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isMicMuted) "Mic Muted" else "Mic Live", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onToggleDeafen,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDeafened) NeonRed.copy(alpha = 0.2f) else DarkSurfaceVariant,
                        contentColor = if (isDeafened) NeonRed else TextPrimary
                    ),
                    modifier = Modifier.weight(1f).testTag("voice_detail_deafen_btn")
                ) {
                    Icon(imageVector = if (isDeafened) Icons.Default.VolumeOff else Icons.Default.VolumeUp, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isDeafened) "Deafened" else "Sound On", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Member Volume Sliders
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "INDIVIDUAL SQUAD VOLUMES",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )

                room.members.forEach { member ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        UserAvatar(profile = member.user, size = 32.dp)
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = member.user.displayName, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                if (member.isSpeaking) {
                                    Text(text = "Speaking...", color = CyberCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Slider(
                                value = member.volume,
                                onValueChange = { onVolumeChange(member.user.id, it) },
                                valueRange = 0f..1.5f,
                                colors = SliderDefaults.colors(
                                    thumbColor = CyberCyan,
                                    activeTrackColor = CyberCyan,
                                    inactiveTrackColor = DarkCardBorder
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
