package com.bryll.hams.views.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.bryll.hams.databinding.FragmentForgotPasswordBinding
import com.bryll.hams.utils.LoadingDialog


class ForgotPasswordFragment : DialogFragment() {
    private lateinit var binding : FragmentForgotPasswordBinding
    private lateinit var loadingDialog : LoadingDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL,android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentForgotPasswordBinding.inflate(inflater,container,false)
        loadingDialog = LoadingDialog(binding.root.context)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonBack.setOnClickListener {
            dismiss()
        }
        binding.buttonForgotPassword.setOnClickListener {
            val email = binding.inputEmail.text.toString()
            if (email.isEmpty()) {
                binding.layoutEmail.error = "Invalid Email"
            } else {

            }
        }

    }


}