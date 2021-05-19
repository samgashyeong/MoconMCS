package com.example.moconmcs

import android.content.pm.PackageManager
import android.graphics.Camera
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.example.moconmcs.databinding.ActivityBarCodeBinding
import com.google.android.gms.common.util.DataUtils

class BarCodeActivity : AppCompatActivity() {
    private val CAMERA_REQUEST_CODE = 1001
    private lateinit var codeScanner: CodeScanner
    private lateinit var binding: ActivityBarCodeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bar_code)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_bar_code)

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
                    Log.d("asdf", "scan: ${it.text}")
                    Toast.makeText(this@BarCodeActivity, it.text, Toast.LENGTH_SHORT).show()
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
        when(requestCode){
            CAMERA_REQUEST_CODE ->{
                if(grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "카메라 퍼미션을 켜주십쇼", Toast.LENGTH_SHORT).show()
                }
                else{
                    Log.d("ㅁㄴㅇㄹ", "onRequestPermissionsResult: 성공")
                }
            }
        }

    }
}