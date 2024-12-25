package com.example.gallery.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.gallery.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val fragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView)
        val controller = fragment?.findNavController()
        NavigationUI.setupActionBarWithNavController(this,controller!!)
    }

    override fun onSupportNavigateUp(): Boolean {
        val controller = Navigation.findNavController(this, R.id.fragmentContainerView)
        return controller.navigateUp()
    }
}