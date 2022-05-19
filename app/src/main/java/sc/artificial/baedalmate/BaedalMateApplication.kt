package sc.artificial.baedalmate

import android.app.Application
import android.content.Context
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

    }
}