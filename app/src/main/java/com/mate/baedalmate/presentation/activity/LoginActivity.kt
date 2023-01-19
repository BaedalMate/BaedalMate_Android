package com.mate.baedalmate.presentation.activity

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ViewTreeObserver
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.UpdateAvailability
import com.mate.baedalmate.R
import com.mate.baedalmate.common.dialog.ConfirmAlertDialog
import com.mate.baedalmate.databinding.ActivityLoginBinding
import com.mate.baedalmate.presentation.viewmodel.MemberViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel by viewModels<MemberViewModel>()
    private lateinit var appUpdateManager: AppUpdateManager
    private var isReady = false
    private var isUpdateAvailable = false
    private lateinit var updateDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        createDialogUpdate()
        setSplash()
        checkUpdate()
    }

    private fun checkUpdate() {
        // TODO: 플레이스토어 출시 이전이므로 업데이트 체크 불가이기 때문에 체크 불가능한 경우 그냥 통과
        appUpdateManager = AppUpdateManagerFactory.create(this)
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                isUpdateAvailable = true
                isReady = true
                ConfirmAlertDialog.showConfirmDialog(updateDialog)
                ConfirmAlertDialog.resizeDialogFragment(this, updateDialog, 0.8f)
            }
        }

        if (!isUpdateAvailable) {
            autoLogin()
        }
    }

    private fun autoLogin() {
        val intent = intent
        val isValid = intent.getBooleanExtra("isValid", true)

        if (isValid) {
            loginViewModel.requestUserInfo()
            loginViewModel.getUserInfoSuccess.observe(
                this,
                Observer { isSuccess ->
                    if (isSuccess) {
                        skipToNextActivity()
                    } else if (isSuccess == false) {
                        Log.e(ContentValues.TAG, "AccessToken 만료")
                        isReady = true
                    } else {
                    }
                }
            )
        } else {
//            Lg.e("ACCOUNT IS NOT VALID")
            isReady = true
        }
    }

    private fun skipToNextActivity() {
        isReady = false // 로그인이 성공한 경우에는 Splash 화면을 없애지 않고 바로 넘어가게 하기 위해 false 설정
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        this@LoginActivity.finish()
    }

    private fun setSplash() {
        binding.root.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    return if (isReady) {
                        binding.root.viewTreeObserver.removeOnPreDrawListener(this)
                        true
                    } else
                        false
                }
            }
        )
    }

    private fun createDialogUpdate() {
        updateDialog = ConfirmAlertDialog.createInfoDialog(
            context = this,
            title = getString(R.string.update_dialog_title),
            description = getString(R.string.update_dialog_description),
            confirmButtonFunction = {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.addCategory(Intent.CATEGORY_DEFAULT)
                intent.data =
                    Uri.parse("http://play.google.com/store/apps/details?id=$packageName")
                startActivity(intent)
                finish()
            }
        )
    }
}