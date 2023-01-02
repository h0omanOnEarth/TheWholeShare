package com.clarissa.thewholeshare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText

class LoginActivity : AppCompatActivity() {
    lateinit var btnLogin:Button
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

        btnLogin.setOnClickListener {
            //untuk test :
            val intent = Intent(this@LoginActivity, DriverMainActivity::class.java)
            startActivity(intent)
        }

        btnBack.setOnClickListener {
            finish()
        }
    }
}