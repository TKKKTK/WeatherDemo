package com.wg.weatherdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wg.weatherdemo.util.NetUtil;
import com.wg.weatherdemo.util.PollingExample;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private AppCompatSpinner mSpinner;
    private ArrayAdapter<String> mSpAdapter;
    private String[] mCities;

    private TextView tvWeather,tvTem,tvTemLowHigh,tvWin,tvAir;
    private ImageView ivWeather;
    private RecyclerView rlvFutrueWeather;

    private PollingExample pollingExample;

    private Handler mHandler = new Handler(Looper.myLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0){
                String weather = (String) msg.obj;
                Log.d("MainActivity", "handleMessage: " + weather);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        pollingExample = new PollingExample();
    }


    private void initView(){
        mSpinner = findViewById(R.id.sp_city);
        mCities = getResources().getStringArray(R.array.cities);
        mSpAdapter = new ArrayAdapter<String>(this,R.layout.sp_item_layout,mCities);
        mSpinner.setAdapter(mSpAdapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                  String selectedCity = mCities[i];
                  getWeatherOfCity(selectedCity);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        tvWeather = findViewById(R.id.tv_weather);
        tvTem = findViewById(R.id.tv_tem);
        tvTemLowHigh = findViewById(R.id.tv_tem_low_high);
        tvWin = findViewById(R.id.tv_win);
        tvAir = findViewById(R.id.tv_air);

        ivWeather = findViewById(R.id.iv_weather);

        rlvFutrueWeather = findViewById(R.id.rlv_future_weather);
    }

    private void getWeatherOfCity(String selectedCity) {
        /**
         * 第一种原生httpConnecttion写法
         */
        //开启子线程，请求网络
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                //请求网络
//                String weatherOfCity = NetUtil.getWeatherOfCity(selectedCity);
//                //使用handler 向主线程发送数据
//                Message message = Message.obtain();
//                message.what = 0;
//                message.obj = weatherOfCity;
//                mHandler.sendMessage(message);
//            }
//        }).start();

        /**
         * 第二种OkHttp写法
         */
//        NetUtil.sendOkhttpRequest(NetUtil.getWeatherUrl(selectedCity), new Callback() {
//            @Override
//            public void onFailure(@NonNull Call call, @NonNull IOException e) {
//
//            }
//            @Override
//            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
//                //使用handler 向主线程发送数据
//                Message message = Message.obtain();
//                message.what = 0;
//                message.obj = response.body().string();
//                mHandler.sendMessage(message);
//            }
//        });

        /**
         * 第三种轮询查询 OkHttp + RxJava 写法
         */
         pollingExample.stopPolling();
         pollingExample.startPolling(NetUtil.getWeatherUrl(selectedCity),5);
    }
}