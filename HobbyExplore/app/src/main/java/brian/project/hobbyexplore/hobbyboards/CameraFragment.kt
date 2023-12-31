package brian.project.hobbyexplore.hobbyboards

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.VideoCapture
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import brian.project.hobbyexplore.R
import brian.project.hobbyexplore.databinding.FragmentCameraBinding
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.UUID
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraFragment : Fragment() {
    private lateinit var viewBinding: FragmentCameraBinding
    private lateinit var cameraExecutor: ExecutorService
    private var imageCapture: ImageCapture? = null
    private var videoCapture: VideoCapture? = null


    private fun uploadImageToFirebase(selectedPhotoUri: Uri) {
        val storageReference = FirebaseStorage.getInstance().reference
        val imageRef = storageReference.child("images/${UUID.randomUUID()}.jpg")

        val uploadTask = imageRef.putFile(selectedPhotoUri)
        Log.i("getPhotoURI", "uploadTask: ${imageRef.putFile(selectedPhotoUri)}")
        uploadTask.addOnSuccessListener {

            // Hide the Lottie animation before navigating to postFragment
            viewBinding.selectImageLoading.visibility = View.GONE
            viewBinding.selectImageLoading.pauseAnimation()

            imageRef.downloadUrl.addOnSuccessListener { uri ->
                val downloadUrl = uri.toString()
                Log.i("getPhotoURI", "selectedPhotoUri: $selectedPhotoUri")
                Log.i("getPhotoURI", "downloadUrl: $downloadUrl")
                findNavController().navigate(
                    CameraFragmentDirections.actionCameraFragmentToPostFragment(
                        "",
                        downloadUrl
                    )
                )
            }
        }.addOnFailureListener {

            // Hide the Lottie animation if the upload fails
            viewBinding.selectImageLoading.visibility = View.GONE
            viewBinding.selectImageLoading.pauseAnimation()

            Log.e("uploadImageToFirebase", "Upload failed", it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentCameraBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        // Set up the listeners for take photo and video capture buttons
        viewBinding.shutterButton.setOnClickListener { takePhoto() }
        viewBinding.selectPhotoButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this.requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                val intent = Intent()
                intent.setType("image/*")
                intent.action = Intent.ACTION_PICK
                startActivityForResult(intent, 10)
            } else {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    1
                )
            }
        }

        cameraExecutor = Executors.newSingleThreadExecutor()


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 10 && resultCode == Activity.RESULT_OK) {

            val selectedPhotoUri = data?.data

            // Show the Lottie animation now that an image has been selected
            viewBinding.selectImageLoading.visibility = View.VISIBLE
            viewBinding.selectImageLoading.playAnimation()

            if (selectedPhotoUri != null) {

                uploadImageToFirebase(selectedPhotoUri)
            }
        }
    }

//    private fun navigateToPostFragmentWithUri(uri: Uri) {
//        val action = CameraFragmentDirections
//            .actionCameraFragmentToPostFragment(selectedPhotoUri = uri.toString())
//        findNavController().navigate(action)
//    }

    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create time stamped name and MediaStore entry.
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
            }
        }

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(
                requireContext().contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
            .build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val msg = getString(R.string.photo_saved)
                    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, msg)
                }
            }
        )
    }


    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewBinding.viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .build()

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }


    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireContext(),
            it
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "CameraXApp"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        val REQUIRED_PERMISSIONS =
            mutableListOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                showPermissionDeniedMessage()
            }
        }
    }

    private fun showPermissionDeniedMessage() {
        Toast.makeText(requireContext(), "Permissions not granted by the user.", Toast.LENGTH_SHORT)
            .show()
    }


}