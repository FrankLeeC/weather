package com.trace.weather.activity;


import android.app.ActionBar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.percent.PercentRelativeLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.baidu.location.LocationClient;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.trace.weather.R;
import com.trace.weather.domain.Weather;
import com.trace.weather.service.StartService;
import com.trace.weather.service.GetWeatherService;
import com.trace.weather.utils.DataUtils;
import com.trace.weather.utils.LocationApplication;
import com.trace.weather.view.PercentLinearLayout;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by dell-pc on 2015/10/29.
 */
public class ShowActivity extends Activity implements SwipeRefreshLayout.OnRefreshListener,GestureDetector.OnGestureListener{
    private ViewFlipper viewFlipper; //切换页面的viewFlipper
    private PercentLinearLayout chartView;
    private PercentRelativeLayout imageRelativeLayout;
    private PercentLinearLayout first,second,third; //viewFlipper包含这三个PercentLinearLayout，分别呈现三天的信息
    private TextView firstCity,firstTemp,firstDay,firstWeather,firstMonth,firstDate,firstTomorrow,firstTomorrowTemp,
            secondCity,secondTemp,secondDay,secondWeather,secondMonth,secondDate,secondWhichDay,secondPage,
            thirdCity,thirdTemp,thirdDay,thirdWeather,thirdMonth,thirdDate,thirdWhichDay,thirdPage;
    private ImageView firstImage,secondImage,thirdImage;
    private ImageView chartFirstImage,chartSecondImage,chartThirdImage,chartFourthImage,chartFifthImage;
    private GestureDetector detector; //手势
    private LineChart chart; //折线图
    //刷新flag，1.若load成功，则执行onreate里的刷新，
    // 若失败，则执行loadSavedWeather里面的刷新，并且onCreate的刷新不执行
    private boolean refreshFlag=false;
    //设置城市flag,当设置城市完毕，并且返回当前activity显示天气后，销毁setCityActivity
    private boolean setCityFlag=false;

    public LocationClient locationClient; //GPS定位

    public AlertDialog.Builder alertDialog; //GPS定位时弹出这个消息提示框
    public AlertDialog dialog; //获取alertDialog的父类，用来关闭alertDialog

    RelativeLayout showLayout;

    int colorFlag=0; //背景色设置的标识

    public static String day,tomorrow,dayAfterTomorrow; //今天，明天，后天的周几
    public static String dateTomorrow,dateAfterTomorrow; //明天，后天的日期
    Calendar calendar=Calendar.getInstance();
    AlarmManager alarmManager;
    ActionBar actionBar;
    private long exitTime=0; //再按一次退出的flag

    private static final int REFRESH_READY=0x123; //当可以刷新时的标量，用于handler
    private SwipeRefreshLayout swipeRefreshLayout; //一个可以下拉刷新的组件

    public static SharedPreferences sharedPreferences; //加载保存的配置
    public static SharedPreferences.Editor editor; //写入配置信息
    private Weather weather; //天气实例
    private String weatherInfo; //获取到的天气信息
    getWeatherReceiver receiver; //获取天气信息的receiver
    RefreshWeatherReceiver refreshWeatherReceiver; //获取刷新后天气的receiver
    ErrorReceiver errorReceiver; //获取数据失败的receiver
    public static boolean flag=false;
    private String code; //刷新时用来传城市代码

    IntentFilter refreshIntentFilter; //receiver的拦截器
    IntentFilter intentFilter;
    IntentFilter errorFileter;

