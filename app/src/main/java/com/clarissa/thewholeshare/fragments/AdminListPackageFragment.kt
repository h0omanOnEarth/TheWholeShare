package com.clarissa.thewholeshare.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.clarissa.thewholeshare.AdminMainActivity
import com.clarissa.thewholeshare.R
import com.clarissa.thewholeshare.api.WholeShareApiService
import com.clarissa.thewholeshare.models.Location
import com.clarissa.thewholeshare.models.Participant
import com.clarissa.thewholeshare.models.User
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONObject

class AdminListPackageFragment : Fragment() {

    lateinit var btnmake : Button
    lateinit var btnongoing : Button
    lateinit var btnfinish : Button
    lateinit var btncancel : Button
    lateinit var rv : RecyclerView

    var idRequest : Int = -1
    var arrPackage = ArrayList<Participant>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        idRequest = arguments?.getInt("id",-1)!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_admin_list_package, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnmake = view.findViewById(R.id.btnmakereport)
        btnongoing = view.findViewById(R.id.btnongoing)
        btncancel = view.findViewById(R.id.btncancel)
        btnfinish = view.findViewById(R.id.btnfinished)
        rv = view.findViewById(R.id.rvdetaillistpackage)
    }

    fun getPackage(id : Int) {
        val strReq = object: StringRequest(
            Method.GET,
            "${WholeShareApiService.WS_HOST}/listPackageByRequest",
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
                    //arrLocations.add(r)
                    //println("test lokasi"+arrLocations[i].address)
                }
                //refreshRecycler()
            },
            Response.ErrorListener {
                Toast.makeText((context as AdminMainActivity),"ERROR!", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String>? {
                val params = HashMap<String, String>()
                params["id"] = id.toString()
                return params
            }
        }
        val queue: RequestQueue = Volley.newRequestQueue((context as AdminMainActivity))
        queue.add(strReq)
    }
}