package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.GamingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

enum class ScreenTab(val title: String) {
    DASHBOARD("Dashboard"),
    SESSIONS("Squads"),
    DISCOVERY("Discovery"),
    VOICE_CHAT("Comms"),
    STATS("Stats"),
    REPLAYS("Clips"),
    FRIENDS("Social"),
    PROFILE("Profile")
}

class GamingViewModel(
    val repository: GamingRepository = GamingRepository()
) : ViewModel() {

    // Current navigation tab
    private val _currentTab = MutableStateFlow(ScreenTab.DASHBOARD)
    val currentTab: StateFlow<ScreenTab> = _currentTab.asStateFlow()

    // Navigation back stack for custom routing
    private val backStack = mutableListOf<ScreenTab>()

    // Selected genre & platform filters
    private val _selectedGenre = MutableStateFlow(GameGenre.ALL)
    val selectedGenre: StateFlow<GameGenre> = _selectedGenre.asStateFlow()

    private val _selectedPlatformFilter = MutableStateFlow<Platform?>(null)
    val selectedPlatformFilter: StateFlow<Platform?> = _selectedPlatformFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Dialogs / Sheet toggles
    private val _showCreateSessionDialog = MutableStateFlow(false)
    val showCreateSessionDialog: StateFlow<Boolean> = _showCreateSessionDialog.asStateFlow()

    private val _showLogMatchDialog = MutableStateFlow(false)
    val showLogMatchDialog: StateFlow<Boolean> = _showLogMatchDialog.asStateFlow()

    private val _showPostHighlightDialog = MutableStateFlow(false)
    val showPostHighlightDialog: StateFlow<Boolean> = _showPostHighlightDialog.asStateFlow()

    private val _viewingProfileUser = MutableStateFlow<GamerProfile?>(null)
    val viewingProfileUser: StateFlow<GamerProfile?> = _viewingProfileUser.asStateFlow()

    private val _showPresenceSelector = MutableStateFlow(false)
    val showPresenceSelector: StateFlow<Boolean> = _showPresenceSelector.asStateFlow()

    // Sub-tab selectors
    private val _socialSubTab = MutableStateFlow(0) // 0: Friends, 1: Followers, 2: Following, 3: Suggested
    val socialSubTab: StateFlow<Int> = _socialSubTab.asStateFlow()

    private val _commsSubTab = MutableStateFlow(0) // 0: Live Chat, 1: Voice Channels
    val commsSubTab: StateFlow<Int> = _commsSubTab.asStateFlow()

    // Repository state delegation
    val currentUser = repository.currentUser
    val friends = repository.friends
    val followers = repository.followers
    val following = repository.following
    val games = repository.games
    val stats = repository.stats
    val sessions = repository.sessions
    val voiceRooms = repository.voiceRooms
    val activeVoiceRoom = repository.activeVoiceRoom
    val isMicMuted = repository.isMicMuted
    val isDeafened = repository.isDeafened
    val chatMessages = repository.chatMessages
    val replays = repository.replays
    val selectedReplay = repository.selectedReplay
    val gameRecommendations = repository.gameRecommendations
    val friendSuggestions = repository.friendSuggestions
    val userPreferredGenres = repository.userPreferredGenres

    // Filtered Friends based on search query
    val filteredFriends = combine(friends, searchQuery) { list, query ->
        if (query.isBlank()) list else list.filter {
            it.tag.contains(query, ignoreCase = true) ||
                    it.displayName.contains(query, ignoreCase = true) ||
                    it.currentActivityTitle.contains(query, ignoreCase = true)
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Filtered Game Recommendations based on genre and preferred platform
    val filteredRecommendations = combine(
        gameRecommendations,
        selectedGenre,
        selectedPlatformFilter
    ) { recs, genre, platform ->
        recs.filter { rec ->
            (genre == GameGenre.ALL || rec.game.genre == genre) &&
                    (platform == null || rec.supportedPlatforms.contains(platform))
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Filtered Sessions
    val filteredSessions = combine(
        sessions,
        selectedGenre,
        selectedPlatformFilter
    ) { sessList, genre, platform ->
        sessList.filter { s ->
            (genre == GameGenre.ALL || s.genre == genre) &&
                    (platform == null || s.crossPlayPlatforms.contains(platform))
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun navigateTo(tab: ScreenTab) {
        if (_currentTab.value != tab) {
            backStack.add(_currentTab.value)
            _currentTab.value = tab
        }
    }

    fun handleBack(): Boolean {
        if (_viewingProfileUser.value != null) {
            _viewingProfileUser.value = null
            return true
        }
        if (_showCreateSessionDialog.value) {
            _showCreateSessionDialog.value = false
            return true
        }
        if (_showLogMatchDialog.value) {
            _showLogMatchDialog.value = false
            return true
        }
        if (_showPostHighlightDialog.value) {
            _showPostHighlightDialog.value = false
            return true
        }
        if (_showPresenceSelector.value) {
            _showPresenceSelector.value = false
            return true
        }
        if (backStack.isNotEmpty()) {
            _currentTab.value = backStack.removeAt(backStack.size - 1)
            return true
        }
        if (_currentTab.value != ScreenTab.DASHBOARD) {
            _currentTab.value = ScreenTab.DASHBOARD
            return true
        }
        return false
    }

    fun setGenreFilter(genre: GameGenre) {
        _selectedGenre.value = genre
    }

    fun setPlatformFilter(platform: Platform?) {
        _selectedPlatformFilter.value = platform
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSocialSubTab(index: Int) {
        _socialSubTab.value = index
    }

    fun setCommsSubTab(index: Int) {
        _commsSubTab.value = index
    }

    fun setViewingProfileUser(user: GamerProfile?) {
        _viewingProfileUser.value = user
    }

    fun setShowCreateSessionDialog(show: Boolean) {
        _showCreateSessionDialog.value = show
    }

    fun setShowLogMatchDialog(show: Boolean) {
        _showLogMatchDialog.value = show
    }

    fun setShowPostHighlightDialog(show: Boolean) {
        _showPostHighlightDialog.value = show
    }

    fun setShowPresenceSelector(show: Boolean) {
        _showPresenceSelector.value = show
    }

    // Proxy repository actions
    fun toggleFollow(userId: String) = repository.toggleFollow(userId)
    fun addFriend(userId: String) = repository.addFriend(userId)
    fun toggleJoinSession(sessionId: String, role: String = "Flex") =
        repository.toggleJoinSession(sessionId, role)
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
    ) = repository.createSession(
        title, gameTitle, genre, timeDisplay, maxPlayers, platforms, voiceRoom, mode, description, hostRole
    )
    fun joinVoiceRoom(room: VoiceRoom) = repository.joinVoiceRoom(room)
    fun leaveVoiceRoom() = repository.leaveVoiceRoom()
    fun toggleMicMute() = repository.toggleMicMute()
    fun toggleDeafen() = repository.toggleDeafen()
    fun updateMemberVolume(memberId: String, volume: Float) =
        repository.updateMemberVolume(memberId, volume)
    fun sendChatMessage(text: String, attachedClip: String? = null, attachedGame: String? = null) =
        repository.sendChatMessage(text, attachedClip, attachedGame)
    fun selectReplay(replay: ReplayHighlight) = repository.selectReplay(replay)
    fun toggleLikeReplay(replayId: String) = repository.toggleLikeReplay(replayId)
    fun postReplayHighlight(title: String, gameTitle: String, tags: List<String>) =
        repository.postReplayHighlight(title, gameTitle, tags)
    fun logMatch(
        gameId: String,
        outcome: String,
        isVictory: Boolean,
        kills: Int,
        deaths: Int,
        assists: Int,
        map: String
    ) = repository.logMatch(gameId, outcome, isVictory, kills, deaths, assists, map)
    fun updatePresence(newStatus: UserStatus, activity: String, platform: Platform) =
        repository.updatePresence(newStatus, activity, platform)

    // Discovery Engine proxy actions
    fun toggleWishlistGame(gameId: String) = repository.toggleWishlistGame(gameId)
    fun sendFriendSuggestionRequest(userId: String) = repository.sendFriendSuggestionRequest(userId)
    fun toggleGenrePreference(genre: GameGenre) = repository.toggleGenrePreference(genre)
}