    //刷新的handler
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case REFRESH_READY:
                    Weather newWeather = new Weather(); //将当前城市代码传给service
                    newWeather.setCityCode(code);
 //                   System.out.println(newWeather.getCityCode()+"==================");
                    Intent newIntent = new Intent(ShowActivity.this, GetWeatherService.class);
                    Bundle newBundle = new Bundle();
                    newBundle.putSerializable("locationInfo", newWeather);
                    newBundle.putInt("refresh", 1);
                    newIntent.putExtras(newBundle);
                    startService(newIntent);
                    swipeRefreshLayout.setRefreshing(false);
                    break;
            }
        }
    };

    final int[] background=new int[]{
            R.color.blue,R.color.blue_grey,R.color.bright_green,
            R.color.bright_orange,R.color.grey,
            R.color.light_blue,R.color.light_green,R.color.orange,
            R.color.pink,R.color.purple,R.color.yellow
    };

    final int[] backgroundImage=new int[]{
            R.drawable.background_simple1,R.drawable.background_simple2,R.drawable.background_simple3,
            R.drawable.background_simple4,R.drawable.background_cloud,R.drawable.background_sky
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather);

        //初始化viewFlipper
        viewFlipper= (ViewFlipper) findViewById(R.id.viewFlipper);
        chartView= (PercentLinearLayout) findViewById(R.id.chartView);
        imageRelativeLayout= (PercentRelativeLayout) findViewById(R.id.image);
        chart= (LineChart) findViewById(R.id.chart);
        chartFirstImage= (ImageView) findViewById(R.id.firstImage);
        chartSecondImage= (ImageView) findViewById(R.id.secondImage);
        chartThirdImage= (ImageView) findViewById(R.id.thirdImage);
        chartFourthImage= (ImageView) findViewById(R.id.fourthImage);
        chartFifthImage= (ImageView) findViewById(R.id.fifthImage);

        locationClient = ((LocationApplication)getApplication()).locationClient; //为locationClient赋值绑定
        showLayout= (RelativeLayout) findViewById(R.id.showLayout); //获取relativeLayout，改变背景色
        alertDialog=new AlertDialog.Builder(this).setMessage(R.string.load); //为当前activity实例化弹窗
        //注册receiver
        intentFilter=new IntentFilter();
        intentFilter.addAction("weatherInfoAction");
        receiver=new getWeatherReceiver();
        registerReceiver(receiver, intentFilter);
        //注册receiver
        refreshIntentFilter=new IntentFilter();
        refreshIntentFilter.addAction("refreshAction");
        refreshWeatherReceiver=new RefreshWeatherReceiver();
        registerReceiver(refreshWeatherReceiver, refreshIntentFilter);
        //注册receiver
        errorFileter=new IntentFilter();
        errorFileter.addAction("errorAction");
        errorReceiver=new ErrorReceiver();
        registerReceiver(errorReceiver,errorFileter);

        Intent alarmIntent=new Intent(ShowActivity.this,StartService.class);
        PendingIntent pi=PendingIntent.getService(this,0,alarmIntent,0);
        Calendar alarmCalendar=Calendar.getInstance();
        alarmCalendar.set(Calendar.HOUR_OF_DAY, 7);
        alarmCalendar.set(Calendar.MINUTE,30);
        alarmCalendar.set(Calendar.SECOND, 0);
        alarmCalendar.set(Calendar.MILLISECOND,0);
        alarmManager= (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmCalendar.getTimeInMillis(), 1000 * 60 * 60 * 24, pi);

//        Intent alarmIntent=new Intent(ShowActivity.this,StartService.class);
//        PendingIntent pi=PendingIntent.getService(this,0,alarmIntent,0);
//        Calendar alarmCalendar=Calendar.getInstance();
//        alarmCalendar.set(Calendar.HOUR_OF_DAY, 12);
//        alarmCalendar.set(Calendar.MINUTE,16);
//        alarmCalendar.set(Calendar.SECOND, 0);
//        alarmCalendar.set(Calendar.MILLISECOND,0);
//        alarmManager= (AlarmManager) getSystemService(ALARM_SERVICE);
//        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmCalendar.getTimeInMillis(), 1000 * 60 * 60 * 24, pi);

        //加载配置
        sharedPreferences=getSharedPreferences("weather", MODE_PRIVATE);

        //获取当前是星期几
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        day=DataUtils.getDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK), 1);
        tomorrow=DataUtils.getDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK), 2);
        dayAfterTomorrow=DataUtils.getDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK), 3);
        //获取当天是几号
        dateTomorrow=DataUtils.getNextDay(1);
        dateAfterTomorrow=DataUtils.getNextDay(2);

        //初始化三个PercentLinearLayout，并且将他们的Touch事件交给GestureDetector来处理，否则无法滑动
        first= (PercentLinearLayout) findViewById(R.id.first);
