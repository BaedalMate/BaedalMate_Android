package com.mate.baedalmate.presentation.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.mate.baedalmate.R
import com.mate.baedalmate.databinding.ActivityMainBinding
import com.mate.baedalmate.presentation.fragment.home.HomeFragmentDirections
import com.mate.baedalmate.presentation.viewmodel.MemberViewModel
import com.mate.baedalmate.presentation.viewmodel.NotificationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var binding: ActivityMainBinding
    private val memberViewModel by viewModels<MemberViewModel>()
    private val notificationViewModel by viewModels<NotificationViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAnalytics = Firebase.analytics
        setFcm()
        getNotificationData()
        initBottomNavigation()
        setBottomNaviVisibility()
        observeAccountValid()
        askNotificationPermission()
    }

    private val permissionLauncherNotification =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val deniedList: List<String> = permissions.filter {
                !it.value
            }.map {
                it.key
            }

            when {
                deniedList.isNotEmpty() -> {
                    val map = deniedList.groupBy { permission ->
                        if (shouldShowRequestPermissionRationale(permission)) "DENIED" else "EXPLAINED"
                    }
                    map["DENIED"]?.let {
                        // request denied , request again
                        Toast.makeText(this,
                            "알림 권한을 허용하지 않으면 정상적 사용이 불가능할 수 있습니다",
                            Toast.LENGTH_SHORT
                        ).show()
                        ActivityCompat.requestPermissions(
                            this,
                            notificationPermission,
                            1000
                        )
                    }
                    map["EXPLAINED"]?.let {
                        Intent().apply {
                            action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                            putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                        }
                        Toast.makeText(
                            this,
                            "설정에서 알림을 허용으로 변경해주세요",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                else -> {
                    //All request are permitted
                    // 알림 최초 허용시에 모든 알림 허용처리
                    with(notificationViewModel) {
                        setNotificationAll(true)
                        setNotificationNewMessage(true)
                        setNotificationRecruit(true)
                        setNotificationNotice(true)
                    }
                }
            }
        }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                // by them granting the POST_NOTIFICATION permission. This UI should provide the user
                // "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                // If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                permissionLauncherNotification.launch(notificationPermission)
            }
        }
    }

    private fun getNotificationData() {
        val extraFragment = intent.getStringExtra(IS_NAVIGATE_FRAGMENT_EXIST_FLAG)
        if (extraFragment != null && extraFragment == NAVIGATE_FRAGMENT_FLAG_NAVIGATION) {
            val chatRoomId = intent.getStringExtra("chatRoomId")?.toInt()
            if (chatRoomId != null) findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToChatFragment(
                    roomId = chatRoomId
                )
            ) else findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToNotificationFragment())
        }
    }

    private fun initBottomNavigation() {
        binding.bottomNavView.setupWithNavController(findNavController())
    }

    private fun findNavController(): NavController {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        return navHostFragment.navController
    }

    private fun setBottomNaviVisibility() {
        binding.bottomNavView.itemIconTintList = null
        findNavController().addOnDestinationChangedListener { _, destination, _ ->
            binding.bottomNavView.visibility = when (destination.id) {
                R.id.HomeFragment -> View.VISIBLE
//                R.id.PostCategoryListFragment -> View.VISIBLE
                // TODO: PostCategoryFragment에서 BottomNavigation이 정상적으로 작동하지 않는 현상
                R.id.ChatListFragment -> View.VISIBLE
                R.id.MyPageFragment -> View.VISIBLE
                else -> View.GONE
            }
        }
    }

    private fun observeAccountValid() {
        memberViewModel.isAccountValid.observe(this) { isValid ->
            if (isValid == false) {
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                intent.putExtra("isValid", isValid)
                startActivity(intent)
                this.finish()
            } else {
            }
        }
    }

    private fun setFcm() {
        lifecycleScope.launch {
            notificationViewModel.registerFcmToken(contentResolver = this@MainActivity.contentResolver)
        }
    }

    companion object {
        const val IS_NAVIGATE_FRAGMENT_EXIST_FLAG = "ExtraFragment"
        const val NAVIGATE_FRAGMENT_FLAG_NAVIGATION = "Notification"
        val notificationPermission = arrayOf(Manifest.permission.POST_NOTIFICATIONS)
    }
}