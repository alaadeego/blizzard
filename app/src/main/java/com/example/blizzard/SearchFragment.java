package com.example.blizzard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.bumptech.glide.Glide;
import com.example.blizzard.Util.TimeUtil;
import com.example.blizzard.model.Weather;
import com.example.blizzard.model.WeatherData;
import com.example.blizzard.viewmodel.BlizzardViewModel;


public class SearchFragment extends Fragment {
    TextView tvCityTitle;
    TextView tvCityTemp;
    TextView tvCityHumidity;
    TextView tvCityDescription;
    TextView tvCityWindSpeed;
    TextView tvTime;
    ImageView IvWeatherImage;
    ProgressBar dataLoading;
    private TimeUtil mTimeUtil = new TimeUtil();
    private BlizzardViewModel mBlizzardViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBlizzardViewModel = new ViewModelProvider(requireActivity()).get(BlizzardViewModel.class);
//        mBlizzardViewModel.init();
        if (getArguments() != null) {
            String cityName = SearchFragmentArgs.fromBundle(getArguments()).getCityName();
            mBlizzardViewModel.getWeather(cityName);
        }
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.search_fragment, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);

        mBlizzardViewModel.getWeatherLiveData().observe(getViewLifecycleOwner(), weatherData -> {
            if (weatherData != null) {
                mTimeUtil.setTime(weatherData.getDt(), weatherData.getTimezone());
                insertDataIntoViews(weatherData);
            }
        });

    }

    private void findViews(@NonNull View view) {
        tvCityTitle = view.findViewById(R.id.tv_cityName);
        tvCityDescription = view.findViewById(R.id.tv_weatherDescription);
        tvCityHumidity = view.findViewById(R.id.tv_humidityValue);
        tvCityTemp = view.findViewById(R.id.tv_tempValue);
        tvCityWindSpeed = view.findViewById(R.id.tv_windSpeed);
        tvTime = view.findViewById(R.id.tv_dayTime);
        IvWeatherImage = view.findViewById(R.id.weather_icon);
        dataLoading = view.findViewById(R.id.data_loading);
    }

    private void insertDataIntoViews(WeatherData weatherData) {
        String cityName = weatherData.getName() + ", " + weatherData.getSys().getCountry();
        tvCityTitle.setText(cityName);

        Double temp = weatherData.getMain().getTemp();
        tvCityTemp.setText(conToCelsius(temp));

        String humidity = weatherData.getMain().getHumidity() + "%";
        tvCityHumidity.setText(humidity);

        Weather weather = weatherData.getWeather().get(0);
        tvCityDescription.setText(weather.getDescription());

        LoadImage(weather.getIcon());

        String windSpeed = weatherData.getWind().getSpeed() + " m/s";

        tvCityWindSpeed.setText(windSpeed);

        tvTime.setText(mTimeUtil.getTime());

        dataLoading.setVisibility(View.INVISIBLE);
        makeVisible();

    }

    private void makeVisible() {
        tvCityTitle.setVisibility(View.VISIBLE);
        tvCityDescription.setVisibility(View.VISIBLE);
        tvCityHumidity.setVisibility(View.VISIBLE);
        tvCityTemp.setVisibility(View.VISIBLE);
        tvCityWindSpeed.setVisibility(View.VISIBLE);
        tvTime.setVisibility(View.VISIBLE);
        IvWeatherImage.setVisibility(View.VISIBLE);
    }

    private void LoadImage(String iconId) {
        String url = String.format("http://openweathermap.org/img/wn/%s@4x.png", iconId);

        Glide.with(requireView())
                .load(url)
                .error(R.drawable.ic_cloud)
                .into(IvWeatherImage);
    }

    private String conToCelsius(Double temp) {
        int celsius = (int) Math.round(temp - 273.15);
        return celsius + "°C";
    }
}