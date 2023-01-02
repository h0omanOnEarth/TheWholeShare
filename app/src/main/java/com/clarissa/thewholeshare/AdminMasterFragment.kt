package com.clarissa.thewholeshare

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray


class AdminMasterFragment(

) : Fragment() {

    var onClickButton:((resource:String)->Unit)? = null
    lateinit var rvLocations:RecyclerView
    lateinit var btnToAddLocation : Button

    lateinit var locationAdapter: LocationAdapter

    //web service :
    val WS_HOST = "http://10.0.2.2:8000/api"

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
            onClickButton?.invoke("add")
        }

        arrLocations = mutableListOf()
        rvLocations.layoutManager = LinearLayoutManager(view.context?.applicationContext)
        locationAdapter = LocationAdapter(view.context, arrLocations, R.layout.item_location)
        rvLocations.adapter = locationAdapter

        fun refreshList(){
            val strReq = object: StringRequest(
                Method.GET,
                "$WS_HOST/listLocations",
                //kalau sukses callback nya apa
                Response.Listener {
                    //it itu hasil nembak laravel nanti masuk ke variable it
                    val obj: JSONArray = JSONArray(it)
                    arrLocations.clear()
                    for (i in 0 until obj.length()){
                        val o = obj.getJSONObject(i)
                        val id = o.getInt("id")
                        val address = o.getString("address")
                        val note = o.getString("note")
                        val status = o.getInt("status")
                        val deleted_at = o.get("deleted_at").toString()
                        val loc = Location(
                           id,address,note,status,deleted_at
                        )
                        arrLocations.add(loc)
                    }
                    locationAdapter.notifyDataSetChanged()
                },

                Response.ErrorListener {
                    Toast.makeText(context,"ERROR!", Toast.LENGTH_SHORT).show()
                }
            ){}
            val queue: RequestQueue = Volley.newRequestQueue(context)
            queue.add(strReq)
        }

        refreshList()

    }

}