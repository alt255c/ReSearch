package tech.alt255.research.presentation.screens.user

import android.R.attr.onClick
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.delay
import tech.alt255.research.R
import tech.alt255.research.presentation.ui.theme.*
import tech.alt255.research.presentation.viewmodels.user.UserViewModel
import tech.alt255.research.presentation.viewmodels.user.UserUiState
import tech.alt255.research.presentation.viewmodels.user.MenuMode

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
fun BackroundLayer(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LightGray)
    ) {
        MaterialTheme(
            colorScheme = MaterialTheme.colorScheme,
            content = content
        )
    }
}

@Composable
fun TopContentUser(
    name: String,
    nickname: String,
    level: Int,
    imageUrl: String?
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(262.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(GradientTopLight, GradientBottomLight),
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY
                ),
                shape = RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp)
            )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .align(Alignment.Center)
            ) {
                Row {
                    Box(
                        modifier = Modifier
                            .size(75.dp)
                            .drawWithContent {
                                drawCircle(MegaLightGray)
                                drawCircle(
                                    color = White,
                                    style = Stroke(width = 1.dp.toPx())
                                )
                                drawContent()
                            }
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

                        Surface(
                            modifier = Modifier
                                .size(42.dp, 16.dp)
                                .align(Alignment.BottomEnd)
                                .offset(y = 0.5.dp),
                            shape = RoundedCornerShape(50),
                            color = Pink,
                            border = BorderStroke(1.dp, White)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Text(
                                    text = "$level lvl",
                                    color = White,
                                    style = TextStyle(fontSize = 12.sp)
                                )
                            }
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.CenterVertically)
                            .padding(20.dp)
                            .offset(y = if (!nickname.equals("@0")) (-5).dp else 0.dp)
                    ) {
                        Text(
                            text = name,
                            color = White,
                            style = TextStyle(
                                fontSize = 24.sp,
                                shadow = Shadow(
                                    color = ShadowColor,
                                    offset = SHADOW_OFFSET,
                                    blurRadius = SHADOW_BLUR_RADIUS
                                )
                            )
                        )
                        if (!nickname.equals("@0")) {
                            Text(
                                text = nickname,
                                color = TextHint,
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    shadow = Shadow(
                                        color = ShadowColor,
                                        offset = SHADOW_OFFSET,
                                        blurRadius = SHADOW_BLUR_RADIUS
                                    )
                                ),
                                modifier = Modifier.padding(top = 5.dp)
                            )
                        }
                    }
                }
            }
            Star(
                tint = Pink,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .absolutePadding(top = 90.dp, right = 10.dp)
                    .size(10.dp)
            )
            Star(
                tint = White,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .absolutePadding(top = 80.dp, right = 70.dp)
                    .size(8.dp)
            )
            Star(
                tint = White,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .absolutePadding(top = 160.dp, right = 25.dp)
                    .size(6.dp)
            )
            Star(
                tint = White,
                modifier = Modifier
                    .absolutePadding(top = 170.dp, left = 10.dp)
                    .size(10.dp)
            )
        }
    }
}

