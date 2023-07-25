package com.bryll.hams.views.profile

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract.Profile
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bryll.hams.BuildConfig
import com.bryll.hams.MainActivity
import com.bryll.hams.R
import com.bryll.hams.databinding.FragmentProfileBinding
import com.bryll.hams.models.Address
import com.bryll.hams.models.AddressType
import com.bryll.hams.models.ContactType
import com.bryll.hams.models.Contacts
import com.bryll.hams.models.Gender
import com.bryll.hams.models.Students

import com.bryll.hams.utils.LoadingDialog
import com.bryll.hams.utils.SECRET_KEY
import com.bryll.hams.utils.STORAGE_LINK
import com.bryll.hams.utils.UiState
import com.bryll.hams.utils.createFile
import com.bryll.hams.utils.formatDate
import com.bryll.hams.utils.getFileName
import com.bryll.hams.utils.getImageTypeFromUri
import com.bryll.hams.utils.getMimeType
import com.bryll.hams.viewmodels.MainViewModel
import com.bryll.hams.viewmodels.ProfileViewModel
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private lateinit var binding : FragmentProfileBinding

    private lateinit var loadingDialog : LoadingDialog
    private val profileViewModel by viewModels<ProfileViewModel>()
    private var student : Students? = null
    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val data = result.data
            try {
                if (data?.data != null) {
                    val result = data.data
                    result?.let {uri ->
                        Log.d("student: ",activity?.getMimeType(uri)!!)
                        val file = activity?.createFile(uri)
                        profileViewModel.updateProfile(file!!)
                    }

                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater,container,false)
        loadingDialog = LoadingDialog(binding.root.context)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonChangePassword.setOnClickListener {
            findNavController().navigate(R.id.action_menu_profile_to_changePasswordFragment)
        }
        profileViewModel.getStudentInfo()
        binding.buttonAddAddress.setOnClickListener {
            findNavController().navigate(R.id.action_menu_profile_to_addAddress)
        }
        binding.buttonCreateContact.setOnClickListener {
            findNavController().navigate(R.id.action_menu_profile_to_addContacts)
        }
        observers()
        binding.textBirthday.setOnClickListener {
            showDatePickerDialog()
        }
        binding.buttonEdit.setOnClickListener {
            student?.let { student->
                val directions = ProfileFragmentDirections.actionMenuProfileToUpdateAccount(student)
                findNavController().navigate(directions)
            }

        }
        binding.buttonPickImage.setOnClickListener {
            val galleryIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            galleryLauncher.launch(galleryIntent)
        }
    }
    private fun observers() {
        profileViewModel.student.observe(viewLifecycleOwner) {
            when(it) {
                is UiState.FAILED -> {
                    loadingDialog.closeDialog()
                    MaterialAlertDialogBuilder(binding.root.context).setTitle("Error").setMessage(it.message).show()
                }
                is UiState.LOADING -> {
                    loadingDialog.showDialog("getting user info....")
                }
                is UiState.SUCCESS -> {
                    loadingDialog.closeDialog()
                    this.student = it.data
                    student?.let {stud->
                        displayInformation(stud)
                    }

                } else  -> loadingDialog.closeDialog()
            }
        }
        profileViewModel.birthday.observe(viewLifecycleOwner) {
            when(it) {
                is UiState.FAILED -> {
                    loadingDialog.closeDialog()
                    MaterialAlertDialogBuilder(binding.root.context).setTitle("Error").setMessage(it.message).show()
                }
                is UiState.LOADING -> {
                    loadingDialog.showDialog("Updating Birthday...")
                }
                is UiState.SUCCESS -> {
                    loadingDialog.closeDialog()
                    Toast.makeText(binding.root.context,"Successfully Updated!",Toast.LENGTH_SHORT).show()
                    binding.textBirthday.text = it.data ?: "NA"
                } else  -> loadingDialog.closeDialog()
            }
        }
        profileViewModel.profile.observe(viewLifecycleOwner) {
            when(it) {
                is UiState.FAILED -> {
                    loadingDialog.closeDialog()
                    MaterialAlertDialogBuilder(binding.root.context).setTitle("Error").setMessage(it.message).show()
                }
                is UiState.LOADING -> {
                    loadingDialog.showDialog("Updating Profile...")
                }
                is UiState.SUCCESS -> {
                    loadingDialog.closeDialog()
                    Toast.makeText(binding.root.context,"Successfully Updated!",Toast.LENGTH_SHORT).show()
                    Glide.with(binding.root.context).load(STORAGE_LINK + it.data).into(binding.imageProfile)

                } else  -> loadingDialog.closeDialog()
            }
        }
    }
    private fun displayInformation(student: Students) {
        binding.layoutAddresses.removeAllViews()
        binding.layoutContacts.removeAllViews()
        binding.textFullname.text = getString(R.string.student_name_placeholder, student.first_name?: " ",student.middle_name ?: " ",student.last_name ?: " ")
        binding.textStudentID.text = student.lrn
        binding.textBirthday.text = (student.birth_date ?: "NA").toString()
        binding.textNationality.text = student.nationality ?: "NA"
        binding.textEmaill.text =student.email ?: "NA"
        binding.textGender.text =Gender.values()[(student.gender ?: 0).toInt()].displayGender
        binding.textFirstname.text = student.first_name ?: "NA"
        binding.textMiddleName.text = student.middle_name ?: "NA"
        binding.textLastname.text = student.last_name ?: "NA"
        binding.textExtensionName.text = student.extension_name ?: "NA"
        student.profile?.let {
            Glide.with(binding.root.context).load(STORAGE_LINK + student.profile).into(binding.imageProfile)
        }
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
        binding.textEmaill.text = student.email
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

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            binding.root.context,
            { _, selectedYear, selectedMonth, selectedDay ->
                // Set the selected date in the TextView
                val selectedDate = formatDate(selectedYear, selectedMonth, selectedDay)
                profileViewModel.updateBirthday(selectedDate)
            },
            year,
            month,
            day
        )

        datePickerDialog
            .show()
    }


 }