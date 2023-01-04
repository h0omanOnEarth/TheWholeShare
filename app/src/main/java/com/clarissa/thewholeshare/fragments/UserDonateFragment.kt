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
import com.clarissa.thewholeshare.models.Location
import com.clarissa.thewholeshare.models.News
import com.clarissa.thewholeshare.models.Request
import com.clarissa.thewholeshare.models.User
import org.json.JSONArray
import java.sql.Timestamp
import java.util.*


class UserDonateFragment(
    var id_user:Int
) : Fragment() {

    lateinit var spinnerLocation:Spinner
    lateinit var etPickUpAddress:EditText
    lateinit var etNote_donate:EditText
    lateinit var btnDonate:Button

    lateinit var arrRequests : MutableList<Request>
    lateinit var spinnerAdapter: ArrayAdapter<String>
    lateinit var listLocations:MutableList<String>
    lateinit var arrIdRequests : MutableList<Int>

    var selectedPosition:Int = -1

    var onClickButton:((resource:String,news: News)->Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arrRequests=  mutableListOf()
        listLocations = mutableListOf()
        arrIdRequests = mutableListOf()
        selectedPosition = 0
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

        spinnerAdapter = ArrayAdapter(view.context,android.R.layout.simple_spinner_item,listLocations)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
        spinnerLocation.adapter = spinnerAdapter

        refreshList()

        spinnerLocation.onItemSelectedListener = object  :
            AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    selectedPosition = p2
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

        btnDonate.setOnClickListener {
            val loc = spinnerLocation.selectedItem.toString()
            val pickup_address = etPickUpAddress.text.toString()
            val note = etNote_donate.text.toString()

            if(loc!="" && pickup_address!="" && note!=""){

            }else{
                alertDialogFailed("ERROR","Fill all the fields!")
            }

        }

    }

    //do insert data


    //fetch data requests
    fun refreshList(){
        val strReq = object: StringRequest(
            Method.GET,
            "${WholeShareApiService.WS_HOST}/listRequest",
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
                    listLocations.add(req.location)
                    arrIdRequests.add(id)
                    spinnerAdapter.notifyDataSetChanged()

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