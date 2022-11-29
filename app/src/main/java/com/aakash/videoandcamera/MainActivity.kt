package com.aakash.videoandcamera

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.aakash.imageandvideopicker.CameraPickfromGallery
import com.aakash.imageandvideopicker.MainActivity
import com.aakash.videoandcamera.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

      binding= DataBindingUtil.setContentView(this,R.layout.activity_main)

        binding.open.setOnClickListener{
           startActivity(Intent(this@MainActivity,MainActivity::class.java))
            //go to image activity
        }
    }
}