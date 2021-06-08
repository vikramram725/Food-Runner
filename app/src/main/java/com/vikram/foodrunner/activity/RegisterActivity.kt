package com.vikram.foodrunner.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.vikram.foodrunner.R
import com.vikram.foodrunner.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

class RegisterActivity : AppCompatActivity() {
    lateinit var toolbar:Toolbar
    lateinit var txtName: EditText
    lateinit var txtMailR: EditText
    lateinit var txtMobileR: EditText
    lateinit var txtAddressR: EditText
    lateinit var txtPasswordR: EditText
    lateinit var txtConfirmR: EditText
    lateinit var btnRegisterR: Button

    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        sharedPreferences = getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)

        val isRegister = sharedPreferences.getBoolean("isRegister",false)

        if (isRegister){
            Toast.makeText( this@RegisterActivity,"Already Registered", Toast.LENGTH_SHORT).show()
            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        toolbar = findViewById(R.id.toolBarR)
        txtName = findViewById(R.id.txtNameR)
        txtMailR = findViewById(R.id.txtMailR)
        txtMobileR = findViewById(R.id.txtMobileR)
        txtAddressR = findViewById(R.id.txtAddress)
        txtPasswordR = findViewById(R.id.txtPasswordR)
        txtConfirmR = findViewById(R.id.txtConfirmR)
        btnRegisterR = findViewById(R.id.btnRegisterR)

        setUpToolbar()

        if(ConnectionManager().checkConnectivity(this@RegisterActivity)) {
            btnRegisterR.setOnClickListener {

                val name = txtName.text.toString()
                val mail = txtMailR.text.toString()
                val mobile = txtMobileR.text.toString()
                val address = txtAddressR.text.toString()
                val password = txtPasswordR.text.toString()
                val confirm = txtConfirmR.text.toString()

                if (name != "" && mail != "" && mobile != "" && address != "" && password != "" && confirm != "") {
                    if (password == confirm) {
                        sharedPreferences.edit().putString("name", name).apply()
                        sharedPreferences.edit().putString("mail", mail).apply()
                        sharedPreferences.edit().putString("mobile", mobile).apply()
                        sharedPreferences.edit().putString("address", address).apply()
                        sharedPreferences.edit().putBoolean("isRegister", true).apply()
                        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                        intent.putExtra("Mobile", mobile)
                        intent.putExtra("Password", password)
                        Toast.makeText(
                            this@RegisterActivity,
                            "Registered Successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        startActivity(intent)
                        finish()
                        val queue = Volley.newRequestQueue(this@RegisterActivity)
                        val url = "http://13.235.250.119/v2/register/fetch_result"
                        /*jsonCreate("name", "$name")
                        jsonCreate("mobile_number","$mobile")
                        jsonCreate("password","$confirm")
                        jsonCreate("address","$address")
                        jsonCreate("email","$mail")*/
                        val jsonParams = JSONObject("""{"name":"$name","mobile_number":"$mobile","password":"$confirm","address":"$address","email":"$mail"}""")
                        val jsonObjectRequest = object:JsonObjectRequest(Request.Method.POST,url,jsonParams,
                            Response.Listener {
                                try{
                                    val data = it.getJSONObject("data")
                                    val success = data.getBoolean("success")
                                    if(success){
                                        sharedPreferences.edit().putBoolean("isRegister", true).apply()
                                        Toast.makeText(
                                            this@RegisterActivity,
                                            "Registered Successfully",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        val userData = data.getJSONObject("data")
                                        val userId = userData.getString("user_id")
                                        sharedPreferences.edit().putString("name", name).apply()
                                        sharedPreferences.edit().putString("mail", mail).apply()
                                        sharedPreferences.edit().putString("mobile", mobile).apply()
                                        sharedPreferences.edit().putString("address", address).apply()
                                        sharedPreferences.edit().putString("user_id",userId).apply()
                                        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                                        intent.putExtra("Mobile", mobile)
                                        intent.putExtra("Password", password)
                                        startActivity(intent)
                                    } else {
                                        /*Toast.makeText(
                                            this@RegisterActivity,
                                            "Error occurred",
                                            Toast.LENGTH_SHORT
                                        ).show()*/
                                    }
                                }
                                catch (e: JSONException){
                                    Toast.makeText(
                                        this@RegisterActivity,
                                        "Exception occurred",
                                        Toast.LENGTH_SHORT
                                    )
                                }
                            },Response.ErrorListener { Toast.makeText(
                                this@RegisterActivity,
                                "Volley exception occurred",
                                Toast.LENGTH_SHORT
                            ) }) {
                            override fun getHeaders(): MutableMap<String, String> {
                                val headers = HashMap<String,String>()
                                headers["Content/Type"] = "appliction/json"
                                headers["Token"] = "b16072bf31c205"
                                return headers
                            }
                        }
                        queue.add(jsonObjectRequest)

                    }

                    else {
                        Toast.makeText(
                            this@RegisterActivity,
                            "Passwords mismatch",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Fill Credentials",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            val alert = AlertDialog.Builder(this)
            alert.setTitle("Error")
            alert.setMessage("Internet Connection not Found")
            alert.setPositiveButton("open settings") { _, _ ->
                val settingsOpen = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsOpen)
                this.finish()
            }
            alert.setNegativeButton("Exit") { _, _ ->
                ActivityCompat.finishAffinity(this)
            }
            alert.create()
            alert.show()
        }
    }
    override fun onPause() {
        super.onPause()
        finish()
    }
    fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Register Account"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
    /*fun jsonCreate(name: String, value: String): JSONObject {
        val rootObject = JSONObject()
        rootObject.put("$name", "$value")
        return rootObject
    }*/
}