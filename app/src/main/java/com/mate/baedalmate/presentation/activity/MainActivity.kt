package com.mate.baedalmate.presentation.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.mate.baedalmate.R
import com.mate.baedalmate.databinding.ActivityMainBinding
import com.mate.baedalmate.presentation.viewmodel.MemberViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var binding: ActivityMainBinding
    private val memberViewModel by viewModels<MemberViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAnalytics = Firebase.analytics
        initBottomNavigation()
        setBottomNaviVisibility()
        observeAccountValid()
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
}