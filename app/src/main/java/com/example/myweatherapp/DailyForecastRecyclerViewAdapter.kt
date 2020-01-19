package com.example.myweatherapp

import android.location.Location
import android.service.autofill.OnClickAction
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myweatherapp.model.DailyData
import kotlinx.android.synthetic.main.layout_daily_forecast.view.*
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter


class DailyForecastRecyclerViewAdapter(private val daily_forecasts: List<DailyData>, var temperature:String,val loc:Location,val onHourlyClickListener: OnHourlyClickListener):
    RecyclerView.Adapter<DailyForecastRecyclerViewAdapter.ForecastViewHolder>(),OnHourlyClickListener
{
    override fun onHourlyClick(v: View) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder {
            return ForecastViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_daily_forecast, parent ,false)
            )
    }

    override fun getItemCount() = daily_forecasts.size

    override fun onBindViewHolder (holder: ForecastViewHolder, position: Int) {

        val dailyForecast = daily_forecasts[position]
        val timeconvert = DateTimeFormatter.ISO_INSTANT
            .format(java.time.Instant.ofEpochSecond(dailyForecast.time))
        val time = Instant.parse(timeconvert).atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("E dd-MM "))
        onHourlyClickListener.onHourlyClick(holder.view.HourlyTextView)
        holder.view.dTimeTextView.text = time
        holder.view.dSummaryTextView.text = dailyForecast.summary
        holder.view.TemperatureHighTextView.text ="max temperature".plus(dailyForecast.temperatureHigh.toString()).plus(temperature)
        holder.view.TemperatureLowTextView.text ="min temperature".plus(dailyForecast.temperatureLow.toString()).plus(temperature)

    }

    class ForecastViewHolder(val view: View):RecyclerView.ViewHolder(view)
    {

    }
}