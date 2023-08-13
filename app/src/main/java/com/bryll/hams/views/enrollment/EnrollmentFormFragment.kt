package com.bryll.hams.views.enrollment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bryll.hams.R
import com.bryll.hams.databinding.FragmentEnrollmentFormBinding
import com.bryll.hams.models.Address
import com.bryll.hams.models.AddressType
import com.bryll.hams.models.ContactType
import com.bryll.hams.models.Contacts
import com.bryll.hams.models.EnrollmentStatus
import com.bryll.hams.models.EnrollmentType
import com.bryll.hams.models.Gender
import com.bryll.hams.models.Students
import com.bryll.hams.utils.LoadingDialog
import com.bryll.hams.utils.STORAGE_LINK
import com.bryll.hams.utils.UiState
import com.bryll.hams.viewmodels.EnrollmentViewModel
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EnrollmentFormFragment : Fragment() {
    private lateinit var binding : FragmentEnrollmentFormBinding

    private val selectedEnrollments = mutableListOf<String>()
    private lateinit var loadingDialog : LoadingDialog
    private val enrollmentViewModel by viewModels<EnrollmentViewModel>()
    private var student : Students ? = null
    private val args : EnrollmentFormFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            student = args.student

        }
    }
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
        student?.let {
            displayInformation(it)
        }
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

    private fun displayInformation(student: Students) {
        binding.layoutAddresses.removeAllViews()
        binding.layoutContacts.removeAllViews()
        binding.inputFirstname.setText(student.first_name ?: "NA")
        binding.inputMiddleName.setText(student.middle_name ?: "NA")
        binding.inputLastname.setText(student.last_name ?: "NA")
        binding.inputExtensionName.setText(student.extension_name ?: "NA")
        binding.inputStudentID.setText(student.lrn)
        binding.inputBirthday.setText((student.birth_date ?: "NA").toString())
        binding.inputNationality.setText( student.nationality ?: "NA")
        binding.inputEmail.setText(student.email ?: "NA")
        binding.inputGender.setText(Gender.values()[(student.gender ?: 0).toInt()].displayGender)
        student.contacts?.let {
            it.map {contact ->
                initContacts(contact)
            }
        }
        student.addresses?.let {
            it.map {address ->
                initAddresses(address)
            }
        }
    }

    private fun initContacts(contacts: Contacts) {
        val view = LayoutInflater.from(binding.layoutContacts.context).inflate(R.layout.row_contacts,binding.layoutContacts,false)
        val  textContactName : TextView = view.findViewById(R.id.textContactName)
        val textContactNumber : TextView = view.findViewById(R.id.textContactNumber)
        val textContactType : TextView = view.findViewById(R.id.textContactType)
        textContactName.text = getString(R.string.student_name_placeholder, contacts.first_name,contacts.middle_name,contacts.last_name)
        textContactNumber.text = contacts.phone
        textContactType.text = ContactType.values()[contacts.type?:0].displayName
        binding.layoutContacts.addView(view)
    }
    private fun initAddresses(address: Address) {
        val view = LayoutInflater.from(binding.layoutAddresses.context).inflate(R.layout.row_address,binding.layoutAddresses,false)
        val  textFullAddress : TextView = view.findViewById(R.id.textFullAddress)
        val textAddressType : TextView = view.findViewById(R.id.textAddressType)
        val textZipCode : TextView = view.findViewById(R.id.textZipCode)
        val textCountry : TextView = view.findViewById(R.id.textCountry)
        textFullAddress.text = address.getFormattedAddress()
        textAddressType.text = AddressType.values()[address.type?:0].name
        textZipCode.text = address.zip_code ?: "NA"
        textCountry.text = address.country ?: "NA"
        binding.layoutAddresses.addView(view)
    }
}