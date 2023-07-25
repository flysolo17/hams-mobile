package com.bryll.hams.views.addresses

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bryll.hams.models.Address
import com.bryll.hams.repository.auth.AuthRepository
import com.bryll.hams.repository.datastore.DataStore
import com.bryll.hams.utils.DataStorage
import com.bryll.hams.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class AddAddressViewModel @Inject constructor(val dataStore: DataStore,val authRepository: AuthRepository) : ViewModel() {
    private val _addAddress = MutableLiveData<UiState<Address>>()
    var address : LiveData<UiState<Address>> = _addAddress

    fun createAddress(houseNo: Int, street: String, barangay: String, municipality: String, province: String, country: String, zipCode: String, type: Int) {
        viewModelScope.launch {
            dataStore.getToken()?.let { token->
                authRepository.createAddress(houseNo,street,barangay,municipality,province,country,zipCode,type, token = token) {
                    _addAddress.postValue(it)
                }
            }
        }
    }
}