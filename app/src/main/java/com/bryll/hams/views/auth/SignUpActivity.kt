package com.bryll.hams.views.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import com.bryll.hams.R
import com.bryll.hams.data.StudentRegistrationData
import com.bryll.hams.databinding.ActivitySignUpBinding
import com.bryll.hams.models.Gender
import com.bryll.hams.utils.LoadingDialog
import com.bryll.hams.utils.UiState
import com.bryll.hams.viewmodels.SignupViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpActivity : AppCompatActivity() {
    private val gender = listOf("MALE","FEMALE")
    lateinit var binding : ActivitySignUpBinding
    lateinit var loadingDialog: LoadingDialog
    private val signupViewModel by viewModels<SignupViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadingDialog = LoadingDialog(this)

        binding.inputGender.setAdapter(ArrayAdapter(this,R.layout.dropdown_items,gender))
        binding.buttonBack.setOnClickListener {
            finish()
        }
        binding.buttonSignUp.setOnClickListener {
            verifyInputs()?.let {

                signupViewModel.signUp(it)
            }
        }
        observers()
        binding.buttonSignIn.setOnClickListener {
            finish()
        }
    }

    private fun observers() {
        signupViewModel.signUp.observe(this) { state ->
            when(state) {
                is UiState.FAILED -> {
                    loadingDialog.closeDialog()
                    MaterialAlertDialogBuilder(this)
                        .setTitle("Error")
                        .setMessage(state.message)
                        .setNegativeButton("Close") { dialog,_ ->
                            dialog.dismiss()
                        }.show()
                }
                is UiState.LOADING -> {
                    loadingDialog.showDialog("Creating Account")
                }
                is UiState.SUCCESS -> {
                    loadingDialog.closeDialog()
                    MaterialAlertDialogBuilder(this).setTitle("Success").setMessage(state.data.message)
                        .setPositiveButton("Login") { dialog, _ ->
                            dialog.dismiss().also {
                                finish()
                            }
                        }
                        .show()
                }
            }
        }

    }
    private fun verifyInputs()  : StudentRegistrationData ?{
        val firstname = binding.inputFirstname.text.toString()
        val middlename = binding.inputMiddleName.text.toString()
        val lastname = binding.inputLastname.text.toString()
        val gender = binding.inputGender.text.toString()
        val studentID = binding.inputStudentID.text.toString()
        val password = binding.inputPassword.text.toString()
        val confirmPassword = binding.inputConfirmPassword.text.toString()
        if (firstname.isEmpty()) {
            binding.layoutFirstname.error = "enter firstname"
        } else if (middlename.isEmpty()) {
            binding.layoutMiddlename.error = "enter middlename"
        } else if (lastname.isEmpty()) {
            binding.layoutLastname.error = "enter lastname"
        }  else if (gender.isEmpty()) {
            binding.layoutGender.error = "enter gender"
        }   else if (studentID.isEmpty()) {
            binding.layoutStudentID.error = "enter ID"
        }  else if (password.isEmpty()) {
            binding.layoutPassword.error = "enter password"
        } else if (password.length < 7) {
            binding.layoutPassword.error = "Password should have 7 characters or more."
        } else if (password != confirmPassword) {
            binding.layoutConfirmPassword.error = "password don't match"
        } else {
            return  StudentRegistrationData.Builder()
                .setLRN(studentID)
                .setFirstName(firstname)
                .setMiddleName(middlename)
                .setLastName(lastname)
                .setGender(if (gender == Gender.MALE.name) 0 else 1)
                .setPassword(password)
                .build()
        }
        return null
    }
}