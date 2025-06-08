package com.se122.interactivelearning.ui.screens.profile

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.se122.interactivelearning.common.ViewState
import com.se122.interactivelearning.data.remote.api.ApiResult
import com.se122.interactivelearning.data.remote.dto.ProfileResponse
import com.se122.interactivelearning.data.remote.dto.UploadAvatarResponse
import com.se122.interactivelearning.domain.usecase.profile.GetProfileUseCase
import com.se122.interactivelearning.domain.usecase.profile.UploadAvatarUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase,
    private val uploadAvatarUseCase: UploadAvatarUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val _profile = MutableStateFlow<ViewState<ProfileResponse>>(ViewState.Idle)
    val profile = _profile.asStateFlow()

    private val _avatar = MutableStateFlow<ViewState<UploadAvatarResponse>>(ViewState.Idle)
    val avatar = _avatar.asStateFlow()

    init {
        viewModelScope.launch {
            loadProfile()
        }
    }

    fun loadProfile() {
        viewModelScope.launch {
            _profile.value = ViewState.Loading
            when (val result = getProfileUseCase.invoke()) {
                is ApiResult.Success -> {
                    _profile.value = ViewState.Success(result.data)
                }
                else -> {}
            }
        }
    }

    fun uploadAvatar(image: Uri) {
        viewModelScope.launch {
            try {
                val bytes = context.contentResolver.openInputStream(image)?.use {
                    it.readBytes()
                }
                if (bytes != null) {
                    val file = MultipartBody.Part.createFormData(
                        "file",
                        "avatar",
                        bytes.toRequestBody("image/*".toMediaTypeOrNull())
                    )
                    _avatar.value = ViewState.Loading
                    when (val result = uploadAvatarUseCase.invoke(file)) {
                        is ApiResult.Success -> {
                            _avatar.value = ViewState.Success(result.data)
                            loadProfile()
                        }
                        else -> {}
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}