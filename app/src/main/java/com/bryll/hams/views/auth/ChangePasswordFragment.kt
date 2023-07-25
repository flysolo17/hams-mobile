package com.bryll.hams.views.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bryll.hams.databinding.FragmentChangePasswordBinding
import com.bryll.hams.utils.LoadingDialog


class ChangePasswordFragment : Fragment() {
    private lateinit var binding : FragmentChangePasswordBinding
//    private val authViewModel: AuthViewModel by viewModels {    AuthViewModel.provideFactory(
//        AuthServiceImpl(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance(),
//            FirebaseStorage.getInstance()), this)}
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

            }
        }

    }

}