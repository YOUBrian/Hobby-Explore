package com.example.hobbyexplore.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Introduce(
    val image : String,
    val title : String,
    val content : String
): Parcelable
{
    constructor() : this( "", "","")
}
data class Appliance(
    val image : String,
    val title : String,
    val price : String,
    val content : String
)

data class Course(
    val link : String,
    val title : String,
    val price : String,
    val content : String
)

data class Place(
    val image : String,
    val title : String,
    val price : String,
    val content : String
)


