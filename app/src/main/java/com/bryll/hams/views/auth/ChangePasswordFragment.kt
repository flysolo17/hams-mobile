package com.bryll.hams.views.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bryll.hams.R
import com.bryll.hams.databinding.FragmentChangePasswordBinding
import com.bryll.hams.databinding.FragmentForgotPasswordBinding
import com.bryll.hams.services.AuthServiceImpl
import com.bryll.hams.utils.LoadingDialog
import com.bryll.hams.utils.UiState
import com.bryll.hams.viewmodels.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class ChangePasswordFragment : Fragment() {
    private lateinit var binding : FragmentChangePasswordBinding
    private val authViewModel: AuthViewModel by viewModels {    AuthViewModel.provideFactory(
        AuthServiceImpl(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance()), this)}
    private lateinit var loadingDialog : LoadingDialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentChangePasswordBinding.inflate(inflater,container,false)
        loadingDialog = LoadingDialog(binding.root.context)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonChangePassword.setOnClickListener {
            val old = binding.inputOldPassword.text.toString()
            val new = binding.inputNewPassword.text.toString()
            val confirm = binding.inputConfirmPassword.text.toString()
            if (old.isEmpty()) {
                binding.layoutOldPassword.error = "Invalid Password"
            } else if (new.isEmpty()) {
                binding.layoutNewPassword.error = "Invalid Password"
            } else if (new != confirm) {
                binding.layoutConfirmPassword.error = "Password does not match"
            } else {
                FirebaseAuth.getInstance().currentUser?.let {
                    authViewModel.reauthenticate(it,it.email!!,old)
                }
            }
        }
        observer()
    }
    private fun observer() {
        authViewModel.changePassword.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.onFailed -> {
                    loadingDialog.closeDialog()
                    Toast.makeText(binding.root.context,it.message, Toast.LENGTH_SHORT).show()
                }
                UiState.onLoading -> {
                    loadingDialog.showDialog("Changing password....")
                }
                is UiState.onSuccess -> {
                    loadingDialog.closeDialog()
                    Toast.makeText(binding.root.context,it.data,Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
            }
        }
        authViewModel.reauthenticate.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.onFailed -> {
                    loadingDialog.closeDialog()
                    Toast.makeText(binding.root.context,it.message, Toast.LENGTH_SHORT).show()
                }
                UiState.onLoading -> {
                    loadingDialog.showDialog("Authenticating....")
                }
                is UiState.onSuccess -> {
                    loadingDialog.closeDialog()
                    authViewModel.changePassword(it.data, password = binding.inputNewPassword.text.toString())
                }
            }
        }
    }
}