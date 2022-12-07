package com.aakash.videoandcamera

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.databinding.DataBindingUtil
import com.aakash.imageandvideopicker.CameraPickfromGallery
import com.aakash.videoandcamera.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

      binding= DataBindingUtil.setContentView(this,R.layout.activity_main)

        val actionBar: ActionBar? = supportActionBar
        if (actionBar != null) actionBar.hide()

         val windowInsetsController =
            WindowCompat.getInsetsController(window, window.decorView) ?: return
            windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
             windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())

        binding.open.setOnClickListener{
           startActivity(Intent(this@MainActivity,CameraPickfromGallery::class.java))
            //go to image activity
        }
    }
}