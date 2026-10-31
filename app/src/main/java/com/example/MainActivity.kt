package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.GamingViewModel
import com.example.ui.viewmodel.ScreenTab

class MainActivity : ComponentActivity() {
    private val viewModel: GamingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                NexusGamingApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun NexusGamingApp(viewModel: GamingViewModel) {
    val currentTab by viewModel.currentTab.collectAsState()
    val activeVoiceRoom by viewModel.activeVoiceRoom.collectAsState()
    val isMicMuted by viewModel.isMicMuted.collectAsState()
    val isDeafened by viewModel.isDeafened.collectAsState()

    // Dialog states
    val showCreateSession by viewModel.showCreateSessionDialog.collectAsState()
    val showLogMatch by viewModel.showLogMatchDialog.collectAsState()
    val showPostHighlight by viewModel.showPostHighlightDialog.collectAsState()
    val viewingProfileUser by viewModel.viewingProfileUser.collectAsState()
    val showPresenceSelector by viewModel.showPresenceSelector.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val games by viewModel.games.collectAsState()

    // Android System Back Handling
    BackHandler(enabled = true) {
        viewModel.handleBack()
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize().background(DarkBg)) {
        val isWideScreen = maxWidth >= 600.dp

        if (isWideScreen) {
            // Adaptive Tablet / Foldable NavigationRail Layout
            Row(modifier = Modifier.fillMaxSize()) {
                NavigationRail(
                    containerColor = DarkSurface,
                    contentColor = TextPrimary,
                    modifier = Modifier.testTag("tablet_nav_rail")
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    listOf(
                        ScreenTab.DASHBOARD to Icons.Default.Dashboard,
                        ScreenTab.SESSIONS to Icons.Default.GroupAdd,
                        ScreenTab.DISCOVERY to Icons.Default.Explore,
                        ScreenTab.VOICE_CHAT to Icons.Default.HeadsetMic,
                        ScreenTab.STATS to Icons.Default.Leaderboard,
                        ScreenTab.REPLAYS to Icons.Default.Videocam,
                        ScreenTab.FRIENDS to Icons.Default.People,
                        ScreenTab.PROFILE to Icons.Default.AccountCircle
                    ).forEach { (tab, icon) ->
                        val isSelected = currentTab == tab
                        NavigationRailItem(
                            selected = isSelected,
                            onClick = { viewModel.navigateTo(tab) },
                            icon = {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = tab.title,
                                    tint = if (isSelected) CyberCyan else TextSecondary
                                )
                            },
                            label = {
                                Text(
                                    text = tab.title,
                                    color = if (isSelected) CyberCyan else TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationRailItemDefaults.colors(
                                indicatorColor = CyberCyan.copy(alpha = 0.2f)
                            )
                        )
                    }
                }

                Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                    ScreenContent(tab = currentTab, viewModel = viewModel)

                    // Docked Voice bar if active
                    if (activeVoiceRoom != null) {
                        ActiveVoiceDockedBar(
                            activeRoom = activeVoiceRoom!!,
                            isMicMuted = isMicMuted,
                            isDeafened = isDeafened,
                            onToggleMic = { viewModel.toggleMicMute() },
                            onToggleDeafen = { viewModel.toggleDeafen() },
                            onLeave = { viewModel.leaveVoiceRoom() },
                            onOpenComms = { viewModel.navigateTo(ScreenTab.VOICE_CHAT) },
                            modifier = Modifier
                                .align(androidx.compose.ui.Alignment.BottomCenter)
                                .windowInsetsPadding(WindowInsets.navigationBars)
                        )
                    }
                }
            }
        } else {
            // Mobile Phone Handheld Layout with bottom NavigationBar
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = DarkBg,
                contentWindowInsets = WindowInsets.safeDrawing,
                bottomBar = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                    ) {
                        // Docked Voice Bar right above navigation bar when connected
                        if (activeVoiceRoom != null && currentTab != ScreenTab.VOICE_CHAT) {
                            ActiveVoiceDockedBar(
                                activeRoom = activeVoiceRoom!!,
                                isMicMuted = isMicMuted,
                                isDeafened = isDeafened,
                                onToggleMic = { viewModel.toggleMicMute() },
                                onToggleDeafen = { viewModel.toggleDeafen() },
                                onLeave = { viewModel.leaveVoiceRoom() },
                                onOpenComms = { viewModel.navigateTo(ScreenTab.VOICE_CHAT) }
                            )
                        }

                        NavigationBar(
                            containerColor = DarkSurface,
                            contentColor = TextPrimary,
                            tonalElevation = 8.dp,
                            modifier = Modifier.testTag("main_bottom_nav")
                        ) {
                            val primaryNavTabs = listOf(
                                ScreenTab.DASHBOARD to Icons.Default.Dashboard,
                                ScreenTab.SESSIONS to Icons.Default.GroupAdd,
                                ScreenTab.DISCOVERY to Icons.Default.Explore,
                                ScreenTab.VOICE_CHAT to Icons.Default.HeadsetMic,
                                ScreenTab.STATS to Icons.Default.Leaderboard,
                                ScreenTab.REPLAYS to Icons.Default.Videocam,
                                ScreenTab.FRIENDS to Icons.Default.People
                            )

                            primaryNavTabs.forEach { (tab, icon) ->
                                val isSelected = currentTab == tab
                                NavigationBarItem(
                                    selected = isSelected,
                                    onClick = { viewModel.navigateTo(tab) },
                                    icon = {
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = tab.title,
                                            tint = if (isSelected) CyberCyan else TextSecondary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = tab.title,
                                            color = if (isSelected) CyberCyan else TextSecondary,
                                            fontSize = 9.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            maxLines = 1
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        indicatorColor = CyberCyan.copy(alpha = 0.2f)
                                    ),
                                    modifier = Modifier.testTag("nav_tab_${tab.name}")
                                )
                            }
                        }
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    ScreenContent(tab = currentTab, viewModel = viewModel)
                }
            }
        }

