package com.example.hobbyexplore.hobbyboards

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class HobbyBoardsViewModel : ViewModel() {
    val db = Firebase.firestore
}