//        second= (PercentLinearLayout) findViewById(R.id.second);
//        third= (PercentLinearLayout) findViewById(R.id.third);
        //初始化三个页面的组件
        firstCity= (TextView) findViewById(R.id.acity);
        firstTemp=(TextView) findViewById(R.id.atemp);
        firstImage= (ImageView) findViewById(R.id.aimage);
        firstDay=(TextView) findViewById(R.id.aday);
        firstWeather=(TextView) findViewById(R.id.aweather);
        firstMonth=(TextView) findViewById(R.id.amonth);
        firstDate=(TextView) findViewById(R.id.adate);
        firstTomorrow=(TextView) findViewById(R.id.atomorrow);
        firstTomorrowTemp=(TextView) findViewById(R.id.atomorrowTemp);

//        secondCity= (TextView) findViewById(R.id.bcity);
//        secondTemp=(TextView) findViewById(R.id.btemp);
//        secondImage= (ImageView) findViewById(R.id.bimage);
//        secondDay=(TextView) findViewById(R.id.bday);
//        secondWeather=(TextView) findViewById(R.id.bweather);
//        secondMonth=(TextView) findViewById(R.id.bmonth);
//        secondDate=(TextView) findViewById(R.id.bdate);
//        secondWhichDay=(TextView) findViewById(R.id.bwhichDay);
//        secondPage=(TextView) findViewById(R.id.bwhichPage);
//
//        thirdCity= (TextView) findViewById(R.id.ccity);
//        thirdTemp=(TextView) findViewById(R.id.ctemp);
//        thirdImage= (ImageView) findViewById(R.id.cimage);
//        thirdDay=(TextView) findViewById(R.id.cday);
//        thirdWeather=(TextView) findViewById(R.id.cweather);
//        thirdMonth=(TextView) findViewById(R.id.cmonth);
//        thirdDate=(TextView) findViewById(R.id.cdate);
//        thirdWhichDay=(TextView) findViewById(R.id.cwhichDay);
//        thirdPage=(TextView) findViewById(R.id.cwhichPage);

        //初始化手势
        detector=new GestureDetector(this,this);

        first.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ShowActivity.this.detector.onTouchEvent(event);
                return true;
            }
        });
        chart.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ShowActivity.this.detector.onTouchEvent(event);
                return true;
            }
        });
//        second.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                ShowActivity.this.detector.onTouchEvent(event);
//                return true;
//            }
//        });
//        third.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                ShowActivity.this.detector.onTouchEvent(event);
//                return true;
//            }
//        });

        //初始化下拉刷新组件
        swipeRefreshLayout= (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(Color.BLUE);

        actionBar=getActionBar();
        loadSavedWeather();
        if(!refreshFlag){
            locationClient.start();
            alertDialog.create();
            dialog=alertDialog.show();
            onPause();
        }
    }

    //按退出键时调用该方法
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK&&event.getAction()==event.ACTION_DOWN){
            if((System.currentTimeMillis()-exitTime)>2000){
                Toast.makeText(ShowActivity.this,"再按一次退出程序",Toast.LENGTH_SHORT).show();
                exitTime=System.currentTimeMillis();
            }
            else {
                if(refreshFlag){
                    GetWeatherService.instance.stopSelf();
                    StartService.instance.stopSelf();
                }
                if(setCityFlag){
                    SetCityActivity.instance.finish();
                }
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=new MenuInflater(this);
        inflater.inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.setCity:
                setCityFlag=true;
                Intent cityIntent=new Intent(ShowActivity.this,SetCityActivity.class);
                cityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(cityIntent);
                onPause();
                break;
            case R.id.setColor:
                Intent colorIntent=new Intent(ShowActivity.this,SetColorActivity.class);
                colorIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivityForResult(colorIntent, 0);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dialog.dismiss();
        unregisterReceiver(refreshWeatherReceiver);
        unregisterReceiver(receiver);
        unregisterReceiver(errorReceiver);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        setContentView(R.layout.weather);

        showLayout= (RelativeLayout) findViewById(R.id.showLayout);
        colorFlag=SetColorActivity.colorPosition;
//        System.out.println(colorFlag + "show==================================");
        if(colorFlag!=-1&&colorFlag<11){
//            System.out.println(colorFlag+"in if=====================");
            showLayout.setBackgroundResource(background[colorFlag]);
//            Toast.makeText(this, " 设置成功", Toast.LENGTH_SHORT).show();
        }
        else if(colorFlag!=-1&&colorFlag>10){
//            System.out.println(colorFlag+"in esle if=====================");
            showLayout.setBackgroundResource(backgroundImage[colorFlag-11]);
        }


        //获取当前是星期几
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        day=DataUtils.getDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK), 1);
        tomorrow=DataUtils.getDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK), 2);
        dayAfterTomorrow=DataUtils.getDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK), 3);
        //获取当天是几号
        dateTomorrow=DataUtils.getNextDay(1);
        dateAfterTomorrow=DataUtils.getNextDay(2);
        //初始化viewFlipper
        viewFlipper= (ViewFlipper) findViewById(R.id.viewFlipper);
        chartView= (PercentLinearLayout) findViewById(R.id.chartView);
        imageRelativeLayout= (PercentRelativeLayout) findViewById(R.id.image);
        first= (PercentLinearLayout) findViewById(R.id.first);
        chart= (LineChart) findViewById(R.id.chart);
        chartFirstImage= (ImageView) findViewById(R.id.firstImage);
        chartSecondImage= (ImageView) findViewById(R.id.secondImage);
        chartThirdImage= (ImageView) findViewById(R.id.thirdImage);
        chartFourthImage= (ImageView) findViewById(R.id.fourthImage);
        chartFifthImage= (ImageView) findViewById(R.id.fifthImage);
