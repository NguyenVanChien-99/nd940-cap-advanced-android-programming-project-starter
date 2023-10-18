package com.example.android.politicalpreparedness.repository

import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.ElectionResponse
import com.example.android.politicalpreparedness.network.models.RepresentativeResponse
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse

interface Repository {
    suspend fun getRepresentativeFromApi(address: String): RepresentativeResponse
}

interface ElectionRepository{
    suspend fun getElectionsFromApi(): ElectionResponse

    suspend fun getVoterInfoFromApi(address: String, electionId: Int): VoterInfoResponse

    suspend fun refreshData()

    suspend fun getElectionsFromDatabase(): List<Election>

    suspend fun getElection(id: Int): Election?

    suspend fun saveElection(election: Election)

    suspend fun deleteElection(election: Election)
}