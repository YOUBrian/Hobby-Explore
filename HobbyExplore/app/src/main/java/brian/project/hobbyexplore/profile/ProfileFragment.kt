package brian.project.hobbyexplore.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import brian.project.hobbyexplore.R
import brian.project.hobbyexplore.databinding.FragmentProfileBinding
import brian.project.hobbyexplore.googlelogin.GoogleSignInHelper
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

class ProfileFragment : Fragment() {

    private val signInHelper by lazy { GoogleSignInHelper(requireContext()) }
    private lateinit var viewModel: ProfileViewModel
    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        binding = FragmentProfileBinding.inflate(inflater)
        val sharedPref = activity?.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)

        binding.photo = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val mbtiResult = sharedPref?.getString("MBTI_Result", R.string.untested.toString())
        val selectedHobbyTitle = sharedPref?.getString("Selected_Hobby_Title", R.string.unselected.toString())

        val logInSharedPref = activity?.getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
        val userImage = logInSharedPref?.getString("photoUrl", "https://firebasestorage.googleapis.com/v0/b/hobby-explore.appspot.com/o/images%2Fuser%20(2).png?alt=media&token=d7986b56-0d57-44cb-bd8e-1dfce4e45d19&_gl=1*10s2fv6*_ga*MjA2MTUwOTE5LjE2OTI1OTUxNzY.*_ga_CW55HF8NVT*MTY5NzUwNzkzMy4xNDguMS4xNjk3NTA5MDM4LjUyLjAuMA..")
        val userName = logInSharedPref?.getString("displayName", R.string.hobby_explore_friends.toString())

        viewModel.getUserPhoto(userImage.toString())
        binding.nickname.text = "$userName"
        binding.selectHobby.text = selectedHobbyTitle
        binding.mbtiResult.text = "$mbtiResult"

        binding.mbtiButton.setOnClickListener {
            it.findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToWhetherTakeMbtiTest())
        }

        val currentUser = signInHelper.getCurrentUser()
        viewModel.setLoggedIn(currentUser != null)

        binding.loginLogoutButton.setOnClickListener {
            Log.d("GoogleSignIn", "Attempting to sign out")
            if (viewModel.isLoggedIn.value == true) {
                signInHelper.signOut {
                    viewModel.setLoggedIn(false)
                    viewModel.resetValues()
                    updateUI(null)
                    logInSharedPref?.edit()?.clear()?.apply()
                }
            } else {
                val signInIntent = signInHelper.getSignInIntent()
                startActivityForResult(signInIntent, GoogleSignInHelper.RC_SIGN_IN)
            }
        }

        viewModel.isLoggedIn.observe(viewLifecycleOwner, Observer { loggedIn ->
            if (loggedIn) {
                binding.loginLogoutButton.text = getString(R.string.sign_out)
            } else {
                binding.loginLogoutButton.text = getString(R.string.log_in)
            }
        })

        viewModel.userPhotoUrl.observe(viewLifecycleOwner, Observer { url ->

            Glide.with(this).load(url).into(binding.imageView)
        })

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GoogleSignInHelper.RC_SIGN_IN) {
            signInHelper.handleSignInResult(data,
                onSuccess = { account ->
                    // save data SharedPreference
                    saveLoginInfoToSharedPreference(account)

                    viewModel.setLoggedIn(true)
                    updateUI(account)
                },
                onFailure = { e ->
                    Log.w("ProfileFragment", "signInResult:failed code=" + e.message)
                    updateUI(null)
                }
            )
        }
    }

    private fun saveLoginInfoToSharedPreference(account: GoogleSignInAccount) {
        val logInSharedPref = activity?.getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
        logInSharedPref?.edit()?.apply {
            putString("displayName", account.displayName)
            putString("photoUrl", account.photoUrl.toString())
            putString("userId", account.id)
            putString("idToken", account.idToken)
            putString("email", account.email)
            putString("familyName", account.familyName)
            putString("givenName", account.givenName)
            apply()
        }
    }


    private fun updateUI(account: GoogleSignInAccount?) {
        if (account != null) {

            binding.nickname.text = account.displayName
            viewModel.getUserPhoto(account.photoUrl.toString())
        } else {

            binding.nickname.text = getString(R.string.hobby_explore_friends)


            val defaultUserImage = "https://firebasestorage.googleapis.com/v0/b/hobby-explore.appspot.com/o/images%2Fuser%20(2).png?alt=media&token=d7986b56-0d57-44cb-bd8e-1dfce4e45d19&_gl=1*10s2fv6*_ga*MjA2MTUwOTE5LjE2OTI1OTUxNzY.*_ga_CW55HF8NVT*MTY5NzUwNzkzMy4xNDguMS4xNjk3NTA5MDM8LjUyLjAuMA.."
            viewModel.getUserPhoto(defaultUserImage)

            binding.selectHobby.text = getString(R.string.unselected)
            binding.mbtiResult.text = getString(R.string.untested)

            viewModel.resetValues()
        }
    }

}
