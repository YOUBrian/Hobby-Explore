package com.example.hobbyexplore

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
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
//        setContentView(R.layout.activity_main)
//        setSupportActionBar(toolbar)
        FirebaseApp.initializeApp(this)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

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
                destination.id == R.id.budget_input) {
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
//        val bindingBadge = BadgeBottomBinding.inflate(LayoutInflater.from(this), itemView, true)
//        bindingBadge.lifecycleOwner = this
//        bindingBadge.viewModel = viewModel
    }

    private fun setupNavController() {
        findNavController(R.id.nav_host_fragment).addOnDestinationChangedListener { navController: NavController, _: NavDestination, _: Bundle? ->
            viewModel.currentFragmentType.value = when (navController.currentDestination?.id) {
                R.id.calendarFragment -> CurrentFragmentType.CALENDAR
                R.id.hobbyBoardsFragment -> CurrentFragmentType.BOARDS
                R.id.hobbyCategoryFragment -> CurrentFragmentType.CATEGORY
                R.id.profileFragment -> CurrentFragmentType.PROFILE
                R.id.detailFragment -> CurrentFragmentType.HOBBY_EXPLORE
                R.id.hobbyAppliaceFragment -> CurrentFragmentType.HOBBY_APPLIANCE
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
}
/*---------------------------------------------------------*/
//import android.os.Bundle
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material.Button
//import androidx.compose.material.Card
//import androidx.compose.material.MaterialTheme
//import androidx.compose.material.OutlinedTextField
//import androidx.compose.material.Surface
//import androidx.compose.material.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.lifecycle.viewmodel.compose.viewModel
//import com.example.hobbyexplore.MainViewModel
//import com.google.ai.generativelanguage.v1beta2.Message
//import com.google.ai.generativelanguage.v1beta1.llmsample.ui.theme.LLMSampleTheme
//
//class MainActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContent {
//            LLMSampleTheme {
//                // A surface container using the 'background' color from the theme
//                Surface(
//                    modifier = Modifier.fillMaxSize(),
//                    color = MaterialTheme.colors.background
//                ) {
//                    SampleUi()
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun SampleUi(
//    mainViewModel: MainViewModel = viewModel()
//) {
//    val (inputText, setInputText) = remember { mutableStateOf("") }
//    val messages: List<Message> by mainViewModel.messages.collectAsState()
//    Column(
//        modifier = Modifier.padding(all = 16.dp),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        OutlinedTextField(
//            modifier = Modifier.fillMaxWidth(),
//            value = inputText,
//            onValueChange = setInputText,
//            label = { Text("Input:") }
//        )
//        Button(
//            onClick = {
//                mainViewModel.sendMessage(inputText)
//            },
//            modifier = Modifier.padding(8.dp)
//        ) {
//            Text("Send Message")
//        }
//        LazyColumn(
//            modifier = Modifier.fillMaxWidth(),
//            reverseLayout = true
//        ) {
//            items(messages) { message ->
//                Card(modifier = Modifier.padding(vertical = 2.dp)) {
//                    Column(
//                        modifier = Modifier.padding(8.dp)
//                    ) {
//                        Text(
//                            text = message.author,
//                            style = MaterialTheme.typography.caption,
//                            color = MaterialTheme.colors.secondary
//                        )
//                        Text(
//                            modifier = Modifier.fillMaxWidth(),
//                            text = message.content,
//                            style = MaterialTheme.typography.body1
//                        )
//                    }
//                }
//            }
//        }
//    }
//}
//
//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun DefaultPreview() {
//    LLMSampleTheme {
//        SampleUi()
//    }
//}