package com.trace.weather.utils;

import android.content.SharedPreferences;

import com.trace.weather.R;
import com.trace.weather.activity.SetColorActivity;
import com.trace.weather.activity.ShowActivity;
import com.trace.weather.domain.Weather;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by dell-pc on 2015/11/2.
 */
public final class DataUtils {
    //当用户进入选择城市界面却没有选择城市直接返回时，由于showActivity被唤醒，会执行getExplicit,出现空指针，
    // 所以设置一个缓存天气，每次获得天气就赋值给它，若没有weatherinfo，则返回cacheWeather,在loadSaved和getExplicit里面都要赋值
    private static Weather cacheWeather;
    private static SharedPreferences sharedPreferences=ShowActivity.sharedPreferences;
    private static SharedPreferences.Editor editor=ShowActivity.editor; //写入配置信息
    private static String day=ShowActivity.day;
    private static String tomorrow=ShowActivity.tomorrow;
    private static String dayAfterTomorrow=ShowActivity.dayAfterTomorrow;
    private static String dateTomorrow=ShowActivity.dateTomorrow;
    private static String dateAfterTomorrow=ShowActivity.dateAfterTomorrow;
    private static Map<String,String> map=new LinkedHashMap<>();

    public final static String[][] city=new String[][]{
            {"北京","朝阳","顺义","怀柔","通州","昌平","延庆","丰台","石景山","大兴","房山","密云","门头沟","平谷","八达岭","佛爷顶",
                    "汤河口","密云上甸子","斋堂","霞云岭","北京城区","海淀"},
            {"天津","宝坻","东丽" ,"西青","北辰","蓟县", "汉沽", "静海", "津南", "塘沽", "大港", "武清", "宁河"},
            {"上海","宝山","嘉定","南汇","浦东","青浦","松江","奉贤","崇明","徐家汇","闵行","金山"},
            {"重庆","合川","南川","江津","万盛","渝北","北碚","巴南","长寿","黔江","万州天城","万州龙宝","涪陵","开县","城口","云阳",
                    "巫溪","奉节","巫山","潼南","垫江","梁平","忠县","石柱","大足","荣昌","铜梁","璧山","丰都","武隆","彭水","綦江",
                    "酉阳","秀山","沙坪坝","永川"},
            {"石家庄","张家口","承德","唐山","秦皇岛","沧州","衡水","邢台","邯郸","保定","廊坊"},
            {"郑州","新乡","许昌","平顶山","信阳","南阳","开封","洛阳","商丘","焦作","鹤壁","濮阳","周口","漯河","驻马店","三门峡",
                    "济源","安阳"},
            {"合肥","芜湖","淮南","马鞍山","安庆","宿州","阜阳","亳州","黄山","滁州","淮北","铜陵","宣城","六安","巢湖","池州","蚌埠"},
            {"杭州","舟山","湖州","嘉兴","金华","绍兴","台州","温州","丽水","衢州","宁波"},
            {"福州","泉州","漳州","龙岩","晋江","南平","厦门","宁德","莆田","三明"},
            {"兰州","平凉","庆阳","武威","金昌","嘉峪关","酒泉","天水","武都","临夏","合作","白银","定西","张掖"},
            {"广州","惠州","梅州","汕头","深圳","珠海","佛山","肇庆","湛江","江门","河源","清远","云浮","潮州","东莞","中山","阳江",
                    "揭阳","茂名","汕尾","韶关"},
            {"南宁","柳州","来宾","桂林","梧州","防城港","贵港","玉林","百色","钦州","河池","北海","崇左","贺州"},
            {"贵阳","安顺","都匀","兴义","铜仁","毕节","六盘水","遵义","凯里"},
            {"昆明","红河","文山","玉溪","楚雄","普洱","昭通","临沧","怒江","香格里拉","丽江","德宏","景洪","大理","曲靖","保山"},
            {"呼和浩特","乌海","集宁","通辽","阿拉善左旗","鄂尔多斯","临河","锡林浩特","呼伦贝尔","乌兰浩特","包头","赤峰"},
            {"南昌","上饶","抚州","宜春","鹰潭","赣州","景德镇","萍乡","新余","九江","吉安"},
            {"武汉","黄冈","荆州","宜昌","恩施","十堰","神农架","随州","荆门","天门","仙桃","潜江","襄樊","鄂州","孝感","黄石","咸宁"},
            {"成都","自贡","绵阳","南充","达州","遂宁","广安","巴中","泸州","宜宾","内江","资阳","乐山","眉山","凉山","雅安","甘孜",
                    "阿坝","德阳","广元","攀枝花"},
            {"银川","中卫","固原","石嘴山","吴忠"},
            {"西宁","黄南","海北","果洛","玉树","海西","海东","海南"},
            {"济南","潍坊","临沂","菏泽","滨州","东营","威海","枣庄","日照","莱芜","聊城","青岛","淄博","德州","烟台","济宁","泰安"},
            {"西安","延安","榆林","铜川","商洛","安康","汉中","宝鸡","咸阳","渭南"},
            {"太原","临汾","运城","朔州","忻州","长治","大同","阳泉","晋中","晋城","吕梁"},
            {"乌鲁木齐","石河子","昌吉","吐鲁番","库尔勒","阿拉尔","阿克苏","喀什","伊宁","塔城","哈密","和田","阿勒泰","阿图什","博乐","克拉玛依"},
            {"拉萨","山南","阿里","昌都","那曲","日喀则","林芝"},
            {"海口","三亚","东方","临高","澄迈","儋州","昌江","白沙","琼中","定安","屯昌","琼海","文昌","保亭","万宁","陵水","西沙","南沙岛","乐东","五指山","琼山"},
            {"长沙","株洲","衡阳","郴州","常德","益阳","娄底","邵阳","岳阳","张家界","怀化","黔阳","永州","吉首","湘潭"},
            {"南京","镇江","苏州","南通","扬州","宿迁","徐州","淮安","连云港","常州","泰州","无锡","盐城"},
            {"哈尔滨","牡丹江","佳木斯","绥化","黑河","双鸭山","伊春","大庆","七台河","鸡西","鹤岗","齐齐哈尔","大兴安岭"},
            {"长春","延吉","四平","白山","白城","辽源","松原","吉林","通化"},
            {"沈阳","鞍山","抚顺","本溪","丹东","葫芦岛","营口","阜新","辽阳","铁岭","朝阳","盘锦","大连","锦州"},
            {"台北","高雄","台中"},
            {"香港","九龙","新界"},
            {"澳门"}
    };
    public final static String[] number=new String[]{
            "101010100","101010300","101010400","101010500","101010600","101010700","101010800",
            "101010900","101011000","101011100","101011200","101011300","101011400","101011500","101011600","101011700","101011800",
            "101011900","101012000","101012100","101012200","101010200", //北京
            "101030100","101030300","101030400","101030500","101030600","101031400","101030800","101030900","101031000",
            "101031100","101031200","101030200","101030700", //天津
            "101020100","101020300","101020500","101020600","101021300","101020800","101020900","101021000","101021100","101021200",
            "101020200","101020700", //上海
            "101040100","101040300","101040400","101040500","101040600","101040700","101040800","101040900","101041000","101041100",
            "101041200","101041300","101041400","101041500","101041600","101041700","101041800","101041900","101042000",
            "101042100","101042200","101042300","101042400","101042500","101042600","101042700","101042800","101042900",
            "101043000","101043100","101043200","101043300","101043400","101043600","101043700","101040200", //重庆
            "101090101","101090301","101090402","101090501","101091101","101090701","101090801","101090901","101091001","101090201",
            "101090601", //河北
            "101180101","101180301","101180401","101180501","101180601","101180701","101180801","101180901","101181001","101181101",
            "101181201","101181301","101181401","101181501","101181601","101181701","101181801","101180201", //河南
            "101220101","101220301","101220401","101220501","101220601","101220701","101220801","101220901","101221001","101221101",
            "101221201","101221301","101221401","101221501","101221601","101221701","101220201", //安徽
            "101210101","101211101","101210201","101210301","101210901","101210501","101210601","101210701","101210801","101211001",
            "101210401", //浙江
            "101230101","101230501","101230601","101230701","101230509","101230901","101230201","101230301","101230401","101230801", //福建
            "101160101","101160301","101160401","101160501","101160601","101161401","101160801","101160901","101161001","101161101",
            "101161201","101161301","101160201","101160701", //甘肃
            "101280101","101280301","101280401","101280501","101280601","101280701","101280800","101280901","101281001","101281101",
            "101281201","101281301","101281401","101281501","101281601","101281701","101281801","101281901","101282001",
            "101282101","101280201", //广东
            "101300101","101300301","101300401","101300501","101300601","101301401","101300801","101300901","101301001","101301101",
            "101301201","101301301","101300201","101300701", //广西
            "101260101","101260301","101260401","101260906","101260601","101260701","101260801","101260201","101260501", //贵州
            "101290101","101290301","101290601","101290701","101290801","101290901","101291001","101291101","101291201","101291301",
            "101291401","101291501","101291601","101290201","101290401","101290501", //云南
            "101080101","101080301","101080401","101080501","101081201","101080701","101080801","101080901","101081000","101081101",
            "101080201","101080601", //内蒙古
            "101240101","101240301","101240401","101240501","101241101","101240701","101240801","101240901","101241001","101240201",
            "101240601", //江西
            "101200101","101200501","101200801","101200901","101201001","101201101","101201201","101201301","101201401","101201501",
            "101201601","101201701","101200201","101200301","101200401","101200601","101200701", //湖北
            "101270101","101270301","101270401","101270501","101270601","101270701","101270801","101270901","101271001","101271101",
            "101271201","101271301","101271401","101271501","101271601","101271701","101271801","101271901","101272001",
            "101272101","101270201", //四川
            "101170101","101170501","101170401","101170201","101170301", //宁夏
            "101150101","101150301","101150801","101150501","101150601","101150701","101150201","101150401", //青海
            "101120101","101120601","101120901","101121001","101121101","101121201","101121301","101121401","101121501","101121601",
            "101121701","101120201","101120301","101120401","101120501","101120701","101120801", //山东
            "101110101","101110300","101110401","101111001","101110601","101110701","101110801","101110901","101110200","101110501", //陕西
            "101100101","101100701","101100801","101100901","101101001","101100501","101100201","101100301","101100401","101100601",
            "101101100", //山西
            "101130101","101130301","101130401","101130501","101130601","101130701","101130801","101130901","101131001","101131101",
            "101131201","101131301","101131401","101131501","101131601","101130201", //新疆
            "101140101","101140301","101140701","101140501","101140601","101140201","101140401", //西藏
            "101310101","101310201","101310202","101310203","101310204","101310205","101310206","101310207","101310208","101310209",
            "101310210","101310211","101310212","101310214","101310215","101310216","101310217","101310220","101310221",
            "101310222","101310102", //海南
            "101250101","101250301","101250401","101250501","101250601","101250700","101250801","101250901","101251001","101251101",
            "101251201","101251301","101251401","101251501","101250201", //湖南
            "101190101","101190301","101190401","101190501","101190601","101191301","101190801","101190901","101191001","101191101",
            "101191201","101190201","101190701", //江苏
            "101050101","101050301","101050401","101050501","101050601","101051301","101050801","101050901","101051002","101051101",
            "101051201","101050201","101050701", //黑龙江
            "101060101","101060301","101060401","101060901","101060601","101060701","101060801","101060201","101060501", //吉林
            "101070101","101070301","101070401","101070501","101070601","101071401","101070801","101070901","101071001","101071101",
            "101071201","101071301","101070201","101070701", //辽林
            "101340101","101340201","101340401", //台湾
            "101320101","101320102","101320103", //香港
            "101330101" //澳门
    };

