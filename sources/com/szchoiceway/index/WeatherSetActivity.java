package com.szchoiceway.index;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class WeatherSetActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "WeatherSetActivity";
    private LauncherApplication mApp = null;
    /* access modifiers changed from: private */
    public CityListAdapter mCityAdpt = new CityListAdapter();
    /* access modifiers changed from: private */
    public Cursor mCityCursor = null;
    private CityRecManager mCityRedMan = null;
    /* access modifiers changed from: private */
    public boolean mCityShowMode = false;
    /* access modifiers changed from: private */
    public SharedPreferences.Editor mEditor = null;
    /* access modifiers changed from: private */
    public ListView mLvCityList = null;
    private SysProviderOpt mProvider = null;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.mApp = (LauncherApplication) getApplication();
        this.mProvider = this.mApp.getProvider();
        this.mEditor = this.mApp.getEditor();
        this.mLvCityList = (ListView) findViewById(R.id.LvCityList);
        this.mCityRedMan = new CityRecManager(this);
        getProvinceList();
        if (this.mLvCityList != null) {
            this.mLvCityList.setAdapter(this.mCityAdpt);
            this.mLvCityList.setOnItemClickListener(this.mCityAdpt);
        }
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        if (getRequestedOrientation() != 0) {
            setRequestedOrientation(0);
        }
        super.onResume();
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        super.onStop();
        finish();
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        if (this.mCityCursor != null) {
            this.mCityCursor.close();
        }
        if (this.mCityRedMan != null) {
            this.mCityRedMan.closeDB();
        }
    }

    /* access modifiers changed from: private */
    public void getCityList(String quName) {
        this.mCityShowMode = true;
        if (this.mCityCursor != null) {
            this.mCityCursor.close();
            this.mCityCursor = null;
        }
        if (this.mCityRedMan != null) {
            this.mCityCursor = this.mCityRedMan.queryCityRec(quName);
            if (this.mCityCursor != null) {
                Log.i(TAG, "mCityCursor count = " + this.mCityCursor.getCount());
            }
        }
    }

    /* access modifiers changed from: private */
    public void getProvinceList() {
        Log.i(TAG, "getProvinceList");
        this.mCityShowMode = false;
        if (this.mCityCursor != null) {
            this.mCityCursor.close();
            this.mCityCursor = null;
        }
        if (this.mCityRedMan != null) {
            this.mCityCursor = this.mCityRedMan.queryProvinceRec();
            if (this.mCityCursor != null) {
                Log.i(TAG, "mCityCursor count = " + this.mCityCursor.getCount());
            } else {
                Log.i(TAG, "mCityCursor count = null");
            }
        }
    }

    private class CityListAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {
        private CityListAdapter() {
        }

        public int getCount() {
            if (WeatherSetActivity.this.mCityCursor != null) {
                return WeatherSetActivity.this.mCityShowMode ? WeatherSetActivity.this.mCityCursor.getCount() + 1 : WeatherSetActivity.this.mCityCursor.getCount();
            }
            return 0;
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return (long) position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = WeatherSetActivity.this.getLayoutInflater().inflate(R.layout.city_list_item, (ViewGroup) null);
            }
            TextView TvCityName = (TextView) convertView.findViewById(R.id.TvCityName);
            String cityName = "";
            if (WeatherSetActivity.this.mCityCursor != null) {
                if (WeatherSetActivity.this.mCityShowMode) {
                    if (position == 0) {
                        cityName = "[返回省份地区列表..]";
                    } else if (position >= 1 && WeatherSetActivity.this.mCityCursor.getCount() > 0 && position <= WeatherSetActivity.this.mCityCursor.getCount()) {
                        WeatherSetActivity.this.mCityCursor.moveToPosition(position - 1);
                        cityName = WeatherSetActivity.this.mCityCursor.getString(WeatherSetActivity.this.mCityCursor.getColumnIndex("cityname"));
                    }
                } else if (position >= 0 && WeatherSetActivity.this.mCityCursor.getCount() > 0 && position <= WeatherSetActivity.this.mCityCursor.getCount() - 1) {
                    WeatherSetActivity.this.mCityCursor.moveToPosition(position);
                    cityName = WeatherSetActivity.this.mCityCursor.getString(WeatherSetActivity.this.mCityCursor.getColumnIndex("quname"));
                }
            }
            if (TvCityName != null) {
                TvCityName.setText(cityName);
            }
            return convertView;
        }

        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            if (WeatherSetActivity.this.mCityShowMode) {
                if (position == 0) {
                    WeatherSetActivity.this.getProvinceList();
                    WeatherSetActivity.this.mCityAdpt.notifyDataSetChanged();
                } else if (position > 0 && WeatherSetActivity.this.mCityCursor.getCount() > 1 && position <= WeatherSetActivity.this.mCityCursor.getCount()) {
                    WeatherSetActivity.this.mCityCursor.moveToPosition(position - 1);
                    String strCityName = WeatherSetActivity.this.mCityCursor.getString(WeatherSetActivity.this.mCityCursor.getColumnIndex("cityname"));
                    String strCityCode = WeatherSetActivity.this.mCityCursor.getString(WeatherSetActivity.this.mCityCursor.getColumnIndex("citycode"));
                    if (WeatherSetActivity.this.mEditor != null) {
                        WeatherSetActivity.this.mEditor.putString("CityCode", strCityCode);
                        WeatherSetActivity.this.mEditor.putString("CityName", strCityName);
                        WeatherSetActivity.this.mEditor.commit();
                        WeatherSetActivity.this.sendBroadcast(new Intent(EventUtils.WEATHER_AREA_CHANGE));
                        WeatherSetActivity.this.finish();
                    }
                }
            } else if (position >= 0 && WeatherSetActivity.this.mCityCursor.getCount() > 0 && position <= WeatherSetActivity.this.mCityCursor.getCount() - 1) {
                WeatherSetActivity.this.mCityCursor.moveToPosition(position);
                WeatherSetActivity.this.getCityList(WeatherSetActivity.this.mCityCursor.getString(WeatherSetActivity.this.mCityCursor.getColumnIndex("quname")));
                WeatherSetActivity.this.mCityAdpt.notifyDataSetChanged();
                WeatherSetActivity.this.mLvCityList.setSelection(0);
            }
        }
    }

    public void onClick(View v) {
        v.getId();
    }
}
