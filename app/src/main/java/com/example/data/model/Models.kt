package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.ColorMobile
import com.example.ui.theme.ColorNintendo
import com.example.ui.theme.ColorPC
import com.example.ui.theme.ColorPlayStation
import com.example.ui.theme.ColorXbox

enum class Platform(val displayName: String, val brandColor: Color, val shortTag: String) {
    PC("PC / Steam", ColorPC, "PC"),
    PLAYSTATION("PlayStation 5", ColorPlayStation, "PS5"),
    XBOX("Xbox Series X", ColorXbox, "XBX"),
    MOBILE("Mobile (iOS/Android)", ColorMobile, "MOB"),
    SWITCH("Nintendo Switch", ColorNintendo, "NSW")
}

enum class UserStatus(val label: String) {
    ONLINE("Online"),
    IN_GAME("In Game"),
    IN_VOICE("In Voice Room"),
    AWAY("Away"),
    OFFLINE("Offline")
}

enum class GameGenre(val title: String) {
    ALL("All Genres"),
    FPS_TACTICAL("Tactical FPS"),
    BATTLE_ROYALE("Battle Royale"),
    MOBA("MOBA"),
    FIGHTING("Fighting"),
    RACING("Racing"),
    SPORTS("Sports"),
    RPG_ACTION("Action RPG"),
    STRATEGY("Strategy / RTS")
}

data class GamerProfile(
    val id: String,
    val tag: String,
    val displayName: String,
    val avatarInitial: String,
    val avatarColorHex: Long = 0xFF00F0FF,
    val status: UserStatus = UserStatus.ONLINE,
    val currentActivityTitle: String = "",
    val currentPlatform: Platform = Platform.PC,
    val level: Int = 50,
    val followersCount: Int = 120,
    val followingCount: Int = 85,
    val isFollowing: Boolean = false,
    val isFriend: Boolean = true,
    val bio: String = "Competitive player | Cross-platform grinder",
    val platformsLinked: List<Platform> = listOf(Platform.PC, Platform.PLAYSTATION),
    val badges: List<String> = listOf("Tactical Ace", "Night Owl", "MVP"),
    val winRate: String = "64.2%",
    val kdRatio: String = "2.18"
)

data class GameTitle(
    val id: String,
    val name: String,
    val genre: GameGenre,
    val activePlayers: String,
    val isInstalled: Boolean = true,
    val bannerDrawableRes: Int? = null
)

data class MatchRecord(
    val id: String,
    val outcome: String, // "VICTORY", "DEFEAT", "1ST PLACE"
    val isVictory: Boolean,
    val score: String,
    val kills: Int,
    val deaths: Int,
    val assists: Int,
    val map: String,
    val timeAgo: String,
    val hasHighlight: Boolean = false
)

data class PlayerStats(
    val gameId: String,
    val gameTitle: String,
    val genre: GameGenre,
    val rankName: String,
    val rankTier: String,
    val winRatePct: Float,
    val kdRatio: Float,
    val matchesPlayed: Int,
    val hoursLogged: Int,
    val headshotPct: Float,
    val mvpCount: Int,
    val recentMatches: List<MatchRecord> = emptyList()
)

data class SessionSlot(
    val slotIndex: Int,
    val role: String, // "IGL", "Entry Fragger", "Support", "Sniper", "Flex"
    val user: GamerProfile? = null
)

data class GameSession(
    val id: String,
    val title: String,
    val gameTitle: String,
    val genre: GameGenre,
    val scheduledTimeDisplay: String,
    val scheduledTimestamp: Long,
    val hostUser: GamerProfile,
    val maxPlayers: Int,
    val slots: List<SessionSlot>,
    val crossPlayPlatforms: List<Platform>,
    val voiceChannelName: String,
    val gameMode: String, // e.g. "Ranked 5v5", "Casual Squads", "Tournament Qualifier"
    val description: String,
    val isJoined: Boolean = false,
    val myRole: String? = null
)

data class VoiceMember(
    val user: GamerProfile,
    val isMuted: Boolean = false,
    val isDeafened: Boolean = false,
    val isSpeaking: Boolean = false,
    val volume: Float = 0.85f
)

data class VoiceRoom(
    val id: String,
    val name: String,
    val gameTitle: String,
    val maxCapacity: Int = 10,
    val members: List<VoiceMember> = emptyList(),
    val isConnected: Boolean = false,
    val bitrate: String = "96 kbps HQ",
    val pingMs: Int = 18
)

data class ChatMessage(
    val id: String,
    val sender: GamerProfile,
    val text: String,
    val timestamp: String,
    val platform: Platform,
    val isSelf: Boolean,
    val attachedClipTitle: String? = null,
    val attachedGame: String? = null
)

data class BookmarkMoment(
    val second: Int,
    val label: String,
    val type: String // "ACE", "CLUTCH", "WIN", "STEAL"
)

data class ReplayHighlight(
    val id: String,
    val title: String,
    val gameTitle: String,
    val author: GamerProfile,
    val durationSec: Int,
    val likesCount: Int,
    val commentsCount: Int,
    val viewsCount: Int,
    val isLiked: Boolean = false,
    val timestamp: String,
    val tags: List<String>,
    val thumbnailDrawableRes: Int,
    val keyMoments: List<BookmarkMoment> = emptyList()
)

data class GameRecommendation(
    val game: GameTitle,
    val matchScore: Int, // e.g. 96%
    val recommendationReason: String,
    val friendsPlaying: List<GamerProfile>,
    val supportedPlatforms: List<Platform>,
    val rating: Float = 4.8f,
    val priceOrPass: String = "Free to Play / Game Pass",
    val isWishlisted: Boolean = false
)

data class FriendSuggestion(
    val user: GamerProfile,
    val compatibilityScore: Int, // e.g. 95%
    val commonGames: List<String>,
    val sharedPlatform: Platform,
    val mutualFriendsCount: Int,
    val recommendationReason: String,
    val isRequestSent: Boolean = false
)

