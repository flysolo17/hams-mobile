package com.bryll.hams.views.contacts

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bryll.hams.R
import com.bryll.hams.databinding.FragmentAddContactsBinding
import com.bryll.hams.models.ContactType
import com.bryll.hams.utils.LoadingDialog
import com.bryll.hams.utils.UiState
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddContacts : Fragment() {

    private lateinit var binding : FragmentAddContactsBinding
    private lateinit var loadingDialog: LoadingDialog
    private val addContactViewModel by viewModels<AddContactViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAddContactsBinding.inflate(inflater,container,false)
        loadingDialog = LoadingDialog(binding.root.context)
        binding.inputFirstname.doAfterTextChanged { binding.layoutFirstname.error = null }
        binding.inputMiddleName.doAfterTextChanged { binding.layoutMiddlename.error = null }
        binding.inputLastname.doAfterTextChanged { binding.layoutLastname.error = null }
        binding.inputPhone.doAfterTextChanged { binding.layoutPhone.error = null }
        binding.inputType.doAfterTextChanged { binding.layoutType.error = null }
        binding.inputType.setAdapter(ArrayAdapter(binding.root.context,R.layout.dropdown_items,ContactType.values()))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonCreateContact.setOnClickListener {
            validateContactForm()
        }
        observers()
    }

    private fun validateContactForm(){
        val firstName = binding.inputFirstname.text.toString()
        val middleName = binding.inputMiddleName.text.toString()
        val lastName = binding.inputLastname.text.toString()
        val phone = binding.inputPhone.text.toString()
        val type = binding.inputType.text.toString()
        if (firstName.isEmpty()) {
            binding.layoutFirstname.error = "Firstname is required!"
            return
        }
        if (middleName.isEmpty()) {
            binding.layoutMiddlename.error = "Middlename is required!"
            return
        }
        if (lastName.isEmpty()) {
            binding.layoutLastname.error = "Lastname is required!"
            return
        }
        if (phone.isEmpty() || phone.length != 11 || !phone.startsWith("09")) {
            binding.layoutPhone.error = "invalid phone number!"
            return
        }
        if (type.isEmpty()) {
            binding.layoutType.error = "contact type is required!"
        }
        val contactType = when (type) {
            ContactType.FATHER.displayName -> {
                0
            }
            ContactType.MOTHER.displayName -> {
                1
            }
            ContactType.GUARDIAN.displayName -> {
                2
            }
            else -> {
                0
            }
        }
        addContactViewModel.saveContact(firstName,middleName,lastName,phone,contactType)
    }
    private fun observers() {
        addContactViewModel.contacts.observe(viewLifecycleOwner) { state->
            when(state) {
                is UiState.FAILED -> {
                    loadingDialog.closeDialog()
                    MaterialAlertDialogBuilder(binding.root.context).setTitle("Error").setMessage(state.message).show()
                }
                is UiState.LOADING -> loadingDialog.showDialog("Saving Contact.....")

                is UiState.SUCCESS -> {
                    loadingDialog.closeDialog()
                    Toast.makeText(binding.root.context,"Successfully Created!",Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }

            }
        }
    }
}