    public static void saveUsualCity(String city){
        Set<String> usualSet=sharedPreferences.getStringSet("usualSet", null);
        if(usualSet==null){
            usualSet=new LinkedHashSet<>(5);
        }
        List<String> usualList=new ArrayList<>(5);
        Iterator<String> iterator=usualSet.iterator();
        while(iterator.hasNext()){
            usualList.add(iterator.next());
        }
        if(!usualList.contains(city)){
            if(usualList.size()==5){
                usualList.set(0,city);
            }
            else{
                usualList.add(city);
            }
        }
        usualSet.clear();
        Iterator<String> ite=usualList.iterator();
        while(ite.hasNext()){
            usualSet.add(ite.next());
        }
//        System.out.println(usualSet.size()+"===========================");
        editor=sharedPreferences.edit();
        editor.putStringSet("usualSet",usualSet);
        editor.commit();
    }

    public static String getCodeByName(String city){
        makeCodeMap();
        return map.get(city);
    }

    public static String getCode(int i,int j){
        int position=0;
        for(int k=0;k<i;k++){
            position+=city[k].length;
        }
        position+=j;
        return number[position];
    }

    public static void makeCodeMap(){
        for(int i=0;i<city.length;i++){
            for(int j=0;j<city[i].length;j++){
                String code=getCode(i,j);
                map.put(city[i][j], code);
            }
        }
    }

