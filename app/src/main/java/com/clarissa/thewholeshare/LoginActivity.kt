package com.clarissa.thewholeshare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request.Method
import com.android.volley.toolbox.JsonObjectRequest
import com.clarissa.thewholeshare.api.WholeShareApiService
import com.clarissa.thewholeshare.models.User
import com.google.gson.Gson
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
    lateinit var btnLogin: Button
    lateinit var btnBack : Button

    //isian :
    lateinit var etUsername : EditText
    lateinit var etPassword : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        btnLogin = findViewById(R.id.btnLogin)
        btnBack = findViewById(R.id.btnBack_Login)
        etUsername = findViewById(R.id.etUsername_Login)
        etPassword = findViewById(R.id.etPassword_Login)

        // When the login button is pressed,
        btnLogin.setOnClickListener {
            // Validate that the input fields are not empty
            if (etUsername.text.toString() == "" || etPassword.toString() == "") {
                alertDialogFailed("ERROR","Fill all the fields!")
            }

            val username = etUsername.text.toString()
            val password = etPassword.text.toString()

            attemptLogin(username, password)
        }

        btnBack.setOnClickListener {
            finish()
        }
    }

    /**
     * Attempt to log in to the application using the provided username and password.
     * Uses the Volley library to communicate with the server implementing a REST API,
     * returning a response whether the login attempt is successful or not.
     *
     * @param username The username of the user being attempted.
     * @param password The password of the user being attempted.
     */
    private fun attemptLogin(username: String, password: String) {
        // Create the request body for the request
        val requestBody = JSONObject()
        requestBody.put("username", username)
        requestBody.put("password", password)

        // Create the actual request object
        val loginRequest = JsonObjectRequest(Method.POST, "${WholeShareApiService.WS_HOST}/login",
            requestBody,
            { response ->
                val status = response.getInt("status")

                // Check the login attempt status, has it failed?
                // 0 = Failed, 1 = Success
                if (status == 0) {
                    val message = response.getString("reason")

                    alertDialogFailed("Login Failed!", message)
                }
                else if (status == 1) {
                    val userJson = response.getJSONObject("user")
                    val loggedUser = Gson().fromJson(userJson.toString(), User::class.java)
//                    alertDialogSuccess("Login Successful!", loggedUser.toString())

                    // Check for the logged in user role, and logged the user in to the appropriate page
                    // 1 = User, 2 = Admin, 3 = Courier
                    if (loggedUser.role == 1) {
                        // user
                        val intent = Intent(this@LoginActivity, UserMainActivity::class.java)
                        intent.putExtra("active_username",username)
                        intent.putExtra("active_id",loggedUser.id)
                        startActivity(intent)
                    } else if (loggedUser.role == 2) {
                        //admin
                        val intent = Intent(this@LoginActivity, AdminMainActivity::class.java)
                        intent.putExtra("active_username",username)
                        startActivity(intent)
                    } else if (loggedUser.role == 3) {
                        //driver
                        val intent = Intent(this@LoginActivity, DriverMainActivity::class.java)
                        intent.putExtra("active_username",username)
                        startActivity(intent)
                    }
                    etPassword.text.clear()
                    etUsername.text.clear()
                }
                else alertDialogFailed("Login Failed!", "Unknown Status")
            },
            { error ->
                alertDialogFailed("Login Failed!", error.toString())
            }
        )
        // Setting the retry connection policy before concluding a timeout event for the request
        loginRequest.retryPolicy =
            DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

        // Add the request object to the service queue
        WholeShareApiService.getInstance(this).addToRequestQueue(loginRequest)
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