package com.trace.weather.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.trace.weather.R;

import java.io.InputStream;

/**
 * Created by dell-pc on 2015/11/3.
 */
public class SetColorActivity extends Activity implements GestureDetector.OnGestureListener{
    public static int colorPosition=0;
    private int colorFlag=0; //记录当前浏览的是第几个color页面
    private ViewFlipper colorFlipper;
    private GestureDetector detector;
    ImageView blue,blue_grey,bright_green,bright_orange,grey,light_blue,light_green,orange,
            pink,purple,yellow,simple1,simple2,simple3,simple4,cloud,sky;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setcolor);
        Toast.makeText(this, "长按即可设置", Toast.LENGTH_LONG).show();
        detector=new GestureDetector(this,this);
        colorFlipper= (ViewFlipper) findViewById(R.id.colorFlipper);
        blue= (ImageView) findViewById(R.id.blue);
        blue_grey= (ImageView) findViewById(R.id.blue_grey);
        bright_green= (ImageView) findViewById(R.id.bright_green);
        bright_orange= (ImageView) findViewById(R.id.bright_orange);
        grey= (ImageView) findViewById(R.id.grey);
        light_blue= (ImageView) findViewById(R.id.light_blue);
        light_green= (ImageView) findViewById(R.id.light_green);
        orange= (ImageView) findViewById(R.id.orange);
        pink= (ImageView) findViewById(R.id.pink);
        purple= (ImageView) findViewById(R.id.purple);
        yellow= (ImageView) findViewById(R.id.yellow);
        simple1= (ImageView) findViewById(R.id.simple1);
        simple2= (ImageView) findViewById(R.id.simple2);
        simple3= (ImageView) findViewById(R.id.simple3);
        simple4= (ImageView) findViewById(R.id.simple4);
        cloud= (ImageView) findViewById(R.id.cloud);
        sky= (ImageView) findViewById(R.id.sky);

        //为了优化内存，此时才加载图片
        setImage();
        blue.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                detector.onTouchEvent(event);
                return false;
            }
        });
        blue_grey.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                detector.onTouchEvent(event);
                return false;
            }
        });
        bright_green.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                detector.onTouchEvent(event);
                return false;
            }
        });
        bright_orange.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                detector.onTouchEvent(event);
                return false;
            }
        });
        grey.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                detector.onTouchEvent(event);
                return false;
            }
        });
        light_blue.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                detector.onTouchEvent(event);
                return false;
            }
        });
        light_green.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                detector.onTouchEvent(event);
                return false;
            }
        });
        orange.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                detector.onTouchEvent(event);
                return false;
            }
        });
        pink.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                detector.onTouchEvent(event);
                return false;
            }
        });
        purple.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                detector.onTouchEvent(event);
                return false;
            }
        });
        yellow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                detector.onTouchEvent(event);
                return false;
            }
        });
        simple1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                detector.onTouchEvent(event);
                return false;
            }
        });
        simple2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                detector.onTouchEvent(event);
                return false;
            }
        });
        simple3.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                detector.onTouchEvent(event);
                return false;
            }
        });
        simple4.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                detector.onTouchEvent(event);
                return false;
            }
        });
        cloud.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                detector.onTouchEvent(event);
                return false;
            }
        });
        sky.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                detector.onTouchEvent(event);
                return false;
            }
        });

    }

    @Override
    protected void onDestroy() {
        recycleBitmap();
        super.onDestroy();

    }

    //内存回收
    public void recycleBitmap(){
        if(blue!=null&&blue.getDrawable()!=null){
            Bitmap bitmap=((BitmapDrawable)blue.getDrawable()).getBitmap();
            blue.setImageBitmap(null);
        }
        if(blue_grey!=null&&blue_grey.getDrawable()!=null){
            Bitmap bitmap=((BitmapDrawable)blue_grey.getDrawable()).getBitmap();
            blue_grey.setImageBitmap(null);
        }
        if(bright_green!=null&&bright_green.getDrawable()!=null){
            Bitmap bitmap=((BitmapDrawable)bright_green.getDrawable()).getBitmap();
            bright_green.setImageBitmap(null);
        }
        if(bright_orange!=null&&bright_orange.getDrawable()!=null){
            Bitmap bitmap=((BitmapDrawable)bright_orange.getDrawable()).getBitmap();
            bright_orange.setImageBitmap(null);
        }
        if(grey!=null&&grey.getDrawable()!=null){
            Bitmap bitmap=((BitmapDrawable)grey.getDrawable()).getBitmap();
            grey.setImageBitmap(null);
        }
        if(light_blue!=null&&light_blue.getDrawable()!=null){
            Bitmap bitmap=((BitmapDrawable)light_blue.getDrawable()).getBitmap();
            light_blue.setImageBitmap(null);
        }
        if(light_green!=null&&light_green.getDrawable()!=null){
            Bitmap bitmap=((BitmapDrawable)light_green.getDrawable()).getBitmap();
            light_green.setImageBitmap(null);
        }
        if(orange!=null&&orange.getDrawable()!=null){
            Bitmap bitmap=((BitmapDrawable)orange.getDrawable()).getBitmap();
            orange.setImageBitmap(null);
        }
        if(pink!=null&&pink.getDrawable()!=null){
            Bitmap bitmap=((BitmapDrawable)pink.getDrawable()).getBitmap();
            pink.setImageBitmap(null);
        }
        if(purple!=null&&purple.getDrawable()!=null){
            Bitmap bitmap=((BitmapDrawable)purple.getDrawable()).getBitmap();
            purple.setImageBitmap(null);
        }
        if(yellow!=null&&yellow.getDrawable()!=null){
            Bitmap bitmap=((BitmapDrawable)yellow.getDrawable()).getBitmap();
            yellow.setImageBitmap(null);
        }
        if(simple1!=null&&simple1.getDrawable()!=null){
            Bitmap bitmap=((BitmapDrawable)simple1.getDrawable()).getBitmap();
            simple1.setImageBitmap(null);
        }
        if(simple2!=null&&simple2.getDrawable()!=null){
            Bitmap bitmap=((BitmapDrawable)simple2.getDrawable()).getBitmap();
            simple2.setImageBitmap(null);
        }
        if(simple3!=null&&simple3.getDrawable()!=null){
            Bitmap bitmap=((BitmapDrawable)simple3.getDrawable()).getBitmap();
            simple3.setImageBitmap(null);
        }
        if(simple4!=null&&simple4.getDrawable()!=null){
            Bitmap bitmap=((BitmapDrawable)simple4.getDrawable()).getBitmap();
            simple4.setImageBitmap(null);
        }
        if(cloud!=null&&cloud.getDrawable()!=null){
            Bitmap bitmap=((BitmapDrawable)cloud.getDrawable()).getBitmap();
            cloud.setImageBitmap(null);
        }
        if(sky!=null&&sky.getDrawable()!=null){
            Bitmap bitmap=((BitmapDrawable)sky.getDrawable()).getBitmap();
            sky.setImageBitmap(null);
//                if(bitmap!=null){
//                bitmap.recycle();
//                bitmap=null;
//            }

        }

    }

    public static Bitmap readBitMap(Context context, int resId){
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        // 获取资源图片
        InputStream is = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, opt);
    }

    public void setImage(){

        blue.setImageBitmap(readBitMap(this,R.drawable.background_blue));
        blue_grey.setImageBitmap(readBitMap(this, R.drawable.background_blue_grey));
        bright_green.setImageBitmap(readBitMap(this,R.drawable.background_bright_green));
        bright_orange.setImageBitmap(readBitMap(this, R.drawable.background_bright_orange));
        grey.setImageBitmap(readBitMap(this, R.drawable.background_grey));
        light_blue.setImageBitmap(readBitMap(this, R.drawable.background_light_blue));
        light_green.setImageBitmap(readBitMap(this, R.drawable.background_light_green));
        orange.setImageBitmap(readBitMap(this, R.drawable.background_orange));
        pink.setImageBitmap(readBitMap(this, R.drawable.background_pink));
        purple.setImageBitmap(readBitMap(this, R.drawable.background_purple));
        yellow.setImageBitmap(readBitMap(this, R.drawable.background_yellow));
        simple1.setImageBitmap(readBitMap(this, R.drawable.background_simple1));
        simple2.setImageBitmap(readBitMap(this, R.drawable.background_simple2));
        simple3.setImageBitmap(readBitMap(this, R.drawable.background_simple3));
        simple4.setImageBitmap(readBitMap(this, R.drawable.background_simple4));
        cloud.setImageBitmap(readBitMap(this, R.drawable.background_cloud));
        sky.setImageBitmap(readBitMap(this, R.drawable.background_sky));
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
        colorPosition=colorFlag;
//        System.out.println(colorPosition+"===========================");
        Intent intent=new Intent(SetColorActivity.this,ShowActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.putExtra("colorFalg", colorFlag);
        startActivity(intent);
        SetColorActivity.this.finish();
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if((e1.getX()-e2.getX())>5){
            if(colorFlag==16){
                colorFlag=0;
            }
            else{
                colorFlag++;
            }
//            System.out.println(colorFlag+"fling==========================");
            colorFlipper.showNext();
        }
        else if((e2.getX()-e1.getX())>5){
            if(colorFlag==0){
                colorFlag=16;
            }
            else{
                colorFlag--;
            }
            colorFlipper.showPrevious();
        }
        return true;
    }
}
