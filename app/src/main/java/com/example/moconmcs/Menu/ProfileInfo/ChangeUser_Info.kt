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
import com.example.moconmcs.R
import com.example.moconmcs.SignUP.SignupActivity
import com.example.moconmcs.databinding.FragmentChangeUserInfoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ChangeUser_Info : Fragment() {
    private lateinit var binding: FragmentChangeUserInfoBinding

    private lateinit var activity: ProfileInfoActivity

    private lateinit var vM : ProfileChangeViewModel

    private lateinit var auth: FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private lateinit var userKind : String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_change_user__info, container, false)

        vM = ViewModelProvider(requireActivity()).get(ProfileChangeViewModel::class.java)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

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
            db.collection("User").document(curUid).update("name", binding.myNameTv.text.toString()
                , "userKind", userKind)
                .addOnCompleteListener {
                    if(it.isSuccessful){
                        vM.userName.value = binding.myNameTv.text.toString()
                        vM.userKind.value = userKind
                        Toast.makeText(requireContext(), "유저정보변경을 완료하였습니다.", Toast.LENGTH_SHORT).show()
                        activity.change(1)
                    }
                    else{
                        Toast.makeText(requireContext(), "오류가 발생하였습니다. 다시시도해주세요.", Toast.LENGTH_SHORT).show()
                    }
                }
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
}