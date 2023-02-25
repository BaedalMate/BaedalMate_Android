package com.mate.baedalmate.common

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

object HideKeyBoardUtil {
    fun hide(activity: Activity) {
        val inputMethodManager: InputMethodManager =
            activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(activity.currentFocus?.windowToken, 0)
    }

    fun hideEditText(context: Context, editText: EditText) {
        editText.clearFocus()
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(editText.windowToken, 0)
    }

    @SuppressLint("ClickableViewAccessibility")
    fun hideTouchDisplay(activity: Activity, view: View) {
        var startClickTime = 0L;
        view.setOnTouchListener { v, event ->
            if (event?.action == MotionEvent.ACTION_DOWN) {
                startClickTime = System.currentTimeMillis()
            } else if (event?.action == MotionEvent.ACTION_UP) {
                if (System.currentTimeMillis() - startClickTime < ViewConfiguration.getTapTimeout()) {
                    val inputMethodManager: InputMethodManager =
                        activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(
                        activity.currentFocus?.windowToken,
                        0
                    )
                } else {
                    // Scroll
                }
            }
            false
        }
    }
}