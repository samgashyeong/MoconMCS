package com.example.moconmcs.Main.SearchFood

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.budiyev.android.codescanner.*
import com.example.moconmcs.R
import com.example.moconmcs.databinding.ActivityBarCodeBinding

class BarCodeActivity : AppCompatActivity() {
    private var isIntent : Boolean = false
    private val CAMERA_REQUEST_CODE = 1001
    private lateinit var codeScanner: CodeScanner
    private lateinit var binding: ActivityBarCodeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bar_code)

        binding = DataBindingUtil.setContentView(this,
            R.layout.activity_bar_code
        )

        setPermissions()
        scan()
    }

    fun scan(){
        codeScanner = CodeScanner(this, binding.scanner)

        codeScanner.apply {
            camera = CodeScanner.CAMERA_BACK
            formats = CodeScanner.ALL_FORMATS

            autoFocusMode = AutoFocusMode.SAFE
            scanMode = ScanMode.CONTINUOUS
            isAutoFocusEnabled = true
            isFlashEnabled = false


            decodeCallback = DecodeCallback {
                runOnUiThread{
                    if(!isIntent){
                        isIntent = true
                        startActivity(Intent(this@BarCodeActivity, FoodResultLoading::class.java)
                            .putExtra("barcodenum", it.text))
                        finish()
                    }
                }
            }

            errorCallback = ErrorCallback {
                runOnUiThread{
                    Log.e("응애", "scan: ${it.message}", )
                }
            }
        }

        binding.scanner.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }


    private  fun setPermissions(){
        val permission = ContextCompat.checkSelfPermission(this,
        android.Manifest.permission.CAMERA)

        if(permission != PackageManager.PERMISSION_GRANTED){
            makeRequest()
        }
    }

    private fun makeRequest(){
        ActivityCompat.requestPermissions(this,
        arrayOf(android.Manifest.permission.CAMERA),
        CAMERA_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            CAMERA_REQUEST_CODE ->{
                if(grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "카메라 권한을 켜주세요.", Toast.LENGTH_SHORT).show()
                    val context: Context = applicationContext
                    val intent =
                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(
                            Uri.parse("package:" + context.packageName)
                        )
                    startActivity(intent)
                    finish()
                }
                else{
                    Log.d("ㅁㄴㅇㄹ", "onRequestPermissionsResult: 성공")
                }
            }
        }
    }
}