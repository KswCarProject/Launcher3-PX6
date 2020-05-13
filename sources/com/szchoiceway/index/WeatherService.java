package com.szchoiceway.index;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.choiceway.index.IWeatherService;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WeatherService extends Service {
    protected static final int GET_WEATHER = 292;
    protected static final int QUIT_DOWNLOAD = 293;
    private static final String TAG = "WeatherService";
    protected static final int UPDATE = 289;
    /* access modifiers changed from: private */
    public long dayTimer = 0;
    /* access modifiers changed from: private */
    public long iRequestUpdateTime = 0;
    private LauncherApplication mApp = null;
    private ServiceStub mBinder = new ServiceStub(this);
    /* access modifiers changed from: private */
    public Handler mDownHandler = null;
    /* access modifiers changed from: private */
    public SharedPreferences.Editor mEditor = null;
    private LocationClient mLocationClient;
    /* access modifiers changed from: private */
    public SharedPreferences mSettings = null;
    private WeatherThread mThread = null;
    public BDLocationListener myListener = new MyLocationListener();
    boolean otherWayGetWeather = false;
    /* access modifiers changed from: private */
    public long realTimer = 0;
    /* access modifiers changed from: private */
    public String strCityCode = "101280601";
    /* access modifiers changed from: private */
    public String strCityName = "深圳";
    /* access modifiers changed from: private */
    public String strCurWeather = "20°C";
    /* access modifiers changed from: private */
    public String strCurWeatherInfor = "东北风  1级 湿度：80%";
    /* access modifiers changed from: private */
    public String strDayWeather = "11°C~20°C";
    /* access modifiers changed from: private */
    public String strLastCityCode = "101280601";
    /* access modifiers changed from: private */
    public String strUpdateData = "2015年5月13日";
    /* access modifiers changed from: private */
    public String strUpdateTimer = "";
    /* access modifiers changed from: private */
    public String strWeatherIconStr = "0";
    /* access modifiers changed from: private */
    public String strWeatherInfor = "晴朗";

    public void onCreate() {
        Log.d(TAG, "onCreate");
        this.realTimer = 0;
        this.dayTimer = 0;
        this.mApp = (LauncherApplication) getApplication();
        this.mSettings = this.mApp.getSetting();
        this.mEditor = this.mApp.getEditor();
        loadLastWeatherValue();
        this.mThread = new WeatherThread();
        this.mThread.start();
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "onCreate");
        if (this.mDownHandler != null) {
            this.mDownHandler.sendEmptyMessage(GET_WEATHER);
        }
        super.onCreate();
        if (isLunarSetting()) {
            initLocation();
            this.mLocationClient = new LocationClient(getApplicationContext());
            this.mLocationClient.registerLocationListener(this.myListener);
        }
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        return 1;
    }

    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
        if (this.mDownHandler != null) {
            this.mDownHandler.removeCallbacks((Runnable) null);
            this.mDownHandler.sendEmptyMessage(QUIT_DOWNLOAD);
        }
        this.mThread = null;
    }

    public IBinder onBind(Intent intent) {
        return this.mBinder;
    }

    private void loadLastWeatherValue() {
        if (this.mSettings != null) {
            this.strCityCode = this.mSettings.getString("CityCode", "101280601");
            this.strCityName = this.mSettings.getString("CityName", "深圳");
            this.strCurWeather = this.mSettings.getString("CurWeather", "20°C");
            this.strDayWeather = this.mSettings.getString("DayWeather", "11°C/20°C");
            this.strUpdateData = this.mSettings.getString("UpdateData", "2015年1月13日");
            this.strWeatherIconStr = this.mSettings.getString("WeatherIcon", "0");
            if (this.strWeatherIconStr.equals("")) {
                this.strWeatherIconStr = "0";
            }
            this.strWeatherInfor = this.mSettings.getString("WeatherInfor", "晴");
            this.strCurWeatherInfor = this.mSettings.getString("CurWeatherInfor", "东北风  1级 湿度：80%");
        }
    }

    /* access modifiers changed from: private */
    public void savaWeatherValue() {
        if (this.mEditor != null) {
            this.mEditor.putString("CityCode", this.strLastCityCode);
            this.mEditor.putString("CityName", this.strCityName);
            this.mEditor.putString("CurWeather", this.strCurWeather);
            this.mEditor.putString("DayWeather", this.strDayWeather);
            this.mEditor.putString("UpdateTimer", this.strUpdateTimer);
            this.mEditor.putString("UpdateData", this.strUpdateData);
            this.mEditor.putString("WeatherIcon", this.strWeatherIconStr);
            this.mEditor.putString("WeatherInfor", this.strWeatherInfor);
            this.mEditor.putString("CurWeatherInfor", this.strCurWeatherInfor);
            this.mEditor.commit();
        }
    }

    public void updateWidget() {
        this.strCityCode = this.mSettings.getString("CityCode", "101280601");
        this.strCityName = this.mSettings.getString("CityName", "深圳");
        this.realTimer = 0;
        this.dayTimer = 0;
        if (this.mDownHandler != null) {
            this.mDownHandler.removeMessages(GET_WEATHER);
            this.mDownHandler.sendEmptyMessage(GET_WEATHER);
        }
    }

    /* access modifiers changed from: package-private */
    public int getWeatherResouceID(String str, boolean dayNight) {
        return 0;
    }

    private class WeatherThread extends Thread {
        private WeatherThread() {
        }

        public void run() {
            Looper.prepare();
            Handler unused = WeatherService.this.mDownHandler = new Handler() {
                public void handleMessage(Message msg) {
                    switch (msg.what) {
                        case WeatherService.GET_WEATHER /*292*/:
                            Log.d(WeatherService.TAG, "GET_WEATHER");
                            if (!WeatherService.this.checkNetworkConntected()) {
                                WeatherService.this.mDownHandler.removeMessages(WeatherService.GET_WEATHER);
                                WeatherService.this.mDownHandler.sendEmptyMessageDelayed(WeatherService.GET_WEATHER, 10000);
                                return;
                            }
                            if (WeatherService.this.realTimer == 0 || SystemClock.elapsedRealtime() - WeatherService.this.realTimer > 3600000) {
                                String unused = WeatherService.this.strCityCode = WeatherService.this.mSettings.getString("CityCode", "101280601");
                                String unused2 = WeatherService.this.strLastCityCode = WeatherService.this.strCityCode;
                                if ("101031200".equals(WeatherService.this.strCityCode) || "101030800".equals(WeatherService.this.strCityCode)) {
                                    String unused3 = WeatherService.this.strCityCode = "101031100";
                                }
                                WeatherForm data = WeatherService.this.queryCurWeatherXml(WeatherService.this.strCityCode);
                                if (data != null) {
                                    if (WeatherService.this.iRequestUpdateTime == 0) {
                                        long unused4 = WeatherService.this.iRequestUpdateTime = WeatherService.this.iRequestUpdateTime + 3600000;
                                    } else {
                                        long unused5 = WeatherService.this.iRequestUpdateTime = WeatherService.this.iRequestUpdateTime + 7200000;
                                    }
                                    long unused6 = WeatherService.this.realTimer = SystemClock.elapsedRealtime();
                                    Log.i(WeatherService.TAG, "handleMessage: realTimer = " + WeatherService.this.realTimer);
                                    Log.i(WeatherService.TAG, "WeatherForm = " + data);
                                    String unused7 = WeatherService.this.strCityName = data.getName();
                                    if (WeatherService.this.otherWayGetWeather) {
                                        String unused8 = WeatherService.this.strCurWeather = data.getTemp();
                                    } else {
                                        String unused9 = WeatherService.this.strCurWeather = data.getTemp() + "°C";
                                    }
                                    String unused10 = WeatherService.this.strWeatherInfor = data.getImgTitle();
                                    String unused11 = WeatherService.this.strCurWeatherInfor = "" + data.getFx() + " " + data.getWind() + " 湿度:" + data.getSD();
                                    String unused12 = WeatherService.this.strUpdateTimer = data.getDdate();
                                    String unused13 = WeatherService.this.strWeatherIconStr = data.getImg();
                                    WeatherService.this.savaWeatherValue();
                                } else {
                                    if (WeatherService.this.realTimer != 0) {
                                        long unused14 = WeatherService.this.realTimer = WeatherService.this.realTimer + 600000;
                                    }
                                    WeatherService.this.mDownHandler.removeMessages(WeatherService.GET_WEATHER);
                                    WeatherService.this.mDownHandler.sendEmptyMessageDelayed(WeatherService.GET_WEATHER, 600000);
                                }
                            }
                            if (WeatherService.this.dayTimer == 0 || SystemClock.elapsedRealtime() - WeatherService.this.dayTimer > 7200000) {
                                String unused15 = WeatherService.this.strCityCode = WeatherService.this.mSettings.getString("CityCode", "101280601");
                                WeatherForm[] dataArray = WeatherService.this.weatherParse(WeatherService.this.strCityCode);
                                if (dataArray != null && dataArray.length > 0) {
                                    Log.i(WeatherService.TAG, "handleMessage: dataArray != null");
                                    if (WeatherService.this.iRequestUpdateTime == 0) {
                                        long unused16 = WeatherService.this.iRequestUpdateTime = WeatherService.this.iRequestUpdateTime + 3600000;
                                    } else {
                                        long unused17 = WeatherService.this.iRequestUpdateTime = WeatherService.this.iRequestUpdateTime + 7200000;
                                    }
                                    long unused18 = WeatherService.this.dayTimer = SystemClock.elapsedRealtime();
                                    String unused19 = WeatherService.this.strUpdateData = dataArray[0].getDdate();
                                    String unused20 = WeatherService.this.strDayWeather = dataArray[0].getTemp();
                                    String unused21 = WeatherService.this.strWeatherIconStr = dataArray[0].getImg();
                                    String unused22 = WeatherService.this.strWeatherInfor = dataArray[0].getImgTitle();
                                    WeatherService.this.savaWeatherValue();
                                } else if (WeatherService.this.dayTimer != 0) {
                                    long unused23 = WeatherService.this.dayTimer = WeatherService.this.dayTimer + 600000;
                                }
                            }
                            WeatherService.this.mDownHandler.removeMessages(WeatherService.GET_WEATHER);
                            WeatherService.this.mDownHandler.sendEmptyMessageDelayed(WeatherService.GET_WEATHER, WeatherService.this.iRequestUpdateTime);
                            return;
                        case WeatherService.QUIT_DOWNLOAD /*293*/:
                            Looper.myLooper().quit();
                            return;
                        default:
                            return;
                    }
                }
            };
            Looper.loop();
        }
    }

    /* access modifiers changed from: package-private */
    public boolean checkNetworkConntected() {
        NetworkInfo networkInfo = ((ConnectivityManager) getSystemService("connectivity")).getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isAvailable()) {
            return false;
        }
        return true;
    }

    public class WeatherForm {
        private String ddate = "";
        private String fx = "";
        private String id = "";
        private String img = "";
        private String imgTitle;
        private String name = "";
        private String sd = "";
        private String temp = "";
        private String weather = "";
        private String week = "";
        private String wind = "";

        public WeatherForm() {
        }

        public WeatherForm(String name2, String id2, String ddate2, String week2, String temp2, String weather2, String wind2, String fx2, String img2, String imgTitle2, String sd2) {
            this.name = name2;
            this.id = id2;
            this.ddate = ddate2;
            this.week = week2;
            this.temp = temp2;
            this.weather = weather2;
            this.wind = wind2;
            this.fx = fx2;
            this.img = img2;
            this.imgTitle = imgTitle2;
            this.sd = sd2;
        }

        public String getName() {
            return this.name;
        }

        public void setName(String name2) {
            this.name = name2;
        }

        public String getId() {
            return this.id;
        }

        public void setId(String id2) {
            this.id = id2;
        }

        public String getDdate() {
            return this.ddate;
        }

        public void setDdate(String ddate2) {
            this.ddate = ddate2;
        }

        public String getWeek() {
            return this.week;
        }

        public void setWeek(String week2) {
            this.week = week2;
        }

        public String getTemp() {
            return this.temp;
        }

        public void setTemp(String temp2) {
            this.temp = temp2;
        }

        public String getWeather() {
            return this.weather;
        }

        public void setWeather(String weather2) {
            this.weather = weather2;
        }

        public String getWind() {
            return this.wind;
        }

        public void setWind(String wind2) {
            this.wind = wind2;
        }

        public String getFx() {
            return this.fx;
        }

        public void setFx(String fx2) {
            this.fx = fx2;
        }

        public String getImg() {
            return this.img;
        }

        public void setImg(String img2) {
            this.img = img2;
        }

        public String getImgTitle() {
            return this.imgTitle;
        }

        public void setImgTitle(String imgTitle2) {
            this.imgTitle = imgTitle2;
        }

        public String getSD() {
            return this.sd;
        }

        public void setSD(String sd2) {
            this.sd = sd2;
        }

        public String toString() {
            return "WeatherForm [name=" + this.name + ", id=" + this.id + ", ddate=" + this.ddate + ", week=" + this.week + ", temp=" + this.temp + ", weather=" + this.weather + ", wind=" + this.wind + ", fx=" + this.fx + ", img=" + this.img + ", imgTitle=" + this.imgTitle + "]";
        }
    }

    public WeatherForm[] weatherParse(String cityId) {
        int picStartLen;
        Document doc = null;
        try {
            doc = Jsoup.parse(new URL("http://m.weather.com.cn/mweather/" + cityId + ".shtml"), 5000);
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        } catch (IOException e12) {
            e12.printStackTrace();
        }
        WeatherForm[] WF = null;
        if (doc != null) {
            Elements es = doc.getElementsByClass("days7");
            if (es.size() > 0) {
                Elements li = es.get(0).getElementsByTag("li");
                if (li.size() > 0) {
                    Element today = li.get(0);
                    Log.i(TAG, "li span= " + today.getElementsByTag("span").text());
                    Elements weather = today.getElementsByTag("i");
                    Log.i(TAG, "weather= " + weather);
                    if (weather.size() >= 0) {
                        Elements img = weather.get(0).getElementsByTag("img");
                        Log.i(TAG, "img= " + img);
                        if (img.size() >= 2) {
                            Element w1 = img.get(0);
                            Element w2 = img.get(1);
                            WF = new WeatherForm[]{new WeatherForm()};
                            WF[0].setTemp(today.getElementsByTag("span").text());
                            WF[0].setImgTitle(w1.attr("alt"));
                            String imgPath = w1.attr("src");
                            if (imgPath != null && imgPath.length() > 0 && (picStartLen = imgPath.lastIndexOf("/")) < imgPath.length()) {
                                String pic = imgPath.substring(picStartLen + 1);
                                if (pic.length() >= 7) {
                                    WF[0].setImg(pic.substring(1, 3));
                                    Log.i(TAG, "img" + pic.substring(1, 3));
                                }
                            }
                            Log.i(TAG, "w1 src= " + w1.attr("src") + " alt = " + w1.attr("alt"));
                            Log.i(TAG, "w2 src= " + w2.attr("src") + " alt = " + w2.attr("alt"));
                        }
                    }
                }
            }
        }
        return WF;
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Removed duplicated region for block: B:101:0x0335  */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x00b7  */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x00cd  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x0105 A[Catch:{ XmlPullParserException -> 0x01af }] */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x013d  */
    /* JADX WARNING: Removed duplicated region for block: B:8:0x006d A[SYNTHETIC, Splitter:B:8:0x006d] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.szchoiceway.index.WeatherService.WeatherForm queryCurWeatherXml(java.lang.String r22) {
        /*
            r21 = this;
            java.lang.String r18 = "WeatherService"
            java.lang.StringBuilder r19 = new java.lang.StringBuilder
            r19.<init>()
            java.lang.String r20 = "WeatherForm = "
            java.lang.StringBuilder r19 = r19.append(r20)
            r0 = r19
            r1 = r22
            java.lang.StringBuilder r19 = r0.append(r1)
            java.lang.String r19 = r19.toString()
            android.util.Log.d(r18, r19)
            r14 = 0
            r10 = 0
            r16 = 0
            r18 = 0
            r0 = r18
            r1 = r21
            r1.otherWayGetWeather = r0
            r13 = 0
            java.net.URL r15 = new java.net.URL     // Catch:{ Exception -> 0x00c1 }
            java.lang.StringBuilder r18 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00c1 }
            r18.<init>()     // Catch:{ Exception -> 0x00c1 }
            java.lang.String r19 = "http://wthrcdn.etouch.cn/WeatherApi?citykey="
            java.lang.StringBuilder r18 = r18.append(r19)     // Catch:{ Exception -> 0x00c1 }
            r0 = r18
            r1 = r22
            java.lang.StringBuilder r18 = r0.append(r1)     // Catch:{ Exception -> 0x00c1 }
            java.lang.String r18 = r18.toString()     // Catch:{ Exception -> 0x00c1 }
            r0 = r18
            r15.<init>(r0)     // Catch:{ Exception -> 0x00c1 }
            java.net.URLConnection r4 = r15.openConnection()     // Catch:{ Exception -> 0x0331 }
            java.net.HttpURLConnection r4 = (java.net.HttpURLConnection) r4     // Catch:{ Exception -> 0x0331 }
            r18 = 5000(0x1388, float:7.006E-42)
            r0 = r18
            r4.setConnectTimeout(r0)     // Catch:{ Exception -> 0x0331 }
            java.lang.String r18 = "GET"
            r0 = r18
            r4.setRequestMethod(r0)     // Catch:{ Exception -> 0x0331 }
            int r18 = r4.getResponseCode()     // Catch:{ Exception -> 0x0331 }
            r19 = 200(0xc8, float:2.8E-43)
            r0 = r18
            r1 = r19
            if (r0 != r1) goto L_0x006b
            java.io.InputStream r10 = r4.getInputStream()     // Catch:{ Exception -> 0x0331 }
        L_0x006b:
            if (r10 != 0) goto L_0x0335
            java.net.URL r14 = new java.net.URL     // Catch:{ Exception -> 0x00c7 }
            java.lang.StringBuilder r18 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00c7 }
            r18.<init>()     // Catch:{ Exception -> 0x00c7 }
            java.lang.String r19 = "http://api.k780.com:88/?app=weather.today&weaid="
            java.lang.StringBuilder r18 = r18.append(r19)     // Catch:{ Exception -> 0x00c7 }
            r0 = r18
            r1 = r22
            java.lang.StringBuilder r18 = r0.append(r1)     // Catch:{ Exception -> 0x00c7 }
            java.lang.String r19 = "&appkey=10003&sign=b59bc3ef6191eb9f747dd4e83c99f2a4&format=xml"
            java.lang.StringBuilder r18 = r18.append(r19)     // Catch:{ Exception -> 0x00c7 }
            java.lang.String r18 = r18.toString()     // Catch:{ Exception -> 0x00c7 }
            r0 = r18
            r14.<init>(r0)     // Catch:{ Exception -> 0x00c7 }
            java.net.URLConnection r4 = r14.openConnection()     // Catch:{ Exception -> 0x032e }
            java.net.HttpURLConnection r4 = (java.net.HttpURLConnection) r4     // Catch:{ Exception -> 0x032e }
            r18 = 5000(0x1388, float:7.006E-42)
            r0 = r18
            r4.setConnectTimeout(r0)     // Catch:{ Exception -> 0x032e }
            java.lang.String r18 = "GET"
            r0 = r18
            r4.setRequestMethod(r0)     // Catch:{ Exception -> 0x032e }
            int r18 = r4.getResponseCode()     // Catch:{ Exception -> 0x032e }
            r19 = 200(0xc8, float:2.8E-43)
            r0 = r18
            r1 = r19
            if (r0 != r1) goto L_0x00b5
            java.io.InputStream r10 = r4.getInputStream()     // Catch:{ Exception -> 0x032e }
        L_0x00b5:
            if (r10 != 0) goto L_0x00cd
            java.lang.String r18 = "WeatherService"
            java.lang.String r19 = "myInput == null"
            android.util.Log.d(r18, r19)
            r17 = 0
        L_0x00c0:
            return r17
        L_0x00c1:
            r5 = move-exception
        L_0x00c2:
            r5.printStackTrace()
            r15 = r14
            goto L_0x006b
        L_0x00c7:
            r5 = move-exception
            r14 = r15
        L_0x00c9:
            r5.printStackTrace()
            goto L_0x00b5
        L_0x00cd:
            r18 = 1
            r0 = r18
            r1 = r21
            r1.otherWayGetWeather = r0
            java.lang.String r18 = "WeatherService"
            java.lang.String r19 = "http://api.k780.com:88"
            android.util.Log.d(r18, r19)
        L_0x00dc:
            org.xmlpull.v1.XmlPullParser r11 = android.util.Xml.newPullParser()
            java.lang.String r18 = "utf-8"
            r0 = r18
            r11.setInput(r10, r0)     // Catch:{ XmlPullParserException -> 0x032b }
            int r6 = r11.getEventType()     // Catch:{ XmlPullParserException -> 0x032b }
            java.lang.String r12 = ""
            com.szchoiceway.index.WeatherService$WeatherForm r17 = new com.szchoiceway.index.WeatherService$WeatherForm     // Catch:{ XmlPullParserException -> 0x032b }
            r0 = r17
            r1 = r21
            r0.<init>()     // Catch:{ XmlPullParserException -> 0x032b }
            r0 = r17
            r1 = r22
            r0.setId(r1)     // Catch:{ XmlPullParserException -> 0x01af }
            r0 = r21
            boolean r0 = r0.otherWayGetWeather     // Catch:{ XmlPullParserException -> 0x01af }
            r18 = r0
            if (r18 == 0) goto L_0x0137
            java.util.Calendar r2 = java.util.Calendar.getInstance()     // Catch:{ XmlPullParserException -> 0x01af }
            r18 = 11
            r0 = r18
            int r8 = r2.get(r0)     // Catch:{ XmlPullParserException -> 0x01af }
            r18 = 12
            r0 = r18
            int r9 = r2.get(r0)     // Catch:{ XmlPullParserException -> 0x01af }
            java.lang.StringBuilder r18 = new java.lang.StringBuilder     // Catch:{ XmlPullParserException -> 0x01af }
            r18.<init>()     // Catch:{ XmlPullParserException -> 0x01af }
            r0 = r18
            java.lang.StringBuilder r18 = r0.append(r8)     // Catch:{ XmlPullParserException -> 0x01af }
            java.lang.String r19 = ":"
            java.lang.StringBuilder r18 = r18.append(r19)     // Catch:{ XmlPullParserException -> 0x01af }
            r0 = r18
            java.lang.StringBuilder r18 = r0.append(r9)     // Catch:{ XmlPullParserException -> 0x01af }
            java.lang.String r18 = r18.toString()     // Catch:{ XmlPullParserException -> 0x01af }
            r17.setDdate(r18)     // Catch:{ XmlPullParserException -> 0x01af }
        L_0x0137:
            r18 = 1
            r0 = r18
            if (r6 == r0) goto L_0x0327
            switch(r6) {
                case 2: goto L_0x0145;
                case 3: goto L_0x0140;
                case 4: goto L_0x016d;
                default: goto L_0x0140;
            }
        L_0x0140:
            int r6 = r11.next()     // Catch:{ IOException -> 0x01aa }
            goto L_0x0137
        L_0x0145:
            java.lang.String r18 = "WeatherService"
            java.lang.String r19 = "START_TAG"
            android.util.Log.i(r18, r19)     // Catch:{ IOException -> 0x01aa }
            java.lang.String r18 = "WeatherService"
            java.lang.StringBuilder r19 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x01aa }
            r19.<init>()     // Catch:{ IOException -> 0x01aa }
            java.lang.String r20 = "pullParser.getName() = "
            java.lang.StringBuilder r19 = r19.append(r20)     // Catch:{ IOException -> 0x01aa }
            java.lang.String r20 = r11.getName()     // Catch:{ IOException -> 0x01aa }
            java.lang.StringBuilder r19 = r19.append(r20)     // Catch:{ IOException -> 0x01aa }
            java.lang.String r19 = r19.toString()     // Catch:{ IOException -> 0x01aa }
            android.util.Log.i(r18, r19)     // Catch:{ IOException -> 0x01aa }
            java.lang.String r12 = r11.getName()     // Catch:{ IOException -> 0x01aa }
            goto L_0x0140
        L_0x016d:
            java.lang.String r18 = "WeatherService"
            java.lang.String r19 = "TEXT"
            android.util.Log.i(r18, r19)     // Catch:{ IOException -> 0x01aa }
            java.lang.String r18 = "WeatherService"
            java.lang.StringBuilder r19 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x01aa }
            r19.<init>()     // Catch:{ IOException -> 0x01aa }
            java.lang.String r20 = "pullParser.getText() = "
            java.lang.StringBuilder r19 = r19.append(r20)     // Catch:{ IOException -> 0x01aa }
            java.lang.String r20 = r11.getText()     // Catch:{ IOException -> 0x01aa }
            java.lang.StringBuilder r19 = r19.append(r20)     // Catch:{ IOException -> 0x01aa }
            java.lang.String r19 = r19.toString()     // Catch:{ IOException -> 0x01aa }
            android.util.Log.i(r18, r19)     // Catch:{ IOException -> 0x01aa }
            r0 = r21
            boolean r0 = r0.otherWayGetWeather     // Catch:{ IOException -> 0x01aa }
            r18 = r0
            if (r18 == 0) goto L_0x0232
            java.lang.String r18 = "citynm"
            r0 = r18
            boolean r18 = r12.equalsIgnoreCase(r0)     // Catch:{ IOException -> 0x01aa }
            if (r18 == 0) goto L_0x01c0
            java.lang.String r18 = r11.getText()     // Catch:{ IOException -> 0x01aa }
            r17.setName(r18)     // Catch:{ IOException -> 0x01aa }
            goto L_0x0140
        L_0x01aa:
            r5 = move-exception
            r5.printStackTrace()     // Catch:{ XmlPullParserException -> 0x01af }
            goto L_0x0137
        L_0x01af:
            r5 = move-exception
            r16 = r17
        L_0x01b2:
            r5.printStackTrace()
        L_0x01b5:
            java.lang.String r18 = "WeatherService"
            java.lang.String r19 = "end"
            android.util.Log.d(r18, r19)
            r17 = r16
            goto L_0x00c0
        L_0x01c0:
            java.lang.String r18 = "temperature_curr"
            r0 = r18
            boolean r18 = r12.equalsIgnoreCase(r0)     // Catch:{ IOException -> 0x01aa }
            if (r18 == 0) goto L_0x01d3
            java.lang.String r18 = r11.getText()     // Catch:{ IOException -> 0x01aa }
            r17.setTemp(r18)     // Catch:{ IOException -> 0x01aa }
            goto L_0x0140
        L_0x01d3:
            java.lang.String r18 = "winp"
            r0 = r18
            boolean r18 = r12.equalsIgnoreCase(r0)     // Catch:{ IOException -> 0x01aa }
            if (r18 == 0) goto L_0x01e6
            java.lang.String r18 = r11.getText()     // Catch:{ IOException -> 0x01aa }
            r17.setWind(r18)     // Catch:{ IOException -> 0x01aa }
            goto L_0x0140
        L_0x01e6:
            java.lang.String r18 = "humidity"
            r0 = r18
            boolean r18 = r12.equalsIgnoreCase(r0)     // Catch:{ IOException -> 0x01aa }
            if (r18 == 0) goto L_0x01f9
            java.lang.String r18 = r11.getText()     // Catch:{ IOException -> 0x01aa }
            r17.setSD(r18)     // Catch:{ IOException -> 0x01aa }
            goto L_0x0140
        L_0x01f9:
            java.lang.String r18 = "wind"
            r0 = r18
            boolean r18 = r12.equalsIgnoreCase(r0)     // Catch:{ IOException -> 0x01aa }
            if (r18 == 0) goto L_0x020c
            java.lang.String r18 = r11.getText()     // Catch:{ IOException -> 0x01aa }
            r17.setFx(r18)     // Catch:{ IOException -> 0x01aa }
            goto L_0x0140
        L_0x020c:
            java.lang.String r18 = "weather_iconid"
            r0 = r18
            boolean r18 = r12.equalsIgnoreCase(r0)     // Catch:{ IOException -> 0x01aa }
            if (r18 == 0) goto L_0x021f
            java.lang.String r18 = r11.getText()     // Catch:{ IOException -> 0x01aa }
            r17.setImg(r18)     // Catch:{ IOException -> 0x01aa }
            goto L_0x0140
        L_0x021f:
            java.lang.String r18 = "weather_curr"
            r0 = r18
            boolean r18 = r12.equalsIgnoreCase(r0)     // Catch:{ IOException -> 0x01aa }
            if (r18 == 0) goto L_0x0140
            java.lang.String r18 = r11.getText()     // Catch:{ IOException -> 0x01aa }
            r17.setImgTitle(r18)     // Catch:{ IOException -> 0x01aa }
            goto L_0x0140
        L_0x0232:
            java.lang.String r18 = "updatetime"
            r0 = r18
            boolean r18 = r12.equalsIgnoreCase(r0)     // Catch:{ IOException -> 0x01aa }
            if (r18 == 0) goto L_0x0245
            java.lang.String r18 = r11.getText()     // Catch:{ IOException -> 0x01aa }
            r17.setDdate(r18)     // Catch:{ IOException -> 0x01aa }
            goto L_0x0140
        L_0x0245:
            java.lang.String r18 = "city"
            r0 = r18
            boolean r18 = r12.equalsIgnoreCase(r0)     // Catch:{ IOException -> 0x01aa }
            if (r18 == 0) goto L_0x027b
            java.lang.String r3 = r11.getText()     // Catch:{ IOException -> 0x01aa }
            java.lang.String r18 = "101031200"
            r0 = r21
            java.lang.String r0 = r0.strLastCityCode     // Catch:{ IOException -> 0x01aa }
            r19 = r0
            boolean r18 = r18.equals(r19)     // Catch:{ IOException -> 0x01aa }
            if (r18 == 0) goto L_0x026a
            java.lang.String r3 = "大港"
        L_0x0263:
            r0 = r17
            r0.setName(r3)     // Catch:{ IOException -> 0x01aa }
            goto L_0x0140
        L_0x026a:
            java.lang.String r18 = "101030800"
            r0 = r21
            java.lang.String r0 = r0.strLastCityCode     // Catch:{ IOException -> 0x01aa }
            r19 = r0
            boolean r18 = r18.equals(r19)     // Catch:{ IOException -> 0x01aa }
            if (r18 == 0) goto L_0x0263
            java.lang.String r3 = "汉沽"
            goto L_0x0263
        L_0x027b:
            java.lang.String r18 = "wendu"
            r0 = r18
            boolean r18 = r12.equalsIgnoreCase(r0)     // Catch:{ IOException -> 0x01aa }
            if (r18 == 0) goto L_0x028e
            java.lang.String r18 = r11.getText()     // Catch:{ IOException -> 0x01aa }
            r17.setTemp(r18)     // Catch:{ IOException -> 0x01aa }
            goto L_0x0140
        L_0x028e:
            java.lang.String r18 = "fengli"
            r0 = r18
            boolean r18 = r12.equalsIgnoreCase(r0)     // Catch:{ IOException -> 0x01aa }
            if (r18 == 0) goto L_0x02a1
            java.lang.String r18 = r11.getText()     // Catch:{ IOException -> 0x01aa }
            r17.setWind(r18)     // Catch:{ IOException -> 0x01aa }
            goto L_0x0140
        L_0x02a1:
            java.lang.String r18 = "shidu"
            r0 = r18
            boolean r18 = r12.equalsIgnoreCase(r0)     // Catch:{ IOException -> 0x01aa }
            if (r18 == 0) goto L_0x02b4
            java.lang.String r18 = r11.getText()     // Catch:{ IOException -> 0x01aa }
            r17.setSD(r18)     // Catch:{ IOException -> 0x01aa }
            goto L_0x0140
        L_0x02b4:
            java.lang.String r18 = "fengxiang"
            r0 = r18
            boolean r18 = r12.equalsIgnoreCase(r0)     // Catch:{ IOException -> 0x01aa }
            if (r18 == 0) goto L_0x02c7
            java.lang.String r18 = r11.getText()     // Catch:{ IOException -> 0x01aa }
            r17.setFx(r18)     // Catch:{ IOException -> 0x01aa }
            goto L_0x0140
        L_0x02c7:
            java.lang.String r18 = "type"
            r0 = r18
            boolean r18 = r12.equalsIgnoreCase(r0)     // Catch:{ IOException -> 0x01aa }
            if (r18 == 0) goto L_0x0140
            java.lang.String r18 = r11.getText()     // Catch:{ IOException -> 0x01aa }
            r17.setImgTitle(r18)     // Catch:{ IOException -> 0x01aa }
            java.lang.String r18 = "WeatherService"
            java.lang.StringBuilder r19 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x01aa }
            r19.<init>()     // Catch:{ IOException -> 0x01aa }
            java.lang.String r20 = "queryCurWeatherXml: type pullParser.getText() = "
            java.lang.StringBuilder r19 = r19.append(r20)     // Catch:{ IOException -> 0x01aa }
            java.lang.String r20 = r11.getText()     // Catch:{ IOException -> 0x01aa }
            java.lang.StringBuilder r19 = r19.append(r20)     // Catch:{ IOException -> 0x01aa }
            java.lang.String r19 = r19.toString()     // Catch:{ IOException -> 0x01aa }
            android.util.Log.i(r18, r19)     // Catch:{ IOException -> 0x01aa }
            java.lang.String r18 = r11.getText()     // Catch:{ IOException -> 0x01aa }
            r0 = r21
            r1 = r18
            int r7 = r0.getWeatherIconIndex(r1)     // Catch:{ IOException -> 0x01aa }
            java.lang.String r18 = java.lang.Integer.toString(r7)     // Catch:{ IOException -> 0x01aa }
            r17.setImg(r18)     // Catch:{ IOException -> 0x01aa }
            java.lang.String r18 = "WeatherService"
            java.lang.StringBuilder r19 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x01aa }
            r19.<init>()     // Catch:{ IOException -> 0x01aa }
            java.lang.String r20 = "queryCurWeatherXml: type Integer.toString(id) = "
            java.lang.StringBuilder r19 = r19.append(r20)     // Catch:{ IOException -> 0x01aa }
            java.lang.String r20 = java.lang.Integer.toString(r7)     // Catch:{ IOException -> 0x01aa }
            java.lang.StringBuilder r19 = r19.append(r20)     // Catch:{ IOException -> 0x01aa }
            java.lang.String r19 = r19.toString()     // Catch:{ IOException -> 0x01aa }
            android.util.Log.i(r18, r19)     // Catch:{ IOException -> 0x01aa }
            r16 = r17
            goto L_0x00c0
        L_0x0327:
            r16 = r17
            goto L_0x01b5
        L_0x032b:
            r5 = move-exception
            goto L_0x01b2
        L_0x032e:
            r5 = move-exception
            goto L_0x00c9
        L_0x0331:
            r5 = move-exception
            r14 = r15
            goto L_0x00c2
        L_0x0335:
            r14 = r15
            goto L_0x00dc
        */
        throw new UnsupportedOperationException("Method not decompiled: com.szchoiceway.index.WeatherService.queryCurWeatherXml(java.lang.String):com.szchoiceway.index.WeatherService$WeatherForm");
    }

    public String getCityCode() {
        return this.strCityCode;
    }

    public String getCityName() {
        return this.strCityName;
    }

    public String getCurWeather() {
        return this.strCurWeather;
    }

    public String getDayWeather() {
        return this.strDayWeather;
    }

    public String getUpdateData() {
        return this.strUpdateData;
    }

    public String getWeatherIconStr() {
        return this.strWeatherIconStr;
    }

    public String getWeatherInfor() {
        return this.strWeatherInfor;
    }

    public String getCurWeatherInfor() {
        return this.strCurWeatherInfor;
    }

    public String getUpdateTimer() {
        return this.strUpdateTimer;
    }

    static class ServiceStub extends IWeatherService.Stub {
        WeakReference<WeatherService> mService;

        ServiceStub(WeatherService service) {
            this.mService = new WeakReference<>(service);
        }

        public String getCityCode() throws RemoteException {
            if (this.mService.get() != null) {
                return ((WeatherService) this.mService.get()).getCityCode();
            }
            return "";
        }

        public String getCityName() throws RemoteException {
            if (this.mService.get() != null) {
                return ((WeatherService) this.mService.get()).getCityName();
            }
            return "";
        }

        public String getCurWeather() throws RemoteException {
            if (this.mService.get() != null) {
                return ((WeatherService) this.mService.get()).getCurWeather();
            }
            return "";
        }

        public String getDayWeather() throws RemoteException {
            if (this.mService.get() != null) {
                return ((WeatherService) this.mService.get()).getDayWeather();
            }
            return "";
        }

        public String getUpdateData() throws RemoteException {
            if (this.mService.get() != null) {
                return ((WeatherService) this.mService.get()).getUpdateData();
            }
            return "";
        }

        public String getWeatherIconStr() throws RemoteException {
            if (this.mService.get() != null) {
                return ((WeatherService) this.mService.get()).getWeatherIconStr();
            }
            return "0";
        }

        public String getWeatherInfor() throws RemoteException {
            if (this.mService.get() != null) {
                return ((WeatherService) this.mService.get()).getWeatherInfor();
            }
            return "";
        }

        public String getCurWeatherInfor() throws RemoteException {
            if (this.mService.get() != null) {
                return ((WeatherService) this.mService.get()).getCurWeatherInfor();
            }
            return "";
        }

        public String getUpdateTimer() throws RemoteException {
            if (this.mService.get() != null) {
                return ((WeatherService) this.mService.get()).getUpdateTimer();
            }
            return "";
        }

        public void updateWeather() throws RemoteException {
            if (this.mService.get() != null) {
                ((WeatherService) this.mService.get()).updateWidget();
            }
        }
    }

    public int getWeatherIconIndex(String weatherInfor) {
        int id = -1;
        if (weatherInfor == null || weatherInfor.length() == 0) {
            return 0;
        }
        if (weatherInfor.equals("晴") || weatherInfor.equals("晴天") || weatherInfor.equals("少云") || weatherInfor.equals("多云转晴")) {
            id = 0;
        } else if (weatherInfor.equals("多云") || weatherInfor.equals("晴转多云")) {
            id = 1;
        } else if (weatherInfor.equals("阴") || weatherInfor.equals("阴天") || weatherInfor.equals("多云转阴") || weatherInfor.equals("多云转阴天") || weatherInfor.equals("多云间阴天")) {
            id = 2;
        } else if (weatherInfor.equals("阵雨") || weatherInfor.equals("多云转阵雨")) {
            id = 3;
        } else if (weatherInfor.equals("雷阵雨")) {
            id = 4;
        } else if (weatherInfor.equals("雷阵雨伴有冰雹")) {
            id = 5;
        } else if (weatherInfor.equals("雨夹雪")) {
            id = 6;
        } else if (weatherInfor.equals("小雨") || weatherInfor.equals("阴转小雨") || weatherInfor.equals("中转小雨")) {
            id = 7;
        } else if (weatherInfor.equals("中雨") || weatherInfor.equals("小到中雨") || weatherInfor.equals("小转中雨") || weatherInfor.equals("大转中雨")) {
            id = 8;
        } else if (weatherInfor.equals("大雨") || weatherInfor.equals("中到大雨") || weatherInfor.equals("中转大雨") || weatherInfor.equals("暴雨转大雨")) {
            id = 9;
        } else if (weatherInfor.equals("暴雨") || weatherInfor.equals("大到暴雨") || weatherInfor.equals("大转暴雨") || weatherInfor.equals("大暴转暴雨")) {
            id = 10;
        } else if (weatherInfor.equals("大暴雨") || weatherInfor.equals("暴到大暴雨") || weatherInfor.equals("暴转大暴雨") || weatherInfor.equals("特大转暴雨")) {
            id = 11;
        } else if (weatherInfor.equals("特大暴雨") || weatherInfor.equals("大暴到特大暴雨") || weatherInfor.equals("大暴转特大暴雨")) {
            id = 12;
        } else if (weatherInfor.equals("阵雪")) {
            id = 13;
        } else if (weatherInfor.equals("小雪") || weatherInfor.equals("中转小雪")) {
            id = 14;
        } else if (weatherInfor.equals("中雪") || weatherInfor.equals("小转中雪") || weatherInfor.equals("小到中雪") || weatherInfor.equals("大转中雪")) {
            id = 15;
        } else if (weatherInfor.equals("大雪") || weatherInfor.equals("中到大雪") || weatherInfor.equals("中转大雪") || weatherInfor.equals("暴转大雪")) {
            id = 16;
        } else if (weatherInfor.equals("暴雪") || weatherInfor.equals("大到暴雪") || weatherInfor.equals("大转暴雪")) {
            id = 17;
        } else if (weatherInfor.contains("雾")) {
            id = 18;
        } else if (weatherInfor.contains("冰雨")) {
            id = 19;
        } else if (weatherInfor.contains("沙尘暴")) {
            id = 20;
        } else if (weatherInfor.contains("浮尘")) {
            id = 30;
        } else if (weatherInfor.contains("扬沙")) {
            id = 30;
        } else if (weatherInfor.contains("强沙尘暴")) {
            id = 31;
        } else if (weatherInfor.contains("霾")) {
            id = 53;
        }
        if (id == -1) {
            if (weatherInfor.contains("特大暴雨")) {
                return 12;
            }
            if (weatherInfor.contains("大暴雨")) {
                return 11;
            }
            if (weatherInfor.contains("暴雨")) {
                return 10;
            }
            if (weatherInfor.contains("大雨")) {
                return 9;
            }
            if (weatherInfor.contains("中雨")) {
                return 8;
            }
            if (weatherInfor.contains("小雨")) {
                return 7;
            }
            if (weatherInfor.contains("阵雨")) {
                return 3;
            }
            if (weatherInfor.contains("阴")) {
                return 2;
            }
            if (weatherInfor.contains("晴")) {
                return 0;
            }
            if (weatherInfor.contains("多云")) {
                return 1;
            }
            if (weatherInfor.contains("暴雪")) {
                return 17;
            }
            if (weatherInfor.contains("大雪")) {
                return 16;
            }
            if (weatherInfor.contains("中雪")) {
                return 15;
            }
            if (weatherInfor.contains("小雪")) {
                return 14;
            }
            if (weatherInfor.contains("阵雪")) {
                return 13;
            }
        }
        return id;
    }

    public class MyLocationListener implements BDLocationListener {
        public MyLocationListener() {
        }

        public void onReceiveLocation(BDLocation location) {
            Log.i(WeatherService.TAG, "onReceiveLocation " + WeatherService.this.mEditor);
            boolean isWifiConnected = ((ConnectivityManager) WeatherService.this.getSystemService("connectivity")).getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED;
            Log.i(WeatherService.TAG, "isWifiConnected = " + isWifiConnected);
            if (isWifiConnected) {
                Log.i(WeatherService.TAG, "onReceiveLocation: location.getCity() = " + location.getCity());
                if (WeatherService.this.mSettings != null) {
                    String unused = WeatherService.this.strCityCode = WeatherService.this.mSettings.getString("CityCode", "101280601");
                    String unused2 = WeatherService.this.strCityName = WeatherService.this.mSettings.getString("CityName", "深圳");
                }
                if (WeatherService.this.mEditor == null || location.getCity() == null) {
                    Log.i(WeatherService.TAG, "mEditor null");
                } else {
                    Log.i(WeatherService.TAG, "onReceiveLocation: WeatherLocationCode.getCityCode(location.getCity()) = " + WeatherLocationCode.getCityCode(location.getCity()));
                    if (!WeatherService.this.strCityCode.equals(WeatherLocationCode.getCityCode(location.getCity())) && !WeatherService.this.strCityName.equals(location.getCity())) {
                        Log.i(WeatherService.TAG, "onReceiveLocation: city-change");
                        WeatherService.this.mEditor.putString("CityCode", WeatherLocationCode.getCityCode(location.getCity()));
                        WeatherService.this.mEditor.putString("CityName", location.getCity());
                        WeatherService.this.mEditor.commit();
                        WeatherService.this.sendBroadcast(new Intent(EventUtils.WEATHER_AREA_CHANGE));
                    }
                }
            }
            Log.i("检查是否启动百度定位", "" + location.getCity());
        }
    }

    public boolean isLunarSetting() {
        String language = getLanguageEnv();
        if (language == null || !language.trim().equals("zh-CN")) {
            return false;
        }
        return true;
    }

    private String getLanguageEnv() {
        Locale l = Locale.getDefault();
        String language = l.getLanguage();
        String country = l.getCountry().toLowerCase();
        if ("zh".equals(language)) {
            if ("cn".equals(country)) {
                return "zh-CN";
            }
            if ("tw".equals(country)) {
                return "zh-TW";
            }
            return language;
        } else if (!"pt".equals(language)) {
            return language;
        } else {
            if ("br".equals(country)) {
                return "pt-BR";
            }
            if ("pt".equals(country)) {
                return "pt-PT";
            }
            return language;
        }
    }

    private void initLocation() {
        Log.i(TAG, "initLocation");
        this.mLocationClient = new LocationClient(this);
        this.mLocationClient.registerLocationListener(this.myListener);
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType(BDLocation.BDLOCATION_GCJ02_TO_BD09LL);
        option.setScanSpan(15000);
        option.setIsNeedAddress(true);
        option.setOpenGps(true);
        option.setLocationNotify(true);
        option.setIsNeedLocationDescribe(true);
        option.setIsNeedLocationPoiList(true);
        option.setIgnoreKillProcess(false);
        option.SetIgnoreCacheException(false);
        option.setEnableSimulateGps(false);
        this.mLocationClient.setLocOption(option);
        this.mLocationClient.start();
        Log.i(TAG, "mLocationClient start");
    }
}
