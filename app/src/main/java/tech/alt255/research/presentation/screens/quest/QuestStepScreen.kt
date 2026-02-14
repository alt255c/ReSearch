package tech.alt255.research.presentation.screens.quest

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
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
import tech.alt255.research.presentation.viewmodels.quest.QuestStepViewModel
import tech.alt255.research.presentation.viewmodels.quest.SubmitResult

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

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun QuestStepScreen(
    questId: Int,
    stepNumber: Int,
    userId: Int,
    token: String,
    onNavigateBack: () -> Unit,
    onStepCompleted: (isFinalStep: Boolean, reward: tech.alt255.research.data.model.quest.StepReward?) -> Unit,
    viewModel: QuestStepViewModel = hiltViewModel()
) {
    val questStep by viewModel.questStep.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    val submitResult by viewModel.submitResult.collectAsStateWithLifecycle()

    var answer by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(questId, stepNumber, userId, token) {
        viewModel.loadQuestStep(userId, token, questId, stepNumber)
    }

    LaunchedEffect(submitResult) {
        if (submitResult is SubmitResult.Success) {
            val result = submitResult as SubmitResult.Success
            if (!result.isFinalStep) {
                delay(2000)
                onStepCompleted(false, result.reward)
            }
        }
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
                                        text = "Шаг $stepNumber",
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
                                    questStep?.let {
                                        Text(
                                            text = it.taskDescription.take(50) + "...",
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
                                text = "Ошибка",
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

                            if (errorMessage == "Этот шаг уже выполнен") {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(15.dp))
                                        .background(
                                            brush = Brush.verticalGradient(
                                                colors = listOf(GradientTopLight, GradientBottomLight)
                                            )
                                        )
                                        .clickable {
                                            onStepCompleted(false, null)
                                        }
                                        .padding(vertical = 16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Перейти к следующему шагу",
                                        color = White,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Pink)
                                    .clickable {
                                        viewModel.clearError()
                                        viewModel.loadQuestStep(userId, token, questId, stepNumber)
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
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
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
                    }
                    questStep == null -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Pink)
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
                                    .height(80.dp)
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
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
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
                                            text = "${questStep!!.points} очков",
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
                                            text = "за выполнение шага",
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
                                    Spacer(modifier = Modifier.weight(1f))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(
                                                when (questStep!!.userStatus) {
                                                    "completed" -> Color(0xFF4CAF50)
                                                    "in_progress" -> StarBlue
                                                    else -> OtherLightGray
                                                }
                                            )
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = when (questStep!!.userStatus) {
                                                "completed" -> "Завершён"
                                                "in_progress" -> "В процессе"
                                                else -> "Не начат"
                                            },
                                            color = White,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium
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
                                        text = "Задание",
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
                                        text = questStep!!.taskDescription,
                                        color = OtherLightGray,
                                        fontSize = 14.sp,
                                        lineHeight = 20.sp
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Тип задания:",
                                            color = OtherLightGray,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = questStep!!.taskType,
                                            color = White,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            Text(
                                text = "Ваш ответ",
                                color = White,
                                fontSize = 16.sp,
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

                            BasicTextField(
                                value = answer,
                                onValueChange = { answer = it },
                                textStyle = TextStyle(
                                    color = White,
                                    fontSize = 16.sp
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Gray)
                                    .border(
                                        width = 1.dp,
                                        color = OtherLightGray.copy(alpha = 0.3f),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .padding(16.dp)
                                    .focusRequester(focusRequester),
                                decorationBox = { innerTextField ->
                                    Box(modifier = Modifier.fillMaxSize()) {
                                        if (answer.isEmpty()) {
                                            Text(
                                                text = "Введите ваш ответ...",
                                                color = OtherLightGray.copy(alpha = 0.5f),
                                                fontSize = 16.sp
                                            )
                                        }
                                        innerTextField()
                                    }
                                }
                            )

                            Spacer(modifier = Modifier.height(20.dp))

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
                                    .clickable(
                                        enabled = answer.isNotBlank() && !isLoading && submitResult == null
                                    ) {
                                        viewModel.submitStep(userId, token, questId, stepNumber, answer)
                                    }
                                    .padding(vertical = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isLoading) {
                                    CircularProgressIndicator(
                                        color = White,
                                        strokeWidth = 2.dp,
                                        modifier = Modifier.size(24.dp)
                                    )
                                } else {
                                    Text(
                                        text = if (submitResult is SubmitResult.Success) {
                                            if ((submitResult as SubmitResult.Success).isFinalStep) "Квест завершён!"
                                            else "Ответ отправлен!"
                                        } else "Отправить ответ",
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

                            submitResult?.let { result ->
                                when (result) {
                                    is SubmitResult.Success -> {
                                        Spacer(modifier = Modifier.height(16.dp))
                                        androidx.compose.animation.AnimatedVisibility(
                                            visible = true,
                                            enter = androidx.compose.animation.fadeIn() +
                                                    androidx.compose.animation.scaleIn(initialScale = 0.8f),
                                            exit = androidx.compose.animation.fadeOut()
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clip(RoundedCornerShape(15.dp))
                                                    .background(Color(0xFF4CAF50).copy(alpha = 0.2f))
                                                    .padding(16.dp)
                                            ) {
                                                Column {
                                                    Text(
                                                        text = "Успешно!",
                                                        color = Color(0xFF4CAF50),
                                                        fontSize = 18.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                    if (result.isFinalStep) {
                                                        Spacer(modifier = Modifier.height(8.dp))
                                                        Text(
                                                            text = "Поздравляем! Вы завершили квест!",
                                                            color = White,
                                                            fontSize = 16.sp
                                                        )
                                                        result.reward?.let { reward ->
                                                            Spacer(modifier = Modifier.height(12.dp))
                                                            Text(
                                                                text = "Награды:",
                                                                color = OtherLightGray,
                                                                fontSize = 14.sp
                                                            )
                                                            Row(
                                                                verticalAlignment = Alignment.CenterVertically,
                                                                modifier = Modifier.padding(top = 4.dp)
                                                            ) {
                                                                androidx.compose.animation.AnimatedContent(
                                                                    targetState = reward.starsEarned,
                                                                    transitionSpec = {
                                                                        androidx.compose.animation.fadeIn() togetherWith
                                                                                androidx.compose.animation.fadeOut()
                                                                    },
                                                                    label = "stars_animation"
                                                                ) { stars ->
                                                                    Row {
                                                                        Icon(
                                                                            painter = painterResource(id = R.drawable.star4),
                                                                            contentDescription = null,
                                                                            tint = StarBlue,
                                                                            modifier = Modifier.size(16.dp)
                                                                        )
                                                                        Spacer(modifier = Modifier.width(8.dp))
                                                                        Text(
                                                                            text = "$stars звёзд",
                                                                            color = StarBlue,
                                                                            fontSize = 16.sp,
                                                                            fontWeight = FontWeight.Bold
                                                                        )
                                                                    }
                                                                }
                                                            }
                                                            if (reward.catReward != null) {
                                                                val cat = reward.catReward
                                                                if (cat.id != null && cat.name != null) {
                                                                    Spacer(modifier = Modifier.height(8.dp))
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
                                                                                    modifier = Modifier.size(48.dp)
                                                                                )
                                                                            }
                                                                        }
                                                                        Spacer(modifier = Modifier.width(12.dp))
                                                                        Column {
                                                                            Text(
                                                                                text = cat.name,
                                                                                color = White,
                                                                                fontSize = 16.sp,
                                                                                fontWeight = FontWeight.Bold
                                                                            )
                                                                            Text(
                                                                                text = "Редкость: ${cat.rarity}",
                                                                                color = OtherLightGray,
                                                                                fontSize = 12.sp
                                                                            )
                                                                        }
                                                                    }
                                                                } else {
                                                                    val compensation = try {
                                                                        val jsonElement = com.google.gson.JsonParser.parseString(
                                                                            com.google.gson.Gson().toJson(cat)
                                                                        ).asJsonObject
                                                                        if (jsonElement.has("compensation")) {
                                                                            jsonElement.get("compensation").asInt
                                                                        } else 0
                                                                    } catch (e: Exception) {
                                                                        0
                                                                    }
                                                                    if (compensation > 0) {
                                                                        Spacer(modifier = Modifier.height(8.dp))
                                                                        Box(
                                                                            modifier = Modifier
                                                                                .fillMaxWidth()
                                                                                .clip(RoundedCornerShape(10.dp))
                                                                                .background(Pink.copy(alpha = 0.2f))
                                                                                .padding(12.dp)
                                                                        ) {
                                                                            Text(
                                                                                text = "У вас уже есть этот котик! Начислена компенсация: +$compensation звёзд",
                                                                                color = Pink,
                                                                                fontSize = 14.sp,
                                                                                textAlign = TextAlign.Center
                                                                            )
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    } else {
                                                        Spacer(modifier = Modifier.height(8.dp))
                                                        Text(
                                                            text = "Ответ принят! Переходите к следующему шагу.",
                                                            color = White,
                                                            fontSize = 16.sp
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    is SubmitResult.Error -> {
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(15.dp))
                                                .background(Color(0xFFF44336).copy(alpha = 0.2f))
                                                .padding(16.dp)
                                        ) {
                                            Text(
                                                text = result.message,
                                                color = Color(0xFFF44336),
                                                fontSize = 16.sp,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
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
                                                .clickable {
                                                    viewModel.clearSubmitResult()
                                                    answer = ""
                                                    focusRequester.requestFocus()
                                                }
                                                .padding(vertical = 16.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "Попробовать снова",
                                                color = White,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
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

                            Spacer(modifier = Modifier.height(40.dp))
                        }
                    }
                }
            }
        }
    }
}