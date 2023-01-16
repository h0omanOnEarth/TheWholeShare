package com.clarissa.thewholeshare.adapters

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.clarissa.thewholeshare.DriverMainActivity
import com.clarissa.thewholeshare.R
import com.clarissa.thewholeshare.api.WholeShareApiService
import com.clarissa.thewholeshare.api.helpers.ParticipantsStatuses
import com.clarissa.thewholeshare.api.responses.CourierPackage
import com.clarissa.thewholeshare.fragments.DriverFinishDetailFragment
import com.clarissa.thewholeshare.fragments.DriverPackageDetailFragment
import org.json.JSONObject

class CourierPackageAdapter(
    val activity: Activity,
    val dataset: ArrayList<CourierPackage>,
    var mode: Int,
    val activeUserId: Int,
    val parentFragment: Fragment
) : RecyclerView.Adapter<CourierPackageAdapter.PackageViewHolder>() {
    class PackageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val packageIdLabel = view.findViewById<TextView>(R.id.packageIdLabel)
        val packageAdressLabel = view.findViewById<TextView>(R.id.packageAddressLabel)
        val packageOwnerLabel = view.findViewById<TextView>(R.id.packageOwnerLabel)
        val packageActionButton = view.findViewById<Button>(R.id.packageActionButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PackageViewHolder {
        val view = LayoutInflater.from(activity).inflate(R.layout.item_package_driver, parent, false)
        
        return PackageViewHolder(view)
    }

    override fun onBindViewHolder(holder: PackageViewHolder, position: Int) {
        holder.packageIdLabel.text = "#${dataset[position].id}"
        holder.packageAdressLabel.text = dataset[position].pickup
        holder.packageOwnerLabel.text = dataset[position].full_name
        holder.itemView.tag = dataset[position].id

        // Set what the action button in every card do depending on the mode being passed to the adapter
        if (mode == ParticipantsStatuses.PENDING) {
            holder.packageActionButton.text = "Take"
            holder.packageActionButton.visibility = TextView.VISIBLE

            // Set the action button event to take the request
            holder.packageActionButton.setOnClickListener { takePackage(holder.itemView.tag as Int) }
        }
        else if (mode == ParticipantsStatuses.DELIVERING) {
            holder.packageActionButton.text = "Finish"
            holder.packageActionButton.visibility = TextView.VISIBLE

            // Set the action button event to finish delivering the request
//            holder.packageActionButton.setOnClickListener { updatePackageStatus(holder.itemView.tag as Int, ParticipantsStatuses.DELIVERED) }
            holder.packageActionButton.setOnClickListener { navigateToFinish(holder.itemView.tag as Int) }
        }
        else if (mode == ParticipantsStatuses.DELIVERED) {
            // An unused button to undo the state of the package. (Add the event and add the visibility to use it)
            holder.packageActionButton.text = "Undo"
            holder.packageActionButton.visibility = TextView.GONE

            // Set the action button event to undo the delivery
//            holder.packageActionButton.setOnClickListener { updatePackageStatus(holder.itemView.tag as Int, ParticipantsStatuses.DELIVERING) }
        }
        else holder.packageActionButton.visibility = TextView.GONE

        // Set the card event to go to the details panel
        holder.itemView.setOnClickListener { navigateToDetails(holder.itemView.tag as Int) }
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    /**
     * Navigate to the finish fragment to confirm and submit a photo of the delivered item.
     */
    private fun navigateToFinish(packageId: Int) {
        val fragmentBundle = Bundle()
        fragmentBundle.putInt("package_id", packageId)
        fragmentBundle.putInt("active_user_id", activeUserId)

        val finishFragment = DriverFinishDetailFragment(parentFragment)

        (activity as DriverMainActivity).switchFragment(R.id.fragment_container_driver, finishFragment, fragmentBundle)
    }

    /**
     * Sent a request to take this package and deliver it to the destination.
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
                    val targetPackage = dataset.find { it.id == packageId }
                    Log.d("TAKE_PACKAGE", "targetPackage: ${targetPackage}")

                    if (targetPackage != null) {
                        val packageIndex = dataset.indexOf(targetPackage)

                        try {
                            val removeStatus = dataset.remove(targetPackage)
                            if (removeStatus) {
                                this.notifyItemRemoved(packageIndex)
                            }
                        }
                        catch (e: Exception) {
                            Log.d("FINISH_REQUEST", "error: ${e.message}")
                        }
                    }
                }
                else if (response.getInt("status") == 2) {
                    alertDialogFailed("Request Failed", response.getString("reason"))
                    this.notifyDataSetChanged()
                }
            },
            { error ->
                alertDialogFailed("Request Error!", error.toString())
            }
        )
        // Set the request timeout policy
        takeRequest.retryPolicy = DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

        WholeShareApiService.getInstance(activity).addToRequestQueue(takeRequest)
    }

    /**
     * Sent a request to the server to update the status of the package to the targeted status.
     *
     * @param packageId The id of the participant package that is going to be updated.
     * @param newStatus An integer which represents the new status the package is going to have.
     */
    private fun updatePackageStatus(packageId: Int, newStatus: Int) {
        // Create the request parameter to sent in the request
        val requestBody = JSONObject()
        requestBody.put("user_id", activeUserId)
        requestBody.put("participant_id", packageId)
        requestBody.put("new_status", newStatus)

        // Create the request object
        val finishRequest = JsonObjectRequest(Request.Method.PUT, "${WholeShareApiService.WS_HOST}/requests/updatePackageStatus", requestBody,
            { response ->
                if (response.getInt("status") == 0)
                    alertDialogFailed("Request Failed", response.getString("reason"))
                else {
                    val targetPackage = dataset.find { it.id == packageId }

                    if (targetPackage != null) {
                        val packageIndex = dataset.indexOf(targetPackage)
                        try {
                            val removeStatus = dataset.remove(targetPackage)
                            if (removeStatus) {
                                this.notifyItemRemoved(packageIndex)
                            }
                        }
                        catch (e: Exception) {
                            Log.d("FINISH_REQUEST", "finishDeliverPackage Error: ${e.message}")
                        }
                    }
                }
            },
            { error ->
                alertDialogFailed("Request Error", "${error}")
            }
        )
        finishRequest.retryPolicy = DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

        WholeShareApiService.getInstance(activity).addToRequestQueue(finishRequest)
    }

    /**
     * Move to a new fragment that will show the detail of the package that is being clicked.
     *
     * @param packageId The id of the package being clicked.
     */
    private fun navigateToDetails(packageId: Int) {
        val detailBundle = Bundle()
        detailBundle.putInt("package_id", packageId)
        detailBundle.putInt("mode", mode)
        detailBundle.putInt("active_user_id", activeUserId)

        // Create the target detail fragment
        val detailFragment = DriverPackageDetailFragment(parentFragment);

        (activity as DriverMainActivity).switchFragment(R.id.fragment_container_driver, detailFragment, detailBundle)
    }

    //alert dialog warning
    private fun alertDialogFailed(title: String, message: String){
        val mAlertDialog = AlertDialog.Builder(activity)
        mAlertDialog.setIcon(R.drawable.high_priority_80px) //set alertdialog icon
        mAlertDialog.setTitle(title) //set alertdialog title
        mAlertDialog.setMessage(message) //set alertdialog message
        mAlertDialog.setPositiveButton("OK") { dialog, id ->

        }
        mAlertDialog.show()
    }
}