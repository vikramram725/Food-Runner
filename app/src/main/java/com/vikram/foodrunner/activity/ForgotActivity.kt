package com.vikram.foodrunner.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.vikram.foodrunner.R

class ForgotActivity : AppCompatActivity() {
    lateinit var toolbar: Toolbar
    lateinit var txtMobileF: EditText
    lateinit var txtEmailF: EditText
    lateinit var btnNextF: Button
    lateinit var sharedPreferences: SharedPreferences
    var mobile: String? = ""
    var mail: String? = ""

    var validmobile = mutableListOf<String>()
    var validmail = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot)

        txtMobileF = findViewById(R.id.txtMobileF)
        txtEmailF = findViewById(R.id.txtEmailF)
        btnNextF = findViewById(R.id.btnNext)
        toolbar = findViewById(R.id.toolBarF)

        setUpToolbar()

        sharedPreferences = getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)

            mail = sharedPreferences.getString("mail","")
            val mailInt = mail.toString()
            validmail.add(mailInt)
            mobile = sharedPreferences.getString("mobile","")
            val mobileInt = mobile.toString()
            validmobile.add(mobileInt)

        btnNextF.setOnClickListener {

            val mobileF = txtMobileF.text.toString()
            val mailF = txtEmailF.text.toString()

            if(mobileF != "" && mailF != "") {
                if (validmobile.contains(mobileF) && validmail.contains(mailF)) {
                    sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()
                    val intent = Intent(this@ForgotActivity, LoginActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this@ForgotActivity, "Wrong Credentials", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                Toast.makeText(this@ForgotActivity, "Fill Credentials", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
    override fun onPause() {
        super.onPause()
        finish()
    }
    fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Forgot Password"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}