    public static int getImage(String weather){
        if(weather.length()==1){
            if(weather.equals("霾")){
                return R.drawable.weather_wu;
            }
            else if(weather.equals("晴")){
                return R.drawable.weather_qin;
            }
            else if(weather.equals("雾")){
                return R.drawable.weather_wu;
            }
            else if(weather.equals("阴")){
                return R.drawable.weather_yin;
            }
        }

        else if(weather.length()>1&&weather.length()<3){
            if((weather.substring(weather.length()-2)).equals("暴雪")){
                return R.drawable.weather_baoxue;
            }
            else if((weather.substring(weather.length()-1)).equals("霾")){
                return R.drawable.weather_wu;
            }
            else if((weather.substring(weather.length()-1)).equals("晴")){
                return R.drawable.weather_qin;
            }
            else if((weather.substring(weather.length()-1)).equals("雾")){
                return R.drawable.weather_wu;
            }
            else if((weather.substring(weather.length()-1)).equals("阴")){
                return R.drawable.weather_yin;
            }
            else if((weather.substring(weather.length()-2)).equals("暴雨")){
                return R.drawable.weather_dayu;
            }
            else if((weather.substring(weather.length()-2)).equals("大雪")){
                return R.drawable.weather_daxue;
            }
            else if((weather.substring(weather.length()-2)).equals("大雨")){
                return R.drawable.weather_dayu;
            }
            else if((weather.substring(weather.length()-2)).equals("多云")){
                return R.drawable.weather_duoyun;
            }

            else if((weather.substring(weather.length()-2)).equals("冰雹")){
                return R.drawable.weather_bingbao;
            }
            else if((weather.substring(weather.length()-2)).equals("小雪")){
                return R.drawable.weather_xiaoxue;
            }
            else if((weather.substring(weather.length()-2)).equals("中雪")){
                return R.drawable.weather_zhongxue;
            }
            else if((weather.substring(weather.length()-2)).equals("小雨")){
                return R.drawable.weather_xiaoyu;
            }
            else if((weather.substring(weather.length()-2)).equals("中雨")){
                return R.drawable.weather_zhongyu;
            }

            else if((weather.substring(weather.length()-2)).equals("阵雪")){
                return R.drawable.weather_xiaoxue;
            }
            else if((weather.substring(weather.length()-2)).equals("阵雨")){
                return R.drawable.weather_leizhenyu;
            }
            else if((weather.substring(weather.length()-2)).equals("大雪")){
                return R.drawable.weather_daxue;
            }
            else if((weather.substring(weather.length()-2)).equals("中雪")){
                return R.drawable.weather_zhongxue;
            }
            else if((weather.substring(weather.length()-2)).equals("浮尘")){
                return R.drawable.weather_shachenbao;
            }
            else if((weather.substring(weather.length()-2)).equals("扬沙")){
                return R.drawable.weather_shachenbao;
            }
        }

        else if(weather.length()>2) {
            if ((weather.substring(weather.length() - 3)).equals("雨夹雪")) {
                return R.drawable.weather_yujiaxue;
            } else if ((weather.substring(weather.length() - 3)).equals("雷阵雨")) {
                return R.drawable.weather_leizhenyu;
            } else if ((weather.substring(weather.length() - 3)).equals("强沙尘暴")) {
                return R.drawable.weather_shachenbao;
            } else if ((weather.substring(weather.length() - 3)).equals("沙尘暴")) {
                return R.drawable.weather_shachenbao;
            } else if ((weather.substring(weather.length() - 2)).equals("暴雪")) {
                return R.drawable.weather_baoxue;
            } else if ((weather.substring(weather.length() - 2)).equals("暴雨")) {
                return R.drawable.weather_dayu;
            } else if ((weather.substring(weather.length() - 2)).equals("大雪")) {
                return R.drawable.weather_daxue;
            } else if ((weather.substring(weather.length() - 2)).equals("大雨")) {
                return R.drawable.weather_dayu;
            } else if ((weather.substring(weather.length() - 2)).equals("多云")) {
                return R.drawable.weather_duoyun;
            } else if ((weather.substring(weather.length() - 2)).equals("冰雹")) {
                return R.drawable.weather_bingbao;
            } else if ((weather.substring(weather.length() - 2)).equals("小雪")) {
                return R.drawable.weather_xiaoxue;
            } else if ((weather.substring(weather.length() - 2)).equals("中雪")) {
                return R.drawable.weather_zhongxue;
            } else if ((weather.substring(weather.length() - 2)).equals("小雨")) {
                return R.drawable.weather_xiaoyu;
            } else if ((weather.substring(weather.length() - 2)).equals("中雨")) {
                return R.drawable.weather_zhongyu;
            } else if ((weather.substring(weather.length() - 2)).equals("阵雪")) {
                return R.drawable.weather_xiaoxue;
            } else if ((weather.substring(weather.length() - 2)).equals("阵雨")) {
                return R.drawable.weather_leizhenyu;
            } else if ((weather.substring(weather.length() - 2)).equals("大雪")) {
                return R.drawable.weather_daxue;
            } else if ((weather.substring(weather.length() - 2)).equals("中雪")) {
                return R.drawable.weather_zhongxue;
            } else if ((weather.substring(weather.length() - 2)).equals("浮尘")) {
                return R.drawable.weather_shachenbao;
            } else if ((weather.substring(weather.length() - 2)).equals("扬沙")) {
                return R.drawable.weather_shachenbao;
            } else if ((weather.substring(weather.length() - 1)).equals("霾")) {
                return R.drawable.weather_wu;
            } else if ((weather.substring(weather.length() - 1)).equals("晴")) {
                return R.drawable.weather_qin;
            } else if ((weather.substring(weather.length() - 1)).equals("雾")) {
                return R.drawable.weather_wu;
            } else if ((weather.substring(weather.length() - 1)).equals("阴")) {
                return R.drawable.weather_yin;
            }
        }
        return R.drawable.weather_error;
    }

