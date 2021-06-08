package com.vikram.foodrunner.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.vikram.foodrunner.R


class MyProfileFragment : Fragment() {
    lateinit var txtName: TextView
    lateinit var txtmail: TextView
    lateinit var txtAddress: TextView
    lateinit var txtMobile: TextView
    var sharedPreferences: SharedPreferences? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_my_profile, container, false)

        txtName = view.findViewById(R.id.txtNameP)
        txtmail = view.findViewById(R.id.txtMailP)
        txtAddress = view.findViewById(R.id.txtAddressP)
        txtMobile = view.findViewById(R.id.txtMobileP)
        sharedPreferences = activity?.getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)!!

        val mail = sharedPreferences?.getString("mail","Email")
        val mobile = sharedPreferences?.getString("mobile","Mobile number")
        val name = sharedPreferences?.getString("name","Name")
        val address = sharedPreferences?.getString("address","Address")

        txtName.text = name.toString()
        txtMobile.text = mobile.toString()
        txtAddress.text = address.toString()
        txtmail.text = mail.toString()

        return view
    }
}