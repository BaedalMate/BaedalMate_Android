package com.mate.baedalmate.common.dialog

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import com.mate.baedalmate.R
import com.mate.baedalmate.common.GetDeviceSize

object LoadingAlertDialog {
    fun createLoadingDialog(context: Context): AlertDialog {
        val dialogView =
            (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
                R.layout.dialog_loading_wait,
                null
            )
        val alertDialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .setCancelable(false)
            .create()
        return alertDialog
    }


    fun resizeDialogFragment(context: Context, dialog: AlertDialog, dialogSizeRatio: Float = 0.9f) {
        val params: ViewGroup.LayoutParams? = dialog?.window?.attributes
        val deviceWidth = GetDeviceSize.getDeviceWidthSize(context)
        params?.width = (deviceWidth * dialogSizeRatio).toInt()
        dialog?.window?.attributes = params as WindowManager.LayoutParams
    }

    fun showLoadingDialog(dialog: AlertDialog) {
        dialog.show()
    }

    fun hideLoadingDialog(dialog: AlertDialog) {
        dialog.dismiss()
    }
}