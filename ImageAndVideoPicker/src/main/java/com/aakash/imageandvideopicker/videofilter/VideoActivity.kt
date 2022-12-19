package com.aakash.imageandvideopicker.videofilter

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.aakash.imageandvideopicker.R
import com.aakash.imageandvideopicker.databinding.ActivityVideoBinding
import com.aakash.imageandvideopicker.videofilter.adapter.VIdeoFilterOption
import com.aakash.imageandvideopicker.videofilter.controller.VideoController
import com.aakash.imageandvideopicker.videofilter.filter.NoEffectFilter
import com.aakash.imageandvideopicker.videofilter.interfaces.Filter
import com.aakash.imageandvideopicker.videofilter.interfaces.ShaderInterface
import com.aakash.imageandvideopicker.videofilter.listener.RvFilterListenr
import com.aakash.imageandvideopicker.videofilter.model.AssetsMetadataExtractor
import com.aakash.imageandvideopicker.videofilter.model.FilterModelOption
import com.aakash.imageandvideopicker.videofilter.model.Metadata
import com.aakash.imageandvideopicker.videofilter.model.Shaders
import java.util.ArrayList


class VideoActivity : AppCompatActivity() ,RvFilterListenr{
    private lateinit var list: MutableList<FilterModelOption>
    private lateinit var adpteer:VIdeoFilterOption

    private var filter: Filter = NoEffectFilter()
    private  lateinit var  shaders:Shaders

    companion object {
        const val WRITE_EXTERNAL_STORAGE = 201
        const val ASSET_NAME = "name"
        fun startActivity(ctx: Context, assetName: String) {
            ctx.startActivity(
                Intent(ctx, VideoActivity::class.java)
                    .putExtra(ASSET_NAME, assetName)
            )
        }
    }

    private var videoController: VideoController? = null
    private lateinit var binding: ActivityVideoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val filename = intent.getStringExtra("path")
            ?: throw RuntimeException("Asset name is null")
        videoController = VideoController(this, filename)
        list=ArrayList()
        adpteer= VIdeoFilterOption(this@VideoActivity,list,this)
        binding.rcyFilter.adapter=adpteer

         var metadata: Metadata? = AssetsMetadataExtractor().extract(filename)

        val videoWidth = metadata?.width?.toInt() ?: return
        val videoHeight = metadata?.height?.toInt() ?: return

         shaders = Shaders(videoWidth, videoHeight)

       for (i in 1..shaders.count-1)
       {
           list.add(FilterModelOption(shaders.getShaderName(i),"imag url"))

       }
        adpteer.notifyDataSetChanged()

        binding.openFilter.setOnClickListener {


            videoController!!.chooseShader()
        }
        binding.progress.setOnClickListener {


        }
    }

    fun setupVideoSurfaceView(mediaPlayer: MediaPlayer, width: Double, height: Double) {
        binding.videoSurfaceView.resizeView(width, height)
        binding.videoSurfaceView.init(mediaPlayer, NoEffectFilter())
    }

    fun setupSeekBar(onSeekBarChangeListener: SeekBar.OnSeekBarChangeListener) {
        binding.intensitySeekBar.max = 100
        binding.intensitySeekBar.isEnabled = false
        binding.intensitySeekBar.setOnSeekBarChangeListener(onSeekBarChangeListener)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.actions_menu, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        menu.findItem(R.id.save).isVisible = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean { // Handle item selection
        return when (item.itemId) {
            R.id.chooseShader -> {
                videoController?.chooseShader()
                true
            }
            R.id.save -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (isStoragePermissionNotGranted()) {
                        requestStoragePermissions()
                    } else {
                        println("VideoActivity.onOptionsItemSelected saved your video")
                     //  videoController?.saveVideo()
                    }
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (requestCode == WRITE_EXTERNAL_STORAGE) //videoController?.saveVideo()
                println("VideoActivity.onRequestPermissionsResult save video method call from hee")
        }
    }


    override fun onResume() {
        super.onResume()
        binding.videoSurfaceView.onResume()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onPause() {
        super.onPause()
        videoController?.onPause()
        binding.videoSurfaceView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        videoController?.onDestroy()
        videoController = null
    }

    private fun requestStoragePermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            WRITE_EXTERNAL_STORAGE
        )
    }

    private fun isStoragePermissionNotGranted(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M
            || Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
        ) {
            return true
        }
        val result =
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        return result != PackageManager.PERMISSION_GRANTED
    }

    fun onSelectShader(shader: ShaderInterface) {
        binding.videoSurfaceView.setShader(shader)
        binding.intensitySeekBar.isEnabled = false
        binding.intensitySeekBar.progress = 100
    }

    fun onSelectFilter(filter: Filter) {
        binding.videoSurfaceView.setFilter(filter)
        binding.intensitySeekBar.isEnabled = true
        binding.intensitySeekBar.progress = 0
    }

    fun onStartSavingVideo() {
        binding.progress.visibility = View.VISIBLE
    }

    fun onFinishSavingVideo(msg: String) {
        runOnUiThread {
            binding.progress.visibility = View.GONE
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }
    }

    fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    override fun onClickFilter(pos: Int) {


        when (shaders.getShader(pos)){

            is ShaderInterface -> {
                Log.w("dataa","running interface")
                showToast("shader")
                filter = NoEffectFilter()
                onSelectShader(shaders.getShader(pos) as ShaderInterface)
               // activity?.onSelectShader(shader)
            }
            is Filter -> {
                showToast("filter")
                filter = shaders.getShader(pos) as Filter
                onSelectFilter(filter)
            }
            else -> return

        }

     //   Toast.makeText(this@VideoActivity, "filter clicked "+list.get(pos).name, Toast.LENGTH_SHORT).show()
        //do your filter stuff

    }
}