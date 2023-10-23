package com.example.android.politicalpreparedness.representative

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.repository.Repository
import com.example.android.politicalpreparedness.representative.model.Representative
import kotlinx.coroutines.launch
import timber.log.Timber

class RepresentativeViewModel(private val repository: Repository) : ViewModel() {

    val representatives = MutableLiveData<List<Representative>>()
//    val address = MutableLiveData<Address>()
    var line1 = MutableLiveData<String>()
    val line2 = MutableLiveData<String>()
    val state = MutableLiveData<String>()
    val city = MutableLiveData<String>()
    val zip = MutableLiveData<String>()
    lateinit var representatives_stored : ArrayList<Representative>
    init {
//        address.value= Address("","","","","")
        line1.value=""
        line2.value=""
        state.value=""
        city.value=""
        zip.value=""
        representatives_stored= ArrayList()
    }

    private fun fetchRepresentatives(add: Address){
        viewModelScope.launch {
            val (offices, officials)= repository.getRepresentativeFromApi(add.toFormattedString())
            representatives.value = offices.flatMap { office -> office.getRepresentatives(officials) }
        }
    }

    /**
     *  The following code will prove helpful in constructing a representative from the API. This code combines the two nodes of the RepresentativeResponse into a single official :

    val (offices, officials) = getRepresentativesDeferred.await()
    _representatives.value = offices.flatMap { office -> office.getRepresentatives(officials) }

    Note: getRepresentatives in the above code represents the method used to fetch data from the API
    Note: _representatives in the above code represents the established mutable live data housing representatives

     */

    fun useLocation(add: Address){
//        address.value=add
        fetchRepresentatives(add)
    }

    fun getRepresentativesFromAddress(){
        val address= Address(line1.value!!, line2.value, city.value!!, state.value!!, zip.value!!)
        fetchRepresentatives(address)
    }
}
