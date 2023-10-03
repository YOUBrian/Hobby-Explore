package com.example.hobbyexplore

import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Date

@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()
        Glide.with(imgView.context)
            .load(imgUri)
            .into(imgView)
    }
}

@BindingAdapter("timeStamp")
fun timestampToDateString(textView: TextView, timestamp: Long){
    val dateFormat = SimpleDateFormat("yyyy.MM.dd HH:mm")
    val date = Date(timestamp)
    textView.text = dateFormat.format(date)
}

