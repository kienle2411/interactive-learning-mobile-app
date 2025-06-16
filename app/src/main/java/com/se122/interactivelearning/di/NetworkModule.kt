package com.se122.interactivelearning.di

import com.se122.interactivelearning.core.data.network.TokenAuthenticator
import com.se122.interactivelearning.data.remote.api.AuthApi
import com.se122.interactivelearning.data.remote.api.ClassroomApi
import com.se122.interactivelearning.data.remote.api.FileApi
import com.se122.interactivelearning.data.remote.api.MeetingApi
import com.se122.interactivelearning.data.remote.api.UserApi
import com.se122.interactivelearning.utils.TokenManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    fun provideBaseUrl() = "https://learn.kienle2411.id.vn/api/"

    @Provides
    @Singleton
    fun provideOkHttpClient(tokenManager: TokenManager, tokenAuthenticator: TokenAuthenticator): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val tokenInterceptor = Interceptor { chain ->
            val token = runBlocking { tokenManager.getAccessToken() }
            val originalRequest = chain.request()
            val requestUrl = originalRequest.url.toString()
            if (requestUrl.contains("/auth/refresh")) {
                return@Interceptor chain.proceed(originalRequest)
            }
            val newRequest = chain.request().newBuilder().apply {
                if (token != null) {
                    header("Authorization", "Bearer $token")
                }
            }.build()
            chain.proceed(newRequest)
        }
        return OkHttpClient.Builder().addInterceptor(loggingInterceptor).addInterceptor(tokenInterceptor).authenticator(tokenAuthenticator).build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(baseUrl: String, okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build()
    }

    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }

    @Provides
    @Singleton
    fun provideClassroomApi(retrofit: Retrofit): ClassroomApi {
        return retrofit.create(ClassroomApi::class.java)
    }

    @Provides
    @Singleton
    fun provideFileApi(retrofit: Retrofit): FileApi {
        return retrofit.create(FileApi::class.java)
    }

    @Provides
    @Singleton
    fun provideMeetingApi(retrofit: Retrofit): MeetingApi {
        return retrofit.create(MeetingApi::class.java)
    }

    @Provides
    @Singleton
    fun provideUserApi(retrofit: Retrofit): UserApi {
        return retrofit.create(UserApi::class.java)
    }
}