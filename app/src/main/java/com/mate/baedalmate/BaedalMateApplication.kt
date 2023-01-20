package com.mate.baedalmate

import android.app.Application
import android.content.Context
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.kakao.sdk.common.KakaoSdk
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BaedalMateApplication : Application() {
    init {
        instance = this
    }
    companion object {
        lateinit var instance: BaedalMateApplication
        fun applicationContext(): Context {
            return instance.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()

        // Kakao SDK Initialize
        KakaoSdk.init(this, BuildConfig.NATIVE_APP_KEY)
        // Firebase Crashlytics Enable at Release
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)
    }
}