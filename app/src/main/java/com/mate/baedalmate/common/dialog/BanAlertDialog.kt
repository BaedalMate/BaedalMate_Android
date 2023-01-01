package com.mate.baedalmate.common.dialog

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import com.mate.baedalmate.common.GetDeviceSize
import com.mate.baedalmate.common.extension.setOnDebounceClickListener
import com.mate.baedalmate.databinding.DialogBanBinding

object BanAlertDialog {
    fun createBanDialog(
        context: Context,
        confirmButtonFunction: (() -> Unit)? = null,
        userNickname: String
    ): AlertDialog {
        val binding = DialogBanBinding.inflate(LayoutInflater.from(context))
        val alertDialog = AlertDialog.Builder(context)
            .setView(binding.root)
            .setCancelable(true)
            .create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.window?.requestFeature(Window.FEATURE_NO_TITLE)

        with(binding) {
            nickname = userNickname
            btnDialogBanCancel.setOnDebounceClickListener {
                alertDialog.dismiss()
            }
            btnDialogBanConfirm.setOnDebounceClickListener {
                if (confirmButtonFunction != null) {
                    confirmButtonFunction()
                }
                alertDialog.dismiss()
            }
        }

        return alertDialog
    }

    fun resizeDialogFragment(context: Context, dialog: AlertDialog, dialogSizeRatio: Float = 0.9f) {
        val params: ViewGroup.LayoutParams? = dialog?.window?.attributes
        val deviceWidth = GetDeviceSize.getDeviceWidthSize(context)
        params?.width = (deviceWidth * dialogSizeRatio).toInt()
        dialog?.window?.attributes = params as WindowManager.LayoutParams
    }

    fun showBanDialog(dialog: AlertDialog) {
        dialog.show()
    }

    fun hideBanDialog(dialog: AlertDialog) {
        dialog.dismiss()
    }
}