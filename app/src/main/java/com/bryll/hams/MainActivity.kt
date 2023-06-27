package com.bryll.hams

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bryll.hams.adapters.SubjectAdapter
import com.bryll.hams.databinding.ActivityMainBinding
import com.bryll.hams.models.AcademicStatus
import com.bryll.hams.models.Grade
import com.bryll.hams.models.Student
import com.bryll.hams.services.AuthServiceImpl
import com.bryll.hams.utils.LoadingDialog
import com.bryll.hams.utils.UiState
import com.bryll.hams.viewmodels.AuthViewModel
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private val authViewModel: AuthViewModel by viewModels {    AuthViewModel.provideFactory(
        AuthServiceImpl(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance(),
            FirebaseStorage.getInstance()), this)}
    private lateinit var loadingDialog : LoadingDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadingDialog = LoadingDialog(this)
        setSupportActionBar(binding.appBarMain.toolbar)
        FirebaseAuth.getInstance().currentUser?.let {
            authViewModel.findStudentByEmail(it.email!!)
        }
        observer()
    }
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun observer() {
        authViewModel.findUserByEmail.observe(this) {
            when(it) {
                is UiState.onFailed -> {
                    loadingDialog.closeDialog()
                    Toast.makeText(binding.root.context, it.message, Toast.LENGTH_SHORT).show()
                }
                is UiState.onLoading -> {
                    loadingDialog.showDialog("Getting user information.....")
                }
                is UiState.onSuccess -> {
                    loadingDialog.closeDialog()
                    setupNavigation(it.data)

                }
            }
        }
    }
    private fun setupNavigation(student: Student) {
        val headerView: View = binding.navigation.getHeaderView(0)
        val fullname: TextView = headerView.findViewById(R.id.textFullname)
        val profile: ShapeableImageView = headerView.findViewById(R.id.imageProfile)
        val studentID : TextView = headerView.findViewById(R.id.textStudentID)
        fullname.text = "${student.studentInfo?.firstName} ${student.studentInfo?.middleName} ${student.studentInfo?.lastname}"
        studentID.text = student.id
        if (student.profile!!.isNotEmpty()) {
            Glide.with(this).load(student.profile).into(profile)
        }
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.menu_dashboard, R.id.menu_record, R.id.menu_academic,R.id.menu_enrollment,R.id.menu_grade,R.id.menu_chat,R.id.menu_profile,R.id.menu_logout
            ), binding.drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navigation.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { _: NavController?, destination: NavDestination, _: Bundle? ->
            when (destination.id) {
                R.id.menu_logout -> {
                    MaterialAlertDialogBuilder(this)
                        .setTitle("Logout")
                        .setMessage("Are you sure you want to logout?")
                        .setPositiveButton("Yes") {dialog ,_ ->
                            FirebaseAuth.getInstance().signOut().also {
                                dialog.dismiss().also {
                                    finish()
                                }
                            }

                        }.setNegativeButton("No") {dialog,_->
                            dialog.dismiss()
                        }
                }

            }
        }
    }
}