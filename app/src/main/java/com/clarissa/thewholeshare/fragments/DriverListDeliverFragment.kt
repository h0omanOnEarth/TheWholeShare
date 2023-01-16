package com.clarissa.thewholeshare.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.clarissa.thewholeshare.R
import com.clarissa.thewholeshare.adapters.CourierPackageAdapter
import com.clarissa.thewholeshare.api.WholeShareApiService
import com.clarissa.thewholeshare.api.helpers.ParticipantsStatuses
import com.clarissa.thewholeshare.api.responses.CourierPackage
import com.clarissa.thewholeshare.models.Participant
import com.clarissa.thewholeshare.models.User
import com.google.gson.Gson


class DriverListDeliverFragment : Fragment() {
    // Components
    lateinit var packageListRecyclerView: RecyclerView
    lateinit var showOngoingButton: Button
    lateinit var showFinishedButton: Button

    // Variables
    lateinit var activeUser: User
    lateinit var packageAdapter: CourierPackageAdapter

    val packageList = arrayListOf<CourierPackage>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Variables
        activeUser = this.requireArguments()["active_user"] as User
        packageAdapter = CourierPackageAdapter(requireActivity(), packageList, ParticipantsStatuses.DELIVERING, activeUser.id, this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_driver_list_deliver, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Components
        packageListRecyclerView = view.findViewById(R.id.packageListRecyclerView)
        showFinishedButton = view.findViewById(R.id.showFinishedButton)
        showOngoingButton = view.findViewById(R.id.showOngoingButton)

        packageListRecyclerView.adapter = packageAdapter
        packageListRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        // Load Events
        fetchOngoingPackages(packageListRecyclerView, packageList)
        setBtnOnGoing()

        // Register Events
        showOngoingButton.setOnClickListener {
            fetchOngoingPackages(packageListRecyclerView, packageList)
            setBtnOnGoing()
        }
        showFinishedButton.setOnClickListener {
            fetchDeliveredPackages(packageListRecyclerView, packageList)
            setBtnFinished()
        }
    }
    fun setBtnOnGoing()
    {
        showOngoingButton.setBackgroundResource(R.drawable.rounded_square_3)
        showOngoingButton.setTextColor(Color.WHITE)
        showFinishedButton.setBackgroundResource(R.drawable.rounded_square_2)
        showFinishedButton.setTextColor(Color.BLACK)
    }
    fun setBtnFinished()
    {
        showOngoingButton.setBackgroundResource(R.drawable.rounded_square_2)
        showOngoingButton.setTextColor(Color.BLACK)
        showFinishedButton.setBackgroundResource(R.drawable.rounded_square_3)
        showFinishedButton.setTextColor(Color.WHITE)
    }

    /**
     * Get the ongoing packets being worked by the courier and put them to the recyclerView provided by the parameter.
     *
     * @param recyclerView The recycler view which the result is to be printed on.
     * @param dataset      The dataset that is attached to the adapter, which is the one to be updated.
     */
    private fun fetchOngoingPackages(recyclerView: RecyclerView, dataset: ArrayList<CourierPackage>) {
        recyclerView.visibility = View.INVISIBLE

        // Update the adapter mode to fetch delivering packages
        (recyclerView.adapter as CourierPackageAdapter).mode = ParticipantsStatuses.DELIVERING

        // Create the request object
        val ongoingRequest = JsonArrayRequest(Request.Method.GET, "${WholeShareApiService.WS_HOST}/requests/getOngoingPackets?user_id=${activeUser.id}", null,
            { response ->
                // Check if the response is successful or not
                if (response.length() == 1) {
                    if (response.getJSONObject(0).has("status") && response.getJSONObject(0).has("reason")) {
                        if (response.getJSONObject(0).getInt("status") == 0) {
                            Toast.makeText(requireContext(), "Invalid user request!", Toast.LENGTH_SHORT).show()
                            return@JsonArrayRequest
                        }
                    }
                }

                // Clear the list and update the UI to be empty.
                dataset.clear()

                // Fill in the response data to the dataset in the adapter
                for (i in 0 until response.length()) {
                    val thisPackage = Gson().fromJson(response.getJSONObject(i).toString(), CourierPackage::class.java)
                    dataset.add(thisPackage)
                }
                recyclerView.adapter?.notifyDataSetChanged()

                recyclerView.visibility = View.VISIBLE
            },
            { err ->
                Log.d("FETCH_ONGOING", "Error: ${err.message}")
            }
        )
        // Set the retry request policy before throwing a timeout
        ongoingRequest.setRetryPolicy(DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT))

        // Add the request to the service queue
        WholeShareApiService.getInstance(requireContext()).addToRequestQueue(ongoingRequest)
    }

    /**
     * Get the delivered packets being worked by the courier and put them to the recyclerView provided by the parameter.
     *
     * @param recyclerView The recycler view which the result is to be printed on.
     * @param dataset      The dataset that is attached to the adapter, which is the one to be updated.
     */
    private fun fetchDeliveredPackages(recyclerView: RecyclerView, dataset: ArrayList<CourierPackage>) {
        recyclerView.visibility = View.INVISIBLE

        // Update the adapter mode to fetch delivered packages
        (recyclerView.adapter as CourierPackageAdapter).mode = ParticipantsStatuses.DELIVERED

        // Create the request object
        val ongoingRequest = JsonArrayRequest(Request.Method.GET, "${WholeShareApiService.WS_HOST}/requests/getDeliveredPackets?user_id=${activeUser.id}", null,
            { response ->
                // Check if the response is successful or not
                if (response.length() == 1) {
                    if (response.getJSONObject(0).has("status") && response.getJSONObject(0).has("reason")) {
                        if (response.getJSONObject(0).getInt("status") == 0) {
                            Toast.makeText(requireContext(), response.getJSONObject(0).getString("reason"), Toast.LENGTH_SHORT).show()
                            return@JsonArrayRequest
                        }
                    }
                }

                // Clear the list and update the UI to be empty.
                dataset.clear()

                // Fill in the response data to the dataset in the adapter
                for (i in 0 until response.length()) {
                    val thisPackage = Gson().fromJson(response.getJSONObject(i).toString(), CourierPackage::class.java)
                    dataset.add(thisPackage)
                }
                recyclerView.adapter?.notifyDataSetChanged()

                recyclerView.visibility = View.VISIBLE
            },
            { err ->
                Log.d("FETCH_ONGOING", "Error: ${err.message}")
            }
        )
        // Set the retry request policy before throwing a timeout
        ongoingRequest.setRetryPolicy(DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT))

        // Add the request to the service queue
        WholeShareApiService.getInstance(requireContext()).addToRequestQueue(ongoingRequest)
    }
}