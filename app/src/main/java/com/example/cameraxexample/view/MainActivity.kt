package com.example.cameraxexample.view

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import com.example.cameraxexample.R
import com.example.cameraxexample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)

    }

    fun showLoadingPercentageLayout() {
        binding.loadingPercentageLayout.mainLayout.visibility = View.VISIBLE
    }

    fun hideLoadingPercentageLayout() {
        runOnUiThread {
            binding.loadingPercentageLayout.percentage.text = ""
            binding.loadingPercentageLayout.mainLayout.visibility = View.GONE
        }
    }

}