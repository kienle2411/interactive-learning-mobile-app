package com.se122.interactivelearning.ui.screens.auth.login

import android.graphics.drawable.Icon
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.se122.interactivelearning.R
import com.se122.interactivelearning.ui.components.InputIcon
import com.se122.interactivelearning.ui.components.PasswordInput
import com.se122.interactivelearning.ui.components.PrimaryButton
import androidx.compose.runtime.getValue
import com.se122.interactivelearning.common.ViewState

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onRegister: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    val loginState by viewModel.loginState.collectAsState()

    val usernameState = remember { mutableStateOf(TextFieldValue("")) }
    val passwordState = remember { mutableStateOf(TextFieldValue("")) }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(space = 16.dp, alignment = Alignment.CenterVertically)
    ) {
        Text(
            text = "Login",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(space = 5.dp, alignment = Alignment.CenterHorizontally),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Don't have account?",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Sign up",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable {
                    onRegister()
                },
            )
        }
        InputIcon(
            value = usernameState.value,
            onValueChange = {
                usernameState.value = it
            },
            placeholder = "Username",
            leadingIcon = Icons.Default.Person
        )
        PasswordInput(
            value = passwordState.value,
            onValueChange = {
                passwordState.value = it
            },
        )
        Text(
            text = "Forgot password?",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.align(Alignment.End),
        )
        PrimaryButton(
            onClick = {
                viewModel.login(usernameState.value.text, passwordState.value.text)
            },
            text = "Continue",
        )

        when (loginState) {
            is ViewState.Idle -> {}
            is ViewState.Loading -> {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is ViewState.Success -> {
                LaunchedEffect(loginState) {
                    onLoginSuccess()
                }
            }
            is ViewState.Error -> {
                val msg = (loginState as ViewState.Error).message ?: "Unknown error"
                Text(msg, color = Color.Red, modifier = Modifier.padding(top = 8.dp))
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(space = 20.dp, alignment = Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(
                modifier = Modifier
                    .weight(1f)
            )
            Text(
                text = "or login with",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            HorizontalDivider(
                modifier = Modifier
                    .weight(1f)
            )
        }
        Image(
            painter = painterResource(id = R.drawable.google_logo),
            contentDescription = "Google Logo",
            modifier = Modifier.size(50.dp)
        )
        Text(
            text = "By clicking Create account you agree to Recognotes\n" +
                    "Terms of use and Privacy policy",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
    }
}