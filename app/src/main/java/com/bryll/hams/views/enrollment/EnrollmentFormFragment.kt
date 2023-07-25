package com.bryll.hams.views.enrollment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bryll.hams.R
import com.bryll.hams.databinding.FragmentEnrollmentFormBinding
import com.bryll.hams.models.EnrollmentStatus
import com.bryll.hams.models.EnrollmentType
import com.bryll.hams.utils.LoadingDialog
import com.bryll.hams.utils.UiState
import com.bryll.hams.viewmodels.EnrollmentViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EnrollmentFormFragment : Fragment() {
    private lateinit var binding : FragmentEnrollmentFormBinding

    private val selectedEnrollments = mutableListOf<String>()
    private lateinit var loadingDialog : LoadingDialog
    private val enrollmentViewModel by viewModels<EnrollmentViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentEnrollmentFormBinding.inflate(inflater,container,false)
        setupCheckBox(binding.checkModularPrint , EnrollmentType.MODULAR_PRINT.name)
        setupCheckBox(binding.checkModularDigital , EnrollmentType.MODULAR_DIGITAL.name)
        setupCheckBox(binding.checkOnline , EnrollmentType.ONLINE.name)
        setupCheckBox(binding.checkEducationTelevision ,EnrollmentType.EDUCATION_TELEVISION.name)
        setupCheckBox(binding.checkRadioBaseInstruction, EnrollmentType.RADIO_BASE_INSTRUCTION.name)
        setupCheckBox(binding.checkHomeSchooling , EnrollmentType.HOME_SCHOOLING.name)
        setupCheckBox(binding.checkBlended , EnrollmentType.BLENDED.name)
        setupCheckBox(binding.checkFaceToFace , EnrollmentType.FACE_TO_FACE.name)
        loadingDialog = LoadingDialog(binding.root.context)
        binding.inputSchoolYearFrom.doAfterTextChanged { binding.layoutSchoolYearFrom.error = null }
        binding.inputSchoolYearTo.doAfterTextChanged { binding.layoutSchoolYearTo.error = null }
        binding.inputGradeLevel.doAfterTextChanged { binding.layoutGradeLevel.error = null }
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonSubmit.setOnClickListener {
            validateInputs()
        }
        observers()
    }

    private fun validateInputs() {
        val from = binding.inputSchoolYearFrom.text.toString()
        val to = binding.inputSchoolYearTo.text.toString()
        val level = binding.inputGradeLevel.text.toString()
        val track = binding.inputTrack.text.toString()
        val strand = binding.inputStrand.text.toString()
        val sem = if (binding.radioSem1.isChecked) 1 else if (binding.radioSem2.isChecked) 2 else null
        if (from.isEmpty()) {
            binding.layoutSchoolYearFrom.error = "this field is empty"
            return
        }
        if (to.isEmpty()) {
            binding.layoutSchoolYearTo.error = "this field is empty"
            return
        }
        if (from.toInt() > to.toInt()) {
            binding.layoutSchoolYearFrom.error = "Invalid Year"
            return
        }
        if (level.isEmpty()) {
            binding.layoutGradeLevel.error = "this field is empty"
            return
        }
        enrollmentViewModel.createEnrollment(level.toInt(),
            String.format("%s-%s",from,to),track.ifEmpty { null },strand.ifEmpty { null },sem,selectedEnrollments)
    }

    private fun setupCheckBox(checkBox: CheckBox,string: String) {
        checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedEnrollments.add(string)
            } else {
                selectedEnrollments.remove(string)
            }
        }
    }

    private fun observers() {
        enrollmentViewModel.enrollmentRequest.observe(viewLifecycleOwner){state ->
            when(state) {
                is UiState.FAILED -> {
                    loadingDialog.closeDialog()
                    MaterialAlertDialogBuilder(binding.root.context).setTitle("Error").setMessage(state.message).show()
                }
                is UiState.LOADING -> {
                    loadingDialog.showDialog("Creating enrollment Request....")
                }
                is UiState.SUCCESS -> {
                    loadingDialog.closeDialog()
                    findNavController().popBackStack()
                } else  -> loadingDialog.closeDialog()
            }
        }
    }
}