package com.example.ui.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MatchRecord
import com.example.data.model.PlayerStats
import com.example.ui.theme.*
import com.example.ui.viewmodel.GamingViewModel

@Composable
fun StatsScreen(
    viewModel: GamingViewModel,
    modifier: Modifier = Modifier
) {
    val statsList by viewModel.stats.collectAsState()
    var selectedGameId by remember { mutableStateOf(statsList.firstOrNull()?.gameId ?: "") }

    val currentStat = statsList.find { it.gameId == selectedGameId } ?: statsList.firstOrNull()

    Scaffold(
        modifier = modifier.testTag("stats_screen"),
        containerColor = Color.Transparent,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.setShowLogMatchDialog(true) },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Log Match Result", fontWeight = FontWeight.Bold) },
                containerColor = CyberCyan,
                contentColor = CyberCyanDark,
                modifier = Modifier
                    .padding(bottom = 72.dp)
                    .testTag("log_match_fab")
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
                        text = "COMPETITIVE STATS TRACKER",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Multi-genre performance analytics, ranked ladder progress, and match history.",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }

            // Competitive Title Selector Carousel
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(statsList) { stat ->
                        val isSelected = stat.gameId == currentStat?.gameId
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) CyberCyan.copy(alpha = 0.2f) else DarkSurfaceElevated,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) CyberCyan else DarkCardBorder
                            ),
                            modifier = Modifier
                                .clickable { selectedGameId = stat.gameId }
                                .testTag("stat_game_tab_${stat.gameId}")
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = stat.gameTitle,
                                    color = if (isSelected) CyberCyan else TextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = stat.genre.title,
                                    color = TextSecondary,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }

            if (currentStat != null) {
                // Tier & Rank Banner
                item {
                    RankBannerCard(stat = currentStat)
                }

                // Core Metrics Grid
                item {
                    StatMetricsGrid(stat = currentStat)
                }

                // Performance Trend Wave (Canvas)
                item {
                    PerformanceTrendCard(matches = currentStat.recentMatches)
                }

                // Recent Match History
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "RECENT MATCH HISTORY (${currentStat.recentMatches.size})",
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                }

                items(currentStat.recentMatches) { match ->
                    MatchHistoryItem(match = match)
                }
            }
        }
    }
}

@Composable
fun RankBannerCard(stat: PlayerStats) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, RadiantGold.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
        color = DarkSurfaceVariant
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = RadiantGold.copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, RadiantGold)
                ) {
                    Text(
                        text = "RANKED COMPETITIVE TIER",
                        color = RadiantGold,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Text(
                    text = stat.rankName,
                    color = TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black
                )

                Text(
                    text = stat.rankTier,
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }

            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(RadiantGold.copy(alpha = 0.15f))
                    .border(2.dp, RadiantGold, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.MilitaryTech,
                    contentDescription = "Rank Icon",
                    tint = RadiantGold,
                    modifier = Modifier.size(34.dp)
                )
            }
        }
    }
}

@Composable
fun StatMetricsGrid(stat: PlayerStats) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MetricTile(
                title = "WIN RATE",
                value = "${stat.winRatePct}%",
                subtitle = "${stat.matchesPlayed} Matches",
                accentColor = NeonGreen,
                modifier = Modifier.weight(1f)
            )
            MetricTile(
                title = "K / D RATIO",
                value = "${stat.kdRatio}",
                subtitle = "Eliminations",
                accentColor = CyberCyan,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MetricTile(
                title = "HEADSHOT / ACC",
                value = "${stat.headshotPct}%",
                subtitle = "Precision",
                accentColor = ElectricPurple,
                modifier = Modifier.weight(1f)
            )
            MetricTile(
                title = "HOURS LOGGED",
                value = "${stat.hoursLogged}h",
                subtitle = "${stat.mvpCount} MVPs",
                accentColor = RadiantGold,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun MetricTile(
    title: String,
    value: String,
    subtitle: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, DarkCardBorder, RoundedCornerShape(12.dp)),
        color = DarkSurfaceElevated
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = title, color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Text(text = value, color = accentColor, fontSize = 20.sp, fontWeight = FontWeight.Black)
            Text(text = subtitle, color = TextMuted, fontSize = 11.sp)
        }
    }
}

@Composable
fun PerformanceTrendCard(matches: List<MatchRecord>) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp)),
        color = DarkSurfaceVariant,
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "PERFORMANCE TREND (RECENT RUN)",
                color = TextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                val width = size.width
                val height = size.height

                if (matches.isNotEmpty()) {
                    val step = width / (matches.size + 1).coerceAtLeast(2)
                    val points = matches.mapIndexed { index, m ->
                        val y = if (m.isVictory) height * 0.25f else height * 0.75f
                        Offset(step * (index + 1), y)
                    }

                    val path = Path().apply {
                        moveTo(0f, height * 0.5f)
                        points.forEach { point ->
                            lineTo(point.x, point.y)
                        }
                    }

                    drawPath(
                        path = path,
                        color = CyberCyan,
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )

                    points.forEachIndexed { i, pt ->
                        val isVic = matches[i].isVictory
                        drawCircle(
                            color = if (isVic) NeonGreen else NeonRed,
                            radius = 5.dp.toPx(),
                            center = pt
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MatchHistoryItem(match: MatchRecord) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .testTag("match_item_${match.id}"),
        color = DarkSurfaceElevated,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (match.isVictory) NeonGreen.copy(alpha = 0.3f) else NeonRed.copy(alpha = 0.3f)
        )
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
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (match.isVictory) NeonGreen.copy(alpha = 0.2f) else NeonRed.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = if (match.isVictory) "VICTORY" else "DEFEAT",
                        color = if (match.isVictory) NeonGreen else NeonRed,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Column {
                    Text(
                        text = "${match.map} • ${match.outcome}",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Score: ${match.score} (${match.timeAgo})",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            if (match.hasHighlight) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = RadiantGold.copy(alpha = 0.2f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.Videocam, contentDescription = null, tint = RadiantGold, modifier = Modifier.size(12.dp))
                        Text("CLIP", color = RadiantGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
