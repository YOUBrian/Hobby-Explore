package com.example.hobbyexplore.calendar

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.hobbyexplore.R
import com.example.hobbyexplore.data.CalendarEvent
import com.example.hobbyexplore.databinding.FragmentCalendarBinding
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet

private lateinit var stringDateSelected: String
private lateinit var databaseReference: DatabaseReference

class CalendarFragment : Fragment() {

    private lateinit var firestore: FirebaseFirestore
    private var selectedPhotoUri: Uri? = null
    private lateinit var binding: FragmentCalendarBinding
    private lateinit var lineChart: LineChart
    private lateinit var lineChart2: LineChart

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewModel: CalendarViewModel = ViewModelProvider(this).get(CalendarViewModel::class.java)
        setHasOptionsMenu(true)
        binding = FragmentCalendarBinding.inflate(inflater)
        lineChart = binding.chart1
        lineChart2 = binding.chart2
        firestore = FirebaseFirestore.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference("calendarData")

        setUpUIObservers(viewModel)
        setUpListeners(viewModel)
        setInitialValues(viewModel)
        viewModel.getCalendarData()
        setLineChartData(viewModel)
        viewModel.dataList.observe(viewLifecycleOwner, Observer { updatedDataList ->
            setLineChartData(viewModel)
        })

        viewModel.ratingDate.observe(viewLifecycleOwner, Observer { events ->
            // 更新UI以顯示events中的數據。 例如，您可以設置一個TextView來顯示評分。
            if (events.isNotEmpty()) {
                val event = events[0]
                binding.ratingTextview.text = event.eventRating.toString()
                // ... 更新其他UI元素 ...
            }
        })

        Log.d("CalendarFragment", "ViewModel: $viewModel")
        return binding.root
    }

    private fun setUpUIObservers(viewModel: CalendarViewModel) {
        viewModel.uploadPhoto.observe(viewLifecycleOwner, Observer { newImageUri ->
            Glide.with(this).load(newImageUri).into(binding.calendarImage)
        })
    }

    private fun setUpListeners(viewModel: CalendarViewModel) {
        binding.calendarImageButton.setOnClickListener { handleImageSelection() }
        binding.recordRatingButton.setOnClickListener {
            handleRecordButtonPress(viewModel)
        }
        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            handleDateChange(viewModel, year, month, dayOfMonth)
        }
        binding.ratingSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.ratingTextview.text = "$progress / 100"
                viewModel.progress.value = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

//        binding.shareButton.setOnClickListener {
//            firestore.collection("calendarData")
//                .whereEqualTo("eventDate", stringDateSelected)
//                .get()
//                .addOnSuccessListener { querySnapshot ->
//                    val event = querySnapshot.documents.firstOrNull()?.toObject(CalendarEvent::class.java)
//                    event?.let {
//                        val contentFromFirebase = it.eventContent
//                        val imageUrlFromFirebase = it.eventImage
//                        findNavController().navigate(CalendarFragmentDirections.actionCalendarFragmentToPostFragment(contentFromFirebase,imageUrlFromFirebase))
//                    }
//                }
//        }


    }

    private fun handleImageSelection() {
        if (hasWritePermission()) {
            val intent = Intent().apply {
                type = "image/*"
                action = Intent.ACTION_PICK
            }
            startActivityForResult(intent, 10)
        } else {
            requestWritePermission()
        }
    }

    private fun handleRecordButtonPress(viewModel: CalendarViewModel) {
        lifecycleScope.launch(Dispatchers.Main) {
            val imageUrl = selectedPhotoUri?.let { uploadImageToFirebase(it) } ?: ""
            val event = CalendarEvent(
                eventId = UUID.randomUUID().toString(),
                eventDate = stringDateSelected,
                eventRating = viewModel.progress.value,
                eventImage = imageUrl,
                eventContent = binding.calendarInputContent.text.toString()
            )
            saveEventToFirestore(event)
            databaseReference.child(stringDateSelected).setValue(binding.ratingTextview.text.toString())
            viewModel.getCalendarData()
            delay(1000)
            setLineChartData(viewModel)
        }
    }

    private fun handleDateChange(viewModel: CalendarViewModel, year: Int, month: Int, dayOfMonth: Int) {
        val formattedMonth = String.format("%02d", month + 1)
        val formattedDay = String.format("%02d", dayOfMonth)
        stringDateSelected = "$year/$formattedMonth/$formattedDay"

        viewModel.getDataForSpecificDate(stringDateSelected).observe(viewLifecycleOwner) { event ->
            event?.let {
                // 更新UI以顯示從Firestore獲取的數據
                binding.ratingTextview.text = it.eventRating.toString()
                Glide.with(this).load(it.eventImage).into(binding.calendarImage)
                binding.calendarInputContent.setText(it.eventContent)
                binding.recordRatingButton.alpha = 0.5f
                binding.recordRatingButton.isEnabled = false
            } ?: run {
                // 如果沒有數據或查詢失敗，更新UI以顯示默認值或錯誤消息
                binding.recordRatingButton.visibility = View.VISIBLE
                binding.recordRatingButton.isEnabled = true
                binding.recordRatingButton.alpha = 1f
                binding.calendarInputContent.text = null
                binding.calendarImage.setImageDrawable(null)
            }
        }
    }


    private fun hasWritePermission(): Boolean {
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestWritePermission() {
        ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
    }

    private fun setInitialValues(viewModel: CalendarViewModel) {
        binding.ratingTextview.text = "50 / 100"
        selectedPhotoUri?.let { Glide.with(this).load(it).into(binding.calendarImage) }
        setLineChartData(viewModel)
    }

    private fun setLineChartData(viewModel: CalendarViewModel) {

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

        viewModel.dataList.value?.let { list ->
            for ((index, pair) in list.withIndex()) {
                Log.i("LineChartData", "Index: $index, Pair: $pair")
                entries.add(Entry(index.toFloat() + 1, pair.second.toFloat()))
            }
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

    private fun calendarClicked(viewModel: CalendarViewModel) {
        databaseReference.child(stringDateSelected).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.value?.let { binding.ratingTextview.text = it.toString() }
                viewModel.getDateData()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 10 && resultCode == Activity.RESULT_OK) {
            selectedPhotoUri = data?.data
            selectedPhotoUri?.let { Glide.with(this).load(it).into(binding.calendarImage) }
        }
    }

    private suspend fun uploadImageToFirebase(selectedPhotoUri: Uri): String {
        val storageReference = FirebaseStorage.getInstance().reference
        val imageRef = storageReference.child("images/${UUID.randomUUID()}.jpg")

        return try {
            imageRef.putFile(selectedPhotoUri).await()
            imageRef.downloadUrl.await().toString()
        } catch (e: Exception) {
            Log.e("uploadImageToFirebase", "Upload failed", e)
            ""
        }
    }

    private fun saveEventToFirestore(event: CalendarEvent) {
        firestore.collection("calendarData")
            .document(event.eventId)
            .set(event)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Event saved!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
    // Toolbar share fun
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
        super.onCreateOptionsMenu(menu, inflater)
        menu.findItem(R.id.boards_post).isVisible = false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.calendar_share -> {
                firestore.collection("calendarData")
                    .whereEqualTo("eventDate", stringDateSelected)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        val event = querySnapshot.documents.firstOrNull()?.toObject(CalendarEvent::class.java)
                        event?.let {
                            val contentFromFirebase = it.eventContent
                            val imageUrlFromFirebase = it.eventImage
                            findNavController().navigate(CalendarFragmentDirections.actionCalendarFragmentToPostFragment(contentFromFirebase,imageUrlFromFirebase))
                        }
                    }
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}
