package tech.alt255.research.presentation.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.delay
import tech.alt255.research.R
import tech.alt255.research.presentation.ui.theme.*
import tech.alt255.research.presentation.viewmodels.profile.ProfileEditViewModel

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
fun Logo(modifier: Modifier) {
    Row(
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .height(52.dp)
                .width(52.dp)
        ) {
            Star(
                tint = White,
                modifier = Modifier
                    .size(36.dp)
                    .align(Alignment.TopEnd)
                    .absolutePadding(top = 2.dp, right = 2.dp)
            )
            Star(
                tint = White,
                modifier = Modifier
                    .size(16.dp)
                    .align(Alignment.TopStart)
                    .absolutePadding(top = 4.dp, left = 5.dp)
            )
            Star(
                tint = White,
                modifier = Modifier
                    .size(16.dp)
                    .align(Alignment.BottomEnd)
                    .absolutePadding(bottom = 4.dp, right = 5.dp)
            )
            Star(
                tint = Pink,
                modifier = Modifier
                    .size(22.dp)
                    .align(Alignment.BottomStart)
                    .absolutePadding(bottom = 2.dp, left = 2.dp)
            )
        }
        Column(
            modifier = Modifier
                .padding(start = 8.dp, top = 8.dp)
        ) {
            Text(
                text = "alt255",
                color = Pink,
                fontFamily = RevardFontFamily,
                style = TextStyle(
                    fontSize = 18.sp,
                    shadow = Shadow(
                        color = ShadowColor,
                        offset = SHADOW_OFFSET,
                        blurRadius = SHADOW_BLUR_RADIUS
                    )
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            color = Pink,
                            shadow = Shadow(
                                color = ShadowColor,
                                offset = SHADOW_OFFSET,
                                blurRadius = SHADOW_BLUR_RADIUS
                            )
                        )
                    ) {
                        append("Re")
                    }
                    withStyle(
                        style = SpanStyle(
                            color = White,
                            shadow = Shadow(
                                color = ShadowColor,
                                offset = SHADOW_OFFSET,
                                blurRadius = SHADOW_BLUR_RADIUS
                            )
                        )
                    ) {
                        append("Search")
                    }
                },
                fontFamily = RevardFontFamily,
                style = TextStyle(
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@Composable
fun TopContent(
    onNavigateBack: () -> Unit,
    onSave: () -> Unit,
    isSaving: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 16.dp)
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
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

        Logo(
            modifier = Modifier.align(Alignment.Center)
        )

        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp)
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .clickable(enabled = !isSaving) { onSave() },
            contentAlignment = Alignment.Center
        ) {
            if (isSaving) {
                CircularProgressIndicator(
                    color = White,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Icon(
                    painter = painterResource(id = R.drawable.ok),
                    contentDescription = "Сохранить",
                    tint = White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Star(
            tint = Pink,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .absolutePadding(top = 30.dp, right = 10.dp)
                .size(10.dp)
        )
        Star(
            tint = White,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .absolutePadding(top = 20.dp, right = 70.dp)
                .size(8.dp)
        )
        Star(
            tint = White,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .absolutePadding(top = 100.dp, right = 25.dp)
                .size(6.dp)
        )
        Star(
            tint = White,
            modifier = Modifier
                .absolutePadding(top = 100.dp, left = 10.dp)
                .size(10.dp)
        )
    }
}

@Composable
fun EditProfileField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    prefix: String = ""
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Gray)
            .padding(12.dp)
    ) {
        Text(
            text = label,
            color = OtherLightGray,
            fontSize = 12.sp
        )
        Spacer(modifier = Modifier.height(1.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (prefix.isNotEmpty()) {
                Text(
                    text = prefix,
                    color = White,
                    fontSize = 16.sp
                )
            }
            BasicTextField(
                value = if (prefix.isNotEmpty()) value.removePrefix(prefix) else value,
                onValueChange = { newVal ->
                    onValueChange(if (prefix.isNotEmpty()) prefix + newVal else newVal)
                },
                textStyle = TextStyle(
                    color = White,
                    fontSize = 14.sp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                decorationBox = { innerTextField ->
                    if (value.isEmpty() || (prefix.isNotEmpty() && value == prefix)) {
                        Text(
                            text = if (prefix == "") "nickname" else "Имя",
                            color = OtherLightGray.copy(alpha = 0.5f),
                            fontSize = 14.sp
                        )
                    }
                    innerTextField()
                }
            )
        }
    }
}

@Composable
fun EditProfileScreen(
    userId: Int,
    token: String,
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit,
    viewModel: ProfileEditViewModel = hiltViewModel()
) {
    val profileState by viewModel.profileState.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    val updateSuccess by viewModel.updateSuccess.collectAsStateWithLifecycle()

    var editedName by remember { mutableStateOf("") }
    var editedNickname by remember { mutableStateOf("") }
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(userId, token) {
        viewModel.loadProfile(userId, token)
    }

    LaunchedEffect(profileState) {
        editedName = profileState?.userName ?: ""
        editedNickname = profileState?.userNickname ?: ""
    }

    fun performSave() {
        if (newPassword.isNotEmpty() || confirmPassword.isNotEmpty()) {
            if (newPassword != confirmPassword) {
                passwordError = "Пароли не совпадают"
                return
            }
            if (newPassword.length < 6) {
                passwordError = "Пароль должен быть не менее 6 символов"
                return
            }
        }
        passwordError = null

        viewModel.updateProfile(
            userId = userId,
            token = token,
            userName = editedName,
            userNickname = editedNickname,
            currentPassword = currentPassword.takeIf { it.isNotEmpty() },
            newPassword = newPassword.takeIf { it.isNotEmpty() }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LightGray)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            TopContent(
                onNavigateBack = onNavigateBack,
                onSave = ::performSave,
                isSaving = isLoading
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    if (isLoading && profileState == null) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Pink)
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp))
                                .background(LightGray)
                                .border(
                                    width = 1.dp,
                                    color = White.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(20.dp)
                                )
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(20.dp))
                                    .height(120.dp)
                                    .background(
                                        brush = Brush.verticalGradient(
                                            colors = listOf(GradientTopLight, GradientBottomLight),
                                            startY = 0f,
                                            endY = Float.POSITIVE_INFINITY
                                        )
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = White.copy(alpha = 0.5f),
                                        shape = RoundedCornerShape(20.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(80.dp)
                                            .drawWithContent {
                                                drawCircle(MegaLightGray)
                                                drawCircle(
                                                    color = White,
                                                    style = Stroke(width = 1.dp.toPx())
                                                )
                                                drawContent()
                                            }
                                    ) {
                                        val imageUrl = profileState?.userPhoto
                                        val baseUrl = stringResource(R.string.base_url)
                                        val fullImageUrl = imageUrl?.let {
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

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = "Невозможно сменить фото :(",
                                        color = White.copy(alpha = 0.7f),
                                        fontSize = 12.sp,
                                        modifier = Modifier.clickable {
                                        }
                                    )
                                }

                                Star(
                                    tint = Pink,
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .absolutePadding(top = 25.dp, right = 10.dp)
                                        .size(10.dp)
                                )
                                Star(
                                    tint = White,
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .absolutePadding(top = 15.dp, right = 70.dp)
                                        .size(8.dp)
                                )
                                Star(
                                    tint = White,
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .absolutePadding(top = 95.dp, right = 25.dp)
                                        .size(6.dp)
                                )
                                Star(
                                    tint = White,
                                    modifier = Modifier
                                        .align(Alignment.TopStart)
                                        .absolutePadding(top = 95.dp, left = 10.dp)
                                        .size(10.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            Box(
                                modifier = Modifier.padding(horizontal = 16.dp)
                            ) {
                                EditProfileField(
                                    label = "Публичное имя",
                                    value = editedName,
                                    onValueChange = { editedName = it }
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Box(
                                modifier = Modifier.padding(horizontal = 16.dp)
                            ) {
                                EditProfileField(
                                    label = "Публичный никнейм",
                                    value = editedNickname,
                                    onValueChange = { editedNickname = it },
                                    prefix = ""
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Box(
                                modifier = Modifier.padding(horizontal = 16.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Gray)
                                        .padding(12.dp)
                                ) {
                                    Text(
                                        text = "Пароль",
                                        color = OtherLightGray,
                                        fontSize = 12.sp
                                    )
                                    Spacer(modifier = Modifier.height(1.dp))
                                    BasicTextField(
                                        value = currentPassword,
                                        onValueChange = { currentPassword = it },
                                        textStyle = TextStyle(
                                            color = White,
                                            fontSize = 16.sp
                                        ),
                                        visualTransformation = PasswordVisualTransformation(),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        decorationBox = { innerTextField ->
                                            if (currentPassword.isEmpty()) {
                                                Text(
                                                    text = "********",
                                                    color = OtherLightGray.copy(alpha = 0.5f),
                                                    fontSize = 16.sp
                                                )
                                            }
                                            innerTextField()
                                        }
                                    )
                                    Divider(
                                        color = OtherLightGray.copy(alpha = 0.3f),
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    )
                                    BasicTextField(
                                        value = newPassword,
                                        onValueChange = { newPassword = it },
                                        textStyle = TextStyle(
                                            color = White,
                                            fontSize = 16.sp
                                        ),
                                        visualTransformation = PasswordVisualTransformation(),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        decorationBox = { innerTextField ->
                                            if (newPassword.isEmpty()) {
                                                Text(
                                                    text = "Новый пароль",
                                                    color = OtherLightGray.copy(alpha = 0.5f),
                                                    fontSize = 16.sp
                                                )
                                            }
                                            innerTextField()
                                        }
                                    )
                                    Divider(
                                        color = OtherLightGray.copy(alpha = 0.3f),
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    )
                                    BasicTextField(
                                        value = confirmPassword,
                                        onValueChange = { confirmPassword = it },
                                        textStyle = TextStyle(
                                            color = White,
                                            fontSize = 16.sp
                                        ),
                                        visualTransformation = PasswordVisualTransformation(),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        decorationBox = { innerTextField ->
                                            if (confirmPassword.isEmpty()) {
                                                Text(
                                                    text = "Подтвердите пароль",
                                                    color = OtherLightGray.copy(alpha = 0.5f),
                                                    fontSize = 16.sp
                                                )
                                            }
                                            innerTextField()
                                        }
                                    )
                                    if (passwordError != null) {
                                        Text(
                                            text = passwordError!!,
                                            color = Color.Red,
                                            fontSize = 12.sp,
                                            modifier = Modifier.padding(top = 4.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            if (updateSuccess) {
                                LaunchedEffect(Unit) {
                                    delay(2000)
                                    viewModel.clearSuccess()
                                }
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp)
                                        .align(Alignment.CenterHorizontally),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Профиль обновлён!",
                                        color = Color(0xFF4CAF50),
                                        fontSize = 14.sp
                                    )
                                }
                            }

                            if (errorMessage != null) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp)
                                        .align(Alignment.CenterHorizontally),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = errorMessage!!,
                                        color = Color.Red,
                                        fontSize = 14.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "v0.0.1 - MVP",
                                color = White,
                                fontSize = 12.sp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentSize(Alignment.Center)
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Box(
                                modifier = Modifier.padding(horizontal = 16.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(15.dp))
                                        .background(Pink.copy(0.2f))
                                        .clickable { onLogout() }
                                        .padding(vertical = 14.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Выйти из аккаунта",
                                        color = Pink,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(30.dp))
                        }
                    }
                }
            }
        }
    }
}