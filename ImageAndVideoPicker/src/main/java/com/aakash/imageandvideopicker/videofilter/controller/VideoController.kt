package com.aakash.imageandvideopicker.videofilter.controller

import android.content.res.AssetFileDescriptor
import android.media.MediaPlayer
import android.os.Build
import android.os.Environment
import android.util.Log
import android.widget.SeekBar
import androidx.annotation.RequiresApi
import com.aakash.imageandvideopicker.videofilter.VideoActivity
import com.aakash.imageandvideopicker.videofilter.filter.AutoFixFilter
import com.aakash.imageandvideopicker.videofilter.filter.GrainFilter
import com.aakash.imageandvideopicker.videofilter.filter.HueFilter
import com.aakash.imageandvideopicker.videofilter.filter.NoEffectFilter
import com.aakash.imageandvideopicker.videofilter.interfaces.*
import com.aakash.imageandvideopicker.videofilter.model.AssetsConverter
import com.aakash.imageandvideopicker.videofilter.model.AssetsMetadataExtractor
import com.aakash.imageandvideopicker.videofilter.model.Metadata
import com.aakash.imageandvideopicker.videofilter.transformAutofix
import com.aakash.imageandvideopicker.videofilter.transformGrain
import com.aakash.imageandvideopicker.videofilter.transformHue
import com.aakash.imageandvideopicker.videofilter.view.ShaderChooserDialog
import java.io.File

class VideoController(private var activity: VideoActivity?,
                     private val filename: String) {

    companion object {
        private const val TAG = "kVideoController"
    }

    private var filter: Filter = NoEffectFilter()

    /*private var assetFileDescriptor: AssetFileDescriptor = activity?.assets?.openFd(filename)
            ?: throw RuntimeException("Asset not found")*/


    private var mediaPlayer: MediaPlayer = MediaPlayer()
  private var metadata: Metadata? = AssetsMetadataExtractor().extract(filename)

    private val intensityChangeListener: ProgressChangeListener = object : ProgressChangeListener() {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            filter.run {
                when (this) {
                    is GrainFilter -> setIntensity(transformGrain(progress))
                    is HueFilter -> setIntensity(transformHue(progress))
                    is AutoFixFilter -> setIntensity(transformAutofix(progress))
                    else -> activity?.showToast("Changing intensity not implemented for selected effect in this demo")
                }
            }
        }
    }

    init {
        setupMediaPlayer()
        setupView()
    }

    private fun setupMediaPlayer() {
        mediaPlayer.isLooping = true
        mediaPlayer.setDataSource(filename);
       /* mediaPlayer.setDataSource(
                assetFileDescriptor.fileDescriptor,
                assetFileDescriptor.startOffset,
                assetFileDescriptor.length
        )*/

        // https://developer.android.com/reference/android/media/MediaPlayer.OnErrorListener
        mediaPlayer.setOnErrorListener { _, what, extra ->
            Log.d(TAG, "OnError! What: $what; Extra: $extra")
            false
        }

        mediaPlayer.setOnCompletionListener {
            Log.d(TAG, "OnComplete!")
        }
    }

    private fun setupView() {
        val metadata = this.metadata
        val activity = this.activity
        if (metadata != null && activity != null) {
            activity.setupVideoSurfaceView(mediaPlayer, metadata.width, metadata.height)
            activity.setupSeekBar(intensityChangeListener)
        }
    }

    fun chooseShader() {
        val videoWidth = metadata?.width?.toInt() ?: return
        val videoHeight = metadata?.height?.toInt() ?: return

        val dialog = ShaderChooserDialog.newInstance(videoWidth, videoHeight)

        dialog.setListener(object : OnSelectShaderListener {
            override fun onSelectShader(shader: Any) {
                when (shader) {
                    is ShaderInterface -> {
                        filter = NoEffectFilter()
                        activity?.onSelectShader(shader)
                    }
                    is Filter -> {
                        filter = shader
                        activity?.onSelectFilter(shader)
                    }
                    else -> return
                }
            }
        })

        activity?.let {
            dialog.show(it.supportFragmentManager, ShaderChooserDialog::class.java.simpleName)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
   /* fun saveVideo() {

        if (filter is NoEffectFilter) {
            activity?.showToast("Saving will work only with Filters.")
        }

        val parent = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                ?: throw RuntimeException("Activity is destroyed!")
        val child = "out.mp4"
        val outPath = File(parent, child).toString()
        val assetConverterThread = AssetConverterThread(
                AssetsConverter(filename),
                filter,
                outPath,
                object : ConvertResultListener {

                    override fun onSuccess() {
                        activity?.onFinishSavingVideo("Video successfully saved at $outPath")
                    }

                    override fun onFail() {
                        activity?.onFinishSavingVideo("Video wasn't saved. Check log for details.")
                    }
                }
        )

        activity?.onStartSavingVideo()
        assetConverterThread.start()
    }*/

    fun onPause() {
        mediaPlayer.pause()
    }

    fun onDestroy() {
        mediaPlayer.stop()
        mediaPlayer.release()
       // assetFileDescriptor.close()
        activity = null
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private class AssetConverterThread(private var assetsConverter: AssetsConverter,
                                       private var filter: Filter,
                                       private var outPath: String,
                                       private var listener: ConvertResultListener
    ) : Thread() {

        override fun run() {
            super.run()
            assetsConverter.startConverter(filter, outPath, listener)
        }
    }
}