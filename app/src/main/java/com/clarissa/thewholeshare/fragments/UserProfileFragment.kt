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
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.clarissa.thewholeshare.R
import com.clarissa.thewholeshare.api.WholeShareApiService
import com.clarissa.thewholeshare.models.Participant
import com.clarissa.thewholeshare.models.Request
import com.clarissa.thewholeshare.models.User
import org.json.JSONArray

class UserProfileFragment(
    var unameActive : String
) : Fragment() {

    lateinit var etUsername : EditText
    lateinit var etFullName : EditText
    lateinit var etPhone : EditText
    lateinit var etAddress : EditText
    lateinit var etEmail : EditText
    lateinit var etPassword : EditText
    lateinit var btnEdit : Button

    lateinit var userActive : User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_profile, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        etUsername = view.findViewById(R.id.etUsername_Profile)
        etFullName = view.findViewById(R.id.etFullName_Profile)
        etPhone = view.findViewById(R.id.etPhoneNumber_Profile)
        etAddress = view.findViewById(R.id.etAddress_Profile)
        etEmail = view.findViewById(R.id.etEmail_Profile)
        etPassword = view.findViewById(R.id.etPassword_profile)
        btnEdit = view.findViewById(R.id.btnEdit_Profile)

        userActive = User(-1,"","","","","","",-1,"")

        getUserLoggedIn(unameActive)


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

        //do edit kalau ngelakuin edit password
        fun doEditProfile(full_name:String, phone:String, address:String, email:String, password:String){
            val strReq = object : StringRequest(
                Method.POST,
                "${WholeShareApiService.WS_HOST}/updateUser",
                Response.Listener {
                    getUserLoggedIn(unameActive)
                    alertDialogSuccess("SUCCESS","Success Edit Profile!")
                    etPassword.text.clear()
                },
                Response.ErrorListener {
                    println(it.message)
                    Toast.makeText(context,it.message, Toast.LENGTH_SHORT).show()
                }
            ){
                override fun getParams(): MutableMap<String, String>? {
                    val params = HashMap<String,String>()
                    params["id"] = userActive.id.toString()
                    params["username"] = unameActive
                    params["password"] = password
                    params["full_name"] = full_name
                    params["phone"] = phone
                    params["address"] = address
                    params["email"] = email
                    params["role"] = userActive.role.toString()
                    return params
                }
            }
            val queue: RequestQueue = Volley.newRequestQueue(context)
            queue.add(strReq)
        }

        fun doEditNoHash(full_name:String, phone:String, address:String, email:String){
            val strReq = object : StringRequest(
                Method.POST,
                "${WholeShareApiService.WS_HOST}/updateUserNoHash",
                Response.Listener {
                    getUserLoggedIn(unameActive)
                    alertDialogSuccess("SUCCESS","Success Edit Profile!")
                },
                Response.ErrorListener {
                    println(it.message)
                    Toast.makeText(context,it.message, Toast.LENGTH_SHORT).show()
                }
            ){
                override fun getParams(): MutableMap<String, String>? {
                    val params = HashMap<String,String>()
                    params["id"] = userActive.id.toString()
                    params["username"] = unameActive
                    params["full_name"] = full_name
                    params["phone"] = phone
                    params["address"] = address
                    params["email"] = email
                    params["role"] = userActive.role.toString()
                    return params
                }
            }
            val queue: RequestQueue = Volley.newRequestQueue(context)
            queue.add(strReq)
        }


        btnEdit.setOnClickListener {
            val full_name = etFullName.text.toString()
            val phone = etPhone.text.toString()
            val address = etAddress.text.toString()
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()

            if(full_name!="" &&phone!="" && address!="" && email!=""){
                //do edit
                if(password!="") {
                    doEditProfile(full_name, phone, address, email, password)
                }else{
                    doEditNoHash(full_name,phone,address,email)
                }
            }else{
                alertDialogFailed("ERROR","Fill all the fIelds!")
            }

        }
    }

    //to get user who logged in
    fun getUserLoggedIn(uname:String){
        val strReq = object: StringRequest(
            Method.GET,
            "${WholeShareApiService.WS_HOST}/listUsers",
            Response.Listener {
                val obj: JSONArray = JSONArray(it)
                for (i in 0 until obj.length()){
                    val o = obj.getJSONObject(i)
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
                loadFills()
            },
            Response.ErrorListener {
                Toast.makeText(context,"ERROR!", Toast.LENGTH_SHORT).show()
            }
        ){}
        val queue: RequestQueue = Volley.newRequestQueue(context)
        queue.add(strReq)
    }

    fun loadFills(){
        etUsername.setText(userActive.username)
        etFullName.setText(userActive.full_name)
        etPhone.setText(userActive.phone)
        etAddress.setText(userActive.address)
        etEmail.setText(userActive.email)
    }




}