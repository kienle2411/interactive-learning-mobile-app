package com.se122.interactivelearning.ui.screens.auth.recovery_password

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.se122.interactivelearning.data.remote.api.ApiResult
import com.se122.interactivelearning.ui.components.PrimaryButton
import com.se122.interactivelearning.ui.screens.auth.register.InputIcon
import java.util.regex.Pattern

@Composable
fun RecoveryPasswordScreen(
    viewModel: RecoveryPasswordViewModel = hiltViewModel(),
    onBackToLogin: () -> Unit,
) {
    val context = LocalContext.current
    val emailState = remember { mutableStateOf(TextFieldValue("")) }
    var isEmailValid by remember { mutableStateOf(true) }
    val uiState by viewModel.uiState.collectAsState()
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun isValidEmail(email: String): Boolean {
        val emailRegex = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$"
        )
        return emailRegex.matcher(email).matches()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
    ) {
        Text(
            text = "Forgot Password?",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = "Enter your email and we'll send you instructions to reset your password.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        InputIcon(
            value = emailState.value,
            onValueChange = {
                emailState.value = it
                isEmailValid = true // reset error when typing
            },
            placeholder = "Email",
            leadingIcon = Icons.Default.Email,
            isError = !isEmailValid,
            errorMessage = if (!isEmailValid) "Invalid email format" else null
        )

        if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }

        PrimaryButton(
            onClick = {
                val email = emailState.value.text.trim()
                isEmailValid = isValidEmail(email)
                if (isEmailValid) {
                    viewModel.sendRecoveryEmail(email)
                }
            },
            text = "Send Recovery Email"
        )

        Text(
            text = "Back to login",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable { onBackToLogin() }
        )
    }

    LaunchedEffect(uiState) {
        when (uiState) {
            is ApiResult.Success -> {
                //toast
                Toast.makeText(
                    context,
                    "Recovery email sent! Please return to login.",
                    Toast.LENGTH_LONG
                ).show()
                println("Success, please return back to Login")
                errorMessage = null
            }
            is ApiResult.Error -> {
                println("Error: ${(uiState as ApiResult.Error).message}")
                val error = uiState as ApiResult.Error
                errorMessage = error.message
            }
            is ApiResult.Exception -> {
                println("Exception error")
                errorMessage = "There was an error, please try again"
            }
            else -> Unit
        }
    }
}

@Composable
fun InputIcon(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    placeholder: String,
    leadingIcon: ImageVector,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder) },
        leadingIcon = { Icon(imageVector = leadingIcon, contentDescription = null) },
        modifier = Modifier.fillMaxWidth(),
        isError = isError,
        supportingText = {
            if (isError && errorMessage != null) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    )
}
