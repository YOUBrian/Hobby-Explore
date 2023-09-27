package com.example.hobbyexplore.calendar

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.example.hobbyexplore.R
import com.example.hobbyexplore.data.CalendarEvent
import com.example.hobbyexplore.databinding.FragmentCalendarBinding
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
        viewModel.getCalendarData()
        Log.i("ssssssssss", "fffffffff")
        firestore = FirebaseFirestore.getInstance()


//        var rating  = viewModel.progress.value
        val selectedDate = Calendar.getInstance()


        /*-----------------------------------*/
        val lineChart = binding.chart1
        val lineChart2 = binding.chart2
        fun setLineChartData() {

            val markData = ArrayList<Entry>()

            markData.add(Entry(1f, 40f))
            markData.add(Entry(2f, 45f))
            markData.add(Entry(3f, 60f))
            markData.add(Entry(4f, 54f))
            markData.add(Entry(5f, 66f))
            markData.add(Entry(6f, 70f))
            markData.add(Entry(7f, 77f))
            markData.add(Entry(8f, 83f))
            markData.add(Entry(9f, 88f))
            markData.add(Entry(10f, 65f))
            markData.add(Entry(11f, 53f))
            markData.add(Entry(12f, 55f))
            markData.add(Entry(13f, 57f))
            markData.add(Entry(14f, 58f))
            markData.add(Entry(15f, 63f))
            markData.add(Entry(16f, 54f))
            markData.add(Entry(17f, 56f))
            markData.add(Entry(18f, 60f))
            markData.add(Entry(19f, 66f))
            markData.add(Entry(20f, 70f))
            markData.add(Entry(21f, 83f))
            markData.add(Entry(22f, 92f))
            markData.add(Entry(23f, 94f))
            markData.add(Entry(24f, 96f))

            val entries = mutableListOf<Entry>()

//            var xValue = 1f // 初始X轴值
//
//            for ((_, pair) in viewModel.dataList.withIndex()) {
//                entries.add(Entry(xValue, pair.second.toFloat()))
//                xValue += 1f // 每次增加1，确保X轴值为整数
//            }
            for ((index, pair) in viewModel.dataList.withIndex()) {
                entries.add(Entry(index.toFloat()+1, pair.second.toFloat()))
                Log.i("daadadada", "index:${index}")
                Log.i("daadadada", "pair:${pair}")
                Log.i("daadadada", "entries:${entries}")
            }

            val lineDataSet = LineDataSet(entries, "我的學習評分曲線")
            lineDataSet.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
            lineDataSet.color = ContextCompat.getColor(requireContext(), R.color.orange)

            val markDataSet = LineDataSet(markData, "平均學習曲線")
            markDataSet.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
            markDataSet.color = ContextCompat.getColor(requireContext(), R.color.black)

            val dataSets = ArrayList<ILineDataSet>()
            dataSets.add(lineDataSet)

            val markDataSets = ArrayList<ILineDataSet>()
            markDataSets.add(markDataSet)

            val data = LineData(dataSets)
            val markData2 = LineData(markDataSets)

            lineChart.data = data
            lineChart.animateXY(1000, 1000)

            lineChart2.data = markData2
//            lineChart2.animateXY(3000, 3000)
        }

        /*-----------------------------------*/


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
            val formattedMonth = String.format("%02d", month + 1) // 将月份格式化为两位数
            val formattedDay = String.format("%02d", dayOfMonth) // 将日期格式化为两位数
            stringDateSelected = "$year/$formattedMonth/$formattedDay"

            Log.d("CalendarFragment", "Date selected: $year-$formattedMonth-$formattedDay")
            selectedDate.set(year, month, dayOfMonth)

            binding.recordRatingButton.setOnClickListener {
                val event = CalendarEvent(
                    eventId = UUID.randomUUID().toString(),
                    eventDate = stringDateSelected,
                    eventRating = viewModel.progress.value
                )

                saveEventToFirestore(event)
                databaseReference.child(stringDateSelected).setValue(binding.ratingTextview.text.toString())
                CoroutineScope(Dispatchers.Main).launch {
                    viewModel.getCalendarData()
                    delay(1000)
                    setLineChartData()
                    Log.i("daadadada", "222222")
                }
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
//                    setLineChartData()
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

        setLineChartData()
        Log.i("ssssssssss", "sdddddddddd")
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