    public static String getDayOfWeek(Integer number,Integer which){
        switch (which){
            case 1:{ //返回当天
                switch (number){
                    case 1:{
                        return "周日";
                    }
                    case 2:{
                        return "周一";
                    }
                    case 3:{
                        return "周二";
                    }
                    case 4:{
                        return "周三";
                    }
                    case 5:{
                        return "周四";
                    }
                    case 6:{
                        return "周五";
                    }
                    case 7:{
                        return "周六";
                    }
                }
            }

            case 2:{ //返回明天
                switch (number){
                    case 1:{
                        return "周一";
                    }
                    case 2:{
                        return "周二";
                    }
                    case 3:{
                        return "周三";
                    }
                    case 4:{
                        return "周四";
                    }
                    case 5:{
                        return "周五";
                    }
                    case 6:{
                        return "周六";
                    }
                    case 7:{
                        return "周日";
                    }
                }
            }

            case 3:{ //返回后天
                switch (number){
                    case 1:{
                        return "周二";
                    }
                    case 2:{
                        return "周三";
                    }
                    case 3:{
                        return "周四";
                    }
                    case 4:{
                        return "周五";
                    }
                    case 5:{
                        return "周六";
                    }
                    case 6:{
                        return "周日";
                    }
                    case 7:{
                        return "周一";
                    }
                }
            }
        }
        return null;
    }

