package com.trace.weather.utils;

import android.app.Application;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.trace.weather.activity.SetCityActivity;
import com.trace.weather.domain.Weather;
import com.trace.weather.service.GetWeatherService;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by dell-pc on 2015/11/14.
 */
public class LocationApplication extends Application{
    public LocationClient locationClient;
    private MyLocationListener locationListener;
    public Vibrator vibrator;
    private final String ak="AsoNjqLwHAcOMGgsoRQFaiQm";
    private final String type="json";
    public static boolean alarmFlag=false;
    private Weather weather=new Weather();
    StringBuilder stringBuilder;
    BufferedReader bufferedReader;
    String info;
    String[] provinces=SetCityActivity.province;
    String[][] cities=SetCityActivity.city;
    String[] number=SetCityActivity.number;
    private final String mCode="22:6C:5C:20:D0:FF:86:6E:00:CF:A0:C9:3A:3B:09:2B:93:9B:D5:A1:com.trace.weather";

    @Override
    public void onCreate() {
//        System.out.println(alarmFlag+"================");
        super.onCreate();
        locationClient=new LocationClient(this.getApplicationContext());
        locationListener=new MyLocationListener();
        locationClient.registerLocationListener(locationListener);
        vibrator =(Vibrator)getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);
    }

    public class MyLocationListener implements BDLocationListener{

        /**
         * 实现实时位置回调监听
         */
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            double longitude,latitude;
            longitude=bdLocation.getLongitude();
            latitude=bdLocation.getLatitude();
            String lon=String.valueOf(longitude);
            String lat=String.valueOf(latitude);
            getLocation(lon, lat);
        }
    }

    public void getLocation(final String longitude,final String latitude){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String path="http://api.map.baidu.com/telematics/v3/reverseGeocoding?location="+longitude+","
                            +latitude+"&output="+type+"&ak="+ak+"&mcode="+mCode;
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
                    info=stringBuilder.toString();
                    setLocation();
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
            }
        }).start();
    }

    public void setLocation(){
        JSONTokener jsonTokener=new JSONTokener(info);
//        System.out.println(info);
        try {
            JSONObject object= (JSONObject) jsonTokener.nextValue();
            String cityStr=object.getString("city");
            String city=cityStr.substring(0,cityStr.length()-1); //去掉后缀“市”
            int n=0;
            for(int i=0;i<provinces.length;i++){
                for(int j=0;j<cities[i].length;j++){
                    if(cities[i][j].equals(city)){
                        for(int k=0;k<i;k++){
                            n=n+cities[k].length;
                        }
                        n=n+j;
                    }
                }
            }
            String cityCode=number[n];
            weather.setCityCode(cityCode);
            if(!alarmFlag) {
                Intent intent = new Intent(this, GetWeatherService.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("locationInfo", weather);
                bundle.putInt("refresh", 1);
                intent.putExtras(bundle);
                startService(intent);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
