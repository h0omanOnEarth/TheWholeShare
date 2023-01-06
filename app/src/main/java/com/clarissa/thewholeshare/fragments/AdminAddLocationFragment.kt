package com.clarissa.thewholeshare.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.clarissa.thewholeshare.AdminMainActivity
import com.clarissa.thewholeshare.MainActivity
import com.clarissa.thewholeshare.R
import com.clarissa.thewholeshare.api.WholeShareApiService
import com.clarissa.thewholeshare.models.Location
import com.clarissa.thewholeshare.models.User
import com.google.gson.Gson
import org.json.JSONObject

class AdminAddLocationFragment : Fragment() {

    //lateinit var for components
    lateinit var etAddress : EditText
    lateinit var etNote : EditText
    lateinit var etDeadline : EditText
    lateinit var btnAdd : Button
    lateinit var btnCancel : Button

//    //web service :
//    val WS_HOST = "http://10.0.2.2:8000/api"

    //for clicking buttons
    var onClickButton:((resource:String)->Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_add_location, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etAddress = view.findViewById(R.id.etAddress_addLocation)
        etNote = view.findViewById(R.id.etNote_addLocation)
        etDeadline = view.findViewById(R.id.etDeadline_addLocation)
        btnAdd = view.findViewById(R.id.btnAdd_addLocation)
        btnCancel = view.findViewById(R.id.btnCancel_addLocation)

        btnCancel.setOnClickListener {
            onClickButton?.invoke("cancel")
        }




        btnAdd.setOnClickListener {
            val address = etAddress.text.toString()
            val note = etNote.text.toString()
            val deadline = etDeadline.text.toString()

            if(address!="" && note!="" && deadline!=""){
                doAddLocation(address,note,deadline)

            }else{
                alertDialogFailed("ERROR", "Fill all the fields!")
            }

        }



    }
    fun doAddLocation(address:String, note:String, deadline:String){
        val requestBody = JSONObject()
        requestBody.put("location",address)
        requestBody.put("batch",1)
        requestBody.put("deadline",deadline)
        requestBody.put("note",note)
        requestBody.put("status","menunggu")

        val addRequest = JsonObjectRequest(Request.Method.POST,"${ WholeShareApiService.WS_HOST}/addrequest",requestBody,
            {
                    response ->
                val status = response.getInt("status")
                println("baris 1");
                println(response);
                println("baris 2");
                println(status);
                // Notify the user if the register attempt failed
                if (status == 0) {
                    val reason = response.getString("reason")
                    alertDialogFailed("Register failed!", reason)
                }
                // Notify the user if the register attempt succeeded
                else if (status == 1) {
                    val userJson = response.getJSONObject("requestloc")
                    val registeredLocation = Gson().fromJson(userJson.toString(), Location::class.java)

                    alertDialogSuccess("Register Successful!", "Location at ${address} has successfully been registered!")
                    clearAllFields()
                }
                else alertDialogFailed("Unknown Status", "Unknown status code")
            },
            {
                    error -> alertDialogFailed("Add Location Failed", error.toString())
            })
        addRequest.retryPolicy =
            DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

        // Queue the request to the service
        WholeShareApiService.getInstance((context as AdminMainActivity)).addToRequestQueue(addRequest)
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
        etNote.text.clear()
        etAddress.text.clear()
        etDeadline.text.clear()
    }



}