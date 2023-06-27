package com.bryll.hams.views.enrollment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bryll.hams.R
import com.bryll.hams.databinding.FragmentEnrollmentFormBinding
import com.bryll.hams.models.Academic
import com.bryll.hams.models.AcademicStatus
import com.bryll.hams.models.Classes
import com.bryll.hams.models.Curriculum
import com.bryll.hams.models.EducationLevel
import com.bryll.hams.models.Student
import com.bryll.hams.services.AuthService
import com.bryll.hams.services.AuthServiceImpl
import com.bryll.hams.services.enrollment.EnrollmentServiceImpl
import com.bryll.hams.utils.LoadingDialog
import com.bryll.hams.utils.UiState
import com.bryll.hams.utils.generateGrades
import com.bryll.hams.utils.getSubjectBySem
import com.bryll.hams.viewmodels.AuthViewModel
import com.bryll.hams.viewmodels.EnrollmentViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID


class EnrollmentFormFragment : Fragment() {
    private lateinit var binding : FragmentEnrollmentFormBinding


    private val enrollmentViewModel: EnrollmentViewModel by viewModels {    EnrollmentViewModel.provideFactory(
        EnrollmentServiceImpl(FirebaseFirestore.getInstance()), this)}

    private val authViewModel: AuthViewModel by viewModels {    AuthViewModel.provideFactory(
        AuthServiceImpl(FirebaseAuth.getInstance(),FirebaseFirestore.getInstance(), FirebaseStorage.getInstance()), this)}
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var classesList : List<Classes>
    private lateinit var classesAdapter : ArrayAdapter<*>
    private lateinit var enrollmentSubjectAdapter : EnrollmentSubjectAdapter
    private lateinit var curriculums:MutableList<Curriculum>
    private var selectedClass : Int = -1
    private var student : Student? = null
    private val args : EnrollmentFormFragmentArgs by navArgs()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        student = args.student
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentEnrollmentFormBinding.inflate(inflater,container,false)
        binding.inputEducationLevel.setAdapter(ArrayAdapter(binding.root.context,R.layout.dropdown_items,EducationLevel.values()))
        binding.inputSem.setAdapter(ArrayAdapter(binding.root.context,R.layout.dropdown_items,
            listOf("1","2")
        ))
        loadingDialog = LoadingDialog(binding.root.context)
        binding.recyclerviewSubjects.layoutManager = LinearLayoutManager(binding.root.context)
        curriculums = mutableListOf()
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       binding.inputEducationLevel.setOnItemClickListener { adapterView, _, i, _ ->
            val item = adapterView.getItemAtPosition(i).toString()
            val educationLevel = item.replace("_", " ")
            enrollmentViewModel.getAllClasses(educationLevel)
        }
        binding.inputClasses.setOnItemClickListener { _, _, i, _ ->
            curriculums.clear()
            val classes = classesList[i]
            selectedClass = i
            classes.curriculum?.let {
                curriculums.addAll(it)
            }
        }

        binding.inputSem.setOnItemClickListener { adapterView, _, i, _ ->
            val item = adapterView.getItemAtPosition(i).toString()
            enrollmentSubjectAdapter = EnrollmentSubjectAdapter(binding.root.context,
                getSubjectBySem(curriculums,item.toInt())
            )
            binding.recyclerviewSubjects.adapter = enrollmentSubjectAdapter
        }
        observers()
        binding.buttonSubmit.setOnClickListener {
            val count =   getSubjectBySem(curriculums,binding.inputSem.text.toString().toInt())
            if (count.isEmpty()) {
                Toast.makeText(binding.root.context,"No Subjects yet",Toast.LENGTH_SHORT).show()
            } else {

                showConfirmationDialog()
            }

        }
    }
    private fun showConfirmationDialog() {
        MaterialAlertDialogBuilder(binding.root.context)
            .setTitle("Alert")
            .setMessage("Please finish your profile before you send an application.")
            .setPositiveButton("Continue") {dialog,_->
                val sem = binding.inputSem.text.toString()
                if (selectedClass != -1 && classesList.size > selectedClass && sem.isNotEmpty()) {
                    val academic = Academic(UUID.randomUUID().toString(),classesList[selectedClass].id,sem, generateGrades(enrollmentSubjectAdapter.getAllCurriculums()),AcademicStatus.REQUESTED)
                    student?.let {
                        enrollmentViewModel.submitEnrollmentForm(it.id!!,academic)
                    }

                } else {
                    Toast.makeText(binding.root.context,"Invalid Application",Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }.setNegativeButton("Cancel") {dialog,_->
                dialog.dismiss()
            }.show()
    }
    private fun observers() {
        enrollmentViewModel.submitApplication.observe(viewLifecycleOwner) {
            when(it) {
                is UiState.onFailed -> {
                    loadingDialog.closeDialog()
                    Toast.makeText(binding.root.context,it.message, Toast.LENGTH_SHORT).show()
                }
                is UiState.onLoading -> {
                    loadingDialog.showDialog("Submitting application.....")
                }
                is UiState.onSuccess -> {
                    loadingDialog.closeDialog()
                    Toast.makeText(binding.root.context,it.data,Toast.LENGTH_SHORT).show().also {
                        findNavController().popBackStack()
                    }
                }
            }
        }
        enrollmentViewModel.classes.observe(viewLifecycleOwner) {
            when(it) {
                is UiState.onFailed -> {
                    loadingDialog.closeDialog()
                    Toast.makeText(binding.root.context,it.message, Toast.LENGTH_SHORT).show()
                }
                is UiState.onLoading -> {
                    loadingDialog.showDialog("Getting all classes.....")
                }
                is UiState.onSuccess -> {
                    loadingDialog.closeDialog()
                    classesList = it.data
                    val classes = classesList.map { data -> data.name ?: "" }
                    classesAdapter = ArrayAdapter(binding.root.context,R.layout.dropdown_items,classes)
                    binding.inputClasses.setAdapter(classesAdapter)
                }
            }
        }
    }

}