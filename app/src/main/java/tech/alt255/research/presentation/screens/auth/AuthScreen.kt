package tech.alt255.research.presentation.screens.auth

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tech.alt255.research.R
import tech.alt255.research.presentation.ui.theme.ReSearchTheme
import tech.alt255.research.presentation.ui.theme.Blue
import tech.alt255.research.presentation.ui.theme.DarkGray
import tech.alt255.research.presentation.ui.theme.ErrorColor
import tech.alt255.research.presentation.ui.theme.GradientBottom
import tech.alt255.research.presentation.ui.theme.GradientTop
import tech.alt255.research.presentation.ui.theme.Gray
import tech.alt255.research.presentation.ui.theme.LightGray
import tech.alt255.research.presentation.ui.theme.MegaLightGray
import tech.alt255.research.presentation.ui.theme.OtherLightGray
import tech.alt255.research.presentation.ui.theme.Pink
import tech.alt255.research.presentation.ui.theme.RevardFontFamily
import tech.alt255.research.presentation.ui.theme.ShadowColor
import tech.alt255.research.presentation.ui.theme.SuccessColor
import tech.alt255.research.presentation.ui.theme.White
import tech.alt255.research.presentation.viewmodels.auth.AuthViewModel
import tech.alt255.research.presentation.viewmodels.auth.AuthUiState

private const val SHADOW_BLUR_RADIUS = 12f
private val SHADOW_OFFSET = Offset(0f, 0f)

