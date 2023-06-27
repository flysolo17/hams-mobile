package com.bryll.hams.views.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.bryll.hams.R
import com.bryll.hams.databinding.FragmentForgotPasswordBinding
import com.bryll.hams.services.AuthServiceImpl
import com.bryll.hams.utils.LoadingDialog
import com.bryll.hams.utils.UiState
import com.bryll.hams.viewmodels.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage


class ForgotPasswordFragment : DialogFragment() {

    private lateinit var binding : FragmentForgotPasswordBinding
    private val authViewModel: AuthViewModel by viewModels {    AuthViewModel.provideFactory(
        AuthServiceImpl(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance(),
            FirebaseStorage.getInstance()), this)}
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
                authViewModel.forgotPassword(email)
            }
        }
        observers()
    }
    private fun observers() {
        authViewModel.resetPassword.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.onFailed -> {
                    loadingDialog.closeDialog()
                    Toast.makeText(binding.root.context,it.message, Toast.LENGTH_SHORT).show()
                }
                UiState.onLoading -> {
                    loadingDialog.showDialog("Loading ....")
                }
                is UiState.onSuccess -> {
                    loadingDialog.closeDialog().also { e->
                        Toast.makeText(binding.root.context,it.data,Toast.LENGTH_SHORT).show()
                        dismiss()
                    }
                }
            }
        }
    }

}