@Composable
fun StarsScope(
    modifier: Modifier,
    currentStars: Int,
    nextLevelStars: Int
) {
    val progress = if (nextLevelStars > 0) {
        currentStars.toFloat() / (currentStars + nextLevelStars).toFloat()
    } else {
        0f
    }

    Box(
        modifier = modifier
            .padding(15.dp)
            .fillMaxWidth()
            .height(110.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(GradientTopLight, GradientBottomLight),
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .border(
                width = 1.dp,
                color = White,
                shape = RoundedCornerShape(20.dp)
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(modifier = Modifier.size(44.dp)) {
                    Star(
                        tint = Pink,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .absolutePadding(top = 0.dp, right = 0.dp)
                            .size(40.dp)
                    )
                    Star(
                        tint = StarBlue,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .absolutePadding(bottom = 0.dp, left = 0.dp)
                            .size(15.dp)
                    )
                }
                Column(modifier = Modifier.padding(horizontal = 10.dp)) {
                    Text(
                        text = "$currentStars из ${currentStars + nextLevelStars}",
                        color = White,
                        style = TextStyle(
                            fontSize = 28.sp,
                            shadow = Shadow(
                                color = ShadowColor,
                                offset = SHADOW_OFFSET,
                                blurRadius = SHADOW_BLUR_RADIUS
                            )
                        )
                    )
                    Text(
                        text = "Собрано",
                        color = White,
                        style = TextStyle(
                            fontSize = 14.sp,
                            shadow = Shadow(
                                color = ShadowColor,
                                offset = SHADOW_OFFSET,
                                blurRadius = SHADOW_BLUR_RADIUS
                            )
                        ),
                        modifier = Modifier.offset(y = (-5).dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(7.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .background(
                        color = StarBlue,
                        shape = RoundedCornerShape(20.dp)
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(fraction = progress)
                        .background(
                            color = Pink,
                            shape = RoundedCornerShape(20.dp)
                        )
                )
            }
            Spacer(modifier = Modifier.height(7.dp))
            Text(
                text = "До следующего уровня $nextLevelStars звёзд",
                color = TextHint,
                style = TextStyle(fontSize = 12.sp)
            )
        }
    }
}

@Composable
fun Settings(
    modifier: Modifier,
) {
    Box(
        modifier = modifier
            .padding(top = 50.dp, end = 16.dp)
            .size(40.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.pen),
            contentDescription = "Редактировать профиль",
            tint = White,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun TopContent(
    name: String,
    nickname: String,
    level: Int,
    stars: Int,
    nextLevelStars: Int,
    imageUrl: String?,
    onNavigateToEditProfile: () -> Unit,
) {
    Box {
        TopContentUser(
            name = name,
            nickname = nickname,
            level = level,
            imageUrl = imageUrl
        )
        Settings(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { onNavigateToEditProfile() }
        )
        StarsScope(
            modifier = Modifier.offset(y = 180.dp),
            currentStars = stars,
            nextLevelStars = nextLevelStars
        )
    }
}

@Composable
fun MenuContent(
    selectedMenu: MenuMode,
    onMenuSelected: (MenuMode) -> Unit
) {
    Box {
        Box(
            modifier = Modifier
                .height(2.dp)
                .fillMaxWidth()
                .background(color = Gray)
                .align(Alignment.BottomCenter)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp)
                .height(32.dp)
        ) {
            Column(
                modifier = Modifier
                    .clickable { onMenuSelected(MenuMode.QUESTS) }
            ) {
                Text(
                    text = "Квесты",
                    color = if (selectedMenu == MenuMode.QUESTS) White else OtherLightGray,
                    style = TextStyle(
                        fontSize = 18.sp,
                        shadow = Shadow(
                            color = ShadowColor,
                            offset = SHADOW_OFFSET,
                            blurRadius = SHADOW_BLUR_RADIUS
                        )
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .height(2.dp)
                        .width(60.dp)
                        .background(color = if (selectedMenu == MenuMode.QUESTS) White else Gray)
                )
            }

            Spacer(modifier = Modifier.width(15.dp))

            Column(
                modifier = Modifier
                    .clickable { onMenuSelected(MenuMode.ACHIVMENTS) },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Ачивки",
                    color = if (selectedMenu == MenuMode.ACHIVMENTS) White else OtherLightGray,
                    style = TextStyle(
                        fontSize = 18.sp,
                        shadow = Shadow(
                            color = ShadowColor,
                            offset = SHADOW_OFFSET,
                            blurRadius = SHADOW_BLUR_RADIUS
                        )
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .height(2.dp)
                        .width(60.dp)
                        .background(color = if (selectedMenu == MenuMode.ACHIVMENTS) White else Gray)
                )
            }

            Spacer(modifier = Modifier.width(15.dp))

            Column(
                modifier = Modifier
                    .clickable { onMenuSelected(MenuMode.CATS) },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Котики",
                    color = if (selectedMenu == MenuMode.CATS) White else OtherLightGray,
                    style = TextStyle(
                        fontSize = 18.sp,
                        shadow = Shadow(
                            color = ShadowColor,
                            offset = SHADOW_OFFSET,
                            blurRadius = SHADOW_BLUR_RADIUS
                        )
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .height(2.dp)
                        .width(60.dp)
                        .background(color = if (selectedMenu == MenuMode.CATS) White else Gray)
                )
            }
        }
    }
}

@Composable
fun Achivment(
    name: String,
    description: String,
    isCompleted: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(45.dp)
    ) {
        Box(
            modifier = Modifier
                .size(45.dp)
                .drawWithContent {
                    drawCircle(color = MegaLightGray)

                    val strokeWidth = 1.dp.toPx()
                    val radius = size.minDimension / 2 - strokeWidth / 2
                    val center = Offset(size.width / 2, size.height / 2)

                    drawCircle(
                        color = White,
                        radius = radius,
                        center = center,
                        style = Stroke(
                            width = strokeWidth,
                            pathEffect = PathEffect.dashPathEffect(
                                intervals = floatArrayOf(10f, 5f),
                                phase = 0f
                            )
                        )
                    )

                    drawContent()
                }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.star4),
                    contentDescription = null,
                    tint = Pink,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .weight(1f)
        ) {
            Text(
                text = name,
                color = White,
                style = TextStyle(
                    fontSize = 18.sp,
                    shadow = Shadow(
                        color = ShadowColor,
                        offset = SHADOW_OFFSET,
                        blurRadius = SHADOW_BLUR_RADIUS
                    )
                )
            )
            Text(
                text = description,
                color = OtherLightGray,
                style = TextStyle(
                    fontSize = 14.sp,
                    shadow = Shadow(
                        color = ShadowColor,
                        offset = SHADOW_OFFSET,
                        blurRadius = SHADOW_BLUR_RADIUS
                    )
                )
            )
        }

        Icon(
            painter = painterResource(id = R.drawable.star4),
            contentDescription = null,
            tint = if (isCompleted) Pink else Gray.copy(alpha = 0.5f),
            modifier = Modifier
                .size(24.dp)
                .align(Alignment.CenterVertically)
                .padding(end = 8.dp)
        )
    }
}

@Composable
fun CutCornerShapeBox(
    modifier: Modifier = Modifier,
    color: Color = Color.White,
    gradient: Brush? = null,
    cornerRadius: Dp = 8.dp,
    cutSize: Dp = 30.dp,
    borderWidth: Dp = 0.dp,
    borderColor: Color = Color.Transparent,
    borderGradient: Brush? = null,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier.drawWithContent {
            val width = size.width
            val height = size.height
            val cornerRadiusPx = cornerRadius.toPx()
            val cutSizePx = cutSize.toPx()
            val borderWidthPx = borderWidth.toPx()

            val path = Path().apply {
                moveTo(0f, 0f)

                lineTo(width - cornerRadiusPx, 0f)

                arcTo(
                    rect = Rect(
                        left = width - cornerRadiusPx * 2,
                        top = 0f,
                        right = width,
                        bottom = cornerRadiusPx * 2
                    ),
                    startAngleDegrees = 270f,
                    sweepAngleDegrees = 90f,
                    forceMoveTo = false
                )

                lineTo(width, height - cornerRadiusPx)

                arcTo(
                    rect = Rect(
                        left = width - cornerRadiusPx * 2,
                        top = height - cornerRadiusPx * 2,
                        right = width,
                        bottom = height
                    ),
                    startAngleDegrees = 0f,
                    sweepAngleDegrees = 90f,
                    forceMoveTo = false
                )

                lineTo(cutSizePx, height)

                lineTo(0f, height - cutSizePx)

                close()
            }

            if (gradient != null) {
                drawPath(path, brush = gradient)
            } else {
                drawPath(path, color = color)
            }

            if (borderWidthPx > 0) {
                if (borderGradient != null) {
                    drawPath(
                        path = path,
                        brush = borderGradient,
                        style = Stroke(width = borderWidthPx)
                    )
                } else {
                    drawPath(
                        path = path,
                        color = borderColor,
                        style = Stroke(width = borderWidthPx)
                    )
                }
            }

            drawContent()
        }
    ) {
        content()
    }
}

@Composable
fun CatBox(
    modifier: Modifier = Modifier,
    name: String,
    data: String?,
    rarity: String,
    imageUrl: String?
) {
    val context = LocalContext.current
    val baseUrl = stringResource(R.string.base_url)
    val fullImageUrl = imageUrl?.let {
        if (it.startsWith("http")) it else baseUrl + it.removePrefix("/")
    }

    Box(
        modifier = modifier
            .height(110.dp)
            .width(170.dp)
            .background(
                color = White,
                shape = RoundedCornerShape(20.dp)
            )
            .border(
                width = 1.dp,
                color = OtherLightGray,
                shape = RoundedCornerShape(20.dp)
            )
            .clip(RoundedCornerShape(20.dp))
    ) {
        if (!fullImageUrl.isNullOrBlank()) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(fullImageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Котик",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxHeight()
                    .align(Alignment.BottomStart)
                    .background(
                        color = White,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .graphicsLayer {
                        shape = RoundedCornerShape(
                            topStart = 20.dp,
                            topEnd = 0.dp,
                            bottomEnd = 0.dp,
                            bottomStart = 20.dp
                        )
                        clip = true
                    },
                error = painterResource(id = R.drawable.avatar),
                placeholder = painterResource(id = R.drawable.avatar)
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.avatar),
                contentDescription = "Котик",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxHeight()
                    .align(Alignment.BottomStart)
                    .background(
                        color = White,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .graphicsLayer {
                        shape = RoundedCornerShape(
                            topStart = 20.dp,
                            topEnd = 0.dp,
                            bottomEnd = 0.dp,
                            bottomStart = 20.dp
                        )
                        clip = true
                    }
            )
        }

        CutCornerShapeBox(
            modifier = Modifier
                .size(86.dp, 109.dp)
                .padding(0.dp)
                .align(Alignment.CenterEnd),
            gradient = Brush.linearGradient(
                colors = listOf(GradientTopLight, GradientBottomLight),
                start = Offset(0f, 0f),
                end = Offset(0f, 120f)
            ),
            cornerRadius = 20.dp,
            cutSize = 40.dp,
            borderWidth = 1.dp,
            borderColor = OtherLightGray
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = name,
                    color = White,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 4.dp, end = 6.dp)
                )
                Text(
                    text = "",
                    color = White,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 16.dp, end = 6.dp)
                )
                Text(
                    text = if (!data.isNullOrEmpty()) {
                        data.substring(0, minOf(10, data.length))
                    } else "",
                    color = White,
                    fontSize = 10.sp,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 6.dp)
                )
                Text(
                    text = if (rarity.isNotEmpty()) rarity.first().toString().uppercase() else "",
                    color = White,
                    fontSize = 24.sp,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 6.dp, end = 8.dp)
                )
            }
        }
    }
}

