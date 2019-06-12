package jp.techacademy.rie.ijichi.autoslideshowapp

import android.content.pm.PackageManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.Manifest
import android.content.ContentUris
import android.provider.MediaStore
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val REQUEST_CODE_PERMISSION = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                getImage()

            }else{
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),REQUEST_CODE_PERMISSION)
            }
        }else{
            getImage()
        }

        next_button.setOnClickListener {
            getImage()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            REQUEST_CODE_PERMISSION ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //許可ボタンおされた時にしたい処理
                    getImage()
                }else{
                    //許可されなかった
                }
        }
    }

    private fun getImage(){
        contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,null,null,null,null)?.let {
            if (it.moveToNext()){
                val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,it.getLong(it.getColumnIndex(MediaStore.Images.Media._ID)))
                Log.d("ANDROID", "URI : " + imageUri.toString())
                imageView.setImageURI(imageUri)
            }
            it.close()
        }
    }


}
