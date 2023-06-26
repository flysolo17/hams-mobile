package com.bryll.hams.views.enrollment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bryll.hams.R
import com.bryll.hams.adapters.EnrollmentAdapter
import com.bryll.hams.databinding.FragmentEnrollmentBinding
import com.bryll.hams.models.AcademicStatus
import com.bryll.hams.models.Student
import com.bryll.hams.services.AuthServiceImpl
import com.bryll.hams.services.enrollment.EnrollmentServiceImpl
import com.bryll.hams.utils.LoadingDialog
import com.bryll.hams.utils.UiState
import com.bryll.hams.viewmodels.AuthViewModel
import com.bryll.hams.viewmodels.EnrollmentViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore


class EnrollmentFragment : Fragment() {

    private lateinit var binding : FragmentEnrollmentBinding
    private lateinit var loadingDialog: LoadingDialog
    private val authViewModel: AuthViewModel by viewModels {    AuthViewModel.provideFactory(
        AuthServiceImpl(FirebaseAuth.getInstance(),FirebaseFirestore.getInstance()), this)}
    private var student : Student? = null
    private lateinit var enrollmentAdapter: EnrollmentAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentEnrollmentBinding.inflate(inflater,container,false)
        loadingDialog = LoadingDialog(binding.root.context)
        binding.recyclerviewAcademics.layoutManager = LinearLayoutManager(binding.root.context)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val user  = FirebaseAuth.getInstance().currentUser
        user?.let {
            authViewModel.findStudentByEmail(it.email!!)
        }
        binding.buttonEnrollment.setOnClickListener {
            student?.let {
                val directions = EnrollmentFragmentDirections.actionMenuEnrollmentToEnrollmentFormFragment(it)
                findNavController().navigate(directions)
            }

        }
        observers()
    }

    private fun observers() {
        authViewModel.findUserByEmail.observe(viewLifecycleOwner) {
            when(it) {
                is UiState.onFailed -> {
                    loadingDialog.closeDialog()
                    Toast.makeText(binding.root.context,it.message, Toast.LENGTH_SHORT).show()
                }
                is UiState.onLoading -> {
                    loadingDialog.showDialog("Getting user information.....")
                }
                is UiState.onSuccess -> {
                    loadingDialog.closeDialog()
                    student = it.data

                    student?.let {stud->
                        identifyAcads(stud)
                        stud.academics?.let { acads ->
                            if (acads.isEmpty()) {
                                binding.textNoEnrollment.visibility = View.VISIBLE
                             } else {
                                binding.textNoEnrollment.visibility = View.GONE
                            }
                            enrollmentAdapter = EnrollmentAdapter(binding.root.context,acads)
                            binding.recyclerviewAcademics.adapter = enrollmentAdapter
                        }
                    }

                }
            }
        }
    }
    private fun identifyAcads(student: Student) {
        val acads = student.academics?.filter { it.academicStatus == AcademicStatus.REQUESTED || it.academicStatus== AcademicStatus.ACCEPTED } ?: mutableListOf()
        if (acads.isEmpty()) {
            binding.buttonEnrollment.visibility = View.VISIBLE
        } else {
            binding.buttonEnrollment.visibility = View.GONE
        }
    }


}