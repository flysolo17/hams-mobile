package com.bryll.hams.views.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import com.bryll.hams.R
import com.bryll.hams.databinding.ActivitySignUpBinding
import com.bryll.hams.models.Contacts
import com.bryll.hams.models.Gender
import com.bryll.hams.models.Student
import com.bryll.hams.models.StudentInformation
import com.bryll.hams.models.StudentStatus
import com.bryll.hams.services.AuthServiceImpl
import com.bryll.hams.utils.LoadingDialog
import com.bryll.hams.utils.UiState
import com.bryll.hams.viewmodels.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

class SignUpActivity : AppCompatActivity() {
    private val gender = listOf("MALE","FEMALE")
    lateinit var binding : ActivitySignUpBinding
    lateinit var loadingDialog: LoadingDialog
    private val authViewModel: AuthViewModel by viewModels {    AuthViewModel.provideFactory(
        AuthServiceImpl(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance()), this)}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadingDialog = LoadingDialog(this)
        binding.inputGender.setAdapter(ArrayAdapter(this,R.layout.dropdown_items,gender))
        binding.buttonBack.setOnClickListener {
            finish()
        }
        binding.buttonSignup.setOnClickListener {
            verifyInputs()
        }
        observers()
    }

    private fun observers() {
        authViewModel.signup.observe(this) { state ->
            when (state) {
                is UiState.onFailed -> {
                    loadingDialog.closeDialog()
                    Toast.makeText(this,state.message,Toast.LENGTH_SHORT).show()
                }
                is UiState.onLoading -> {
                    loadingDialog.showDialog("Creating account.....")
                }
                is UiState.onSuccess -> {
                    loadingDialog.closeDialog()
                    authViewModel.saveStudent(state.data)
                }
            }

        }
        authViewModel.saveStudent.observe(this) { state->
            when (state) {
                is UiState.onFailed -> {
                    loadingDialog.closeDialog()
                    Toast.makeText(this,state.message,Toast.LENGTH_SHORT).show()
                }
                is UiState.onLoading -> {
                    loadingDialog.showDialog("Saving student information.....")
                }
                is UiState.onSuccess -> {
                    loadingDialog.closeDialog()
                    finish()
                    Toast.makeText(this,state.data,Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun verifyInputs() {
        val lrn = binding.inputLRN.text.toString()
        val firstname = binding.inputFirstname.text.toString()
        val middlename = binding.inputMiddlename.text.toString()
        val lastname = binding.inputLastname.text.toString()
        val gender = binding.inputGender.text.toString()
        val email = binding.inputEmail.text.toString()
        val password = binding.inputPassword.text.toString()
        if (lrn.isEmpty()) {
            binding.layoutLRN.error = "enter LRN"
        } else if (firstname.isEmpty()) {
            binding.layoutFirstname.error = "enter firstname"
        } else if (middlename.isEmpty()) {
            binding.layoutMiddlename.error = "enter middlename"
        }  else if (lastname.isEmpty()) {
            binding.layoutLastname.error = "enter firstname"
        }  else if (gender.isEmpty()) {
            binding.layoutGender.error = "enter gender"
        }  else if (email.isEmpty()) {
            binding.layoutEmail.error = "enter email"
        }  else if (password.isEmpty()) {
            binding.layoutPassword.error = "enter password"
        } else {
            val student =  Student(
                id = lrn,
                email= email,
                profile="",
             studentInfo = StudentInformation(
                 firstName = firstname,
                 middleName = middlename,
                 lastname = lastname,
                 gender = if (gender.equals(Gender.MALE)) Gender.MALE else Gender.FEMALE,
                 nationality = "Filipino",
                 dob = null
             ),
             contacts = Contacts(null,null,null),
             status  = StudentStatus.PRE_ENROLLED,
             academics = listOf(),
             createdAt = Date(),
            )
            authViewModel.signup(email,password, student = student)
        }
    }
}