package com.se122.interactivelearning.data

import android.util.Log
import com.se122.interactivelearning.data.remote.api.AuthApi
import com.se122.interactivelearning.data.remote.dto.RefreshRequest
import com.se122.interactivelearning.di.LogoutEventBus
import com.se122.interactivelearning.utils.TokenManager
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Provider

class TokenAuthenticator @Inject constructor(
    private val tokenManager: TokenManager,
    private val authApiProvider: Provider<AuthApi>,
    private val logoutEventBus: LogoutEventBus
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {

        val requestUrl = response.request.url.toString()
        if (requestUrl.contains("/auth/refresh")) {
            runBlocking {
                tokenManager.clearToken()
                logoutEventBus.sendLogoutEvent()
            }
            return null
        }

        val refreshToken = runBlocking { tokenManager.getRefreshToken() }
        val accessToken = runBlocking { tokenManager.getAccessToken() }

        if (refreshToken.isNullOrBlank() || accessToken.isNullOrBlank()) {
            runBlocking {
                logoutEventBus.sendLogoutEvent()
            }
            return null
        }

        val authApi = authApiProvider.get()
        val refreshResponse = runBlocking { authApi.refresh("Bearer $refreshToken") }

        if (refreshResponse.isSuccessful) {
            val tokenBody = refreshResponse.body()?.data
            if (tokenBody == null) {
                runBlocking {
                    tokenManager.clearToken()
                    logoutEventBus.sendLogoutEvent()
                }
                return null
            }
            val accessToken = refreshResponse.body()?.data?.accessToken ?: return null
            val refreshToken = refreshResponse.body()?.data?.refreshToken ?: return null
            Log.i("HomeViewModel", "RefreshToken1 $refreshToken")
            runBlocking {
                tokenManager.saveTokens(accessToken, refreshToken)
            }
            response.request.newBuilder()
                .header("Authorization", "Bearer $accessToken")
                .build()
        } else {
            runBlocking {
                tokenManager.clearToken()
                logoutEventBus.sendLogoutEvent()
            }
            return null
        }
        return null
    }
}

fun Response.responseCount(): Int {
    var count = 1
    var priorResponse = this.priorResponse
    while (priorResponse != null) {
        count++
        priorResponse = priorResponse.priorResponse
    }
    return count
}