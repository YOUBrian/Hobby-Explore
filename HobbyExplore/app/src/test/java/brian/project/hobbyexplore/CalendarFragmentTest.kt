package brian.project.hobbyexplore

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.view.View
import android.widget.SeekBar
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.test.core.app.ApplicationProvider
import brian.project.hobbyexplore.calendar.CalendarFragment
import brian.project.hobbyexplore.calendar.CalendarViewModel
import brian.project.hobbyexplore.calendar.SeekBarTest
import brian.project.hobbyexplore.data.CalendarEvent
import brian.project.hobbyexplore.databinding.FragmentCalendarBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.ArgumentMatchers.eq
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import org.mockito.Mockito.spy
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import java.util.Calendar

@RunWith(MockitoJUnitRunner::class)
class CalendarFragmentTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var fragment: SeekBarTest

    @Before
    fun setUp() {
        val viewModel = mock(CalendarViewModel::class.java)

        val realFragment = CalendarFragment()
        realFragment.calendarViewModel = viewModel
        realFragment.calendarBinding = mock(FragmentCalendarBinding::class.java)

        fragment = realFragment
    }

//    @Test
//    fun testSeekBarProgressChanged() {
//        // Prepare
//        val mockSeekBar = mock(SeekBar::class.java)
//        val progress = 50
//        val expectedText = "Progress: $progress"
//
//        `when`(fragment.calendarBinding.ratingSeekBar).thenReturn(mockSeekBar)
//
//        // Mocking the listener and its call
//        val listenerCaptor = ArgumentCaptor.forClass(SeekBar.OnSeekBarChangeListener::class.java)
//        verify(mockSeekBar).setOnSeekBarChangeListener(listenerCaptor.capture())
//        listenerCaptor.value?.onProgressChanged(mockSeekBar, progress, true)
//
//        // Assert
//        verify(fragment.calendarBinding.ratingTextview).setText(expectedText)
//
//        verify(fragment.calendarViewModel).setProgress(progress)
//    }



    @Test
    fun testSeekBar() {
        val mockBinding = mock(FragmentCalendarBinding::class.java)
        val mockViewModel = mock(CalendarViewModel::class.java)

        fragment.calendarBinding = mockBinding
        fragment.calendarViewModel = mockViewModel

        assertEquals(fragment.calendarBinding, mockBinding)
        assertEquals(fragment.calendarViewModel, mockViewModel)
    }

}
