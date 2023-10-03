package com.example.hobbyexplore.data

import java.text.SimpleDateFormat

data class CalendarEvent(
    val eventId: String,
    val eventDate: String?,
    val eventRating: Int?,
    val eventImage: String,
    val eventContent: String
)
{
 constructor():this("","",0, "", "")
}