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
import com.google.gson.Gson
import org.json.JSONArray
import java.sql.Timestamp
import java.util.*


class UserDonateFragment(
    var username:String,
    var arrParticipants : MutableList<Participant>,
    var arrRequests : MutableList<Request>,
    var arrUsers : MutableList<User>
) : Fragment() {

    lateinit var spinnerLocation:Spinner
    lateinit var etPickUpAddress:EditText
    lateinit var btnDonate:Button
    lateinit var tvBatch:TextView
    lateinit var etNotes:EditText

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
        etNotes = view.findViewById(R.id.etNotes_donate)

        if(listLocations.size>0) {
            tvBatch.text = "Batch : " + arrRequests[selectedPosition].batch.toString()
        }else{
            tvBatch.text = "Batch : -"
        }

        spinnerAdapter = ArrayAdapter(view.context,android.R.layout.simple_spinner_item,listLocations)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
        spinnerLocation.adapter = spinnerAdapter

        getUserLoggedIn()
        fetchParticipants()
        fetchRequestsDonatePage()

        for(i in arrRequests.indices){
                listLocations.add(arrRequests[i].location)
                arrIdRequests.add(arrRequests[i].id)
                spinnerAdapter.notifyDataSetChanged()
        }


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
        fun doInsertParticipant(pickup_address:String,notes:String){
            val strReq = object : StringRequest(
                Method.POST,
                "${WholeShareApiService.WS_HOST}/insertParticipant",
                Response.Listener {
                    spinnerAdapter.clear()
                    fetchParticipants()
                    fetchRequestsDonatePage()
                    clearAllFields()
                    onClickButton?.invoke("donate")
                    alertDialogSuccess("SUCCESS", "Donate request sent!")
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
                    params["note"] = notes
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
                val notes = etNotes.text.toString()

                if(loc!="" && pickup_address!=""&&notes!=""){
                    if(isAlreadyParticipated(arrIdRequests[selectedPosition])==false) {
                        doInsertParticipant(pickup_address,notes)
                    }else{
                        alertDialogFailed("ERROR", "You have already participated!")
                    }
                }else{
                    alertDialogFailed("ERROR","Fill all the fields!")
                }
            }else{
                alertDialogFailed("ERROR","There's no location available!")
            }
        }

    }


    fun clearAllFields(){
        etPickUpAddress.text.clear()
    }

    //to get user who logged in
    fun getUserLoggedIn(){
       for(i in arrUsers.indices){
           if(arrUsers[i].username==username){
               userActive = arrUsers[i]
               break
           }
       }
    }


    fun fetchRequestsDonatePage(){
        val strReq = object: StringRequest(
            Method.GET,
            "${WholeShareApiService.WS_HOST}/listLocationsUser",
            Response.Listener {
                val obj: JSONArray = JSONArray(it)
                arrRequests.clear()
                spinnerAdapter.notifyDataSetChanged()
                for (i in 0 until obj.length()){
                    var ada = -1
                    val o = obj.getJSONObject(i)
                    val id = o.getInt("id")
                    val location = o.getString("location")
                    val batch = o.getInt("batch")
                    val deadline = o.get("deadline").toString()
                    val note = o.getString("note")
                    val status = o.getString("status")
                    val created_at = o.get("created_at").toString()
                    val updated_at = o.get("updated_at").toString()
                    val deleted_at = o.get("deleted_at").toString()

                    val req = Request(
                        id,location,batch,deadline,note,status,created_at,updated_at,deleted_at
                    )

                    for(j in arrParticipants.indices){
                        if(arrParticipants[j].request_id==req.id){
                            ada = j
                        }
                    }

                    if(ada==-1){
                        arrRequests.add(req)
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
                for (i in 0 until obj.length()){
                    val o = obj.getJSONObject(i)
//                    val id = o.getInt("id")
//                    val user_id = o.getInt("user_id")
//                    val request_id = o.getInt("request_id")
//                    val courier_id = o.getInt("courier_id")
//                    val pickup = o.getString("pickup")
//                    val note = o.getString("note")
//                    val status = o.getInt("status")
//                    val created_at = o.get("created_at").toString()
//                    val updated_at = o.get("updated_at").toString()
//
//                    val participant = Participant(
//                        id,user_id,request_id,courier_id, pickup,note,status,created_at,updated_at
//                    )

                    val participant = Gson().fromJson(o.toString(), Participant::class.java)

                    if(participant.user_id == userActive.id){
                        arrParticipants.add(participant)
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

    fun isAlreadyParticipated(id_request:Int):Boolean{
        var isParticipated = false

        for(i in arrParticipants.indices){
            if(arrParticipants[i].request_id==id_request && arrParticipants[i].user_id==userActive.id){
                isParticipated = true
                break
            }
        }

        return isParticipated
    }


}