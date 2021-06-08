package com.vikram.foodrunner.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.vikram.foodrunner.R
import com.vikram.foodrunner.activity.FoodActivity
import com.vikram.foodrunner.database.rest.RestEntity

class FavouritesAdapter(val context: Context, val restList: List<RestEntity>):RecyclerView.Adapter<FavouritesAdapter.FavouritesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouritesViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_rest_single_row, parent, false)
        return FavouritesViewHolder(view)
    }
    override fun onBindViewHolder(holder: FavouritesViewHolder, position: Int) {
        val info = restList[position]
        val id = info.restId
        holder.name.text = info.restName
        holder.rating.text = info.rating
        holder.costForOne.text = info.costPer
        Picasso.get().load(info.image).error(R.drawable.foodie_logo).into(holder.imageUrl)

        holder.llRest.setOnClickListener {
            val intent = Intent(context, FoodActivity::class.java)
            intent.putExtra("id",info.restId)
            intent.putExtra("image",info.image)
            intent.putExtra("name",info.restName)
            intent.putExtra("rating",info.rating)
            intent.putExtra("cost",info.costPer)
            context.startActivity(intent)
        }
        val restEntity = RestEntity(
            id,
            holder.name.text.toString(),
            holder.rating.text.toString(),
            holder.costForOne.text.toString(),
            holder.imageUrl.toString()
        )
        val checkFav = RestaurantAdapter.DBAsyncTask(context, restEntity, 1).execute()
        val isFav = checkFav.get()

        if (isFav){
            val draw =  ContextCompat.getDrawable(context,R.drawable.ic_rest_added_fav)
            holder.imgBtnFav.setImageDrawable(draw)
        } else {
            val noDraw =  ContextCompat.getDrawable(context,R.drawable.ic_fav_rest)
            holder.imgBtnFav.setImageDrawable(noDraw)
        }
        holder.imgBtnFav.setOnClickListener {
            if (!RestaurantAdapter.DBAsyncTask(context, restEntity, 1).execute().get()){
                val async = RestaurantAdapter.DBAsyncTask(context, restEntity, 2).execute()
                val result = async.get()
                if (result){
                    Toast.makeText(context,"Restaurant added to Favourites", Toast.LENGTH_SHORT).show()
                    val draw =  ContextCompat.getDrawable(context,R.drawable.ic_rest_added_fav)
                    holder.imgBtnFav.setImageDrawable(draw)
                } else {
                    Toast.makeText(context,"Error in adding Favourites", Toast.LENGTH_SHORT).show()
                }
            } else {
                val async = RestaurantAdapter.DBAsyncTask(context, restEntity, 3).execute()
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
    class FavouritesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.restName)
        val rating: TextView = view.findViewById(R.id.txtRating)
        val costForOne: TextView = view.findViewById(R.id.costPer)
        val imageUrl: ImageView = view.findViewById(R.id.imgRest)
        val imgBtnFav: ImageButton = view.findViewById(R.id.imgbtnFav)
        val llRest: LinearLayout = view.findViewById(R.id.llRest)
    }
}
