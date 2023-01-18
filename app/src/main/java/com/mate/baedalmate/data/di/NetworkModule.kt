package com.mate.baedalmate.data.di

import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.gson.GsonBuilder
import com.mate.baedalmate.BaedalMateApplication
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import com.mate.baedalmate.BuildConfig
import com.mate.baedalmate.common.extension.readValue
import com.mate.baedalmate.common.extension.storeValue
import io.sentry.Sentry
import io.sentry.SentryLevel
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject
import java.lang.Exception
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val BASE_URL = "http://3.35.27.107:8080"

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        return OkHttpClient.Builder()
            .addNetworkInterceptor { chain ->
                val requestBuild = chain.request().newBuilder()
                    .removeHeader("User-Agent") // otherwise addHeader not work.
                    .addHeader(
                        "User-Agent",
                        "${Build.MODEL} BaedalMate/${BuildConfig.VERSION_NAME} (${BaedalMateApplication.applicationContext().packageName}; build:${BuildConfig.VERSION_CODE}; Android ${Build.VERSION.SDK_INT})"
                    )
                    .build()
                chain.proceed(requestBuild)
            }
            .addInterceptor { chain: Interceptor.Chain ->
                val accessToken = runBlocking {
                    BaedalMateApplication.applicationContext().tokenDataStore.readValue(
                        stringPreferencesKey("accessToken")
                    )
                }
                val request = chain.request()
                // Header에 AccessToken을 삽입하지 않는 대상
                if (request.url.encodedPath.equals("/login/oauth2/kakao", true)) {
                    chain.proceed(request)
                } else {
                    chain.proceed(
                        request.newBuilder().apply {
                            addHeader("Authorization", "Bearer $accessToken")
                        }.build()
                    )
                }
            }
            .addInterceptor { chain: Interceptor.Chain ->
                val originalRequest = chain.request()
                val originalResponse = chain.proceed(originalRequest)
                val accessToken = runBlocking {
                    BaedalMateApplication.applicationContext().tokenDataStore.readValue(
                        stringPreferencesKey("accessToken")
                    )
                }
                val refreshToken =
                    runBlocking {
                        BaedalMateApplication.applicationContext().tokenDataStore.readValue(
                            stringPreferencesKey("refreshToken")
                        )
                    }
                val emptyBody = RequestBody.create("application/json".toMediaTypeOrNull(), "")
                when (originalResponse.code) {
                    401 -> {
                        val newRequest = Request.Builder()
                            .url("${BASE_URL}/api/v1/refresh")
                            .addHeader("Authorization", "Bearer $accessToken")
                            .addHeader("Refresh-Token", "$refreshToken")
                            .post(emptyBody)
                            .build()
                        var tokenRefreshResponse = chain.proceed(newRequest)
                        when (tokenRefreshResponse.code) {
                            200 or 403 -> {
                                val rawResponseJson = tokenRefreshResponse.peekBody(2048).string()
                                try {
                                    val refreshedAccessToken =
                                        JSONObject(rawResponseJson).getString("accessToken")
                                    val refreshedRefreshToken =
                                        JSONObject(rawResponseJson).getString("refreshToken")
                                    runBlocking {
                                        BaedalMateApplication.applicationContext().tokenDataStore.storeValue(
                                            stringPreferencesKey("accessToken"),
                                            refreshedAccessToken,
                                        )
                                        BaedalMateApplication.applicationContext().tokenDataStore.storeValue(
                                            stringPreferencesKey("refreshToken"),
                                            refreshedRefreshToken,
                                        )
                                    }
                                    val newTokenOriginalRequest = chain.request().newBuilder()
                                        .removeHeader("Authorization")
                                        .addHeader("Authorization", "Bearer $refreshedAccessToken")
                                        .build()
                                    tokenRefreshResponse = chain.proceed(newTokenOriginalRequest)
                                } catch (e: JSONException) {
                                    e.printStackTrace()
                                    Sentry.captureException(e)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    Sentry.captureException(e)
                                }
                            }
                            in 400..401 -> {
                                if (!originalRequest.url.encodedPath.equals("/api/v1/user", true)) {
                                    val handler = Handler(Looper.getMainLooper())
                                    handler.postDelayed(
                                        Runnable {
                                            Toast.makeText(
                                                BaedalMateApplication.applicationContext(),
                                                "다시 로그인 해주세요",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        },
                                        0
                                    )
                                }
                            }
                            else -> {
                                Log.e("Token Refresh Network Error", "$$tokenRefreshResponse")
                                Sentry.captureMessage(
                                    "Token Refresh Network Error: $tokenRefreshResponse",
                                    SentryLevel.ERROR
                                )
                                val handler = Handler(Looper.getMainLooper())
                                handler.postDelayed(
                                    Runnable {
                                        Toast.makeText(
                                            BaedalMateApplication.applicationContext(),
                                            "네트워크 연결이 불안정 합니다",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    },
                                    0
                                )
                            }
                        }
                        tokenRefreshResponse
                    }
                    403 -> {
                        originalResponse
                    }
                    else -> {
                        originalResponse
                    }
                }
            }
            .addInterceptor(loggingInterceptor)
            .readTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
            .connectTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
            .build()
    }

    @Singleton
    @Provides
    fun provideConverterFactory(): GsonConverterFactory =
        GsonConverterFactory.create()

    @Singleton
    @Provides
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(gsonConverterFactory)
            .client(okHttpClient)
            .build()
    }
}