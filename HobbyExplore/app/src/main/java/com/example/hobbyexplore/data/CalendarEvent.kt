package com.example.hobbyexplore.data

import java.text.SimpleDateFormat

data class CalendarEvent(
    val eventId: String,
    val eventDate: String,
    val eventRating: Int?
)
{
 constructor():this("","",0)
}