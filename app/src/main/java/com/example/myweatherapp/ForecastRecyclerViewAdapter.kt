package com.example.myweatherapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myweatherapp.model.Data
import kotlinx.android.synthetic.main.layout_daily_forecast.view.*
import kotlinx.android.synthetic.main.layout_forecast.view.*
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import com.example.myweatherapp.MainActivity as MainActivity1


class ForecastRecyclerViewAdapter( private val hourly_forecasts: List<Data>,var temperature:String):
    RecyclerView.Adapter<ForecastRecyclerViewAdapter.ForecastViewHolder>()
{

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder {
            return ForecastViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_forecast, parent ,false)
            )
    }


    override fun getItemCount() = hourly_forecasts.size

    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {

        val hourlyForecast = hourly_forecasts[position]
        val timeconvert = DateTimeFormatter.ISO_INSTANT
            .format(java.time.Instant.ofEpochSecond(hourlyForecast.time))
        val time = Instant.parse(timeconvert).atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("E dd-MM HH:mm"))

        holder.view.timeTextView.text = time
        holder.view.summaryTextView.text = hourlyForecast.summary
        holder.view.temperatureTextView.text =hourlyForecast.temperature.toString().plus(temperature)

    }

    class ForecastViewHolder(val view: View):RecyclerView.ViewHolder(view)
    {

    }
}