package com.example.moconmcs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.moconmcs.databinding.FragmentSignUp1Binding

class SignUpFragment1 : Fragment() {

    private lateinit var binding : FragmentSignUp1Binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_sign_up1, container, false)


        return binding.root
    }
}