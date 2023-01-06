package com.clarissa.thewholeshare.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.clarissa.thewholeshare.AdminMainActivity
import com.clarissa.thewholeshare.adapters.LocationAdapter
import com.clarissa.thewholeshare.R
import com.clarissa.thewholeshare.models.Location


class AdminMasterFragment(

) : Fragment() {

    var onClickButton:((resource:String)->Unit)? = null
    lateinit var rvLocations:RecyclerView
    lateinit var btnToAddLocation : Button

    lateinit var locationAdapter: LocationAdapter

//    //web service :
//    val WS_HOST = "http://10.0.2.2:8000/api"

    //mutable list
    lateinit var arrLocations : MutableList<Location>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_master, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvLocations = view.findViewById(R.id.rvLocations)
        btnToAddLocation = view.findViewById(R.id.btnToAddLocation)

        btnToAddLocation.setOnClickListener {
            (context as AdminMainActivity).switchFragment(3)
        }

    }

}