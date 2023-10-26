package brian.project.hobbyexplore

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import brian.project.hobbyexplore.calendar.CalendarFragment
import org.junit.Test

import org.junit.Assert.*
import org.junit.Rule
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import java.util.Calendar

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(MockitoJUnitRunner::class)
class ExampleUnitTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val calendarFragment = CalendarFragment()


    @Test
    fun testGetCurrentDateString() {

        val mockCalendar = mock(Calendar::class.java)


        `when`(mockCalendar.get(Calendar.YEAR)).thenReturn(2023)
        `when`(mockCalendar.get(Calendar.MONTH)).thenReturn(9)
        `when`(mockCalendar.get(Calendar.DAY_OF_MONTH)).thenReturn(23)


        val result = calendarFragment.getCurrentDateString(mockCalendar)

        println(result)
        assertEquals("2023/10/23", result)

    }
}