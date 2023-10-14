package com.example.hobbyexplore

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
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import com.example.hobbyexplore.databinding.ActivityMainBinding
import com.example.hobbyexplore.ext.getVmFactory
import com.example.hobbyexplore.util.CurrentFragmentType
import com.example.hobbyexplore.util.Logger
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.launch

//import com.google.firebase.FirebaseApp

class MainActivity : BaseActivity() {

    val viewModel by viewModels<MainViewModel> { getVmFactory() }

    private lateinit var binding: ActivityMainBinding

    // get the height of status bar from system
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
        if (!isNetworkConnected(this)) {
            AlertDialog.Builder(this)
                .setTitle("沒有網路連線")
                .setMessage("請檢查您的網路連線並重試.")
                .setPositiveButton("離開") { _, _ -> finish() }
                .setCancelable(false)
                .show()
        }

//        setSupportActionBar(toolbar)
        FirebaseApp.initializeApp(this)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        viewModel.navigateToProfileByBottomNav.observe(
            this,
            Observer {
                it?.let {
                    binding.bottomNavView.selectedItemId = R.id.navigation_profile
                    viewModel.onProfileNavigated()
                }
            }
        )

        val navController = findNavController(R.id.nav_host_fragment)

//        dataSource.getAllOrders().observe(this){
//            navView.getOrCreateBadge(R.id.navigation_cart_menu).number = it.size
//        }


        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.personalityTestFragment ||
                destination.id == R.id.whetherTakeMbtiTest ||
                destination.id == R.id.mbtiTestFragment ||
                destination.id == R.id.mbtiTestResultFragment ||
                destination.id == R.id.chatGptFragment ||
                destination.id == R.id.budget_input ||
                destination.id == R.id.googleLogInFragment) {
                binding.bottomNavView.visibility = View.GONE
            }
            else {
                binding.bottomNavView.visibility = View.VISIBLE
            }
        }
        setupToolbar()
        setupBottomNav()
        setupNavController()
    }

    private fun setupBottomNav() {

        binding.bottomNavView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_hobbyCategory -> {
                    findNavController(R.id.nav_host_fragment).navigate(NavigationDirections.navigateToHobbyCategoryFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.navigation_calendar -> {

                    findNavController(R.id.nav_host_fragment).navigate(NavigationDirections.navigateToCalendarFragment("",""))
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
}