package com.mate.baedalmate.common.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.mate.baedalmate.common.GetDeviceSize
import com.mate.baedalmate.common.extension.isActivityDestroyed

open class RoundDialogFragment() : DialogFragment() {
    private var dismissOnPause = false
    var dialogSizeRatio: Float = 0.9f // 좌우 여백을 조정하려는 경우 해당 변수를 조정필요. 기본 기기 좌우여백 10%
    var dialogHeightSizeRatio: Float = 1f // 1f인 경우 Dialog 세로길이를 조절하지 않음
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(SAVED_DISMISS_ON_PAUSE, dismissOnPause)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        if (savedInstanceState != null) {
            dismissOnPause = savedInstanceState.getBoolean(SAVED_DISMISS_ON_PAUSE, false)
        }

        return object : Dialog(requireContext()) {
            override fun show() {
                if (context.isActivityDestroyed()) return
                super.show()
            }

            override fun dismiss() {
                if (context.isActivityDestroyed()) return
                super.dismiss()
            }
        }.apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val contentView = onCreateView(LayoutInflater.from(context), null, savedInstanceState)
            if (contentView != null) {
                setContentView(contentView)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        resizeDialogFragment()
    }

    private fun resizeDialogFragment() {
        val params: ViewGroup.LayoutParams? = dialog?.window?.attributes
        val deviceWidth = GetDeviceSize.getDeviceWidthSize(requireContext())
        val deviceHeight = GetDeviceSize.getDeviceHeightSize(requireContext())
        params?.width = (deviceWidth * dialogSizeRatio).toInt()
        if(dialogHeightSizeRatio != 1f) {
            params?.height = (deviceHeight * dialogHeightSizeRatio).toInt()
        }
        dialog?.window?.attributes = params as WindowManager.LayoutParams
    }

    override fun onPause() {
        super.onPause()
        if (isCancelable && dismissOnPause) {
            dismiss()
        }
    }

    protected fun setDismissOnPause(dismissOnPause: Boolean) {
        this.dismissOnPause = dismissOnPause
    }

    companion object {
        private const val SAVED_DISMISS_ON_PAUSE = "dismissOnPause"
    }
}
