package com.example.weather_app;

import androidx.appcompat.app.AppCompatActivity;

import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.weather_app.databinding.ActivityMainBinding;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.locks.Condition;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

//https://api.openweathermap.org/data/2.5/weather?q=Meerut&appid=2f3e0bfab9e5dcc8d7db91bba78ae359
public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        fetchWeatherData("Meerut");
        SearchCity();
    }

    private void SearchCity() {
        SearchView searchView = binding.searchView;
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Perform a new weather data fetch when the user submits a query
                fetchWeatherData(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Optional: Handle text changes as the user types
                return true;
            }
        });
    }


    private void fetchWeatherData(String city) {
        Retrofit retrofit=new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .build();

        ApiInterface apiInterface = retrofit.create(ApiInterface.class);

        Call<WeatherApp> call = apiInterface.getWeatherData(city,"2f3e0bfab9e5dcc8d7db91bba78ae359","metric");
        call.enqueue(new Callback<WeatherApp>() {
            @Override
            public void onResponse(Call<WeatherApp> call, Response<WeatherApp> response) {
                WeatherApp weatherApp = response.body();
                if (response.isSuccessful() && weatherApp != null) {
                    List<Weather> weatherList = weatherApp.getWeather();
                    Sys sys = weatherApp.getSys();
                    Wind wind = weatherApp.getWind();
                    Main main = weatherApp.getMain();
                    if (main != null && sys != null && wind != null ) {
                    double temperature = main.getTemp();
                    double temp_max = main.getTempMax();
                    double temp_min = main.getTempMin();
                    long humidity = main.getHumidity();
                    long sunrise = sys.getSunrise();
                    double speed = wind.getSpeed();
                    long sunset = sys.getSunset();
                    long seaLevel = main.getPressure();



                    Weather weather = weatherList.get(0);  // Get the first weather item
                    String condition = weather.getMain();

                        binding.textView4.setText(temperature+ " ℃");
                        binding.textView6.setText("Max:"+temp_max+"℃");
                        binding.textView5.setText("Min:"+temp_min+"℃");
                        binding.humidity.setText(humidity + " %");
                        binding.ws.setText(speed + " m/s");
                        binding.sunrise.setText((time(sunrise)));
                        binding.sunset.setText((time(sunset)));
                        binding.sealevel.setText(seaLevel + " hpa");
                        binding.condition.setText(condition);
                        binding.weather2.setText(condition);
                        binding.textView2.setText(city);
                        binding.textView7.setText(dayName(System.currentTimeMillis(),TimeZone.getTimeZone("UTC")));
                        binding.textView8.setText(date());
                        changeimageinbg(condition);
                    }

                }

            }
            @Override
            public void onFailure(Call<WeatherApp> call, Throwable t) {

            }
        });
        }

    private void changeimageinbg(String condition) {
        if( "Clear Sky".equals(condition)|| "Clear".equals(condition)|| "Sunny".equals(condition)){
            binding.getRoot().setBackgroundResource(R.drawable.sunny_background);
            binding.lottieAnimationView.setAnimation(R.raw.sun);
        } else if ( "Partly Clouds".equals(condition)|| "Clouds".equals(condition)|| "Overcast".equals(condition)|| "Mist".equals(condition)|| "Foggy".equals(condition)|| "Haze".equals(condition)){
            binding.getRoot().setBackgroundResource(R.drawable.colud_background);
            binding.lottieAnimationView.setAnimation(R.raw.cloud);
        }else if ( "Light Rain".equals(condition)|| "Drizzle".equals(condition)|| "Moderate Rain".equals(condition)|| "Showers".equals(condition)|| "Heavy Rain".equals(condition)){
            binding.getRoot().setBackgroundResource(R.drawable.colud_background);
            binding.lottieAnimationView.setAnimation(R.raw.cloud);
        }else if ( "Light Snow".equals(condition)|| "Moderate Snow".equals(condition)|| "Heavy Snow".equals(condition)|| "Blizzard".equals(condition)){
            binding.getRoot().setBackgroundResource(R.drawable.colud_background);
            binding.lottieAnimationView.setAnimation(R.raw.cloud);
        }else{
            binding.getRoot().setBackgroundResource(R.drawable.sunny_background);
            binding.lottieAnimationView.setAnimation(R.raw.sun);
        }
        binding.lottieAnimationView.playAnimation();
    }


    private String dayName(long timestamp, TimeZone timezone ) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE", Locale.getDefault());
        sdf.setTimeZone(android.icu.util.TimeZone.getTimeZone("UTC"));
        return sdf.format(new Date(timestamp));
    }
    private String time(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(new Date(timestamp * 1000L));
    }

    private String date(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        return sdf.format(new java.util.Date());
    }
}