        // Dialogs
        if (showCreateSession) {
            CreateSessionDialog(
                onDismiss = { viewModel.setShowCreateSessionDialog(false) },
                onCreate = { title, game, genre, time, capacity, platforms, voice, mode, desc, role ->
                    viewModel.createSession(title, game, genre, time, capacity, platforms, voice, mode, desc, role)
                    viewModel.setShowCreateSessionDialog(false)
                }
            )
        }

        if (showLogMatch) {
            LogMatchDialog(
                games = games,
                onDismiss = { viewModel.setShowLogMatchDialog(false) },
                onLog = { gameId, outcome, isVic, k, d, a, map ->
                    viewModel.logMatch(gameId, outcome, isVic, k, d, a, map)
                    viewModel.setShowLogMatchDialog(false)
                }
            )
        }

        if (showPostHighlight) {
            PostHighlightDialog(
                onDismiss = { viewModel.setShowPostHighlightDialog(false) },
                onPost = { title, game, tags ->
                    viewModel.postReplayHighlight(title, game, tags)
                    viewModel.setShowPostHighlightDialog(false)
                }
            )
        }

        if (viewingProfileUser != null) {
            UserProfileDetailDialog(
                user = viewingProfileUser!!,
                onDismiss = { viewModel.setViewingProfileUser(null) },
                onToggleFollow = { viewModel.toggleFollow(viewingProfileUser!!.id) },
                onDirectMessage = {
                    val user = viewingProfileUser!!
                    viewModel.setViewingProfileUser(null)
                    viewModel.sendChatMessage("Party invite sent to @${user.displayName}!")
                    viewModel.navigateTo(ScreenTab.VOICE_CHAT)
                }
            )
        }

        if (showPresenceSelector) {
            PresenceSelectorDialog(
                currentUser = currentUser,
                onDismiss = { viewModel.setShowPresenceSelector(false) },
                onUpdate = { status, act, platform ->
                    viewModel.updatePresence(status, act, platform)
                    viewModel.setShowPresenceSelector(false)
                }
            )
        }
    }
}

@Composable
fun ScreenContent(tab: ScreenTab, viewModel: GamingViewModel) {
    when (tab) {
        ScreenTab.DASHBOARD -> DashboardScreen(viewModel = viewModel)
        ScreenTab.SESSIONS -> SessionsScreen(viewModel = viewModel)
        ScreenTab.DISCOVERY -> DiscoveryScreen(viewModel = viewModel)
        ScreenTab.VOICE_CHAT -> VoiceChatScreen(viewModel = viewModel)
        ScreenTab.STATS -> StatsScreen(viewModel = viewModel)
        ScreenTab.REPLAYS -> ReplaysScreen(viewModel = viewModel)
        ScreenTab.FRIENDS -> FriendsScreen(viewModel = viewModel)
        ScreenTab.PROFILE -> ProfileScreen(viewModel = viewModel)
    }
}
