package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ReplayHighlight
import com.example.ui.components.PlatformBadge
import com.example.ui.components.UserAvatar
import com.example.ui.theme.*
import com.example.ui.viewmodel.GamingViewModel

@Composable
fun ReplaysScreen(
    viewModel: GamingViewModel,
    modifier: Modifier = Modifier
) {
    val replays by viewModel.replays.collectAsState()
    val selectedReplay by viewModel.selectedReplay.collectAsState()

    var isPlaying by remember { mutableStateOf(false) }
    var currentPlaybackSec by remember { mutableStateOf(0) }

    LaunchedEffect(isPlaying, selectedReplay) {
        if (isPlaying) {
            val maxSec = selectedReplay?.durationSec ?: 30
            while (isPlaying && currentPlaybackSec < maxSec) {
                kotlinx.coroutines.delay(1000)
                currentPlaybackSec += 1
            }
            if (currentPlaybackSec >= maxSec) {
                isPlaying = false
                currentPlaybackSec = 0
            }
        }
    }

    Scaffold(
        modifier = modifier.testTag("replays_screen"),
        containerColor = Color.Transparent,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.setShowPostHighlightDialog(true) },
                icon = { Icon(Icons.Default.Videocam, contentDescription = null) },
                text = { Text("Upload Highlight", fontWeight = FontWeight.Bold) },
                containerColor = RadiantGold,
                contentColor = Color.Black,
                modifier = Modifier
                    .padding(bottom = 72.dp)
                    .testTag("upload_highlight_fab")
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
                    Text(
                        text = "INSTANT REPLAY HIGHLIGHTS",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Shared gaming moments, clutch defuses, aerial goals, and competitive aces.",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }

            // Featured / Selected Active Player
            if (selectedReplay != null) {
                item {
                    val active = selectedReplay!!
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.dp, CyberCyan.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                            .testTag("active_replay_player"),
                        color = DarkSurfaceElevated
                    ) {
                        Column {
                            // Video Canvas / Viewport simulation
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = active.thumbnailDrawableRes),
                                    contentDescription = active.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )

                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color(0x44000000)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    IconButton(
                                        onClick = { isPlaying = !isPlaying },
                                        modifier = Modifier
                                            .size(56.dp)
                                            .clip(CircleShape)
                                            .background(CyberCyan.copy(alpha = 0.9f))
                                            .testTag("play_pause_video_btn")
                                    ) {
                                        Icon(
                                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                            contentDescription = if (isPlaying) "Pause" else "Play",
                                            tint = CyberCyanDark,
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }
                                }

                                // Timeline Position Badge
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color.Black.copy(alpha = 0.8f),
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(8.dp)
                                ) {
                                    Text(
                                        text = "0:${if (currentPlaybackSec < 10) "0$currentPlaybackSec" else "$currentPlaybackSec"} / 0:${active.durationSec}",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            // Interactive Timeline Scrubber with Bookmark Markers
                            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                                Slider(
                                    value = currentPlaybackSec.toFloat(),
                                    onValueChange = {
                                        currentPlaybackSec = it.toInt()
                                        isPlaying = false
                                    },
                                    valueRange = 0f..active.durationSec.toFloat(),
                                    colors = SliderDefaults.colors(
                                        thumbColor = CyberCyan,
                                        activeTrackColor = CyberCyan,
                                        inactiveTrackColor = DarkCardBorder
                                    )
                                )

                                // Key Moment Bookmarks (e.g. Ace, Clutch, Win)
                                if (active.keyMoments.isNotEmpty()) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = "Moments:", color = TextMuted, fontSize = 11.sp)
                                        active.keyMoments.forEach { moment ->
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = if (currentPlaybackSec >= moment.second) RadiantGold.copy(alpha = 0.2f) else DarkSurfaceVariant,
                                                border = androidx.compose.foundation.BorderStroke(1.dp, RadiantGold.copy(alpha = 0.4f)),
                                                modifier = Modifier
                                                    .clickable {
                                                        currentPlaybackSec = moment.second
                                                        isPlaying = true
                                                    }
                                                    .testTag("moment_bookmark_${moment.second}")
                                            ) {
                                                Text(
                                                    text = "${moment.label} (0:${moment.second}s)",
                                                    color = RadiantGold,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Clip Title & Info
                                Text(
                                    text = active.title,
                                    color = TextPrimary,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        UserAvatar(profile = active.author, size = 30.dp)
                                        Column {
                                            Text(
                                                text = active.author.displayName,
                                                color = TextPrimary,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = "${active.gameTitle} • ${active.timestamp}",
                                                color = TextSecondary,
                                                fontSize = 10.sp
                                            )
                                        }
                                        PlatformBadge(platform = active.author.currentPlatform, compact = true)
                                    }

                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        IconButton(
                                            onClick = { viewModel.toggleLikeReplay(active.id) },
                                            modifier = Modifier.testTag("like_clip_btn")
                                        ) {
                                            Icon(
                                                imageVector = if (active.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                                contentDescription = "Like",
                                                tint = if (active.isLiked) NeonRed else TextSecondary,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        Text(text = "${active.likesCount}", color = TextSecondary, fontSize = 12.sp)

                                        IconButton(
                                            onClick = {
                                                viewModel.sendChatMessage(
                                                    "Check out this play: ${active.title}",
                                                    active.title,
                                                    active.gameTitle
                                                )
                                            },
                                            modifier = Modifier.testTag("share_clip_to_chat_btn")
                                        ) {
                                            Icon(
                                                Icons.Default.Share,
                                                contentDescription = "Share to Chat",
                                                tint = CyberCyan,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // All Highlights Feed
            item {
                Text(
                    text = "COMMUNITY HIGHLIGHTS FEED (${replays.size})",
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            items(replays) { replay ->
                ReplayFeedItem(
                    replay = replay,
                    isSelected = selectedReplay?.id == replay.id,
                    onSelect = {
                        viewModel.selectReplay(replay)
                        currentPlaybackSec = 0
                        isPlaying = true
                    },
                    onLike = { viewModel.toggleLikeReplay(replay.id) }
                )
            }
        }
    }
}

@Composable
fun ReplayFeedItem(
    replay: ReplayHighlight,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onLike: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onSelect() }
            .testTag("replay_item_${replay.id}"),
        color = if (isSelected) DarkSurfaceElevated else DarkSurfaceVariant,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) CyberCyan else DarkCardBorder
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                Image(
                    painter = painterResource(id = replay.thumbnailDrawableRes),
                    contentDescription = replay.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0x44000000)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.PlayCircle,
                        contentDescription = null,
                        tint = CyberCyan,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = replay.title,
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(text = replay.author.displayName, color = TextSecondary, fontSize = 11.sp)
                    PlatformBadge(platform = replay.author.currentPlatform, compact = true)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${replay.gameTitle} • 0:${replay.durationSec}s",
                        color = CyberCyan,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp),
                            modifier = Modifier.clickable { onLike() }
                        ) {
                            Icon(
                                imageVector = if (replay.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = null,
                                tint = if (replay.isLiked) NeonRed else TextSecondary,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(text = "${replay.likesCount}", color = TextSecondary, fontSize = 10.sp)
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Icon(Icons.Default.Visibility, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(14.dp))
                            Text(text = "${replay.viewsCount}", color = TextSecondary, fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }
}