//        second= (PercentLinearLayout) findViewById(R.id.second);
//        third= (PercentLinearLayout) findViewById(R.id.third);

        firstCity= (TextView) findViewById(R.id.acity);
        firstTemp=(TextView) findViewById(R.id.atemp);
        firstImage= (ImageView) findViewById(R.id.aimage);
        firstDay=(TextView) findViewById(R.id.aday);
        firstWeather=(TextView) findViewById(R.id.aweather);
        firstMonth=(TextView) findViewById(R.id.amonth);
        firstDate=(TextView) findViewById(R.id.adate);
        firstTomorrow=(TextView) findViewById(R.id.atomorrow);
        firstTomorrowTemp=(TextView) findViewById(R.id.atomorrowTemp);

//        secondCity= (TextView) findViewById(R.id.bcity);
//        secondTemp=(TextView) findViewById(R.id.btemp);
//        secondImage= (ImageView) findViewById(R.id.bimage);
//        secondDay=(TextView) findViewById(R.id.bday);
//        secondWeather=(TextView) findViewById(R.id.bweather);
//        secondMonth=(TextView) findViewById(R.id.bmonth);
//        secondDate=(TextView) findViewById(R.id.bdate);
//        secondWhichDay=(TextView) findViewById(R.id.bwhichDay);
//        secondPage=(TextView) findViewById(R.id.bwhichPage);
//
//        thirdCity= (TextView) findViewById(R.id.ccity);
//        thirdTemp=(TextView) findViewById(R.id.ctemp);
//        thirdImage= (ImageView) findViewById(R.id.cimage);
//        thirdDay=(TextView) findViewById(R.id.cday);
//        thirdWeather=(TextView) findViewById(R.id.cweather);
//        thirdMonth=(TextView) findViewById(R.id.cmonth);
//        thirdDate=(TextView) findViewById(R.id.cdate);
//        thirdWhichDay=(TextView) findViewById(R.id.cwhichDay);
//        thirdPage=(TextView) findViewById(R.id.cwhichPage);

        detector=new GestureDetector(this,this);

        first.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ShowActivity.this.detector.onTouchEvent(event);
                return true;
            }
        });
        chart.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ShowActivity.this.detector.onTouchEvent(event);
                return true;
            }
        });
