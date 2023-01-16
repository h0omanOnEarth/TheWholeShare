package com.clarissa.thewholeshare.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.android.volley.DefaultRetryPolicy
import com.clarissa.thewholeshare.DriverMainActivity
import com.clarissa.thewholeshare.R
import com.clarissa.thewholeshare.api.WholeShareApiService
import com.clarissa.thewholeshare.api.custom.MultiPartRequest
import kotlinx.coroutines.*
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException


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
        val path = getPath(imageUri!!)
        if (path != null) {
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, imageUri)
                uploadBitMap(bitmap)
            }
            catch (e: IOException) {
                e.printStackTrace()
            }
        }
        else {
            Toast.makeText(requireContext(), "No image is found!", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Get the file absolute path from the Uri given in the parameter.
     *
     * @param uri The uri which is to be parsed as a file path.
     * @return The string containing the path of the file.
     */
    @SuppressLint("Range")
    private fun getPath(uri: Uri): String {
        // Get the document Id from the uri
        Log.d("GET_PATH", "uri: $uri")
        var cursor = requireActivity().contentResolver?.query(uri, arrayOf(MediaStore.Images.Media.DATA), null, null, null)!!
        cursor.moveToFirst()
        Log.d("GET_PATH", "getString: ${cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))}")
        val path: String = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close()

        return path
    }

    /**
     * Upload the picture that has been fetched by the Application after taking a photo to the server and update the status of the package.
     */
    private fun uploadBitMap(bitmap: Bitmap) {
        val uploadRequest = object : MultiPartRequest(Method.POST, "${WholeShareApiService.WS_HOST}/requests/uploadPhoto",
            { response ->
                val jsonObject = JSONObject(String(response.data))

                // Check if the operation has failed.
                if (jsonObject.getInt("status") == 0) {
                    Toast.makeText(requireContext(), jsonObject.getString("reason"), Toast.LENGTH_LONG).show()
                }
                else {
                    // Change the fragment to the previous fragment
                    (requireActivity() as DriverMainActivity).switchFragment(R.id.fragment_container_driver, DriverListDeliverFragment(), Bundle())
                }
            },
            { error ->
                error.printStackTrace()
            }
        ) {
            override fun getByteData(): Map<String, DataPart> {
                val params = HashMap<String, DataPart>()
                val imageName = "report_$packageId"
                params.put("image", DataPart("$imageName.png", getFileDataFromDrawable(bitmap)!!))
                return params
            }

            override fun getParams(): MutableMap<String, String>? {
                val params = HashMap<String, String>()
                params.put("user_id", activeUserId.toString())
                params.put("package_id", packageId.toString())
                params.put("image_name", "courier_delivered_$packageId.png")
                return params
            }
        }
        uploadRequest.retryPolicy = DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

        WholeShareApiService.getInstance(requireContext()).addToRequestQueue(uploadRequest)
    }

    fun getFileDataFromDrawable(bitmap: Bitmap): ByteArray? {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }

    private fun updateImageView(imageUri: Uri?) {
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
