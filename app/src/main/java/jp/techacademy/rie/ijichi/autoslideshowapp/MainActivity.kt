package jp.techacademy.rie.ijichi.autoslideshowapp

import android.content.pm.PackageManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.Manifest
import android.content.ContentUris
import android.content.DialogInterface
import android.database.Cursor
import android.net.Uri
import android.os.Handler
import android.provider.MediaStore
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private val REQUEST_CODE_PERMISSION = 100
    private var imageCursor: Cursor? = null
    private var mTimer: Timer? = null
    private var mHandler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        stop_button.visibility = View.GONE

        showPermission()

        next_button.setOnClickListener {
            if (imageCursor!!.isLast) {
                getImage(2)
            } else {
                getImage(0)
            }
        }

        back_button.setOnClickListener {
            if (imageCursor!!.isFirst) {
                getImage(3)
            } else {
                getImage(1)
            }
        }

        move_button.setOnClickListener {
            stop_button.visibility = View.VISIBLE
            move_button.visibility = View.GONE
            next_button.visibility = View.GONE
            back_button.visibility = View.GONE
            showSlide()
        }

        stop_button.setOnClickListener {
            stopSlide()
            stop_button.visibility = View.GONE
            move_button.visibility = View.VISIBLE
            next_button.visibility = View.VISIBLE
            back_button.visibility = View.VISIBLE
        }

    }

    private fun showPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                getImage(2)
            } else {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_CODE_PERMISSION)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CODE_PERMISSION ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getImage(2)
                } else {
                    AlertDialog.Builder(this).apply {
                        setMessage("スライドを開始するにはパーミッション許可が必要です。")
                        setPositiveButton("OK") { _, _ ->
                            showPermission()
                        }
                        show()
                    }
                }
        }
    }

    private fun getImage(isOperation: Int) {
        contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, null)?.let {
            if (imageCursor == null)
                imageCursor = it

            val cursorMove = when (isOperation) {
                0 -> imageCursor!!.moveToNext()
                1 -> imageCursor!!.moveToPrevious()
                2 -> imageCursor!!.moveToFirst()
                3 -> imageCursor!!.moveToLast()
                else -> false
            }
            if (cursorMove) {
                val imageUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    imageCursor!!.getLong(it.getColumnIndex(MediaStore.Images.Media._ID))
                )
                Log.d("ANDROID", "URI : " + imageUri.toString())
                imageView.setImageURI(imageUri)
                val position = imageCursor!!.position
                Log.d("ANDROID", "$position")
            }
        }
    }

    private fun showSlide() {
        if (mTimer == null) {
            mTimer = Timer()
            mTimer!!.schedule(object : TimerTask() {
                override fun run() {
                    mHandler.post {
                        getImage(0)
                    }
                }
            }, 2000, 2000)
        }
    }

    private fun stopSlide() {
        if (mTimer != null) {
            mTimer!!.cancel()
            mTimer = null
        }
        if (imageCursor!!.isAfterLast) {
            getImage(3)
        }
    }
}



