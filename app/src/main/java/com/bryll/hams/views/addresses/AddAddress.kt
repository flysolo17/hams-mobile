package com.bryll.hams.views.addresses

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bryll.hams.R
import com.bryll.hams.databinding.FragmentAddAddressBinding
import com.bryll.hams.models.AddressType
import com.bryll.hams.utils.LoadingDialog
import com.bryll.hams.utils.UiState
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddAddress : Fragment() {
    private lateinit var binding : FragmentAddAddressBinding
    private val addressType = listOf("CURRENT","PERMANENT")
    private var studentID : Int? = null

    private lateinit var loadingDialog: LoadingDialog
    private val addAddressViewModel by viewModels<AddAddressViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAddAddressBinding.inflate(inflater,container,false)
        loadingDialog = LoadingDialog(binding.root.context)
        binding.houseNoEditText.doAfterTextChanged { binding.houseNoLayout.error = null }
        binding.streetEditText.doAfterTextChanged { binding.streetLayout.error = null }
        binding.barangayEditText.doAfterTextChanged { binding.barangayLayout.error = null }
        binding.municipalityEditText.doAfterTextChanged { binding.municipalityLayout.error = null }
        binding.provinceEditText.doAfterTextChanged { binding.provinceLayout.error = null }
        binding.zipCodeEditText.doAfterTextChanged { binding.zipCodeLayout.error = null }
        binding.typeEdittext.doAfterTextChanged { binding.typeLayout.error = null }
        binding.typeEdittext.setAdapter(ArrayAdapter(binding.root.context,R.layout.dropdown_items,addressType))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       binding.buttonSave.setOnClickListener {
           validateFormAndSubmit()
       }
        observers()
    }
    private fun validateFormAndSubmit()  {
        val houseNo = binding.houseNoEditText.text.toString()
        val street = binding.streetEditText.text.toString()
        val barangay = binding.barangayEditText.text.toString()
        val municipality = binding.municipalityEditText.text.toString()
        val province = binding.provinceEditText.text.toString()
        val country = binding.countruEditText.text.toString()
        val zipCode = binding.zipCodeEditText.text.toString()
        val type = binding.typeEdittext.text.toString()
        val zipCodeRegex = Regex("^\\d{4,10}\$")
        // Validate each field
        if (houseNo.isEmpty()) {
            binding.houseNoLayout.error = "House No is required"
            return
        }

        if (street.isEmpty()) {
            binding.streetLayout.error = "Street is required"
            return
        }

        if (barangay.isEmpty()) {
            binding.barangayLayout.error = "Barangay is required"
            return
        }

        if (municipality.isEmpty()) {
            binding.municipalityLayout.error = "Municipality is required"
            return
        }

        if (province.isEmpty()) {
            binding.provinceLayout.error = "Province is required"
            return
        }

        if (zipCode.isEmpty()) {
            binding.zipCodeLayout.error = "Zip Code is required"
            return
        }

        if (!zipCode.matches(zipCodeRegex)) {
            binding.zipCodeLayout.error = "Invalid Zip Code"
            return
        }
        if (type.isEmpty()) {
            binding.typeLayout.error = "Type is required"
            return
        }
        val addressType = if (type == AddressType.CURRENT.name) 0 else 1
        addAddressViewModel.createAddress(houseNo.toInt(), street, barangay, municipality, province, country, zipCode, addressType)
    }
    private fun observers() {
       addAddressViewModel.address.observe(viewLifecycleOwner) {state ->
           when(state) {
               is UiState.FAILED -> {
                   loadingDialog.closeDialog()
                   MaterialAlertDialogBuilder(binding.root.context).setTitle("Error").setMessage(state.message).show()
               }
               is UiState.LOADING -> {
                   loadingDialog.showDialog("Creating Address!")
               }

               is UiState.SUCCESS -> {
                   loadingDialog.closeDialog()
                   Toast.makeText(binding.root.context,"Successfully Created",Toast.LENGTH_SHORT).show()
                   findNavController().popBackStack()
               }
           }

       }
    }

}