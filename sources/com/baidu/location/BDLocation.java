package com.baidu.location;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import com.baidu.location.Address;
import java.util.ArrayList;
import java.util.List;

public final class BDLocation implements Parcelable {
    public static final String BDLOCATION_BD09LL_TO_GCJ02 = "bd09ll2gcj";
    public static final String BDLOCATION_BD09_TO_GCJ02 = "bd092gcj";
    public static final String BDLOCATION_GCJ02_TO_BD09 = "bd09";
    public static final String BDLOCATION_GCJ02_TO_BD09LL = "bd09ll";
    public static final Parcelable.Creator<BDLocation> CREATOR = new Parcelable.Creator<BDLocation>() {
        public BDLocation createFromParcel(Parcel parcel) {
            return new BDLocation(parcel);
        }

        public BDLocation[] newArray(int i) {
            return new BDLocation[i];
        }
    };
    public static final int LOCATION_WHERE_IN_CN = 1;
    public static final int LOCATION_WHERE_OUT_CN = 0;
    public static final int LOCATION_WHERE_UNKNOW = 2;
    public static final int OPERATORS_TYPE_MOBILE = 1;
    public static final int OPERATORS_TYPE_TELECOMU = 3;
    public static final int OPERATORS_TYPE_UNICOM = 2;
    public static final int OPERATORS_TYPE_UNKONW = 0;
    public static final int TypeCacheLocation = 65;
    public static final int TypeCriteriaException = 62;
    public static final int TypeGpsLocation = 61;
    public static final int TypeNetWorkException = 63;
    public static final int TypeNetWorkLocation = 161;
    public static final int TypeNone = 0;
    public static final int TypeOffLineLocation = 66;
    public static final int TypeOffLineLocationFail = 67;
    public static final int TypeOffLineLocationNetworkFail = 68;
    public static final int TypeServerError = 167;
    private String buildingid;
    private String floor;
    private boolean indoorLocMode;
    private boolean isCellChangeFlag;
    private Address mAddr;
    private String mAddrStr;
    private double mAltitude;
    private String mBuildingName;
    private String mCoorType;
    private String mCu;
    private float mDerect;
    private boolean mHasAddr;
    private boolean mHasAltitude;
    private boolean mHasRadius;
    private boolean mHasSateNumber;
    private boolean mHasSpeed;
    private double mLatitude;
    private int mLocType;
    private int mLocationWhere;
    private double mLongitude;
    private int mOperators;
    private int mParkState;
    private List<Poi> mPoiList;
    private float mRadius;
    private int mSatelliteNumber;
    private String mSemaAptag;
    private String mSemaPoiRegion;
    private String mSemaRegular;
    private float mSpeed;
    private String mTime;
    private String netWorkLocationType;

    public BDLocation() {
        this.mLocType = 0;
        this.mTime = null;
        this.mLatitude = Double.MIN_VALUE;
        this.mLongitude = Double.MIN_VALUE;
        this.mHasAltitude = false;
        this.mAltitude = Double.MIN_VALUE;
        this.mHasSpeed = false;
        this.mSpeed = 0.0f;
        this.mHasRadius = false;
        this.mRadius = 0.0f;
        this.mHasSateNumber = false;
        this.mSatelliteNumber = -1;
        this.mDerect = -1.0f;
        this.mCoorType = null;
        this.mHasAddr = false;
        this.mAddrStr = null;
        this.mSemaAptag = null;
        this.mSemaPoiRegion = null;
        this.mSemaRegular = null;
        this.isCellChangeFlag = false;
        this.mAddr = new Address.Builder().build();
        this.floor = null;
        this.buildingid = null;
        this.mBuildingName = null;
        this.indoorLocMode = false;
        this.mParkState = 0;
        this.mLocationWhere = 1;
        this.netWorkLocationType = null;
        this.mCu = "";
        this.mPoiList = null;
    }

