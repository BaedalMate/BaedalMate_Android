package com.mate.baedalmate.common.dialog

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.mate.baedalmate.R
import com.mate.baedalmate.common.GetDeviceSize
import com.mate.baedalmate.common.extension.setOnDebounceClickListener
import com.mate.baedalmate.databinding.DialogConfirmBinding

object ConfirmAlertDialog {
    fun createChoiceDialog(
        context: Context,
        title: String,
        description: String,
        confirmButtonFunction: (() -> Unit)? = null,
    ): AlertDialog {
        val binding = DialogConfirmBinding.inflate(LayoutInflater.from(context))
        val alertDialog = AlertDialog.Builder(context)
            .setView(binding.root)
            .setCancelable(false)
            .create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.window?.requestFeature(Window.FEATURE_NO_TITLE)

        with(binding) {
            tvDialogConfirmTitle.text = title
            tvDialogConfirmDescription.text = description
            btnDialogConfirmCancel.setOnDebounceClickListener {
                alertDialog.dismiss()
            }
            btnDialogConfirmConfirm.setOnDebounceClickListener {
                if (confirmButtonFunction != null) {
                    confirmButtonFunction()
                }
                alertDialog.dismiss()
            }
        }

        return alertDialog
    }

    fun createInfoDialog(
        context: Context,
        title: String,
        description: String,
        confirmButtonFunction: (() -> Unit)? = null,
    ): AlertDialog {
        val binding = DialogConfirmBinding.inflate(LayoutInflater.from(context))
        val alertDialog = AlertDialog.Builder(context)
            .setView(binding.root)
            .setCancelable(true)
            .create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.window?.requestFeature(Window.FEATURE_NO_TITLE)

        with(binding) {
            tvDialogConfirmTitle.text = title
            tvDialogConfirmDescription.text = description
            btnDialogConfirmCancel.visibility = View.GONE
            btnDialogConfirmConfirm.background = ContextCompat.getDrawable(btnDialogConfirmConfirm.context, R.drawable.selector_btn_gray_line_white_radius_10_bottom)
            btnDialogConfirmConfirm.setOnDebounceClickListener {
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

    fun showConfirmDialog(dialog: AlertDialog) {
        dialog.show()
    }

    fun hideConfirmDialog(dialog: AlertDialog) {
        dialog.dismiss()
    }
}