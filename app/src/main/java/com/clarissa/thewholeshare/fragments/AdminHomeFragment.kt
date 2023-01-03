package com.clarissa.thewholeshare.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.clarissa.thewholeshare.R

class AdminHomeFragment : Fragment() {

    lateinit var tvTotalLocations : TextView
    lateinit var tvTotalPackages_OnGoing: TextView
    lateinit var tvTotalPackages_Finished : TextView
    lateinit var tvTotalPackages_Canceled : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvTotalLocations = view.findViewById(R.id.tvTotalLoc_homeAdmin)
        tvTotalPackages_OnGoing = view.findViewById(R.id.tvTotalPackagesOnGoing_homeAdmin)
        tvTotalPackages_Canceled = view.findViewById(R.id.tvTotalFinishedPackage_homeAdmin)
        tvTotalPackages_Finished = view.findViewById(R.id.tvTotalCanceledPackages_homeAdmin)
    }


}