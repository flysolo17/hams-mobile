package com.bryll.hams

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bryll.hams.databinding.ActivityMainBinding
import com.bryll.hams.models.Students
import com.bryll.hams.utils.LoadingDialog
import com.bryll.hams.utils.SECRET_KEY
import com.bryll.hams.utils.STORAGE_LINK
import com.bryll.hams.utils.UiState
import com.bryll.hams.viewmodels.MainViewModel
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    private lateinit var loadingDialog : LoadingDialog

    private val mainViewModel by viewModels<MainViewModel>()

    private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // initialize Data binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadingDialog = LoadingDialog(this)
        setSupportActionBar(binding.appBarMain.toolbar)
        mainViewModel.getStudentInfo()

        observers()
    }
    private fun observers() {
        mainViewModel.student.observe(this) {
            when(it) {
                is UiState.FAILED -> {
                    loadingDialog.closeDialog()
                    MaterialAlertDialogBuilder(this).setTitle("Error").setMessage(it.message).show()
                }
                is UiState.LOADING -> {
                    loadingDialog.showDialog("getting user info....")
                }
                is UiState.SUCCESS -> {
                    loadingDialog.closeDialog()
                    initHeader(it.data)
                    setupNavigation()
                } else  -> loadingDialog.closeDialog()
            }
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


    private fun initHeader(student : Students) {
        val headerView: View = binding.navigation.getHeaderView(0)
        val fullname: TextView = headerView.findViewById(R.id.textFullname)
        val profile: ShapeableImageView = headerView.findViewById(R.id.imageProfile)
        val studentID : TextView = headerView.findViewById(R.id.textStudentID)
        fullname.text = getString(R.string.student_name_placeholder,
            student.first_name ?: "",
            student.middle_name ?: "",
            student.last_name ?: ""
        )
        studentID.text = student.lrn
        student.profile?.let {
            Glide.with(this).load(STORAGE_LINK+student.profile).into(profile)
        }

    }
    private fun setupNavigation() {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.menu_dashboard, R.id.menu_record,R.id.menu_enrollment,R.id.menu_grade,R.id.menu_chat,R.id.menu_profile,R.id.menu_logout
            ), binding.drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navigation.setupWithNavController(navController)
    }

}