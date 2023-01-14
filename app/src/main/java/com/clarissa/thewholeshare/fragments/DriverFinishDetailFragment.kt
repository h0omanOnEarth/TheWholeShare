package com.clarissa.thewholeshare.fragments

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import com.clarissa.thewholeshare.DriverMainActivity
import com.clarissa.thewholeshare.R
import kotlinx.coroutines.*


class DriverFinishDetailFragment(
    var previousFragment: Fragment
) : Fragment() {
    // Components
    lateinit var finishDeliverMessageLabel: TextView
    lateinit var deliverPhotoImageView: ImageView
    lateinit var submitDeliveryPhotoButton: Button
    lateinit var finishPackageButton: Button
    lateinit var backFinishButton: Button

    // Variables
    var activeUserId: Int? = null
    var packageId: Int? = null

    // Coroutine
    val coroutine = CoroutineScope(Dispatchers.Default)

    // Camera Variables
    val CAMERA_REQUEST_CODE = 1888
    var imageUri: Uri? = null
    var cameraResultLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            updateImageView(imageUri)
            finishPackageButton.isEnabled = true
        }
        else {
            Toast.makeText(requireContext(), "Error while saving image in gallery.", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Variables
        activeUserId = this.requireArguments().getInt("active_user_id")
        packageId = this.requireArguments().getInt("package_id")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_driver_finish_detail, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Components
        finishDeliverMessageLabel = view.findViewById(R.id.finishDeliverMessageLabel)
        deliverPhotoImageView = view.findViewById(R.id.deliverPhotoImageView)
        submitDeliveryPhotoButton = view.findViewById(R.id.submitDeliveryPhotoButton)
        finishPackageButton = view.findViewById(R.id.finishPackageButton)
        backFinishButton = view.findViewById(R.id.backFinishButton)

        finishDeliverMessageLabel.text = "Finish deliver #${packageId} Package?"
        finishPackageButton.isEnabled = false

        // Register Events
        backFinishButton.setOnClickListener {
            (requireActivity() as DriverMainActivity).switchFragment(R.id.fragment_container_driver, previousFragment, Bundle())
        }
        finishPackageButton.setOnClickListener {
            finishDelivery()
        }
        submitDeliveryPhotoButton.setOnClickListener {
            // Check for the required permission to use the camera
            if (requireActivity().checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                openCameraInterface()
            }
            else {
                // Ask for the required permission before using the camera.
                requireActivity().requestPermissions(arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
            }
        }
    }

    /**
     * Sent the request to the server by uploading a picture of the report and finishing the delivery.
     */
    private fun finishDelivery() {
        // TODO: Sent a request to the server and switch back to the original fragment
    }

    public fun updateImageView(imageUri: Uri?) {
        deliverPhotoImageView.setImageURI(imageUri)
    }

    private fun openCameraInterface() {
        val content = ContentValues()
        content.put(MediaStore.Images.Media.TITLE, "WholeSharePhoto")
        content.put(MediaStore.Images.Media.DESCRIPTION, "WholeShare Image Photo For Report")

        imageUri = requireActivity().contentResolver?.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, content)

        cameraResultLauncher.launch(imageUri)
    }
}
