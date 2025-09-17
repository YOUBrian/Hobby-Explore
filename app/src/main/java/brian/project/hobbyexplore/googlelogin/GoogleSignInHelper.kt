package brian.project.hobbyexplore.googlelogin

import android.content.Context
import android.content.Intent
import android.util.Log
import brian.project.hobbyexplore.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

class GoogleSignInHelper(private val context: Context) {

    companion object {
        const val RC_SIGN_IN = 9001
    }

    // Use requestIdToken to get the token for FirebaseAuth
    private val googleSignInOptions: GoogleSignInOptions =
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id)) // must match the OAuth client in Firebase console
            .requestEmail()
            .build()

    private val googleSignInClient: GoogleSignInClient =
        GoogleSignIn.getClient(context, googleSignInOptions)

    fun getSignInIntent(): Intent = googleSignInClient.signInIntent

    fun handleSignInResult(
        data: Intent?,
        onSuccess: (GoogleSignInAccount) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)
            if (account != null) {
                Log.d("GoogleSignInHelper", "Sign in success account=${account.email}")
                onSuccess(account)
            } else {
                onFailure(Exception("GoogleSignInAccount is null"))
            }
        } catch (e: ApiException) {
            Log.e("GoogleSignInHelper", "Sign in failed ${e.statusCode}", e)
            onFailure(e)
        }
    }

    fun signOut(onCompletion: (() -> Unit)? = null) {
        googleSignInClient.signOut().addOnCompleteListener {
            onCompletion?.invoke()
            Log.d("GoogleSignIn", "Signed out successfully")
        }
    }

    fun getLastSignedInAccount(): GoogleSignInAccount? =
        GoogleSignIn.getLastSignedInAccount(context)

    fun getCurrentUser(): GoogleSignInAccount? =
        GoogleSignIn.getLastSignedInAccount(context)
}
