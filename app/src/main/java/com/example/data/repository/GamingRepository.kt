package com.example.data.repository

import com.example.R
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GamingRepository(
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) {
    // Current logged-in user profile
    private val _currentUser = MutableStateFlow(
        GamerProfile(
            id = "user_me",
            tag = "ShadowKitsune#042",
            displayName = "Alex Rivera",
            avatarInitial = "SK",
            avatarColorHex = 0xFF00F0FF,
            status = UserStatus.ONLINE,
            currentActivityTitle = "Valorant [Competitive 5v5]",
            currentPlatform = Platform.PC,
            level = 88,
            followersCount = 342,
            followingCount = 128,
            isFollowing = false,
            isFriend = false,
            bio = "Competitive Ascendant IGL & Fragger. Cross-play enthusiast. Always down for ranked scrims!",
            platformsLinked = listOf(Platform.PC, Platform.PLAYSTATION, Platform.XBOX),
            badges = listOf("Ascendant Tier", "Clutch Master", "Verified IGL", "Highlight Creator"),
            winRate = "68.4%",
            kdRatio = "1.82"
        )
    )
    val currentUser: StateFlow<GamerProfile> = _currentUser.asStateFlow()

    // Friends list
    private val _friends = MutableStateFlow<List<GamerProfile>>(emptyList())
    val friends: StateFlow<List<GamerProfile>> = _friends.asStateFlow()

    // Followers
    private val _followers = MutableStateFlow<List<GamerProfile>>(emptyList())
    val followers: StateFlow<List<GamerProfile>> = _followers.asStateFlow()

    // Following
    private val _following = MutableStateFlow<List<GamerProfile>>(emptyList())
    val following: StateFlow<List<GamerProfile>> = _following.asStateFlow()

    // Games catalog
    private val _games = MutableStateFlow<List<GameTitle>>(emptyList())
    val games: StateFlow<List<GameTitle>> = _games.asStateFlow()

    // Stats across competitive titles
    private val _stats = MutableStateFlow<List<PlayerStats>>(emptyList())
    val stats: StateFlow<List<PlayerStats>> = _stats.asStateFlow()

    // Game Discovery Engine State
    private val _gameRecommendations = MutableStateFlow<List<GameRecommendation>>(emptyList())
    val gameRecommendations: StateFlow<List<GameRecommendation>> = _gameRecommendations.asStateFlow()

    private val _friendSuggestions = MutableStateFlow<List<FriendSuggestion>>(emptyList())
    val friendSuggestions: StateFlow<List<FriendSuggestion>> = _friendSuggestions.asStateFlow()

    private val _userPreferredGenres = MutableStateFlow<Set<GameGenre>>(
        setOf(GameGenre.FPS_TACTICAL, GameGenre.BATTLE_ROYALE, GameGenre.SPORTS, GameGenre.FIGHTING)
    )
    val userPreferredGenres: StateFlow<Set<GameGenre>> = _userPreferredGenres.asStateFlow()


    // Multiplayer Sessions (Scheduling)
    private val _sessions = MutableStateFlow<List<GameSession>>(emptyList())
    val sessions: StateFlow<List<GameSession>> = _sessions.asStateFlow()

    // Voice Rooms
    private val _voiceRooms = MutableStateFlow<List<VoiceRoom>>(emptyList())
    val voiceRooms: StateFlow<List<VoiceRoom>> = _voiceRooms.asStateFlow()

    private val _activeVoiceRoom = MutableStateFlow<VoiceRoom?>(null)
    val activeVoiceRoom: StateFlow<VoiceRoom?> = _activeVoiceRoom.asStateFlow()

    private val _isMicMuted = MutableStateFlow(false)
    val isMicMuted: StateFlow<Boolean> = _isMicMuted.asStateFlow()

    private val _isDeafened = MutableStateFlow(false)
    val isDeafened: StateFlow<Boolean> = _isDeafened.asStateFlow()

    // Cross-Platform Chat Messages
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    // Instant Replay Highlights
    private val _replays = MutableStateFlow<List<ReplayHighlight>>(emptyList())
    val replays: StateFlow<List<ReplayHighlight>> = _replays.asStateFlow()

    private val _selectedReplay = MutableStateFlow<ReplayHighlight?>(null)
    val selectedReplay: StateFlow<ReplayHighlight?> = _selectedReplay.asStateFlow()

    init {
        seedInitialData()
        startVoiceSpeakerSimulation()
    }

    private fun seedInitialData() {
        val f1 = GamerProfile(
            id = "f1",
            tag = "ValkyriePrime#PS5",
            displayName = "Elena Rostova",
            avatarInitial = "VP",
            avatarColorHex = 0xFF3B82F6,
            status = UserStatus.IN_GAME,
            currentActivityTitle = "Apex Legends [Ranked Match]",
            currentPlatform = Platform.PLAYSTATION,
            level = 95,
            followersCount = 1240,
            followingCount = 310,
            isFollowing = true,
            isFriend = true,
            bio = "Master rank Wraith main. Controller demon on PS5.",
            platformsLinked = listOf(Platform.PLAYSTATION, Platform.PC),
            badges = listOf("Apex Predator", "20 Bomb", "Season 18 MVP"),
            winRate = "24.6%",
            kdRatio = "3.12"
        )

        val f2 = GamerProfile(
            id = "f2",
            tag = "CyberGhost_99",
            displayName = "Kai Chen",
            avatarInitial = "CG",
            avatarColorHex = 0xFF38BDF8,
            status = UserStatus.IN_VOICE,
            currentActivityTitle = "Valorant [Voice Room Active]",
            currentPlatform = Platform.PC,
            level = 76,
            followersCount = 560,
            followingCount = 190,
            isFollowing = true,
            isFriend = true,
            bio = "Jett & Omen main. Low ping 240Hz grinder.",
            platformsLinked = listOf(Platform.PC),
            badges = listOf("Immortal II", "Entry Specialist"),
            winRate = "61.2%",
            kdRatio = "1.45"
        )

        val f3 = GamerProfile(
            id = "f3",
            tag = "NeonDrifter#XBX",
            displayName = "Marcus Vance",
            avatarInitial = "ND",
            avatarColorHex = 0xFF22C55E,
            status = UserStatus.ONLINE,
            currentActivityTitle = "Rocket League [Lobby]",
            currentPlatform = Platform.XBOX,
            level = 68,
            followersCount = 412,
            followingCount = 145,
            isFollowing = true,
            isFriend = true,
            bio = "Xbox Series X / Elite controller. Grand Champ mechanical player.",
            platformsLinked = listOf(Platform.XBOX, Platform.PC),
            badges = listOf("Grand Champion", "Flip Reset Master"),
            winRate = "59.8%",
            kdRatio = "2.10"
        )

        val f4 = GamerProfile(
            id = "f4",
            tag = "AcrobatQueen#M",
            displayName = "Sora Lin",
            avatarInitial = "AQ",
            avatarColorHex = 0xFFF97316,
            status = UserStatus.IN_GAME,
            currentActivityTitle = "Warzone Mobile [Battle Royale]",
            currentPlatform = Platform.MOBILE,
            level = 54,
            followersCount = 890,
            followingCount = 230,
            isFollowing = true,
            isFriend = true,
            bio = "Touch control sniper & tablet gyroscope specialist.",
            platformsLinked = listOf(Platform.MOBILE, Platform.PLAYSTATION),
            badges = listOf("Top 100 Mobile", "Longshot King"),
            winRate = "32.1%",
            kdRatio = "4.20"
        )

        val f5 = GamerProfile(
            id = "f5",
            tag = "PixelSamurai",
            displayName = "Kenji Sato",
            avatarInitial = "PS",
            avatarColorHex = 0xFFA855F7,
            status = UserStatus.ONLINE,
            currentActivityTitle = "Street Fighter 6 [Battle Hub]",
            currentPlatform = Platform.PC,
            level = 112,
            followersCount = 3120,
            followingCount = 180,
            isFollowing = true,
            isFriend = true,
            bio = "Master rank Ryu / Luke. Hitbox controller player. Tournament caster.",
            platformsLinked = listOf(Platform.PC, Platform.PLAYSTATION),
            badges = listOf("EVO Top 64", "Master Rank", "Parry God"),
            winRate = "72.4%",
            kdRatio = "2.80"
        )

        val f6 = GamerProfile(
            id = "f6",
            tag = "SwitchBlade",
            displayName = "Liam O'Connor",
            avatarInitial = "SB",
            avatarColorHex = 0xFFEF4444,
            status = UserStatus.AWAY,
            currentActivityTitle = "Super Smash Bros [Away]",
            currentPlatform = Platform.SWITCH,
            level = 42,
            followersCount = 180,
            followingCount = 95,
            isFollowing = false,
            isFriend = true,
            bio = "Switch OLED hand-held warrior.",
            platformsLinked = listOf(Platform.SWITCH),
            badges = listOf("Smash Elite"),
            winRate = "51.0%",
            kdRatio = "1.15"
        )

        // Non-friend follower to demonstrate follow/following interaction
        val follower1 = GamerProfile(
            id = "foll_1",
            tag = "NovaStriker",
            displayName = "Zack Taylor",
            avatarInitial = "NS",
            avatarColorHex = 0xFF38BDF8,
            status = UserStatus.ONLINE,
            currentActivityTitle = "Valorant [Deathmatch]",
            currentPlatform = Platform.PC,
            level = 38,
            followersCount = 85,
            followingCount = 140,
            isFollowing = false,
            isFriend = false,
            bio = "Ascendant aspiring player. Looking for guides and party mates.",
            platformsLinked = listOf(Platform.PC),
            badges = listOf("Sharp Shooter"),
            winRate = "53.2%",
            kdRatio = "1.12"
        )

        val follower2 = GamerProfile(
            id = "foll_2",
            tag = "ChronoCross#PS5",
            displayName = "Maya Gomez",
            avatarInitial = "CC",
            avatarColorHex = 0xFF3B82F6,
            status = UserStatus.IN_GAME,
            currentActivityTitle = "Destiny 2 [Grandmaster Nightfall]",
            currentPlatform = Platform.PLAYSTATION,
            level = 82,
            followersCount = 420,
            followingCount = 210,
            isFollowing = true,
            isFriend = false,
            bio = "Raid sherpa and endgame PvE/PvP enthusiast.",
            platformsLinked = listOf(Platform.PLAYSTATION, Platform.PC),
            badges = listOf("Raid Sherpa", "Flawless"),
            winRate = "66.0%",
            kdRatio = "1.90"
        )

        val initialFriends = listOf(f1, f2, f3, f4, f5, f6)
        _friends.value = initialFriends
        _followers.value = listOf(f1, f2, f3, f4, follower1, follower2)
        _following.value = listOf(f1, f2, f3, f4, f5, follower2)

        // Games catalog
        _games.value = listOf(
            GameTitle("g_val", "Valorant", GameGenre.FPS_TACTICAL, "2.4M Online", true, R.drawable.banner_tactical),
            GameTitle("g_apex", "Apex Legends", GameGenre.BATTLE_ROYALE, "1.8M Online", true, R.drawable.banner_royale),
            GameTitle("g_rl", "Rocket League", GameGenre.SPORTS, "950K Online", true, null),
            GameTitle("g_sf6", "Street Fighter 6", GameGenre.FIGHTING, "380K Online", true, null),
            GameTitle("g_cs2", "Counter-Strike 2", GameGenre.FPS_TACTICAL, "1.2M Online", true, null),
            GameTitle("g_cod", "Call of Duty: Warzone", GameGenre.BATTLE_ROYALE, "2.1M Online", true, null),
            GameTitle("g_forza", "Forza Horizon 5", GameGenre.RACING, "290K Online", true, null),
            GameTitle("g_lol", "League of Legends", GameGenre.MOBA, "3.5M Online", true, null),
            GameTitle("g_destiny", "Destiny 2", GameGenre.RPG_ACTION, "410K Online", true, null)
        )

        // Stats
        _stats.value = listOf(
            PlayerStats(
                gameId = "g_val",
                gameTitle = "Valorant",
                genre = GameGenre.FPS_TACTICAL,
                rankName = "Ascendant III",
                rankTier = "Top 2.8% of Players",
                winRatePct = 68.4f,
                kdRatio = 1.82f,
                matchesPlayed = 154,
                hoursLogged = 248,
                headshotPct = 36.8f,
                mvpCount = 42,
                recentMatches = listOf(
                    MatchRecord("m1", "VICTORY 13-9", true, "24 / 11 / 6", 24, 11, 6, "Haven", "1 hour ago", true),
                    MatchRecord("m2", "VICTORY 13-11", true, "31 / 14 / 8", 31, 14, 8, "Ascent (MVP)", "3 hours ago", true),
                    MatchRecord("m3", "DEFEAT 10-13", false, "18 / 16 / 4", 18, 16, 4, "Bind", "Yesterday", false),
                    MatchRecord("m4", "VICTORY 13-7", true, "22 / 9 / 7", 22, 9, 7, "Sunset", "2 days ago", false)
                )
            ),
            PlayerStats(
                gameId = "g_apex",
                gameTitle = "Apex Legends",
                genre = GameGenre.BATTLE_ROYALE,
                rankName = "Diamond II",
                rankTier = "Top 4.1% of Players",
                winRatePct = 26.5f,
                kdRatio = 2.94f,
                matchesPlayed = 210,
                hoursLogged = 310,
                headshotPct = 28.4f,
                mvpCount = 58,
                recentMatches = listOf(
                    MatchRecord("a1", "CHAMPION #1", true, "11 Kills / 2.8k Dmg", 11, 1, 4, "World's Edge", "4 hours ago", true),
                    MatchRecord("a2", "TOP 3", true, "7 Kills / 1.9k Dmg", 7, 1, 2, "Olympus", "Yesterday", false),
                    MatchRecord("a3", "CHAMPION #1", true, "14 Kills / 3.4k Dmg", 14, 0, 5, "Storm Point", "3 days ago", true)
                )
            ),
            PlayerStats(
                gameId = "g_rl",
                gameTitle = "Rocket League",
                genre = GameGenre.SPORTS,
                rankName = "Grand Champion I",
                rankTier = "Top 1.5% of Players",
                winRatePct = 62.0f,
                kdRatio = 2.40f, // Goals per game
                matchesPlayed = 430,
                hoursLogged = 380,
                headshotPct = 85.0f, // Shot accuracy
                mvpCount = 112,
                recentMatches = listOf(
                    MatchRecord("r1", "VICTORY 4-2", true, "3 Goals / 2 Saves", 3, 0, 2, "DFH Stadium", "5 hours ago", true),
                    MatchRecord("r2", "VICTORY 3-1", true, "2 Goals / 4 Saves", 2, 0, 1, "Champions Field", "Yesterday", false)
                )
            ),
            PlayerStats(
                gameId = "g_sf6",
                gameTitle = "Street Fighter 6",
                genre = GameGenre.FIGHTING,
                rankName = "Master Rank (1520 LP)",
                rankTier = "Top 2.0% of Players",
                winRatePct = 65.5f,
                kdRatio = 1.90f,
                matchesPlayed = 320,
                hoursLogged = 175,
                headshotPct = 78.0f, // Drive parry %
                mvpCount = 95,
                recentMatches = listOf(
                    MatchRecord("s1", "VICTORY 2-0", true, "Perfect Round x1", 2, 0, 0, "Metro City Arena", "Yesterday", true)
                )
            )
        )

        // Custom multiplayer scheduled sessions
        _sessions.value = listOf(
            GameSession(
                id = "sess_1",
                title = "Ascendant to Immortal Push (5-Stack)",
                gameTitle = "Valorant",
                genre = GameGenre.FPS_TACTICAL,
                scheduledTimeDisplay = "Tonight at 8:30 PM",
                scheduledTimestamp = System.currentTimeMillis() + 3600000 * 3,
                hostUser = _currentUser.value,
                maxPlayers = 5,
                slots = listOf(
                    SessionSlot(0, "IGL / Initiator", _currentUser.value),
                    SessionSlot(1, "Entry Duelist", f2),
                    SessionSlot(2, "Smoker / Controller", null),
                    SessionSlot(3, "Sentinel / Anchor", null),
                    SessionSlot(4, "Flex / Second Duelist", null)
                ),
                crossPlayPlatforms = listOf(Platform.PC),
                voiceChannelName = "Squad Alpha - Competitive",
                gameMode = "Ranked 5v5",
                description = "Need disciplined comms and active crosshair placement. Mic required in Squad Alpha room.",
                isJoined = true,
                myRole = "IGL / Initiator"
            ),
            GameSession(
                id = "sess_2",
                title = "Predator Lobby Kill Races & RP Farm",
                gameTitle = "Apex Legends",
                genre = GameGenre.BATTLE_ROYALE,
                scheduledTimeDisplay = "Tomorrow at 7:00 PM",
                scheduledTimestamp = System.currentTimeMillis() + 3600000 * 22,
                hostUser = f1,
                maxPlayers = 3,
                slots = listOf(
                    SessionSlot(0, "Fragger (Wraith)", f1),
                    SessionSlot(1, "Scout / Recon (Bloodhound)", f4),
                    SessionSlot(2, "Support (Conduit / Newcastle)", null)
                ),
                crossPlayPlatforms = listOf(Platform.PLAYSTATION, Platform.XBOX, Platform.PC),
                voiceChannelName = "Casual Gaming Lounge",
                gameMode = "Trios Ranked",
                description = "Cross-platform squad. Push early rotation and play for endgame ring positions.",
                isJoined = false,
                myRole = null
            ),
            GameSession(
                id = "sess_3",
                title = "Cross-Play 3v3 Tournament Scrims",
                gameTitle = "Rocket League",
                genre = GameGenre.SPORTS,
                scheduledTimeDisplay = "Saturday at 3:00 PM",
                scheduledTimestamp = System.currentTimeMillis() + 3600000 * 48,
                hostUser = f3,
                maxPlayers = 3,
                slots = listOf(
                    SessionSlot(0, "First Man / Striker", f3),
                    SessionSlot(1, "Rotator / Midfield", null),
                    SessionSlot(2, "Third Man / Goalkeeper", null)
                ),
                crossPlayPlatforms = listOf(Platform.PC, Platform.PLAYSTATION, Platform.XBOX, Platform.SWITCH),
                voiceChannelName = "Scrims Strategy Room",
                gameMode = "Competitive 3v3",
                description = "Fast rotation and pass-first mentality. Console & PC players welcome!",
                isJoined = false,
                myRole = null
            )
        )

        // Voice rooms
        _voiceRooms.value = listOf(
            VoiceRoom(
                id = "vr_1",
                name = "Squad Alpha - Competitive",
                gameTitle = "Valorant / Tactical FPS",
                maxCapacity = 6,
                members = listOf(
                    VoiceMember(f2, isMuted = false, isDeafened = false, isSpeaking = true, volume = 0.90f),
                    VoiceMember(f1, isMuted = false, isDeafened = false, isSpeaking = false, volume = 0.85f)
                ),
                isConnected = false,
                bitrate = "128 kbps Lossless",
                pingMs = 14
            ),
            VoiceRoom(
                id = "vr_2",
                name = "Casual Gaming Lounge",
                gameTitle = "Cross-Platform Party",
                maxCapacity = 12,
                members = listOf(
                    VoiceMember(f3, isMuted = false, isDeafened = false, isSpeaking = false, volume = 0.80f),
                    VoiceMember(f4, isMuted = true, isDeafened = false, isSpeaking = false, volume = 0.75f)
                ),
                isConnected = false,
                bitrate = "96 kbps HQ",
                pingMs = 21
            ),
            VoiceRoom(
                id = "vr_3",
                name = "Scrims Strategy Room",
                gameTitle = "Tournament Coordination",
                maxCapacity = 8,
                members = listOf(
                    VoiceMember(f5, isMuted = false, isDeafened = false, isSpeaking = false, volume = 1.0f)
                ),
                isConnected = false,
                bitrate = "128 kbps Lossless",
                pingMs = 16
            )
        )

        // Chat messages across PC, Console, Mobile
        _chatMessages.value = listOf(
            ChatMessage("c1", f2, "Yo @ShadowKitsune, ready for the 8:30 PM ranked session? I warmed up on AimLab.", "8:10 PM", Platform.PC, false),
            ChatMessage("c2", _currentUser.value, "Yeah! Just grabbing coffee. Are we locking in Haven or Ascent for map vote?", "8:12 PM", Platform.PC, true),
            ChatMessage("c3", f1, "Haven 100%! I tuned my controller deadzones on PS5, ready to entry frag.", "8:14 PM", Platform.PLAYSTATION, false),
            ChatMessage("c4", f3, "Let's go! I'm hopping on my Series X right after this Rocket League scrim.", "8:15 PM", Platform.XBOX, false),
            ChatMessage("c5", f4, "Checking in from iPad Pro! Clip from yesterday's 1v3 is up on the Highlights feed btw 🔥", "8:16 PM", Platform.MOBILE, false, "1v4 Clutch Ace - Haven", "Valorant")
        )

        // Instant Replay Highlights
        _replays.value = listOf(
            ReplayHighlight(
                id = "rep_1",
                title = "1v4 Clutch Ace in Round 24 (Match Point)",
                gameTitle = "Valorant",
                author = _currentUser.value,
                durationSec = 34,
                likesCount = 384,
                commentsCount = 42,
                viewsCount = 1820,
                isLiked = true,
                timestamp = "3 hours ago",
                tags = listOf("Ace", "Clutch", "Vandal", "Ascendant"),
                thumbnailDrawableRes = R.drawable.banner_tactical,
                keyMoments = listOf(
                    BookmarkMoment(6, "First Kill (A Long)", "KILL"),
                    BookmarkMoment(14, "Double Headshot (Site)", "HEADSHOT"),
                    BookmarkMoment(22, "Defuse Fake & 1v1 Win", "CLUTCH"),
                    BookmarkMoment(30, "Defuse Completed", "WIN")
                )
            ),
            ReplayHighlight(
                id = "rep_2",
                title = "Kraber 360 No-Scope for Champion Squad Victory",
                gameTitle = "Apex Legends",
                author = f1,
                durationSec = 22,
                likesCount = 942,
                commentsCount = 88,
                viewsCount = 4190,
                isLiked = false,
                timestamp = "Yesterday",
                tags = listOf("Apex", "Kraber", "NoScope", "Champion"),
                thumbnailDrawableRes = R.drawable.banner_royale,
                keyMoments = listOf(
                    BookmarkMoment(5, "Jump-Pad Launch", "CLUTCH"),
                    BookmarkMoment(12, "Mid-Air 360 Spin", "HEADSHOT"),
                    BookmarkMoment(16, "Final Knockout", "WIN")
                )
            ),
            ReplayHighlight(
                id = "rep_3",
                title = "Ceiling Musty Flick Double Tap in Overtime",
                gameTitle = "Rocket League",
                author = f3,
                durationSec = 18,
                likesCount = 612,
                commentsCount = 53,
                viewsCount = 2740,
                isLiked = true,
                timestamp = "2 days ago",
                tags = listOf("RocketLeague", "MustyFlick", "Freestyle", "XboxSeriesX"),
                thumbnailDrawableRes = R.drawable.banner_tactical,
                keyMoments = listOf(
                    BookmarkMoment(4, "Ceiling Launch", "STEAL"),
                    BookmarkMoment(10, "Musty Flick", "ACE"),
                    BookmarkMoment(15, "Top Corner Goal", "WIN")
                )
            )
        )

        _selectedReplay.value = _replays.value.firstOrNull()

        seedDiscoveryEngine(initialFriends)
    }

    private fun seedDiscoveryEngine(friendsList: List<GamerProfile>) {
        val f1 = friendsList.getOrNull(0)
        val f2 = friendsList.getOrNull(1)
        val f3 = friendsList.getOrNull(2)
        val f4 = friendsList.getOrNull(3)
        val f5 = friendsList.getOrNull(4)

        _gameRecommendations.value = listOf(
            GameRecommendation(
                game = GameTitle("rec_1", "The Finals", GameGenre.FPS_TACTICAL, "1.1M Online", false, R.drawable.banner_tactical),
                matchScore = 98,
                recommendationReason = "High match: You have 248 hrs in Valorant & Tactical Shooters + fast-paced team destruction.",
                friendsPlaying = listOfNotNull(f2, f1),
                supportedPlatforms = listOf(Platform.PC, Platform.PLAYSTATION, Platform.XBOX),
                rating = 4.8f,
                priceOrPass = "Free to Play (Cross-Play)",
                isWishlisted = false
            ),
            GameRecommendation(
                game = GameTitle("rec_2", "Helldivers 2", GameGenre.RPG_ACTION, "850K Online", false, R.drawable.banner_royale),
                matchScore = 95,
                recommendationReason = "Trending with friends: Elena Rostova & 3 friends active in cross-play squad co-op.",
                friendsPlaying = listOfNotNull(f1, f3),
                supportedPlatforms = listOf(Platform.PC, Platform.PLAYSTATION),
                rating = 4.9f,
                priceOrPass = "$39.99 / Squad Favorite",
                isWishlisted = true
            ),
            GameRecommendation(
                game = GameTitle("rec_3", "Marvel Rivals", GameGenre.FPS_TACTICAL, "920K Online", false, null),
                matchScore = 93,
                recommendationReason = "Based on your high competitive win-rate (68%) and preferred role as IGL / Team Coordinator.",
                friendsPlaying = listOfNotNull(f2, f4),
                supportedPlatforms = listOf(Platform.PC, Platform.PLAYSTATION, Platform.XBOX),
                rating = 4.7f,
                priceOrPass = "Free to Play / Closed Beta",
                isWishlisted = false
            ),
            GameRecommendation(
                game = GameTitle("rec_4", "Tekken 8", GameGenre.FIGHTING, "340K Online", false, null),
                matchScore = 89,
                recommendationReason = "Recommended from your 175 hrs in Street Fighter 6 (Master Rank) + fighting genre focus.",
                friendsPlaying = listOfNotNull(f5),
                supportedPlatforms = listOf(Platform.PC, Platform.PLAYSTATION, Platform.XBOX),
                rating = 4.8f,
                priceOrPass = "Game Pass & Steam",
                isWishlisted = false
            ),
            GameRecommendation(
                game = GameTitle("rec_5", "EA Sports FC 25", GameGenre.SPORTS, "1.4M Online", false, null),
                matchScore = 84,
                recommendationReason = "Matches your passion for Rocket League competitive rotations and high mechanical play.",
                friendsPlaying = listOfNotNull(f3),
                supportedPlatforms = listOf(Platform.PC, Platform.PLAYSTATION, Platform.XBOX, Platform.SWITCH),
                rating = 4.5f,
                priceOrPass = "$69.99 (Cross-Play Enabled)",
                isWishlisted = false
            )
        )

        _friendSuggestions.value = listOf(
            FriendSuggestion(
                user = GamerProfile(
                    id = "sug_1",
                    tag = "KiraViper#PC",
                    displayName = "Kira Zhao",
                    avatarInitial = "KV",
                    avatarColorHex = 0xFF00F0FF,
                    status = UserStatus.IN_GAME,
                    currentActivityTitle = "Valorant [Ascendant Queue]",
                    currentPlatform = Platform.PC,
                    level = 92,
                    followersCount = 780,
                    followingCount = 210,
                    bio = "Ascendant III smokes & controller player. Need consistent 5-stack comms.",
                    platformsLinked = listOf(Platform.PC),
                    badges = listOf("Ascendant Tier", "Smoke Specialist", "High Win Rate"),
                    winRate = "67.1%",
                    kdRatio = "1.52"
                ),
                compatibilityScore = 97,
                commonGames = listOf("Valorant", "Apex Legends", "The Finals"),
                sharedPlatform = Platform.PC,
                mutualFriendsCount = 4,
                recommendationReason = "Similar skill rating (Ascendant), shared main games, and 4 mutual friends including Kai Chen."
            ),
            FriendSuggestion(
                user = GamerProfile(
                    id = "sug_2",
                    tag = "ShadowRacer_X",
                    displayName = "Dante Moreno",
                    avatarInitial = "SR",
                    avatarColorHex = 0xFF22C55E,
                    status = UserStatus.ONLINE,
                    currentActivityTitle = "Rocket League [Grand Champ 2]",
                    currentPlatform = Platform.XBOX,
                    level = 84,
                    followersCount = 430,
                    followingCount = 180,
                    bio = "Xbox Series X | Competitive 3v3 passer & aerial defender. Always in low ping party voice.",
                    platformsLinked = listOf(Platform.XBOX, Platform.PC),
                    badges = listOf("Grand Champion II", "Fast Rotator"),
                    winRate = "63.4%",
                    kdRatio = "2.35"
                ),
                compatibilityScore = 92,
                commonGames = listOf("Rocket League", "Forza Horizon 5"),
                sharedPlatform = Platform.XBOX,
                mutualFriendsCount = 2,
                recommendationReason = "Plays with Marcus Vance (NeonDrifter) and shares competitive Sports/Racing focus."
            ),
            FriendSuggestion(
                user = GamerProfile(
                    id = "sug_3",
                    tag = "AuraStriker#PS5",
                    displayName = "Camila Silva",
                    avatarInitial = "AS",
                    avatarColorHex = 0xFF3B82F6,
                    status = UserStatus.IN_VOICE,
                    currentActivityTitle = "Street Fighter 6 [Battle Hub Lobby]",
                    currentPlatform = Platform.PLAYSTATION,
                    level = 104,
                    followersCount = 1120,
                    followingCount = 340,
                    bio = "PS5 / DualSense Edge. Street Fighter 6 Diamond/Master rank player. Looking for sparring partners.",
                    platformsLinked = listOf(Platform.PLAYSTATION, Platform.PC),
                    badges = listOf("Master Tier", "EVO Competitor"),
                    winRate = "69.0%",
                    kdRatio = "2.40"
                ),
                compatibilityScore = 89,
                commonGames = listOf("Street Fighter 6", "Apex Legends"),
                sharedPlatform = Platform.PLAYSTATION,
                mutualFriendsCount = 3,
                recommendationReason = "Shares high fighting-game rank and mutual friend Kenji Sato (PixelSamurai)."
            )
        )
    }

    fun toggleWishlistGame(gameId: String) {
        _gameRecommendations.update { list ->
            list.map { rec ->
                if (rec.game.id == gameId) rec.copy(isWishlisted = !rec.isWishlisted) else rec
            }
        }
    }

    fun sendFriendSuggestionRequest(userId: String) {
        _friendSuggestions.update { list ->
            list.map { sug ->
                if (sug.user.id == userId) sug.copy(isRequestSent = true) else sug
            }
        }
    }

    fun toggleGenrePreference(genre: GameGenre) {
        val current = _userPreferredGenres.value.toMutableSet()
        if (current.contains(genre)) {
            if (current.size > 1) { // keep at least 1
                current.remove(genre)
            }
        } else {
            current.add(genre)
        }
        _userPreferredGenres.value = current
    }


    // Toggle follow status
    fun toggleFollow(userId: String) {
        val currentFollowing = _following.value.toMutableList()
        val isAlreadyFollowing = currentFollowing.any { it.id == userId }

        if (isAlreadyFollowing) {
            currentFollowing.removeAll { it.id == userId }
        } else {
            val user = _friends.value.find { it.id == userId }
                ?: _followers.value.find { it.id == userId }
            if (user != null) {
                currentFollowing.add(user.copy(isFollowing = true))
            }
        }
        _following.value = currentFollowing

        // Update in friends & followers
        _friends.update { list ->
            list.map { if (it.id == userId) it.copy(isFollowing = !isAlreadyFollowing) else it }
        }
        _followers.update { list ->
            list.map { if (it.id == userId) it.copy(isFollowing = !isAlreadyFollowing) else it }
        }
        _currentUser.update {
            it.copy(followingCount = currentFollowing.size)
        }
    }

    // Add friend / accept friend
    fun addFriend(userId: String) {
        val user = _followers.value.find { it.id == userId } ?: return
        if (_friends.value.none { it.id == userId }) {
            _friends.update { it + user.copy(isFriend = true) }
        }
    }

    // Create a new multiplayer scheduled session
    fun createSession(
        title: String,
        gameTitle: String,
        genre: GameGenre,
        timeDisplay: String,
        maxPlayers: Int,
        platforms: List<Platform>,
        voiceRoom: String,
        mode: String,
        description: String,
        hostRole: String
    ) {
        val me = _currentUser.value
        val slots = mutableListOf<SessionSlot>()
        slots.add(SessionSlot(0, hostRole, me))
        for (i in 1 until maxPlayers) {
            val roleName = when (i) {
                1 -> "Entry / Fragger"
                2 -> "Support / Utility"
                3 -> "Anchor / Defense"
                else -> "Flex Squad Member"
            }
            slots.add(SessionSlot(i, roleName, null))
        }

        val newSession = GameSession(
            id = "sess_${System.currentTimeMillis()}",
            title = title,
            gameTitle = gameTitle,
            genre = genre,
            scheduledTimeDisplay = timeDisplay,
            scheduledTimestamp = System.currentTimeMillis() + 7200000,
            hostUser = me,
            maxPlayers = maxPlayers,
            slots = slots,
            crossPlayPlatforms = platforms,
            voiceChannelName = voiceRoom,
            gameMode = mode,
            description = description,
            isJoined = true,
            myRole = hostRole
        )

        _sessions.update { listOf(newSession) + it }
    }

    // Join / Leave session
    fun toggleJoinSession(sessionId: String, preferredRole: String = "Flex") {
        val me = _currentUser.value
        _sessions.update { list ->
            list.map { session ->
                if (session.id == sessionId) {
                    val alreadyJoined = session.isJoined
                    if (alreadyJoined) {
                        // Leave
                        val updatedSlots = session.slots.map { slot ->
                            if (slot.user?.id == me.id) slot.copy(user = null) else slot
                        }
                        session.copy(isJoined = false, myRole = null, slots = updatedSlots)
                    } else {
                        // Join first available slot
                        var filled = false
                        val updatedSlots = session.slots.map { slot ->
                            if (!filled && slot.user == null) {
                                filled = true
                                slot.copy(user = me)
                            } else {
                                slot
                            }
                        }
                        session.copy(isJoined = true, myRole = preferredRole, slots = updatedSlots)
                    }
                } else {
                    session
                }
            }
        }
    }

    // Voice room actions
    fun joinVoiceRoom(room: VoiceRoom) {
        val me = _currentUser.value
        val myVoiceMember = VoiceMember(
            user = me,
            isMuted = _isMicMuted.value,
            isDeafened = _isDeafened.value,
            isSpeaking = false,
            volume = 1.0f
        )
        val updatedRoom = room.copy(
            isConnected = true,
            members = room.members.filter { it.user.id != me.id } + myVoiceMember
        )
        _activeVoiceRoom.value = updatedRoom
        _currentUser.update { it.copy(status = UserStatus.IN_VOICE) }

        // Update in voiceRooms list
        _voiceRooms.update { list ->
            list.map { if (it.id == room.id) updatedRoom else it.copy(isConnected = false) }
        }
    }

    fun leaveVoiceRoom() {
        val me = _currentUser.value
        val active = _activeVoiceRoom.value
        if (active != null) {
            _voiceRooms.update { list ->
                list.map { r ->
                    if (r.id == active.id) {
                        r.copy(isConnected = false, members = r.members.filter { it.user.id != me.id })
                    } else r
                }
            }
        }
        _activeVoiceRoom.value = null
        _currentUser.update { it.copy(status = UserStatus.ONLINE) }
    }

    fun toggleMicMute() {
        val newMuted = !_isMicMuted.value
        _isMicMuted.value = newMuted
        val me = _currentUser.value
        _activeVoiceRoom.update { room ->
            room?.copy(
                members = room.members.map {
                    if (it.user.id == me.id) it.copy(isMuted = newMuted, isSpeaking = if (newMuted) false else it.isSpeaking) else it
                }
            )
        }
    }

    fun toggleDeafen() {
        val newDeaf = !_isDeafened.value
        _isDeafened.value = newDeaf
        val me = _currentUser.value
        _activeVoiceRoom.update { room ->
            room?.copy(
                members = room.members.map {
                    if (it.user.id == me.id) it.copy(isDeafened = newDeaf) else it
                }
            )
        }
    }

    fun updateMemberVolume(memberId: String, volume: Float) {
        _activeVoiceRoom.update { room ->
            room?.copy(
                members = room.members.map {
                    if (it.user.id == memberId) it.copy(volume = volume) else it
                }
            )
        }
    }

    // Chat functionality
    fun sendChatMessage(text: String, attachedClip: String? = null, attachedGame: String? = null) {
        val me = _currentUser.value
        val newMsg = ChatMessage(
            id = "c_${System.currentTimeMillis()}",
            sender = me,
            text = text,
            timestamp = "Just now",
            platform = me.currentPlatform,
            isSelf = true,
            attachedClipTitle = attachedClip,
            attachedGame = attachedGame
        )
        _chatMessages.update { it + newMsg }

        // Simulate interactive cross-platform teammate response
        scope.launch {
            delay(1200)
            val responder = _friends.value.randomOrNull() ?: return@launch
            val replyText = when {
                text.contains("rank", ignoreCase = true) || text.contains("push", ignoreCase = true) ->
                    "Count me in! Connecting my headset now."
                text.contains("ready", ignoreCase = true) || text.contains("go", ignoreCase = true) ->
                    "Locked and loaded! Inviting lobby."
                text.contains("clip", ignoreCase = true) || text.contains("replay", ignoreCase = true) ->
                    "That play was actually cracked! Just upvoted your highlight."
                else ->
                    "Copy that, party chat linked up across platforms!"
            }
            val replyMsg = ChatMessage(
                id = "c_${System.currentTimeMillis()}",
                sender = responder,
                text = replyText,
                timestamp = "Just now",
                platform = responder.currentPlatform,
                isSelf = false
            )
            _chatMessages.update { it + replyMsg }
        }
    }

    // Replay highlight interactions
    fun selectReplay(replay: ReplayHighlight) {
        _selectedReplay.value = replay
    }

    fun toggleLikeReplay(replayId: String) {
        _replays.update { list ->
            list.map { r ->
                if (r.id == replayId) {
                    val newLiked = !r.isLiked
                    val newCount = if (newLiked) r.likesCount + 1 else r.likesCount - 1
                    r.copy(isLiked = newLiked, likesCount = newCount)
                } else r
            }
        }
        if (_selectedReplay.value?.id == replayId) {
            _selectedReplay.update { r ->
                r?.let {
                    val newLiked = !it.isLiked
                    val newCount = if (newLiked) it.likesCount + 1 else it.likesCount - 1
                    it.copy(isLiked = newLiked, likesCount = newCount)
                }
            }
        }
    }

    fun postReplayHighlight(title: String, gameTitle: String, tags: List<String>) {
        val me = _currentUser.value
        val newReplay = ReplayHighlight(
            id = "rep_${System.currentTimeMillis()}",
            title = title,
            gameTitle = gameTitle,
            author = me,
            durationSec = 28,
            likesCount = 1,
            commentsCount = 0,
            viewsCount = 12,
            isLiked = true,
            timestamp = "Just now",
            tags = tags,
            thumbnailDrawableRes = R.drawable.banner_tactical,
            keyMoments = listOf(
                BookmarkMoment(4, "Opener Knock", "KILL"),
                BookmarkMoment(16, "Clutch Outplay", "CLUTCH"),
                BookmarkMoment(24, "Victory Finish", "WIN")
            )
        )
        _replays.update { listOf(newReplay) + it }
        _selectedReplay.value = newReplay
    }

    // Log match stat result
    fun logMatch(
        gameId: String,
        outcome: String,
        isVictory: Boolean,
        kills: Int,
        deaths: Int,
        assists: Int,
        map: String
    ) {
        val newRecord = MatchRecord(
            id = "m_${System.currentTimeMillis()}",
            outcome = outcome,
            isVictory = isVictory,
            score = "$kills / $deaths / $assists",
            kills = kills,
            deaths = deaths,
            assists = assists,
            map = map,
            timeAgo = "Just now",
            hasHighlight = isVictory
        )

        _stats.update { list ->
            list.map { stat ->
                if (stat.gameId == gameId) {
                    val newMatches = listOf(newRecord) + stat.recentMatches
                    val totalM = stat.matchesPlayed + 1
                    val newWinRate = if (isVictory) {
                        ((stat.winRatePct * stat.matchesPlayed + 100f) / totalM)
                    } else {
                        ((stat.winRatePct * stat.matchesPlayed) / totalM)
                    }
                    val newKd = if (deaths > 0) kills.toFloat() / deaths.toFloat() else kills.toFloat()
                    stat.copy(
                        matchesPlayed = totalM,
                        winRatePct = (newWinRate * 10).toInt() / 10f,
                        kdRatio = ((stat.kdRatio + newKd) / 2f * 100).toInt() / 100f,
                        recentMatches = newMatches
                    )
                } else stat
            }
        }
    }

    // Update presence status & active platform
    fun updatePresence(newStatus: UserStatus, activity: String, platform: Platform) {
        _currentUser.update {
            it.copy(status = newStatus, currentActivityTitle = activity, currentPlatform = platform)
        }
    }

    // Voice audio wave speaker pulse simulation
    private fun startVoiceSpeakerSimulation() {
        scope.launch {
            while (true) {
                delay(3000)
                _activeVoiceRoom.update { room ->
                    room?.copy(
                        members = room.members.map { member ->
                            if (member.user.id != _currentUser.value.id && !member.isMuted) {
                                member.copy(isSpeaking = listOf(true, false).random())
                            } else {
                                member
                            }
                        }
                    )
                }
            }
        }
    }
}
