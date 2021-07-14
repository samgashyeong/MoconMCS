package com.example.moconmcs.Dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.example.moconmcs.R

class CommDialog(
    context: Context,
    commDialogInterface: CommDialogInterface
    , mainMessage: String
    , message: String, )
    : Dialog(context) {

    private var mainMessage: String
    private var message: String

    private var commDialoginterface : CommDialogInterface

    init {
        this.commDialoginterface = commDialogInterface
        this.mainMessage = mainMessage
        this.message = message
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_common_dialog)

        findViewById<TextView>(R.id.mainMessageTv).text = mainMessage
        findViewById<TextView>(R.id.messageTv).text = message

        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        findViewById<TextView>(R.id.checkBtn).setOnClickListener {
            this.commDialoginterface!!.onCheckBtnClick()
        }
        findViewById<TextView>(R.id.cancleBtn).setOnClickListener {
            this.commDialoginterface.onCancleBtnClick()
        }
    }
}