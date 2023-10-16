package brian.project.hobbyexplore

import android.content.Context
import android.graphics.Rect
import android.net.ConnectivityManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import brian.project.hobbyexplore.databinding.ActivityMainBinding
import brian.project.hobbyexplore.ext.getVmFactory
import brian.project.hobbyexplore.util.CurrentFragmentType
import brian.project.hobbyexplore.util.Logger
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

//import com.google.firebase.FirebaseApp

class MainActivity : BaseActivity() {
    private lateinit var auth: FirebaseAuth
    val viewModel by viewModels<MainViewModel> { getVmFactory() }
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    private val statusBarHeight: Int
        get() {
            val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
            return when {
                resourceId > 0 -> resources.getDimensionPixelSize(resourceId)
                else -> 0
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        FirebaseApp.initializeApp(this)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as? NavHostFragment
        navController = navHostFragment?.navController ?: return


        //check internet
        if (!isNetworkConnected(this)) {
            showNetworkErrorDialog()
        } else {
            Log.d("DEBUGGGG", "Checking navigation condition...")
            navHostFragment?.view?.post {
                val sharedPreferences = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
                val accountId = sharedPreferences.getString("userId", "N/A")

                if (accountId != "N/A") {
                    // User is signed in
                    Log.d("DEBUGGGG", "$accountId")
                    navController.navigate(R.id.hobbyCategoryFragment)
                } else {
                    // No user is signed in
                    Log.d("DEBUGGGG", "$accountId")
                    navController.navigate(R.id.googleLogInFragment)
                }
            }
        }

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        viewModel.navigateToProfileByBottomNav.observe(this, Observer {
            it?.let {
                binding.bottomNavView.selectedItemId = R.id.navigation_profile
                viewModel.onProfileNavigated()
            }
        })

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id in listOf(
                    R.id.personalityTestFragment,
                    R.id.whetherTakeMbtiTest,
                    R.id.mbtiTestFragment,
                    R.id.mbtiTestResultFragment,
                    R.id.chatGptFragment,
                    R.id.budget_input,
                    R.id.googleLogInFragment
                )
            ) {
                binding.bottomNavView.visibility = View.GONE
            } else {
                binding.bottomNavView.visibility = View.VISIBLE
            }
        }

        setupToolbar()
        setupBottomNav()
        setupNavController()
    }

    private fun showNetworkErrorDialog() {
        AlertDialog.Builder(this)
            .setTitle("沒有網路連線")
            .setMessage("請檢查您的網路連線並重試.")
            .setPositiveButton("重新嘗試") { _, _ ->
                if (isNetworkConnected(this)) {
                    recreate()
                } else {
                    showNetworkErrorDialog()
                }
            }
            .setNegativeButton("離開") { _, _ -> finish() }
            .setCancelable(false)
            .show()
    }

    private fun setupBottomNav() {
        binding.bottomNavView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_hobbyCategory -> {
                    navController.navigate(NavigationDirections.navigateToHobbyCategoryFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.navigation_calendar -> {
                    navController.navigate(NavigationDirections.navigateToCalendarFragment("",""))
                    return@setOnItemSelectedListener true
                }
                R.id.navigation_hobbyBoards -> {

                    findNavController(R.id.nav_host_fragment).navigate(NavigationDirections.navigateToHobbyBoardsFragment())
                    return@setOnItemSelectedListener true
                }

                R.id.navigation_profile -> {

                    findNavController(R.id.nav_host_fragment).navigate(NavigationDirections.navigateToProfileFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.personalityTestFragment -> {
                    binding.bottomNavView.visibility = View.GONE
                    return@setOnItemSelectedListener true
                }
            }

            false
        }
        val menuView = binding.bottomNavView.getChildAt(0) as BottomNavigationMenuView
        val itemView = menuView.getChildAt(2) as BottomNavigationItemView
    }

    private fun setupNavController() {
        findNavController(R.id.nav_host_fragment).addOnDestinationChangedListener { navController: NavController, _: NavDestination, _: Bundle? ->
            viewModel.currentFragmentType.value = when (navController.currentDestination?.id) {
                R.id.calendarFragment -> CurrentFragmentType.CALENDAR
                R.id.hobbyBoardsFragment -> CurrentFragmentType.BOARDS
                R.id.hobbyCategoryFragment -> CurrentFragmentType.CATEGORY
                R.id.profileFragment -> CurrentFragmentType.PROFILE
                R.id.detailFragment -> CurrentFragmentType.HOBBY_EXPLORE
                R.id.hobbyApplianceFragment -> CurrentFragmentType.HOBBY_APPLIANCE
                R.id.hobbyCourseFragment -> CurrentFragmentType.HOBBY_COURSE
                R.id.hobbyPlaceFragment -> CurrentFragmentType.HOBBY_PLACE
                R.id.chatGptFragment -> CurrentFragmentType.RECOMMEND_HOBBY
                R.id.applianceRecommendFragment -> CurrentFragmentType.RECOMMEND_APPLIANCE
                R.id.courseRecommendFragment -> CurrentFragmentType.RECOMMEND_COURSE
                R.id.placeRecommendFragment -> CurrentFragmentType.RECOMMEND_PLACE
                else -> CurrentFragmentType.HOBBY_EXPLORE
            }
        }
    }

    private fun setupToolbar() {

        binding.toolbar.setPadding(0, statusBarHeight, 0, 0)
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.navigationIcon = ContextCompat.getDrawable(this, R.drawable.main_back_icon)
        launch {

            val dpi = resources.displayMetrics.densityDpi.toFloat()
            val dpiMultiple = dpi / DisplayMetrics.DENSITY_DEFAULT

            val cutoutHeight = getCutoutHeight()

            Logger.i("====== ${Build.MODEL} ======")
            Logger.i("$dpi dpi (${dpiMultiple}x)")
            Logger.i("statusBarHeight: ${statusBarHeight}px/${statusBarHeight / dpiMultiple}dp")

            when {
                cutoutHeight > 0 -> {
                    Logger.i("cutoutHeight: ${cutoutHeight}px/${cutoutHeight / dpiMultiple}dp")

                    val oriStatusBarHeight =
                        resources.getDimensionPixelSize(R.dimen.height_status_bar_origin)

                    binding.toolbar.setPadding(0, oriStatusBarHeight, 0, 0)
                    val layoutParams = Toolbar.LayoutParams(
                        Toolbar.LayoutParams.WRAP_CONTENT,
                        Toolbar.LayoutParams.WRAP_CONTENT
                    )
                    layoutParams.gravity = Gravity.CENTER

                    when (Build.MODEL) {
                        "Pixel 5" -> {
                            Logger.i("Build.MODEL is ${Build.MODEL}")
                        }

                        else -> {
                            layoutParams.topMargin = statusBarHeight - oriStatusBarHeight
                        }
                    }
                    binding.textToolbarTitle.layoutParams = layoutParams
                }
            }
            Logger.i("====== ${Build.MODEL} ======")
        }
    }

    fun isNetworkConnected(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    v.isCursorVisible = false
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                } else {
                    v.isCursorVisible = true
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    override fun onBackPressed() {
        val v = currentFocus
        if (v is EditText) {
            v.isCursorVisible = false
            v.clearFocus()
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(v.windowToken, 0)
        }
        super.onBackPressed()
        Log.d("MyActivity", "onBackPressed Called!")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
