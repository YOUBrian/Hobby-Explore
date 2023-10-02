package com.example.hobbyexplore.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Introduce(
    val image : String,
    val title : String,
    val content : String,
    val name : String
): Parcelable
{
    constructor() : this( "", "","", "")
}
@Parcelize
data class Appliance(
    val image : String,
    val title : String,
    val price : String,
    val content : String
): Parcelable
{
    constructor() : this("", "", "","")
}
@Parcelize
data class Course(
    val link : String,
    val title : String,
    val price : String,
    val content : String,
    val image : String
): Parcelable
{
    constructor() : this("", "", "","","")
}
@Parcelize
data class Place(
    val image : String,
    val title : String,
    val price : String,
    val content : String
): Parcelable
{
    constructor() : this("", "", "","")
}


