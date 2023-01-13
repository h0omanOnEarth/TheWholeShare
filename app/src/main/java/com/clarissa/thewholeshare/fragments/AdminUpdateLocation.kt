package com.clarissa.thewholeshare.fragments

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
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
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class AdminUpdateLocation : Fragment() {
    lateinit var etlocation:EditText
    lateinit var etbatch:EditText
    lateinit var etdeadline:EditText
    lateinit var etnote:EditText
    lateinit var etstatus:EditText
    lateinit var btnedit: Button
    var idloc:Int=-1
    var listLocation = ArrayList<Location>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        idloc = arguments?.getInt("id",-1)!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_admin_update_location, container, false)
        getRequests()
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
                    listLocation.add(r)
                }
                loadfills()
            },
            Response.ErrorListener {
                Toast.makeText(context,"ERROR!", Toast.LENGTH_SHORT).show()
            }
        ){}
        val queue: RequestQueue = Volley.newRequestQueue(context)
        queue.add(strReq)
    }
    fun loadfills()
    {
        etlocation.setText(listLocation[idloc-1].address.toString())
        etbatch.setText(listLocation[idloc-1].batch.toString())
        etdeadline.setText(listLocation[idloc-1].deadline.toString())
        etnote.setText(listLocation[idloc-1].note.toString())
        etstatus.setText(listLocation[idloc-1].status.toString())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        etlocation = view.findViewById(R.id.etLocation)
        etbatch = view.findViewById(R.id.etbatch)
        etdeadline = view.findViewById(R.id.etDeadline)
        etnote = view.findViewById(R.id.etNote)
        etstatus = view.findViewById(R.id.etStatus)
        btnedit = view.findViewById(R.id.btnEdit_Loc)
        btnedit.setOnClickListener {

            val loc = etlocation.text.toString()
            val batch = etbatch.text.toString()
            val dead = etdeadline.text.toString()
            val note = etnote.text.toString()
            val status = etstatus.text.toString()
            if(loc!="" &&batch!="" && dead!="" && note!="" &&status!=""){
                Toast.makeText(context, "tes2", Toast.LENGTH_SHORT).show()
                getUpdate(idloc,loc,batch.toInt(),dead,note,status)
            }
            else{
                alertDialogFailed("ERROR","Fill all the fIelds!")
            }
        }
    }
    //alert dialog warning
    fun alertDialogFailed(title:String, message:String){
        val mAlertDialog = AlertDialog.Builder(context as AdminMainActivity)
        mAlertDialog.setIcon(R.drawable.high_priority_80px) //set alertdialog icon
        mAlertDialog.setTitle(title) //set alertdialog title
        mAlertDialog.setMessage(message) //set alertdialog message
        mAlertDialog.setPositiveButton("OK") { dialog, id ->

        }
        mAlertDialog.show()
    }

    //alert dialog sukses
    fun alertDialogSuccess(title:String, message:String){
        val mAlertDialog = AlertDialog.Builder(context as AdminMainActivity)
        mAlertDialog.setIcon(R.drawable.ok_80px) //set alertdialog icon
        mAlertDialog.setTitle(title) //set alertdialog title
        mAlertDialog.setMessage(message) //set alertdialog message
        mAlertDialog.setPositiveButton("OK") { dialog, id ->

        }
        mAlertDialog.show()
    }
    //update
    fun getUpdate(id : Int, location:String, batch:Int, deadline: String, note:String, status:String) {
        val strReq = object : StringRequest(
            Method.POST,
            "${WholeShareApiService.WS_HOST}/updateRequest",
            Response.Listener {
                alertDialogSuccess("SUCCESS", "Success Edit Request!")
            },
            Response.ErrorListener {
                println(it.message)
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String>? {
                val params = HashMap<String, String>()
                params["id"] = idloc.toString()
                params["location"] = location
                params["batch"] = batch.toString()
                params["deadline"] = deadline.toString()
                params["note"] = note
                params["status"] = status
                return params
            }
        }
        val queue: RequestQueue = Volley.newRequestQueue(context)
        queue.add(strReq)
    }
}
