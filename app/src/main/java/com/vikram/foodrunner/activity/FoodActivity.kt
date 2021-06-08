package com.vikram.foodrunner.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.vikram.foodrunner.R
import com.vikram.foodrunner.adapter.CartAdapter
import com.vikram.foodrunner.adapter.FoodAdapter
import com.vikram.foodrunner.database.order.FoodDatabase
import com.vikram.foodrunner.database.order.FoodEntity
import com.vikram.foodrunner.database.rest.RestDatabase
import com.vikram.foodrunner.database.rest.RestEntity
import com.vikram.foodrunner.model.Food
import com.vikram.foodrunner.util.ConnectionManager
import org.json.JSONException

class FoodActivity : AppCompatActivity() {

    lateinit var toolbar: Toolbar
    lateinit var progressBar: ProgressBar
    lateinit var progressLayout: RelativeLayout
    lateinit var recyclerView: RecyclerView
    lateinit var recyclerAdapter: FoodAdapter
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var imgbtnFav: ImageButton
    lateinit var btnProceed: Button
    val foodList = ArrayList<Food>()
    lateinit var restEn: RestEntity
    var restId: String? = ""
    var name: String? = ""
    var image: String? = ""
    var rating: String? = ""
    var cost: String? = ""
    var title: String = "Food Items"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food)

        toolbar = findViewById(R.id.toolBarFood)
        progressLayout = findViewById(R.id.progressLayout)
        progressBar = findViewById(R.id.progressBar)
        recyclerView = findViewById(R.id.recycler_food)
        imgbtnFav = findViewById(R.id.imgAddFav)
        btnProceed = findViewById(R.id.btnProceed)
        layoutManager = LinearLayoutManager(this@FoodActivity)
        btnProceed.visibility = View.INVISIBLE

        if(ConnectionManager().checkConnectivity(this@FoodActivity as Context)) {
            progressLayout.visibility = View.VISIBLE
            if (intent != null){
                restId = intent.getStringExtra("id")
                image = intent.getStringExtra("image")
                name = intent.getStringExtra("name").toString()
                rating = intent.getStringExtra("rating")
                cost = intent.getStringExtra("cost")
                title = name as String
            } else {
                Toast.makeText(this@FoodActivity,"Intent Error", Toast.LENGTH_SHORT).show()
                finish()
            }
            if (restId == ""){
                Toast.makeText(this@FoodActivity,"Restaurant ID Error", Toast.LENGTH_SHORT).show()
                finish()
            }
            setSupportActionBar(toolbar)
            supportActionBar?.title = title

            restEn = RestEntity(
                restId.toString(),
                name.toString(),
                rating.toString(),
                cost.toString(),
                image.toString()
            )
            val checkFav = DBRest(applicationContext,restEn,1).execute()
            val isFav = checkFav.get()
            if (isFav) {
                val draw =  ContextCompat.getDrawable(applicationContext,R.drawable.ic_rest_added_fav)
                imgbtnFav.setImageDrawable(draw)
            }

            val foo = DBFood(applicationContext).execute()
            val resu = foo.get()
            if(resu != 0) {
                btnProceed.visibility = View.VISIBLE
                btnProceed.setOnClickListener {
                    val intent = Intent(this@FoodActivity, CartActivity::class.java)
                    intent.putExtra("hotel", title)
                    startActivity(intent)
                }
            }
            imgbtnFav.setOnClickListener {
                if(!isFav){
                    val result = DBRest(this@FoodActivity,restEn,2).execute().get()
                    if (result){
                        Toast.makeText(this@FoodActivity,"Restaurant added to Favourites", Toast.LENGTH_SHORT).show()
                        val draw =  ContextCompat.getDrawable(applicationContext,R.drawable.ic_rest_added_fav)
                        imgbtnFav.setImageDrawable(draw)
                    } else {
                        Toast.makeText(this@FoodActivity,"Error in adding Favourites", Toast.LENGTH_SHORT).show()
                    }
                } else{
                    val async = DBRest(this@FoodActivity,restEn,3).execute()
                    val result = async.get()
                    if(result){
                        Toast.makeText(this@FoodActivity,"Restaurant removed from Favourites", Toast.LENGTH_SHORT).show()
                        val noDraw =  ContextCompat.getDrawable(applicationContext,R.drawable.ic_fav_rest)
                        imgbtnFav.setImageDrawable(noDraw)
                    } else {
                        Toast.makeText(this@FoodActivity,"Error in removing Favourites", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            val queue = Volley.newRequestQueue(this@FoodActivity)
            val url = "http://13.235.250.119/v2/restaurants/fetch_result/$restId"
            val jsonObjectRequest = object:JsonObjectRequest(
                Request.Method.GET,url,null,
                Response.Listener {
                    try {
                        progressLayout.visibility = View.GONE
                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")
                        if (success) {
                            val food = data.getJSONArray("data")
                            for(i in 0 until food.length()) {
                                val foodObject = food.getJSONObject(i)
                                val foodDet = Food(
                                    foodObject.getString("id"),
                                    i+1,
                                    foodObject.getString("name"),
                                    foodObject.getString("cost_for_one"),
                                    foodObject.getString("restaurant_id")
                                )
                                foodList.add(foodDet)
                                recyclerAdapter = FoodAdapter(this,foodList,restEn)
                                recyclerView.layoutManager = layoutManager
                                recyclerView.adapter = recyclerAdapter
                            }
                            } else {
                            Toast.makeText(this@FoodActivity,"Error in fetching data", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: JSONException){
                        Toast.makeText(this@FoodActivity,"Exception occurred", Toast.LENGTH_SHORT).show()
                    }
                },Response.ErrorListener {
                    Toast.makeText(this@FoodActivity,"Volley Error occurred", Toast.LENGTH_SHORT).show()
                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String,String>()
                    headers["Content-type"] = "application/json"
                    headers["Token"] = "b16072bf31c205"
                    return headers
                }
            }
                    queue.add(jsonObjectRequest)
        }
        else {
            val alert = AlertDialog.Builder(this )
            alert.setTitle("Error")
            alert.setMessage("Internet Connection not Found")
            alert.setPositiveButton("open settings") { _, _ ->
                val settingsOpen = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsOpen)
                finish()
            }
            alert.setNegativeButton("Exit") { _, _ ->
                ActivityCompat.finishAffinity(this)
            }
            alert.create()
            alert.show()
        }
    }
    class DBRest(val context: Context,val restEntity: RestEntity,val mode:Int):AsyncTask<Void,Void,Boolean>(){
        val dB = Room.databaseBuilder(context,RestDatabase::class.java,"rest-db").build()
        override fun doInBackground(vararg p0: Void?): Boolean {
            when(mode){
                1 ->{
                    val rest: RestEntity? = dB.restDao().getRestById(restEntity.restId)
                    dB.close()
                    return rest != null
                }
                2 ->{
                    dB.restDao().insert(restEntity)
                    dB.close()
                    return true
                }
                3 ->{
                    dB.restDao().delete(restEntity)
                    dB.close()
                    return true
                }
            }
            return false
        }
    }
    class DBFood(val context: Context): AsyncTask<Void,Void,Int?>(){
        val db = Room.databaseBuilder(context,FoodDatabase::class.java,"food-db").build()
        override fun doInBackground(vararg p0: Void?): Int? {
            val result: List<FoodEntity>? = db.foodDao().getAllFood()
            val size: Int? = result?.size
            return size
        }
    }

    class DeleteFood(val context: Context): AsyncTask<Void,Void,Boolean>(){
        override fun doInBackground(vararg p0: Void?): Boolean {
            val db = Room.databaseBuilder(context,FoodDatabase::class.java,"food-db").build()
            db.foodDao().deleteFood()
            return true
        }
    }
    override fun onBackPressed() {
        val async = CartActivity.GetAllFoodAdded(applicationContext).execute().get()
        if (async != null){
            DeleteFood(applicationContext).execute()
        }
        val intent = Intent(this@FoodActivity,MainActivity::class.java)
        startActivity(intent)
    }
}