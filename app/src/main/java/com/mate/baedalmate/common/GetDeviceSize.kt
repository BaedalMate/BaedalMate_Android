package com.mate.baedalmate.common

import android.content.Context
import android.graphics.Point
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

object GetDeviceSize {
    val convertInchesTomm: Double = 25.4

    fun getDeviceWidthSize(context: Context): Int {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        if (Build.VERSION.SDK_INT < 30) {
            val display = windowManager.defaultDisplay
            val size = Point()
            display.getSize(size)
            return size.x
        } else {
            val rect = windowManager.currentWindowMetrics.bounds
            return rect.width()
        }
    }

    fun getDeviceWidthSizeDp(context: Context): Int {
        return (
            context.getResources().getDisplayMetrics().widthPixels / context.getResources()
                .getDisplayMetrics().density
            ).toInt()
    }

    fun getDeviceHeightSize(context: Context): Int {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        if (Build.VERSION.SDK_INT < 30) {
            val display = windowManager.defaultDisplay
            val size = Point()
            display.getSize(size)
            return size.y
        } else {
            val rect = windowManager.currentWindowMetrics.bounds
            return rect.height()
        }
    }

    fun getDeviceHeightSizeDp(context: Context): Int {
        return (
            context.getResources().getDisplayMetrics().heightPixels / context.getResources()
                .getDisplayMetrics().density
            ).toInt()
    }

    fun getPhysicalSizeWithInches(context: Context): Double {
        val wi = getPhysicalSizeWithInchesWidth(context)
        val hi = getPhysicalSizeWithInchesHeight(context)
        val x = Math.pow(wi, 2.0)
        val y = Math.pow(hi, 2.0)
        return (Math.round(Math.sqrt(x + y) * 10.0) / 10.0)
    }

    fun getPhysicalSizeWithInchesWidth(context: Context): Double {
        val point = Point()
        (context.getSystemService(AppCompatActivity.WINDOW_SERVICE) as WindowManager).defaultDisplay.getRealSize(
            point
        )
        val displayMetrics = context.resources.displayMetrics
        val width = point.x
        return (width.toDouble() / displayMetrics.xdpi.toDouble())
    }

    fun getPhysicalSizeWithInchesHeight(context: Context): Double {
        val point = Point()
        (context.getSystemService(AppCompatActivity.WINDOW_SERVICE) as WindowManager).defaultDisplay.getRealSize(
            point
        )
        val displayMetrics = context.resources.displayMetrics
        val height = point.y
        return (height.toDouble() / displayMetrics.ydpi.toDouble())
    }

    fun getPhysicalSizeWithmm(context: Context): Double {
        val wi = getPhysicalSizeWithmmWidth(context)
        val hi = getPhysicalSizeWithmmHeight(context)
        val x = Math.pow(wi, 2.0)
        val y = Math.pow(hi, 2.0)
        return (Math.round(Math.sqrt(x + y) * 10.0) / 10.0)
    }

    fun getPhysicalSizeWithmmWidth(context: Context): Double =
        getPhysicalSizeWithInchesWidth(context) * convertInchesTomm

    fun getPhysicalSizeWithmmHeight(context: Context): Double =
        getPhysicalSizeWithInchesHeight(context) * convertInchesTomm

    fun getTextSizemm(context: Context, textView: TextView): Double {
        val displayMetric: DisplayMetrics = context.getResources().getDisplayMetrics()
        return (textView.textSize * convertInchesTomm) / (displayMetric.densityDpi.toDouble())
    }

    fun convertDPTomm(context: Context, value: Int): Double {
        val displayMetric: DisplayMetrics = context.getResources().getDisplayMetrics()
        return (value.dp * convertInchesTomm) / (displayMetric.densityDpi.toDouble())
    }
}
