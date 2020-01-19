package com.example.myweatherapp

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.ui.AppBarConfiguration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myweatherapp.model.ForecastItem
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.android.synthetic.main.fragment_hourly_forecast.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//private const val ARG_PARAM1 = "param1"
//private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [HourlyForecastFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [HourlyForecastFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HourlyForecastFragment : Fragment() {
    val PERMISSION_ID = 42
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    lateinit var loc: Location
    lateinit var locationURL:String
    private lateinit var appBarConfiguration: AppBarConfiguration
    var units:String ="si"
    lateinit var temperature:String
    val hourlyExclude:String ="currently,minutely,daily,alerts,flags"
    // TODO: Rename and change types of parameters
//    private var param1: String? = null
//    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_hourly_forecast, container, false)
    }

    // TODO: Rename method, update argument and hook method into UI event


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        HourlyRefreshLayout.setOnRefreshListener {
            fetchHourlyForecast(temperature)
        }
        fetchHourlyForecast(temperature)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
//        if (context is OnFragmentInteractionListener) {
//            listener = context
//        } else {
//            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
//        }
    }

    override fun onDetach() {
        super.onDetach()
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HourlyForecastFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(locp: Location) =
            HourlyForecastFragment().apply {
                arguments = Bundle().apply {
                    loc = locp
                }
            }

    }
    private fun fetchHourlyForecast(temperature: String) {

        if (::loc.isInitialized) {
            HourlyRefreshLayout.isRefreshing = true
            locationURL = loc.latitude.toString() + "," + loc.longitude.toString()

            RetrofitClient.instance.getHourlyForecast(locationURL,units,hourlyExclude)
                .enqueue(object : Callback<ForecastItem> {
                    override fun onFailure(call: Call<ForecastItem>, t: Throwable) {
                        Toast.makeText(activity, t.message, Toast.LENGTH_LONG).show()
                        Log.d("onFailure", "FAILURE ${t.message}")
                    }

                    override fun onResponse(
                        call: Call<ForecastItem>,
                        response: Response<ForecastItem>
                    ) {
                        HourlyRefreshLayout.isRefreshing = false
                        Log.d("onResponse", "${response.code()}+ ${response.body()}")

                        val forecasts = response.body()
                        forecasts?.let {
                            showHourlyForecast(it,temperature)
                        }
                    }

                })

        }
        else
            HourlyRefreshLayout.isRefreshing=false

    }
    private fun showHourlyForecast(forecast_item: ForecastItem, temperature:String)
    {
        HourlyForecastRecyclerView.layoutManager= LinearLayoutManager(activity)
        HourlyForecastRecyclerView.adapter = ForecastRecyclerViewAdapter(forecast_item.hourly.data,temperature)
    }


}
