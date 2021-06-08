package com.vikram.foodrunner.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.vikram.foodrunner.R

class VerificationActivity : AppCompatActivity() {

    lateinit var toolbar: Toolbar
    lateinit var etOtp: EditText
    lateinit var etPasswordV: EditText
    lateinit var etConfirmPasswordV: EditText
    lateinit var btnReset: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verification)


        etOtp = findViewById(R.id.etOtp)
        etPasswordV = findViewById(R.id.etPasswordV)
        etConfirmPasswordV = findViewById(R.id.etConfirmPasswordV)
        btnReset = findViewById(R.id.btnReset)
        toolbar = findViewById(R.id.toolBarV)
        setUpToobar()

        btnReset.setOnClickListener {
            val otp = etOtp.text.toString()
            val password = etPasswordV.text.toString()
            val confirmPass = etConfirmPasswordV.text.toString()
            if (otp != "" && password != "" && confirmPass != "") {
                if (password == confirmPass) {

                } else {
                    Toast.makeText(
                        this@VerificationActivity,
                        "Password mismatch",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(this@VerificationActivity,"Fill the Credentials", Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun setUpToobar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "FOODIE"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}