package com.vikram.foodrunner.fragment

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.vikram.foodrunner.R
import com.vikram.foodrunner.adapter.FavouritesAdapter
import com.vikram.foodrunner.database.rest.RestDatabase
import com.vikram.foodrunner.database.rest.RestEntity


class FavouritesFragment : Fragment() {

    lateinit var restFavRecyclerView: RecyclerView
    lateinit var progressLayoutFav: RelativeLayout
    lateinit var progressBarFav: ProgressBar
    lateinit var txtEmpty: TextView
    lateinit var imgEmpty: ImageView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: FavouritesAdapter
    var restList = listOf<RestEntity>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_favourites, container, false)

        txtEmpty = view.findViewById(R.id.txtFavEmpty)
        txtEmpty.visibility = View.INVISIBLE
        imgEmpty = view.findViewById(R.id.imgEmpty)
        imgEmpty.visibility = View.INVISIBLE
        restFavRecyclerView = view.findViewById(R.id.restFavRecyclerView)
        progressLayoutFav = view.findViewById(R.id.progressLayoutFav)
        progressBarFav = view.findViewById(R.id.progressBarFav)
        progressLayoutFav.visibility = View.VISIBLE

        layoutManager = LinearLayoutManager(activity as Context)
        restList = RetrieveRest(activity as Context).execute().get()

        val size: Int? = restList.size

        if(activity != null && size != 0){
            progressLayoutFav.visibility = View.GONE
            recyclerAdapter = FavouritesAdapter(activity as Context,restList)
            restFavRecyclerView.adapter = recyclerAdapter
            restFavRecyclerView.layoutManager = layoutManager
        } else {
            progressLayoutFav.visibility = View.GONE
            txtEmpty.visibility = View.VISIBLE
            imgEmpty.visibility = View.VISIBLE
        }

        return view
    }
    class RetrieveRest(val context: Context): AsyncTask<Void,Void,List<RestEntity>>(){
        override fun doInBackground(vararg p0: Void?): List<RestEntity> {
            val db = Room.databaseBuilder(context,RestDatabase::class.java,"rest-db").build()
            return db.restDao().getAllRest()
        }
    }
}
