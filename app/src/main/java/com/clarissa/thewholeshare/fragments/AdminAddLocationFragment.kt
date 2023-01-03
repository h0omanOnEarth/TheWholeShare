package com.clarissa.thewholeshare.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.clarissa.thewholeshare.R

class AdminAddLocationFragment : Fragment() {

    //lateinit var for components
    lateinit var etAddress : EditText
    lateinit var etNote : EditText
    lateinit var btnAdd : Button
    lateinit var btnCancel : Button

    //web service :
    val WS_HOST = "http://10.0.2.2:8000/api"

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
        btnAdd = view.findViewById(R.id.btnAdd_addLocation)
        btnCancel = view.findViewById(R.id.btnCancel_addLocation)

        btnCancel.setOnClickListener {
            onClickButton?.invoke("cancel")
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

        fun clearAllFields(){
            etNote.text.clear()
            etAddress.text.clear()
        }



        btnAdd.setOnClickListener {
            val address = etAddress.text.toString()
            val note = etNote.text.toString()

            if(address!="" && note!=""){
                //do insert

            }else{
                alertDialogFailed("ERROR", "Fill all the fields!")
            }

        }

    }




}