package com.bryll.hams.views.enrollment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bryll.hams.R
import com.bryll.hams.databinding.FragmentEnrollmentBinding
import com.bryll.hams.models.EnrollmentStatus
import com.bryll.hams.models.EnrollmentType
import com.bryll.hams.models.Students
import com.bryll.hams.utils.LoadingDialog
import com.bryll.hams.utils.UiState
import com.bryll.hams.viewmodels.EnrollmentViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EnrollmentFragment : Fragment() ,EnrollmentAdapter.EnrollmentClickListener{

    private lateinit var binding : FragmentEnrollmentBinding
    private lateinit var loadingDialog: LoadingDialog
    private var student : Students? = null
    private val enrollmentViewModel by viewModels<EnrollmentViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentEnrollmentBinding.inflate(inflater,container,false)
        binding.recyclerviewAcademics.layoutManager = LinearLayoutManager(binding.root.context)
        loadingDialog = LoadingDialog(binding.root.context)

        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonEnrollment.setOnClickListener {
            val directions = EnrollmentFragmentDirections.actionMenuEnrollmentToEnrollmentFormFragment(student!!)
            findNavController().navigate(directions)
        }
        enrollmentViewModel.getStudentInfo()
        observers()
    }
    private fun observers() {
        enrollmentViewModel.student.observe(viewLifecycleOwner) {
            when(it) {
                is UiState.FAILED -> {
                    loadingDialog.closeDialog()
                    MaterialAlertDialogBuilder(binding.root.context).setTitle("Error").setMessage(it.message).show()
                }
                is UiState.LOADING -> {
                    loadingDialog.showDialog("getting student info....")
                }
                is UiState.SUCCESS -> {
                    loadingDialog.closeDialog().also {_->
                        this.student = it.data
                        enrollmentViewModel.getEnrollments()
                    }
                } else  -> loadingDialog.closeDialog()
            }
        }
        enrollmentViewModel.enrollments.observe(viewLifecycleOwner){ state ->
            when(state) {
                is UiState.FAILED -> {
                    loadingDialog.closeDialog()
                    MaterialAlertDialogBuilder(binding.root.context).setTitle("Error").setMessage(state.message).show()
                }
                is UiState.LOADING -> {
                    loadingDialog.showDialog("getting enrollments....")
                }
                is UiState.SUCCESS -> {
                    loadingDialog.closeDialog()
                    val enrollment = state.data.filter { it.status == EnrollmentStatus.ENROLLED.ordinal || it.status == EnrollmentStatus.PROCESSING.ordinal}
                    binding.buttonEnrollment.isEnabled = (student?.hasNullValues() == true || enrollment.isNotEmpty()) != true
                    binding.recyclerviewAcademics.adapter = EnrollmentAdapter(binding.root.context,state.data,this)

                } else  -> loadingDialog.closeDialog()
            }
        }
        enrollmentViewModel.cancelEnrollment.observe(viewLifecycleOwner) {
            when(it) {
                is UiState.FAILED -> {
                    loadingDialog.closeDialog()
                    MaterialAlertDialogBuilder(binding.root.context).setTitle("Error").setMessage(it.message).show()
                }
                is UiState.LOADING -> {
                    loadingDialog.showDialog("Cancelling enrollment....")
                }
                is UiState.SUCCESS -> {
                    loadingDialog.closeDialog()
                    Toast.makeText(binding.root.context,it.data.message,Toast.LENGTH_SHORT).show()
                } else  -> loadingDialog.closeDialog()
            }
        }
    }

    override fun onCancel(id: Int) {
       enrollmentViewModel.cancelEnrollment(id)
    }
}