package com.bryll.hams.views.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.bryll.hams.MainActivity
import com.bryll.hams.databinding.ActivityLoginBinding
import com.bryll.hams.models.Student
import com.bryll.hams.services.AuthServiceImpl
import com.bryll.hams.utils.LoadingDialog
import com.bryll.hams.utils.UiState
import com.bryll.hams.viewmodels.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class LoginActivity : AppCompatActivity() {
    private lateinit var  binding : ActivityLoginBinding

    private val authViewModel: AuthViewModel by viewModels {    AuthViewModel.provideFactory(
        AuthServiceImpl(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance(),FirebaseStorage.getInstance()), this)}
    private lateinit var loadingDialog : LoadingDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadingDialog = LoadingDialog(this)
        val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            authViewModel.findStudentByEmail(it.email!!)
        }
        binding.buttonSignUp.setOnClickListener {
            startActivity(Intent(this,SignUpActivity::class.java))
        }
        binding.buttonLoggedIn.setOnClickListener {
            val studentID = binding.inputStudentID.text.toString()
            val password = binding.inputPassword.text.toString()
            if (studentID.isEmpty()) {
                binding.layoutStudentID.error = "invalid id"
            } else if (password.isEmpty()) {
                binding.layoutPassword.error = "invalid password"
            } else {
                authViewModel.getUserByID(id = studentID)
            }
        }
        observers()
        binding.buttonForgotPassword.setOnClickListener {
            val dialog = ForgotPasswordFragment()
            if (!dialog.isAdded) {
                dialog.show(supportFragmentManager,"Forgot Password")
            }
        }
    }
    private fun observers() {
        authViewModel.login.observe(this) {
            when (it) {
                is UiState.onFailed -> {
                    loadingDialog.closeDialog()
                    Toast.makeText(this,it.message,Toast.LENGTH_SHORT).show()
                }
                UiState.onLoading -> {
                    loadingDialog.showDialog("Logging in ....")
                }
                is UiState.onSuccess -> {
                    loadingDialog.closeDialog()
                    updateUI(it.data)
                }
            }
        }
        authViewModel.student.observe(this) {
            when (it) {
                is UiState.onFailed -> {
                    loadingDialog.closeDialog()
                    Toast.makeText(this,it.message,Toast.LENGTH_SHORT).show()
                }
                UiState.onLoading -> {
                    loadingDialog.showDialog("Checking student id....")
                }
                is UiState.onSuccess -> {
                    loadingDialog.closeDialog()
                    val password = binding.inputPassword.text.toString()
                    authViewModel.login(it.data.email!!,password)
                }
            }
        }

    }

    override fun onStart() {
        super.onStart()
        val user = FirebaseAuth.getInstance().currentUser
        updateUI(user)
    }

    private fun updateUI(user: FirebaseUser?) {
        user?.let {
            startActivity(Intent(this,MainActivity::class.java))
        }
    }


}