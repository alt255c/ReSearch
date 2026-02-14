package tech.alt255.research.presentation.screens.rating

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.delay
import tech.alt255.research.R
import tech.alt255.research.presentation.ui.theme.*
import tech.alt255.research.presentation.viewmodels.rating.RatingViewModel

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
fun RatingScreen(
    userId: Int,
    token: String,
    onNavigateBack: () -> Unit,
    viewModel: RatingViewModel = hiltViewModel()
) {
    val ratingUsers by viewModel.ratingUsers.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    val currentUserRank by viewModel.currentUserRank.collectAsStateWithLifecycle()

    val listState = rememberLazyListState()

    LaunchedEffect(userId, token) {
        viewModel.loadRating(userId, token, refresh = true)
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleIndex ->
                if (lastVisibleIndex != null && lastVisibleIndex >= ratingUsers.size - 5) {
                    viewModel.loadMoreRating(userId, token)
                }
            }
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(120000)
            viewModel.refreshRating(userId, token)
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
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(White.copy(alpha = 0.2f))
                                        .clickable { onNavigateBack() },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.arrow),
                                        contentDescription = "Назад",
                                        tint = White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Text(
                                    text = "Рейтинг пользователей",
                                    color = White,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    style = TextStyle(
                                        shadow = Shadow(
                                            color = ShadowColor,
                                            offset = SHADOW_OFFSET,
                                            blurRadius = SHADOW_BLUR_RADIUS
                                        )
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "Рейтинг по звёздам",
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
                            currentUserRank?.let { rank ->
                                Text(
                                    text = "Ваше место: $rank",
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
                    isLoading && ratingUsers.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Pink)
                        }
                    }
                    errorMessage != null && ratingUsers.isEmpty() -> {
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
                                        viewModel.refreshRating(userId, token)
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
                    ratingUsers.isEmpty() -> {
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
                                text = "Рейтинг пуст",
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
                                text = "Нет активных пользователей",
                                color = OtherLightGray.copy(alpha = 0.7f),
                                fontSize = 14.sp
                            )
                        }
                    }
                    else -> {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(
                                top = 16.dp,
                                bottom = 16.dp,
                                start = 16.dp,
                                end = 16.dp
                            )
                        ) {
                            items(ratingUsers) { user ->
                                RatingUserCard(
                                    user = user,
                                    isCurrentUser = user.id == userId
                                )
                            }

                            if (isLoading && ratingUsers.isNotEmpty()) {
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
                    }
                }
            }
        }
    }
}

@Composable
fun RatingUserCard(
    user: tech.alt255.research.data.model.rating.RatingUser,
    isCurrentUser: Boolean
) {
    val borderColor = if (isCurrentUser) Pink else White.copy(alpha = 0.2f)
    val borderWidth = if (isCurrentUser) 2.dp else 1.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(15.dp),
                ambientColor = ShadowColor.copy(alpha = 0.1f),
                spotColor = ShadowColor.copy(alpha = 0.1f)
            )
            .background(
                color = Gray,
                shape = RoundedCornerShape(15.dp)
            )
            .border(
                width = borderWidth,
                color = borderColor,
                shape = RoundedCornerShape(15.dp)
            )
            .padding(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(30.dp)
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
                Text(
                    text = user.rank.toString(),
                    color = if (user.rank <= 3) Pink else OtherLightGray,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Box(
                modifier = Modifier
                    .size(38.dp)
                    .drawWithContent {
                        drawCircle(MegaLightGray)
                        drawCircle(
                            color = White,
                            style = Stroke(width = 1.dp.toPx())
                        )
                        drawContent()
                    }
            ) {
                val baseUrl = stringResource(R.string.base_url)
                val fullImageUrl = user.photo.takeIf { it.isNotEmpty() }?.let {
                    if (it.startsWith("http")) it else baseUrl + it.removePrefix("/")
                }
                if (!fullImageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(fullImageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Аватар",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .align(Alignment.Center),
                        error = painterResource(id = R.drawable.avatar),
                        placeholder = painterResource(id = R.drawable.avatar)
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.avatar),
                        contentDescription = "Аватар",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .align(Alignment.Center)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = user.name,
                    color = White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (user.nickname.isNotEmpty()) {
                    Text(
                        text = "@${user.nickname}",
                        color = OtherLightGray,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.star4),
                    contentDescription = "Звёзды",
                    tint = StarBlue,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = user.stars.toString(),
                    color = StarBlue,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}