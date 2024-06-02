package com.example.fitnesstrackerapp.mvvm

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.fitnesstrackerapp.R
import com.example.fitnesstrackerapp.databinding.ActivityMainBinding
import com.example.fitnesstrackerapp.other.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        navigateToTrackingFragmentIfNeeded(intent)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.navHostFragment) as NavHostFragment

        val navController = navHostFragment.navController

        binding.bottomNavigationView.setupWithNavController(navController)
        binding.bottomNavigationView.setOnNavigationItemReselectedListener { /* No operation */ }

        navHostFragment.navController.addOnDestinationChangedListener { controller, destination, _ ->
            when (destination.id) {
                R.id.profileFragment, R.id.trainingFragment,
                R.id.statisticsFragment, R.id.trackingRouteFragment,
                R.id.communityFragment, R.id.newsFragment
                -> {

                    if(controller.previousBackStackEntry != null &&
                        controller.previousBackStackEntry!!.destination.id == R.id.profileFragment){
                        binding.bottomNavigationView.visibility = View.GONE
                        val param = binding.frameLayoutContainer.layoutParams as ConstraintLayout.LayoutParams
                        param.setMargins(0,0,0,0)
                        binding.frameLayoutContainer.layoutParams = param
                    } else {
                        binding.bottomNavigationView.visibility = View.VISIBLE

                        val param = binding.frameLayoutContainer.layoutParams as ConstraintLayout.LayoutParams

                        param.setMargins(0,0,0,
                            getResources().getDimension(R.dimen.margin_bottom).toInt()
                        )
                        binding.frameLayoutContainer.layoutParams = param
                    }
                }

                else -> {
                    binding.bottomNavigationView.visibility = View.GONE
                    val param = binding.frameLayoutContainer.layoutParams as ConstraintLayout.LayoutParams
                    param.setMargins(0,0,0,0)
                    binding.frameLayoutContainer.layoutParams = param
                }

            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navigateToTrackingFragmentIfNeeded(intent)
    }

    private fun navigateToTrackingFragmentIfNeeded(intent: Intent?) {
        if(intent?.action == ACTION_SHOW_TRACKING_FRAGMENT) {
            val navHostFragment = supportFragmentManager
                .findFragmentById(R.id.navHostFragment) as NavHostFragment

            navHostFragment.findNavController().navigate(R.id.action_global_trackingRouteFragment)
        }
    }
}
