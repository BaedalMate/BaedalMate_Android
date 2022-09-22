package com.mate.baedalmate.presentation.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.mate.baedalmate.R
import com.mate.baedalmate.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initBottomNavigation()
        setBottomNaviVisibility()
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
                R.id.ChatFragment -> View.VISIBLE
                R.id.MyPageFragment -> View.VISIBLE
                else -> View.GONE
            }
        }
    }
}