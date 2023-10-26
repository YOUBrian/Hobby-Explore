package brian.project.hobbyexplore.calendar

import brian.project.hobbyexplore.databinding.FragmentCalendarBinding
import java.util.Calendar

interface DateStringProvider {
    fun getCurrentDateString(date: Calendar): String
}

interface SetUpUIObserversTest {
    fun setUpUIObservers(viewModel: CalendarViewModel)
}

interface HandleImageSelectionTest{
    fun handleImageSelection()
}

interface HasWritePermissionTest{
    fun hasWritePermission(): Boolean
}

interface RequestWritePermissionTest{
    fun requestWritePermission()
}

interface SeekBarTest {
    var calendarViewModel: CalendarViewModel
    var calendarBinding: FragmentCalendarBinding
}