package com.se122.interactivelearning.utils

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import com.se122.interactivelearning.data.AuthPreferencesKeys
import com.se122.interactivelearning.data.dataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import java.io.IOException
import javax.inject.Inject

class TokenManager @Inject constructor(@ApplicationContext context: Context) {
    private val dataStore = context.dataStore

    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        dataStore.edit { prefs ->
            prefs[AuthPreferencesKeys.ACCESS_TOKEN] = accessToken
            prefs[AuthPreferencesKeys.REFRESH_TOKEN] = refreshToken
        }
    }

    val accessTokenFlow: Flow<String?> = dataStore.data.map { prefs -> prefs[AuthPreferencesKeys.ACCESS_TOKEN] }
    val refreshTokenFlow: Flow<String?> = dataStore.data.map { prefs -> prefs[AuthPreferencesKeys.REFRESH_TOKEN] }

    suspend fun clearToken() {
        dataStore.edit { prefs ->
            prefs.remove(AuthPreferencesKeys.ACCESS_TOKEN)
            prefs.remove(AuthPreferencesKeys.REFRESH_TOKEN)
        }
    }

    suspend fun getAccessToken(): String? {
        val preferences = dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .first()
        return preferences[AuthPreferencesKeys.ACCESS_TOKEN]
    }

    suspend fun getRefreshToken(): String? {
        val preferences = dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }.first()
        return preferences[AuthPreferencesKeys.REFRESH_TOKEN]
    }

    fun getAccessTokenSync(): String? {
        var token: String? = null
        runBlocking {
            token = dataStore.data
                .catch { exception ->
                    if (exception is IOException) {
                        emit(emptyPreferences())
                    } else {
                        throw exception
                    }
                }
                .first()[AuthPreferencesKeys.ACCESS_TOKEN]
        }
        return token
    }

    fun getRefreshTokenSync(): String? {
        var token: String? = null
        runBlocking {
            token = dataStore.data
                .catch { exception ->
                    if (exception is IOException) {
                        emit(emptyPreferences())
                    } else {
                        throw exception
                    }
                }
                .first()[AuthPreferencesKeys.REFRESH_TOKEN]
        }
        return token
    }
}