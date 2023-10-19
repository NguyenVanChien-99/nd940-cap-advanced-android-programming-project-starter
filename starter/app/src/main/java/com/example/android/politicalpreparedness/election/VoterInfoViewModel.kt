package com.example.android.politicalpreparedness.election

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import com.example.android.politicalpreparedness.repository.ElectionRepositoryImpl
import kotlinx.coroutines.launch

class VoterInfoViewModel(dataSource: ElectionDao) : ViewModel() {

    private val repository = ElectionRepositoryImpl(dataSource)
    val voterInfo = MutableLiveData<VoterInfoResponse>()
    val savedElection = MutableLiveData<Election?>()


    //TODO: Add live data to hold voter info

    fun fetchVoterInfo(
        address: String,
        electionId: Int
    ) {
        viewModelScope.launch {
            val response = repository.getVoterInfoFromApi(address, electionId)
            voterInfo.value = response
        }
    }

    //TODO: Add var and methods to support loading URLs

    fun saveElection(election: Election) {
        viewModelScope.launch {
            repository.saveElection(election)
            savedElection.value = election
        }
    }

    fun removeElection(election: Election) {
        viewModelScope.launch {
            repository.deleteElection(election)
            savedElection.value=null
        }
    }

    fun fetchElection(electionId: Int) {
        viewModelScope.launch {
            val election=repository.getElection(electionId)
            savedElection.value = election
        }
    }
    //TODO: cont'd -- Populate initial state of save button to reflect proper action based on election saved status

    /**
     * Hint: The saved state can be accomplished in multiple ways. It is directly related to how elections are saved/removed from the database.
     */

}