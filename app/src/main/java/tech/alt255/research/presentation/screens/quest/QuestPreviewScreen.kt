package tech.alt255.research.presentation.screens.quest

import android.annotation.SuppressLint
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
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
import tech.alt255.research.presentation.viewmodels.quest.AcceptQuestResult
import tech.alt255.research.presentation.viewmodels.quest.QuestPreviewViewModel
import tech.alt255.research.presentation.ui.theme.SuccessColor

private const val SHADOW_BLUR_RADIUS = 12f
private val SHADOW_OFFSET = Offset(0f, 0f)

@SuppressLint("LocalContextGetResourceValueCall")
@Composable
fun QuestPreviewScreen(
    questId: Int,
    userId: Int,
    token: String,
    onNavigateBack: () -> Unit,
    onNavigateToQuestStep: (questId: Int, stepNumber: Int) -> Unit,
    viewModel: QuestPreviewViewModel = hiltViewModel()
) {
    val questPreview by viewModel.questPreview.collectAsStateWithLifecycle()
    val currentProgress by viewModel.currentProgress.collectAsStateWithLifecycle()
    val totalSteps by viewModel.totalSteps.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    val acceptResult by viewModel.acceptQuestResult.collectAsStateWithLifecycle()

    LaunchedEffect(questId, userId, token) {
        viewModel.loadQuestPreview(userId, token, questId)
    }

    val context = LocalContext.current
    val baseUrl = stringResource(R.string.base_url)

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
                        .height(160.dp)
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier.size(48.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Star(tint = Pink, modifier = Modifier.size(48.dp))
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = questPreview?.title ?: "Загрузка...",
                                        color = White,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 2,
                                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
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
                        }

                        Star(tint = Pink, modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 50.dp, end = 10.dp)
                            .size(10.dp))
                        Star(tint = White, modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 40.dp, end = 70.dp)
                            .size(8.dp))
                        Star(tint = White, modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 100.dp, end = 25.dp)
                            .size(6.dp))
                        Star(tint = White, modifier = Modifier
                            .padding(top = 100.dp, start = 10.dp)
                            .size(10.dp))
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                when {
                    isLoading -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Pink)
                        }
                    }
                    errorMessage != null -> {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(16.dp),
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
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Pink)
                                    .clickable {
                                        viewModel.clearError()
                                        viewModel.loadQuestPreview(userId, token, questId)
                                    }
                                    .padding(horizontal = 32.dp, vertical = 16.dp)
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
                    questPreview == null -> {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(16.dp),
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
                                text = "Квест не найден",
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
                                text = "Возможно, он был удалён или недоступен",
                                color = OtherLightGray.copy(alpha = 0.7f),
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Pink)
                                    .clickable { onNavigateBack() }
                                    .padding(horizontal = 32.dp, vertical = 16.dp)
                            ) {
                                Text(
                                    text = "Вернуться назад",
                                    color = White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                    else -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(horizontal = 16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(120.dp)
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
                                    .padding(12.dp)
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(modifier = Modifier.size(44.dp)) {
                                            Star(
                                                tint = Pink,
                                                modifier = Modifier
                                                    .align(Alignment.TopEnd)
                                                    .size(40.dp)
                                            )
                                            Star(
                                                tint = StarBlue,
                                                modifier = Modifier
                                                    .align(Alignment.BottomStart)
                                                    .size(15.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = "${questPreview!!.rewardStars} звёзд",
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
                                                text = "награда за квест",
                                                color = White.copy(alpha = 0.8f),
                                                fontSize = 14.sp
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Прогресс: $currentProgress/${questPreview!!.stepsCount}",
                                            color = White.copy(alpha = 0.8f),
                                            fontSize = 12.sp
                                        )
                                        Text(
                                            text = questPreview!!.districtName ?: "Все районы",
                                            color = White.copy(alpha = 0.8f),
                                            fontSize = 12.sp
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(6.dp)
                                            .clip(RoundedCornerShape(3.dp))
                                            .background(White.copy(alpha = 0.3f))
                                    ) {
                                        val progressFraction = if (questPreview!!.stepsCount > 0)
                                            currentProgress.toFloat() / questPreview!!.stepsCount
                                        else 0f
                                        Box(
                                            modifier = Modifier
                                                .fillMaxHeight()
                                                .fillMaxWidth(fraction = progressFraction)
                                                .background(
                                                    color = StarBlue,
                                                    shape = RoundedCornerShape(3.dp)
                                                )
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

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
                                    .padding(16.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "Описание",
                                        color = White,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        style = TextStyle(
                                            shadow = Shadow(
                                                color = ShadowColor,
                                                offset = SHADOW_OFFSET,
                                                blurRadius = SHADOW_BLUR_RADIUS
                                            )
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = questPreview!!.description,
                                        color = OtherLightGray,
                                        fontSize = 14.sp,
                                        lineHeight = 20.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

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
                                    .padding(16.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "Детали",
                                        color = White,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        style = TextStyle(
                                            shadow = Shadow(
                                                color = ShadowColor,
                                                offset = SHADOW_OFFSET,
                                                blurRadius = SHADOW_BLUR_RADIUS
                                            )
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    QuestDetailRow("Тип квеста", questPreview!!.questType)
                                    QuestDetailRow("Район", questPreview!!.districtName ?: "Любой")
                                    QuestDetailRow("Количество шагов", questPreview!!.stepsCount.toString())
                                }
                            }

                            questPreview!!.rewardCat?.let { cat ->
                                Spacer(modifier = Modifier.height(20.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            color = Gray,
                                            shape = RoundedCornerShape(15.dp)
                                        )
                                        .border(
                                            width = 1.dp,
                                            color = SuccessColor.copy(alpha = 0.5f),
                                            shape = RoundedCornerShape(15.dp)
                                        )
                                        .padding(16.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(48.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            val fullImageUrl = cat.imageUrl?.let {
                                                if (it.startsWith("http")) it else baseUrl + it.removePrefix("/")
                                            }
                                            if (!fullImageUrl.isNullOrBlank()) {
                                                AsyncImage(
                                                    model = ImageRequest.Builder(context)
                                                        .data(fullImageUrl)
                                                        .crossfade(true)
                                                        .build(),
                                                    contentDescription = null,
                                                    modifier = Modifier
                                                        .size(48.dp),
                                                    error = painterResource(id = R.drawable.avatar),
                                                    placeholder = painterResource(id = R.drawable.avatar)
                                                )
                                            } else {
                                                Icon(
                                                    painter = painterResource(id = R.drawable.avatar),
                                                    contentDescription = null,
                                                    tint = Pink,
                                                    modifier = Modifier.size(48.dp)
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            cat.name?.let {
                                                Text(
                                                    text = it,
                                                    color = White,
                                                    fontSize = 18.sp,
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
                                            Text(
                                                text = "Редкость: ${cat.rarity}",
                                                color = OtherLightGray,
                                                fontSize = 12.sp
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(30.dp))

                            Column(
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                when {
                                    questPreview!!.userStatus == "completed" -> {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(15.dp))
                                                .background(SuccessColor)
                                                .padding(vertical = 16.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "Квест завершён!",
                                                color = OtherLightGray,
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                    acceptResult is AcceptQuestResult.Success -> {
                                        ButtonQuest1(
                                            text = "Начать выполнение",
                                            onClick = { onNavigateToQuestStep(questId, 1) }
                                        )
                                    }
                                    questPreview!!.isAccepted -> {
                                        val isProgressLoading by viewModel.isProgressLoading.collectAsStateWithLifecycle()
                                        val currentProgress by viewModel.currentProgress.collectAsStateWithLifecycle()

                                        if (isProgressLoading) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clip(RoundedCornerShape(15.dp))
                                                    .background(
                                                        brush = Brush.verticalGradient(
                                                            colors = listOf(GradientTopLight, GradientBottomLight)
                                                        )
                                                    )
                                                    .padding(vertical = 16.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                CircularProgressIndicator(
                                                    color = White,
                                                    strokeWidth = 2.dp,
                                                    modifier = Modifier.size(24.dp)
                                                )
                                            }
                                        } else {
                                            val nextStep = currentProgress + 1
                                            val buttonText = when (questPreview!!.userStatus) {
                                                "in_progress" -> "Продолжить"
                                                else -> "Начать"
                                            }
                                            ButtonQuest1(
                                                text = buttonText,
                                                onClick = { onNavigateToQuestStep(questId, nextStep) }
                                            )
                                        }
                                    }
                                    questPreview!!.status == "active" -> {
                                        ButtonQuest1(
                                            text = "Принять квест",
                                            onClick = { viewModel.acceptQuest(userId, token, questId) },
                                            isLoading = isLoading
                                        )
                                    }
                                    else -> {
                                        ButtonQuest(
                                            text = when (questPreview!!.status) {
                                                "upcoming" -> "Квест скоро будет доступен"
                                                "expired" -> "Квест завершён"
                                                else -> "Квест недоступен"
                                            },
                                            color = OtherLightGray.copy(alpha = 0.5f),
                                            onClick = { },
                                            enabled = false
                                        )
                                    }
                                }

                                if (acceptResult is AcceptQuestResult.Error) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Color(0xFFF44336).copy(alpha = 0.2f))
                                            .padding(12.dp)
                                    ) {
                                        Text(
                                            text = (acceptResult as AcceptQuestResult.Error).message,
                                            color = Color(0xFFF44336),
                                            fontSize = 14.sp,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(15.dp))
                                        .background(OtherLightGray.copy(alpha = 0.2f))
                                        .clickable { onNavigateBack() }
                                        .padding(vertical = 14.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Вернуться назад",
                                        color = OtherLightGray,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(40.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuestDetailRow(title: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            color = OtherLightGray,
            fontSize = 14.sp
        )
        Text(
            text = value,
            color = White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun ButtonQuest(
    text: String,
    color: Color,
    onClick: () -> Unit,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(15.dp))
            .background(if (enabled) color else color.copy(alpha = 0.5f))
            .clickable(enabled = enabled && !isLoading) { onClick() }
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(color = White, strokeWidth = 2.dp, modifier = Modifier.size(24.dp))
        } else {
            Text(
                text = text,
                color = White,
                fontSize = 18.sp,
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
    }
}

@Composable
fun ButtonQuest1(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(15.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(GradientTopLight, GradientBottomLight),
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY
                )
            )
            .clickable(enabled = enabled && !isLoading) { onClick() }
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(color = White, strokeWidth = 2.dp, modifier = Modifier.size(24.dp))
        } else {
            Text(
                text = text,
                color = White,
                fontSize = 18.sp,
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
    }
}