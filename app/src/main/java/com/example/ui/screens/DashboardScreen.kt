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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.*
import com.example.ui.components.PlatformBadge
import com.example.ui.components.StatusDot
import com.example.ui.components.UserAvatar
import com.example.ui.theme.*
import com.example.ui.viewmodel.GamingViewModel
import com.example.ui.viewmodel.ScreenTab

@Composable
fun DashboardScreen(
    viewModel: GamingViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val friends by viewModel.friends.collectAsState()
    val sessions by viewModel.sessions.collectAsState()
    val replays by viewModel.replays.collectAsState()
    val stats by viewModel.stats.collectAsState()
    val recommendations by viewModel.gameRecommendations.collectAsState()
    val friendSuggestions by viewModel.friendSuggestions.collectAsState()
    val activeVoiceRoom by viewModel.activeVoiceRoom.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // 1. Dashboard Top Header: User Profile Presence & Platform Selector
        item {
            DashboardHeader(
                user = currentUser,
                activeVoice = activeVoiceRoom != null,
                onPresenceClick = { viewModel.setShowPresenceSelector(true) },
                onProfileClick = { viewModel.navigateTo(ScreenTab.PROFILE) }
            )
        }

        // 2. Hero Interactive Gaming Banner: "Live Squad Lobby & Comms"
        item {
            HeroGamingBanner(
                activeVoice = activeVoiceRoom,
                onJoinComms = { viewModel.navigateTo(ScreenTab.VOICE_CHAT) },
                onCreateSession = { viewModel.setShowCreateSessionDialog(true) }
            )
        }

        // 3. Friends Online & In-Game Carousel
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "PARTY & FRIENDS",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Surface(
                            shape = CircleShape,
                            color = NeonGreen.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "${friends.count { it.status != UserStatus.OFFLINE }} LIVE",
                                color = NeonGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }

                    TextButton(
                        onClick = { viewModel.navigateTo(ScreenTab.FRIENDS) },
                        modifier = Modifier.testTag("see_all_friends_btn")
                    ) {
                        Text("View Social", color = CyberCyan, fontSize = 13.sp)
                        Icon(
                            Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = CyberCyan,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(friends) { friend ->
                        FriendQuickCard(
                            friend = friend,
                            onClick = { viewModel.setViewingProfileUser(friend) }
                        )
                    }
                }
            }
        }

        // 4. Next Scheduled Session / Custom Organizer Preview
        item {
            val nextSession = sessions.firstOrNull()
            if (nextSession != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                Icons.Default.Event,
                                contentDescription = null,
                                tint = RadiantGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "NEXT SQUAD SESSION",
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }

                        TextButton(onClick = { viewModel.navigateTo(ScreenTab.SESSIONS) }) {
                            Text("All Sessions (${sessions.size})", color = CyberCyan, fontSize = 13.sp)
                        }
                    }

                    ScheduledSessionHeroCard(
                        session = nextSession,
                        onToggleJoin = { viewModel.toggleJoinSession(nextSession.id) },
                        onDetails = { viewModel.navigateTo(ScreenTab.SESSIONS) }
                    )
                }
            }
        }

        // 5. Game Discovery Spotlight (New Recommendations from Engine)
        item {
            val topRec = recommendations.firstOrNull()
            if (topRec != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                Icons.Default.Explore,
                                contentDescription = null,
                                tint = ElectricPurple,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "DISCOVERY ENGINE",
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }

                        TextButton(onClick = { viewModel.navigateTo(ScreenTab.DISCOVERY) }) {
                            Text("Open Discovery", color = CyberCyan, fontSize = 13.sp)
                            Icon(
                                Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = CyberCyan,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    DiscoverySpotlightCard(
                        recommendation = topRec,
                        onOpen = { viewModel.navigateTo(ScreenTab.DISCOVERY) },
                        onToggleWishlist = { viewModel.toggleWishlistGame(topRec.game.id) }
                    )
                }
            }
        }

        // 6. Competitive Rank & Stats Snapshot
        item {
            val topStat = stats.firstOrNull()
            if (topStat != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                Icons.Default.TrendingUp,
                                contentDescription = null,
                                tint = CyberCyan,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "COMPETITIVE STATS",
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }

                        TextButton(onClick = { viewModel.navigateTo(ScreenTab.STATS) }) {
                            Text("Detailed Analytics", color = CyberCyan, fontSize = 13.sp)
                        }
                    }

                    StatsQuickSnapshotCard(
                        stat = topStat,
                        onViewStats = { viewModel.navigateTo(ScreenTab.STATS) }
                    )
                }
            }
        }

        // 7. Instant Replay Highlight Spotlight
        item {
            val topReplay = replays.firstOrNull()
            if (topReplay != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                Icons.Default.Videocam,
                                contentDescription = null,
                                tint = NeonRed,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "TRENDING REPLAY CLIP",
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }

                        TextButton(onClick = { viewModel.navigateTo(ScreenTab.REPLAYS) }) {
                            Text("Clips Hub (${replays.size})", color = CyberCyan, fontSize = 13.sp)
                        }
                    }

                    TrendingReplayCard(
                        replay = topReplay,
                        onPlay = {
                            viewModel.selectReplay(topReplay)
                            viewModel.navigateTo(ScreenTab.REPLAYS)
                        },
                        onLike = { viewModel.toggleLikeReplay(topReplay.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun DashboardHeader(
    user: GamerProfile,
    activeVoice: Boolean,
    onPresenceClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("dashboard_header"),
        color = DarkSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .clickable { onProfileClick() }
                        .testTag("header_profile_click")
                ) {
                    UserAvatar(profile = user, size = 46.dp)
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = user.tag,
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black
                            )
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = CyberCyan.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "LVL ${user.level}",
                                    color = CyberCyan,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Text(
                            text = user.currentActivityTitle,
                            color = TextSecondary,
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Presence and Platform Selector Button
                OutlinedButton(
                    onClick = onPresenceClick,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberCyan),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.5f)),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("change_presence_btn")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        StatusDot(status = user.status)
                        PlatformBadge(platform = user.currentPlatform, compact = true)
                        Icon(
                            Icons.Default.ArrowDropDown,
                            contentDescription = "Change Status",
                            tint = CyberCyan,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HeroGamingBanner(
    activeVoice: VoiceRoom?,
    onJoinComms: () -> Unit,
    onCreateSession: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, Brush.horizontalGradient(listOf(CyberCyan, ElectricPurple)), RoundedCornerShape(16.dp))
            .testTag("hero_gaming_banner"),
        color = DarkSurfaceVariant
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = painterResource(id = R.drawable.banner_tactical),
                contentDescription = "Tactical Gaming Arena",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            )

            // Cyber gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0x77080B11),
                                Color(0xEE080B11),
                                Color(0xFF080B11)
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = CyberCyan.copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan)
                    ) {
                        Text(
                            text = "CROSS-PLATFORM SQUAD HUB",
                            color = CyberCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        PlatformBadge(platform = Platform.PC, compact = true)
                        PlatformBadge(platform = Platform.PLAYSTATION, compact = true)
                        PlatformBadge(platform = Platform.XBOX, compact = true)
                    }
                }

                Text(
                    text = "Coordinate, Chat & Dominate",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black
                )

                Text(
                    text = if (activeVoice != null)
                        "Connected to ${activeVoice.name} (${activeVoice.pingMs}ms)"
                    else
                        "Voice channels, custom scheduling, and shared instant replays ready.",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(2.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onCreateSession,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CyberCyan,
                            contentColor = CyberCyanDark
                        ),
                        modifier = Modifier.testTag("hero_create_session_btn")
                    ) {
                        Icon(
                            Icons.Default.AddCircleOutline,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Create Squad", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    OutlinedButton(
                        onClick = onJoinComms,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ElectricPurple),
                        modifier = Modifier.testTag("hero_join_comms_btn")
                    ) {
                        Icon(
                            Icons.Default.Headset,
                            contentDescription = null,
                            tint = ElectricPurple,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Voice & Chat", fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun FriendQuickCard(
    friend: GamerProfile,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .width(136.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .testTag("friend_quick_card_${friend.id}"),
        color = DarkSurfaceVariant,
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            UserAvatar(profile = friend, size = 48.dp)

            Text(
                text = friend.displayName,
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            PlatformBadge(platform = friend.currentPlatform, compact = true)

            Text(
                text = friend.currentActivityTitle,
                color = if (friend.status == UserStatus.IN_GAME) NeonGreen else TextSecondary,
                fontSize = 10.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun ScheduledSessionHeroCard(
    session: GameSession,
    onToggleJoin: () -> Unit,
    onDetails: () -> Unit
) {
    val filledSlots = session.slots.count { it.user != null }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onDetails() }
            .testTag("scheduled_session_hero_card"),
        color = DarkSurfaceElevated,
        border = androidx.compose.foundation.BorderStroke(1.dp, RadiantGold.copy(alpha = 0.35f))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
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
                            Icons.Default.Schedule,
                            contentDescription = null,
                            tint = RadiantGold,
                            modifier = Modifier.size(14.dp)
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

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = session.title,
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${session.gameTitle} • ${session.gameMode} • ${session.genre.title}",
                    color = CyberCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Roster slots preview
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    session.slots.forEach { slot ->
                        val u = slot.user
                        if (u != null) {
                            UserAvatar(profile = u, size = 28.dp, showStatusDot = false)
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .border(1.dp, TextSecondary.copy(alpha = 0.5f), CircleShape)
                                    .background(DarkSurfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.PersonAdd,
                                    contentDescription = "Open Slot",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }

                    Text(
                        text = "$filledSlots/${session.maxPlayers} Filled",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                Button(
                    onClick = onToggleJoin,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (session.isJoined) NeonRed else CyberCyan,
                        contentColor = if (session.isJoined) Color.White else CyberCyanDark
                    ),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("session_hero_rsvp_btn")
                ) {
                    Text(
                        text = if (session.isJoined) "Leave Squad" else "RSVP / Join",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun DiscoverySpotlightCard(
    recommendation: GameRecommendation,
    onOpen: () -> Unit,
    onToggleWishlist: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onOpen() }
            .testTag("discovery_spotlight_card"),
        color = DarkSurfaceVariant,
        border = androidx.compose.foundation.BorderStroke(1.dp, ElectricPurple.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = ElectricPurple.copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ElectricPurple)
                ) {
                    Text(
                        text = "${recommendation.matchScore}% AFFINITY MATCH",
                        color = ElectricPurple,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                IconButton(
                    onClick = onToggleWishlist,
                    modifier = Modifier.size(32.dp).testTag("spotlight_wishlist_btn")
                ) {
                    Icon(
                        imageVector = if (recommendation.isWishlisted) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Save Game",
                        tint = if (recommendation.isWishlisted) CyberCyan else TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = recommendation.game.name,
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = recommendation.recommendationReason,
                    color = TextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    recommendation.supportedPlatforms.forEach { platform ->
                        PlatformBadge(platform = platform, compact = true)
                    }
                }

                Text(
                    text = recommendation.priceOrPass,
                    color = NeonGreen,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun StatsQuickSnapshotCard(
    stat: PlayerStats,
    onViewStats: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onViewStats() }
            .testTag("stats_quick_snapshot_card"),
        color = DarkSurfaceElevated,
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.35f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = stat.gameTitle,
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = RadiantGold.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = stat.rankName,
                            color = RadiantGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Text(text = stat.rankTier, color = TextSecondary, fontSize = 11.sp)
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${stat.winRatePct}%",
                        color = NeonGreen,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text("Win Rate", color = TextSecondary, fontSize = 11.sp)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${stat.kdRatio}",
                        color = CyberCyan,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text("K/D", color = TextSecondary, fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun TrendingReplayCard(
    replay: ReplayHighlight,
    onPlay: () -> Unit,
    onLike: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onPlay() }
            .testTag("trending_replay_card"),
        color = DarkSurfaceVariant,
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                Image(
                    painter = painterResource(id = replay.thumbnailDrawableRes),
                    contentDescription = replay.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Overlay play button
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0x55000000)),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(CyberCyan.copy(alpha = 0.85f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.PlayArrow,
                            contentDescription = "Play Replay",
                            tint = CyberCyanDark,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                // Duration badge
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color.Black.copy(alpha = 0.8f),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                ) {
                    Text(
                        text = "0:${if (replay.durationSec < 10) "0${replay.durationSec}" else "${replay.durationSec}"}",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = replay.title,
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        UserAvatar(profile = replay.author, size = 22.dp, showStatusDot = false)
                        Text(
                            text = replay.author.displayName,
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.clickable { onLike() }
                        ) {
                            Icon(
                                imageVector = if (replay.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Like",
                                tint = if (replay.isLiked) NeonRed else TextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "${replay.likesCount}",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                Icons.Default.Visibility,
                                contentDescription = "Views",
                                tint = TextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "${replay.viewsCount}",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
