package com.clarissa.thewholeshare.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.clarissa.thewholeshare.AdminMainActivity
import com.clarissa.thewholeshare.R
import com.clarissa.thewholeshare.api.WholeShareApiService
import com.clarissa.thewholeshare.models.Location
import org.json.JSONArray

class AdminHomeFragment : Fragment() {

    lateinit var tvTotalLocations : TextView
    lateinit var tvTotalPackages_OnGoing: TextView
    lateinit var tvTotalPackages_Finished : TextView
    lateinit var tvTotalPackages_Canceled : TextView

    var arrLocations = ArrayList<Location>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view =  inflater.inflate(R.layout.fragment_admin_home, container, false)
        getRequests()
        return view;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvTotalLocations = view.findViewById(R.id.tvTotalLoc_homeAdmin)
        tvTotalPackages_OnGoing = view.findViewById(R.id.tvTotalPackagesOnGoing_homeAdmin)
        tvTotalPackages_Canceled = view.findViewById(R.id.tvTotalFinishedPackage_homeAdmin)
        tvTotalPackages_Finished = view.findViewById(R.id.tvTotalCanceledPackages_homeAdmin)
    }

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
                isi()
            },
            Response.ErrorListener {
                Toast.makeText((context as AdminMainActivity),"ERROR!", Toast.LENGTH_SHORT).show()
            }
        ){}
        val queue: RequestQueue = Volley.newRequestQueue((context as AdminMainActivity))
        queue.add(strReq)
        //println("size 2" + arrLocations.size)
    }

    fun isi(){
        tvTotalLocations.setText(arrLocations.size.toString())
    }
}