package com.clarissa.thewholeshare.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.clarissa.thewholeshare.R
import com.clarissa.thewholeshare.api.WholeShareApiService
import com.clarissa.thewholeshare.models.User


class DriverHomeFragment : Fragment() {
    // Components
    lateinit var availablePacketsLabel: TextView
    lateinit var ongoingPacketsLabel: TextView
    lateinit var canceledPacketsLabel: TextView
    lateinit var finishedPacketsLabel: TextView

    // Variable
    lateinit var activeUser: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Variables
        activeUser = this.requireArguments().get("active_user") as User
//        Log.d("ACTIVE_USER", "onCreate: ${activeUser}")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_driver_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize components
        availablePacketsLabel = view.findViewById(R.id.tv_totalPackAvailable)
        ongoingPacketsLabel = view.findViewById(R.id.tv_totalPackOnGoing)
        canceledPacketsLabel = view.findViewById(R.id.tvTotalPackCanceled_HomeAdmin)
        finishedPacketsLabel = view.findViewById(R.id.tvTotalFinished_homeAdmin)

        // Load Events
        countAllAvailablePackets(availablePacketsLabel)
        countAllOngoingPackets(ongoingPacketsLabel)
        countAllCancelledPackets(canceledPacketsLabel)
        countAllFinishedPackets(finishedPacketsLabel)

        // Register Events
    }

    /**
     * Fetch the total count of packets that is currently available and have not yet been taken by other courier.
     * Display the successful count message to the available counter in a card.
     *
     * @param label A TextView that will output the count result of the response.
     */
    private fun countAllAvailablePackets(label: TextView) {
        // Make the request object
        val availableRequest = StringRequest(Request.Method.GET, "${WholeShareApiService.WS_HOST}/requests/countAvailable",
            {
                label.text = it
            },
            {
                Log.d("AVAILABLE_PACKETS", "ERROR: ${it.message}")
            }
        )
        // Set the request timeout to 5 second before attempting to retrying
        availableRequest.setRetryPolicy(DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT))

        // Add the request object to the queue to process
        WholeShareApiService.getInstance(this.requireContext()).addToRequestQueue(availableRequest)
    }

    /**
     * Fetch the total count of packets that is currently ongoing and have been taken by you.
     * Display the successful count message to the ongoing counter in a card.
     *
     * @param label A TextView that will output the count result of the response.
     */
    private fun countAllOngoingPackets(label: TextView) {
        // Make the request object
        val ongoingRequest = StringRequest(Request.Method.GET, "${WholeShareApiService.WS_HOST}/requests/countOngoing?user_id=${activeUser.id}",
            {
                label.text = it
            },
            {
                Log.d("ONGOING_PACKETS", "ERROR: ${it.message}")
            }
        )
        // Set the request timeout to 5 second before attempting to retrying
        ongoingRequest.retryPolicy = DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

        // Add the request object to the queue to process
        WholeShareApiService.getInstance(this.requireContext()).addToRequestQueue(ongoingRequest)
    }

    /**
     * Fetch the total count of packets that is cancelled.
     * Display the successful count message to the ongoing counter in a card.
     *
     * @param label A TextView that will output the count result of the response.
     */
    private fun countAllCancelledPackets(label: TextView) {
        // Make the request object
        val cancelledRequest = StringRequest(Request.Method.GET, "${WholeShareApiService.WS_HOST}/requests/countCancelled?user_id=${activeUser.id}",
            {
                label.text = it
            },
            {
                Log.d("CANCELLED_PACKETS", "ERROR: ${it.message}")
            }
        )
        // Set the request timeout to 5 second before attempting to retrying
        cancelledRequest.retryPolicy = DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

        // Add the request object to the queue to process
        WholeShareApiService.getInstance(this.requireContext()).addToRequestQueue(cancelledRequest)
    }

    /**
     * Fetch the total count of packets that is finished by you.
     * Display the successful count message to the ongoing counter in a card.
     *
     * @param label A TextView that will output the count result of the response.
     */
    private fun countAllFinishedPackets(label: TextView) {
        // Make the request object
        val finishedRequest = StringRequest(Request.Method.GET, "${WholeShareApiService.WS_HOST}/requests/countFinished?user_id=${activeUser.id}",
            {
                label.text = it
            },
            {
                Log.d("FINISHED_PACKETS", "ERROR: ${it.message}")
            }
        )
        // Set the request timeout to 5 second before attempting to retrying
        finishedRequest.retryPolicy = DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

        // Add the request object to the queue to process
        WholeShareApiService.getInstance(this.requireContext()).addToRequestQueue(finishedRequest)
    }
}