package com.example.nfte.stormy;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;


public class MainActivity extends ActionBarActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    private CurrentWeather mCurrentWeather;
    
    @Bind(R.id.timeLabel) TextView mTimeLabel;
    @Bind(R.id.temperatureLabel) TextView mTemperatureLabel;
    @Bind(R.id.humidityValue) TextView mHumidityValue;
    @Bind(R.id.precipValue) TextView mPrecipValue;
    @Bind(R.id.summaryLabel) TextView mSummaryLabel;
    @Bind(R.id.iconImageView) ImageView mIconImageView;
    @Bind(R.id.refreshImageView) ImageView mRefreshImageView;
    @Bind(R.id.progressBar) ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        mProgressBar.setVisibility(View.INVISIBLE);

        final double latitude = 37.8267;
        final double longitude = -122.423;

        mRefreshImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getForecast(latitude,longitude);
            }
        });


        getForecast(latitude, longitude);
        Log.d(TAG, "Main UI Code is Running");

    }

    private void getForecast(double latitude, double longitude) {
        String apiKey = "553142b0c0e199e1416b81d99b304173";
        String forecastUrl  = "https://api.forecast.io/forecast/"+ apiKey+ "/"+ latitude +","+longitude; //"https://api.forecast.io/forecast/"+apiKey+"/"+latitude+","+ longitude;


        if(isNetworkAvailable()) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    toggleRefresh();
                }
            });

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(forecastUrl).build();

            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleRefresh();
                        }
                    });
                    alertUserAboutError();
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleRefresh();
                        }
                    });
                    try {
                        String jsonData = response.body().string();
                        Log.v(TAG, jsonData);
                        if (response.isSuccessful()) {
                            mCurrentWeather = getCurrentDetails(jsonData);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateDisplay();
                                }
                            });

                        } else {
                            alertUserAboutError();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Exception Caught", e);
                    } catch (JSONException e) {
                        Log.e(TAG, "Exception Caught", e);
                    }
                }
            });
        }
        else {
            Toast.makeText(this, getString(R.string.network_unavailable), Toast.LENGTH_LONG).show();
        }
    }

    private void toggleRefresh() {
        if(mProgressBar.getVisibility() == View.INVISIBLE) {
            mProgressBar.setVisibility(View.VISIBLE);
            mRefreshImageView.setVisibility(View.INVISIBLE);
        }
        else {
            mProgressBar.setVisibility(View.INVISIBLE);
            mRefreshImageView.setVisibility(View.VISIBLE);
        }
    }

    private void updateDisplay() {
        mTemperatureLabel.setText(mCurrentWeather.getmTemperature() + "");
        mTimeLabel.setText("At "+ mCurrentWeather.getFormattedTime() +", it will be");
        mHumidityValue.setText(mCurrentWeather.getmHumidity() + "");
        mPrecipValue.setText(mCurrentWeather.getmPrecipChance() + "%");
        mSummaryLabel.setText(mCurrentWeather.getmSummary());

        Drawable drawable = getResources().getDrawable(mCurrentWeather.getIconId());
        mIconImageView.setImageDrawable(drawable);

    }

    private CurrentWeather getCurrentDetails(String jsonData) throws JSONException{

        JSONObject forecast = new JSONObject(jsonData);
        String timeZone = forecast.getString("timezone");
        Log.i(TAG,"From JSON: "+ timeZone);
        JSONObject currently = forecast.getJSONObject("currently");

        CurrentWeather currentWeather = new CurrentWeather();
        currentWeather.setmHumidity(currently.getDouble("humidity"));
        currentWeather.setmTime(currently.getLong("time"));
        currentWeather.setmIcon(currently.getString("icon"));
        currentWeather.setmPrecipChance(currently.getDouble("precipProbability"));
        currentWeather.setmSummary(currently.getString("summary"));
        currentWeather.setmTemperature(currently.getDouble("temperature"));
        currentWeather.setmTimeZone(timeZone);

        Log.d(TAG, currentWeather.getFormattedTime());




     return currentWeather;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if(networkInfo != null && networkInfo.isConnected()){
            isAvailable = true;
        }
        return isAvailable;
    }

    private void alertUserAboutError() {
        AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.show(getFragmentManager(), "error_dialog");

    }

}
