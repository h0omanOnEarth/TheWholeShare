package com.clarissa.thewholeshare.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.clarissa.thewholeshare.AdminMainActivity
import com.clarissa.thewholeshare.adapters.LocationAdapter
import com.clarissa.thewholeshare.R
import com.clarissa.thewholeshare.adapters.MasterLocationAdapter
import com.clarissa.thewholeshare.api.WholeShareApiService
import com.clarissa.thewholeshare.models.Location
import com.clarissa.thewholeshare.models.Request
import org.json.JSONArray


class AdminMasterFragment(

) : Fragment() {

    var onClickButton:((resource:String)->Unit)? = null
    lateinit var rvLocations:RecyclerView
    lateinit var btnToAddLocation : Button

    lateinit var locationAdapter: LocationAdapter

//    //web service :
//    val WS_HOST = "http://10.0.2.2:8000/api"

    //mutable list
    var arrLocations = ArrayList<Location>()
    //rv
    lateinit var locationrv:RecyclerView
    lateinit var adapterLocation:MasterLocationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_admin_master, container, false)
        locationrv = view.findViewById(R.id.rvLocations)
        btnToAddLocation = view.findViewById(R.id.btnToAddLocation)

        btnToAddLocation.setOnClickListener {
            (context as AdminMainActivity).switchFragment(3)
        }
        getRequests()
        //println("size" + arrLocations.size)
        //refreshRecycler()
        return view
    }
    fun refreshRecycler()
    {
        adapterLocation = MasterLocationAdapter(activity as AdminMainActivity, arrLocations)
        locationrv.adapter = adapterLocation
        val mLayoutManager: RecyclerView.LayoutManager = GridLayoutManager((activity as AdminMainActivity), 1)
        locationrv.layoutManager =  mLayoutManager
    }
    //fungsi get requests
    fun getRequests(){
        val strReq = object: StringRequest(
            Method.GET,
            "${WholeShareApiService.WS_HOST}/listRequest",
            Response.Listener {
                val obj: JSONArray = JSONArray(it)
                println(obj.length())
                for (i in 0 until obj.length()) {
                    val o = obj.getJSONObject(i)
                    println(o)
                    val id = o.getInt("id")
                    val location = o.getString("location")
                    val batch = o.getInt("batch")
                    val deadline = o.get("deadline").toString()
                    val note = o.getString("note")
                    val status = o.getString("status")

                    val r = Location(
                        id, location, batch, deadline, note, status
                    )
                    arrLocations.add(r)
                    println("test lokasi"+arrLocations[i].address)
                }
                refreshRecycler()
            },
            Response.ErrorListener {
                Toast.makeText((context as AdminMainActivity),"ERROR!", Toast.LENGTH_SHORT).show()
            }
        ){}
        val queue: RequestQueue = Volley.newRequestQueue((context as AdminMainActivity))
        queue.add(strReq)
        //println("size 2" + arrLocations.size)
    }

}