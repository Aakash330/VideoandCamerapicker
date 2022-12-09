package com.picker.gallery.view

import android.Manifest

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentUris
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity


import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.aakash.videobasesamplegpuv.MainActivity
import com.aakash.videobasesamplegpuv.PortraitCameraActivity

import com.google.android.material.tabs.TabLayout

import com.picker.gallery.R
import kotlinx.android.synthetic.main.activity_picker.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

import java.text.SimpleDateFormat
import java.util.*
import kotlin.Exception

class PickerActivity : AppCompatActivity() {

    private val PERMISSIONS_CAMERA = 124
    var IMAGES_THRESHOLD = 0
    var VIDEOS_THRESHOLD = 0
    var REQUEST_RESULT_CODE = 101
    var openVideoOrCamera = false

    //for taking image from camera
    val takeimageFromCamera: ActivityResultLauncher<Intent?>? = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        object : ActivityResultCallback<ActivityResult?> {
            override fun onActivityResult(result: ActivityResult?) {
                if( result!!.resultCode == AppCompatActivity.RESULT_OK && result.data!=null)
                {
                    //from data h
                 //   setImageData(result.data,1)
                }

            }
        })

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picker)

        val i = intent
        IMAGES_THRESHOLD = i.getIntExtra("IMAGES_LIMIT", 0)
        VIDEOS_THRESHOLD = i.getIntExtra("VIDEOS_LIMIT", 0)
        REQUEST_RESULT_CODE = i.getIntExtra("REQUEST_RESULT_CODE", 0)

        setUpViewPager(viewpager)
        tabs.setupWithViewPager(viewpager)
        setupTabIcons()
        tabs.getTabAt(0)?.setIcon(selectedTabIcons[0])
        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.setIcon(tabIconList[tab.position])
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                if(tab!!.position==1)
                {
                    openVideoOrCamera=true //that menas open the video camera activity
                }else
                {
                    openVideoOrCamera=false
                }
                tab?.setIcon(selectedTabIcons[tab.position])

            }

        })

        camera.setOnClickListener {
            if(openVideoOrCamera)
            {
                startActivity(Intent(this@PickerActivity, PortraitCameraActivity::class.java))
                finish()
                return@setOnClickListener
            }
            if (isCameraPermitted()){
                dispatchTakePictureIntent()
                Toast.makeText(this@PickerActivity, "permissionn granted", Toast.LENGTH_SHORT).show()
            }

            else{
                Toast.makeText(this@PickerActivity, "permission dney", Toast.LENGTH_SHORT).show()

                checkCameraPermission()

            }



        /*    Toast.makeText(this@PickerActivity, "image clicked", Toast.LENGTH_SHORT).show()
            try {
              *//*   var launchIntent =
                getPackageManager().getLaunchIntentForPackage("com.aakash.imageandvideopicker");
                if (launchIntent != null) {
                    startActivity(
                        launchIntent) //null pointer check in case package name was not found ClassNotFoundException
                }
              *//*


            } catch (e:Exception) {
                e.printStackTrace()
            }*/
          //  startActivity(Intent(this@PickerActivity, Class.forName(" com.aakash.imageandvideopicker.MainActivity")))

        }
    }

    private fun isCameraPermitted(): Boolean {
        val permission = android.Manifest.permission.CAMERA
        val cameraPermission = checkCallingOrSelfPermission(permission)
        return (cameraPermission == PackageManager.PERMISSION_GRANTED)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun checkCameraPermission(): Boolean {
        requestPermissions(arrayOf(Manifest.permission.CAMERA), PERMISSIONS_CAMERA)
        return true
    }

    val REQUEST_TAKE_PHOTO = 1
    private fun dispatchTakePictureIntent() {

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        //this i am passing the intent but it may be image size issue
        takeimageFromCamera!!.launch(intent)

        var photoFile: File? = null
        try {
            photoFile = createImageFile()
        } catch (ex: IOException) {
        }
        if (photoFile != null) {
            val photoURI = FileProvider.getUriForFile(this, "com.picker.gallery.fileprovider", photoFile)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)

        }

       /* val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            var photoFile: File? = null
            try {
                photoFile = createImageFile()
            } catch (ex: IOException) {
            }
            if (photoFile != null) {
                val photoURI = FileProvider.getUriForFile(this, "com.picker.gallery.fileprovider", photoFile)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)

               startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
            }
        }*/
    }

    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(imageFileName, ".jpg", storageDir)
         mCurrentPhotoPath = image.absolutePath
        return image
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) galleryAddPic()
    }

    private var mCurrentPhotoPath: String = ""

    private fun galleryAddPic() {
        val f = File(mCurrentPhotoPath)
        val contentUri = Uri.fromFile(f)
        val path = "${Environment.getExternalStorageDirectory()}${File.separator}Zoho Social${File.separator}media${File.separator}Zoho Social Images"
        val folder = File(path)
        if (!folder.exists()) folder.mkdirs()
        val file = File(path, "${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}_picture.jpg")
        val out = FileOutputStream(file)
        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, contentUri)
        val ei = ExifInterface(mCurrentPhotoPath)
        val orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)
        val rotatedBitmap: Bitmap? = when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap, 90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap, 180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap, 270f)
            ExifInterface.ORIENTATION_NORMAL -> bitmap
            else -> null
        }
        rotatedBitmap?.compress(Bitmap.CompressFormat.JPEG, 70, out)
        out.close()
        ContentUris.parseId(Uri.parse(MediaStore.Images.Media.insertImage(contentResolver, file.absolutePath, file.name, file.name)))
        try {
            viewpager.currentItem = 0
            ((viewpager.adapter as ViewPagerAdapter).mFragmentList[0] as? PhotosFragment)?.initViews()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun rotateImage(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }


    val tabIconList: ArrayList<Int> = ArrayList()
    private val tabIcons = intArrayOf(R.drawable.ic_picker_photos_unselected,
            R.drawable.ic_video_unselected)

    private val selectedTabIcons = intArrayOf(R.drawable.ic_picker_photos_selected,
            R.drawable.ic_video_selected)

    private fun setupTabIcons() {
        tabIconList.add(tabIcons[0])
        tabIconList.add(tabIcons[1])
        tabs.getTabAt(0)?.setIcon(tabIconList[0])
        tabs.getTabAt(1)?.setIcon(tabIconList[1])

        for (i in 0 until tabs.tabCount) {
            val tab = tabs.getTabAt(i)
            tab?.setCustomView(R.layout.tab_icon)
        }
    }

    private fun setUpViewPager(viewPager: ViewPager) {
         val adapter = ViewPagerAdapter(this@PickerActivity.supportFragmentManager)
         val photosFragment = PhotosFragment()
         adapter.addFragment(photosFragment, "PHOTOS")
         val videosFragment = VideosFragment()
         adapter.addFragment(videosFragment, "VIDEOS")
         viewPager.adapter = adapter
         (viewPager.adapter as ViewPagerAdapter).notifyDataSetChanged()
         viewPager.currentItem = 0
    }

    internal inner class ViewPagerAdapter(manager: FragmentManager) : FragmentStatePagerAdapter(manager) {
        val mFragmentList: ArrayList<Fragment> = ArrayList()
        val mFragmentTitleList: ArrayList<String> = ArrayList()

        override fun getItem(position: Int): Fragment = mFragmentList[position]

        override fun getCount(): Int = mFragmentList.size

        fun addFragment(fragment: Fragment, title: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        override fun getItemPosition(`object`: Any): Int = POSITION_NONE

        override fun getPageTitle(position: Int): CharSequence? = null
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onResume() {
        if (!isCameraPermitted())
             checkCameraPermission()
        super.onResume()
    }
}