    //获取明天的日期
    public static String getNextDay(Integer number) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");;
        Calendar c = Calendar.getInstance();
        long t = c.getTimeInMillis();
        long l;
        if(number==0){
            l=t;
        }
        else if(number==1){
            l = t + 24 * 3600 * 1000;
        }
        else if(number==2){
            l = t + 2 * 24 * 3600 * 1000;
        }
        else if (number==3){
            l = t + 3 * 24 * 3600 * 1000;
        }
        else{
            l = t + 4 * 24 * 3600 * 1000;
        }
        Date d = new Date(l);
        String s = sdf.format(d);
        return s;
    }

    public static Weather loadSavedWeather(){
        ShowActivity.flag=true;
        String savedLocation=sharedPreferences.getString("location",null);
        String code=sharedPreferences.getString("code",null);
        String savedWeather1=sharedPreferences.getString("weather1",null);
        String savedTemp0=sharedPreferences.getString("temp0",null);
        String savedWeather2=sharedPreferences.getString("weather2",null);
        String savedTemp1=sharedPreferences.getString("temp1",null);
        String savedWeather3=sharedPreferences.getString("weather3",null);
        String savedTemp2=sharedPreferences.getString("temp2",null);
        String savedWeather4=sharedPreferences.getString("weather4",null);
        String savedTemp3=sharedPreferences.getString("temp3",null);
        String savedWeather5=sharedPreferences.getString("weather5",null);
        String savedTemp4=sharedPreferences.getString("temp4",null);
        String savedDay=sharedPreferences.getString("day",null);
        String savedTomorrow=sharedPreferences.getString("tomorrow",null);
        String savedDayAfterTomorrow=sharedPreferences.getString("dayAfterTomorrow", null);
        String savedDate=sharedPreferences.getString("date", null);
        String savedDateTomorrow=sharedPreferences.getString("dateTomorrow",null);
        String savedDateAfterTomorrow=sharedPreferences.getString("dateAfterTomorrow",null);
        int savedColorFlag=sharedPreferences.getInt("colorFlag",0);
        SetColorActivity.colorPosition=savedColorFlag;
        Weather weather=new Weather(savedLocation,code,savedTemp0,savedTemp1,savedTemp2,savedTemp3,savedTemp4,
                savedWeather1,savedWeather2,savedWeather3,savedWeather4,savedWeather5,savedDay,savedTomorrow,
                savedDayAfterTomorrow,savedDate,savedDateTomorrow,savedDateAfterTomorrow,savedColorFlag);
        cacheWeather=weather;
        return weather;
    }

    public static void saveWeather(Weather weather,int colorFlag){
        editor=sharedPreferences.edit();
        editor.putString("location",weather.getCity());
        editor.putString("code",weather.getCityCode());
        editor.putString("weather1",weather.getWeather1());
        editor.putString("temp0",weather.getTemp0());
        editor.putString("weather2",weather.getWeather2());
        editor.putString("temp1",weather.getTemp1());
        editor.putString("weather3",weather.getWeather3());
        editor.putString("temp2",weather.getTemp2());
        editor.putString("weather4",weather.getWeather4());
        editor.putString("temp3",weather.getTemp3());
        editor.putString("weather5",weather.getWeather5());
        editor.putString("temp4",weather.getTemp4());
        editor.putString("day",weather.getDay());
        editor.putString("tomorrow",weather.getTomorrow());
        editor.putString("dayAfterTomorrow",weather.getDayAfterTomorrow());
        editor.putString("date",weather.getDate());
        editor.putString("dateTomorrow",weather.getDateTomorrow());
        editor.putString("dateAfterTomorrow",weather.getDateAfterTomorrow());
        editor.putInt("colorFlag",colorFlag);
        editor.commit();
    }

    public static Weather getExplicitWeather(String weatherInfo){
        Weather weather=new Weather();
        if(weatherInfo==null){
            return cacheWeather;
        }
        else {
            JSONTokener jsonTokener=new JSONTokener(weatherInfo);
            try {
                JSONObject object= (JSONObject) jsonTokener.nextValue();
                JSONObject weatherObject=object.getJSONObject("weatherinfo");
                weather.setCity(weatherObject.getString("city"));
                weather.setCityCode(weatherObject.get("cityid").toString());
                weather.setTemp0(weatherObject.getString("temp1"));
                weather.setTemp1(weatherObject.getString("temp2"));
                weather.setTemp2(weatherObject.getString("temp3"));
                weather.setTemp3(weatherObject.getString("temp4"));
                weather.setTemp4(weatherObject.getString("temp5"));
                weather.setWeather1(weatherObject.getString("weather1"));
                weather.setWeather2(weatherObject.getString("weather2"));
                weather.setWeather3(weatherObject.getString("weather3"));
                weather.setWeather4(weatherObject.getString("weather4"));
                weather.setWeather5(weatherObject.getString("weather5"));
                weather.setDay(day);
                weather.setTomorrow(tomorrow);
                weather.setDateAfterTomorrow(dayAfterTomorrow);
                weather.setDate(weatherObject.get("date_y").toString());
                weather.setDateTomorrow(dateTomorrow);
                weather.setDateAfterTomorrow(dateAfterTomorrow);
                cacheWeather=weather;
                return weather;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
