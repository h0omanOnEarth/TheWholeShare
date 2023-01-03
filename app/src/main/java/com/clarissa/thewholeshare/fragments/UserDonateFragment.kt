package com.clarissa.thewholeshare.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import com.clarissa.thewholeshare.R
import com.clarissa.thewholeshare.models.Location


class UserDonateFragment : Fragment() {

    lateinit var spinnerLocation:Spinner
    lateinit var etPickUpAddress:EditText
    lateinit var etNote_donate:EditText
    lateinit var btnDonate:Button

    //web service :
    val WS_HOST = "http://10.0.2.2:8000/api"

    lateinit var arrLocations : MutableList<Location>
    lateinit var spinnerAdapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_donate, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        spinnerLocation = view.findViewById(R.id.spinnerLocations)
        etPickUpAddress = view.findViewById(R.id.etPickUpAddress_User)
        etNote_donate = view.findViewById(R.id.etNote_donate)
        btnDonate = view.findViewById(R.id.btnDonate)



    }
}