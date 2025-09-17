package brian.project.hobbyexplore.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import brian.project.hobbyexplore.R
import brian.project.hobbyexplore.databinding.FragmentProfileBinding
import brian.project.hobbyexplore.googlelogin.GoogleSignInHelper
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class ProfileFragment : Fragment() {

    private val signInHelper by lazy { GoogleSignInHelper(requireContext()) }
    private val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private lateinit var viewModel: ProfileViewModel
    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        binding.photo = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val currentUser = firebaseAuth.currentUser
        viewModel.setLoggedIn(currentUser != null && !currentUser.isAnonymous)

        if (currentUser != null) {
            loadUserProfile(currentUser.uid, currentUser.isAnonymous)
        }

        // Navigate to MBTI test
        binding.mbtiButton.setOnClickListener {
            it.findNavController()
                .navigate(ProfileFragmentDirections.actionProfileFragmentToWhetherTakeMbtiTest())
        }

        // Login / Logout
        binding.loginLogoutButton.setOnClickListener {
            val user = firebaseAuth.currentUser
            if (user != null && !user.isAnonymous) {
                // Case: Google user → Sign out and switch to anonymous guest
                firebaseAuth.signOut()
                signInHelper.signOut {
                    firebaseAuth.signInAnonymously()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val guestUser = firebaseAuth.currentUser
                                guestUser?.let {
                                    val guestData = mapOf(
                                        "displayName" to "趣探朋友",
                                        "mbtiResult" to "未測驗",
                                        "selectedHobby" to "未選擇"
                                    )
                                    firestore.collection("users").document(it.uid)
                                        .set(guestData, SetOptions.merge())
                                }
                                viewModel.setLoggedIn(false)
                                updateUI(null, true)
                            } else {
                                Log.e("ProfileFragment", "Anonymous sign-in failed", task.exception)
                            }
                        }
                }
            } else {
                // Case: Guest → Try Google login
                val signInIntent = signInHelper.getSignInIntent()
                startActivityForResult(signInIntent, GoogleSignInHelper.RC_SIGN_IN)
            }
        }

        // Observe login state
        viewModel.isLoggedIn.observe(viewLifecycleOwner, Observer { loggedIn ->
            binding.loginLogoutButton.text =
                if (loggedIn) getString(R.string.sign_out) else getString(R.string.log_in)
        })

        // Observe user photo
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
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    firebaseAuth.signInWithCredential(credential)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val user = firebaseAuth.currentUser
                                if (user != null) {
                                    saveUserProfile(user.uid, account)
                                    viewModel.setLoggedIn(true)
                                    updateUI(account, false)
                                    loadUserProfile(user.uid, false)
                                }
                            } else {
                                Log.e("ProfileFragment", "Firebase sign in failed", task.exception)
                                updateUI(null, true)
                            }
                        }
                },
                onFailure = { e ->
                    Log.w("ProfileFragment", "Google sign in failed ${e.message}")
                    updateUI(null, true)
                }
            )
        }
    }

    // Save only basic info to Firestore
    private fun saveUserProfile(uid: String, account: GoogleSignInAccount) {
        val userData = mapOf(
            "displayName" to (account.displayName ?: "趣探朋友"),
            "email" to (account.email ?: ""),
            "photoUrl" to (account.photoUrl?.toString() ?: "")
        )
        firestore.collection("users").document(uid)
            .set(userData, SetOptions.merge()) // Merge to avoid overwriting mbtiResult or hobby
    }

    // Load profile info (support guest and google user)
    private fun loadUserProfile(uid: String, isAnonymous: Boolean) {
        firestore.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    binding.nickname.text =
                        if (isAnonymous) "趣探朋友"
                        else document.getString("displayName") ?: getString(R.string.hobby_explore_friends)

                    binding.mbtiResult.text =
                        document.getString("mbtiResult") ?: "未測驗"

                    binding.selectHobby.text =
                        document.getString("selectedHobby") ?: "未選擇"

                    val photoUrl = document.getString("photoUrl")
                        ?: "https://firebasestorage.googleapis.com/v0/b/hobby-explore.appspot.com/o/images%2Fuser%20(2).png?alt=media&token=d7986b56-0d57-44cb-bd8e-1dfce4e45d19"
                    viewModel.getUserPhoto(photoUrl)
                } else {
                    Log.d("ProfileFragment", "No profile found for uid=$uid")
                }
            }
            .addOnFailureListener { e ->
                Log.e("ProfileFragment", "Failed to load user profile", e)
            }
    }

    // Update UI (guest or google user)
    private fun updateUI(account: GoogleSignInAccount?, isGuest: Boolean) {
        if (account != null && !isGuest) {
            binding.nickname.text = account.displayName
            viewModel.getUserPhoto(account.photoUrl?.toString() ?: "")
        } else {
            binding.nickname.text = "趣探朋友"
            val defaultUserImage =
                "https://firebasestorage.googleapis.com/v0/b/hobby-explore.appspot.com/o/images%2Fuser%20(2).png?alt=media&token=d7986b56-0d57-44cb-bd8e-1dfce4e45d19"
            viewModel.getUserPhoto(defaultUserImage)
            binding.selectHobby.text = "未選擇"
            binding.mbtiResult.text = "未測驗"
        }
    }
}
