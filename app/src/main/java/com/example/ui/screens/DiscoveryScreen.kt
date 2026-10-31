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
import com.example.data.model.*
import com.example.ui.components.PlatformBadge
import com.example.ui.components.UserAvatar
import com.example.ui.theme.*
import com.example.ui.viewmodel.GamingViewModel

@Composable
fun DiscoveryScreen(
    viewModel: GamingViewModel,
    modifier: Modifier = Modifier
) {
    val recommendations by viewModel.filteredRecommendations.collectAsState()
    val friendSuggestions by viewModel.friendSuggestions.collectAsState()
    val selectedGenre by viewModel.selectedGenre.collectAsState()
    val selectedPlatform by viewModel.selectedPlatformFilter.collectAsState()
    val preferredGenres by viewModel.userPreferredGenres.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("discovery_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Discovery Engine Header Banner
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, Brush.horizontalGradient(listOf(ElectricPurple, CyberCyan)), RoundedCornerShape(16.dp)),
                color = DarkSurfaceVariant
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = ElectricPurple.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ElectricPurple)
                        ) {
                            Icon(
                                Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = ElectricPurple,
                                modifier = Modifier
                                    .padding(6.dp)
                                    .size(18.dp)
                            )
                        }

                        Text(
                            text = "GAME DISCOVERY ENGINE",
                            color = CyberCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                    }

                    Text(
                        text = "Curated For Your Playstyle",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )

                    Text(
                        text = "Real-time match engine calculating compatibility from your played genres, competitive stats, and party friends' activities.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // 2. Personal Preference Genre Filters (Interactive Tuning)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "FAVORITE GENRES (ENGINE WEIGHTS)",
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val allGenres = listOf(
                        GameGenre.FPS_TACTICAL,
                        GameGenre.BATTLE_ROYALE,
                        GameGenre.MOBA,
                        GameGenre.FIGHTING,
                        GameGenre.SPORTS,
                        GameGenre.RACING,
                        GameGenre.RPG_ACTION,
                        GameGenre.STRATEGY
                    )
                    items(allGenres) { genre ->
                        val isPreferred = preferredGenres.contains(genre)
                        FilterChip(
                            selected = isPreferred,
                            onClick = { viewModel.toggleGenrePreference(genre) },
                            label = {
                                Text(
                                    genre.title,
                                    fontSize = 12.sp,
                                    fontWeight = if (isPreferred) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ElectricPurple,
                                selectedLabelColor = Color.White,
                                containerColor = DarkSurfaceElevated,
                                labelColor = TextSecondary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isPreferred,
                                borderColor = if (isPreferred) ElectricPurple else DarkCardBorder
                            ),
                            leadingIcon = if (isPreferred) {
                                {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            } else null,
                            modifier = Modifier.testTag("genre_filter_${genre.name}")
                        )
                    }
                }
            }
        }

        // 3. Platform Filter Chips
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "TARGET PLATFORM",
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        FilterChip(
                            selected = selectedPlatform == null,
                            onClick = { viewModel.setPlatformFilter(null) },
                            label = { Text("All Platforms", fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CyberCyan,
                                selectedLabelColor = CyberCyanDark,
                                containerColor = DarkSurfaceElevated,
                                labelColor = TextSecondary
                            )
                        )
                    }
                    items(Platform.values().toList()) { platform ->
                        val isSelected = selectedPlatform == platform
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                viewModel.setPlatformFilter(if (isSelected) null else platform)
                            },
                            label = { Text(platform.displayName, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = platform.brandColor.copy(alpha = 0.2f),
                                selectedLabelColor = platform.brandColor,
                                containerColor = DarkSurfaceElevated,
                                labelColor = TextSecondary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = if (isSelected) platform.brandColor else DarkCardBorder
                            )
                        )
                    }
                }
            }
        }

        // 4. Recommended Games Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "RECOMMENDED TITLES (${recommendations.size})",
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }

        items(recommendations) { rec ->
            GameRecommendationCard(
                recommendation = rec,
                onToggleWishlist = { viewModel.toggleWishlistGame(rec.game.id) },
                onScheduleSession = {
                    viewModel.setShowCreateSessionDialog(true)
                }
            )
        }

        // 5. Suggested Friends With Similar Interests Section
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "POTENTIAL SQUADMATES",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Gamers matching your titles, skill rating & platforms",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }
        }

        items(friendSuggestions) { suggestion ->
            FriendSuggestionCard(
                suggestion = suggestion,
                onSendRequest = { viewModel.sendFriendSuggestionRequest(suggestion.user.id) },
                onViewProfile = { viewModel.setViewingProfileUser(suggestion.user) }
            )
        }
    }
}

