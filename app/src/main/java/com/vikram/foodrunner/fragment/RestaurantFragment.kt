package com.vikram.foodrunner.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.view.menu.MenuView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.MenuCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.vikram.foodrunner.R
import com.vikram.foodrunner.adapter.RestaurantAdapter
import com.vikram.foodrunner.model.Restaurant
import com.vikram.foodrunner.util.ConnectionManager
import org.json.JSONException
import java.util.*
import kotlin.collections.HashMap

class RestaurantFragment : Fragment() {

    lateinit var restRecyclerView: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: RestaurantAdapter
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    val restInfo = arrayListOf<Restaurant>()
    val ratingComparator = Comparator<Restaurant> { rest1, rest2 ->
        if (rest1.rating.compareTo(rest2.rating, true) == 0) {
            rest2.name.compareTo(rest1.name, true)
        } else {
            rest1.rating.compareTo(rest2.rating, true)
        }
    }
    val costComparator = Comparator<Restaurant> { rest1, rest2 ->
        if (rest1.cost_for_one.compareTo(rest2.cost_for_one, true) == 0) {
            rest2.name.compareTo(rest1.name, true)
        } else {
            rest1.cost_for_one.compareTo(rest2.cost_for_one, true)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_restaurant, container, false)

        setHasOptionsMenu(true)

        restRecyclerView = view.findViewById(R.id.restRecyclerView)
        layoutManager = LinearLayoutManager(activity)
        progressLayout = view.findViewById(R.id.progressLayout)
        progressBar = view.findViewById(R.id.progressBar)
        progressLayout.visibility = View.VISIBLE

        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/"
        if (ConnectionManager().checkConnectivity(activity as Context)) {
            val jsonObjectRequest = object : JsonObjectRequest(Request.Method.GET, url, null,
                Response.Listener {
                    try {
                        progressLayout.visibility = View.GONE
                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")
                        if (success) {
                            val restData = data.getJSONArray("data")
                            for (i in 0 until restData.length()) {
                                val restJSONObject = restData.getJSONObject(i)
                                val restObject = Restaurant(
                                    restJSONObject.getString("id"),
                                    restJSONObject.getString("name"),
                                    restJSONObject.getString("rating"),
                                    restJSONObject.getString("cost_for_one"),
                                    restJSONObject.getString("image_url")
                                )
                                restInfo.add(restObject)
                                if (activity != null) {
                                    recyclerAdapter =
                                        RestaurantAdapter(activity as Context, restInfo)
                                    restRecyclerView.layoutManager = layoutManager
                                    restRecyclerView.adapter = recyclerAdapter
                                } else {
                                    Toast.makeText(
                                        activity as Context,
                                        "Activity becomes null",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        } else {
                            Toast.makeText(
                                activity as Context,
                                "Some error Occurred",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: JSONException) {
                        Toast.makeText(
                            activity as Context,
                            "Exception Occurred",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }, Response.ErrorListener {
                    if (activity != null) {
                        Toast.makeText(
                            activity as Context,
                            "Volley Error Occurred",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-type"] = "application/json"
                    headers["Token"] = "b16072bf31c205"
                    return headers
                }
            }
            queue.add(jsonObjectRequest)

        } else {
            val alert = AlertDialog.Builder(activity as Context)
            alert.setTitle("Error")
            alert.setMessage("Internet Connection not Found")
            alert.setPositiveButton("open settings") { _, _ ->
                val settingsOpen = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsOpen)
                activity?.finish()
            }
            alert.setNegativeButton("Exit") { _, _ ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            alert.create()
            alert.show()
        }

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater?.inflate(R.menu.menu_restaurant, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item?.itemId
            when(id) {
                R.id.menuSort -> {
                    Collections.sort(restInfo, costComparator)
                }
                R.id.menuSortRev -> {
                    Collections.sort(restInfo, costComparator)
                    restInfo.reverse()
                }
                R.id.menuSortRate -> {
                    Collections.sort(restInfo, ratingComparator)
                    restInfo.reverse()
                }
            }
        recyclerAdapter.notifyDataSetChanged()
        return super.onOptionsItemSelected(item)
    }
}