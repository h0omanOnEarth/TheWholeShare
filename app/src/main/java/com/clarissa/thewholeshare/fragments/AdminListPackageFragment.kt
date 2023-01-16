package com.clarissa.thewholeshare.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
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
import com.clarissa.thewholeshare.adapters.ListPackageAdapter
import com.clarissa.thewholeshare.adapters.MasterLocationAdapter
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
    var stat : Int = 2
    var arrPackage = ArrayList<Participant>()

    lateinit var dp : ListPackageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        idRequest = arguments?.getInt("id",-1)!!
        println("id : "+idRequest)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_admin_list_package, container, false)
        btnmake = view.findViewById(R.id.btnmakereport)
        btnongoing = view.findViewById(R.id.btnongoing)
        btncancel = view.findViewById(R.id.btncancel)
        btnfinish = view.findViewById(R.id.btnfinished)
        rv = view.findViewById(R.id.rvdetaillistpackage)
        getPackage(idRequest,stat)
        btnfinish.setOnClickListener(View.OnClickListener {
            getPackage(idRequest,3)
        })
        btnongoing.setOnClickListener(View.OnClickListener {
            getPackage(idRequest,2)
        })
        btncancel.setOnClickListener(View.OnClickListener {
            getPackage(idRequest,4)
        })

        //make report
        btnmake.setOnClickListener {
            (context as AdminMainActivity).switchMakeReport(idRequest)
        }
        return view
    }



    fun getPackage(id : Int, stat : Int) {
        arrPackage.clear()
        val strReq = object: StringRequest(
            Method.POST,
            "${WholeShareApiService.WS_HOST}/listPackageByRequest",
            Response.Listener {
                println("cek it : " +it)
                val obj: JSONArray = JSONArray(it)
                println("obj : " + obj.length())
                for (i in 0 until obj.length()) {
                    val o = obj.getJSONObject(i)
                    println(o)
                    val id = o.getInt("id")
                    val user_id = o.getInt("user_id")
                    val request_id = o.getInt("request_id")
                    val courier_id = o.getInt("courier_id")
                    val fullname = o.getString("full_name")
                    val pickup = o.getString("pickup")
                    val note = o.get("note").toString()
                    val status = o.getInt("status")

                    val r = Participant(
                        id, user_id, request_id, courier_id, pickup, note, status, fullname
                    )
                    arrPackage.add(r)
                    //println("test lokasi"+arrLocations[i].address)
                }
                println("size : "+arrPackage.size)
                refreshRecycler()
            },
            Response.ErrorListener {
//                println(it.message)
//                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String>? {
                val params = HashMap<String, String>()
                params["id"] = id.toString()
                params["stat"] = stat.toString()

                return params
            }
        }
        val queue: RequestQueue = Volley.newRequestQueue((context as AdminMainActivity))
        queue.add(strReq)
    }

    fun refreshRecycler()
    {
        dp = ListPackageAdapter(activity as AdminMainActivity, arrPackage)
        rv.adapter = dp
        val mLayoutManager: RecyclerView.LayoutManager = GridLayoutManager((activity as AdminMainActivity), 1)
        rv.layoutManager =  mLayoutManager
    }
}