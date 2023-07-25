package com.bryll.hams.views.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.bryll.hams.MainActivity
import com.bryll.hams.databinding.ActivityLoginBinding
import com.bryll.hams.utils.LoadingDialog
import com.bryll.hams.utils.SECRET_KEY
import com.bryll.hams.utils.UiState
import com.bryll.hams.viewmodels.LoginViewModel
import com.bryll.hams.viewmodels.MainViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var  binding : ActivityLoginBinding


    private lateinit var loadingDialog : LoadingDialog
    private val loginViewModel  by viewModels<LoginViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadingDialog = LoadingDialog(this)
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
                loginViewModel.loginFunc(studentID,password)
            }
        }
        binding.buttonForgotPassword.setOnClickListener {
            val dialog = ForgotPasswordFragment()
            if (!dialog.isAdded) {
                dialog.show(supportFragmentManager,"Forgot Password")
            }
        }
        observers()

    }
    private fun observers() {
        loginViewModel.login.observe(this) { state->
            when(state) {
                is UiState.FAILED ->{
                    loadingDialog.closeDialog()
                    MaterialAlertDialogBuilder(this)
                        .setTitle("Error")
                        .setMessage(state.message)
                        .setNegativeButton("Close") { dialog,_ ->
                            dialog.dismiss()
                        }.show()
                }
               is UiState.LOADING -> {
                   loadingDialog.showDialog("Logging in....")
               }
                is UiState.SUCCESS -> {
                    state.data.data?.let {
                        loginViewModel.saveToken(it.toString())
                    }.also {
                        loadingDialog.closeDialog()
                        Toast.makeText(this,state.data.message ,Toast.LENGTH_SHORT).show().also {
                            startActivity(Intent(this,MainActivity::class.java))
                        }
                    }
                }
            }
        }

    }

    override fun onStart() {
        super.onStart()
        val currentUser = loginViewModel.getToken()
        if (currentUser != null) {
            startActivity(Intent(this,MainActivity::class.java))
        }
    }


}