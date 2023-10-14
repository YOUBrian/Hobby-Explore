package com.example.hobbyexplore.profile

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
import com.example.hobbyexplore.R
import com.example.hobbyexplore.databinding.FragmentProfileBinding
import com.example.hobbyexplore.googlelogin.GoogleSignInHelper
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

        val mbtiResult = sharedPref?.getString("MBTI_Result", "N/A")
        val selectedHobbyTitle = sharedPref?.getString("Selected_Hobby_Title", "N/A")

        val logInSharedPref = activity?.getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
        val userImage = logInSharedPref?.getString("photoUrl", "")
        val userName = logInSharedPref?.getString("displayName", "N/A")

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
                binding.loginLogoutButton.text = "登出"
            } else {
                binding.loginLogoutButton.text = "登入"
            }
        })

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GoogleSignInHelper.RC_SIGN_IN) {
            signInHelper.handleSignInResult(data,
                onSuccess = { account ->
                    // 当登入成功后，存储账号信息到SharedPreference中
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
            binding.nickname.text = "預設名稱"
            viewModel.resetValues()
        }
    }
}
