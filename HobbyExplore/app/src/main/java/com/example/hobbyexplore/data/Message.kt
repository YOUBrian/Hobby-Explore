package com.example.hobbyexplore.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Message(
    val id: String,
    val name: String,
    val content: String,
    val createdTime: Long,
    val image: String,
    val rating: Int,
    val category: String
): Parcelable
{
    constructor() : this( "", "", "", 0L, "",0, "")
}
