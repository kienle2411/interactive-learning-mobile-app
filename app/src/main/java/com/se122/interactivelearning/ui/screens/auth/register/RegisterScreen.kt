package com.se122.interactivelearning.ui.screens.auth.register

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.se122.interactivelearning.common.ViewState
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import com.se122.interactivelearning.ui.components.PrimaryButton
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel = hiltViewModel(),
    onRegisterSuccess: () -> Unit,
    onLoginClick: () -> Unit,
) {
    val registerState by viewModel.registerState.collectAsState()
    val scrollState = rememberScrollState()

    val context = LocalContext.current
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val calendar = Calendar.getInstance()

    val username = remember { mutableStateOf(TextFieldValue("")) }
    val password = remember { mutableStateOf(TextFieldValue("")) }
    val firstName = remember { mutableStateOf(TextFieldValue("")) }
    val lastName = remember { mutableStateOf(TextFieldValue("")) }
    val dateOfBirth = remember { mutableStateOf(TextFieldValue("")) }
    val email = remember { mutableStateOf(TextFieldValue("")) }
    val phone = remember { mutableStateOf(TextFieldValue("")) }

    val usernameError = remember { mutableStateOf(false) }
    val passwordError = remember { mutableStateOf(false) }
    val firstNameError = remember { mutableStateOf(false) }
    val lastNameError = remember { mutableStateOf(false) }
    val dateOfBirthError = remember { mutableStateOf(false) }
    val emailError = remember { mutableStateOf(false) }
    val phoneError = remember { mutableStateOf(false) }

    val genders = listOf("MALE", "FEMALE", "OTHER")
    val selectedGender = remember { mutableStateOf(genders[0]) }
    var genderExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Register",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))

            InputIcon(
                value = username.value,
                onValueChange = { username.value = it },
                placeholder = "Username",
                isError = usernameError.value,
                supportingText = if (usernameError.value) "Username is required" else null
            )

            Spacer(modifier = Modifier.height(4.dp))

            PasswordInput(
                value = password.value,
                onValueChange = { password.value = it },
                isError = passwordError.value,
                supportingText = if (passwordError.value) "Password is required" else null
            )

            InputIcon(
                value = firstName.value,
                onValueChange = { firstName.value = it },
                placeholder = "First Name",
                isError = firstNameError.value,
                supportingText = if (firstNameError.value) "First name is required" else null
            )

            InputIcon(
                value = lastName.value,
                onValueChange = { lastName.value = it },
                placeholder = "Last Name",
                isError = lastNameError.value,
                supportingText = if (lastNameError.value) "Last name is required" else null
            )

            OutlinedTextField(
                value = dateOfBirth.value.text,
                onValueChange = {},
                enabled = false,
                isError = dateOfBirthError.value,
                supportingText = {
                    if (dateOfBirthError.value) Text("Date of birth is required")
                },
                label = { Text("Date of Birth") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        DatePickerDialog(
                            context,
                            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                                val selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
                                dateOfBirth.value =
                                    TextFieldValue(selectedDate.format(dateFormatter))
                            },
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                        ).show()
                    }
            )

            InputIcon(
                value = email.value,
                onValueChange = { email.value = it },
                placeholder = "Email",
                isError = emailError.value,
                supportingText = if (emailError.value) "Email is required" else null
            )

            InputIcon(
                value = phone.value,
                onValueChange = { phone.value = it },
                placeholder = "Phone",
                isError = phoneError.value,
                supportingText = if (phoneError.value) "Phone number is required" else null
            )

            ExposedDropdownMenuBox(
                expanded = genderExpanded,
                onExpandedChange = { genderExpanded = !genderExpanded },
            ) {
                OutlinedTextField(
                    value = selectedGender.value,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Gender") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = genderExpanded)
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = genderExpanded,
                    onDismissRequest = { genderExpanded = false },
                ) {
                    genders.forEach { gender ->
                        DropdownMenuItem(
                            text = { Text(gender) },
                            onClick = {
                                selectedGender.value = gender
                                genderExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            PrimaryButton(
                onClick = {
                    // validate fields
                    usernameError.value = username.value.text.isBlank()
                    passwordError.value = password.value.text.isBlank()
                    firstNameError.value = firstName.value.text.isBlank()
                    lastNameError.value = lastName.value.text.isBlank()
                    dateOfBirthError.value = dateOfBirth.value.text.isBlank()
                    emailError.value = email.value.text.isBlank()
                    phoneError.value = phone.value.text.isBlank()

                    val hasError = listOf(
                        usernameError.value,
                        passwordError.value,
                        firstNameError.value,
                        lastNameError.value,
                        dateOfBirthError.value,
                        emailError.value,
                        phoneError.value
                    ).any { it }

                    if (!hasError) {
                        viewModel.register(
                            username = username.value.text,
                            password = password.value.text,
                            firstName = firstName.value.text,
                            lastName = lastName.value.text,
                            dateOfBirth = dateOfBirth.value.text + "T00:00:00.000Z",
                            email = email.value.text,
                            phone = phone.value.text,
                            gender = selectedGender.value,
                        )
                    }
                },
                text = "Create Account"
            )

            Spacer(modifier = Modifier.height(4.dp))

            when (registerState) {
                is ViewState.Idle -> {}
                is ViewState.Loading -> CircularProgressIndicator()
                is ViewState.Success -> {
                    LaunchedEffect(Unit) { onRegisterSuccess() }
                }
                is ViewState.Error -> {
                    Text(
                        text = (registerState as ViewState.Error).message ?: "Registration failed",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row {
//                Text("Already have an account?")
                Text(
                    text = "Already have an account? ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Login",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable {
                        onLoginClick()
                    },
                )
            }
        }
    }
}

@Composable
fun InputIcon(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    placeholder: String,
    isError: Boolean = false,
    supportingText: String? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(placeholder) },
        isError = isError,
        supportingText = {
            if (supportingText != null) Text(supportingText)
        },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun PasswordInput(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    isError: Boolean = false,
    supportingText: String? = null
) {
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("Password") },
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            val icon = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(imageVector = icon, contentDescription = null)
            }
        },
        isError = isError,
        supportingText = {
            if (supportingText != null) Text(supportingText)
        },
        modifier = Modifier.fillMaxWidth()
    )
}
