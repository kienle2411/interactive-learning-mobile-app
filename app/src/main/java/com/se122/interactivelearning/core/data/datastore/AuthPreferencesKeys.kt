package com.se122.interactivelearning.core.data.datastore

import androidx.datastore.preferences.core.stringPreferencesKey

object AuthPreferencesKeys {
    val ACCESS_TOKEN = stringPreferencesKey("access_token")
    val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
}