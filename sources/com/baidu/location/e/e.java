package com.baidu.location.e;

import android.database.Cursor;
import android.database.MatrixCursor;
import com.baidu.location.Address;
import com.baidu.location.BDLocation;
import com.baidu.location.Poi;
import com.baidu.location.f.b;
import com.baidu.location.f.i;
import com.baidu.location.h.c;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

final class e {
    private static final String[] a = {"CoorType", "Time", "LocType", "Longitude", "Latitude", "Radius", "NetworkLocationType", "Country", "CountryCode", "Province", "City", "CityCode", "District", "Street", "StreetNumber", "PoiList", "LocationDescription"};

    static final class a {
        final String a;
        final String b;
        final boolean c;
        final boolean d;
        final boolean e;
        final int f;
        final BDLocation g;
        final boolean h;
        final LinkedHashMap<String, Integer> i;

        public a(String[] strArr) {
            boolean z;
            if (strArr == null) {
                this.a = null;
                this.b = null;
                this.i = null;
                this.c = false;
                this.d = false;
                this.e = false;
                this.g = null;
                this.h = false;
                this.f = 8;
                return;
            }
            LinkedHashMap<String, Integer> linkedHashMap = new LinkedHashMap<>();
            int i2 = 0;
            int i3 = 8;
            boolean z2 = false;
            boolean z3 = false;
            boolean z4 = false;
            boolean z5 = false;
            BDLocation bDLocation = null;
            String str = null;
            String str2 = null;
            while (i2 < strArr.length) {
                try {
                    if (strArr[i2].equals("-loc")) {
                        str2 = strArr[i2 + 1];
                        String[] split = str2.split("&");
                        for (int i4 = 0; i4 < split.length; i4++) {
                            if (split[i4].startsWith("cl=")) {
                                str = split[i4].substring(3);
                            } else if (split[i4].startsWith("wf=")) {
                                String[] split2 = split[i4].substring(3).split("\\|");
                                for (String split3 : split2) {
                                    String[] split4 = split3.split(";");
                                    if (split4.length >= 2) {
                                        linkedHashMap.put(split4[0], Integer.valueOf(split4[1]));
                                    }
                                }
                            }
                        }
                    } else if (strArr[i2].equals("-com")) {
                        String[] split5 = strArr[i2 + 1].split(";");
                        if (split5.length > 0) {
                            BDLocation bDLocation2 = new BDLocation();
                            try {
                                bDLocation2.setLatitude(Double.valueOf(split5[0]).doubleValue());
                                bDLocation2.setLongitude(Double.valueOf(split5[1]).doubleValue());
                                bDLocation2.setLocType(Integer.valueOf(split5[2]).intValue());
                                bDLocation2.setNetworkLocationType(split5[3]);
                                bDLocation = bDLocation2;
                            } catch (Exception e2) {
                                bDLocation = bDLocation2;
                                z = false;
                                this.a = str2;
                                this.b = str;
                                this.i = linkedHashMap;
                                this.c = z;
                                this.d = z4;
                                this.e = z3;
                                this.f = i3;
                                this.g = bDLocation;
                                this.h = z2;
                            }
                        }
                    } else if (strArr[i2].equals("-log")) {
                        if (strArr[i2 + 1].equals("true")) {
                            z5 = true;
                        }
                    } else if (strArr[i2].equals("-rgc")) {
                        if (strArr[i2 + 1].equals("true")) {
                            z3 = true;
                        }
                    } else if (strArr[i2].equals("-poi")) {
                        if (strArr[i2 + 1].equals("true")) {
                            z4 = true;
                        }
                    } else if (strArr[i2].equals("-minap")) {
                        try {
                            i3 = Integer.valueOf(strArr[i2 + 1]).intValue();
                        } catch (Exception e3) {
                        }
                    } else if (strArr[i2].equals("-des") && strArr[i2 + 1].equals("true")) {
                        z2 = true;
                    }
                    i2 += 2;
                } catch (Exception e4) {
                    z = false;
                    this.a = str2;
                    this.b = str;
                    this.i = linkedHashMap;
                    this.c = z;
                    this.d = z4;
                    this.e = z3;
                    this.f = i3;
                    this.g = bDLocation;
                    this.h = z2;
                }
            }
            str2 = !z5 ? null : str2;
            z = true;
            this.a = str2;
            this.b = str;
            this.i = linkedHashMap;
            this.c = z;
            this.d = z4;
            this.e = z3;
            this.f = i3;
            this.g = bDLocation;
            this.h = z2;
        }
    }

