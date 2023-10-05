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
import androidx.navigation.fragment.findNavController
import com.example.hobbyexplore.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task

class GoogleLogInFragment : Fragment() {

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 9001

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_google_log_in, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        val signInButton: SignInButton = view.findViewById(R.id.sign_in_button)
        signInButton.setOnClickListener {
            signIn()
        }
    }

    private fun signIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)

            saveUserDataToPreferences(account)
            Log.i("asfdfdsfdew", "sss${account.displayName},${account.photoUrl}")


            findNavController().navigate(GoogleLogInFragmentDirections.actionGoogleLogInFragmentToPersonalityTestFragment())
        } catch (e: ApiException) {
            // Handle sign-in failure.
        }
    }

    private fun getPreferences(): SharedPreferences {
        return requireActivity().getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
    }

    private fun saveUserDataToPreferences(account: GoogleSignInAccount?) {
        val preferencesEditor = getPreferences().edit()

        preferencesEditor.putString("userId", account?.id)
        preferencesEditor.putString("idToken", account?.idToken)
        preferencesEditor.putString("email", account?.email)
        preferencesEditor.putString("displayName", account?.displayName)
        preferencesEditor.putString("familyName", account?.familyName)
        preferencesEditor.putString("givenName", account?.givenName)
        preferencesEditor.putString("photoUrl", account?.photoUrl?.toString())
        Log.i("aasdgfdsds", "${account?.id}, ${account?.idToken}, ${account?.email}, ${account?.displayName}, ${account?.familyName}, ${account?.givenName}, ${account?.photoUrl}")
        preferencesEditor.apply()
    }
}