package com.example.myweather20

import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.myweather20.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MainActivity : AppCompatActivity() {

    private  val binding : ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        fetchWeatherData("Moradabad")
        searchCity()
    }

    private fun searchCity() {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if(query != null){
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })

    }

    private fun fetchWeatherData(city : String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface :: class.java)
        val response = retrofit.getWeatherData(city,"7f35a947e5b14761b39103c67acf2ca0","metric")
        response.enqueue(object : Callback<WeatherApp>{
            override fun onResponse(
                call : Call<WeatherApp>,
                response : Response<WeatherApp>
            ) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    val temperature = responseBody.main.temp.toString()
                    val humidity = responseBody.main.humidity
                    val windSpeed = responseBody.wind.speed
                    val sunRise = responseBody.sys.sunrise
                    val sunSet = responseBody.sys.sunset
                    val seaLevel = responseBody.main.sea_level
                    val condition = responseBody.weather.firstOrNull()?.main?:"unknown"
                    val maxTemp = responseBody.main.temp_max
                    val minTemp = responseBody.main.temp_min
                    binding.temp.text = "$temperature °C"
                    binding.minTemp.text = "Min Temp : $minTemp °C"
                    binding.maxTemp.text = "Max Temp : $maxTemp °C"
                    binding.humidity.text = "$humidity %"
                    binding.windSpeed.text = "$windSpeed m/s"
                    binding.sunset.text = "${time(sunSet.toLong())}"
                    binding.sunrise.text = "${time(sunRise.toLong() )}"
                    binding.seaLevel.text = "$seaLevel m"
                    binding.weather.text = "$condition"
                    binding.condition.text = "$condition"

                    binding.day.text = dayName(System.currentTimeMillis())
                        binding.date.text = date()
                        binding.city.text = "$city"
                    // Log.d("TAG","onResponse : $temperature")

                    changeImageOnChangingWeather(condition)
                }
            }

            override fun onFailure(
                call : Call<WeatherApp?>,
                t : Throwable
            ) {
                TODO("Not yet implemented")
            }

        })



    }
    private fun changeImageOnChangingWeather(conditions : String) {

        when(conditions){
            "Haze","Partially clouds","Clouds","Overcast","Foggy","Mist"->{
                binding.root.setBackgroundResource(R.drawable.cloud_screen)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }

            "Clear sky","Sunny","Clear"->{
                binding.root.setBackgroundResource(R.drawable.sunny_screen)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }

            "Rain","Light Rain","Heavy rain","Moderate rain","Showers","Drizzle","Thunderstorm","Gusty winds"->{
                binding.root.setBackgroundResource(R.drawable.rain_screen)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }

            "Snow","Light snow","Moderate snow","Heavy snow","Blizzard"->{
                binding.root.setBackgroundResource(R.drawable.snow_screen)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }

            else->{
                binding.root.setBackgroundResource(R.drawable.sunny_screen)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
        }
        binding.lottieAnimationView.playAnimation()
    }

    private fun time(timestamp : Long): String {
        val sdf = SimpleDateFormat("HH:mm",Locale.getDefault())
        return sdf.format(Date(timestamp*1000))
    }

    private fun date(): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy",Locale.getDefault())
        return sdf.format(Date())
    }

    fun dayName(timestamp : Long) : String{
        val sdf = SimpleDateFormat("EEEE",Locale.getDefault())
        return sdf.format(Date())
    }
}