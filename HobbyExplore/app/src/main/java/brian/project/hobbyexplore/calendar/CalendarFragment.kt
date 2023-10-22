package brian.project.hobbyexplore.calendar

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
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
import androidx.fragment.app.viewModels
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
    // Binding and Views
    private lateinit var binding: FragmentCalendarBinding
    private lateinit var lineChart2: LineChart

    // Firebase & Data-related
    private lateinit var firestore: FirebaseFirestore
    private var currentEventId: String? = null
    private var selectedPhotoUri: Uri? = null
    private var dateObserver: Observer<CalendarEvent?>? = null
    private var currentDocumentId: String? = null


    // Fragment State
    var stringDateSelected: String? = null

    private var year: Int = 0
    private var month: Int = 0
    private var day: Int = 0

    private val viewModel: CalendarViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        binding = FragmentCalendarBinding.inflate(inflater)


        val logInSharedPref =
            activity?.getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
        val userId = logInSharedPref?.getString("userId", "N/A")

        lineChart2 = binding.chart2
        firestore = FirebaseFirestore.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference("calendarData")

        setupInitialData()

        setUpUIObservers(viewModel)
        setUpListeners(viewModel)
        setInitialValues(viewModel)

        setLineChartData(viewModel)
        viewModel.dataList.observe(viewLifecycleOwner, Observer { updatedDataList ->
            Log.d("DEBUG", "Data List: $updatedDataList")
            setLineChartData(viewModel)
        })

        viewModel.selectedPhotoUri.observe(viewLifecycleOwner, Observer { uri ->
            uri?.let {
                Glide.with(this@CalendarFragment).load(it).into(binding.calendarImage)
                binding.calendarImage.visibility = View.VISIBLE
                binding.calendarImageCardView.visibility = View.VISIBLE
            }
        })


        handleDateChange(viewModel, year, month, day)

        viewModel.getCalendarData(userId.toString())

        Log.d("CalendarFragment", "ViewModel: $viewModel")



        return binding.root

    }
    private fun setupInitialData() {
        val currentDate = Calendar.getInstance()
        stringDateSelected = getCurrentDateString(currentDate)
    }

    private fun getCurrentDateString(date: Calendar): String {
        year = date.get(Calendar.YEAR)
        month = date.get(Calendar.MONTH)
        day = date.get(Calendar.DAY_OF_MONTH)
        return "$year/${String.format("%02d", month + 1)}/${String.format("%02d", day)}"
    }

    private fun setUpUIObservers(viewModel: CalendarViewModel) {
        viewModel.uploadPhoto.observe(viewLifecycleOwner, Observer { newImageUri ->
            if (isAdded()) {
                Glide.with(this).load(newImageUri).into(binding.calendarImage)
            }
        })
    }

    private fun setUpListeners(viewModel: CalendarViewModel) {
        binding.calendarImageButton.setOnClickListener {
            handleImageSelection()
            binding.calendarImageCardView.visibility = View.VISIBLE
        }
        binding.recordRatingButton.setOnClickListener {
            binding.recordRatingButton.text = getString(R.string.confirm_edit)
            handleRecordButtonPress(viewModel)
        }
        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            handleDateChange(viewModel, year, month, dayOfMonth)
        }
        binding.ratingSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.ratingTextview.text = getString(R.string.progress_text, progress)
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
        val logInSharedPref = activity?.getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
        val userId = logInSharedPref?.getString("userId", "N/A") ?: "N/A"

        viewModel.handleRecordButtonPress(
            stringDateSelected ?: "",
            viewModel.progress.value ?: 0,
            binding.calendarInputContent.text.toString(),
            userId
        )

        viewModel.eventSaveStatus.observe(viewLifecycleOwner, Observer { status ->
            when(status) {
                Status.SUCCESS -> {
                    playSuccessAnimation()
                }
                Status.FAILURE -> {
                    Toast.makeText(requireContext(), "Error: Operation failed", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    // do noting
                }
            }
        })

        viewModel.getCalendarData(userId)
    }

    private fun handleDateChange(
        viewModel: CalendarViewModel,
        year: Int,
        month: Int,
        dayOfMonth: Int
    ) {
        val logInSharedPref = activity?.getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
        val userId = logInSharedPref?.getString("userId", "N/A") ?: "N/A"

        stringDateSelected = "$year/${String.format("%02d", month + 1)}/${String.format("%02d", dayOfMonth)}"

        viewModel.handleDateChange(year, month, dayOfMonth, userId)

        viewModel.specificDateData.observe(viewLifecycleOwner, Observer { event ->
            updateUIBasedOnEvent(event)
            currentDocumentId = event?.eventId
        })

    }

    private fun updateUIBasedOnEvent(event: CalendarEvent?) {
        if (isAdded) {
            if (event != null) {
                currentEventId = event.eventId
                binding.ratingSeekBar.progress = event.eventRating?.toInt() ?: 0
                binding.ratingTextview.text = event.eventRating?.toString() ?: getString(R.string.unrated)

                if (event.eventImage?.isNotBlank() == true) {
                    Glide.with(this).load(event.eventImage).into(binding.calendarImage)
                    binding.calendarImage.visibility = View.VISIBLE
                } else {
                    binding.calendarImage.visibility = View.GONE
                }

                binding.calendarInputContent.setText(event.eventContent)
                binding.recordRatingButton.text = getString(R.string.confirm_edit)
                binding.calendarImageCardView.visibility =
                    if (event.eventImage?.isNotBlank() == true) View.VISIBLE else View.GONE

            } else {
                currentEventId = null
                binding.ratingSeekBar.progress = 0
                binding.ratingTextview.text = getString(R.string.unrated)
                binding.recordRatingButton.visibility = View.VISIBLE
                binding.recordRatingButton.isEnabled = true
                binding.recordRatingButton.alpha = 1f
                binding.recordRatingButton.text = getString(R.string.save_record)
                binding.calendarInputContent.text = null
                binding.calendarImage.setImageDrawable(null)
                binding.calendarImageCardView.visibility = View.GONE
            }
        }
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

        val entries = viewModel.getFormattedDataList()
        val mockData = viewModel.generateMockData()

        val lineDataSet = LineDataSet(entries, getString(R.string.my_learning_score_curve))
        lineDataSet.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
        lineDataSet.color = ContextCompat.getColor(requireContext(), R.color.orange)
        lineDataSet.setCircleColor(Color.RED)
        lineDataSet.valueFormatter = intValueFormatter
        lineDataSet.valueTextSize = 8f

        val markDataSet = LineDataSet(mockData, getString(R.string.average_learning_curve))
        markDataSet.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
        markDataSet.color = ContextCompat.getColor(requireContext(), R.color.black)
        markDataSet.setCircleColor(Color.DKGRAY)
        markDataSet.valueFormatter = intValueFormatter
        markDataSet.valueTextSize = 8f

        val dataSets = ArrayList<ILineDataSet>()
        dataSets.add(lineDataSet)
        dataSets.add(markDataSet)

        val data = LineData(dataSets)

        lineChart2.setExtraOffsets(0f, 0f, 0f, 8f)
        lineChart2.axisLeft.isEnabled = true
        lineChart2.xAxis.isEnabled = true
        lineChart2.description.isEnabled = false
        lineChart2.axisRight.isEnabled = false
        lineChart2.data = data
        lineChart2.invalidate()
        lineChart2.animateXY(1000, 1000)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 10 && resultCode == Activity.RESULT_OK) {
            viewModel.handleSelectedPhoto(data)
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

                val sharedPref = activity?.getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
                val userIdFromPref = sharedPref?.getString("userId", null)

                if (userIdFromPref != null) {
                    stringDateSelected?.let { viewModel.fetchEventForDate(userIdFromPref, it) }

                    viewModel.eventForDate.observe(viewLifecycleOwner, Observer { event ->
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
                            Toast.makeText(requireContext(), getString(R.string.no_data_for_date), Toast.LENGTH_SHORT).show()
                        }
                    })
                } else {
                    Toast.makeText(requireContext(), getString(R.string.cannot_get_user_id), Toast.LENGTH_SHORT).show()
                }

                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun playSuccessAnimation() {
        val animationView = binding.success
        animationView.visibility = View.VISIBLE
        animationView.playAnimation()

        animationView.addAnimatorListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                animationView.visibility = View.GONE
            }
        })
    }
}