@Composable
fun AuthScreen(
    onLoginSuccess: (userId: Int, email: String) -> Unit,
    onNavigateToHome: (userId: Int, email: String) -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val emailState by viewModel.email.collectAsStateWithLifecycle()
    val codeVerificationState by viewModel.codeVerificationState.collectAsStateWithLifecycle()

    var authMode by remember { mutableStateOf(AuthMode.SIGN_IN) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var verificationCode by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(viewModel) {
        viewModel.navigationEvents.collect { event ->
            when (event) {
                is AuthViewModel.NavigationEvent.ToCodeVerificationScreen -> {
                    log("AuthScreen: NAVIGATING TO CODE VERIFICATION SCREEN VIA EVENT")
                    authMode = AuthMode.CODE_VERIFICATION
                    email = event.email
                }
                is AuthViewModel.NavigationEvent.ToHomeScreen -> {
                    log("AuthScreen: Navigating to home screen with userId=${event.userId}, email=${event.email}")
                    onNavigateToHome(event.userId, event.email)
                }
                is AuthViewModel.NavigationEvent.ToSignInScreen -> {
                    authMode = AuthMode.SIGN_IN
                }
                is AuthViewModel.NavigationEvent.ToSignUpScreen -> {
                    authMode = AuthMode.SIGN_UP
                }
            }
        }
    }

    LaunchedEffect(uiState) {
        log("AuthScreen: Processing uiState = $uiState")

        when (val state = uiState) {
            is AuthUiState.LoginSuccess -> {
                log("AuthScreen: LoginSuccess, navigating to main screen")
                onLoginSuccess(state.userId, state.email)
            }
            is AuthUiState.NeedVerification -> {
                log("AuthScreen: NeedVerification received, email=${state.email}")
                email = state.email
                log("AuthScreen: Set local email to ${state.email}")
            }
            is AuthUiState.Success -> {
                log("AuthScreen: Success - ${state.message}")
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = state.message,
                        withDismissAction = true
                    )
                }
            }
            is AuthUiState.Error -> {
                log("AuthScreen: Error - ${state.message}")
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = state.message,
                        withDismissAction = true
                    )
                }
            }
            else -> {}
        }
    }

    LaunchedEffect(authMode) {
        log("AuthScreen: authMode effect triggered, authMode=$authMode")

        if (uiState !is AuthUiState.NeedVerification) {
            log("AuthScreen: Resetting viewModel state")
            viewModel.resetState()
        }

        when (authMode) {
            AuthMode.SIGN_IN, AuthMode.SIGN_UP -> {
                log("AuthScreen: Clearing input fields for SIGN_IN/SIGN_UP")
                email = ""
                password = ""
                confirmPassword = ""
            }
            AuthMode.CODE_VERIFICATION -> {
                log("AuthScreen: Clearing verification code field")
                verificationCode = ""
                viewModel.resetCodeError()
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
        BackroundLayer {
            Column {
                TopContent(authMode = authMode)
                AuthSwitch(
                    selectedMode = authMode,
                    onAuthModeChanged = { newMode ->
                        log("AuthScreen: Manual auth mode change from $authMode to $newMode")
                        authMode = newMode
                        viewModel.resetState()
                        email = ""
                        password = ""
                        confirmPassword = ""
                        verificationCode = ""
                    },
                    email = email,
                    onEmailChange = { email = it },
                    password = password,
                    onPasswordChange = { password = it },
                    confirmPassword = confirmPassword,
                    onConfirmPasswordChange = { confirmPassword = it },
                    verificationCode = verificationCode,
                    onVerificationCodeChange = { verificationCode = it },
                    isLoading = isLoading,
                    onSignInClick = {
                        log("AuthScreen: Sign in clicked with email=$email")
                        viewModel.login(email, password)
                    },
                    onSignUpStep1Click = {
                        log("AuthScreen: Sign up step1 clicked with email=$email")
                        viewModel.registerStep1(email, password)
                    },
                    onVerifyCodeClick = {
                        log("AuthScreen: Verify code clicked with code=$verificationCode")
                        when (codeVerificationState.type) {
                            is AuthViewModel.CodeVerificationType.Registration -> {
                                viewModel.registerStep2(emailState, verificationCode)
                            }
                            is AuthViewModel.CodeVerificationType.PasswordReset -> {
                                viewModel.resetPassword(codeVerificationState.email, verificationCode)
                            }
                        }
                    },
                    onForgotPasswordClick = {
                        log("AuthScreen: Forgot password clicked")
                        viewModel.forgotPassword(email)
                    },
                    viewModel = viewModel,
                    codeVerificationState = codeVerificationState
                )
            }
        }
    }
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
fun BuildsBackround(tint: Color, modifier: Modifier) {
    Row(
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.Bottom)
                .size(width = 20.dp, height = 50.dp)
                .background(tint)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Box(
            modifier = Modifier
                .align(Alignment.Bottom)
                .size(width = 35.dp, height = 90.dp)
                .background(tint)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Box(
            modifier = Modifier
                .align(Alignment.Bottom)
                .size(width = 35.dp, height = 80.dp)
                .background(tint)
        )
        Box(
            modifier = Modifier
                .align(Alignment.Bottom)
                .size(width = 35.dp, height = 90.dp)
                .background(tint)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Box(
            modifier = Modifier
                .align(Alignment.Bottom)
                .size(width = 35.dp, height = 80.dp)
                .background(tint)
        )
        Box(
            modifier = Modifier
                .align(Alignment.Bottom)
                .size(width = 35.dp, height = 65.dp)
                .background(tint)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Box(
            modifier = Modifier
                .align(Alignment.Bottom)
                .size(width = 35.dp, height = 75.dp)
                .background(tint)
        )
        Box(
            modifier = Modifier
                .align(Alignment.Bottom)
                .size(width = 35.dp, height = 90.dp)
                .background(tint)
        )
        Box(
            modifier = Modifier
                .align(Alignment.Bottom)
                .size(width = 35.dp, height = 75.dp)
                .background(tint)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Box(
            modifier = Modifier
                .align(Alignment.Bottom)
                .size(width = 80.dp, height = 55.dp)
                .background(tint)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Box(
            modifier = Modifier
                .align(Alignment.Bottom)
                .size(width = 35.dp, height = 90.dp)
                .background(tint)
        )
    }
}

@Composable
fun BuildsForeground(tint: Color, authMode: AuthMode, modifier: Modifier) {
    val offsetX by animateDpAsState(
        targetValue = when (authMode) {
            AuthMode.SIGN_IN -> (-60).dp
            AuthMode.SIGN_UP, AuthMode.CODE_VERIFICATION -> (-5).dp
        },
        label = ""
    )
    Row(
        modifier = modifier.offset(x = offsetX)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.Bottom)
                .size(width = 50.dp, height = 115.dp)
                .background(tint)
        )
        Box(
            modifier = Modifier
                .align(Alignment.Bottom)
                .size(width = 40.dp, height = 95.dp)
                .background(tint)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Box(
            modifier = Modifier
                .align(Alignment.Bottom)
                .size(width = 100.dp, height = 60.dp)
                .background(tint)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Box(
            modifier = Modifier
                .align(Alignment.Bottom)
                .size(width = 40.dp, height = 115.dp)
                .background(tint)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Box(
            modifier = Modifier
                .align(Alignment.Bottom)
                .size(width = 40.dp, height = 95.dp)
                .background(tint)
        )
        Box(
            modifier = Modifier
                .align(Alignment.Bottom)
                .size(width = 40.dp, height = 115.dp)
                .background(tint)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Box(
            modifier = Modifier
                .align(Alignment.Bottom)
                .size(width = 40.dp, height = 95.dp)
                .background(tint)
        )
        Box(
            modifier = Modifier
                .align(Alignment.Bottom)
                .size(width = 40.dp, height = 75.dp)
                .background(tint)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Box(
            modifier = Modifier
                .align(Alignment.Bottom)
                .size(width = 100.dp, height = 60.dp)
                .background(tint)
        )
    }
}

@Composable
fun TopContent(authMode: AuthMode) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(310.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(GradientTop, GradientBottom),
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY
                )
            )
    ) {
        Logo(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(bottom = 140.dp)
        )
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Star(
                tint = Pink,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .absolutePadding(top = 70.dp, right = 10.dp)
                    .size(10.dp)
            )
            Star(
                tint = White,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .absolutePadding(top = 60.dp, right = 70.dp)
                    .size(8.dp)
            )
            Star(
                tint = White,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .absolutePadding(top = 140.dp, right = 25.dp)
                    .size(6.dp)
            )
            Star(
                tint = White,
                modifier = Modifier
                    .absolutePadding(top = 150.dp, left = 10.dp)
                    .size(10.dp)
            )
        }
        BuildsBackround(
            DarkGray,
            modifier = Modifier
                .align(Alignment.BottomStart)
        )
        BuildsForeground(
            LightGray,
            authMode = authMode,
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .align(Alignment.BottomStart)
        )
    }
}

@Composable
fun CustomTextField(
    modifier: Modifier,
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    isError: Boolean = false,
    errorMessage: String = ""
) {
    Column {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = White,
                unfocusedTextColor = White,
                focusedLabelColor = OtherLightGray,
                unfocusedLabelColor = OtherLightGray,
                focusedContainerColor = Gray,
                unfocusedContainerColor = Gray,
                focusedBorderColor = if (isError) ErrorColor else White,
                unfocusedBorderColor = if (isError) ErrorColor else MegaLightGray,
                cursorColor = White
            ),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            visualTransformation = visualTransformation,
            isError = isError,
            modifier = modifier.fillMaxWidth()
        )
        if (isError && errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = ErrorColor,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 4.dp, top = 2.dp)
            )
        }
    }
}

@Composable
fun CodeVerificationField(
    code: String,
    onCodeChange: (String) -> Unit,
    verifiedDigits: List<Int> = emptyList(),
    isError: Boolean = false,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    var isFocused by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100)
        focusRequester.requestFocus()
    }

    BasicTextField(
        value = code,
        singleLine = true,
        onValueChange = { newValue ->
            if (newValue.length <= 5 && newValue.all { it.isDigit() }) {
                onCodeChange(newValue)
                if (newValue.length == 5) {
                    keyboardController?.hide()
                }
            }
        },
        enabled = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = modifier
            .fillMaxWidth()
            .focusRequester(focusRequester)
            .onFocusChanged { isFocused = it.isFocused }
            .clickable {
                focusRequester.requestFocus()
            },
        decorationBox = { it ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    repeat(5) { index ->
                        val isVerified = verifiedDigits.contains(index + 1)
                        CharView(
                            index = index,
                            text = code,
                            isFocused = isFocused && index == code.length,
                            isVerified = isVerified,
                            isError = isError,
                            modifier = Modifier
                                .weight(1f)
                                .height(80.dp)
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun CharView(
    index: Int,
    text: String,
    isFocused: Boolean,
    isVerified: Boolean = false,
    isError: Boolean = false,
    modifier: Modifier = Modifier
) {
    val char = if (index < text.length) text[index].toString() else ""
    val isFilled = index < text.length

    val borderColor = when {
        isVerified -> SuccessColor
        isError -> ErrorColor
        isFocused -> White
        isFilled -> White
        else -> MegaLightGray
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(
                color = Gray,
                shape = RoundedCornerShape(10.dp)
            )
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(10.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = char,
            color = if (isVerified) SuccessColor else White,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun CustomGradientButton(
    text: String,
    onClick: () -> Unit,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = { if (!isLoading && enabled) onClick() },
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(14.dp),
        color = Color.Transparent,
        enabled = enabled && !isLoading
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(GradientTop, GradientBottom)
                    ),
                    shape = RoundedCornerShape(14.dp)
                )
                .padding(horizontal = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = White,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = text,
                    color = White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

fun isValidEmail(email: String): Boolean {
    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$"
    return email.matches(emailRegex.toRegex())
}

@Composable
fun AuthSwitch(
    selectedMode: AuthMode,
    onAuthModeChanged: (AuthMode) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    confirmPassword: String,
    onConfirmPasswordChange: (String) -> Unit,
    verificationCode: String,
    onVerificationCodeChange: (String) -> Unit,
    isLoading: Boolean,
    onSignInClick: () -> Unit,
    onSignUpStep1Click: () -> Unit,
    onVerifyCodeClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    viewModel: AuthViewModel,
    codeVerificationState: AuthViewModel.CodeVerificationState
) {
    val emailError by viewModel.emailError.collectAsStateWithLifecycle()
    val passwordError by viewModel.passwordError.collectAsStateWithLifecycle()
    val confirmPasswordError by viewModel.confirmPasswordError.collectAsStateWithLifecycle()
    val loginError by viewModel.loginError.collectAsStateWithLifecycle()
    val codeError by viewModel.codeError.collectAsStateWithLifecycle()

    var localEmailError by remember { mutableStateOf(false) }
    var localPasswordError by remember { mutableStateOf(false) }
    var localConfirmPasswordError by remember { mutableStateOf(false) }
    var localEmailErrorMessage by remember { mutableStateOf("") }
    var localPasswordErrorMessage by remember { mutableStateOf("") }
    var localConfirmPasswordErrorMessage by remember { mutableStateOf("") }

    LaunchedEffect(selectedMode) {
        localEmailError = false
        localPasswordError = false
        localConfirmPasswordError = false
        localEmailErrorMessage = ""
        localPasswordErrorMessage = ""
        localConfirmPasswordErrorMessage = ""
    }

    LaunchedEffect(emailError, passwordError, confirmPasswordError, loginError) {
        when (selectedMode) {
            AuthMode.SIGN_IN -> {
                emailError?.let {
                    localEmailError = true
                    localEmailErrorMessage = it
                }
                passwordError?.let {
                    localPasswordError = true
                    localPasswordErrorMessage = it
                }
                loginError?.let {
                    localEmailError = true
                    localPasswordError = true
                    localEmailErrorMessage = it
                    localPasswordErrorMessage = it
                }
            }
            AuthMode.SIGN_UP -> {
                emailError?.let {
                    localEmailError = true
                    localEmailErrorMessage = it
                }
                passwordError?.let {
                    localPasswordError = true
                    localPasswordErrorMessage = it
                }
                confirmPasswordError?.let {
                    localConfirmPasswordError = true
                    localConfirmPasswordErrorMessage = it
                }
            }
            else -> {}
        }
    }

    LaunchedEffect(verificationCode) {
        if (verificationCode.length == 5 &&
            !isLoading &&
            !codeVerificationState.isVerifying &&
            codeError == null &&
            selectedMode == AuthMode.CODE_VERIFICATION) {

            log("AuthSwitch: Auto-verifying code with length 5")
            onVerifyCodeClick()
        }
    }

    LaunchedEffect(codeError) {
        if (codeError != null && selectedMode == AuthMode.CODE_VERIFICATION) {
            kotlinx.coroutines.delay(1000)
            onVerificationCodeChange("")
        }
    }

    fun validateSignUp(): Boolean {
        var isValid = true

        if (!isValidEmail(email)) {
            localEmailError = true
            localEmailErrorMessage = "Введите корректный email"
            isValid = false
        } else {
            localEmailError = false
            localEmailErrorMessage = ""
        }

        if (password.length <= 6) {
            localPasswordError = true
            localPasswordErrorMessage = "Пароль должен быть больше 6 символов"
            isValid = false
        } else {
            localPasswordError = false
            localPasswordErrorMessage = ""
        }

        if (password != confirmPassword) {
            localConfirmPasswordError = true
            localConfirmPasswordErrorMessage = "Пароли не совпадают"
            isValid = false
        } else {
            localConfirmPasswordError = false
            localConfirmPasswordErrorMessage = ""
        }

        return isValid
    }

    fun validateSignIn(): Boolean {
        var isValid = true

        if (email.isBlank()) {
            localEmailError = true
            localEmailErrorMessage = "Введите почту"
            isValid = false
        } else {
            localEmailError = false
            localEmailErrorMessage = ""
        }

        if (password.isBlank()) {
            localPasswordError = true
            localPasswordErrorMessage = "Введите пароль"
            isValid = false
        } else {
            localPasswordError = false
            localPasswordErrorMessage = ""
        }

        return isValid
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(topStartPercent = 7, topEndPercent = 7))
            .background(Gray)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (selectedMode) {
                AuthMode.SIGN_IN, AuthMode.SIGN_UP -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(15.dp)
                            .height(height = 40.dp)
                            .clip(RoundedCornerShape(percent = 20))
                            .background(LightGray)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(percent = 20))
                                    .background(
                                        if (selectedMode == AuthMode.SIGN_IN)
                                            Gray.copy(alpha = 1.0f)
                                        else
                                            Gray.copy(alpha = 0.0f)
                                    )
                                    .clickable { onAuthModeChanged(AuthMode.SIGN_IN) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    stringResource(R.string.sign_in),
                                    color = White,
                                    fontWeight = if (selectedMode == AuthMode.SIGN_IN) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(percent = 20))
                                    .background(
                                        if (selectedMode == AuthMode.SIGN_UP)
                                            Gray.copy(alpha = 1.0f)
                                        else
                                            Gray.copy(alpha = 0.0f)
                                    )
                                    .clickable { onAuthModeChanged(AuthMode.SIGN_UP) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    stringResource(R.string.sign_up),
                                    color = White,
                                    fontWeight = if (selectedMode == AuthMode.SIGN_UP) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
                AuthMode.CODE_VERIFICATION -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(15.dp)
                            .height(height = 40.dp)
                            .clip(RoundedCornerShape(percent = 20))
                            .background(LightGray),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            stringResource(R.string.verification),
                            color = White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            when (selectedMode) {
                AuthMode.SIGN_IN -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 15.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.email),
                            color = OtherLightGray,
                            style = TextStyle(
                                fontSize = 16.sp
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                        CustomTextField(
                            label = "",
                            value = email,
                            onValueChange = {
                                onEmailChange(it)
                                localEmailError = false
                                localEmailErrorMessage = ""
                            },
                            keyboardType = KeyboardType.Email,
                            isError = localEmailError,
                            errorMessage = localEmailErrorMessage,
                            modifier = Modifier
                                .offset(y = (-8).dp)
                        )
                        Text(
                            text = stringResource(R.string.password),
                            color = OtherLightGray,
                            style = TextStyle(
                                fontSize = 16.sp
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                        CustomTextField(
                            label = "",
                            value = password,
                            onValueChange = {
                                onPasswordChange(it)
                                localPasswordError = false
                                localPasswordErrorMessage = ""
                            },
                            keyboardType = KeyboardType.Password,
                            visualTransformation = PasswordVisualTransformation(),
                            isError = localPasswordError,
                            errorMessage = localPasswordErrorMessage,
                            modifier = Modifier
                                .offset(y = (-8).dp)
                        )
                        Text(
                            text = stringResource(R.string.forgot_password),
                            color = Blue,
                            style = TextStyle(
                                fontSize = 16.sp
                            ),
                            modifier = Modifier
                                .clickable { onForgotPasswordClick() }
                                .align(Alignment.End)
                        )
                        Spacer(modifier = Modifier.height(25.dp))
                        CustomGradientButton(
                            text = stringResource(R.string.login_button),
                            onClick = {
                                if (validateSignIn()) {
                                    onSignInClick()
                                }
                            },
                            isLoading = isLoading,
                            enabled = email.isNotBlank() && password.isNotBlank()
                        )
                    }
                }
                AuthMode.SIGN_UP -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 15.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.email),
                            color = OtherLightGray,
                            style = TextStyle(
                                fontSize = 16.sp
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                        CustomTextField(
                            label = "",
                            value = email,
                            onValueChange = {
                                onEmailChange(it)
                                localEmailError = false
                                localEmailErrorMessage = ""
                            },
                            keyboardType = KeyboardType.Email,
                            isError = localEmailError,
                            errorMessage = localEmailErrorMessage,
                            modifier = Modifier
                                .offset(y = (-8).dp)
                        )
                        Text(
                            text = stringResource(R.string.password),
                            color = OtherLightGray,
                            style = TextStyle(
                                fontSize = 16.sp
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                        CustomTextField(
                            label = "",
                            value = password,
                            onValueChange = {
                                onPasswordChange(it)
                                localPasswordError = false
                                localPasswordErrorMessage = ""
                            },
                            keyboardType = KeyboardType.Password,
                            visualTransformation = PasswordVisualTransformation(),
                            isError = localPasswordError,
                            errorMessage = localPasswordErrorMessage,
                            modifier = Modifier
                                .offset(y = (-8).dp)
                        )
                        Text(
                            text = stringResource(R.string.repeat_password),
                            color = OtherLightGray,
                            style = TextStyle(
                                fontSize = 16.sp
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                        CustomTextField(
                            label = "",
                            value = confirmPassword,
                            onValueChange = {
                                onConfirmPasswordChange(it)
                                localConfirmPasswordError = false
                                localConfirmPasswordErrorMessage = ""
                            },
                            keyboardType = KeyboardType.Password,
                            visualTransformation = PasswordVisualTransformation(),
                            isError = localConfirmPasswordError,
                            errorMessage = localConfirmPasswordErrorMessage,
                            modifier = Modifier
                                .offset(y = (-8).dp)
                        )
                        Spacer(modifier = Modifier.height(25.dp))
                        CustomGradientButton(
                            text = stringResource(R.string.continue_button),
                            onClick = {
                                if (validateSignUp()) {
                                    onSignUpStep1Click()
                                }
                            },
                            isLoading = isLoading,
                            enabled = email.isNotBlank() &&
                                    password.isNotBlank() &&
                                    confirmPassword.isNotBlank()
                        )
                    }
                }
                AuthMode.CODE_VERIFICATION -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 15.dp)
                    ) {
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(
                            text = stringResource(R.string.verification_message1),
                            color = OtherLightGray,
                            style = TextStyle(
                                fontSize = 14.sp
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 20.dp)
                        )
                        Text(
                            text = stringResource(R.string.verification_message2),
                            color = OtherLightGray,
                            style = TextStyle(
                                fontSize = 14.sp
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 20.dp)
                        )
                        CodeVerificationField(
                            code = verificationCode,
                            onCodeChange = { newCode ->
                                onVerificationCodeChange(newCode)
                                if (codeError != null) {
                                    viewModel.resetCodeError()
                                }
                            },
                            verifiedDigits = codeVerificationState.verifiedDigits,
                            isError = codeError != null,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        if (codeError != null) {
                            Text(
                                text = codeError ?: "",
                                color = ErrorColor,
                                fontSize = 14.sp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 20.dp)
                            )
                        }

                        if (codeVerificationState.isVerifying) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    color = White,
                                    strokeWidth = 2.dp
                                )
                            }
                        }
                    }
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.app_version),
                color = White,
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
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

enum class AuthMode {
    SIGN_IN,
    SIGN_UP,
    CODE_VERIFICATION
}

private fun log(message: String) {
    println("AuthScreen: $message")
    android.util.Log.d("AuthScreen", message)
}

@Preview(showSystemUi = true)
@Composable
fun AuthScreenPreview() {
    ReSearchTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(LightGray)
        ) {
            Column {
                TopContent(authMode = AuthMode.SIGN_IN)
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStartPercent = 7, topEndPercent = 7))
                        .background(Gray)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(15.dp)
                            .height(height = 40.dp)
                            .clip(RoundedCornerShape(percent = 20))
                            .background(LightGray)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(percent = 20))
                                    .background(Gray.copy(alpha = 1.0f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "Войти",
                                    color = White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(percent = 20))
                                    .background(Gray.copy(alpha = 0.0f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "Регистрация",
                                    color = White,
                                    fontWeight = FontWeight.Normal
                                )
                            }
                        }
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 15.dp)
                    ) {
                        Text(
                            text = "Почта",
                            color = OtherLightGray,
                            style = TextStyle(fontSize = 16.sp),
                            modifier = Modifier.fillMaxWidth()
                        )
                        CustomTextField(
                            label = "",
                            value = "example@mail.com",
                            onValueChange = {},
                            keyboardType = KeyboardType.Email,
                            modifier = Modifier.offset(y = (-8).dp)
                        )
                        Text(
                            text = "Пароль",
                            color = OtherLightGray,
                            style = TextStyle(fontSize = 16.sp),
                            modifier = Modifier.fillMaxWidth()
                        )
                        CustomTextField(
                            label = "",
                            value = "password",
                            onValueChange = {},
                            keyboardType = KeyboardType.Password,
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.offset(y = (-8).dp)
                        )
                        Text(
                            text = "Забыли пароль?",
                            color = Blue,
                            style = TextStyle(fontSize = 16.sp),
                            modifier = Modifier.align(Alignment.End)
                        )
                        Spacer(modifier = Modifier.height(25.dp))
                        CustomGradientButton(
                            text = "Войти",
                            onClick = {},
                            isLoading = false,
                            enabled = true
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "v1.0.0",
                            color = White,
                            fontFamily = RevardFontFamily,
                            style = TextStyle(
                                fontSize = 18.sp,
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
        }
    }
}

@Preview
@Composable
fun SignUpPreview() {
    ReSearchTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(LightGray)
        ) {
            Column {
                TopContent(authMode = AuthMode.SIGN_UP)
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStartPercent = 7, topEndPercent = 7))
                        .background(Gray)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(15.dp)
                            .height(height = 40.dp)
                            .clip(RoundedCornerShape(percent = 20))
                            .background(LightGray)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(percent = 20))
                                    .background(Gray.copy(alpha = 0.0f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "Войти",
                                    color = White,
                                    fontWeight = FontWeight.Normal
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(percent = 20))
                                    .background(Gray.copy(alpha = 1.0f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "Регистрация",
                                    color = White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 15.dp)
                    ) {
                        Text(
                            text = "Почта",
                            color = OtherLightGray,
                            style = TextStyle(fontSize = 16.sp),
                            modifier = Modifier.fillMaxWidth()
                        )
                        CustomTextField(
                            label = "",
                            value = "example@mail.com",
                            onValueChange = {},
                            keyboardType = KeyboardType.Email,
                            modifier = Modifier.offset(y = (-8).dp)
                        )
                        Text(
                            text = "Пароль",
                            color = OtherLightGray,
                            style = TextStyle(fontSize = 16.sp),
                            modifier = Modifier.fillMaxWidth()
                        )
                        CustomTextField(
                            label = "",
                            value = "password",
                            onValueChange = {},
                            keyboardType = KeyboardType.Password,
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.offset(y = (-8).dp)
                        )
                        Text(
                            text = "Повторите пароль",
                            color = OtherLightGray,
                            style = TextStyle(fontSize = 16.sp),
                            modifier = Modifier.fillMaxWidth()
                        )
                        CustomTextField(
                            label = "",
                            value = "password",
                            onValueChange = {},
                            keyboardType = KeyboardType.Password,
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.offset(y = (-8).dp)
                        )
                        Spacer(modifier = Modifier.height(25.dp))
                        CustomGradientButton(
                            text = "Продолжить",
                            onClick = {},
                            isLoading = false,
                            enabled = true
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "v1.0.0",
                            color = White,
                            fontFamily = RevardFontFamily,
                            style = TextStyle(
                                fontSize = 18.sp,
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
        }
    }
}