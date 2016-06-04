package com.trace.weather.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.trace.weather.R;
import com.trace.weather.activity.ShowActivity;
import com.trace.weather.domain.Weather;
import com.trace.weather.utils.DataUtils;
import com.trace.weather.utils.LocationApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;


/**
 * Created by dell-pc on 2015/10/29.
 */
public class GetWeatherService extends Service{
    public static GetWeatherService instance;
    private final int NOTIFICATION_ID=0x123;
    BufferedReader bufferedReader;
    StringBuilder stringBuilder;
    String code;
    Bundle bundle;
    NotificationManager notificationManager;
    Notification notification;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance=this;
        notificationManager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        bundle=intent.getExtras();
        Weather weather= (Weather) bundle.get("locationInfo");
//        System.out.println(weather.getCityCode()+"================================");
        code=weather.getCityCode();
        getWeather(code);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
    }

    public void getWeather(String code){
//        System.out.println(code);
        final String cityCode=code;
        new Thread(){
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void run() {
                super.run();
                try {
                    String path="http://weather.51wnl.com/weatherinfo/GetMoreWeather?cityCode="+cityCode+"&weatherType=0";
                    URL url=new URL(path);
                    URLConnection connection=url.openConnection();
                    connection.setConnectTimeout(2000);
                    connection.connect();
                    bufferedReader=new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    stringBuilder=new StringBuilder();
                    String line=null;
                    while((line=bufferedReader.readLine())!=null){
                        stringBuilder.append(line);
                    }
                } catch (MalformedURLException e) {
                    Intent intent=new Intent();
                    intent.setAction("errorAction");
                    sendBroadcast(intent);
                } catch (IOException e) {
                    Intent intent=new Intent();
                    intent.setAction("errorAction");
                    sendBroadcast(intent);
                }
                finally {
                    try{
                        if(bufferedReader!=null){
                            bufferedReader.close();
                        }
                    }
                    catch(IOException e){
                        e.printStackTrace();
                    }
                }
                String weatherInformation=stringBuilder.toString();
//                System.out.println(weatherInformation);
                int flag=bundle.getInt("refresh",-1);
                int alarmFlag=bundle.getInt("alarm",-1);
                if(alarmFlag==1){
//                    System.out.println("=============notificate===========");
                    String temp=DataUtils.getExplicitWeather(weatherInformation).getTemp0();
                    notification=new Notification.Builder(GetWeatherService.this)
                            .setAutoCancel(true)
                            .setTicker("有新的天气消息")
                            .setSmallIcon(R.mipmap.weather_icon)
                            .setContentText("一条新的天气消息")
                            .setContentText("今天气温是" + temp + ",请注意增减衣服哦~")
                            .build();
                    notificationManager.notify(NOTIFICATION_ID,notification);
                }
                else if(alarmFlag==-1){
                    if(flag==-1){
                        Intent intent=new Intent();
                        intent.setAction("weatherInfoAction");
                        Bundle bundle=new Bundle();
                        bundle.putString("weatherInfo",weatherInformation);
                        intent.putExtra("weatherInfoBundle", bundle);
                        sendBroadcast(intent);
                    }
                    else if(flag==1){
                        Intent intent=new Intent();
                        intent.setAction("refreshAction");
                        Bundle bundle=new Bundle();
                        bundle.putString("weatherInfo",weatherInformation);
                        intent.putExtra("weatherInfoBundle", bundle);
                        sendBroadcast(intent);
                    }
                }
            }
        }.start();
    }
}
