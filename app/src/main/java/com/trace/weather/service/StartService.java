package com.trace.weather.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.baidu.location.LocationClient;
import com.trace.weather.domain.Weather;
import com.trace.weather.utils.LocationApplication;

/**
 * Created by dell-pc on 2015/11/22.
 */
public class StartService extends Service{
    SharedPreferences sharedPreferences;
    public static StartService instance;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance=this;
//        System.out.println("==========start============");
        sharedPreferences=getSharedPreferences("weather",MODE_PRIVATE);
        String code=sharedPreferences.getString("code",null);
        if(code!=null){
            Weather weather=new Weather();
            weather.setCityCode(code);
            Intent intent=new Intent(this,GetWeatherService.class);
            Bundle bundle=new Bundle();
            bundle.putSerializable("locationInfo", weather);
            bundle.putInt("alarm", 1);
            intent.putExtras(bundle);
            startService(intent);
        }
        stopSelf();
    }

    @Override
    public void onDestroy() {
        stopSelf();
    }
}
