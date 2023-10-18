package com.example.android.politicalpreparedness.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.android.politicalpreparedness.network.models.Election

@Dao
interface ElectionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(election: Election)

    @Query("SELECT * FROM election_table")
    suspend fun getAllElection(): List<Election>

    @Query("SELECT * FROM election_table where id = :id")
    suspend fun getSingleElection(id: Int): Election?

    @Delete
    suspend fun delete(election: Election)

    @Query("DELETE FROM election_table")
    suspend fun clear()

}