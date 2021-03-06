package com.example.moconmcs.Main.BottomSheet

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.moconmcs.R
import com.example.moconmcs.databinding.CheckBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetDialog: BottomSheetDialogFragment() {
    private lateinit var bottomSheetDialogListener: BottomSheetButtonClickListener
    lateinit var binding: CheckBottomSheetBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,
            R.layout.check_bottom_sheet, container, false)
        return binding.root
    }


    override fun onAttach(activity: Activity) { //선언
        super.onAttach(activity)
        bottomSheetDialogListener = context as BottomSheetButtonClickListener
//        try{
//        }catch (e: ClassCastException){
//            Toast.makeText(getActivity(), "에러가 발생하였습니다. 다음에 다시 시도해주세요", Toast.LENGTH_SHORT).show()
//        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) { //눌러서 Activity한테 데이터 전달
        super.onActivityCreated(savedInstanceState)
        binding.BarCodeClick.setOnClickListener {
            bottomSheetDialogListener.layoutClick(1)
        }
        binding.NumClick.setOnClickListener {
            bottomSheetDialogListener.layoutClick(2)
        }
        binding.prevResultTv.setOnClickListener {
            bottomSheetDialogListener.layoutClick(3)
        }
    }
}