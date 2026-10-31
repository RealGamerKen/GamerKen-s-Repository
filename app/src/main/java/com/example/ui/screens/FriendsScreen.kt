package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GamerProfile
import com.example.data.model.UserStatus
import com.example.ui.components.PlatformBadge
import com.example.ui.components.UserAvatar
import com.example.ui.theme.*
import com.example.ui.viewmodel.GamingViewModel

@Composable
fun FriendsScreen(
    viewModel: GamingViewModel,
    modifier: Modifier = Modifier
) {
    val subTab by viewModel.socialSubTab.collectAsState()
    val friends by viewModel.filteredFriends.collectAsState()
    val followers by viewModel.followers.collectAsState()
    val following by viewModel.following.collectAsState()
    val friendSuggestions by viewModel.friendSuggestions.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("friends_screen")
    ) {
        // Search & Social Filter Top Bar
        Surface(
            color = DarkSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = { Text("Search gamer tags, titles, or friends...", color = TextMuted, fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary) },
                    trailingIcon = if (searchQuery.isNotEmpty()) {
                        {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextSecondary)
                            }
                        }
                    } else null,
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberCyan,
                        unfocusedBorderColor = DarkCardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_friends_input")
                )

                // Sub-tabs: Friends, Followers, Following, Suggestions
                TabRow(
                    selectedTabIndex = subTab,
                    containerColor = Color.Transparent,
                    contentColor = CyberCyan,
                    divider = {}
                ) {
                    Tab(
                        selected = subTab == 0,
                        onClick = { viewModel.setSocialSubTab(0) },
                        text = { Text("Friends (${friends.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                        modifier = Modifier.testTag("tab_friends")
                    )
                    Tab(
                        selected = subTab == 1,
                        onClick = { viewModel.setSocialSubTab(1) },
                        text = { Text("Followers (${followers.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                        modifier = Modifier.testTag("tab_followers")
                    )
                    Tab(
                        selected = subTab == 2,
                        onClick = { viewModel.setSocialSubTab(2) },
                        text = { Text("Following (${following.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                        modifier = Modifier.testTag("tab_following")
                    )
                    Tab(
                        selected = subTab == 3,
                        onClick = { viewModel.setSocialSubTab(3) },
                        text = { Text("Suggested (${friendSuggestions.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                        modifier = Modifier.testTag("tab_suggestions")
                    )
                }
            }
        }

        // Active List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            when (subTab) {
                0 -> {
                    // Friends List
                    items(friends) { friend ->
                        GamerSocialItem(
                            user = friend,
                            onUserClick = { viewModel.setViewingProfileUser(friend) },
                            onFollowToggle = { viewModel.toggleFollow(friend.id) },
                            onInvite = {
                                viewModel.sendChatMessage(
                                    "Hey @${friend.displayName}, want to join up for a match?",
                                    null,
                                    friend.currentActivityTitle
                                )
                            }
                        )
                    }
                }
                1 -> {
                    // Followers List
                    items(followers) { follower ->
                        GamerSocialItem(
                            user = follower,
                            onUserClick = { viewModel.setViewingProfileUser(follower) },
                            onFollowToggle = { viewModel.toggleFollow(follower.id) },
                            onInvite = { viewModel.addFriend(follower.id) }
                        )
                    }
                }
                2 -> {
                    // Following List
                    items(following) { followingUser ->
                        GamerSocialItem(
                            user = followingUser,
                            onUserClick = { viewModel.setViewingProfileUser(followingUser) },
                            onFollowToggle = { viewModel.toggleFollow(followingUser.id) },
                            onInvite = {
                                viewModel.sendChatMessage("GGs from the match! Good playing with you.")
                            }
                        )
                    }
                }
                3 -> {
                    // Suggested Squadmates
                    items(friendSuggestions) { suggestion ->
                        FriendSuggestionCard(
                            suggestion = suggestion,
                            onSendRequest = { viewModel.sendFriendSuggestionRequest(suggestion.user.id) },
                            onViewProfile = { viewModel.setViewingProfileUser(suggestion.user) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GamerSocialItem(
    user: GamerProfile,
    onUserClick: () -> Unit,
    onFollowToggle: () -> Unit,
    onInvite: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onUserClick() }
            .testTag("gamer_social_item_${user.id}"),
        color = DarkSurfaceVariant,
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                UserAvatar(profile = user, size = 44.dp)

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = user.displayName,
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        PlatformBadge(platform = user.currentPlatform, compact = true)
                    }

                    Text(
                        text = user.tag,
                        color = TextSecondary,
                        fontSize = 11.sp
                    )

                    Text(
                        text = if (user.status == UserStatus.IN_GAME) "Playing: ${user.currentActivityTitle}" else user.status.label,
                        color = if (user.status == UserStatus.IN_GAME) NeonGreen else TextMuted,
                        fontSize = 10.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                // Follow / Following toggle button
                OutlinedButton(
                    onClick = onFollowToggle,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (user.isFollowing) TextSecondary else CyberCyan
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (user.isFollowing) DarkCardBorder else CyberCyan.copy(alpha = 0.5f)
                    ),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("follow_toggle_btn_${user.id}")
                ) {
                    Text(
                        text = if (user.isFollowing) "Following" else "Follow",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Quick invite / chat button
                IconButton(
                    onClick = onInvite,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(DarkSurfaceElevated)
                        .testTag("invite_friend_btn_${user.id}")
                ) {
                    Icon(
                        Icons.Default.Send,
                        contentDescription = "Message",
                        tint = CyberCyan,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
