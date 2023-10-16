package com.example.android.politicalpreparedness.network.jsonadapter

import android.annotation.SuppressLint
import com.example.android.politicalpreparedness.network.models.Division
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ElectionAdapter {

    @SuppressLint("SimpleDateFormat")
    val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd");

    @FromJson
    fun dateFromJson(date: String): Date? {
        return simpleDateFormat.parse(date)
    }

    @ToJson
    fun dateToJson(date: Date): String {
        return simpleDateFormat.format(date)
    }

    @FromJson
    fun divisionFromJson(ocdDivisionId: String): Division {
        val countryDelimiter = "country:"
        val stateDelimiter = "state:"
        val country = ocdDivisionId.substringAfter(countryDelimiter, "")
            .substringBefore("/")
        val state = ocdDivisionId.substringAfter(stateDelimiter, "")
            .substringBefore("/")
        return Division(ocdDivisionId, country, state)
    }

    @ToJson
    fun divisionToJson(division: Division): String {
        return division.id
    }
}