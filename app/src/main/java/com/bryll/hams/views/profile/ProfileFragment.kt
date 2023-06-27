package com.bryll.hams.views.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bryll.hams.R
import com.bryll.hams.databinding.FragmentProfileBinding

import com.bryll.hams.models.Student
import com.bryll.hams.services.AuthServiceImpl
import com.bryll.hams.utils.LoadingDialog
import com.bryll.hams.utils.UiState
import com.bryll.hams.viewmodels.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage


class ProfileFragment : Fragment() {

    private lateinit var binding : FragmentProfileBinding
    private val authViewModel: AuthViewModel by viewModels {    AuthViewModel.provideFactory(
        AuthServiceImpl(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance(),
            FirebaseStorage.getInstance()), this)}
    private lateinit var loadingDialog : LoadingDialog
    private var _student :Student ? = null
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
        FirebaseAuth.getInstance().currentUser?.let {
            authViewModel.findStudentByEmail(it.email!!)
        }
        observers()
        binding.buttonChangePassword.setOnClickListener {
            findNavController().navigate(R.id.action_menu_profile_to_changePasswordFragment)
        }
        binding.buttonEdit.setOnClickListener {
            _student?.let {student->
                val directions = ProfileFragmentDirections.actionMenuProfileToUpdateProfileFragment(student = student)
                findNavController().navigate(directions)
            }

        }
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
                    _student = it.data
                    _student?.let { student ->
                        displayInformation(student)
                    }

                }
            }
        }
    }
    private fun displayInformation(student: Student) {
        binding.textFullname.text = "${student.studentInfo?.firstName} ${student.studentInfo?.middleName} ${student.studentInfo?.lastname}"
        binding.textStudentID.text = student.id
        binding.textBirthday.text = (student.studentInfo?.dob ?: "NA").toString()
        binding.textNationality.text = student.studentInfo?.nationality
        binding.textGender.text = student.studentInfo?.gender?.name ?: "NA"
        binding.textContactName.text = student.contacts?.name ?: "NA"
        binding.textContactNumber.text = student.contacts?.phone ?: "NA"
        binding.textContactType.text = student.contacts?.type?.name ?: "NA"
        binding.textEmaill.text = student.email
    }
 }