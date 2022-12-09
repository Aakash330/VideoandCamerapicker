package com.aakash.imageandvideopicker

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaPlayer.OnPreparedListener
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import android.widget.MediaController
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.aakash.imageandvideopicker.databinding.ActivityCameraPickfromGalleryBinding
import com.aakash.imageandvideopicker.trimvideo.CompressOption
import com.aakash.imageandvideopicker.trimvideo.LogMessage
import com.aakash.imageandvideopicker.trimvideo.TrimVideo
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.picker.gallery.model.GalleryData
import com.picker.gallery.model.interactor.GalleryPicker
import com.picker.gallery.utils.MLog
import com.picker.gallery.view.PickerActivity
import java.io.File


class CameraPickfromGallery : AppCompatActivity() {
    private lateinit var binding:ActivityCameraPickfromGalleryBinding
    private val PERMISSIONS_READ_WRITE = 123
    private lateinit var playerView:PlayerView
    private lateinit var simpleExoPlayer:ExoPlayer
    private lateinit var trackSelector: DefaultTrackSelector
    private lateinit var mFrameLayout:FrameLayout
    private lateinit var builder: AlertDialog.Builder
    private lateinit var  mediaController:MediaController
    private lateinit var path: String

    val REQUEST_RESULT_CODE = 101



    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_camera_pickfrom_gallery)
        if (isReadWritePermitted()) getGalleryResults() else checkReadWritePermission()
        val i = Intent(this@CameraPickfromGallery, PickerActivity::class.java)
        i.putExtra("IMAGES_LIMIT", 4)
        i.putExtra("VIDEOS_LIMIT", 3)
        i.putExtra("REQUEST_RESULT_CODE", REQUEST_RESULT_CODE)
        startActivityForResult(i, 101)
       // setContentView(R.layout.activity_camera_pickfrom_gallery)

        binding.btnPlaynext.setOnClickListener {
            if (checkCamStoragePer())
                openTrimActivity(path)

        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == REQUEST_RESULT_CODE && data != null) {
            val mediaList = data.getParcelableArrayListExtra<GalleryData>("MEDIA")
            MLog.e("SELECTED MEDIA", mediaList!!.get(0).photoUri)
            inilizetheALlVideoView()
            path=mediaList!!.get(0).photoUri
            playthevideoView(path)
            checkCamStoragePer()

           // playDashVideo(mediaList!!.get(0).photoUri)
            Toast.makeText(this@CameraPickfromGallery, ""+ mediaList!!.get(0).photoUri, Toast.LENGTH_SHORT).show()
        }
    }

    fun getGalleryResults() {
        val images = GalleryPicker(this).getImages()
        val videos = GalleryPicker(this).getVideos()
      //  text.text = "IMAGES COUNT: ${images.size}\nVIDEOS COUNT: ${videos.size}"
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun checkReadWritePermission(): Boolean {
        requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSIONS_READ_WRITE)
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSIONS_READ_WRITE -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) getGalleryResults()
        }
    }

    private fun isReadWritePermitted(): Boolean = (checkCallingOrSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && checkCallingOrSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)

    override fun onResume() {


        /* Intent launchIntent =
                   getPackageManager().getLaunchIntentForPackage("com.aakash.videoandcamera");
                if (launchIntent != null) {
                    startActivity(
                        launchIntent); //null pointer check in case package name was not found ClassNotFoundException
                }*/
        val sharedPreferences: SharedPreferences
        sharedPreferences = getSharedPreferences("com.aakash.imageandvideopicker", MODE_PRIVATE)
       // sharedPreferences.edit().putString("path")
        val string=sharedPreferences.getString("path","Akash")
        Log.w("runinng","path"+string)
        super.onResume()
    }


    fun inilizetheALlVideoView()

    {
        try {
            trackSelector = DefaultTrackSelector(this)
            simpleExoPlayer = SimpleExoPlayer.Builder(this).setTrackSelector(trackSelector).build()
            playerView = findViewById<PlayerView>(R.id.exoPlayerView)
            mFrameLayout = findViewById(R.id.fram)
            playerView.player=simpleExoPlayer//here is SimplaeExoplyer set in player view

        }catch (e:Exception)
        {
            println("CameraPickfromGallery.inilizetheALlVideoView${e.message}")
        }

        //all controller widget
        //all controller widget
    /*    farwordBtn = playerView.findViewById<ImageView>(R.id.fwd)
        rewBtn = playerView.findViewById<ImageView>(R.id.rew)
        setting = playerView.findViewById<ImageView>(R.id.exo_track_selection_view)
        speedBtn = playerView.findViewById<ImageView>(R.id.exo_playback_speed)
        speedTxt = playerView.findViewById<TextView>(R.id.speed)
        fullscreenButton = playerView.findViewById<ImageView>(R.id.fullscreen)*/
    }
  //play the video

  /*fun  playDashVideo(path:String) {


// Create a data source factory.


// Create a data source factory.
    *//*  val dataSourceFactory: DataSource.Factory = DefaultHttpDataSource.Factory()
          // Create a DASH media source pointing to a DASH manifest uri.
      // Create a DASH media source pointing to a DASH manifest uri.
      val mediaSource: MediaSource = DashMediaSource.Factory(dataSourceFactory)
          .createMediaSource(MediaItem.fromUri(Uri.fromFile(File(path))))*//*



      //

      //val playerInfo: String = Util.getUserAgent(context, "ExoPlayerInfo")
      val dataSourceFactory = DefaultDataSourceFactory(
          this@CameraPickfromGallery, path
      )
      val mediaSource=ProgressiveMediaSource.Factory(dataSourceFactory)
          .createMediaSource()
      *//*val mediaSource: MediaSource = DefaultDataSource
          DataSource.Factory
          .createMediaSource(Uri.parse(path))*//*
      //simpleExoPlayer.prepare(mediaSource)


     *//* val videoSource = ExtractorMediaSource.Factory(dataSourceFactory).
      createMediaSource(Uri.fromFile(File(path)))*//*
// Create a player instance.



// Set the media source to be played.
      Toast.makeText(this@CameraPickfromGallery, "dash url run", Toast.LENGTH_SHORT).show()
    //  Log.d(com.codingsick.maneet.Play_NormalVideos.TAG, "playDashVideo:$url1")
      if (simpleExoPlayer.isPlaying()) {
          val t: Long = simpleExoPlayer.getCurrentPosition()
          // simpleExoPlayer.pause();
          simpleExoPlayer.setMediaSource(mediaSource)
          binding.progressBar.setVisibility(View.GONE)
          binding.exoPlayerView.showController()
          simpleExoPlayer.seekTo(t)
          // simpleExoPlayer.play();
      } else {
          simpleExoPlayer.setMediaSource(mediaSource)
          // Prepare the player.
          simpleExoPlayer.prepare()
          simpleExoPlayer.play()
          binding.exoPlayerView.showController()
          binding.progressBar.setVisibility(View.GONE)
      }


  }*/

  fun  playthevideoView(path:String)
  {
      binding.videoview.setVideoPath(path)

      // creating object of
      // media controller class
       mediaController = MediaController(this)

      // sets the anchor view
      // anchor view for the videoView
      mediaController.setAnchorView(binding.videoview)

      // sets the media player to the videoView
      mediaController.setMediaPlayer(binding.videoview)

      // sets the media controller to the videoView
      binding.videoview.setMediaController(mediaController);

      // starts the video
      binding.videoview.start()



  }
