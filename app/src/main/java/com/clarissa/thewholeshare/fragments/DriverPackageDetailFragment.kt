package com.clarissa.thewholeshare.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.clarissa.thewholeshare.DriverMainActivity
import com.clarissa.thewholeshare.R
import com.clarissa.thewholeshare.api.WholeShareApiService
import com.clarissa.thewholeshare.api.helpers.ParticipantsStatuses
import org.json.JSONObject


class DriverPackageDetailFragment(
    val previousFragment: Fragment
): Fragment() {
    // Components
    lateinit var idDetailLabel: TextView
    lateinit var addressDetailLabel: TextView
    lateinit var noteDetailLabel: TextView
    lateinit var pickupDetailLabel: TextView
    lateinit var userPackageDetailLabel: TextView
    lateinit var takePackageButton: Button
    lateinit var cancelPackageButton: Button
    lateinit var backDetailButton: Button

    // Variables
    var activeUserId: Int? = null
    var packageId: Int? = null
    var mode: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Variables
        activeUserId = this.requireArguments().getInt("active_user_id")
        packageId = this.requireArguments().getInt("package_id")
        mode = this.requireArguments().getInt("mode")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_driver_package_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Components
        idDetailLabel = view.findViewById(R.id.idDetailLabel)
        addressDetailLabel = view.findViewById(R.id.addressDetailLabel)
        noteDetailLabel = view.findViewById(R.id.noteDetailLabel)
        pickupDetailLabel = view.findViewById(R.id.pickupDetailLabel)
        userPackageDetailLabel = view.findViewById(R.id.userPackageDetailLabel)
        takePackageButton = view.findViewById(R.id.takePackageButton)
        cancelPackageButton = view.findViewById(R.id.cancelPackageButton)
        backDetailButton = view.findViewById(R.id.backDetailButton)

        // Load Events
        loadPackageDetails()

        // Register Events

        // Toggle the visibility and event of the button according to the mode of the fragment
        if (mode == ParticipantsStatuses.PENDING) {
            takePackageButton.visibility = View.VISIBLE
            cancelPackageButton.visibility = View.GONE
            takePackageButton.text = "Take"

            takePackageButton.setOnClickListener {
                takePackage(this.packageId!!)
            }
        }
        else if (mode == ParticipantsStatuses.DELIVERING) {
            takePackageButton.visibility = View.VISIBLE
            cancelPackageButton.visibility = View.VISIBLE
            takePackageButton.text = "Finish"
            cancelPackageButton.text = "Cancel"

            takePackageButton.setOnClickListener {
                navigateToFinish()
            }
            cancelPackageButton.setOnClickListener {
                cancelPackageDelivery(packageId!!)
            }
        }
        else if (mode == ParticipantsStatuses.DELIVERED) {
            takePackageButton.visibility = View.GONE
            cancelPackageButton.visibility = View.GONE
        }
        backDetailButton.setOnClickListener {
            (requireActivity() as DriverMainActivity).switchFragment(R.id.fragment_container_driver, previousFragment, Bundle())
        }
    }

    /**
     * Sent a request asking for the detail of the requested package. If the request fails server side, show an alert on why it failed to the UI.
     */
    private fun loadPackageDetails() {
        // Create the request object
        val detailRequest = JsonObjectRequest(Request.Method.GET, "${WholeShareApiService.WS_HOST}/requests/getPacketDetail?package_id=${packageId}", null,
            { response ->
                // Check if the response is a successful response
                if (response.getInt("status") == 0) {
                    alertDialogFailed("Request Failed!", response.getString("reason"))
                }
                else if (response.getInt("status") == 1) {
                    // Set the data to the UI labels
                    val detail = response.getJSONObject("detail")
                    idDetailLabel.text = "#${detail.getString("id")}"
                    addressDetailLabel.text = detail.getString("location")
                    noteDetailLabel.text = "" // detail.getString("location")
                    pickupDetailLabel.text = detail.getString("pickup")
                    userPackageDetailLabel.text = detail.getString("full_name")
                }
                else alertDialogFailed("Unknown Error!", "Unrecognized Response status code!")
            },
            { error ->
                this.alertDialogFailed("Request Error", error.toString())
            }
        )
        // Set the request retry policy before throwing timeout
        detailRequest.retryPolicy = DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

        // Add the request to the queue
        WholeShareApiService.getInstance(requireContext()).addToRequestQueue(detailRequest)
    }

    /**
     * Sent a request to take this package and deliver it to the destination. if the request is succesful, return the fragment to the previous fragment.
     *
     * @param packageId The package id that is going to be taken by the current authenticated courier.
     */
    private fun takePackage(packageId: Int) {
        Log.d("TAKE_PACKAGE", "Taking Package")

        // Create the request body parameters
        val requestBody = JSONObject()
        requestBody.put("user_id", activeUserId)
        requestBody.put("participant_id", packageId)

        // Create the request object
        val takeRequest = JsonObjectRequest(Request.Method.PUT, "${WholeShareApiService.WS_HOST}/requests/takePackage", requestBody,
            { response ->
                if (response.getInt("status") == 0) {
                    alertDialogFailed("Request Failed", response.getString("reason"))
                }
                else if (response.getInt("status") == 1) {
                    val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()

                    // If the response is a successful response, go back to the previous transaction
                    fragmentTransaction.replace(R.id.fragment_container_driver, previousFragment)
                    fragmentTransaction.commit()
                }
                else alertDialogFailed("Unknown Error!", "Unknown Response status code!")
            },
            { error ->
                alertDialogFailed("Request Error!", error.toString())
            }
        )
        // Set the request timeout policy
        takeRequest.retryPolicy = DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

        WholeShareApiService.getInstance(requireContext()).addToRequestQueue(takeRequest)
    }

    /**
     * Navigate to the finish fragment to confirm and submit a photo of the delivered item.
     */
    private fun navigateToFinish() {
        val fragmentBundle = Bundle()
        fragmentBundle.putInt("package_id", packageId!!)
        fragmentBundle.putInt("active_user_id", activeUserId!!)

        val finishFragment = DriverFinishDetailFragment(this)

        (requireActivity() as DriverMainActivity).switchFragment(R.id.fragment_container_driver, finishFragment, fragmentBundle)

    }

    /**
     * Cancel the package from delivery from the current authenticated Courier, and set the status of the package back to pending.
     */
    private fun cancelPackageDelivery(packageId: Int) {
        // Create the request body
        val requestBody = JSONObject()
        requestBody.put("user_id", activeUserId)
        requestBody.put("package_id", packageId)

        // Create the request objetc
        val cancelRequest = JsonObjectRequest(Request.Method.PUT, "${WholeShareApiService.WS_HOST}/requests/cancelPackageDelivery", requestBody,
            { response ->
                if (response.getInt("status") == 0) {
                     alertDialogFailed("Request Failed!", response.getString("reason"))
                }
                else if (response.getInt("status") == 1) {
                    // Notify the user if the operation is succesful
                    alertDialogSuccess("Successfully Cancelled", response.getString("message"))

                    // Change from the detailed fragment to the previous fragment.
                    (requireActivity() as DriverMainActivity).switchFragment(R.id.fragment_container_driver, previousFragment, Bundle())
                }
            },
            { error ->
                alertDialogFailed("Request Error", "${error}")
            }
        )
        // Set the request object retry policy before throwing the timeout event
        cancelRequest.retryPolicy = DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

        // Add the request to the service queue
        WholeShareApiService.getInstance(requireContext()).addToRequestQueue(cancelRequest)
    }

    /**
     * Show an failed alert dialog to the UI containing a title and a message provided by the paraneters.
     *
     * @param title A string representing the title of the alert.
     * @param message A string containing the detailed alert message about the situation.
     */
    private fun alertDialogFailed(title: String, message: String){
        val mAlertDialog = AlertDialog.Builder(requireContext())
        mAlertDialog.setIcon(R.drawable.high_priority_80px) //set alertdialog icon
        mAlertDialog.setTitle(title) //set alertdialog title
        mAlertDialog.setMessage(message) //set alertdialog message
        mAlertDialog.setPositiveButton("OK") { dialog, id ->

        }
        mAlertDialog.show()
    }

    /**
     * Show a success alert dialog to the UI containing a title and a message provided by the parameters.
     *
     * @param title A string representing the title of the alert.
     * @param message A string containing the detailed alert message.
     */
    fun alertDialogSuccess(title:String, message:String){
        val mAlertDialog = AlertDialog.Builder(requireContext())
        mAlertDialog.setIcon(R.drawable.ok_80px) //set alertdialog icon
        mAlertDialog.setTitle(title) //set alertdialog title
        mAlertDialog.setMessage(message) //set alertdialog message
        mAlertDialog.setPositiveButton("OK") { dialog, id ->

        }
        mAlertDialog.show()
    }
}