    static Cursor a(BDLocation bDLocation) {
        String str;
        String str2;
        String str3;
        String str4;
        String str5;
        String str6;
        String str7;
        String str8;
        String format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(new Date(System.currentTimeMillis()));
        MatrixCursor matrixCursor = new MatrixCursor(a);
        Object[] objArr = new Object[a.length];
        objArr[matrixCursor.getColumnIndex("CoorType")] = "gcj02";
        objArr[matrixCursor.getColumnIndex("Time")] = format;
        objArr[matrixCursor.getColumnIndex("LocType")] = Integer.valueOf(bDLocation.getLocType());
        objArr[matrixCursor.getColumnIndex("Longitude")] = Double.valueOf(bDLocation.getLongitude());
        objArr[matrixCursor.getColumnIndex("Latitude")] = Double.valueOf(bDLocation.getLatitude());
        objArr[matrixCursor.getColumnIndex("Radius")] = Float.valueOf(bDLocation.getRadius());
        objArr[matrixCursor.getColumnIndex("NetworkLocationType")] = bDLocation.getNetworkLocationType();
        Address address = bDLocation.getAddress();
        if (address != null) {
            str8 = address.country;
            str7 = address.countryCode;
            str6 = address.province;
            str5 = address.city;
            str4 = address.cityCode;
            str3 = address.district;
            str2 = address.street;
            str = address.streetNumber;
        } else {
            str = null;
            str2 = null;
            str3 = null;
            str4 = null;
            str5 = null;
            str6 = null;
            str7 = null;
            str8 = null;
        }
        objArr[matrixCursor.getColumnIndex("Country")] = str8;
        objArr[matrixCursor.getColumnIndex("CountryCode")] = str7;
        objArr[matrixCursor.getColumnIndex("Province")] = str6;
        objArr[matrixCursor.getColumnIndex("City")] = str5;
        objArr[matrixCursor.getColumnIndex("CityCode")] = str4;
        objArr[matrixCursor.getColumnIndex("District")] = str3;
        objArr[matrixCursor.getColumnIndex("Street")] = str2;
        objArr[matrixCursor.getColumnIndex("StreetNumber")] = str;
        List<Poi> poiList = bDLocation.getPoiList();
        if (poiList == null) {
            objArr[matrixCursor.getColumnIndex("PoiList")] = null;
        } else {
            StringBuffer stringBuffer = new StringBuffer();
            int i = 0;
            while (true) {
                int i2 = i;
                if (i2 >= poiList.size()) {
                    break;
                }
                Poi poi = poiList.get(i2);
                stringBuffer.append(poi.getId()).append(";").append(poi.getName()).append(";").append(poi.getRank()).append(";|");
                i = i2 + 1;
            }
            objArr[matrixCursor.getColumnIndex("PoiList")] = stringBuffer.toString();
        }
        objArr[matrixCursor.getColumnIndex("LocationDescription")] = bDLocation.getLocationDescribe();
        matrixCursor.addRow(objArr);
        return matrixCursor;
    }

