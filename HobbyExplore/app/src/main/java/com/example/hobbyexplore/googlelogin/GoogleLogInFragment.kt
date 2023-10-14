package com.example.hobbyexplore.googlelogin

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.hobbyexplore.R
import com.example.hobbyexplore.databinding.FragmentGoogleLogInBinding
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.SignInButton

class GoogleLogInFragment : Fragment() {

    private val signInHelper by lazy { GoogleSignInHelper(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentGoogleLogInBinding.inflate(inflater)
        binding.guestLoginButton.setOnClickListener{
            it.findNavController().navigate(GoogleLogInFragmentDirections.actionGoogleLogInFragmentToPersonalityTestFragment())
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val signInButton: SignInButton = view.findViewById(R.id.sign_in_button)
        signInButton.setOnClickListener {
            signIn()
        }
    }

    private fun signIn() {
        val signInIntent = signInHelper.getSignInIntent()
        startActivityForResult(signInIntent, GoogleSignInHelper.RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GoogleSignInHelper.RC_SIGN_IN) {
            signInHelper.handleSignInResult(data,
                onSuccess = { account ->
                    saveUserDataToPreferences(account)
                    Log.i("asfdfdsfdew", "sss${account.displayName},${account.photoUrl}")
                    findNavController().navigate(GoogleLogInFragmentDirections.actionGoogleLogInFragmentToPersonalityTestFragment())
                },
                onFailure = { e ->
                    // Handle sign-in failure.
                }
            )
        }
    }

    private fun getPreferences(): SharedPreferences {
        return requireActivity().getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
    }

    private fun saveUserDataToPreferences(account: GoogleSignInAccount) {
        val preferencesEditor = getPreferences().edit()

        preferencesEditor.putString("userId", account.id)
        preferencesEditor.putString("idToken", account.idToken)
        preferencesEditor.putString("email", account.email)
        preferencesEditor.putString("displayName", account.displayName)
        preferencesEditor.putString("familyName", account.familyName)
        preferencesEditor.putString("givenName", account.givenName)
        preferencesEditor.putString("photoUrl", account.photoUrl?.toString())
        Log.i("aasdgfdsds", "${account.id}, ${account.idToken}, ${account.email}, ${account.displayName}, ${account.familyName}, ${account.givenName}, ${account.photoUrl}")
        preferencesEditor.apply()
    }
}
