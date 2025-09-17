package brian.project.hobbyexplore.googlelogin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import brian.project.hobbyexplore.databinding.FragmentGoogleLogInBinding
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class GoogleLogInFragment : Fragment() {

    private val signInHelper by lazy { GoogleSignInHelper(requireContext()) }
    private val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentGoogleLogInBinding.inflate(inflater, container, false)

        // Guest login (訪客登入)
        binding.guestLoginButton.setOnClickListener { v ->
            firebaseAuth.signInAnonymously()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = firebaseAuth.currentUser
                        if (user != null) {
                            val guestData = mapOf(
                                "displayName" to "趣探朋友",
                                "mbtiResult" to "未測驗",
                                "selectedHobby" to "未選擇"
                            )
                            firestore.collection("users").document(user.uid)
                                .set(guestData, SetOptions.merge())
                        }
                        // 跳轉到 PersonalityTest
                        v.findNavController().navigate(
                            GoogleLogInFragmentDirections
                                .actionGoogleLogInFragmentToPersonalityTestFragment()
                        )
                    } else {
                        Log.e("GoogleLogin", "Guest login failed", task.exception)
                    }
                }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val signInButton: SignInButton = view.findViewById(brian.project.hobbyexplore.R.id.sign_in_button)
        signInButton.setOnClickListener { signIn() }
    }

    private fun signIn() {
        val signInIntent = signInHelper.getSignInIntent()
        startActivityForResult(signInIntent, GoogleSignInHelper.RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GoogleSignInHelper.RC_SIGN_IN) {
            signInHelper.handleSignInResult(
                data,
                onSuccess = { account ->
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    firebaseAuth.signInWithCredential(credential)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val user = firebaseAuth.currentUser
                                if (user != null) {
                                    // 只更新基本資訊，不覆蓋 MBTI 與 hobby
                                    val userData = mapOf(
                                        "displayName" to (user.displayName ?: "趣探朋友"),
                                        "email" to (user.email ?: ""),
                                        "photoUrl" to (user.photoUrl?.toString() ?: "")
                                    )
                                    firestore.collection("users").document(user.uid)
                                        .set(userData, SetOptions.merge())
                                }
                                findNavController().navigate(
                                    GoogleLogInFragmentDirections
                                        .actionGoogleLogInFragmentToPersonalityTestFragment()
                                )
                            } else {
                                Log.e("GoogleLogin", "Firebase login failed", task.exception)
                            }
                        }
                },
                onFailure = { e ->
                    if (e is ApiException) {
                        Log.e("GoogleSignIn", "Google login failed statusCode=${e.statusCode}", e)
                    } else {
                        Log.e("GoogleSignIn", "Google login failed", e)
                    }
                }
            )
        }
    }
}