    static BDLocation a(Cursor cursor) {
        BDLocation bDLocation = new BDLocation();
        if (cursor == null || cursor.getCount() <= 0 || !cursor.moveToFirst()) {
            bDLocation.setLocType(67);
        } else {
            int i = 0;
            double d = 0.0d;
            double d2 = 0.0d;
            String str = null;
            String str2 = null;
            float f = 0.0f;
            String str3 = null;
            if (cursor.getColumnIndex("LocType") != -1) {
                i = cursor.getInt(cursor.getColumnIndex("LocType"));
            }
            if (cursor.getColumnIndex("Latitude") != -1) {
                d = cursor.getDouble(cursor.getColumnIndex("Latitude"));
            }
            if (cursor.getColumnIndex("Longitude") != -1) {
                d2 = cursor.getDouble(cursor.getColumnIndex("Longitude"));
            }
            if (cursor.getColumnIndex("CoorType") != -1) {
                str = cursor.getString(cursor.getColumnIndex("CoorType"));
            }
            if (cursor.getColumnIndex("NetworkLocationType") != -1) {
                str2 = cursor.getString(cursor.getColumnIndex("NetworkLocationType"));
            }
            if (cursor.getColumnIndex("Radius") != -1) {
                f = cursor.getFloat(cursor.getColumnIndex("Radius"));
            }
            if (cursor.getColumnIndex("Time") != -1) {
                str3 = cursor.getString(cursor.getColumnIndex("Time"));
            }
            String str4 = null;
            String str5 = null;
            String str6 = null;
            String str7 = null;
            String str8 = null;
            String str9 = null;
            String str10 = null;
            String str11 = null;
            if (cursor.getColumnIndex("Country") != -1) {
                str4 = cursor.getString(cursor.getColumnIndex("Country"));
            }
            if (cursor.getColumnIndex("CountryCode") != -1) {
                str5 = cursor.getString(cursor.getColumnIndex("CountryCode"));
            }
            if (cursor.getColumnIndex("Province") != -1) {
                str6 = cursor.getString(cursor.getColumnIndex("Province"));
            }
            if (cursor.getColumnIndex("City") != -1) {
                str7 = cursor.getString(cursor.getColumnIndex("City"));
            }
            if (cursor.getColumnIndex("CityCode") != -1) {
                str8 = cursor.getString(cursor.getColumnIndex("CityCode"));
            }
            if (cursor.getColumnIndex("District") != -1) {
                str9 = cursor.getString(cursor.getColumnIndex("District"));
            }
            if (cursor.getColumnIndex("Street") != -1) {
                str10 = cursor.getString(cursor.getColumnIndex("Street"));
            }
            if (cursor.getColumnIndex("StreetNumber") != -1) {
                str11 = cursor.getString(cursor.getColumnIndex("StreetNumber"));
            }
            Address build = new Address.Builder().country(str4).countryCode(str5).province(str6).city(str7).cityCode(str8).district(str9).street(str10).streetNumber(str11).build();
            ArrayList arrayList = null;
            if (cursor.getColumnIndex("PoiList") != -1) {
                arrayList = new ArrayList();
                String string = cursor.getString(cursor.getColumnIndex("PoiList"));
                if (string != null) {
                    try {
                        String[] split = string.split("\\|");
                        for (int i2 = 0; i2 < split.length; i2++) {
                            String[] split2 = split[i2].split(";");
                            if (split2.length >= 3) {
                                arrayList.add(new Poi(split2[0], split2[1], Double.valueOf(split2[2]).doubleValue()));
                            }
                        }
                    } catch (Exception e) {
                        if (arrayList.size() == 0) {
                            arrayList = null;
                        }
                    } catch (Throwable th) {
                        if (arrayList.size() == 0) {
                        }
                        throw th;
                    }
                }
                if (arrayList.size() == 0) {
                    arrayList = null;
                }
            }
            String str12 = null;
            if (cursor.getColumnIndex("LocationDescription") != -1) {
                str12 = cursor.getString(cursor.getColumnIndex("LocationDescription"));
            }
            bDLocation.setTime(str3);
            bDLocation.setRadius(f);
            bDLocation.setLocType(i);
            bDLocation.setCoorType(str);
            bDLocation.setLatitude(d);
            bDLocation.setLongitude(d2);
            bDLocation.setNetworkLocationType(str2);
            bDLocation.setAddr(build);
            bDLocation.setPoiList(arrayList);
            bDLocation.setLocationDescribe(str12);
        }
        return bDLocation;
    }

