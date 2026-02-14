package tech.alt255.research.presentation.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay
import tech.alt255.research.R
import tech.alt255.research.presentation.ui.theme.GradientBottomLight
import tech.alt255.research.presentation.ui.theme.GradientTopLight
import tech.alt255.research.presentation.ui.theme.Gray
import tech.alt255.research.presentation.ui.theme.LightGray
import tech.alt255.research.presentation.ui.theme.MegaLightGray
import tech.alt255.research.presentation.ui.theme.OtherLightGray
import tech.alt255.research.presentation.ui.theme.Pink
import tech.alt255.research.presentation.ui.theme.ShadowColor
import tech.alt255.research.presentation.ui.theme.StarBlue
import tech.alt255.research.presentation.ui.theme.White
import tech.alt255.research.presentation.viewmodels.home.HomeViewModel
import tech.alt255.research.presentation.viewmodels.home.PaginationState

private const val SHADOW_BLUR_RADIUS = 12f
private val SHADOW_OFFSET = Offset(0f, 0f)

@Composable
fun Star(tint: Color, modifier: Modifier) {
    Icon(
        painter = painterResource(id = R.drawable.star4),
        contentDescription = null,
        tint = tint,
        modifier = modifier
    )
}

@Composable
fun HomeScreen(
    userId: Int,
    token: String,
    onNavigateToQuest: (questId: Int) -> Unit,
    onNavigateToUser: () -> Unit,
    onNavigateToRating: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val availableQuests by viewModel.availableQuests.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    val paginationState by viewModel.paginationState.collectAsStateWithLifecycle()

    val listState = rememberLazyListState()

    LaunchedEffect(userId, token) {
        viewModel.loadAvailableQuests(userId, token, refresh = true)
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleIndex ->
                if (lastVisibleIndex != null && lastVisibleIndex >= availableQuests.size - 5) {
                    viewModel.loadMoreQuests(userId, token)
                }
            }
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(120000)
            viewModel.refreshQuests(userId, token)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LightGray)
    ) {
        MaterialTheme {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(GradientTopLight, GradientBottomLight),
                                startY = 0f,
                                endY = Float.POSITIVE_INFINITY
                            ),
                            shape = RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp)
                        )
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp),
                            clip = false
                        )
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                                .padding(top = 20.dp)
                                .align(Alignment.Center)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.star4),
                                    contentDescription = "Логотип",
                                    tint = Pink,
                                    modifier = Modifier.size(40.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "ReSearch",
                                    color = White,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    style = TextStyle(
                                        shadow = Shadow(
                                            color = ShadowColor,
                                            offset = SHADOW_OFFSET,
                                            blurRadius = SHADOW_BLUR_RADIUS
                                        )
                                    )
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .drawWithContent {
                                            drawCircle(MegaLightGray)
                                            drawCircle(
                                                color = Pink,
                                                style = Stroke(width = 2.dp.toPx())
                                            )
                                            drawContent()
                                        }
                                        .clip(CircleShape)
                                        .background(White.copy(alpha = 0.2f))
                                        .clickable { onNavigateToUser() },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.avatar),
                                        contentDescription = "Аватар пользователя",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape)
                                            .align(Alignment.Center)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "Добро пожаловать!",
                                color = White.copy(alpha = 0.9f),
                                fontSize = 22.sp,
                                style = TextStyle(
                                    shadow = Shadow(
                                        color = ShadowColor,
                                        offset = SHADOW_OFFSET,
                                        blurRadius = SHADOW_BLUR_RADIUS
                                    )
                                )
                            )
                            Text(
                                text = "Исследуй, выполняй, получай награды",
                                color = White.copy(alpha = 0.8f),
                                fontSize = 14.sp,
                                style = TextStyle(
                                    shadow = Shadow(
                                        color = ShadowColor,
                                        offset = SHADOW_OFFSET,
                                        blurRadius = SHADOW_BLUR_RADIUS
                                    )
                                ),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }

                        Star(
                            tint = Pink,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(top = 45.dp, end = 10.dp)
                                .size(10.dp)
                        )
                        Star(
                            tint = White,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(top = 40.dp, end = 70.dp)
                                .size(8.dp)
                        )
                        Star(
                            tint = White,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(top = 120.dp, end = 25.dp)
                                .size(6.dp)
                        )
                        Star(
                            tint = White,
                            modifier = Modifier
                                .padding(top = 95.dp, start = 10.dp)
                                .size(10.dp)
                        )
                    }
                }

                when {
                    isLoading && availableQuests.isEmpty() -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = 50.dp),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            CircularProgressIndicator(color = Pink)
                        }
                    }
                    errorMessage != null && availableQuests.isEmpty() -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.star4),
                                contentDescription = null,
                                tint = Pink,
                                modifier = Modifier.size(60.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Ошибка загрузки",
                                color = Pink,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                style = TextStyle(
                                    shadow = Shadow(
                                        color = ShadowColor,
                                        offset = SHADOW_OFFSET,
                                        blurRadius = SHADOW_BLUR_RADIUS
                                    )
                                )
                            )
                            Text(
                                text = errorMessage ?: "Неизвестная ошибка",
                                color = OtherLightGray,
                                fontSize = 14.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Pink)
                                    .clickable {
                                        viewModel.clearError()
                                        viewModel.refreshQuests(userId, token)
                                    }
                                    .padding(horizontal = 24.dp, vertical = 12.dp)
                            ) {
                                Text(
                                    text = "Повторить",
                                    color = White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                    availableQuests.isEmpty() -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.star4),
                                contentDescription = null,
                                tint = OtherLightGray,
                                modifier = Modifier.size(80.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Пока нет доступных квестов",
                                color = OtherLightGray,
                                fontSize = 18.sp,
                                style = TextStyle(
                                    shadow = Shadow(
                                        color = ShadowColor,
                                        offset = SHADOW_OFFSET,
                                        blurRadius = SHADOW_BLUR_RADIUS
                                    )
                                )
                            )
                            Text(
                                text = "Загляните позже",
                                color = OtherLightGray.copy(alpha = 0.7f),
                                fontSize = 14.sp
                            )
                        }
                    }
                    else -> {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                                top = 20.dp,
                                bottom = 20.dp,
                                start = 16.dp,
                                end = 16.dp
                            )
                        ) {
                            items(availableQuests) { quest ->
                                HomeQuestCard(
                                    quest = quest,
                                    onClick = { onNavigateToQuest(quest.id) }
                                )
                            }

                            if (paginationState is PaginationState.Loading) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(
                                            color = Pink,
                                            modifier = Modifier.size(32.dp),
                                            strokeWidth = 3.dp
                                        )
                                    }
                                }
                            }

                            item {
                                Spacer(modifier = Modifier.height(30.dp))
                            }
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.BottomStart
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Pink.copy(alpha = 0.2f))
                                        .clickable {
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.star4),
                                        contentDescription = "Главная",
                                        tint = Pink,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(OtherLightGray.copy(alpha = 0.2f))
                                        .clickable {
                                            onNavigateToRating()
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.star4),
                                        contentDescription = "Рейтинг",
                                        tint = OtherLightGray,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HomeQuestCard(
    quest: tech.alt255.research.data.model.quest.AvailableQuest,
    onClick: () -> Unit
) {
    val borderColor = if (quest.isAccepted) StarBlue else White.copy(alpha = 0.2f)
    val borderWidth = if (quest.isAccepted) 2.dp else 1.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = ShadowColor.copy(alpha = 0.1f),
                spotColor = ShadowColor.copy(alpha = 0.1f)
            )
            .background(
                color = Gray,
                shape = RoundedCornerShape(20.dp)
            )
            .border(
                width = borderWidth,
                color = borderColor,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = quest.title,
                    color = White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                    style = TextStyle(
                        shadow = Shadow(
                            color = ShadowColor,
                            offset = SHADOW_OFFSET,
                            blurRadius = SHADOW_BLUR_RADIUS
                        )
                    )
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            when (quest.status) {
                                "active" -> Color(0xFF4CAF50)
                                "upcoming" -> Color(0xFF2196F3)
                                "expired" -> Color(0xFFF44336)
                                else -> OtherLightGray
                            }
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = when (quest.status) {
                            "active" -> "Активен"
                            "upcoming" -> "Скоро"
                            "expired" -> "Завершён"
                            else -> quest.status
                        },
                        color = White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        style = TextStyle(
                            shadow = Shadow(
                                color = ShadowColor,
                                offset = SHADOW_OFFSET,
                                blurRadius = SHADOW_BLUR_RADIUS
                            )
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = quest.shortDescription,
                color = OtherLightGray,
                fontSize = 14.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(StarBlue.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.star4),
                        contentDescription = "Награда",
                        tint = StarBlue,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${quest.rewardStars}",
                        color = StarBlue,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                quest.districtName?.let { district ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(OtherLightGray.copy(alpha = 0.15f))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.star4),
                            contentDescription = "Район",
                            tint = OtherLightGray,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = district,
                            color = OtherLightGray,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            if (quest.isAccepted && quest.userStatus == "in_progress") {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "В процессе",
                        color = StarBlue,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}