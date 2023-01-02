package com.clarissa.thewholeshare

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley


class RegisterActivity : AppCompatActivity() {

    lateinit var btnRegister : Button
    lateinit var btnBack : Button

    //isian
    lateinit var etUsername : EditText
    lateinit var etPassword : EditText
    lateinit var etConfirm : EditText
    lateinit var etFullName : EditText
    lateinit var etAddress :EditText
    lateinit var etPhone : EditText
    lateinit var etEmail : EditText

    //radio buttons :
    lateinit var rbUser : RadioButton
    lateinit var rbAdmin : RadioButton
    lateinit var rbDriver : RadioButton

    lateinit var radios: Array<RadioButton>

    //web service :
    val WS_HOST = "http://10.0.2.2:8000/api"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        btnRegister = findViewById(R.id.btnRegister)
        btnBack = findViewById(R.id.btnBackRegister)
        etUsername = findViewById(R.id.etUsername_Register)
        etPassword = findViewById(R.id.etPassword_Register)
        etConfirm = findViewById(R.id.etConfirmPassword_Register)
        etFullName = findViewById(R.id.etFullName_Register)
        etAddress = findViewById(R.id.etAddress_Register)
        etPhone = findViewById(R.id.etPhone_Register)
        etEmail = findViewById(R.id.etEmail_Register)
        rbUser = findViewById(R.id.rbUser_Register)
        rbAdmin = findViewById(R.id.rbAdmin_Register)
        rbDriver = findViewById(R.id.rbDriver_Register)
        radios = arrayOf(rbUser, rbAdmin, rbDriver)

        btnRegister.setOnClickListener {
            val username  = etUsername.text.toString()
            val password = etPassword.text.toString()
            val confirm = etConfirm.text.toString()
            val full_name = etFullName.text.toString()
            val address = etAddress.text.toString()
            val phone = etPhone.text.toString()
            val email = etEmail.text.toString()
            var role = ""

            for(radio in radios){
                if(radio.isChecked) role = radio.text.toString()
            }

            if(username!="" && password!="" && confirm!="" && full_name!="" && address!="" && phone!="" && email!="" && role!=""){
                //insert into database

                var role_int = -1
                if(role=="Admin"){
                    role_int = 2
                }else if(role=="User"){
                    role_int = 1
                }else if(role=="Driver"){
                    role_int = 3
                }

                if(password==confirm){
                    val strReq = object : StringRequest(
                        Method.POST,
                        "$WS_HOST/register",
                        Response.Listener {
                            val mAlertDialog = AlertDialog.Builder(this@RegisterActivity)
                            mAlertDialog.setIcon(R.drawable.ok_80px) //set alertdialog icon
                            mAlertDialog.setTitle("SUCCESS") //set alertdialog title
                            mAlertDialog.setMessage("Register Success!") //set alertdialog message
                            mAlertDialog.setPositiveButton("OK") { dialog, id ->

                            }
                            mAlertDialog.show()
                        },
                        Response.ErrorListener {
                            println(it.message)
                            Toast.makeText(this,it.message, Toast.LENGTH_SHORT).show()
                        }
                    ){
                        override fun getParams(): MutableMap<String, String>? {
                            val params = HashMap<String,String>()
                            params["username"] = username
                            params["password"] = password
                            params["full_name"] = full_name
                            params["phone"] = phone
                            params["address"] = address
                            params["email"] = email
                            params["role"] = role_int.toString()
                            return params

                        }
                    }
                    val queue: RequestQueue = Volley.newRequestQueue(this)
                    queue.add(strReq)
                }else{
                    val mAlertDialog = AlertDialog.Builder(this@RegisterActivity)
                    mAlertDialog.setIcon(R.drawable.high_priority_80px) //set alertdialog icon
                    mAlertDialog.setTitle("WARNING") //set alertdialog title
                    mAlertDialog.setMessage("Password and Confirmation Password didn't match!") //set alertdialog message
                    mAlertDialog.setPositiveButton("OK") { dialog, id ->

                    }
                    mAlertDialog.show()
                }
            }else{
                val mAlertDialog = AlertDialog.Builder(this@RegisterActivity)
                mAlertDialog.setIcon(R.drawable.high_priority_80px) //set alertdialog icon
                mAlertDialog.setTitle("WARNING") //set alertdialog title
                mAlertDialog.setMessage("Fill All The Fields!") //set alertdialog message
                mAlertDialog.setPositiveButton("OK") { dialog, id ->

                }
                mAlertDialog.show()
            }

        }

        btnBack.setOnClickListener {
            finish()
        }

    }
}