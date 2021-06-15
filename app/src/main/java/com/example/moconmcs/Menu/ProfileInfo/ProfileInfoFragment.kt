package com.example.moconmcs.Menu.ProfileInfo

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.moconmcs.R
import com.example.moconmcs.databinding.FragmentChangeUserInfoBinding
import com.example.moconmcs.databinding.FragmentProfileInfoBinding

class ProfileInfoFragment : Fragment() {
    private lateinit var activity: ProfileInfoActivity

    private lateinit var binding: FragmentProfileInfoBinding
    private lateinit var vM : ProfileChangeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_profile_info, container, false)

        vM = ViewModelProvider(requireActivity()).get(ProfileChangeViewModel::class.java)



        binding.btn.setOnClickListener {
            activity.change(2)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vM.userKind.observe(requireActivity(), Observer {
            binding.myKindTv.text = vM.userKind.value
        })
        vM.userEmail.observe(requireActivity(), Observer {
            binding.myEmailTv.text = vM.userEmail.value
        })
        vM.userName.observe(requireActivity(), Observer {
            binding.myNameTv.text = vM.userName.value
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