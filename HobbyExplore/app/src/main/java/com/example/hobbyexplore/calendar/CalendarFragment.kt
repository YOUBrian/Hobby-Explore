package com.example.hobbyexplore.calendar

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.Toast
import com.example.hobbyexplore.R
import com.example.hobbyexplore.data.CalendarEvent
import com.example.hobbyexplore.databinding.FragmentCalendarBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar
import java.util.UUID

class CalendarFragment : Fragment() {
    private lateinit var firestore: FirebaseFirestore

    companion object {
        fun newInstance() = CalendarFragment()
    }

    private lateinit var viewModel: CalendarViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentCalendarBinding.inflate(inflater)
        firestore = FirebaseFirestore.getInstance()

        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance()
            Log.d("CalendarFragment", "Date selected: $year-$month-$dayOfMonth")
            selectedDate.set(year, month, dayOfMonth)

            // 创建一个 CalendarEvent 对象并将其保存到 Firestore
            val event = CalendarEvent(
                eventId = UUID.randomUUID().toString(), // 生成唯一的事件ID
                eventDate = selectedDate.timeInMillis,
                eventName = "Your Event Name"
            )

            saveEventToFirestore(event)
        }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CalendarViewModel::class.java)
        // TODO: Use the ViewModel
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