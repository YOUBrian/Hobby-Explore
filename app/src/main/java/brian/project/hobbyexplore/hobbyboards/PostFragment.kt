package brian.project.hobbyexplore.hobbyboards

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import brian.project.hobbyexplore.databinding.FragmentPostBinding

class PostFragment : Fragment() {

    private var _binding: FragmentPostBinding? = null
    val binding get() = _binding!!

    private val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private var currentUserName: String = "趣探朋友" // default name for guest

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ).toTypedArray()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPostBinding.inflate(inflater, container, false)
        val viewModel: PostViewModel = ViewModelProvider(this).get(PostViewModel::class.java)
        binding.user = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val args = PostFragmentArgs.fromBundle(requireArguments())
        val imageUri = args.imageUri
        val imageStringToUri = Uri.parse(imageUri)

        // Load correct user name from Firestore
        loadCurrentUserName { name ->
            currentUserName = name
            viewModel.updateUserName(name)
        }

        val postSharedPref = activity?.getSharedPreferences("postMessageData", Context.MODE_PRIVATE)

        /*------------Write post data when callback---------------*/
        binding.ratingBar.rating = postSharedPref?.getString("postRating", "5")?.toFloat()!!
        binding.userContentInput.setText(postSharedPref.getString("postContent", ""))
        val category = postSharedPref.getString("postCategory", "")
        val position = (binding.categoryMenu.adapter as ArrayAdapter<String>).getPosition(category)
        binding.categoryMenu.setSelection(position)
        /*------------Write post data when callback---------------*/

        viewModel.uploadPhoto.value = imageUri
        viewModel.uploadPhoto.observe(viewLifecycleOwner, Observer { newImageUri ->
            Glide.with(this).load(newImageUri).into(binding.postImage)
        })

        viewModel.userContent.observe(viewLifecycleOwner, Observer {
            binding.userContentInput.setText(it)
        })

        viewModel.userRating.observe(viewLifecycleOwner, Observer {
            binding.ratingBar.rating = it
        })

        binding.cameraButton.setOnClickListener {
            viewModel.userContent.value = binding.userContentInput.text.toString()
            viewModel.userRating.value = binding.ratingBar.rating
            savePostMessageToPreferences()
            it.findNavController()
                .navigate(PostFragmentDirections.actionPostFragmentToCameraFragment())
        }

        binding.publishButton.setOnClickListener {
            val content = binding.userContentInput.text.toString()
            val rating = binding.ratingBar.rating
            val category = binding.categoryMenu.selectedItem.toString()

            it.findNavController()
                .navigate(PostFragmentDirections.actionPostFragmentToHobbyBoardsFragment())

            // Use currentUserName instead of SharedPreferences
            viewModel.postMessageData(content, rating, imageUri, category, currentUserName)
            Log.i("PostFragment", "Post by $currentUserName, image=$imageStringToUri, rating=$rating")

            clearPostMessagePreferences()
        }

        val postContent = postSharedPref.getString("postContent", "")
        val contentFromArgs = args.content
        val imageUrlFromArgs = args.imageUri

        if (!contentFromArgs.isNullOrEmpty()) {
            binding.userContentInput.setText(contentFromArgs)
        } else if (!postContent.isNullOrEmpty()) {
            binding.userContentInput.setText(postContent)
        }

        Glide.with(this).load(imageUrlFromArgs).into(binding.postImage)

        return binding.root
    }

    // Fetch the current user nickname from Firestore
    private fun loadCurrentUserName(callback: (String) -> Unit) {
        val user = firebaseAuth.currentUser
        if (user != null) {
            firestore.collection("users").document(user.uid).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val name = document.getString("displayName") ?: "趣探朋友"
                        callback(name)
                    } else {
                        callback("趣探朋友")
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("PostFragment", "Failed to load nickname", e)
                    callback("趣探朋友")
                }
        } else {
            callback("趣探朋友")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                showPermissionDeniedMessage()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
    }

    private fun allPermissionsGranted() = CameraFragment.REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireContext(),
            it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun showPermissionDeniedMessage() {
        Toast.makeText(requireContext(), "Permissions not granted by the user.", Toast.LENGTH_SHORT)
            .show()
    }

    private fun getPreferences(): SharedPreferences {
        return requireActivity().getSharedPreferences("postMessageData", Context.MODE_PRIVATE)
    }

    private fun savePostMessageToPreferences() {
        val preferencesEditor = getPreferences().edit()
        preferencesEditor.putString("postRating", binding.ratingBar.rating.toString())
        preferencesEditor.putString("postContent", binding.userContentInput.text.toString())
        preferencesEditor.putString("postCategory", binding.categoryMenu.selectedItem.toString())
        preferencesEditor.apply()
    }

    private fun clearPostMessagePreferences() {
        val preferencesEditor = getPreferences().edit()
        preferencesEditor.clear()
        preferencesEditor.apply()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
