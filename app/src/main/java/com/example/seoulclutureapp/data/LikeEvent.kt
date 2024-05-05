package com.example.seoulclutureapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events")
data class LikeEvent(
    @PrimaryKey
    val title: String,
    val date: String,
    val classification:String,
    val borough: String,
    val place :String,
    val orgName:String? = null,
    val useTarget:String? = null,
    val useFee:String? = null,
    val link:String? = null,
    val imgSrc:String? = null,
    val longitude:String? = null,
    val latitude:String? = null,
    val isFree:String? = ""
)
