package com.vikram.foodrunner.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.vikram.foodrunner.R

class LoginActivity : AppCompatActivity() {
    lateinit var toolbar: Toolbar
    lateinit var mobileNumberL: EditText
    lateinit var passwordL: EditText
    private lateinit var btnlogin: Button
    lateinit var txtForgotL: TextView
    lateinit var txtRegisterL: TextView
    var mobileIn: String? = ""
    var mailIn: String? = ""
    var passwordIn: String? = ""

    var validmobile = mutableListOf<String>()
    var validpassword = mutableListOf<String>()

    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sharedPreferences = getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)

        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        if (isLoggedIn) {
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        mobileNumberL = findViewById(R.id.mobileNumberL)
        passwordL = findViewById(R.id.passwordL)
        btnlogin = findViewById(R.id.btnLoginL)
        txtForgotL = findViewById(R.id.txtForgotL)
        txtRegisterL = findViewById(R.id.txtSignUpL)
        toolbar = findViewById(R.id.toolBarL)
        setUpToolbar()

        if(intent != null ) {
            mobileIn = intent.getStringExtra("Mobile")
            val mobileInt = mobileIn.toString()
            validmobile.add(mobileInt)
            passwordIn = intent.getStringExtra("Password")
            val passwordInt = passwordIn.toString()
            validpassword.add(passwordInt)
        }
        btnlogin.setOnClickListener {

            val mobile = mobileNumberL.text.toString()
            val password = passwordL.text.toString()

            if(mobile != "" && password != "") {
                if ((validmobile.contains(mobile)) && (validpassword.contains(password))) {
                    Toast.makeText(this@LoginActivity, "LogIn Successful", Toast.LENGTH_SHORT)
                        .show()
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@LoginActivity, "Wrong Credentials", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                Toast.makeText(this@LoginActivity, "Fill Credentials", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        txtForgotL.setOnClickListener {
            val intent = Intent(this@LoginActivity, ForgotActivity::class.java)
            startActivity(intent)
        }
        txtRegisterL.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
    override fun onPause() {
        super.onPause()
        finish()
    }
    fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Login"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}