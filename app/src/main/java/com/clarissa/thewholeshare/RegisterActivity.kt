package com.clarissa.thewholeshare

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import androidx.appcompat.app.AlertDialog
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.clarissa.thewholeshare.api.WholeShareApiService
import com.clarissa.thewholeshare.models.User
import com.google.gson.Gson
import org.json.JSONObject

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
                    doRegister(username,password,full_name,phone,address,email,role_int)
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

    /**
     * Registers a new user to the server.
     *
     * @param username  The username of the account.
     * @param password  The password of the account.
     * @param full_name The full name of the account.
     * @param phone     The phone number of the user.
     * @param address   The address of the user.
     * @param email     The email of the user.
     * @param role_int  The account privilage.
     */
    fun doRegister(username:String, password:String, full_name:String, phone:String, address:String, email:String, role_int:Int) {
        // Create the request body
        val requestBody = JSONObject()
        requestBody.put("username", username)
        requestBody.put("password", password)
        requestBody.put("full_name", full_name)
        requestBody.put("phone", phone)
        requestBody.put("address", address)
        requestBody.put("email", email)
        requestBody.put("role", role_int.toString())

        // Create the actual register request object with the request body
        val registerRequest = JsonObjectRequest(Request.Method.POST, "${WholeShareApiService.WS_HOST}/register", requestBody,
            { response ->
                // Retrieve the response status on the operation
                val status = response.getInt("status")

                // Notify the user if the register attempt failed
                if (status == 0) {
                    val reason = response.getString("reason")
                    alertDialogFailed("Register failed!", reason)
                }
                // Notify the user if the register attempt succeeded
                else if (status == 1) {
                    val userJson = response.getJSONObject("user")
                    val registeredUser = Gson().fromJson(userJson.toString(), User::class.java)

                    alertDialogSuccess("Register Successful!", "User ${username} has successfully been registered!")
                    clearAllFileds()
                }
                else alertDialogFailed("Unknown Status", "Unknown status code")
            },
            { error ->
                alertDialogFailed("Register failed!", error.toString())
            })
        // Set the timeout retry policy
        registerRequest.retryPolicy =
            DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

        // Queue the request to the service
        WholeShareApiService.getInstance(this).addToRequestQueue(registerRequest)
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

    //alert dialog sukses
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