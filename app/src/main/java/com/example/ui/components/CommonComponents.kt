package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GamerProfile
import com.example.data.model.Platform
import com.example.data.model.UserStatus
import com.example.data.model.VoiceRoom
import com.example.ui.theme.*

@Composable
fun PlatformBadge(
    platform: Platform,
    modifier: Modifier = Modifier,
    compact: Boolean = false
) {
    val icon = when (platform) {
        Platform.PC -> Icons.Default.Computer
        Platform.PLAYSTATION -> Icons.Default.Gamepad
        Platform.XBOX -> Icons.Default.SportsEsports
        Platform.MOBILE -> Icons.Default.PhoneAndroid
        Platform.SWITCH -> Icons.Default.VideogameAsset
    }

    Surface(
        modifier = modifier.testTag("platform_badge_${platform.name}"),
        shape = RoundedCornerShape(6.dp),
        color = platform.brandColor.copy(alpha = 0.15f),
        border = androidx.compose.foundation.BorderStroke(1.dp, platform.brandColor.copy(alpha = 0.45f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = if (compact) 6.dp else 8.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = platform.displayName,
                tint = platform.brandColor,
                modifier = Modifier.size(if (compact) 12.dp else 14.dp)
            )
            Text(
                text = if (compact) platform.shortTag else platform.displayName,
                color = platform.brandColor,
                fontSize = if (compact) 10.sp else 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun StatusDot(status: UserStatus, modifier: Modifier = Modifier) {
    val color = when (status) {
        UserStatus.IN_GAME -> NeonGreen
        UserStatus.IN_VOICE -> CyberCyan
        UserStatus.ONLINE -> Color(0xFF38BDF8)
        UserStatus.AWAY -> RadiantGold
        UserStatus.OFFLINE -> TextMuted
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alphaAnim by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    val currentAlpha = if (status == UserStatus.IN_GAME || status == UserStatus.IN_VOICE) alphaAnim else 1.0f

    Box(
        modifier = modifier
            .size(10.dp)
            .shadow(4.dp, CircleShape, ambientColor = color, spotColor = color)
            .background(color.copy(alpha = currentAlpha), CircleShape)
            .border(1.5.dp, DarkBg, CircleShape)
    )
}

@Composable
fun UserAvatar(
    profile: GamerProfile,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    showStatusDot: Boolean = true
) {
    val bgBrush = Brush.linearGradient(
        colors = listOf(
            Color(profile.avatarColorHex),
            Color(profile.avatarColorHex).copy(alpha = 0.4f)
        )
    )

    Box(
        modifier = modifier
            .size(size)
            .testTag("user_avatar_${profile.id}")
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(bgBrush)
                .border(1.5.dp, Color(profile.avatarColorHex).copy(alpha = 0.8f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = profile.avatarInitial,
                color = Color.White,
                fontSize = (size.value * 0.4f).sp,
                fontWeight = FontWeight.Black
            )
        }

        if (showStatusDot) {
            StatusDot(
                status = profile.status,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 1.dp, y = 1.dp)
            )
        }
    }
}

@Composable
fun ActiveVoiceDockedBar(
    activeRoom: VoiceRoom,
    isMicMuted: Boolean,
    isDeafened: Boolean,
    onToggleMic: () -> Unit,
    onToggleDeafen: () -> Unit,
    onLeave: () -> Unit,
    onOpenComms: () -> Unit,
    modifier: Modifier = Modifier
) {
    val hasSpeakingMember = activeRoom.members.any { it.isSpeaking }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("docked_voice_bar")
            .clickable { onOpenComms() },
        color = DarkSurfaceElevated,
        shape = RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.4f)),
        tonalElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                // Animated soundwave icon
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (hasSpeakingMember) CyberCyan.copy(alpha = 0.2f) else DarkSurfaceVariant)
                        .border(1.dp, if (hasSpeakingMember) CyberCyan else DarkCardBorder, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (hasSpeakingMember) Icons.Default.GraphicEq else Icons.Default.Headset,
                        contentDescription = "Voice Active",
                        tint = if (hasSpeakingMember) CyberCyan else TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = activeRoom.name,
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "• ${activeRoom.pingMs}ms",
                            color = NeonGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Text(
                        text = "${activeRoom.members.size} connected | ${activeRoom.bitrate}",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            // Quick Mute / Deafen / Disconnect Controls
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onToggleMic,
                    modifier = Modifier
                        .size(38.dp)
                        .testTag("bar_mute_button")
                ) {
                    Icon(
                        imageVector = if (isMicMuted) Icons.Default.MicOff else Icons.Default.Mic,
                        contentDescription = "Toggle Mute",
                        tint = if (isMicMuted) NeonRed else CyberCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = onToggleDeafen,
                    modifier = Modifier
                        .size(38.dp)
                        .testTag("bar_deafen_button")
                ) {
                    Icon(
                        imageVector = if (isDeafened) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                        contentDescription = "Toggle Deafen",
                        tint = if (isDeafened) NeonRed else TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = onLeave,
                    modifier = Modifier
                        .size(38.dp)
                        .testTag("bar_disconnect_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.CallEnd,
                        contentDescription = "Leave Voice",
                        tint = NeonRed,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
