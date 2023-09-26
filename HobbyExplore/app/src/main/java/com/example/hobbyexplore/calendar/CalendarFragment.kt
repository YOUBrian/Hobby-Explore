package com.example.hobbyexplore.calendar

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.SeekBar
import android.widget.Toast
import androidx.databinding.adapters.TextViewBindingAdapter.setText
import com.example.hobbyexplore.R
import com.example.hobbyexplore.data.CalendarEvent
import com.example.hobbyexplore.databinding.FragmentCalendarBinding
import com.example.hobbyexplore.hobbyboards.HobbyBoardsViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar
import java.util.UUID

private lateinit var stringDateSelected: String
private lateinit var databaseReference: DatabaseReference
class CalendarFragment : Fragment() {
    private lateinit var firestore: FirebaseFirestore
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewModel: CalendarViewModel = ViewModelProvider(this).get(CalendarViewModel::class.java)
        val binding = FragmentCalendarBinding.inflate(inflater)
        firestore = FirebaseFirestore.getInstance()

//        var rating  = viewModel.progress.value
        val selectedDate = Calendar.getInstance()


        fun calendarClicked() {
            databaseReference.child(stringDateSelected).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.value != null) {
                        binding.ratingTextview.setText(snapshot.value.toString())
//                        viewModel.getCalendarData()
                        viewModel.getDateData()
                    Log.i("getDateData", "getDateData: ${snapshot.value.toString()}")
                    } else {
//                        binding.ratingTextview.setText("null")
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        }


        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
                stringDateSelected = "$year/${month + 1}/$dayOfMonth"



            Log.d("CalendarFragment", "Date selected: $year-$month-$dayOfMonth")
            selectedDate.set(year, month, dayOfMonth)

            binding.recordRatingButton.setOnClickListener {
                val event = CalendarEvent(
                    eventId = UUID.randomUUID().toString(),
                    eventDate = stringDateSelected,
                    eventRating = viewModel.progress.value
                )
//                Log.i("ratingValue", "ratingValue:$rating")
                saveEventToFirestore(event)
                databaseReference.child(stringDateSelected).setValue(binding.ratingTextview.text.toString())
            }
            calendarClicked()
        }
        databaseReference = FirebaseDatabase.getInstance().getReference("calendarData")

        fun bindViews() {
            val seekBar = binding.ratingSeekBar
            val textView = binding.ratingTextview
            textView.text = "50 / 100"
            seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    textView.text = "$progress / 100"
                    viewModel.progress.value = progress
//                    Log.i("ratingValue", "ratingValue:$rating")
                    Log.i("ratingValue", "progress:$progress")
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // do nothing
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    // do nothing
                }
            })
        }

        bindViews()


        return binding.root
    }

    private fun saveEventToFirestore(event: CalendarEvent) {
        firestore.collection("calendarData")
            .document(event.eventId)
            .set(event)
            .addOnSuccessListener {
                Log.d("CalendarFragment", "aaaa")
                // 事件成功保存到 Firestore
                Toast.makeText(requireContext(), "Event saved!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                // 保存事件失败
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }



}