package com.mate.baedalmate.common.extension

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.ContextWrapper

fun Context.isActivityDestroyed(): Boolean {
    if (this is Application) return false
    val activity = getActivity() ?: return true
    return activity.isFinishing || activity.isDestroyed
}

fun Context.getActivity(): Activity? {
    return when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.getActivity()
        else -> null
    }
}