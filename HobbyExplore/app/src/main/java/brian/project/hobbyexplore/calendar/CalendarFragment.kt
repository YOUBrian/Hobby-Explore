package brian.project.hobbyexplore.calendar

import android.Manifest
import android.app.Activity
import android.content.Context
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
import android.widget.EditText
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import brian.project.hobbyexplore.R
import brian.project.hobbyexplore.data.CalendarEvent
import brian.project.hobbyexplore.databinding.FragmentCalendarBinding
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
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomnavigation.BottomNavigationView


private lateinit var databaseReference: DatabaseReference

class CalendarFragment : Fragment() {
    var stringDateSelected: String? = null
    private lateinit var firestore: FirebaseFirestore
    private var selectedPhotoUri: Uri? = null
    private lateinit var binding: FragmentCalendarBinding
    private lateinit var lineChart: LineChart
    private lateinit var lineChart2: LineChart
    private var currentEventId: String? = null
    private var dateObserver: Observer<CalendarEvent?>? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewModel: CalendarViewModel =
            ViewModelProvider(this).get(CalendarViewModel::class.java)
        setHasOptionsMenu(true)
        binding = FragmentCalendarBinding.inflate(inflater)

        val currentDate = Calendar.getInstance()
        val year = currentDate.get(Calendar.YEAR)
        val month = currentDate.get(Calendar.MONTH)  // Calendar.MONTH returns 0-based month
        val day = currentDate.get(Calendar.DAY_OF_MONTH)
        val logInSharedPref = activity?.getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
        val userId = logInSharedPref?.getString("userId", "N/A")

        stringDateSelected = "$year/${String.format("%02d", month)}/${String.format("%02d", day)}"

//        lineChart = binding.chart1
        lineChart2 = binding.chart2
        firestore = FirebaseFirestore.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference("calendarData")

        setUpUIObservers(viewModel)
        setUpListeners(viewModel)
        setInitialValues(viewModel)

        setLineChartData(viewModel)
        viewModel.dataList.observe(viewLifecycleOwner, Observer { updatedDataList ->
            Log.d("DEBUGGGGG", "Data List: $updatedDataList")
            setLineChartData(viewModel)
        })

        handleDateChange(viewModel, year, month, day)

        viewModel.getCalendarData(userId.toString())

        Log.d("CalendarFragment", "ViewModel: $viewModel")