    private BDLocation(Parcel parcel) {
        this.mLocType = 0;
        this.mTime = null;
        this.mLatitude = Double.MIN_VALUE;
        this.mLongitude = Double.MIN_VALUE;
        this.mHasAltitude = false;
        this.mAltitude = Double.MIN_VALUE;
        this.mHasSpeed = false;
        this.mSpeed = 0.0f;
        this.mHasRadius = false;
        this.mRadius = 0.0f;
        this.mHasSateNumber = false;
        this.mSatelliteNumber = -1;
        this.mDerect = -1.0f;
        this.mCoorType = null;
        this.mHasAddr = false;
        this.mAddrStr = null;
        this.mSemaAptag = null;
        this.mSemaPoiRegion = null;
        this.mSemaRegular = null;
        this.isCellChangeFlag = false;
        this.mAddr = new Address.Builder().build();
        this.floor = null;
        this.buildingid = null;
        this.mBuildingName = null;
        this.indoorLocMode = false;
        this.mParkState = 0;
        this.mLocationWhere = 1;
        this.netWorkLocationType = null;
        this.mCu = "";
        this.mPoiList = null;
        this.mLocType = parcel.readInt();
        this.mTime = parcel.readString();
        this.mLatitude = parcel.readDouble();
        this.mLongitude = parcel.readDouble();
        this.mAltitude = parcel.readDouble();
        this.mSpeed = parcel.readFloat();
        this.mRadius = parcel.readFloat();
        this.mSatelliteNumber = parcel.readInt();
        this.mDerect = parcel.readFloat();
        this.floor = parcel.readString();
        this.mParkState = parcel.readInt();
        this.buildingid = parcel.readString();
        this.mBuildingName = parcel.readString();
        this.netWorkLocationType = parcel.readString();
        String readString = parcel.readString();
        String readString2 = parcel.readString();
        String readString3 = parcel.readString();
        String readString4 = parcel.readString();
        String readString5 = parcel.readString();
        String readString6 = parcel.readString();
        parcel.readString();
        String readString7 = parcel.readString();
        this.mAddr = new Address.Builder().country(readString7).countryCode(parcel.readString()).province(readString).city(readString2).cityCode(readString6).district(readString3).street(readString4).streetNumber(readString5).build();
        boolean[] zArr = new boolean[7];
        this.mOperators = parcel.readInt();
        this.mCu = parcel.readString();
        this.mSemaAptag = parcel.readString();
        this.mSemaPoiRegion = parcel.readString();
        this.mSemaRegular = parcel.readString();
        this.mLocationWhere = parcel.readInt();
        try {
            parcel.readBooleanArray(zArr);
            this.mHasAltitude = zArr[0];
            this.mHasSpeed = zArr[1];
            this.mHasRadius = zArr[2];
            this.mHasSateNumber = zArr[3];
            this.mHasAddr = zArr[4];
            this.isCellChangeFlag = zArr[5];
            this.indoorLocMode = zArr[6];
        } catch (Exception e) {
        }
        ArrayList arrayList = new ArrayList();
        parcel.readList(arrayList, Poi.class.getClassLoader());
        if (arrayList.size() == 0) {
            this.mPoiList = null;
        } else {
            this.mPoiList = arrayList;
        }
    }

    public BDLocation(BDLocation bDLocation) {
        int i = 0;
        this.mLocType = 0;
        this.mTime = null;
        this.mLatitude = Double.MIN_VALUE;
        this.mLongitude = Double.MIN_VALUE;
        this.mHasAltitude = false;
        this.mAltitude = Double.MIN_VALUE;
        this.mHasSpeed = false;
        this.mSpeed = 0.0f;
        this.mHasRadius = false;
        this.mRadius = 0.0f;
        this.mHasSateNumber = false;
        this.mSatelliteNumber = -1;
        this.mDerect = -1.0f;
        this.mCoorType = null;
        this.mHasAddr = false;
        this.mAddrStr = null;
        this.mSemaAptag = null;
        this.mSemaPoiRegion = null;
        this.mSemaRegular = null;
        this.isCellChangeFlag = false;
        this.mAddr = new Address.Builder().build();
        this.floor = null;
        this.buildingid = null;
        this.mBuildingName = null;
        this.indoorLocMode = false;
        this.mParkState = 0;
        this.mLocationWhere = 1;
        this.netWorkLocationType = null;
        this.mCu = "";
        this.mPoiList = null;
        this.mLocType = bDLocation.mLocType;
        this.mTime = bDLocation.mTime;
        this.mLatitude = bDLocation.mLatitude;
        this.mLongitude = bDLocation.mLongitude;
        this.mHasAltitude = bDLocation.mHasAltitude;
        this.mAltitude = bDLocation.mAltitude;
        this.mHasSpeed = bDLocation.mHasSpeed;
        this.mSpeed = bDLocation.mSpeed;
        this.mHasRadius = bDLocation.mHasRadius;
        this.mRadius = bDLocation.mRadius;
        this.mHasSateNumber = bDLocation.mHasSateNumber;
        this.mSatelliteNumber = bDLocation.mSatelliteNumber;
        this.mDerect = bDLocation.mDerect;
        this.mCoorType = bDLocation.mCoorType;
        this.mHasAddr = bDLocation.mHasAddr;
        this.mAddrStr = bDLocation.mAddrStr;
        this.isCellChangeFlag = bDLocation.isCellChangeFlag;
        this.mAddr = new Address.Builder().country(bDLocation.mAddr.country).countryCode(bDLocation.mAddr.countryCode).province(bDLocation.mAddr.province).city(bDLocation.mAddr.city).cityCode(bDLocation.mAddr.cityCode).district(bDLocation.mAddr.district).street(bDLocation.mAddr.street).streetNumber(bDLocation.mAddr.streetNumber).build();
        this.floor = bDLocation.floor;
        this.buildingid = bDLocation.buildingid;
        this.mBuildingName = bDLocation.mBuildingName;
        this.mLocationWhere = bDLocation.mLocationWhere;
        this.mParkState = bDLocation.mParkState;
        this.indoorLocMode = bDLocation.indoorLocMode;
        this.netWorkLocationType = bDLocation.netWorkLocationType;
        this.mOperators = bDLocation.mOperators;
        this.mCu = bDLocation.mCu;
        this.mSemaAptag = bDLocation.mSemaAptag;
        this.mSemaPoiRegion = bDLocation.mSemaPoiRegion;
        this.mSemaRegular = bDLocation.mSemaRegular;
        if (bDLocation.mPoiList == null) {
            this.mPoiList = null;
            return;
        }
        ArrayList arrayList = new ArrayList();
        while (true) {
            int i2 = i;
            if (i2 < bDLocation.mPoiList.size()) {
                Poi poi = bDLocation.mPoiList.get(i2);
                arrayList.add(new Poi(poi.getId(), poi.getName(), poi.getRank()));
                i = i2 + 1;
            } else {
                this.mPoiList = arrayList;
                return;
            }
        }
    }