@Composable
fun UserScreen(
    userId: Int,
    token: String,
    onNavigateToQuest: (questId: Int) -> Unit,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToRating: () -> Unit,
    viewModel: UserViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val userData by viewModel.userData.collectAsStateWithLifecycle()
    val selectedMenu by viewModel.selectedMenu.collectAsStateWithLifecycle()
    val isRefreshingProfile by viewModel.isRefreshingProfile.collectAsStateWithLifecycle()
    val isPullRefreshing by viewModel.isPullRefreshing.collectAsStateWithLifecycle()

    val achievementsData by viewModel.achievementsData.collectAsStateWithLifecycle()
    val catsData by viewModel.catsData.collectAsStateWithLifecycle()
    val questsData by viewModel.questsData.collectAsStateWithLifecycle()

    LaunchedEffect(userId, token) {
        viewModel.loadUserProfile(userId, token)
        viewModel.loadUserQuests(userId, token, page = 1)
    }

    LaunchedEffect(selectedMenu) {
        when (selectedMenu) {
            MenuMode.ACHIVMENTS -> {
                if (achievementsData.items.isEmpty()) {
                    viewModel.loadUserAchievements(userId, token, page = 1)
                }
            }
            MenuMode.CATS -> {
                if (catsData.items.isEmpty()) {
                    viewModel.loadUserCats(userId, token, page = 1)
                }
            }
            MenuMode.QUESTS -> {
                if (questsData.items.isEmpty()) {
                    viewModel.loadUserQuests(userId, token, page = 1)
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(60000)
            viewModel.refreshAllData(userId, token)
        }
    }

    BackroundLayer {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(0.dp)
                ) {
                    if (isRefreshingProfile) {
                        CircularProgressIndicator(
                            color = Pink,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                when (uiState) {
                    is UserUiState.InitialLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Pink)
                        }
                    }
                    is UserUiState.Success -> {
                        val profile = userData?.profile
                        Column(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            TopContent(
                                name = profile?.userName ?: "Пользователь",
                                nickname = profile?.userNickname?.takeIf { it.isNotEmpty() }?.let { "@$it" } ?: "",
                                level = profile?.level ?: 1,
                                stars = profile?.stars ?: 0,
                                nextLevelStars = profile?.nextLevelStars ?: 500,
                                imageUrl = profile?.userPhoto,
                                onNavigateToEditProfile
                            )

                            Spacer(modifier = Modifier.height(70.dp))

                            MenuContent(
                                selectedMenu = selectedMenu,
                                onMenuSelected = {
                                    viewModel.setSelectedMenu(it)
                                }
                            )

                            Box(modifier = Modifier.weight(1f)) {
                                when (selectedMenu) {
                                    MenuMode.ACHIVMENTS -> {
                                        AchievementsContent(
                                            achievements = achievementsData.items,
                                            isLoading = achievementsData.isLoading,
                                            hasMore = achievementsData.hasMore,
                                            onLoadMore = { viewModel.loadMore(userId, token) }
                                        )
                                    }
                                    MenuMode.CATS -> {
                                        CatsContent(
                                            cats = catsData.items,
                                            isLoading = catsData.isLoading,
                                            hasMore = catsData.hasMore,
                                            onLoadMore = { viewModel.loadMore(userId, token) }
                                        )
                                    }
                                    MenuMode.QUESTS -> {
                                        QuestsContent(
                                            quests = questsData.items,
                                            isLoading = questsData.isLoading,
                                            hasMore = questsData.hasMore,
                                            onLoadMore = { viewModel.loadMore(userId, token) },
                                            onQuestClick = onNavigateToQuest
                                        )
                                    }
                                }
                            }
                        }
                    }
                    is UserUiState.Error -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = (uiState as UserUiState.Error).message,
                                color = White,
                                fontSize = 18.sp
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.Transparent, LightGray)
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(OtherLightGray.copy(alpha = 0.2f))
                        .clickable { onNavigateToHome() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.home),
                        contentDescription = "Главная",
                        tint = OtherLightGray,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(20.dp))

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(OtherLightGray.copy(alpha = 0.2f))
                        .clickable { onNavigateToRating() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ranking),
                        contentDescription = "Рейтинг",
                        tint = OtherLightGray,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AchievementsContent(
    achievements: List<tech.alt255.research.data.model.user.UserAchievement>,
    isLoading: Boolean,
    hasMore: Boolean,
    onLoadMore: () -> Unit
) {
    val listState = rememberLazyListState()

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleIndex ->
                if (lastVisibleIndex != null && lastVisibleIndex >= achievements.size - 5) {
                    if (!isLoading && hasMore) {
                        onLoadMore()
                    }
                }
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp)
    ) {
        if (achievements.isEmpty() && !isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Нет ачивок",
                    color = OtherLightGray,
                    fontSize = 14.sp
                )
            }
        } else {
            LazyColumn(
                state = listState,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 60.dp)
            ) {
                val activeAchievements = achievements.filter { !it.isCompleted }
                val completedAchievements = achievements.filter { it.isCompleted }

                if (activeAchievements.isNotEmpty()) {
                    item {
                        Text(
                            text = "Активные",
                            color = White,
                            style = TextStyle(
                                fontSize = 18.sp,
                                shadow = Shadow(
                                    color = ShadowColor,
                                    offset = SHADOW_OFFSET,
                                    blurRadius = SHADOW_BLUR_RADIUS
                                )
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    items(activeAchievements) { achievement ->
                        Achivment(
                            name = achievement.name,
                            description = achievement.description,
                            isCompleted = false
                        )
                    }
                }

                if (completedAchievements.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = "Выполнены",
                            color = White,
                            style = TextStyle(
                                fontSize = 18.sp,
                                shadow = Shadow(
                                    color = ShadowColor,
                                    offset = SHADOW_OFFSET,
                                    blurRadius = SHADOW_BLUR_RADIUS
                                )
                            )
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    items(completedAchievements) { achievement ->
                        Achivment(
                            name = achievement.name,
                            description = achievement.description,
                            isCompleted = true
                        )
                    }
                }

                if (isLoading) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = Pink,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CatsContent(
    cats: List<tech.alt255.research.data.model.user.UserCat>,
    isLoading: Boolean,
    hasMore: Boolean,
    onLoadMore: () -> Unit
) {
    val listState = rememberLazyListState()

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleIndex ->
                if (lastVisibleIndex != null && lastVisibleIndex >= cats.size - 5) {
                    if (!isLoading && hasMore) {
                        onLoadMore()
                    }
                }
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp)
    ) {
        Text(
            text = "Котики",
            color = White,
            style = TextStyle(
                fontSize = 18.sp,
                shadow = Shadow(
                    color = ShadowColor,
                    offset = SHADOW_OFFSET,
                    blurRadius = SHADOW_BLUR_RADIUS
                )
            )
        )
        Spacer(modifier = Modifier.height(6.dp))

        if (cats.isEmpty() && !isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Нет котиков",
                    color = OtherLightGray,
                    fontSize = 14.sp
                )
            }
        } else {
            LazyColumn(
                state = listState,
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(cats.chunked(2)) { rowCats ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        rowCats.forEach { cat ->
                            CatBox(
                                name = cat.name,
                                data = cat.obtainedAt,
                                rarity = cat.rarity,
                                imageUrl = cat.imageUrl
                            )
                        }

                        if (rowCats.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }

                if (isLoading) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = Pink,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuestsContent(
    quests: List<tech.alt255.research.data.model.quest.UserQuest>,
    isLoading: Boolean,
    hasMore: Boolean,
    onLoadMore: () -> Unit,
    onQuestClick: (questId: Int) -> Unit
) {
    val listState = rememberLazyListState()

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleIndex ->
                if (lastVisibleIndex != null && lastVisibleIndex >= quests.size - 5) {
                    if (!isLoading && hasMore) {
                        onLoadMore()
                    }
                }
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp)
    ) {
        val relevantQuests = quests.filter { it.isRelevant }
        val nonRelevantQuests = quests.filter { !it.isRelevant }

        if (relevantQuests.isEmpty() && nonRelevantQuests.isEmpty() && !isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Нет активных квестов",
                    color = OtherLightGray,
                    fontSize = 14.sp
                )
            }
        } else {
            LazyColumn(
                state = listState,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 60.dp)
            ) {
                if (relevantQuests.isNotEmpty()) {
                    item {
                        Text(
                            text = "Актуальные квесты",
                            color = White,
                            style = TextStyle(
                                fontSize = 18.sp,
                                shadow = Shadow(
                                    color = ShadowColor,
                                    offset = SHADOW_OFFSET,
                                    blurRadius = SHADOW_BLUR_RADIUS
                                )
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                    }

                    items(relevantQuests) { quest ->
                        UserQuestItem(
                            quest = quest,
                            onClick = { onQuestClick(quest.id) }
                        )
                    }
                }

                if (nonRelevantQuests.isNotEmpty()) {
                    item {
                        if (relevantQuests.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                        Text(
                            text = "Завершенные квесты",
                            color = OtherLightGray,
                            style = TextStyle(
                                fontSize = 18.sp,
                                shadow = Shadow(
                                    color = ShadowColor,
                                    offset = SHADOW_OFFSET,
                                    blurRadius = SHADOW_BLUR_RADIUS
                                )
                            )
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    items(nonRelevantQuests) { quest ->
                        UserQuestItem(
                            quest = quest,
                            onClick = { onQuestClick(quest.id) }
                        )
                    }
                }

                if (isLoading) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = Pink,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UserQuestItem(
    quest: tech.alt255.research.data.model.quest.UserQuest,
    onClick: () -> Unit
) {
    val progressValue = if (quest.totalSteps > 0)
        quest.progress.toFloat() / quest.totalSteps.toFloat()
    else 0f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Gray,
                shape = RoundedCornerShape(15.dp)
            )
            .border(
                width = 1.dp,
                color = MegaLightGray,
                shape = RoundedCornerShape(15.dp)
            )
            .clickable(onClick = onClick)
            .padding(15.dp)
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
                    fontSize = 16.sp,
                    modifier = Modifier.weight(1f),
                    style = TextStyle(
                        shadow = Shadow(
                            color = ShadowColor,
                            offset = SHADOW_OFFSET,
                            blurRadius = SHADOW_BLUR_RADIUS
                        )
                    )
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${quest.rewardStars}",
                        color = StarBlue,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        painter = painterResource(id = R.drawable.star4),
                        contentDescription = null,
                        tint = StarBlue,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = quest.description,
                color = OtherLightGray,
                fontSize = 14.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .background(
                        color = MegaLightGray,
                        shape = RoundedCornerShape(4.dp)
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(fraction = progressValue)
                        .background(
                            color = when (quest.userStatus) {
                                "completed" -> Pink
                                "in_progress" -> StarBlue
                                "failed" -> Color.Red
                                else -> OtherLightGray
                            },
                            shape = RoundedCornerShape(4.dp)
                        )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${quest.progress}/${quest.totalSteps} шагов",
                    color = OtherLightGray,
                    fontSize = 12.sp
                )

                Text(
                    text = when (quest.userStatus) {
                        "completed" -> "Завершено"
                        "in_progress" -> "В процессе"
                        "failed" -> "Провалено"
                        else -> quest.userStatus ?: "Не начат"
                    },
                    color = when (quest.userStatus) {
                        "completed" -> Pink
                        "in_progress" -> StarBlue
                        "failed" -> Color.Red
                        else -> OtherLightGray
                    },
                    fontSize = 12.sp
                )
            }

            quest.completedAt?.let { completedAt ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Завершен: $completedAt",
                    color = OtherLightGray.copy(alpha = 0.7f),
                    fontSize = 10.sp
                )
            }
        }
    }
}