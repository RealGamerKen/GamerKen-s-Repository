package com.example.ui.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.PlatformBadge
import com.example.ui.components.UserAvatar
import com.example.ui.theme.*
import com.example.ui.viewmodel.GamingViewModel

@Composable
fun SessionsScreen(
    viewModel: GamingViewModel,
    modifier: Modifier = Modifier
) {
    val sessions by viewModel.filteredSessions.collectAsState()
    val selectedGenre by viewModel.selectedGenre.collectAsState()
    val selectedPlatform by viewModel.selectedPlatformFilter.collectAsState()

    Scaffold(
        modifier = modifier.testTag("sessions_screen"),
        containerColor = Color.Transparent,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.setShowCreateSessionDialog(true) },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Schedule Squad", fontWeight = FontWeight.Bold) },
                containerColor = CyberCyan,
                contentColor = CyberCyanDark,
                modifier = Modifier
                    .padding(bottom = 72.dp)
                    .testTag("schedule_squad_fab")
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 140.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "MULTIPLAYER SESSIONS",
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )

                        Surface(
                            shape = CircleShape,
                            color = RadiantGold.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "${sessions.size} ACTIVE",
                                color = RadiantGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Text(
                        text = "Organize, reserve roles, and schedule cross-platform competitive matches.",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }

            // Genre & Platform Filter bar
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(GameGenre.values().toList()) { genre ->
                            val isSelected = selectedGenre == genre
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.setGenreFilter(genre) },
                                label = { Text(genre.title, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CyberCyan,
                                    selectedLabelColor = CyberCyanDark,
                                    containerColor = DarkSurfaceElevated,
                                    labelColor = TextSecondary
                                )
                            )
                        }
                    }
                }
            }

            // Sessions list
            items(sessions) { session ->
                SessionCardItem(
                    session = session,
                    onToggleJoin = { viewModel.toggleJoinSession(session.id) }
                )
            }
        }
    }
}

@Composable
fun SessionCardItem(
    session: GameSession,
    onToggleJoin: () -> Unit
) {
    val filledCount = session.slots.count { it.user != null }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .testTag("session_card_${session.id}"),
        color = DarkSurfaceVariant,
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Top row: Schedule tag + crossplay badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = RadiantGold.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, RadiantGold.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Default.AccessTime,
                            contentDescription = null,
                            tint = RadiantGold,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = session.scheduledTimeDisplay,
                            color = RadiantGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    session.crossPlayPlatforms.forEach { platform ->
                        PlatformBadge(platform = platform, compact = true)
                    }
                }
            }

            // Game & title
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = session.title,
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${session.gameTitle} • ${session.gameMode} • ${session.genre.title}",
                    color = CyberCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                if (session.description.isNotBlank()) {
                    Text(
                        text = session.description,
                        color = TextSecondary,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            // Voice channel info
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = DarkSurfaceElevated,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.HeadsetMic,
                        contentDescription = null,
                        tint = ElectricPurple,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Voice: ${session.voiceChannelName}",
                        color = TextPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Squad Roster Slots
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SQUAD ROSTER ($filledCount/${session.maxPlayers})",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )

                    if (session.isJoined && session.myRole != null) {
                        Text(
                            text = "Your Role: ${session.myRole}",
                            color = NeonGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                session.slots.forEach { slot ->
                    val user = slot.user
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (user != null) DarkSurfaceElevated else DarkSurface,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (user != null) CyberCyan.copy(alpha = 0.3f) else DarkCardBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (user != null) {
                                    UserAvatar(profile = user, size = 26.dp, showStatusDot = false)
                                    Text(
                                        text = user.displayName,
                                        color = TextPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    PlatformBadge(platform = user.currentPlatform, compact = true)
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .size(26.dp)
                                            .clip(CircleShape)
                                            .background(DarkSurfaceVariant)
                                            .border(1.dp, TextSecondary.copy(alpha = 0.4f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.Add,
                                            contentDescription = null,
                                            tint = TextSecondary,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                    Text(
                                        text = "Open Squad Slot",
                                        color = TextSecondary,
                                        fontSize = 12.sp
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = if (user != null) CyberCyan.copy(alpha = 0.15f) else DarkSurfaceVariant
                            ) {
                                Text(
                                    text = slot.role,
                                    color = if (user != null) CyberCyan else TextSecondary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }

            // RSVP CTA
            Button(
                onClick = onToggleJoin,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (session.isJoined) NeonRed else CyberCyan,
                    contentColor = if (session.isJoined) Color.White else CyberCyanDark
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("rsvp_btn_${session.id}")
            ) {
                Icon(
                    imageVector = if (session.isJoined) Icons.Default.Cancel else Icons.Default.CheckCircleOutline,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (session.isJoined) "Leave Session" else "Join Squad / RSVP",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }
    }
}
