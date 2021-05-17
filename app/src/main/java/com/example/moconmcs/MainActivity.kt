package com.example.moconmcs

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    val btn1 = findViewById<Button>(R.id.btn1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val  a = 1
        val b= 2
        var c = a+b

        btn1.setOnClickListener {
            Toast.makeText(this, "이준상이 만든 버튼임.", Toast.LENGTH_SHORT).show()
        }
    }
}