//ready the launcher the to start the video trim

    var videoTrimResultLauncher = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK &&
            result.data != null
        ) {
            val uri =
                Uri.parse(TrimVideo.getTrimmedVideoPath(result.data))

            playthevideoView(TrimVideo.getTrimmedVideoPath(result.data))
          /*  binding.videoview.setMediaController(mediaController)
            binding.videoview.setVideoURI(uri)
            binding.videoview.requestFocus()
            binding.videoview.start()
            binding.videoview.setOnPreparedListener(OnPreparedListener { mediaPlayer: MediaPlayer? ->
                mediaController.setAnchorView(
                    binding.videoview
                )
            })*/
            val filepath = uri.toString()
            val file = File(filepath)
            val length = file.length()
            Log.d("videosize"
               ,
                "Video size:: " + length / 1024
            )
        } else LogMessage.v("videoTrimResultLauncher data is null")
    }

    private fun openTrimActivity(data: String) {
        val trimType=0
        if (trimType == 0) {
            TrimVideo.activity(data)
                .setCompressOption(CompressOption()) //pass empty constructor for default compress option
                .start(this, videoTrimResultLauncher)
        }
    }


    private fun checkCamStoragePer(): Boolean {
        return checkPermission(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        )
    }


    private fun checkPermission(vararg permissions: String): Boolean {
        var allPermitted = false
        for (permission in permissions) {
            allPermitted = (ContextCompat.checkSelfPermission(this, permission)
                    == PackageManager.PERMISSION_GRANTED)
            if (!allPermitted) break
        }
        if (allPermitted) return true
        ActivityCompat.requestPermissions(
            this, permissions,
            220
        )
        return false
    }



}