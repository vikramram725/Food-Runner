package com.vikram.foodrunner.activity

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.vikram.foodrunner.R
import com.vikram.foodrunner.adapter.CartAdapter
import com.vikram.foodrunner.database.order.FoodDatabase
import com.vikram.foodrunner.database.order.FoodEntity

class CartActivity : AppCompatActivity() {

    lateinit var recyclerFoodOrder: RecyclerView
    lateinit var recyclerAdapter: CartAdapter
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var toolbar: Toolbar
    lateinit var txtHotelName: TextView
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var btnOrder: Button
    var sum = 0
    lateinit var foodListEnt: List<FoodEntity>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        toolbar = findViewById(R.id.toolbarCart)
        txtHotelName = findViewById(R.id.txtHotelName)
        progressLayout = findViewById(R.id.progressLayoutC)
        progressBar = findViewById(R.id.progressBarC)
        recyclerFoodOrder = findViewById(R.id.recyclerFoodOrder)

        btnOrder = findViewById(R.id.btnOrder)
        progressLayout.visibility = View.VISIBLE
        layoutManager = LinearLayoutManager(this@CartActivity)

        setUpToolbar()

        if(intent != null){
            txtHotelName.text = intent.getStringExtra("hotel")
        } else {
            txtHotelName.text = "Foodie"
        }
        val async = GetAllFoodAdded(applicationContext).execute()
        val result = async.get()
        val checkEmpty: List<FoodEntity>? = result
        val size: Int? = checkEmpty?.size
        if(size != 0 ){
            progressLayout.visibility = View.GONE
            foodListEnt = result
            for (i in 0 until size!!) {
                val info = result[i]
                sum += info.cost.toInt()
            }
            recyclerAdapter = CartAdapter(this@CartActivity,foodListEnt)
            recyclerFoodOrder.adapter = recyclerAdapter
            recyclerFoodOrder.layoutManager = layoutManager
        }
        btnOrder.text = "Place The Order( Rs.$sum )"
        btnOrder.setOnClickListener {
            val intent = Intent(this@CartActivity,SuccessActivity::class.java)
            startActivity(intent)
        }
    }
    class GetAllFoodAdded(val context: Context): AsyncTask<Void,Void,List<FoodEntity>>(){
        override fun doInBackground(vararg p0: Void?): List<FoodEntity> {
            val db = Room.databaseBuilder(context,FoodDatabase::class.java,"food-db").build()
            return db.foodDao().getAllFood()
        }
    }
    fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "My Cart"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onPause() {
        super.onPause()
        finish()
    }
}