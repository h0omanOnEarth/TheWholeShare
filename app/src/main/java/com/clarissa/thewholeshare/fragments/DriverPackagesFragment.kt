package com.clarissa.thewholeshare.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.clarissa.thewholeshare.models.User
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject

class DriverPackagesFragment : Fragment() {
    // Components
    lateinit var recyclerViewDriverPackages: RecyclerView

    // Variables
    lateinit var packageAdapter: CourierPackageAdapter
    lateinit var activeUser: User
    val packageList = arrayListOf<CourierPackage>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Variables
        activeUser = this.requireArguments()["active_user"] as User
        packageAdapter = CourierPackageAdapter(requireContext(), packageList, ParticipantsStatuses.PENDING, activeUser.id)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_driver_packages, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Components
        recyclerViewDriverPackages = view.findViewById(R.id.rvListPackages_Driver)
        recyclerViewDriverPackages.adapter = packageAdapter
        recyclerViewDriverPackages.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        // Load Events
        fetchAvailablePackages(recyclerViewDriverPackages, packageList)

        // Register Events

    }

    /**
     * Create a GET request to the server to get the list of pending packages.
     *
     * @param recyclerView The recycler view that will show the list of data to the user'
     * @param list         The list of data in the application that will be updated by the response of the request.
     */
    private fun fetchAvailablePackages(recyclerView: RecyclerView, list: ArrayList<CourierPackage>) {
        // Create the GET object request
        val availableRequest = JsonArrayRequest(Request.Method.GET, "${WholeShareApiService.WS_HOST}/requests/getAvailablePackets?user_id=${activeUser.id}", null,
            { response ->
                Log.d("AVAILABLE_PACKAGE", "fetchAvailablePackages: $response")
                if (response.length() == 1) {
                    if (response.getJSONObject(0).has("status")) {
                        if (response.getJSONObject(0).getInt("status") == 0) {
                            Toast.makeText(requireContext(), response.getJSONObject(0).getString("reason"), Toast.LENGTH_SHORT).show()
                            return@JsonArrayRequest
                        }
                    }
                }
                // Clear list data to reset it
                list.clear()

                // Iterate the data from the response and add it to the application arraylist
                for (i in 0 until response.length()) {
                    val newPackage = Gson().fromJson(response.getJSONObject(i).toString(), CourierPackage::class.java)
                    Log.d("AVAILABLE_PACKAGE", "fetchAvailablePackages: $newPackage")
                    list.add(newPackage)
                    recyclerView.adapter?.notifyItemInserted(i)
                }
                recyclerView.adapter?.notifyDataSetChanged()
            },
            { error ->
                Log.d("FETCH_AVAILABLE", "fetchAvailablePackages: ${error}")
            }
        )
        // Set the retry policy of the request before attempting a retry or a timeout
        availableRequest.retryPolicy = DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

        // Add the request to the queue of the API service
        WholeShareApiService.getInstance(requireContext()).addToRequestQueue(availableRequest)
    }
}