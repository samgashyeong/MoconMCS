package com.example.moconmcs.Dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.example.moconmcs.Main.SearchFood.FoodResultActivity
import com.example.moconmcs.R

class ErrorDialog(
    context: Context,
    errorDialogInterface: ErrorDialogInterface
    , mainMessage: String
    , message: String, )
                : Dialog(context) {

    private var mainMessage: String
    private var message: String

    private var errorDialogInterface: ErrorDialogInterface

    init {
        this.errorDialogInterface = errorDialogInterface
        this.mainMessage = mainMessage
        this.message = message
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_food_result_error)

        findViewById<TextView>(R.id.mainMessageTv).text = mainMessage
        findViewById<TextView>(R.id.messageTv).text = message

        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        findViewById<TextView>(R.id.checkBtn).setOnClickListener {
            this.errorDialogInterface!!.onCheckBtnClick()
        }


    }
}