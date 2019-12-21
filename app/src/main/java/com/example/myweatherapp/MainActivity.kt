package com.example.myweatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myweatherapp.data.Data
import com.example.myweatherapp.data.ForecastItem
import com.example.myweatherapp.data.Hourly
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        RetrofitClient.instance.getForecast().enqueue(object : Callback<ForecastItem>{
            override fun onFailure(call: Call<ForecastItem>, t: Throwable) {
                Toast.makeText(applicationContext,t.message,Toast.LENGTH_LONG).show()
                Log.d("onFailure","FAILURE ${t.message}")
            }

            override fun onResponse(
                call: Call<ForecastItem>,
                response: Response<ForecastItem>
            ) {
                Log.d("onResponse","${response.code()}+ ${response.body()}")
                val forecasts = response.body()
                forecasts?.let{
                    showHourlyForecast(it)
                }
            }

        })



    }
    private fun showHourlyForecast(forecast_item: ForecastItem)
    {
        ForecastRecyclerView.layoutManager=LinearLayoutManager(this)
        ForecastRecyclerView.adapter = ForecastRecyclerViewAdapter(forecast_item.hourly.data)
    }
}