        return binding.root

    }

    private fun setUpUIObservers(viewModel: CalendarViewModel) {
        viewModel.uploadPhoto.observe(viewLifecycleOwner, Observer { newImageUri ->
            Glide.with(this).load(newImageUri).into(binding.calendarImage)
        })
    }

    private fun setUpListeners(viewModel: CalendarViewModel) {


        binding.calendarImageButton.setOnClickListener {
            handleImageSelection()
            binding.calendarImageCardView.visibility = View.VISIBLE
        }
        binding.recordRatingButton.setOnClickListener {
            binding.recordRatingButton.text = "修改"
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
            val logInSharedPref = activity?.getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
            val userId = logInSharedPref?.getString("userId", "N/A")
            val imageUrl = selectedPhotoUri?.let { uploadImageToFirebase(it) } ?: ""

            val event = CalendarEvent(
                eventId = currentEventId ?: UUID.randomUUID().toString(),
                eventDate = stringDateSelected,
                eventRating = viewModel.progress.value,
                eventImage = imageUrl,
                eventContent = binding.calendarInputContent.text.toString(),
                eventUserId = userId.toString()
            )

            saveEventToFirestore(event)
                .addOnSuccessListener {
                    currentEventId = event.eventId // update currentEventId
                    Toast.makeText(requireContext(), "Event saved!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                }

            stringDateSelected?.let {
                databaseReference.child(it)
                    .setValue(binding.ratingTextview.text.toString())
            }
            viewModel.getCalendarData(userId.toString())
            delay(2000)
//            setLineChartData(viewModel)
        }
    }

    private fun handleDateChange(viewModel: CalendarViewModel, year: Int, month: Int, dayOfMonth: Int) {
        val formattedMonth = String.format("%02d", month + 1)
        val formattedDay = String.format("%02d", dayOfMonth)
        val logInSharedPref = activity?.getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
        val userId = logInSharedPref?.getString("userId", "N/A")
        stringDateSelected = "$year/$formattedMonth/$formattedDay"

        dateObserver?.let {
            viewModel.specificDateData.removeObserver(it)
        }

        dateObserver = Observer { event ->
            if (event != null) {
                currentEventId = event.eventId
                binding.ratingSeekBar.progress = event.eventRating!!.toInt()
                binding.ratingTextview.text = event.eventRating.toString()

                if (event.eventImage?.isNotBlank() == true) {
                    Glide.with(this).load(event.eventImage).into(binding.calendarImage)
                    binding.calendarImage.visibility = View.VISIBLE // 如果有圖片，設為可見
                } else {
                    binding.calendarImage.visibility = View.GONE // 如果沒有圖片，設為不可見
                }

                binding.calendarInputContent.setText(event.eventContent)
                binding.recordRatingButton.text = "修改"
                binding.calendarImageCardView.visibility = if (event.eventImage?.isNotBlank() == true) View.VISIBLE else View.GONE
            } else {
                currentEventId = null
                binding.ratingSeekBar.progress = 0
                binding.ratingTextview.text = "未評分"
                binding.recordRatingButton.visibility = View.VISIBLE
                binding.recordRatingButton.isEnabled = true
                binding.recordRatingButton.alpha = 1f
                binding.recordRatingButton.text = "儲存"
                binding.calendarInputContent.text = null
                binding.calendarImage.setImageDrawable(null)
                binding.calendarImageCardView.visibility = View.GONE
            }
        }

        viewModel.getDataForSpecificDate(stringDateSelected!!, userId.toString())
        viewModel.specificDateData.observe(viewLifecycleOwner, dateObserver!!)
    }






    private fun hasWritePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestWritePermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            1
        )
    }

    private fun setInitialValues(viewModel: CalendarViewModel) {
        selectedPhotoUri?.let { Glide.with(this).load(it).into(binding.calendarImage) }
        setLineChartData(viewModel)
    }


    class IntegerValueFormatter : ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            return value.toInt().toString()
        }
    }
    private fun setLineChartData(viewModel: CalendarViewModel) {
        val intValueFormatter = IntegerValueFormatter()

        lineChart2.xAxis.granularity = 1f
        lineChart2.xAxis.valueFormatter = intValueFormatter
        lineChart2.xAxis.position = XAxis.XAxisPosition.BOTTOM
        lineChart2.axisLeft.granularity = 1f
        lineChart2.axisLeft.valueFormatter = intValueFormatter
        lineChart2.axisRight.granularity = 1f
        lineChart2.axisRight.valueFormatter = intValueFormatter

        val mockData = ArrayList<Entry>()

        mockData.add(Entry(1f, 40f))
        mockData.add(Entry(2f, 45f))
        mockData.add(Entry(3f, 60f))
        mockData.add(Entry(4f, 54f))
        mockData.add(Entry(5f, 66f))
        mockData.add(Entry(6f, 70f))
        mockData.add(Entry(7f, 77f))
        mockData.add(Entry(8f, 83f))
        mockData.add(Entry(9f, 88f))
        mockData.add(Entry(10f, 65f))
        mockData.add(Entry(11f, 53f))
        mockData.add(Entry(12f, 55f))
        mockData.add(Entry(13f, 57f))
        mockData.add(Entry(14f, 58f))
        mockData.add(Entry(15f, 63f))
        mockData.add(Entry(16f, 54f))
        mockData.add(Entry(17f, 56f))
        mockData.add(Entry(18f, 60f))
        mockData.add(Entry(19f, 92f))
        mockData.add(Entry(20f, 94f))
        mockData.add(Entry(21f, 96f))

        val entries = mutableListOf<Entry>()

        viewModel.dataList.value?.let { list ->
            for ((index, pair) in list.withIndex()) {
                entries.add(Entry(index.toFloat() + 1, pair.second.toFloat()))
            }
        }

        val lineDataSet = LineDataSet(entries, "我的學習評分曲線")
        lineDataSet.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
        lineDataSet.color = ContextCompat.getColor(requireContext(), R.color.orange)

        val markDataSet = LineDataSet(mockData, "學員平均學習曲線")
        markDataSet.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
        markDataSet.color = ContextCompat.getColor(requireContext(), R.color.black)

        val dataSets = ArrayList<ILineDataSet>()
        dataSets.add(lineDataSet)
        dataSets.add(markDataSet) //

        val data = LineData(dataSets)

        lineChart2.axisLeft.isEnabled = true
        lineChart2.xAxis.isEnabled = true
        lineChart2.description.isEnabled = false
        lineChart2.axisRight.isEnabled = false
        lineChart2.data = data
        lineChart2.invalidate() //
        lineChart2.animateXY(1000, 1000)
    }

    /*-----------------------------------*/

    private fun calendarClicked(viewModel: CalendarViewModel) {
        stringDateSelected?.let {
            databaseReference.child(it)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.value?.let { binding.ratingTextview.text = it.toString() }
                        viewModel.getDateData()
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 10 && resultCode == Activity.RESULT_OK) {
            selectedPhotoUri = data?.data
            selectedPhotoUri?.let {
                Glide.with(this).load(it).into(binding.calendarImage)
                binding.calendarImage.visibility = View.VISIBLE
                binding.calendarImageCardView.visibility = View.VISIBLE
            }
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

    private fun saveEventToFirestore(event: CalendarEvent): Task<Void> {
        return firestore.collection("calendarData")
            .document(event.eventId)
            .set(event)
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

                val sharedPref = activity?.getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
                val userIdFromPref = sharedPref?.getString("userId", null)

                if (userIdFromPref != null) {
                    firestore.collection("calendarData")
                        .whereEqualTo("eventUserId", userIdFromPref)
                        .whereEqualTo("eventDate", stringDateSelected)
                        .get()
                        .addOnSuccessListener { querySnapshot ->
                            val event = querySnapshot.documents.firstOrNull()
                                ?.toObject(CalendarEvent::class.java)
                            if (event != null) {
                                val contentFromFirebase = event.eventContent
                                val imageUrlFromFirebase = event.eventImage
                                findNavController().navigate(
                                    CalendarFragmentDirections.actionCalendarFragmentToPostFragment(
                                        contentFromFirebase,
                                        imageUrlFromFirebase
                                    )
                                )

                                val bottomNavigation = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavView)
                                bottomNavigation.menu.findItem(R.id.navigation_hobbyBoards).isChecked = true


                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "尚未有該日期的數據!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                } else {
                    Toast.makeText(requireContext(), "無法獲取用戶ID", Toast.LENGTH_SHORT).show()
                }

                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun saveOrUpdateEventToFirestore(event: CalendarEvent) {
        firestore.collection("calendarData")
            .document(event.eventId)
            .set(event)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Event saved or updated!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

}