    /* JADX WARNING: Unknown top exception splitter block from list: {B:17:0x0105=Splitter:B:17:0x0105, B:112:0x0305=Splitter:B:112:0x0305} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public BDLocation(java.lang.String r15) {
        /*
            r14 = this;
            r13 = 2
            r2 = 1
            r12 = 1
            r8 = 0
            r0 = 0
            r14.<init>()
            r14.mLocType = r8
            r14.mTime = r0
            r14.mLatitude = r2
            r14.mLongitude = r2
            r14.mHasAltitude = r8
            r14.mAltitude = r2
            r14.mHasSpeed = r8
            r1 = 0
            r14.mSpeed = r1
            r14.mHasRadius = r8
            r1 = 0
            r14.mRadius = r1
            r14.mHasSateNumber = r8
            r1 = -1
            r14.mSatelliteNumber = r1
            r1 = -1082130432(0xffffffffbf800000, float:-1.0)
            r14.mDerect = r1
            r14.mCoorType = r0
            r14.mHasAddr = r8
            r14.mAddrStr = r0
            r14.mSemaAptag = r0
            r14.mSemaPoiRegion = r0
            r14.mSemaRegular = r0
            r14.isCellChangeFlag = r8
            com.baidu.location.Address$Builder r1 = new com.baidu.location.Address$Builder
            r1.<init>()
            com.baidu.location.Address r1 = r1.build()
            r14.mAddr = r1
            r14.floor = r0
            r14.buildingid = r0
            r14.mBuildingName = r0
            r14.indoorLocMode = r8
            r14.mParkState = r8
            r14.mLocationWhere = r12
            r14.netWorkLocationType = r0
            java.lang.String r1 = ""
            r14.mCu = r1
            r14.mPoiList = r0
            if (r15 == 0) goto L_0x005f
            java.lang.String r1 = ""
            boolean r1 = r15.equals(r1)
            if (r1 == 0) goto L_0x0060
        L_0x005f:
            return
        L_0x0060:
            org.json.JSONObject r1 = new org.json.JSONObject     // Catch:{ Exception -> 0x0110 }
            r1.<init>(r15)     // Catch:{ Exception -> 0x0110 }
            java.lang.String r2 = "result"
            org.json.JSONObject r2 = r1.getJSONObject(r2)     // Catch:{ Exception -> 0x0110 }
            java.lang.String r3 = "error"
            java.lang.String r3 = r2.getString(r3)     // Catch:{ Exception -> 0x0110 }
            int r3 = java.lang.Integer.parseInt(r3)     // Catch:{ Exception -> 0x0110 }
            r14.setLocType(r3)     // Catch:{ Exception -> 0x0110 }
            java.lang.String r4 = "time"
            java.lang.String r2 = r2.getString(r4)     // Catch:{ Exception -> 0x0110 }
            r14.setTime(r2)     // Catch:{ Exception -> 0x0110 }
            r2 = 61
            if (r3 != r2) goto L_0x0128
            java.lang.String r0 = "content"
            org.json.JSONObject r0 = r1.getJSONObject(r0)     // Catch:{ Exception -> 0x0110 }
            java.lang.String r1 = "point"
            org.json.JSONObject r1 = r0.getJSONObject(r1)     // Catch:{ Exception -> 0x0110 }
            java.lang.String r2 = "y"
            java.lang.String r2 = r1.getString(r2)     // Catch:{ Exception -> 0x0110 }
            double r2 = java.lang.Double.parseDouble(r2)     // Catch:{ Exception -> 0x0110 }
            r14.setLatitude(r2)     // Catch:{ Exception -> 0x0110 }
            java.lang.String r2 = "x"
            java.lang.String r1 = r1.getString(r2)     // Catch:{ Exception -> 0x0110 }
            double r2 = java.lang.Double.parseDouble(r1)     // Catch:{ Exception -> 0x0110 }
            r14.setLongitude(r2)     // Catch:{ Exception -> 0x0110 }
            java.lang.String r1 = "radius"
            java.lang.String r1 = r0.getString(r1)     // Catch:{ Exception -> 0x0110 }
            float r1 = java.lang.Float.parseFloat(r1)     // Catch:{ Exception -> 0x0110 }
            r14.setRadius(r1)     // Catch:{ Exception -> 0x0110 }
            java.lang.String r1 = "s"
            java.lang.String r1 = r0.getString(r1)     // Catch:{ Exception -> 0x0110 }
            float r1 = java.lang.Float.parseFloat(r1)     // Catch:{ Exception -> 0x0110 }
            r14.setSpeed(r1)     // Catch:{ Exception -> 0x0110 }
            java.lang.String r1 = "d"
            java.lang.String r1 = r0.getString(r1)     // Catch:{ Exception -> 0x0110 }
            float r1 = java.lang.Float.parseFloat(r1)     // Catch:{ Exception -> 0x0110 }
            r14.setDirection(r1)     // Catch:{ Exception -> 0x0110 }
            java.lang.String r1 = "n"
            java.lang.String r1 = r0.getString(r1)     // Catch:{ Exception -> 0x0110 }
            int r1 = java.lang.Integer.parseInt(r1)     // Catch:{ Exception -> 0x0110 }
            r14.setSatelliteNumber(r1)     // Catch:{ Exception -> 0x0110 }
            java.lang.String r1 = "h"
            boolean r1 = r0.has(r1)     // Catch:{ Exception -> 0x0110 }
            if (r1 == 0) goto L_0x00f0
            java.lang.String r1 = "h"
            double r2 = r0.getDouble(r1)     // Catch:{ Exception -> 0x039b }
            r14.setAltitude(r2)     // Catch:{ Exception -> 0x039b }
        L_0x00f0:
            java.lang.String r1 = "in_cn"
            boolean r1 = r0.has(r1)     // Catch:{ Exception -> 0x011f }
            if (r1 == 0) goto L_0x011a
            java.lang.String r1 = "in_cn"
            java.lang.String r0 = r0.getString(r1)     // Catch:{ Exception -> 0x011f }
            int r0 = java.lang.Integer.parseInt(r0)     // Catch:{ Exception -> 0x011f }
            r14.setLocationWhere(r0)     // Catch:{ Exception -> 0x011f }
        L_0x0105:
            int r0 = r14.mLocationWhere     // Catch:{ Exception -> 0x0110 }
            if (r0 != 0) goto L_0x0121
            java.lang.String r0 = "wgs84"
            r14.setCoorType(r0)     // Catch:{ Exception -> 0x0110 }
            goto L_0x005f
        L_0x0110:
            r0 = move-exception
            r0.printStackTrace()
            r14.mLocType = r8
            r14.mHasAddr = r8
            goto L_0x005f
        L_0x011a:
            r0 = 1
            r14.setLocationWhere(r0)     // Catch:{ Exception -> 0x011f }
            goto L_0x0105
        L_0x011f:
            r0 = move-exception
            goto L_0x0105
        L_0x0121:
            java.lang.String r0 = "gcj02"
            r14.setCoorType(r0)     // Catch:{ Exception -> 0x0110 }
            goto L_0x005f
        L_0x0128:
            r2 = 161(0xa1, float:2.26E-43)
            if (r3 != r2) goto L_0x033e
            java.lang.String r2 = "content"
            org.json.JSONObject r9 = r1.getJSONObject(r2)     // Catch:{ Exception -> 0x0110 }
            java.lang.String r1 = "point"
            org.json.JSONObject r1 = r9.getJSONObject(r1)     // Catch:{ Exception -> 0x0110 }
            java.lang.String r2 = "y"
            java.lang.String r2 = r1.getString(r2)     // Catch:{ Exception -> 0x0110 }
            double r2 = java.lang.Double.parseDouble(r2)     // Catch:{ Exception -> 0x0110 }
            r14.setLatitude(r2)     // Catch:{ Exception -> 0x0110 }
            java.lang.String r2 = "x"
            java.lang.String r1 = r1.getString(r2)     // Catch:{ Exception -> 0x0110 }
            double r2 = java.lang.Double.parseDouble(r1)     // Catch:{ Exception -> 0x0110 }
            r14.setLongitude(r2)     // Catch:{ Exception -> 0x0110 }
            java.lang.String r1 = "radius"
            java.lang.String r1 = r9.getString(r1)     // Catch:{ Exception -> 0x0110 }
            float r1 = java.lang.Float.parseFloat(r1)     // Catch:{ Exception -> 0x0110 }
            r14.setRadius(r1)     // Catch:{ Exception -> 0x0110 }
            java.lang.String r1 = "sema"
            boolean r1 = r9.has(r1)     // Catch:{ Exception -> 0x0110 }
            if (r1 == 0) goto L_0x01f7
            java.lang.String r1 = "sema"
            org.json.JSONObject r2 = r9.getJSONObject(r1)     // Catch:{ Exception -> 0x0110 }
            java.lang.String r1 = "aptag"
            boolean r1 = r2.has(r1)     // Catch:{ Exception -> 0x0110 }
            if (r1 == 0) goto L_0x0183
            java.lang.String r1 = "aptag"
            java.lang.String r1 = r2.getString(r1)     // Catch:{ Exception -> 0x0110 }
            boolean r3 = android.text.TextUtils.isEmpty(r1)     // Catch:{ Exception -> 0x0110 }
            if (r3 != 0) goto L_0x01c4
            r14.mSemaAptag = r1     // Catch:{ Exception -> 0x0110 }
        L_0x0183:
            java.lang.String r1 = "aptagd"
            boolean r1 = r2.has(r1)     // Catch:{ Exception -> 0x0110 }
            if (r1 == 0) goto L_0x01cb
            java.lang.String r1 = "aptagd"
            org.json.JSONObject r1 = r2.getJSONObject(r1)     // Catch:{ Exception -> 0x0110 }
            java.lang.String r3 = "pois"
            org.json.JSONArray r3 = r1.getJSONArray(r3)     // Catch:{ Exception -> 0x0110 }
            java.util.ArrayList r4 = new java.util.ArrayList     // Catch:{ Exception -> 0x0110 }
            r4.<init>()     // Catch:{ Exception -> 0x0110 }
            r1 = r8
        L_0x019d:
            int r5 = r3.length()     // Catch:{ Exception -> 0x0110 }
            if (r1 >= r5) goto L_0x01c9
            org.json.JSONObject r5 = r3.getJSONObject(r1)     // Catch:{ Exception -> 0x0110 }
            java.lang.String r6 = "pname"
            java.lang.String r6 = r5.getString(r6)     // Catch:{ Exception -> 0x0110 }
            java.lang.String r7 = "pid"
            java.lang.String r7 = r5.getString(r7)     // Catch:{ Exception -> 0x0110 }
            java.lang.String r10 = "pr"
            double r10 = r5.getDouble(r10)     // Catch:{ Exception -> 0x0110 }
            com.baidu.location.Poi r5 = new com.baidu.location.Poi     // Catch:{ Exception -> 0x0110 }
            r5.<init>(r7, r6, r10)     // Catch:{ Exception -> 0x0110 }
            r4.add(r5)     // Catch:{ Exception -> 0x0110 }
            int r1 = r1 + 1
            goto L_0x019d
        L_0x01c4:
            java.lang.String r1 = ""
            r14.mSemaAptag = r1     // Catch:{ Exception -> 0x0110 }
            goto L_0x0183
        L_0x01c9:
            r14.mPoiList = r4     // Catch:{ Exception -> 0x0110 }
        L_0x01cb:
            java.lang.String r1 = "poiregion"
            boolean r1 = r2.has(r1)     // Catch:{ Exception -> 0x0110 }
            if (r1 == 0) goto L_0x01e1
            java.lang.String r1 = "poiregion"
            java.lang.String r1 = r2.getString(r1)     // Catch:{ Exception -> 0x0110 }
            boolean r3 = android.text.TextUtils.isEmpty(r1)     // Catch:{ Exception -> 0x0110 }
            if (r3 != 0) goto L_0x01e1
            r14.mSemaPoiRegion = r1     // Catch:{ Exception -> 0x0110 }
        L_0x01e1:
            java.lang.String r1 = "regular"
            boolean r1 = r2.has(r1)     // Catch:{ Exception -> 0x0110 }
            if (r1 == 0) goto L_0x01f7
            java.lang.String r1 = "regular"
            java.lang.String r1 = r2.getString(r1)     // Catch:{ Exception -> 0x0110 }
            boolean r2 = android.text.TextUtils.isEmpty(r1)     // Catch:{ Exception -> 0x0110 }
            if (r2 != 0) goto L_0x01f7
            r14.mSemaRegular = r1     // Catch:{ Exception -> 0x0110 }
        L_0x01f7:
            java.lang.String r1 = "addr"
            boolean r1 = r9.has(r1)     // Catch:{ Exception -> 0x0110 }
            if (r1 == 0) goto L_0x0310
            java.lang.String r1 = "addr"
            java.lang.String r1 = r9.getString(r1)     // Catch:{ Exception -> 0x0110 }
            java.lang.String r2 = ","
            java.lang.String[] r10 = r1.split(r2)     // Catch:{ Exception -> 0x0110 }
            int r11 = r10.length     // Catch:{ Exception -> 0x0110 }
            if (r11 <= 0) goto L_0x03b0
            r1 = 0
            r1 = r10[r1]     // Catch:{ Exception -> 0x0110 }
            r7 = r1
        L_0x0212:
            if (r11 <= r12) goto L_0x03ad
            r1 = 1
            r1 = r10[r1]     // Catch:{ Exception -> 0x0110 }
            r6 = r1
        L_0x0218:
            if (r11 <= r13) goto L_0x03aa
            r1 = 2
            r1 = r10[r1]     // Catch:{ Exception -> 0x0110 }
            r5 = r1
        L_0x021e:
            r1 = 3
            if (r11 <= r1) goto L_0x03a7
            r1 = 3
            r1 = r10[r1]     // Catch:{ Exception -> 0x0110 }
            r4 = r1
        L_0x0225:
            r1 = 4
            if (r11 <= r1) goto L_0x03a4
            r1 = 4
            r1 = r10[r1]     // Catch:{ Exception -> 0x0110 }
            r3 = r1
        L_0x022c:
            r1 = 5
            if (r11 <= r1) goto L_0x03a1
            r1 = 5
            r1 = r10[r1]     // Catch:{ Exception -> 0x0110 }
            r2 = r1
        L_0x0233:
            r1 = 6
            if (r11 <= r1) goto L_0x039e
            r1 = 6
            r1 = r10[r1]     // Catch:{ Exception -> 0x0110 }
        L_0x0239:
            r12 = 7
            if (r11 <= r12) goto L_0x023f
            r0 = 7
            r0 = r10[r0]     // Catch:{ Exception -> 0x0110 }
        L_0x023f:
            com.baidu.location.Address$Builder r10 = new com.baidu.location.Address$Builder     // Catch:{ Exception -> 0x0110 }
            r10.<init>()     // Catch:{ Exception -> 0x0110 }
            com.baidu.location.Address$Builder r1 = r10.country(r1)     // Catch:{ Exception -> 0x0110 }
            com.baidu.location.Address$Builder r0 = r1.countryCode(r0)     // Catch:{ Exception -> 0x0110 }
            com.baidu.location.Address$Builder r0 = r0.province(r7)     // Catch:{ Exception -> 0x0110 }
            com.baidu.location.Address$Builder r0 = r0.city(r6)     // Catch:{ Exception -> 0x0110 }
            com.baidu.location.Address$Builder r0 = r0.cityCode(r2)     // Catch:{ Exception -> 0x0110 }
            com.baidu.location.Address$Builder r0 = r0.district(r5)     // Catch:{ Exception -> 0x0110 }
            com.baidu.location.Address$Builder r0 = r0.street(r4)     // Catch:{ Exception -> 0x0110 }
            com.baidu.location.Address$Builder r0 = r0.streetNumber(r3)     // Catch:{ Exception -> 0x0110 }
            com.baidu.location.Address r0 = r0.build()     // Catch:{ Exception -> 0x0110 }
            r14.mAddr = r0     // Catch:{ Exception -> 0x0110 }
            r0 = 1
            r14.mHasAddr = r0     // Catch:{ Exception -> 0x0110 }
        L_0x026d:
            java.lang.String r0 = "floor"
            boolean r0 = r9.has(r0)     // Catch:{ Exception -> 0x0110 }
            if (r0 == 0) goto L_0x0288
            java.lang.String r0 = "floor"
            java.lang.String r0 = r9.getString(r0)     // Catch:{ Exception -> 0x0110 }
            r14.floor = r0     // Catch:{ Exception -> 0x0110 }
            java.lang.String r0 = r14.floor     // Catch:{ Exception -> 0x0110 }
            boolean r0 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x0110 }
            if (r0 == 0) goto L_0x0288
            r0 = 0
            r14.floor = r0     // Catch:{ Exception -> 0x0110 }
        L_0x0288:
            java.lang.String r0 = "loctp"
            boolean r0 = r9.has(r0)     // Catch:{ Exception -> 0x0110 }
            if (r0 == 0) goto L_0x02a3
            java.lang.String r0 = "loctp"
            java.lang.String r0 = r9.getString(r0)     // Catch:{ Exception -> 0x0110 }
            r14.netWorkLocationType = r0     // Catch:{ Exception -> 0x0110 }
            java.lang.String r0 = r14.netWorkLocationType     // Catch:{ Exception -> 0x0110 }
            boolean r0 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x0110 }
            if (r0 == 0) goto L_0x02a3
            r0 = 0
            r14.netWorkLocationType = r0     // Catch:{ Exception -> 0x0110 }
        L_0x02a3:
            java.lang.String r0 = "bldgid"
            boolean r0 = r9.has(r0)     // Catch:{ Exception -> 0x0110 }
            if (r0 == 0) goto L_0x02be
            java.lang.String r0 = "bldgid"
            java.lang.String r0 = r9.getString(r0)     // Catch:{ Exception -> 0x0110 }
            r14.buildingid = r0     // Catch:{ Exception -> 0x0110 }
            java.lang.String r0 = r14.buildingid     // Catch:{ Exception -> 0x0110 }
            boolean r0 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x0110 }
            if (r0 == 0) goto L_0x02be
            r0 = 0
            r14.buildingid = r0     // Catch:{ Exception -> 0x0110 }
        L_0x02be:
            java.lang.String r0 = "bldg"
            boolean r0 = r9.has(r0)     // Catch:{ Exception -> 0x0110 }
            if (r0 == 0) goto L_0x02d9
            java.lang.String r0 = "bldg"
            java.lang.String r0 = r9.getString(r0)     // Catch:{ Exception -> 0x0110 }
            r14.mBuildingName = r0     // Catch:{ Exception -> 0x0110 }
            java.lang.String r0 = r14.mBuildingName     // Catch:{ Exception -> 0x0110 }
            boolean r0 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x0110 }
            if (r0 == 0) goto L_0x02d9
            r0 = 0
            r14.mBuildingName = r0     // Catch:{ Exception -> 0x0110 }
        L_0x02d9:
            java.lang.String r0 = "ibav"
            boolean r0 = r9.has(r0)     // Catch:{ Exception -> 0x0110 }
            if (r0 == 0) goto L_0x02f0
            java.lang.String r0 = "ibav"
            java.lang.String r0 = r9.getString(r0)     // Catch:{ Exception -> 0x0110 }
            boolean r1 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x0110 }
            if (r1 == 0) goto L_0x0319
            r0 = 0
            r14.mParkState = r0     // Catch:{ Exception -> 0x0110 }
        L_0x02f0:
            java.lang.String r0 = "in_cn"
            boolean r0 = r9.has(r0)     // Catch:{ Exception -> 0x0335 }
            if (r0 == 0) goto L_0x0330
            java.lang.String r0 = "in_cn"
            java.lang.String r0 = r9.getString(r0)     // Catch:{ Exception -> 0x0335 }
            int r0 = java.lang.Integer.parseInt(r0)     // Catch:{ Exception -> 0x0335 }
            r14.setLocationWhere(r0)     // Catch:{ Exception -> 0x0335 }
        L_0x0305:
            int r0 = r14.mLocationWhere     // Catch:{ Exception -> 0x0110 }
            if (r0 != 0) goto L_0x0337
            java.lang.String r0 = "wgs84"
            r14.setCoorType(r0)     // Catch:{ Exception -> 0x0110 }
            goto L_0x005f
        L_0x0310:
            r0 = 0
            r14.mHasAddr = r0     // Catch:{ Exception -> 0x0110 }
            r0 = 0
            r14.setAddrStr(r0)     // Catch:{ Exception -> 0x0110 }
            goto L_0x026d
        L_0x0319:
            java.lang.String r1 = "0"
            boolean r1 = r0.equals(r1)     // Catch:{ Exception -> 0x0110 }
            if (r1 == 0) goto L_0x0325
            r0 = 0
            r14.mParkState = r0     // Catch:{ Exception -> 0x0110 }
            goto L_0x02f0
        L_0x0325:
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)     // Catch:{ Exception -> 0x0110 }
            int r0 = r0.intValue()     // Catch:{ Exception -> 0x0110 }
            r14.mParkState = r0     // Catch:{ Exception -> 0x0110 }
            goto L_0x02f0
        L_0x0330:
            r0 = 1
            r14.setLocationWhere(r0)     // Catch:{ Exception -> 0x0335 }
            goto L_0x0305
        L_0x0335:
            r0 = move-exception
            goto L_0x0305
        L_0x0337:
            java.lang.String r0 = "gcj02"
            r14.setCoorType(r0)     // Catch:{ Exception -> 0x0110 }
            goto L_0x005f
        L_0x033e:
            r0 = 66
            if (r3 == r0) goto L_0x0346
            r0 = 68
            if (r3 != r0) goto L_0x0391
        L_0x0346:
            java.lang.String r0 = "content"
            org.json.JSONObject r0 = r1.getJSONObject(r0)     // Catch:{ Exception -> 0x0110 }
            java.lang.String r1 = "point"
            org.json.JSONObject r1 = r0.getJSONObject(r1)     // Catch:{ Exception -> 0x0110 }
            java.lang.String r2 = "y"
            java.lang.String r2 = r1.getString(r2)     // Catch:{ Exception -> 0x0110 }
            double r2 = java.lang.Double.parseDouble(r2)     // Catch:{ Exception -> 0x0110 }
            r14.setLatitude(r2)     // Catch:{ Exception -> 0x0110 }
            java.lang.String r2 = "x"
            java.lang.String r1 = r1.getString(r2)     // Catch:{ Exception -> 0x0110 }
            double r2 = java.lang.Double.parseDouble(r1)     // Catch:{ Exception -> 0x0110 }
            r14.setLongitude(r2)     // Catch:{ Exception -> 0x0110 }
            java.lang.String r1 = "radius"
            java.lang.String r1 = r0.getString(r1)     // Catch:{ Exception -> 0x0110 }
            float r1 = java.lang.Float.parseFloat(r1)     // Catch:{ Exception -> 0x0110 }
            r14.setRadius(r1)     // Catch:{ Exception -> 0x0110 }
            java.lang.String r1 = "isCellChanged"
            java.lang.String r0 = r0.getString(r1)     // Catch:{ Exception -> 0x0110 }
            boolean r0 = java.lang.Boolean.parseBoolean(r0)     // Catch:{ Exception -> 0x0110 }
            java.lang.Boolean r0 = java.lang.Boolean.valueOf(r0)     // Catch:{ Exception -> 0x0110 }
            r14.setCellChangeFlag(r0)     // Catch:{ Exception -> 0x0110 }
            java.lang.String r0 = "gcj02"
            r14.setCoorType(r0)     // Catch:{ Exception -> 0x0110 }
            goto L_0x005f
        L_0x0391:
            r0 = 167(0xa7, float:2.34E-43)
            if (r3 != r0) goto L_0x005f
            r0 = 2
            r14.setLocationWhere(r0)     // Catch:{ Exception -> 0x0110 }
            goto L_0x005f
        L_0x039b:
            r1 = move-exception
            goto L_0x00f0
        L_0x039e:
            r1 = r0
            goto L_0x0239
        L_0x03a1:
            r2 = r0
            goto L_0x0233
        L_0x03a4:
            r3 = r0
            goto L_0x022c
        L_0x03a7:
            r4 = r0
            goto L_0x0225
        L_0x03aa:
            r5 = r0
            goto L_0x021e
        L_0x03ad:
            r6 = r0
            goto L_0x0218
        L_0x03b0:
            r7 = r0
            goto L_0x0212
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baidu.location.BDLocation.<init>(java.lang.String):void");
    }

    private String getCuid() {
        return this.mCu;
    }

    private static String getModel() {
        return Build.MODEL;
    }

    private String getSemaPoiRegion() {
        return this.mSemaPoiRegion;
    }

    private String getSemaRegular() {
        return this.mSemaRegular;
    }

    private void setCellChangeFlag(Boolean bool) {
        this.isCellChangeFlag = bool.booleanValue();
    }

    public int describeContents() {
        return 0;
    }

    public String getAdUrl(String str) {
        String valueOf = String.valueOf(this.mLatitude);
        String valueOf2 = String.valueOf(this.mLongitude);
        String cuid = getCuid();
        return "http://lba.baidu.com/" + "?a=" + Jni.Encrypt("ak=" + str + "&" + "lat=" + valueOf + "&" + "lng=" + valueOf2 + "&" + "cu=" + cuid + "&" + "mb=" + getModel());
    }

    public String getAddrStr() {
        return this.mAddr.address;
    }

    public Address getAddress() {
        return this.mAddr;
    }

    public double getAltitude() {
        return this.mAltitude;
    }

    public String getBuildingID() {
        return this.buildingid;
    }

    public String getBuildingName() {
        return this.mBuildingName;
    }

    public String getCity() {
        return this.mAddr.city;
    }

    public String getCityCode() {
        return this.mAddr.cityCode;
    }

    public String getCoorType() {
        return this.mCoorType;
    }

    public String getCountry() {
        return this.mAddr.country;
    }

    public String getCountryCode() {
        return this.mAddr.countryCode;
    }

    public float getDerect() {
        return this.mDerect;
    }

    public float getDirection() {
        return this.mDerect;
    }

    public String getDistrict() {
        return this.mAddr.district;
    }

    public String getFloor() {
        return this.floor;
    }

    public double getLatitude() {
        return this.mLatitude;
    }

    public int getLocType() {
        return this.mLocType;
    }

    public String getLocationDescribe() {
        return this.mSemaAptag;
    }

    public int getLocationWhere() {
        return this.mLocationWhere;
    }

    public double getLongitude() {
        return this.mLongitude;
    }

    public String getNetworkLocationType() {
        return this.netWorkLocationType;
    }

    public int getOperators() {
        return this.mOperators;
    }

    public List<Poi> getPoiList() {
        return this.mPoiList;
    }

    public String getProvince() {
        return this.mAddr.province;
    }

    public float getRadius() {
        return this.mRadius;
    }

    public int getSatelliteNumber() {
        this.mHasSateNumber = true;
        return this.mSatelliteNumber;
    }

    public String getSemaAptag() {
        return this.mSemaAptag;
    }

    public float getSpeed() {
        return this.mSpeed;
    }

    public String getStreet() {
        return this.mAddr.street;
    }

    public String getStreetNumber() {
        return this.mAddr.streetNumber;
    }

    public String getTime() {
        return this.mTime;
    }

    public boolean hasAddr() {
        return this.mHasAddr;
    }

    public boolean hasAltitude() {
        return this.mHasAltitude;
    }

    public boolean hasRadius() {
        return this.mHasRadius;
    }

    public boolean hasSateNumber() {
        return this.mHasSateNumber;
    }

    public boolean hasSpeed() {
        return this.mHasSpeed;
    }

    public void internalSet(int i, String str) {
        if (str != null && i == 0) {
            this.mCu = str;
        }
    }

    public boolean isCellChangeFlag() {
        return this.isCellChangeFlag;
    }

    public boolean isIndoorLocMode() {
        return this.indoorLocMode;
    }

    public int isParkAvailable() {
        return this.mParkState;
    }

    public void setAddr(Address address) {
        if (address != null) {
            this.mAddr = address;
            this.mHasAddr = true;
        }
    }

    public void setAddrStr(String str) {
        this.mAddrStr = str;
        if (str == null) {
            this.mHasAddr = false;
        } else {
            this.mHasAddr = true;
        }
    }

    public void setAltitude(double d) {
        this.mAltitude = d;
        this.mHasAltitude = true;
    }

    public void setBuildingID(String str) {
        this.buildingid = str;
    }

    public void setBuildingName(String str) {
        this.mBuildingName = str;
    }

    public void setCoorType(String str) {
        this.mCoorType = str;
    }

    public void setDirection(float f) {
        this.mDerect = f;
    }

    public void setFloor(String str) {
        this.floor = str;
    }

    public void setIndoorLocMode(boolean z) {
        this.indoorLocMode = z;
    }

    public void setLatitude(double d) {
        this.mLatitude = d;
    }

    public void setLocType(int i) {
        this.mLocType = i;
    }

    public void setLocationDescribe(String str) {
        this.mSemaAptag = str;
    }

    public void setLocationWhere(int i) {
        this.mLocationWhere = i;
    }

    public void setLongitude(double d) {
        this.mLongitude = d;
    }

    public void setNetworkLocationType(String str) {
        this.netWorkLocationType = str;
    }

    public void setOperators(int i) {
        this.mOperators = i;
    }

    public void setParkAvailable(int i) {
        this.mParkState = i;
    }

    public void setPoiList(List<Poi> list) {
        this.mPoiList = list;
    }

    public void setRadius(float f) {
        this.mRadius = f;
        this.mHasRadius = true;
    }

    public void setSatelliteNumber(int i) {
        this.mSatelliteNumber = i;
    }

    public void setSpeed(float f) {
        this.mSpeed = f;
        this.mHasSpeed = true;
    }

    public void setTime(String str) {
        this.mTime = str;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.mLocType);
        parcel.writeString(this.mTime);
        parcel.writeDouble(this.mLatitude);
        parcel.writeDouble(this.mLongitude);
        parcel.writeDouble(this.mAltitude);
        parcel.writeFloat(this.mSpeed);
        parcel.writeFloat(this.mRadius);
        parcel.writeInt(this.mSatelliteNumber);
        parcel.writeFloat(this.mDerect);
        parcel.writeString(this.floor);
        parcel.writeInt(this.mParkState);
        parcel.writeString(this.buildingid);
        parcel.writeString(this.mBuildingName);
        parcel.writeString(this.netWorkLocationType);
        parcel.writeString(this.mAddr.province);
        parcel.writeString(this.mAddr.city);
        parcel.writeString(this.mAddr.district);
        parcel.writeString(this.mAddr.street);
        parcel.writeString(this.mAddr.streetNumber);
        parcel.writeString(this.mAddr.cityCode);
        parcel.writeString(this.mAddr.address);
        parcel.writeString(this.mAddr.country);
        parcel.writeString(this.mAddr.countryCode);
        parcel.writeInt(this.mOperators);
        parcel.writeString(this.mCu);
        parcel.writeString(this.mSemaAptag);
        parcel.writeString(this.mSemaPoiRegion);
        parcel.writeString(this.mSemaRegular);
        parcel.writeInt(this.mLocationWhere);
        parcel.writeBooleanArray(new boolean[]{this.mHasAltitude, this.mHasSpeed, this.mHasRadius, this.mHasSateNumber, this.mHasAddr, this.isCellChangeFlag, this.indoorLocMode});
        parcel.writeList(this.mPoiList);
    }
}
