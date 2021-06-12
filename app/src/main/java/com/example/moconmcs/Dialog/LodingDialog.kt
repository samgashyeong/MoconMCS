package com.example.moconmcs.Dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.TextView
import androidx.databinding.DataBindingUtil.setContentView
import com.example.moconmcs.R

class LodingDialog(
    context: Context
    , message: String)
    : Dialog(context) {
    private var message: String


    init {
        this.message = message
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_loding_dialog)

        findViewById<TextView>(R.id.messageTvLoding).text = message

        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

    }
}