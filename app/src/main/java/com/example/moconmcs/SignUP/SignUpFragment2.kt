package com.example.moconmcs.SignUP

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.moconmcs.Onboarding.OnboardingActivity
import com.example.moconmcs.R
import com.example.moconmcs.data.FirebaseDb.User
import com.example.moconmcs.databinding.FragmentSignUp2Binding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class SignUpFragment2 : Fragment() {

    private lateinit var binding: FragmentSignUp2Binding

    private lateinit var email : String
    private lateinit var pw: String
    private lateinit var name: String
    private lateinit var userKind :String
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var userUid : String
    private lateinit var viewModel: SignUpViewModel
    private lateinit var activity: SignupActivity

    fun saveData(email : String, pw : String, name : String){
        this.email = email
        this.pw = pw
        this.name = name
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sign_up2, container, false)
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()

        userUid= firebaseAuth.currentUser!!.uid


        binding.btn1.visibility = View.INVISIBLE
        binding.backBtn.setOnClickListener {
            activity.changeFragment(0)
        }
        binding.viganGroup.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId){
                R.id.vigan-> {userKind = "비건"
                    binding.btn1.visibility = View.VISIBLE}
                R.id.ovo -> {
                    userKind = "오보"
                    binding.btn1.visibility = View.VISIBLE
                }
                R.id.lacto -> {
                    userKind = "락토"
                    binding.btn1.visibility = View.VISIBLE
                }
                R.id.lactoOvo -> {
                    userKind = "락토오보"
                    binding.btn1.visibility = View.VISIBLE
                }
            }
        }

//        (activity as AppCompatActivity?)!!.supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)

        binding.btn1.setOnClickListener {
            firebaseAuth.createUserWithEmailAndPassword(viewModel.email.value.toString(), viewModel.pw.value.toString())
                .addOnFailureListener {
                    Toast.makeText(context, "실패하였습니다. 다시 시도 해주세요", Toast.LENGTH_SHORT).show()
                }
                .addOnCompleteListener{
                    if (it.isSuccessful){
                        firebaseFirestore.collection("User").document(userUid).set(User(name, userKind))
                            .addOnFailureListener {
                                Toast.makeText(context, "유저등록에 실패하였습니다. 다시시도해주세요.", Toast.LENGTH_SHORT).show()
                            }
                            .addOnSuccessListener {
                                Toast.makeText(context, "회원가입에 성공하였습니다.", Toast.LENGTH_SHORT).show()
                                val intent: Intent = Intent(context, OnboardingActivity::class.java)
                                intent.putExtra("isLoginBack", true);
                                startActivity(intent)
                                activity.finish()
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
        Toast.makeText(context, viewModel.email.value.toString(), Toast.LENGTH_SHORT).show()
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

