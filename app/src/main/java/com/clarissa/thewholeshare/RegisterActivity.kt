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
import org.json.JSONArray


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

    //mutable list untuk memasukkan daftar users
    lateinit var arrUsers : MutableList<User>

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
        arrUsers = mutableListOf()

        refreshList()


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

                    if(checkUsername(username)==true){
                        alertDialogFailed("ERROR","Username has been taken!")
                    }else{
                        //username belum terdaftar
                        doRegister(username,password,full_name,phone,address,email,role_int)
                    }

                }else{
                  alertDialogFailed("ERROR","Password and Confirmation Password didn't match!")
                }
            }else{
               alertDialogFailed("ERROR","Fill all the fields!")
            }

        }

        btnBack.setOnClickListener {
            finish()
        }

    }

    fun doRegister(username:String, password:String, full_name:String, phone:String, address:String, email:String, role_int:Int){
        val strReq = object : StringRequest(
            Method.POST,
            "$WS_HOST/register",
            Response.Listener {
                refreshList()
                alertDialogSuccess("SUCCESS", "Register Success!")
                clearAllFileds()
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
    }

    //fungsi untuk cek username
    fun checkUsername(uname:String):Boolean{
        var isAda = false

        for(i in arrUsers.indices){
            if(arrUsers[i].username==uname){
                isAda = true
                break
            }
        }

        return  isAda
    }

    //fungsi untuk refresh semua data ke dalam list
    fun refreshList(){
        val strReq = object:StringRequest(
            Method.GET,
            "$WS_HOST/listUsers",
            Response.Listener {
                val obj: JSONArray = JSONArray(it)
                arrUsers.clear()
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
                    arrUsers.add(u)

                }
                println(arrUsers.size)
            },
            Response.ErrorListener {
                Toast.makeText(this,"ERROR!",Toast.LENGTH_SHORT).show()
            }
        ){}
        val queue:RequestQueue = Volley.newRequestQueue(this)
        queue.add(strReq)
    }

    //alert dialog warning
    fun alertDialogFailed(title:String, message:String){
        val mAlertDialog = AlertDialog.Builder(this@RegisterActivity)
        mAlertDialog.setIcon(R.drawable.high_priority_80px) //set alertdialog icon
        mAlertDialog.setTitle(title) //set alertdialog title
        mAlertDialog.setMessage(message) //set alertdialog message
        mAlertDialog.setPositiveButton("OK") { dialog, id ->

        }
        mAlertDialog.show()
    }

    fun alertDialogSuccess(title:String, message:String){
        val mAlertDialog = AlertDialog.Builder(this@RegisterActivity)
        mAlertDialog.setIcon(R.drawable.ok_80px) //set alertdialog icon
        mAlertDialog.setTitle(title) //set alertdialog title
        mAlertDialog.setMessage(message) //set alertdialog message
        mAlertDialog.setPositiveButton("OK") { dialog, id ->

        }
        mAlertDialog.show()
    }

    //bersihkan semua inputan setelah register
    fun clearAllFileds(){
        etUsername.text.clear()
        etPassword.text.clear()
        etEmail.text.clear()
        etConfirm.text.clear()
        etPhone.text.clear()
        etAddress.text.clear()
        etFullName.text.clear()

        for(radio in radios){
            if(radio.isChecked){
                radio.isChecked = false
            }
        }

    }

}