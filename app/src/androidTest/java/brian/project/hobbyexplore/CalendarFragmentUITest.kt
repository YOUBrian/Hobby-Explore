package brian.project.hobbyexplore

import android.os.SystemClock
import android.view.MotionEvent
import android.view.View
import android.widget.ScrollView
import android.widget.SeekBar
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.swipeLeft
import androidx.test.espresso.action.ViewActions.swipeRight
import androidx.test.espresso.action.ViewActions.swipeUp
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import brian.project.hobbyexplore.calendar.CalendarFragment
import brian.project.hobbyexplore.googlelogin.GoogleLogInFragment
import org.hamcrest.Matcher
import org.hamcrest.core.AllOf.allOf
//import androidx.test.espresso.matcher.ViewMatchers.matches
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CalendarFragmentUITest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setup() {
        activityRule.scenario.onActivity { activity ->
            activity.supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, CalendarFragment())
                .commitNow()
        }
    }


    @Test
    fun testCalendarButtonClick() {
        Thread.sleep(2000)

        onView(withId(R.id.calendar_input_content))
            .perform(typeText("EditText Test!!"))
        Thread.sleep(2000)

        onView(withId(R.id.rating_seekBar))
            .perform(setProgress(95))
        Thread.sleep(2000)

        onView(withId(R.id.record_rating_Button))
            .perform(click())
        Thread.sleep(2000)

        onView(withId(R.id.calendar_scroll_view))
            .perform(swipeUp())
        Thread.sleep(2000)

        onView(withId(R.id.rating_seekBar))
            .perform(setProgress(60))
        Thread.sleep(2000)

        onView(withId(R.id.record_rating_Button))
            .perform(click())
        Thread.sleep(2000)

    }

    private fun setProgress(progress: Int): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return isAssignableFrom(SeekBar::class.java)
            }

            override fun getDescription(): String {
                return "set progress on a SeekBar"
            }

            override fun perform(uiController: UiController, view: View) {
                val seekBar = view as SeekBar
                seekBar.progress = progress
            }
        }
    }
}
