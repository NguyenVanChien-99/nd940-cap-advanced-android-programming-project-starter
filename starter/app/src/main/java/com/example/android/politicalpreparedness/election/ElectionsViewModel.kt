package com.example.android.politicalpreparedness.election

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.ElectionResponse
import com.example.android.politicalpreparedness.repository.ElectionRepositoryImpl
import kotlinx.coroutines.launch

class ElectionsViewModel(dataSource: ElectionDao) : ViewModel() {

    private val repository = ElectionRepositoryImpl(dataSource)

    val upcomingElections = MutableLiveData<ElectionResponse>()
    val savedElections = MutableLiveData<List<Election>>()
    val selectedElection = MutableLiveData<Election?>()

    fun fetchElections() {
        viewModelScope.launch {
            upcomingElections.value = repository.getElectionsFromApi()
            savedElections.value = repository.getElectionsFromDatabase()
        }
    }

    fun navigateToElectionDetail(election: Election) {
        selectedElection.value = election
    }

    fun finishNavigateToElectionDetail() {
        selectedElection.value = null
    }
}