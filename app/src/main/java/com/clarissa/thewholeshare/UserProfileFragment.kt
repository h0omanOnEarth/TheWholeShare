package com.clarissa.thewholeshare

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

    //web service :
    val WS_HOST = "http://10.0.2.2:8000/api"

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

    }

    //to get user who logged in
    fun getUserLoggedIn(uname:String){
        val strReq = object: StringRequest(
            Method.GET,
            "$WS_HOST/listUsers",
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