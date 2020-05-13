package com.szchoiceway.index;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CityRecManager {
    private static final String TAG = "CityRecManager";
    private SQLiteDatabase db = this.helper.getWritableDatabase();
    private DBHelper helper;

    public CityRecManager(Context context) {
        this.helper = new DBHelper(context);
    }

    public void closeDB() {
        this.db.close();
    }

    public void deleteProvince() {
    }

    public void addProvinceRec(String quName, String cityName, String pyName) {
        if (this.db != null) {
            try {
                this.db.execSQL("INSERT INTO provincelist VALUES(null, ?, ?, ?)", new Object[]{quName, cityName, pyName});
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }
    }

    public Cursor queryCityRec(String quName) {
        if (this.db == null || quName == null) {
            return null;
        }
        try {
            return this.db.query("citylist", (String[]) null, " quname = ? ", new String[]{quName}, (String) null, (String) null, (String) null, (String) null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Cursor queryProvinceRec() {
        try {
            return this.db.query("provincelist", (String[]) null, (String) null, (String[]) null, (String) null, (String) null, (String) null, (String) null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void addCityRec(String cityName, String centerName, String pyName, String cityCode, String quName, String quPyName) {
        if (this.db != null) {
            try {
                this.db.execSQL("INSERT INTO citylist VALUES(null, ?, ?, ?, ?, ?, ?)", new Object[]{cityName, centerName, pyName, cityCode, quName, quPyName});
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }
    }

    class DBHelper extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "china.db";
        private static final int DATABASE_VERSION = 1;
        private static final String DB_PATH = "/data/data/com.szchoiceway.index/databases/";
        private final Context myContext;

        public DBHelper(Context context) {
            super(context, "/data/data/com.szchoiceway.index/databases/china.db", (SQLiteDatabase.CursorFactory) null, 1);
            this.myContext = context;
            try {
                createDataBase();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void createDataBase() throws IOException {
            if (!checkDataBase()) {
                try {
                    File dir = new File(DB_PATH);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    File dbf = new File("/data/data/com.szchoiceway.index/databases/china.db");
                    if (dbf.exists()) {
                        dbf.delete();
                    }
                    SQLiteDatabase.openOrCreateDatabase(dbf, (SQLiteDatabase.CursorFactory) null);
                    copyDataBase();
                } catch (IOException e) {
                    throw new Error("数据库创建失败");
                }
            }
        }

        private void copyDataBase() throws IOException {
            if (this.myContext != null) {
                InputStream myInput = this.myContext.getAssets().open(DATABASE_NAME);
                OutputStream myOutput = new FileOutputStream("/data/data/com.szchoiceway.index/databases/china.db");
                byte[] buffer = new byte[4096];
                while (true) {
                    int length = myInput.read(buffer);
                    if (length > 0) {
                        myOutput.write(buffer, 0, length);
                    } else {
                        myOutput.flush();
                        myOutput.close();
                        myInput.close();
                        return;
                    }
                }
            }
        }

        private boolean checkDataBase() {
            boolean z;
            SQLiteDatabase checkDB = null;
            try {
                checkDB = SQLiteDatabase.openDatabase("/data/data/com.szchoiceway.index/databases/china.db", (SQLiteDatabase.CursorFactory) null, 1);
                Cursor c = null;
                try {
                    c = checkDB.query("provincelist", (String[]) null, (String) null, (String[]) null, (String) null, (String) null, (String) null, (String) null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (c != null) {
                    if (c.getCount() != 0) {
                        if (c != null) {
                            c.close();
                        }
                        try {
                            c = checkDB.query("citylist", (String[]) null, (String) null, (String[]) null, (String) null, (String) null, (String) null, (String) null);
                        } catch (Exception e2) {
                            e2.printStackTrace();
                        }
                        if (c != null) {
                            if (c.getCount() != 0) {
                                if (c != null) {
                                    c.close();
                                }
                                if (checkDB != null) {
                                    checkDB.close();
                                }
                                if (checkDB != null) {
                                    z = true;
                                } else {
                                    z = false;
                                }
                                return z;
                            }
                        }
                        if (checkDB != null) {
                            checkDB.close();
                        }
                        if (c == null) {
                            return false;
                        }
                        c.close();
                        return false;
                    }
                }
                if (checkDB != null) {
                    checkDB.close();
                }
                if (c == null) {
                    return false;
                }
                c.close();
                return false;
            } catch (SQLiteException e3) {
                Log.i(CityRecManager.TAG, "checkDataBase not exist");
            }
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS provincelist(_id INTEGER PRIMARY KEY AUTOINCREMENT, quname VARCHAR, cityname VARCHAR, pyname VARCHAR)");
            db.execSQL("CREATE TABLE IF NOT EXISTS citylist(_id INTEGER PRIMARY KEY AUTOINCREMENT, cityname VARCHAR, centername VARCHAR, pyname VARCHAR, citycode VARCHAR, quname VARCHAR, qu_pyname VARCHAR)");
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }
}
