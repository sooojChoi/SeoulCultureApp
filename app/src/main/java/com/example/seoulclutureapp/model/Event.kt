package com.example.seoulclutureapp.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Event (
    @SerialName(value="CODENAME")
    val classification:String,
    @SerialName(value="GUNAME")
    val borough: String,
    @SerialName(value="TITLE")
    val title:String,
    @SerialName(value="DATE")
    val date:String,
    @SerialName(value="PLACE")
    val place :String,
    @SerialName(value="ORG_NAME")
    val orgName:String,
    @SerialName(value="USE_TRGT")
    val useTarget:String,
    @SerialName(value="USE_FEE")
    val useFee:String,
    @SerialName(value="ORG_LINK")
    val link:String,
    @SerialName(value="MAIN_IMG")
    val imgSrc:String,
    @SerialName(value="RGSTDATE")
    val registerDate:String,
    @SerialName(value="STRTDATE")
    val startDate:String,
    @SerialName(value="END_DATE")
    val endDate:String,
    @SerialName(value="LOT")
    val longitude:String,
    @SerialName(value="LAT")
    val latitude:String,
    @SerialName(value="IS_FREE")
    val isFree:String
)

@Serializable
data class EventResult(
    @SerialName(value="list_total_count")
    val totalCount:Int? = null,
    @SerialName(value="RESULT")
    val resultCode:CodeResult? = null,
    @SerialName(value="row")
    val eventList: List<Event> = listOf(
        Event("클래식","강동구","재미있는 공연1",
            "2024-01-02","강동문화회관","기관1",
            "전체관람","2,000원","링크1",
            "이미지 소스1","2024-01-02",
            "202-01-02","2024-01-02",
            "longitude","latitude","free")
    )
)

@Serializable
data class TotalEventInfo(
    @SerialName(value="culturalEventInfo")
    val totalEventInfo: EventResult
)

@Serializable
data class CodeResult(
    @SerialName(value="CODE")
    val code:String? = null,
    @SerialName(value="MESSAGE")
    val message:String? = null,
)