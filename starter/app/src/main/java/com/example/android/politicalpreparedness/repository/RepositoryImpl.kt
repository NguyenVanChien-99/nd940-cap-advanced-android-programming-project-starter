package com.example.android.politicalpreparedness.repository

import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.ElectionResponse
import com.example.android.politicalpreparedness.network.models.RepresentativeResponse
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import kotlinx.coroutines.withContext

class RepositoryImpl:Repository {
    override suspend fun getRepresentativeFromApi(address: String): RepresentativeResponse {
        return CivicsApi.retrofitService.getRepresentatives(address)
    }
}

class ElectionRepositoryImpl(private val electionDao: ElectionDao):ElectionRepository{
    override suspend fun getElectionsFromApi(): ElectionResponse {
        return CivicsApi.retrofitService.getElections()
    }

    override suspend fun getVoterInfoFromApi(
        address: String,
        electionId: Int
    ): VoterInfoResponse {
        return CivicsApi.retrofitService.getVoterInfo(address,electionId)
    }

    override suspend fun refreshData() {
        TODO("Not yet implemented")
    }

    override suspend fun getElectionsFromDatabase(): List<Election> {
        return electionDao.getAllElection()
    }

    override suspend fun getElection(id: Int): Election? {
        return electionDao.getSingleElection(id)
    }

    override suspend fun saveElection(election: Election) {
        electionDao.insert(election)
    }

    override suspend fun deleteElection(election: Election) {
        electionDao.delete(election)
    }

}