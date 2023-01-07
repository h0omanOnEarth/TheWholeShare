package com.clarissa.thewholeshare.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.clarissa.thewholeshare.R
import com.clarissa.thewholeshare.api.WholeShareApiService
import com.clarissa.thewholeshare.models.*
import org.json.JSONArray
import java.sql.Timestamp
import java.util.*


class UserDonateFragment(
    var username:String,
    var arrParticipants : MutableList<Participant>,
    var arrRequests : MutableList<Request>,
    var arrNews : MutableList<News>
) : Fragment() {

    lateinit var spinnerLocation:Spinner
    lateinit var etPickUpAddress:EditText
    lateinit var btnDonate:Button
    lateinit var tvBatch:TextView

    lateinit var spinnerAdapter: ArrayAdapter<String>
    lateinit var listLocations:MutableList<String>
    lateinit var arrIdRequests : MutableList<Int>


    var selectedPosition:Int = -1
    lateinit var userActive : User

    var onClickButton:((resource:String)->Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        listLocations = mutableListOf()
        arrIdRequests = mutableListOf()
        selectedPosition = 0
        userActive = User(-1,"","","","","","",-1,"")
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
        btnDonate = view.findViewById(R.id.btnDonate)
        tvBatch = view.findViewById(R.id.tvBatch)


        if(listLocations.size>0) {
            tvBatch.text = "Batch : " + arrRequests[selectedPosition].batch.toString()
        }else{
            tvBatch.text = "Batch : -"
        }

        spinnerAdapter = ArrayAdapter(view.context,android.R.layout.simple_spinner_item,listLocations)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
        spinnerLocation.adapter = spinnerAdapter

        getUserLoggedIn(username)
        fetchRequests()

        spinnerLocation.onItemSelectedListener = object  :
            AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    selectedPosition = p2
                    tvBatch.text = "Batch : "+arrRequests[selectedPosition].batch.toString()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        //alert dialog warning
        fun alertDialogFailed(title:String, message:String){
            val mAlertDialog = AlertDialog.Builder(view.context)
            mAlertDialog.setIcon(R.drawable.high_priority_80px) //set alertdialog icon
            mAlertDialog.setTitle(title) //set alertdialog title
            mAlertDialog.setMessage(message) //set alertdialog message
            mAlertDialog.setPositiveButton("OK") { dialog, id ->

            }
            mAlertDialog.show()
        }

        //alert dialog sukses
        fun alertDialogSuccess(title:String, message:String){
            val mAlertDialog = AlertDialog.Builder(view.context)
            mAlertDialog.setIcon(R.drawable.ok_80px) //set alertdialog icon
            mAlertDialog.setTitle(title) //set alertdialog title
            mAlertDialog.setMessage(message) //set alertdialog message
            mAlertDialog.setPositiveButton("OK") { dialog, id ->

            }
            mAlertDialog.show()
        }

        //do insert data
        fun doInsertParticipant(pickup_address:String){
            val strReq = object : StringRequest(
                Method.POST,
                "${WholeShareApiService.WS_HOST}/insertParticipant",
                Response.Listener {
                    alertDialogSuccess("SUCCESS", "Donate request sent!")
                    spinnerAdapter.clear()
                    fetchParticipants()
                    fetchRequests()
                    clearAllFields()
                    onClickButton?.invoke("donate")
                },
                Response.ErrorListener {
                    alertDialogFailed("ERROR",it.message.toString())
                }
            ){
                override fun getParams(): MutableMap<String, String>? {
                    val params = HashMap<String,String>()
                    params["user_id"] = userActive.id.toString()
                    params["request_id"] = arrIdRequests[selectedPosition].toString()
                    params["pickup"] = pickup_address
                    params["status"] = "0"
                    return params

                }
            }
            val queue: RequestQueue = Volley.newRequestQueue(context)
            queue.add(strReq)
        }

        btnDonate.setOnClickListener {
            if(listLocations.size>0){
                var loc = ""
                if(listLocations.size>0) {
                    loc = spinnerLocation.selectedItem.toString()
                }
                val pickup_address = etPickUpAddress.text.toString()

                if(loc!="" && pickup_address!=""){
                    if(isAlreadyParticipated(arrIdRequests[selectedPosition])){
                        alertDialogFailed("ERROR","You have already participated!")
                    }else {
                        doInsertParticipant(pickup_address)
                    }
                }else{
                    alertDialogFailed("ERROR","Fill all the fields!")
                }
            }else{
                alertDialogFailed("ERROR","There's no location available!")
            }
        }

    }

    //check participated or not yet
    fun isAlreadyParticipated(id_request:Int):Boolean{
        var isParticipated = false

        for(i in arrParticipants.indices){
            if(arrParticipants[i].request_id==id_request){
                isParticipated = true
                break
            }
        }

        return isParticipated
    }


    fun clearAllFields(){
        etPickUpAddress.text.clear()
    }

    //to get user who logged in
    fun getUserLoggedIn(uname:String){
        val strReq = object: StringRequest(
            Method.GET,
            "${WholeShareApiService.WS_HOST}/listUsers",
            Response.Listener {
                val obj: JSONArray = JSONArray(it)
                println(obj.length())
                for (i in 0 until obj.length()){
                    val o = obj.getJSONObject(i)
                    println(o)
                    val id = o.getInt("id")
                    val username = o.getString("username")
                    val password = o.getString("password")
                    val full_name = o.getString("full_name")
                    val phone = o.getString("phone")
                    val address = o.getString("address")
                    val email = o.getString("email")
                    val role = o.getInt("role")
                    val deleted_at = o.get("deleted_at").toString()
                    val u = User(
                        id,username,password,full_name,phone,address,email,role,deleted_at
                    )
                    if(username==uname) {
                        userActive = u
                        break
                    }
                }
                println(userActive)
            },
            Response.ErrorListener {
                Toast.makeText(context,"ERROR!", Toast.LENGTH_SHORT).show()
            }
        ){}
        val queue: RequestQueue = Volley.newRequestQueue(context)
        queue.add(strReq)
    }

    //fetch data requests
    fun fetchRequests(){
        val strReq = object: StringRequest(
            Method.GET,
            "${WholeShareApiService.WS_HOST}/listLocationsUser",
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

                    if(isAlreadyParticipated(req.id)==false) {
                        arrRequests.add(req)
                        listLocations.add(req.location)
                        arrIdRequests.add(req.id)
                        spinnerAdapter.notifyDataSetChanged()
                    }
                }
            },
            Response.ErrorListener {
                Toast.makeText(context,"ERROR!", Toast.LENGTH_SHORT).show()
            }
        ){}
        val queue: RequestQueue = Volley.newRequestQueue(context)
        queue.add(strReq)
    }

    //fetch data participants
    fun fetchParticipants(){
        val strReq = object: StringRequest(
            Method.GET,
            "${WholeShareApiService.WS_HOST}/listParticipants",
            Response.Listener {
                val obj: JSONArray = JSONArray(it)
                arrParticipants.clear()
                println(obj.length())
                for (i in 0 until obj.length()){
                    val o = obj.getJSONObject(i)
                    println(o)
                    val id = o.getInt("id")
                    val user_id = o.getInt("user_id")
                    val request_id = o.getInt("request_id")
                    val pickup = o.getString("pickup")
                    val status = o.getInt("status")
                    val created_at = o.get("created_at").toString()
                    val updated_at = o.get("updated_at").toString()

                    val participant = Participant(
                        id,user_id,request_id,pickup,status,created_at,updated_at
                    )

                    if(user_id==userActive.id){
                        arrParticipants.add(participant)
                    }
                }
            },
            Response.ErrorListener {
                Toast.makeText(context,"ERROR!", Toast.LENGTH_SHORT).show()
            }
        ){}
        val queue: RequestQueue = Volley.newRequestQueue(context)
        queue.add(strReq)
    }

}