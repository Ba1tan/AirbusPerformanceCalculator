package com.example.airbusperformancecalculator

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.airbusperformancecalculator.adapters.FragmentAdapter
import com.example.airbusperformancecalculator.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var fragmentAdapter: FragmentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val insetsController = androidx.core.view.WindowCompat.getInsetsController(window, window.decorView)
        insetsController.isAppearanceLightStatusBars = false
        insetsController.isAppearanceLightNavigationBars = false

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        fragmentAdapter = FragmentAdapter(supportFragmentManager, lifecycle)
        binding.viewpager.adapter = fragmentAdapter

        val tabTitles = arrayOf(
            getString(R.string.calc_page_name),
            getString(R.string.metar_name)
        )
        val tabIcons = intArrayOf(
            R.drawable.baseline_airplanemode_active_24,
            R.drawable.baseline_air_24
        )

        TabLayoutMediator(binding.tabLayout, binding.viewpager) { tab, position ->
            tab.text = tabTitles.getOrElse(position) { "" }
            tab.setIcon(tabIcons.getOrElse(position) { 0 })
        }.attach()
    }

    fun navigateToCalculator() {
        binding.viewpager.currentItem = 0
    }
}