    static String a(BDLocation bDLocation, int i) {
        if (bDLocation == null || bDLocation.getLocType() == 67) {
            return String.format(Locale.CHINA, "&ofl=%s|%d", new Object[]{"1", Integer.valueOf(i)});
        }
        String format = String.format(Locale.CHINA, "&ofl=%s|%d|%f|%f|%d", new Object[]{"1", Integer.valueOf(i), Double.valueOf(bDLocation.getLongitude()), Double.valueOf(bDLocation.getLatitude()), Integer.valueOf((int) bDLocation.getRadius())});
        String str = bDLocation.getAddress() != null ? format + "&ofaddr=" + bDLocation.getAddress().address : format;
        if (bDLocation.getPoiList() != null && bDLocation.getPoiList().size() > 0) {
            Poi poi = bDLocation.getPoiList().get(0);
            str = str + String.format(Locale.US, "&ofpoi=%s|%s", new Object[]{poi.getId(), poi.getName()});
        }
        if (c.c == null) {
            return str;
        }
        return str + String.format(Locale.US, "&pack=%s&sdk=%.3f", new Object[]{c.c, Float.valueOf(6.23f)});
    }

    static String a(BDLocation bDLocation, BDLocation bDLocation2, a aVar) {
        StringBuffer stringBuffer = new StringBuffer();
        if (bDLocation2 == null) {
            stringBuffer.append("&ofcl=0");
        } else {
            stringBuffer.append(String.format(Locale.US, "&ofcl=1|%f|%f|%d", new Object[]{Double.valueOf(bDLocation2.getLongitude()), Double.valueOf(bDLocation2.getLatitude()), Integer.valueOf((int) bDLocation2.getRadius())}));
        }
        if (bDLocation == null) {
            stringBuffer.append("&ofwf=0");
        } else {
            stringBuffer.append(String.format(Locale.US, "&ofwf=1|%f|%f|%d", new Object[]{Double.valueOf(bDLocation.getLongitude()), Double.valueOf(bDLocation.getLatitude()), Integer.valueOf((int) bDLocation.getRadius())}));
        }
        if (aVar == null || !aVar.e) {
            stringBuffer.append("&rgcn=0");
        } else {
            stringBuffer.append("&rgcn=1");
        }
        if (aVar == null || !aVar.d) {
            stringBuffer.append("&poin=0");
        } else {
            stringBuffer.append("&poin=1");
        }
        if (aVar == null || !aVar.h) {
            stringBuffer.append("&desc=0");
        } else {
            stringBuffer.append("&desc=1");
        }
        if (aVar != null) {
            stringBuffer.append(String.format(Locale.US, "&aps=%d", new Object[]{Integer.valueOf(aVar.f)}));
        }
        return stringBuffer.toString();
    }

    static String[] a(com.baidu.location.f.a aVar, i iVar, BDLocation bDLocation, String str, boolean z, int i) {
        ArrayList arrayList = new ArrayList();
        StringBuffer stringBuffer = new StringBuffer();
        if (aVar != null) {
            stringBuffer.append(b.a().b(aVar));
        }
        if (iVar != null) {
            stringBuffer.append(iVar.a(30));
        }
        if (stringBuffer.length() > 0) {
            if (str != null) {
                stringBuffer.append(str);
            }
            arrayList.add("-loc");
            arrayList.add(stringBuffer.toString());
        }
        if (bDLocation != null) {
            String format = String.format(Locale.US, "%f;%f;%d;%s", new Object[]{Double.valueOf(bDLocation.getLatitude()), Double.valueOf(bDLocation.getLongitude()), Integer.valueOf(bDLocation.getLocType()), bDLocation.getNetworkLocationType()});
            arrayList.add("-com");
            arrayList.add(format);
        }
        if (z) {
            arrayList.add("-log");
            arrayList.add("true");
        }
        if (com.baidu.location.h.i.f.equals("all")) {
            arrayList.add("-rgc");
            arrayList.add("true");
        }
        if (com.baidu.location.h.i.h) {
            arrayList.add("-poi");
            arrayList.add("true");
        }
        if (com.baidu.location.h.i.g) {
            arrayList.add("-des");
            arrayList.add("true");
        }
        arrayList.add("-minap");
        arrayList.add(Integer.toString(i));
        String[] strArr = new String[arrayList.size()];
        arrayList.toArray(strArr);
        return strArr;
    }
}