@Composable
fun GameRecommendationCard(
    recommendation: GameRecommendation,
    onToggleWishlist: () -> Unit,
    onScheduleSession: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .testTag("rec_card_${recommendation.game.id}"),
        color = DarkSurfaceVariant,
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            if (recommendation.game.bannerDrawableRes != null) {
                Image(
                    painter = painterResource(id = recommendation.game.bannerDrawableRes),
                    contentDescription = recommendation.game.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                )
            }

            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = CyberCyan.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan)
                        ) {
                            Text(
                                text = "${recommendation.matchScore}% MATCH",
                                color = CyberCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = DarkSurfaceElevated
                        ) {
                            Text(
                                text = recommendation.game.genre.title,
                                color = TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    IconButton(
                        onClick = onToggleWishlist,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("wishlist_btn_${recommendation.game.id}")
                    ) {
                        Icon(
                            imageVector = if (recommendation.isWishlisted) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Save Game",
                            tint = if (recommendation.isWishlisted) RadiantGold else TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(
                        text = recommendation.game.name,
                        color = TextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = recommendation.recommendationReason,
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }

                // Friends currently playing / active
                if (recommendation.friendsPlaying.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy((-6).dp)) {
                            recommendation.friendsPlaying.forEach { friend ->
                                UserAvatar(profile = friend, size = 24.dp, showStatusDot = false)
                            }
                        }
                        Text(
                            text = "${recommendation.friendsPlaying.joinToString { it.displayName }} playing this",
                            color = NeonGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Platforms & Actions
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

                    OutlinedButton(
                        onClick = onScheduleSession,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberCyan),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.6f)),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("schedule_game_btn_${recommendation.game.id}")
                    ) {
                        Icon(
                            Icons.Default.GroupAdd,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Create Squad", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun FriendSuggestionCard(
    suggestion: FriendSuggestion,
    onSendRequest: () -> Unit,
    onViewProfile: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onViewProfile() }
            .testTag("friend_suggestion_${suggestion.user.id}"),
        color = DarkSurfaceVariant,
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    UserAvatar(profile = suggestion.user, size = 42.dp)
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = suggestion.user.displayName,
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            PlatformBadge(platform = suggestion.sharedPlatform, compact = true)
                        }
                        Text(
                            text = suggestion.user.tag,
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = ElectricPurple.copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ElectricPurple)
                ) {
                    Text(
                        text = "${suggestion.compatibilityScore}% SQUAD FIT",
                        color = ElectricPurple,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Text(
                text = suggestion.recommendationReason,
                color = TextSecondary,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )

            // Common games tags
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    suggestion.commonGames.forEach { gameName ->
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = DarkSurfaceElevated
                        ) {
                            Text(
                                text = gameName,
                                color = CyberCyan,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Button(
                    onClick = onSendRequest,
                    enabled = !suggestion.isRequestSent,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (suggestion.isRequestSent) DarkSurfaceElevated else CyberCyan,
                        contentColor = if (suggestion.isRequestSent) TextSecondary else CyberCyanDark
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("send_friend_req_btn_${suggestion.user.id}")
                ) {
                    Icon(
                        imageVector = if (suggestion.isRequestSent) Icons.Default.Check else Icons.Default.PersonAdd,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (suggestion.isRequestSent) "Sent" else "Connect",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
