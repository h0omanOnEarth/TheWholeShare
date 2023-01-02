package com.clarissa.thewholeshare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray

class LoginActivity : AppCompatActivity() {
    lateinit var btnLogin:Button
    lateinit var btnBack : Button

    //isian :
    lateinit var etUsername : EditText
    lateinit var etPassword : EditText

    //mutable list untuk memasukkan daftar users
    lateinit var arrUsers : MutableList<User>

    //web service :
    val WS_HOST = "http://10.0.2.2:8000/api"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        btnLogin = findViewById(R.id.btnLogin)
        btnBack = findViewById(R.id.btnBack_Login)
        etUsername = findViewById(R.id.etUsername_Login)
        etPassword = findViewById(R.id.etPassword_Login)
        arrUsers = mutableListOf()

        refreshList()

        btnLogin.setOnClickListener {
            val username = etUsername.text.toString()
            val password = etPassword.text.toString()

            if(username!="" && password!=""){
                //maka lakukan login
                if(isUsernameRegistered(username)==true){

                    if(isPasswordTrue(username,password)!=-1){
                        val role = isPasswordTrue(username,password)

                        if(role==1){
                            //user
                            val intent = Intent(this@LoginActivity, UserMainActivity::class.java)
                            intent.putExtra("active_username",username)
                            startActivity(intent)
                        }else if(role==2){
                            //admin
                            val intent = Intent(this@LoginActivity, AdminMainActivity::class.java)
                            intent.putExtra("active_username",username)
                            startActivity(intent)
                        }else if(role==3){
                            //driver
                            val intent = Intent(this@LoginActivity, DriverMainActivity::class.java)
                            intent.putExtra("active_username",username)
                            startActivity(intent)
                        }

                        etUsername.text.clear()
                        etPassword.text.clear()

                    }else{
                        alertDialogFailed("ERROR","Wrong password!")
                    }

                }else{
                    alertDialogFailed("ERROR","Username isn't registered!")
                }

            }else{
                alertDialogFailed("ERROR","Fill all the fields!")
            }
        }

        btnBack.setOnClickListener {
            finish()
        }
    }

    fun isPasswordTrue(uname: String, pass:String):Int{
       var theRole = -1

        for(i in arrUsers.indices){
            if(arrUsers[i].username==uname){
                if(arrUsers[i].password==pass){
                    theRole = arrUsers[i].role
                    break
                }
            }
        }

        return theRole
    }

    //fungsi untuk melakukan login
    fun isUsernameRegistered(uname:String):Boolean{
        var isAda = false
        for(i in arrUsers.indices){
            if(arrUsers[i].username==uname){
                isAda = true
                break
            }
        }
        return  isAda
    }

    //untuk refresh list atau memasukkan data ke dalam mutable list :
    fun refreshList(){
        val strReq = object: StringRequest(
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
                Toast.makeText(this,"ERROR!", Toast.LENGTH_SHORT).show()
            }
        ){}
        val queue: RequestQueue = Volley.newRequestQueue(this)
        queue.add(strReq)
    }

    //alert dialog warning
    fun alertDialogFailed(title:String, message:String){
        val mAlertDialog = AlertDialog.Builder(this@LoginActivity)
        mAlertDialog.setIcon(R.drawable.high_priority_80px) //set alertdialog icon
        mAlertDialog.setTitle(title) //set alertdialog title
        mAlertDialog.setMessage(message) //set alertdialog message
        mAlertDialog.setPositiveButton("OK") { dialog, id ->

        }
        mAlertDialog.show()
    }

    //alert dialog sukses
    fun alertDialogSuccess(title:String, message:String){
        val mAlertDialog = AlertDialog.Builder(this@LoginActivity)
        mAlertDialog.setIcon(R.drawable.ok_80px) //set alertdialog icon
        mAlertDialog.setTitle(title) //set alertdialog title
        mAlertDialog.setMessage(message) //set alertdialog message
        mAlertDialog.setPositiveButton("OK") { dialog, id ->

        }
        mAlertDialog.show()
    }

}