package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.window.Dialog
import com.example.data.model.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.GamingViewModel

@Composable
fun CreateSessionDialog(
    onDismiss: () -> Unit,
    onCreate: (title: String, game: String, genre: GameGenre, time: String, maxSlots: Int, platforms: List<Platform>, voice: String, mode: String, desc: String, role: String) -> Unit
) {
    var title by remember { mutableStateOf("Ranked Ascendant Grind 5v5") }
    var selectedGame by remember { mutableStateOf("Valorant") }
    var selectedGenre by remember { mutableStateOf(GameGenre.FPS_TACTICAL) }
    var timeDisplay by remember { mutableStateOf("Tonight at 9:00 PM") }
    var capacity by remember { mutableStateOf(5) }
    var voiceRoom by remember { mutableStateOf("Squad Alpha - Competitive") }
    var mode by remember { mutableStateOf("Competitive 5v5") }
    var hostRole by remember { mutableStateOf("IGL / Initiator") }
    var description by remember { mutableStateOf("Looking for positive, callout-focused teammates. Cross-play mic required.") }
    var selectedPlatforms by remember {
        mutableStateOf(listOf(Platform.PC, Platform.PLAYSTATION, Platform.XBOX))
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = DarkSurfaceVariant,
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.5f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("create_session_dialog")
        ) {
            Column(
                modifier = Modifier
                    .padding(18.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SCHEDULE MULTIPLAYER SQUAD",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Session Title") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberCyan,
                        unfocusedBorderColor = DarkCardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("session_title_input")
                )

                // Quick Game Preset selection
                Text(text = "SELECT TITLE", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                val gamesList = listOf("Valorant", "Apex Legends", "Rocket League", "Street Fighter 6", "The Finals")
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(gamesList) { game ->
                        val isSelected = selectedGame == game
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedGame = game
                                selectedGenre = when (game) {
                                    "Valorant", "The Finals" -> GameGenre.FPS_TACTICAL
                                    "Apex Legends" -> GameGenre.BATTLE_ROYALE
                                    "Rocket League" -> GameGenre.SPORTS
                                    "Street Fighter 6" -> GameGenre.FIGHTING
                                    else -> GameGenre.FPS_TACTICAL
                                }
                            },
                            label = { Text(game, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CyberCyan,
                                selectedLabelColor = CyberCyanDark,
                                containerColor = DarkSurfaceElevated,
                                labelColor = TextSecondary
                            )
                        )
                    }
                }

                // Preset Schedule Times
                Text(text = "SCHEDULE TIME PRESETS", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                val timePresets = listOf("Tonight 8:30 PM", "Tonight 9:00 PM", "Tomorrow 7:00 PM", "Saturday 3:00 PM")
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(timePresets) { preset ->
                        val isSelected = timeDisplay == preset
                        FilterChip(
                            selected = isSelected,
                            onClick = { timeDisplay = preset },
                            label = { Text(preset, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = RadiantGold,
                                selectedLabelColor = Color.Black,
                                containerColor = DarkSurfaceElevated,
                                labelColor = TextSecondary
                            )
                        )
                    }
                }

                // Host Role
                OutlinedTextField(
                    value = hostRole,
                    onValueChange = { hostRole = it },
                    label = { Text("Your Squad Role (e.g. IGL, Fragger)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberCyan,
                        unfocusedBorderColor = DarkCardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        onCreate(
                            title, selectedGame, selectedGenre, timeDisplay, capacity,
                            selectedPlatforms, voiceRoom, mode, description, hostRole
                        )
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyberCyan,
                        contentColor = CyberCyanDark
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("confirm_create_session_btn")
                ) {
                    Text("Publish Scheduled Session", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
fun LogMatchDialog(
    games: List<GameTitle>,
    onDismiss: () -> Unit,
    onLog: (gameId: String, outcome: String, isVictory: Boolean, kills: Int, deaths: Int, assists: Int, map: String) -> Unit
) {
    var selectedGameId by remember { mutableStateOf(games.firstOrNull()?.id ?: "g_val") }
    var isVictory by remember { mutableStateOf(true) }
    var kills by remember { mutableStateOf("18") }
    var deaths by remember { mutableStateOf("8") }
    var assists by remember { mutableStateOf("6") }
    var mapName by remember { mutableStateOf("Haven") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = DarkSurfaceVariant,
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth().testTag("log_match_dialog")
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "RECORD COMPETITIVE MATCH",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black
                )

                // Victory / Defeat Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { isVictory = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isVictory) NeonGreen else DarkSurfaceElevated,
                            contentColor = if (isVictory) Color.Black else TextSecondary
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("VICTORY", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { isVictory = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (!isVictory) NeonRed else DarkSurfaceElevated,
                            contentColor = if (!isVictory) Color.White else TextSecondary
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("DEFEAT", fontWeight = FontWeight.Bold)
                    }
                }

                OutlinedTextField(
                    value = mapName,
                    onValueChange = { mapName = it },
                    label = { Text("Map / Arena Name") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberCyan,
                        unfocusedBorderColor = DarkCardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = kills,
                        onValueChange = { kills = it },
                        label = { Text("Kills") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = deaths,
                        onValueChange = { deaths = it },
                        label = { Text("Deaths") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = assists,
                        onValueChange = { assists = it },
                        label = { Text("Assists") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Button(
                    onClick = {
                        val k = kills.toIntOrNull() ?: 0
                        val d = deaths.toIntOrNull() ?: 1
                        val a = assists.toIntOrNull() ?: 0
                        val outcome = if (isVictory) "VICTORY" else "DEFEAT"
                        onLog(selectedGameId, outcome, isVictory, k, d, a, mapName)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = CyberCyanDark),
                    modifier = Modifier.fillMaxWidth().testTag("save_match_result_btn")
                ) {
                    Text("Save to Stats & Recalculate Rank", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun PostHighlightDialog(
    onDismiss: () -> Unit,
    onPost: (title: String, game: String, tags: List<String>) -> Unit
) {
    var title by remember { mutableStateOf("1v3 Fast Ace on Ascent B Site") }
    var gameTitle by remember { mutableStateOf("Valorant") }
    var tagText by remember { mutableStateOf("Ace, Clutch, Ascendant") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = DarkSurfaceVariant,
            border = androidx.compose.foundation.BorderStroke(1.dp, RadiantGold.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth().testTag("post_highlight_dialog")
        ) {
            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(text = "SHARE INSTANT REPLAY HIGHLIGHT", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Black)

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Moment Title") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = gameTitle,
                    onValueChange = { gameTitle = it },
                    label = { Text("Game Title") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = tagText,
                    onValueChange = { tagText = it },
                    label = { Text("Tags (comma separated)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        val tags = tagText.split(",").map { it.trim() }.filter { it.isNotBlank() }
                        onPost(title, gameTitle, tags)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RadiantGold, contentColor = Color.Black),
                    modifier = Modifier.fillMaxWidth().testTag("confirm_upload_highlight_btn")
                ) {
                    Text("Post Highlight Clip", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun UserProfileDetailDialog(
    user: GamerProfile,
    onDismiss: () -> Unit,
    onToggleFollow: () -> Unit,
    onDirectMessage: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = DarkSurfaceVariant,
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth().testTag("user_profile_detail_dialog")
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "PLAYER IDENTITY", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                UserAvatar(profile = user, size = 64.dp)

                Text(text = user.displayName, color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Black)
                Text(text = user.tag, color = CyberCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    user.platformsLinked.forEach { platform ->
                        PlatformBadge(platform = platform, compact = true)
                    }
                }

                Text(text = user.bio, color = TextSecondary, fontSize = 12.sp, lineHeight = 16.sp)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DarkSurfaceElevated, RoundedCornerShape(8.dp))
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "${user.followersCount}", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text(text = "Followers", color = TextSecondary, fontSize = 10.sp)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "${user.followingCount}", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text(text = "Following", color = TextSecondary, fontSize = 10.sp)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = user.winRate, color = NeonGreen, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text(text = "Win Rate", color = TextSecondary, fontSize = 10.sp)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onToggleFollow,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(if (user.isFollowing) "Unfollow" else "Follow")
                    }

                    Button(
                        onClick = onDirectMessage,
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = CyberCyanDark),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Party Invite")
                    }
                }
            }
        }
    }
}

@Composable
fun PresenceSelectorDialog(
    currentUser: GamerProfile,
    onDismiss: () -> Unit,
    onUpdate: (status: UserStatus, activity: String, platform: Platform) -> Unit
) {
    var selectedStatus by remember { mutableStateOf(currentUser.status) }
    var selectedPlatform by remember { mutableStateOf(currentUser.currentPlatform) }
    var activityTitle by remember { mutableStateOf(currentUser.currentActivityTitle) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = DarkSurfaceVariant,
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth().testTag("presence_selector_dialog")
        ) {
            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(text = "CHANGE STATUS & ACTIVE PLATFORM", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Black)

                Text(text = "STATUS", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(UserStatus.values().toList()) { status ->
                        val isSelected = selectedStatus == status
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedStatus = status },
                            label = { Text(status.label, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CyberCyan,
                                selectedLabelColor = CyberCyanDark,
                                containerColor = DarkSurfaceElevated,
                                labelColor = TextSecondary
                            )
                        )
                    }
                }

                Text(text = "ACTIVE GAMING DEVICE", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(Platform.values().toList()) { platform ->
                        val isSelected = selectedPlatform == platform
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedPlatform = platform },
                            label = { Text(platform.displayName, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = platform.brandColor.copy(alpha = 0.2f),
                                selectedLabelColor = platform.brandColor,
                                containerColor = DarkSurfaceElevated,
                                labelColor = TextSecondary
                            )
                        )
                    }
                }

                OutlinedTextField(
                    value = activityTitle,
                    onValueChange = { activityTitle = it },
                    label = { Text("What are you playing?") },
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = { onUpdate(selectedStatus, activityTitle, selectedPlatform) },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = CyberCyanDark),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Apply Live Status", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
