package com.example.moconmcs.SignUP

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.moconmcs.Dialog.LodingDialog
import com.example.moconmcs.Onboarding.OnboardingActivity
import com.example.moconmcs.R
import com.example.moconmcs.data.FirebaseDb.User
import com.example.moconmcs.databinding.FragmentSignUp2Binding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class SignUpFragment2 : Fragment(){

    private lateinit var binding: FragmentSignUp2Binding
//    private lateinit var email : String
//    private lateinit var pw: String
//    private lateinit var name: String
    private lateinit var userKind :String
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var userUid : String
    private lateinit var viewModel: SignUpViewModel
    private lateinit var activity: SignupActivity
    private lateinit var dialog : LodingDialog


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sign_up2, container, false)
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()
        dialog = LodingDialog(requireContext(), "계정 만드는 중..")

        binding.backBtn.setOnClickListener {
            activity.changeFragment(0)
        }
        val items = resources.getStringArray(R.array.my_array)
        val myAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.my_array, R.layout.spinner_layout)
        myAdapter.setDropDownViewResource(R.layout.spinner_layout)

        binding.spinner.adapter = myAdapter

        binding.spinner.setSelection(3)

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


        binding.btn1.setOnClickListener {
            if(userKind == "1"){
                Toast.makeText(requireContext(), "비건 단계를 선택해주세요", Toast.LENGTH_SHORT).show()
            }
            else{
                dialog.show()
                firebaseAuth.createUserWithEmailAndPassword(viewModel.email.value.toString(), viewModel.pw.value.toString())
                    .addOnFailureListener {
                        Toast.makeText(context, "실패하였습니다. 다시 시도 해주세요", Toast.LENGTH_SHORT).show()
                        Log.d(TAG, "onCreateView: ${it}")
                        dialog.dismiss()
                    }
                    .addOnCompleteListener{
                        if (it.isSuccessful){
                            userUid = firebaseAuth.currentUser!!.uid
                            firebaseFirestore.collection("User").document(userUid).set(User(viewModel.name.value.toString(), userKind, viewModel.hash.value.toString()))
                                .addOnFailureListener {
                                    dialog.dismiss()
                                    Toast.makeText(context, "유저등록에 실패하였습니다. 다시시도해주세요.", Toast.LENGTH_SHORT).show()
                                }
                                .addOnSuccessListener {
                                    Log.d(TAG, "onCreateView: ${userUid}")
                                    val intent: Intent = Intent(context, OnboardingActivity::class.java)
                                    intent.putExtra("isLoginBack", true)
                                    startActivity(intent)
                                    activity.finish()
                                    activity.changeFragment(3)
                                    dialog.dismiss()
                                }
                        }
                    }
            }
        }

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        viewModel.email.observe(requireActivity(), Observer {
//            viewModel.email
//        })
//        viewModel.pw.observe(requireActivity(), Observer {
//            this.pw = viewModel.pw.value.toString()
//        })
//        viewModel.name.observe(requireActivity(), Observer {
//            this.name = viewModel.name.value.toString()
//        })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = ViewModelProvider(requireActivity()).get(SignUpViewModel::class.java)
        activity = getActivity() as SignupActivity
    }

    override fun onDetach() {
        super.onDetach()
        activity.onDetachedFromWindow()
    }


//    fun loadData(fm : SignUpFragment1){
//        fm.saveData(email, pw, name)
//    }


}

