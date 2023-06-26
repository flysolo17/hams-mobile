package com.bryll.hams.views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bryll.hams.R
import com.bryll.hams.adapters.EnrollmentAdapter
import com.bryll.hams.adapters.SubjectAdapter
import com.bryll.hams.databinding.FragmentDashboardBinding
import com.bryll.hams.models.Academic
import com.bryll.hams.models.AcademicStatus
import com.bryll.hams.models.Grade
import com.bryll.hams.services.AuthServiceImpl
import com.bryll.hams.utils.LoadingDialog
import com.bryll.hams.utils.UiState
import com.bryll.hams.viewmodels.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class DashboardFragment : Fragment() {

    private val authViewModel: AuthViewModel by viewModels {    AuthViewModel.provideFactory(
        AuthServiceImpl(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance()), this)}
    private lateinit var binding : FragmentDashboardBinding
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var subjectAdapter: SubjectAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentDashboardBinding.inflate(inflater,container,false)
        loadingDialog = LoadingDialog(binding.root.context)
        binding.recyclerviewSubjects.layoutManager = LinearLayoutManager(binding.root.context)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val user  = FirebaseAuth.getInstance().currentUser
        user?.let {
            authViewModel.findStudentByEmail(it.email!!)
        }
        observers()
    }
    private fun observers() {
        authViewModel.findUserByEmail.observe(viewLifecycleOwner) {
            when(it) {
                is UiState.onFailed -> {
                    loadingDialog.closeDialog()
                    Toast.makeText(binding.root.context,it.message.toString(), Toast.LENGTH_SHORT).show()
                }
                is UiState.onLoading -> {
                    loadingDialog.showDialog("Getting user information.....")
                }
                is UiState.onSuccess -> {
                    loadingDialog.closeDialog()
                    it.data.academics?.let { acadList->
                        val subjects : MutableList<Grade> = mutableListOf()
                        acadList.map { acads ->
                            if (acads.academicStatus == AcademicStatus.ACCEPTED) {
                                acads.grades?.let {grade ->
                                    subjects.addAll(grade)
                                }
                            }
                        }
                        subjectAdapter = SubjectAdapter(binding.root.context,subjects)
                        binding.recyclerviewSubjects.adapter = subjectAdapter
                        if (subjects.isEmpty()) {
                            binding.textNoSubjects.visibility = View.VISIBLE
                        } else {
                            binding.textNoSubjects.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }

}