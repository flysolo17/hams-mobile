package com.bryll.hams.views.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.bryll.hams.R
import com.bryll.hams.databinding.FragmentLogoutBinding
import com.bryll.hams.viewmodels.MainViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Logout : DialogFragment() {

    private lateinit var binding : FragmentLogoutBinding
    private val mainViewModel by viewModels<MainViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentLogoutBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonYes.setOnClickListener {
            mainViewModel.deleteToken().also {
                dismiss()
                activity?.finish()
            }
        }
        binding.buttonNo.setOnClickListener {
            dismiss()
        }

    }
}