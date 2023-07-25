package com.bryll.hams.views.auth

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bryll.hams.R
import com.bryll.hams.databinding.FragmentUpdateAccountBinding
import com.bryll.hams.models.Gender
import com.bryll.hams.models.Students
import com.bryll.hams.utils.LoadingDialog
import com.bryll.hams.utils.STORAGE_LINK
import com.bryll.hams.utils.UiState
import com.bryll.hams.utils.formatDate
import com.bryll.hams.viewmodels.UpdateAccountViewModel
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException
import java.util.Calendar

@AndroidEntryPoint
class UpdateAccount : Fragment() {

    private lateinit var binding : FragmentUpdateAccountBinding
    private val args : UpdateAccountArgs by navArgs()
    private var student : Students ? = null
    private val updateAccountViewModel by viewModels<UpdateAccountViewModel>()
    private lateinit var loadingDialog : LoadingDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        student = args.students
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentUpdateAccountBinding.inflate(inflater,container,false)
        loadingDialog = LoadingDialog(binding.root.context)

        student?.let {
            binding.inputFirstname.setText(it.first_name)
            binding.inputMiddlename.setText(it.middle_name)
            binding.inputLastname.setText(it.last_name)
            binding.inputExtensionName.setText(it.extension_name)
            binding.inputGender.setText(Gender.values()[(it.gender ?: 0).toInt()].displayGender)
            binding.inputGender.setAdapter(ArrayAdapter(binding.root.context,R.layout.dropdown_items,Gender.values()))
            binding.inputNationality.setText(it.nationality)
            binding.inputEmail.setText(it.email)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.inputFirstname.doAfterTextChanged { binding.layoutFirstname.error = null }
        binding.inputMiddlename.doAfterTextChanged { binding.layoutMiddlename.error = null }
        binding.inputLastname.doAfterTextChanged { binding.layoutLastname.error = null }
        binding.inputExtensionName.doAfterTextChanged { binding.layoutExtensionName.error = null }
        binding.inputGender.doAfterTextChanged { binding.layoutGender.error = null }
        binding.inputNationality.doAfterTextChanged { binding.layoutNationality.error = null }
        binding.inputEmail.doAfterTextChanged { binding.layoutEmail.error = null }
        binding.buttonUpdateAccount.setOnClickListener {
            validateUpdateForm()
        }
        observer()
    }
    private fun validateUpdateForm() {
        val first_name  = binding.inputFirstname.text.toString()
        val middle_name =binding.inputMiddlename.text.toString()
        val last_name = binding.inputLastname.text.toString()
        val extension_name =binding.inputExtensionName.text.toString()
        val gender = binding.inputGender.text.toString()
        val nationality = binding.inputNationality.text.toString()
        val email = binding.inputEmail.text.toString()
        if (first_name.isEmpty()) {
            binding.layoutFirstname.error = "Firstname is required!"
            return
        }
        if (middle_name.isEmpty()) {
            binding.layoutMiddlename.error = "Middlename is required!"
            return
        }
        if (last_name.isEmpty()) {
            binding.layoutLastname.error = "Lastname is required!"
            return
        }
        if (gender.isEmpty()) {
            binding.layoutGender.error = "Gender is required!"
            return
        }
        if (nationality.isEmpty()) {
            binding.layoutNationality.error = "Nationality is required!"
            return
        }
        if (email.isEmpty()) {
            binding.layoutEmail.error = "Email is required!"
            return
        }
        val  extension = extension_name.ifEmpty { null }
        val sex = if (gender === Gender.MALE.displayGender) 0 else 1
        updateAccountViewModel.updateInfo(first_name,middle_name,last_name,extension,sex,nationality,email)
    }
    private fun observer() {
        updateAccountViewModel.updateAccount.observe(viewLifecycleOwner) {state->
            when(state) {
                is UiState.FAILED -> {
                    loadingDialog.closeDialog()
                    MaterialAlertDialogBuilder(binding.root.context).setTitle("Error").setMessage(state.message).show()
                }
                is UiState.LOADING -> {
                    loadingDialog.showDialog("Updating Student information...")
                }
                is UiState.SUCCESS -> {
                    loadingDialog.closeDialog()
                    Toast.makeText(binding.root.context,"Successfully Updated!",Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()

                } else  -> loadingDialog.closeDialog()
            }
        }
    }
}