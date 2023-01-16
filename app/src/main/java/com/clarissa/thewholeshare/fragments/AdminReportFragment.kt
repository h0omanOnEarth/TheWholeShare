package com.clarissa.thewholeshare.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONObject
import java.util.HashMap

class AdminReportFragment : Fragment() {

    lateinit var ettitle:EditText
    lateinit var etreport:EditText
    lateinit var btnsend:Button
    lateinit var btncancel:Button

    var arrLocations = ArrayList<Location>()
    var arrParticipants = ArrayList<Participant>()
    var idRequest:Int = -1

    var batchlama=-1
    var batchbaru=-1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        idRequest = arguments?.getInt("id",-1)!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_admin_report, container, false)
        getRequests()
        getParticipants()
        return view
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
                }
                setBatchLama()
            },
            Response.ErrorListener {
                Toast.makeText(context,"ERROR!", Toast.LENGTH_SHORT).show()
            }
        ){}
        val queue: RequestQueue = Volley.newRequestQueue(context)
        queue.add(strReq)
    }

    //fungsi set batch lama
    fun setBatchLama() {
        println("jum arr loc = " + arrLocations.size)
        batchlama = arrLocations[idRequest - 1].batch
        batchbaru = batchlama+1
        println("batch: "+batchlama)
    }

    //fungsi get participants
    fun getParticipants(){
        val strReq = object: StringRequest(
            Method.GET,
            "${WholeShareApiService.WS_HOST}/listParticipants",
            Response.Listener {
                val obj: JSONArray = JSONArray(it)
                println(obj.length())
                for (i in 0 until obj.length()) {
                    val o = obj.getJSONObject(i)
                    println(o)
//                    val id = o.getInt("id")
//                    val user_id = o.getInt("user_id")
//                    val request_id = o.getInt("request_id")
//                    val courier_id = o.getInt("courier_id")
//                    val pickup = o.getString("pickup")
//                    val note = o.getString("note")
//                    val status = o.getInt("status")
//
//                    val p = Participant(
//                        id, user_id, request_id, courier_id, pickup, note, status
//                    )
                    val p = Gson().fromJson(o.toString(), Participant::class.java)
                    arrParticipants.add(p)
                }
            },
            Response.ErrorListener {
                Toast.makeText((context as AdminMainActivity),"ERROR!", Toast.LENGTH_SHORT).show()
            }
        ){}
        val queue: RequestQueue = Volley.newRequestQueue((context as AdminMainActivity))
        queue.add(strReq)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ettitle = view.findViewById(R.id.etTitle_titleReport)
        etreport = view.findViewById(R.id.etReport_report)
        btnsend = view.findViewById(R.id.btnSend_report)
        btncancel = view.findViewById(R.id.btnCancel_report)
        println("size req = "+arrLocations.size)
        println("batchlama:"+batchlama+" batchbaru: "+batchbaru)

        btnsend.setOnClickListener {
            val title = ettitle.text.toString()
            val content = etreport.text.toString()
            if(title!=""&&content!="")
            {
                addReport(title,content)
                updateRequest(batchbaru)
                deleteParticipant()
            }
            else
            {
                alertDialogFailed("ERROR", "Fill all the fields!")
            }
        }

        btncancel.setOnClickListener {
            (context as AdminMainActivity).switchFragDetail(idRequest)
        }
    }
    fun updateRequest(batch:Int)
    {
        val strReq = object : StringRequest(
            Method.POST,
            "${WholeShareApiService.WS_HOST}/updatebatch",
            Response.Listener {
            },
            Response.ErrorListener {
                println(it.message)
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String>? {
                val params = HashMap<String, String>()
                params["id"] = idRequest.toString()
                params["batch"] = batch.toString()
                return params
            }
        }
        val queue: RequestQueue = Volley.newRequestQueue(context)
        queue.add(strReq)
    }
    fun addReport(title:String,content:String)
    {
        val requestBody = JSONObject()
        requestBody.put("title",title)
        requestBody.put("content",content)
        requestBody.put("request_id",idRequest)
        requestBody.put("batch", arrLocations[idRequest - 1].batch)
        val addReport = JsonObjectRequest(
            Request.Method.POST,"${ WholeShareApiService.WS_HOST}/addreport",requestBody,
            {
                    response -> alertDialogSuccess("Report Added!", "${title} has successfully been added!")
                clearAllFields()
                (context as AdminMainActivity).switchFragDetail(idRequest)
            },
            {
                    error -> alertDialogFailed("Add Location Failed", error.toString())
            })
        addReport.retryPolicy =
            DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

        // Queue the request to the service
        WholeShareApiService.getInstance((context as AdminMainActivity)).addToRequestQueue(addReport)
    }
    //alert dialog warning
    fun alertDialogFailed(title:String, message:String){
        val mAlertDialog = AlertDialog.Builder((context as AdminMainActivity))
        mAlertDialog.setIcon(R.drawable.high_priority_80px) //set alertdialog icon
        mAlertDialog.setTitle(title) //set alertdialog title
        mAlertDialog.setMessage(message) //set alertdialog message
        mAlertDialog.setPositiveButton("OK") { dialog, id ->

        }
        mAlertDialog.show()
    }

    //alert dialog sukses
    fun alertDialogSuccess(title:String, message:String){
        val mAlertDialog = AlertDialog.Builder((context as AdminMainActivity))
        mAlertDialog.setIcon(R.drawable.ok_80px) //set alertdialog icon
        mAlertDialog.setTitle(title) //set alertdialog title
        mAlertDialog.setMessage(message) //set alertdialog message
        mAlertDialog.setPositiveButton("OK") { dialog, id ->

        }
        mAlertDialog.show()
    }

    fun clearAllFields(){
        ettitle.text.clear()
        etreport.text.clear()
    }

    fun deleteParticipant()
    {
        val strReq = object : StringRequest(
            Method.POST,
            "${WholeShareApiService.WS_HOST}/deleteparticipant",
            Response.Listener {
            },
            Response.ErrorListener {
//                println(it.message)
//                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String>? {
                val params = HashMap<String, String>()
                params["id"] = idRequest.toString()
                return params
            }
        }
        val queue: RequestQueue = Volley.newRequestQueue(context)
        queue.add(strReq)
    }
}