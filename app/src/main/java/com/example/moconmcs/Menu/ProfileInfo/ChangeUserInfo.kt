package com.example.moconmcs.Menu.ProfileInfo

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.moconmcs.Dialog.CommDialog
import com.example.moconmcs.Dialog.CommDialogInterface
import com.example.moconmcs.Main.AppDatabase
import com.example.moconmcs.Main.SearchFood.db.FoodListEntity
import com.example.moconmcs.R
import com.example.moconmcs.SignUP.SignupActivity
import com.example.moconmcs.databinding.FragmentChangeUserInfoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ChangeUserInfo : Fragment(), CommDialogInterface {
    private lateinit var binding: FragmentChangeUserInfoBinding

    private lateinit var activity: ProfileInfoActivity

    private lateinit var vM : ProfileChangeViewModel

    private lateinit var auth: FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private lateinit var userKind : String
    private lateinit var room : AppDatabase
    private lateinit var foodListData : List<FoodListEntity>
    private lateinit var dialog: CommDialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        room = AppDatabase.getInstance(this.requireContext())
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_change_user__info, container, false)

        vM = ViewModelProvider(requireActivity()).get(ProfileChangeViewModel::class.java)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        foodListData = room.foodListDao().getAll()!!
//        dialog = CommDialog(requireContext(), this, "식품 이전기록 삭제", "비건단계의 정보를 변경하시면 식품 이전기록 정보가 삭제됩니다.\n정보를 변경하시겠습니까?")
        val items = resources.getStringArray(R.array.my_array_change)
        val myAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.my_array_change, R.layout.spinner_layout)
        myAdapter.setDropDownViewResource(R.layout.spinner_layout)
        val curUser = auth.currentUser
        val curUid = curUser!!.uid

        binding.spinner.adapter = myAdapter

        when(vM.userKind.value){
            "비건" -> {
                binding.spinner.setSelection(3)
                Log.d(TAG, "onCreateView: 비건 실행됨")
            }
            "락토" -> {
                binding.spinner.setSelection(2)
                Log.d(TAG, "onCreateView: 락토 실행됨")
            }
            "오보" -> {
                binding.spinner.setSelection(1)
                Log.d(TAG, "onCreateView: 오보 실행됨")
            }
            "락토오보" -> {
                binding.spinner.setSelection(0)
                Log.d(TAG, "onCreateView: 락토오보 실행됨")
            }
        }

        binding.spinner.onItemSelectedListener= object : AdapterView.OnItemSelectedListener,
            AdapterView.OnItemClickListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {

                //아이템이 클릭 되면 맨 위부터 position 0번부터 순서대로 동작하게 됩니다.
                when (position) {
                    0 -> {
                        userKind = "락토오보"
                    }
                    1 -> {
                        userKind = "오보"
                    }
                    2 ->{
                        userKind = "락토"
                    }
                    3->{
                        userKind = "비건"
                    }
                    else -> {
                        userKind = "1"
                    }
                }
            }
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
            }
        }

        binding.changeBtn.setOnClickListener {
//            if(vM.userKind.value != userKind){
//                dialog.show()
//            }
//            else{
//                changeUser()
//            }
            changeUser()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vM.userName.observe(requireActivity(), Observer {
            binding.myNameTv.setText(vM.userName.value)
        })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = getActivity() as ProfileInfoActivity
    }

    override fun onDetach() {
        super.onDetach()
        activity.onDetachedFromWindow()
    }

    override fun onCheckBtnClick() {
        changeUser()
    }

    private fun changeUser() {
        db.collection("User").document(auth.currentUser!!.uid).update("name", binding.myNameTv.text.toString()
            , "userKind", userKind)
            .addOnCompleteListener {
                if(it.isSuccessful){
//                    if(vM.userKind.value != userKind){
//                        for (i in foodListData){
//                            room.foodListDao().delete(i)
//                        }
//                    }
                    vM.userName.value = binding.myNameTv.text.toString()
                    vM.userKind.value = userKind
                    Toast.makeText(activity, "유저정보변경을 완료하였습니다.", Toast.LENGTH_SHORT).show()
                    activity.change(1)
                }
                else{
                    Toast.makeText(activity, "오류가 발생하였습니다. 다시시도해주세요.", Toast.LENGTH_SHORT).show()
                }
            }
        activity.change(1)
        //dialog.dismiss()
    }

    override fun onCancleBtnClick() {
        dialog.dismiss()
    }
}