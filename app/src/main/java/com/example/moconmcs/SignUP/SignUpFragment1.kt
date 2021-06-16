package com.example.moconmcs.SignUP

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.moconmcs.Hash.sha
import com.example.moconmcs.R
import com.example.moconmcs.databinding.FragmentSignUp1Binding
import java.lang.Exception
import java.math.BigInteger

class SignUpFragment1 : Fragment() {

    private lateinit var binding : FragmentSignUp1Binding
    private lateinit var userViewModel : SignUpViewModel

    private lateinit var email : String
    private lateinit var pw: String
    private lateinit var name: String

    private lateinit var activity: SignupActivity
//
//
//    fun saveData(){
//
//        userViewModel = ViewModelProvider(requireActivity()).get(SignUpViewModel::class.java)
//        userViewModel.setSignUser(binding.emailLogin.text.toString()
//            , binding.pwLogin.text.toString()
//            , binding.nameEt.text.toStringe())
//    }
//    fun saveData(email : String, pw : String, name : String){
//        this.email = email
//        this.pw = pw
//        this.name = name
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_sign_up1, container, false)
        userViewModel = ViewModelProvider(requireActivity()).get(SignUpViewModel::class.java)

        binding.btn1.setOnClickListener {
                 if(binding.emailLogin.text.isEmpty()
                or binding.pwLogin.text.isEmpty()
                or binding.pwLogin2.text.isEmpty()
                or binding.nameEt.text.isEmpty()){
                binding.errorTv.visibility = View.VISIBLE
                binding.errorTv.text = "빈칸을 채워주세요."
            }
                 else if(!binding.emailLogin.text.toString().matches("^[A-Za-z0-9+_.-]+@(.+)\$".toRegex())){
                     Log.d(TAG, "onCreateView: 이메일 형식이 틀림")
                     binding.errorTv.visibility = View.VISIBLE
                     binding.errorTv.text = "이메일 형식을 지켜주세요."
                 }
                 else if(!binding.pwLogin.text.toString()
                         .matches("^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[\$@!%*#?&_]).{8,12}.\$".toRegex())){
                     Log.d(TAG, "onCreateView: 비밀번호 형식이 틀림")
                     binding.errorTv.visibility = View.VISIBLE
                     binding.errorTv.text = "비밀번호 형식을 지켜주세요"
                 }
                 else if(binding.pwLogin2.text.toString() != binding.pwLogin.text.toString()){
                     Log.d(TAG, "onCreateView: 비밀번호가 틀림")
                     binding.errorTv.visibility = View.VISIBLE
                     binding.errorTv.text = "비밀번호가 일치하지않습니다."
                 }
            else if(binding.emailLogin.text.isNotEmpty()
                and binding.pwLogin.text.isNotEmpty()
                and binding.pwLogin2.text.isNotEmpty()
                and binding.nameEt.text.isNotEmpty()){

                     val input : ByteArray = binding.pwLogin2.text.toString().toByteArray()
                     var output : ByteArray = input
                     try{
                         output = sha.encryptSHA(output, "SHA-256")
                     }catch (e: Exception){
                         Log.d(TAG, "onCreateView: ${e}")
                     }
                     val data = BigInteger(1, output)
                userViewModel.setSignUser(binding.emailLogin.text.toString()
                    , binding.pwLogin.text.toString()
                    , binding.nameEt.text.toString()
                ,data.toString(16))
                     Log.d(TAG, "onCreateView: ${userViewModel.hash.value}")
                activity.changeFragment(1)
                Log.d(TAG, "onCreateView: ${binding.emailLogin.text}, ${binding.pwLogin.text}, \n  ${binding.nameEt.text}")
            }
        }
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        userViewModel = ViewModelProvider(requireActivity()).get(SignUpViewModel::class.java)
        activity = getActivity() as SignupActivity
    }

//    fun loadData(fm : SignUpFragment2){
//        fm.saveData(binding.emailLogin.text.toString(), binding.pwLogin.text.toString(), binding.nameEt.text.toString())
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userViewModel.email.observe(requireActivity(), Observer {
            binding.emailLogin.setText(userViewModel.email.value)
        })
        userViewModel.pw.observe(requireActivity(), Observer {
            binding.pwLogin.setText(userViewModel.pw.value)
            binding.pwLogin2.setText(userViewModel.pw.value)
        })
        userViewModel.name.observe(requireActivity(), Observer {
            binding.nameEt.setText(userViewModel.name.value)


        })
    }
    override fun onDetach() {
        super.onDetach()
        activity.onDetachedFromWindow()
    }
}