//        second.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                ShowActivity.this.detector.onTouchEvent(event);
//                return true;
//            }
//        });
//        third.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                ShowActivity.this.detector.onTouchEvent(event);
//                return true;
//            }
//        });
        swipeRefreshLayout= (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(Color.BLUE);
        getExplicitWeather();
    }



    @Override
    public void onRefresh() {
        refreshFlag = true;
        handler.sendEmptyMessageDelayed(REFRESH_READY, 2000);
    }



    public void next(){
        viewFlipper.showNext();
    }

    public void prev(){
        viewFlipper.showPrevious();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return detector.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if(e1.getX()-e2.getX()>50){
            next();
        }
        else if(e2.getX()-e1.getX()>50){
            prev();
        }

        return true;

    }


    public class ErrorReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            try{
                dialog.dismiss();
                Toast.makeText(ShowActivity.this,"刷新成功",Toast.LENGTH_SHORT).show();
            }catch(Exception e){
                Toast.makeText(ShowActivity.this,"刷新成功",Toast.LENGTH_SHORT).show();
            }
        }
    }


    public class RefreshWeatherReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            refreshFlag=true;
            weatherInfo= intent.getBundleExtra("weatherInfoBundle").getString("weatherInfo");
            getExplicitWeather();
            try{
                dialog.dismiss();
                Toast.makeText(ShowActivity.this,"刷新成功",Toast.LENGTH_SHORT).show();
            }catch(Exception e){
                Toast.makeText(ShowActivity.this,"刷新成功",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class getWeatherReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            intent.setClass(context, ShowActivity.class);
            //这个设置可以让showActivity被唤醒，并且不会从oncreate开始，而是执行onRestart方法
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            refreshFlag=true;
            weatherInfo=intent.getBundleExtra("weatherInfoBundle").getString("weatherInfo");
            startActivity(intent);
            SetCityActivity.instance.finish();

        }
    }

    public void loadSavedWeather(){
        weather=DataUtils.loadSavedWeather();
        colorFlag=weather.getColorFlag();
        if(colorFlag<11){
            showLayout.setBackgroundResource(background[weather.getColorFlag()]);
        }
        else if(colorFlag>10){
            showLayout.setBackgroundResource(backgroundImage[colorFlag-11]);
        }
        code=weather.getCityCode();
        if(weather.getDate()!=null&&weather.getCity()!=null&&weather.getCityCode()!=null){
            weatherItem(weather, 1);
            imageItem(weather);
            drawLineChart(weather);

//            weatherItem(weather,2);
//            weatherItem(weather,3);
        }
        else{
            refreshFlag=true;
            locationClient.start();
            alertDialog.create();
            dialog=alertDialog.show();
            onPause();
        }
    }

    public void imageItem(Weather weather){
        chartView.setBackgroundColor(Color.WHITE);
        chartFirstImage.setImageResource(DataUtils.getImage(weather.getWeather1()));
        chartSecondImage.setImageResource(DataUtils.getImage(weather.getWeather2()));
        chartThirdImage.setImageResource(DataUtils.getImage(weather.getWeather3()));
        chartFourthImage.setImageResource(DataUtils.getImage(weather.getWeather4()));
        chartFifthImage.setImageResource(DataUtils.getImage(weather.getWeather5()));
    }

    public void drawLineChart(Weather weatherInChart){
        List<String> dataX=new ArrayList<>();
        List<Entry> dataYLow=new ArrayList<>();
        List<Entry> dataYHigh=new ArrayList<>();
            for(int i=0;i<5;i++){
                String date=DataUtils.getNextDay(i).substring(5);
                dataX.add(date);
            }
            for(int i=0;i<5;i++) {
                try {
                    String tempStr = (String) Weather.class.getMethod("getTemp" + i).invoke(weatherInChart);
                    String[] tempStrArray = tempStr.split("~");
                    String tempFirst = tempStrArray[0].substring(0, tempStrArray[0].length() - 1);
                    Integer tempLow = Integer.parseInt(tempFirst);
                    Entry entry = new Entry(tempLow, i);
                    dataYLow.add(entry);
                    String tempSecond=tempStrArray[1].substring(0,tempStrArray[1].length()-1);
                    Integer tempHigh=Integer.parseInt(tempSecond);
                    Entry entry1=new Entry(tempHigh,i);
                    dataYHigh.add(entry1);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }

        LineDataSet lineDataSet=new LineDataSet(dataYLow,null);
        lineDataSet.setLineWidth(3);//设置线宽
        lineDataSet.setCircleSize(5);//设置圆尺寸
        lineDataSet.setColor(Color.BLUE);//设置线条颜色
        lineDataSet.setCircleColor(Color.BLACK);//设置圆球的颜色
        lineDataSet.setDrawHighlightIndicators(false);//如果设置false,则不会显示纵横线
        lineDataSet.setHighlightEnabled(false);
        lineDataSet.setDrawHorizontalHighlightIndicator(false);
        lineDataSet.setValueTextSize(5);//设置字体大小
        LineDataSet lineDataSet1=new LineDataSet(dataYHigh,null);
        lineDataSet1.setLineWidth(3);//设置线宽
        lineDataSet1.setCircleSize(5);//设置圆尺寸
        lineDataSet1.setColor(Color.RED);//设置线条颜色
        lineDataSet1.setCircleColor(Color.BLACK);//设置圆球的颜色
        lineDataSet1.setDrawHighlightIndicators(false);//如果设置false,则不会显示纵横线
        lineDataSet1.setDrawHorizontalHighlightIndicator(false);
        lineDataSet1.setHighlightEnabled(false);
        lineDataSet1.setValueTextSize(5);//设置字体大小
        List<LineDataSet> lineDataSetList=new ArrayList<>();
        lineDataSetList.add(lineDataSet);
        lineDataSetList.add(lineDataSet1);
        LineData lineData=new LineData(dataX,lineDataSetList);
        chart.setDrawBorders(false);//设置是否添加边框
        chart.setDescription("五天的天气情况");//描述
        chart.setTouchEnabled(true);// 触摸
        chart.setDragEnabled(true);// 拖拽
        chart.setScaleEnabled(true);// 缩放
        chart.setData(lineData);
        chart.setBackgroundColor(Color.WHITE);
        chart.animateX(2000);
    }

    public void getExplicitWeather(){
        weather=DataUtils.getExplicitWeather(weatherInfo);
 //       System.out.println(weather.getCity()+"============================================");
        DataUtils.saveUsualCity(weather.getCity()); //保存常用城市
        code=weather.getCityCode();
        if(weather!=null){
            code=weather.getCityCode();
            DataUtils.saveWeather(weather,colorFlag);
            weatherItem(weather, 1);
            imageItem(weather);
//            weatherItem(weather,2);
//            weatherItem(weather,3);
            drawLineChart(weather);
        }
    }



    public void weatherItem(Weather weather,Integer number){
        if(number==1){
            firstCity.setText(weather.getCity());
            firstTemp.setText(weather.getTemp0());
            firstImage.setImageResource(DataUtils.getImage(weather.getWeather1()));
            firstDay.setText(day);
            firstWeather.setText(weather.getWeather1());
            firstMonth.setText(weather.getDate().substring(5, 8));
            firstDate.setText(weather.getDate().substring(8));
            firstTomorrow.setText("明天");
            firstTomorrowTemp.setText(weather.getTemp1());
        }
//        else if(number==2){
//            secondCity.setText(weather.getCity());
//            secondTemp.setText(weather.getTemp2());
//            secondImage.setImageResource(DataUtils.getImage(weather.getWeather2()));
//            secondDay.setText(tomorrow);
//            secondWeather.setText(weather.getWeather2());
//            secondMonth.setText(weather.getDate().substring(5,8));
//            secondDate.setText(dateTomorrow.substring(8));
//            secondWhichDay.setText("明天");
//            secondPage.setText("2/3");
//        }
//        else if(number==3){
//            thirdCity.setText(weather.getCity());
//            thirdTemp.setText(weather.getTemp3());
//            thirdImage.setImageResource(DataUtils.getImage(weather.getWeather3()));
//            thirdDay.setText(dayAfterTomorrow);
//            thirdWeather.setText(weather.getWeather3());
//            thirdMonth.setText(weather.getDate().substring(5,8));
//            thirdDate.setText(dateAfterTomorrow.substring(8));
//            thirdWhichDay.setText("后天");
//            thirdPage.setText("3/3");
//        }

    }

}
