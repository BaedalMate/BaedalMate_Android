package com.mate.baedalmate.common.extension

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.annotation.IdRes
import androidx.navigation.NavController
import androidx.navigation.NavDirections

@SuppressLint("UnsafeNavigation")
fun NavController.navigateSafe(@IdRes action: Int, args: Bundle? = null): Boolean {
    return try {
        navigate(action, args)
        true
    } catch (t: Throwable) {
        Log.e("NAVIGATION_SAFE_TAG", "navigation error for action $action")
        false
    }
}

fun NavController.navigateSafe(direction: NavDirections): Boolean {
    return try {
        navigate(direction)
        true
    } catch (t: Throwable) {
        Log.e("NAVIGATION_SAFE_TAG", "navigation error for action $direction")
        false
    }
}