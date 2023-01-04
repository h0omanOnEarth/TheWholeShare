package com.clarissa.thewholeshare.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.clarissa.thewholeshare.R
import com.clarissa.thewholeshare.api.WholeShareApiService
import com.clarissa.thewholeshare.models.Location
import com.clarissa.thewholeshare.models.Request
import com.clarissa.thewholeshare.models.User
import org.json.JSONArray
import java.sql.Timestamp
import java.util.*


class UserDonateFragment : Fragment() {

    lateinit var spinnerLocation:Spinner
    lateinit var etPickUpAddress:EditText
    lateinit var etNote_donate:EditText
    lateinit var btnDonate:Button

    //web service :
    val WS_HOST = "http://10.0.2.2:8000/api"

    lateinit var arrRequests : MutableList<Request>
    lateinit var spinnerAdapter: ArrayAdapter<Request>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arrRequests=  mutableListOf()
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

        refreshList()

        spinnerAdapter = ArrayAdapter(view.context,android.R.layout.simple_spinner_item,arrRequests)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
        spinnerLocation.adapter = spinnerAdapter

    }

    fun refreshList(){
        val strReq = object: StringRequest(
            Method.GET,
            "${WholeShareApiService.WS_HOST}/listRequests",
            Response.Listener {
                val obj: JSONArray = JSONArray(it)
                arrRequests.clear()
                println(obj.length())
                for (i in 0 until obj.length()){
                    val o = obj.getJSONObject(i)
                    println(o)
                    val id = o.getInt("id")
                    val location = o.getString("location")
                    val batch = o.getInt("batch")
                    val deadline = o.get("deadline").toString()
                    val note = o.getString("note")
                    val status = o.getInt("status")
                    val created_at = o.get("created_at").toString()
                    val updated_at = o.get("updated_at").toString()
                    val deleted_at = o.get("deleted_at").toString()

                    val req = Request(
                        id,location,batch,deadline,note,status,created_at,updated_at,deleted_at
                    )
                    arrRequests.add(req)

                }
                println(arrRequests.size)
            },
            Response.ErrorListener {
                Toast.makeText(context,"ERROR!", Toast.LENGTH_SHORT).show()
            }
        ){}
        val queue: RequestQueue = Volley.newRequestQueue(context)
        queue.add(strReq)
    }

}