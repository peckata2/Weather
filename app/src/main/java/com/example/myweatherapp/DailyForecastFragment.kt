package com.example.myweatherapp

import android.Manifest
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.FragmentManager
import androidx.navigation.ui.AppBarConfiguration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myweatherapp.model.ForecastItem
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.fragment_daily_forecast2.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [DailyForecastFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [DailyForecastFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DailyForecastFragment : Fragment(),OnHourlyClickListener {
    override fun onHourlyClick(v: View) {
        HourlyForecastFragment.newInstance(loc)
    }

    val PERMISSION_ID = 42
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    lateinit var loc: Location
    lateinit var locationURL:String
    private lateinit var appBarConfiguration: AppBarConfiguration
    var units:String ="si"
    lateinit var temperature:String
    val DailyExclude:String ="currently,minutely,hourly,alerts,flags"
    // TODO: Rename and change types of parameters
//    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        arguments?.let {
//            param1 = it.getString(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
//        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_daily_forecast2, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        temperature = getString(R.string.celsius)


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)
        DailyRefreshLayout.setOnRefreshListener {
            fetchHourlyForecast(temperature)
        }
        getLastLocation()
        fetchHourlyForecast(temperature)

    }

    // TODO: Rename method, update argument and hook method into UI event
//    fun onButtonPressed(uri: Uri) {
//        listener?.onFragmentInteraction(uri)
//    }

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
        //listener = null
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
//    interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        fun onFragmentInteraction(uri: Uri)
//    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DailyForecastFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =DailyForecastFragment()
//            DailyForecastFragment().apply {
//                arguments = Bundle().apply {
//                    loc = locp
////                }
//            }
    }
    private fun fetchHourlyForecast(temperature: String) {

        if (::loc.isInitialized) {
            DailyRefreshLayout.isRefreshing = true
            locationURL = loc.latitude.toString() + "," + loc.longitude.toString()

            RetrofitClient.instance.getHourlyForecast(locationURL,units,DailyExclude)
                .enqueue(object : Callback<ForecastItem> {
                    override fun onFailure(call: Call<ForecastItem>, t: Throwable) {
                        Toast.makeText(activity, t.message, Toast.LENGTH_LONG).show()
                        Log.d("onFailure", "FAILURE ${t.message}")
                    }

                    override fun onResponse(
                        call: Call<ForecastItem>,
                        response: Response<ForecastItem>
                    ) {
                        DailyRefreshLayout.isRefreshing = false
                        Log.d("onResponse", "${response.code()}+ ${response.body()}")

                        val forecasts = response.body()
                        forecasts?.let {
                            showDailyForecast(it,temperature)
                        }
                    }

                })

        }
        else
            DailyRefreshLayout.isRefreshing=false

    }
    private fun showDailyForecast(forecast_item: ForecastItem, temperature:String)
    {
        DailyForecastRecyclerView.layoutManager= LinearLayoutManager(activity,RecyclerView.HORIZONTAL,false)
        DailyForecastRecyclerView.adapter =DailyForecastRecyclerViewAdapter(forecast_item.daily.data,temperature,loc,this)
    }
    private fun getLastLocation(){
        if (checkPermissions()) {
            Log.d("check permissions result: ","${checkPermissions()}")
            if (isLocationEnabled()) {
                Log.d("isLocationEnabled result: ","${isLocationEnabled()}")
                mFusedLocationClient.lastLocation.addOnCompleteListener(activity!!) { task ->
                    var location: Location? = task.result
                    if (location == null) {
                        loc=requestNewLocationData()
                        Log.d("if location == null l=requestNewLocationData: ","$loc")
                        fetchHourlyForecast(temperature)

                    } else {
                        Log.d("location not null  ","$location")
                        loc=location
                        Log.d("location not null l=location: ","$loc")
                        fetchHourlyForecast(temperature)
                    }

                }

            } else {
                Toast.makeText(activity, "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }

    }


    private fun requestNewLocationData():Location {
        var mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 10000
        mLocationRequest.fastestInterval = 5000
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)
        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest, LocationCallback(),
            Looper.myLooper()
        ).addOnCompleteListener(activity!!) {
            mFusedLocationClient.lastLocation.result!!
        }

        return mFusedLocationClient.lastLocation.result!!
    }


    private fun isLocationEnabled(): Boolean {
        var locationManager: LocationManager = activity!!.getSystemService(LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            activity!!.parent,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_ID
        )
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_ID) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLastLocation()
            }
        }
    }

}
