package com.example.hobbyexplore.googlelogin

import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

class GoogleSignInHelper(private val context: Context) {

    companion object {
        const val RC_SIGN_IN = 9001
    }

    private val googleSignInOptions: GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .build()

    private val googleSignInClient: GoogleSignInClient = GoogleSignIn.getClient(context, googleSignInOptions)

    fun getSignInIntent(): Intent = googleSignInClient.signInIntent

    fun handleSignInResult(data: Intent?, onSuccess: (GoogleSignInAccount) -> Unit, onFailure: (Exception) -> Unit) {
        try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)
            onSuccess(account!!)
        } catch (e: ApiException) {
            onFailure(e)
        }
    }

    fun signOut(onCompletion: (() -> Unit)?) {
        googleSignInClient.signOut().addOnCompleteListener {
            onCompletion?.invoke()
            Log.d("GoogleSignIn", "Signed out successfully")
        }
    }

    fun getLastSignedInAccount(): GoogleSignInAccount? = GoogleSignIn.getLastSignedInAccount(context)

    fun getCurrentUser(): GoogleSignInAccount? {
        return GoogleSignIn.getLastSignedInAccount(context)
    }

}
