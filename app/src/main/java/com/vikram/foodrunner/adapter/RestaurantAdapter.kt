package com.vikram.foodrunner.adapter

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.squareup.picasso.Picasso
import com.vikram.foodrunner.R
import com.vikram.foodrunner.activity.FoodActivity
import com.vikram.foodrunner.database.rest.RestDatabase
import com.vikram.foodrunner.database.rest.RestEntity
import com.vikram.foodrunner.model.Restaurant

class RestaurantAdapter(val context: Context, val restList: List<Restaurant>):RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_rest_single_row,parent,false)
        return RestaurantViewHolder(view)
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        val info = restList[position]
        val id = info.id
        Picasso.get().load(info.image_url).error(R.drawable.foodie_logo).into(holder.imageUrl)
        holder.name.text = info.name
        holder.rating.text = info.rating
        holder.costForOne.text = info.cost_for_one

        holder.llRest.setOnClickListener {
            val intent = Intent(context,FoodActivity::class.java)
            intent.putExtra("id",id)
            intent.putExtra("image",info.image_url)
            intent.putExtra("name",info.name)
            intent.putExtra("rating",info.rating)
            intent.putExtra("cost",info.cost_for_one)
            context.startActivity(intent)
        }
        val restEntity = RestEntity(
            id,
            holder.name.text.toString(),
            holder.rating.text.toString(),
            holder.costForOne.text.toString(),
            info.image_url
        )
        val checkFav = DBAsyncTask(context,restEntity,1).execute()
        val isFav = checkFav.get()

        if (isFav){
            val draw =  ContextCompat.getDrawable(context,R.drawable.ic_rest_added_fav)
            holder.imgBtnFav.setImageDrawable(draw)
        } else {
            val noDraw =  ContextCompat.getDrawable(context,R.drawable.ic_fav_rest)
            holder.imgBtnFav.setImageDrawable(noDraw)
        }
        holder.imgBtnFav.setOnClickListener {
            if (!DBAsyncTask(context,restEntity,1).execute().get()){
                val async = DBAsyncTask(context,restEntity,2).execute()
                val result = async.get()
                if (result){
                    Toast.makeText(context,"Restaurant added to Favourites", Toast.LENGTH_SHORT).show()
                    val draw =  ContextCompat.getDrawable(context,R.drawable.ic_rest_added_fav)
                    holder.imgBtnFav.setImageDrawable(draw)
                } else {
                    Toast.makeText(context,"Error in adding Favourites", Toast.LENGTH_SHORT).show()
                }
            } else {
                val async = DBAsyncTask(context,restEntity,3).execute()
                val result = async.get()
                if(result){
                    Toast.makeText(context,"Restaurant removed from Favourites", Toast.LENGTH_SHORT).show()
                    val noDraw =  ContextCompat.getDrawable(context,R.drawable.ic_fav_rest)
                    holder.imgBtnFav.setImageDrawable(noDraw)
                } else {
                    Toast.makeText(context,"Error in removing Favourites", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return restList.size
    }
    class RestaurantViewHolder(view: View): RecyclerView.ViewHolder(view){
        val imageUrl: ImageView = view.findViewById(R.id.imgRest)
        val name: TextView = view.findViewById(R.id.restName)
        val rating:TextView = view.findViewById(R.id.txtRating)
        val costForOne: TextView = view.findViewById(R.id.costPer)
        val imgBtnFav: ImageButton = view.findViewById(R.id.imgbtnFav)
        val llRest: LinearLayout = view.findViewById(R.id.llRest)
    }
    class DBAsyncTask(val context: Context,val restEntity: RestEntity,val mode:Int):AsyncTask<Void,Void,Boolean>(){
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
}