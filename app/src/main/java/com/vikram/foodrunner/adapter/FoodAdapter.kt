package com.vikram.foodrunner.adapter

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.vikram.foodrunner.R
import com.vikram.foodrunner.activity.FoodActivity
import com.vikram.foodrunner.database.order.FoodDatabase
import com.vikram.foodrunner.database.order.FoodEntity
import com.vikram.foodrunner.database.rest.RestEntity
import com.vikram.foodrunner.model.Food

class FoodAdapter(val context:Context,val foodList: List<Food>,val foodEn: RestEntity): RecyclerView.Adapter<FoodAdapter.FoodViewHolder>() {

    val resId = foodEn.restId
    val restName = foodEn.restName
    val restImage = foodEn.image
    val restRating = foodEn.rating
    val restCost = foodEn.costPer

    class FoodViewHolder(view: View): RecyclerView.ViewHolder(view){
        val serialFood: TextView = view.findViewById(R.id.serialFood)
        val txtFood: TextView = view.findViewById(R.id.txtfoodName)
        val rsFood: TextView = view.findViewById(R.id.rsFood)
        val btnAdd: Button = view.findViewById(R.id.btnAdd)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_food_single_row,parent,false)
        return FoodViewHolder(view)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val food = foodList[position]
        holder.serialFood.text = food.serialId.toString()
        val id = food.id
        holder.txtFood.text = food.foodName
        holder.rsFood.text = food.cost
        val restId = food.restId

        val foodEntity = FoodEntity(
            id,
            holder.serialFood.text.toString(),
            holder.txtFood.text.toString(),
            holder.rsFood.text.toString(),
            restId
        )

        val asyn = DBAsyncTask(context,foodEntity,1).execute()
        val resul = asyn.get()
        if(resul){
            holder.btnAdd.text = "Remove"
            val favColor =
                ContextCompat.getColor(context, R.color.favourites)
            holder.btnAdd.setBackgroundColor(favColor)
        } else {
            holder.btnAdd.text = "Add"
            val noFavColor =
                ContextCompat.getColor(context, R.color.colorPrimary)
            holder.btnAdd.setBackgroundColor(noFavColor)
        }

        holder.btnAdd.setOnClickListener {

            if (!DBAsyncTask(context,foodEntity,1).execute().get()){
                val async = DBAsyncTask(context.applicationContext,foodEntity,2).execute()
                val result = async.get()
                if (result){
                    Toast.makeText(context,"Added Cart", Toast.LENGTH_SHORT).show()
                    holder.btnAdd.text = "Remove"
                    val favColor =
                        ContextCompat.getColor(context, R.color.favourites)
                    holder.btnAdd.setBackgroundColor(favColor)
                    val intent = Intent(context,FoodActivity::class.java)
                    intent.putExtra("id",resId)
                    intent.putExtra("image",restImage)
                    intent.putExtra("name",restName)
                    intent.putExtra("rating",restRating)
                    intent.putExtra("cost",restCost)
                    context.startActivity(intent)
                } else {
                    Toast.makeText(context,"Error in adding Cart", Toast.LENGTH_SHORT).show()
                }
            } else {
                val async = DBAsyncTask(context.applicationContext,foodEntity,3).execute()
                val result = async.get()
                if (result){
                    Toast.makeText(context,"Removed from Cart", Toast.LENGTH_SHORT).show()
                    holder.btnAdd.text = "Add"
                    val noFavColor =
                        ContextCompat.getColor(context, R.color.colorPrimary)
                    holder.btnAdd.setBackgroundColor(noFavColor)
                    val intent = Intent(context,FoodActivity::class.java)
                    intent.putExtra("id",resId)
                    intent.putExtra("image",restImage)
                    intent.putExtra("name",restName)
                    intent.putExtra("rating",restRating)
                    intent.putExtra("cost",restCost)
                    context.startActivity(intent)
                } else {
                    Toast.makeText(context,"Error in removing Cart", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return foodList.size
    }
    class DBAsyncTask(val context: Context,val foodEntity: FoodEntity,val mode: Int): AsyncTask<Void,Void,Boolean>(){
        val db = Room.databaseBuilder(context,FoodDatabase::class.java,"food-db").build()
        override fun doInBackground(vararg p0: Void?): Boolean {
            when(mode){
                1 -> {
                    val food: FoodEntity? = db.foodDao().getFoodById(foodEntity.id)
                    db.close()
                    return food != null
                }
                2 -> {
                    db.foodDao().insert(foodEntity)
                    db.close()
                    return true
                }
                3 -> {
                    db.foodDao().delete(foodEntity)
                    db.close()
                    return true
                }
            }
            return false
        }
    }
}