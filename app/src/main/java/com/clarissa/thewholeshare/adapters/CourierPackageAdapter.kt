package com.clarissa.thewholeshare.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.clarissa.thewholeshare.R
import com.clarissa.thewholeshare.api.WholeShareApiService
import com.clarissa.thewholeshare.api.helpers.ParticipantsStatuses
import com.clarissa.thewholeshare.api.responses.CourierPackage
import org.json.JSONObject

class CourierPackageAdapter(
    val context: Context,
    val dataset: ArrayList<CourierPackage>,
    var mode: Int,
    val activeUserId: Int
) : RecyclerView.Adapter<CourierPackageAdapter.PackageViewHolder>() {
    class PackageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val packageIdLabel = view.findViewById<TextView>(R.id.packageIdLabel)
        val packageAdressLabel = view.findViewById<TextView>(R.id.packageAddressLabel)
        val packageOwnerLabel = view.findViewById<TextView>(R.id.packageOwnerLabel)
        val packageActionButton = view.findViewById<Button>(R.id.packageActionButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PackageViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_package, parent, false)
        
        return PackageViewHolder(view)
    }

    override fun onBindViewHolder(holder: PackageViewHolder, position: Int) {
        holder.packageIdLabel.text = "#${dataset[position].id}"
        holder.packageAdressLabel.text = dataset[position].pickup
        holder.packageOwnerLabel.text = dataset[position].full_name
        holder.itemView.tag = dataset[position].id

        // Set what the action button in every card do depending on the mode being passed to the adapter
        if (mode == ParticipantsStatuses.DELIVERING) {
            holder.packageActionButton.text = "Finish"
            holder.packageActionButton.visibility = TextView.VISIBLE

            holder.packageActionButton.setOnClickListener { updatePackageStatus(holder.itemView.tag as Int, ParticipantsStatuses.DELIVERED) }
        }
        else if (mode == ParticipantsStatuses.DELIVERED) {
            // An unused button to undo the state of the package. (Add the event and add the visibility to use it)
            holder.packageActionButton.text = "Undo"
            holder.packageActionButton.visibility = TextView.GONE

//            holder.packageActionButton.setOnClickListener { updatePackageStatus(holder.itemView.tag as Int, ParticipantsStatuses.DELIVERING) }
        }
        else holder.packageActionButton.visibility = TextView.GONE
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    /**
     * Sent a request to the server to update the status of the package from delivering to finish being delivered.
     *
     * @param packageId The id of the participant package that is going to be updated.
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

        WholeShareApiService.getInstance(context).addToRequestQueue(finishRequest)
    }

    //alert dialog warning
    private fun alertDialogFailed(title: String, message: String){
        val mAlertDialog = AlertDialog.Builder(context)
        mAlertDialog.setIcon(R.drawable.high_priority_80px) //set alertdialog icon
        mAlertDialog.setTitle(title) //set alertdialog title
        mAlertDialog.setMessage(message) //set alertdialog message
        mAlertDialog.setPositiveButton("OK") { dialog, id ->

        }
        mAlertDialog.show()
    }
}