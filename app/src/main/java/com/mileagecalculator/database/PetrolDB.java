package com.mileagecalculator.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.mileagecalculator.fuelefficiency.Utils;

import java.util.ArrayList;
import java.util.HashMap;

@SuppressWarnings("CanBeFinal")
public class PetrolDB extends SQLiteOpenHelper {
    private static SQLiteDatabase sqliteDB;
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "petrolingDB";

    private static String TABLE_NAME_REGISTRATION = "registration";
    private static String TABLE_NAME_ENTRY = "entry";
    private static String TABLE_NAME_SERVICE = "service";
    private static String TABLE_NAME_REPORT = "reports";

    private static String SNO = "sno";
    private static String ID = "id";
    private static String VEHCLE_ID = "vehcle_id";
    private static String VEHCLE_MODEL = "vehcle_model";
    private static String VEHCLE_FUEL_TYPE = "vehcle_fuel_type";
    private static String VEHCLE_MILEAGE_TYPE = "vehcle_mileage_type";
    private static String NAME = "name";
    private static String VEHCLENAME = "vehcle_name";
    private static String PASSWORD = "password";
    private static String DATE = "date";
    private static String PRICE = "price";
    private static String READING = "reading";
    private static String NEW_READING = "new_reading";
    private static String CURRENT_READING = "current_reading";
    private static String NEXT_READING = "next_reading";
    private static String FUEL = "fuel";
    private static String AVERAGE = "average";
    private static String PRICE_AVERAGE = "price_average";

    /* String Value for Reports Table */
    private static String DAY_DATE = "day_date";
    private static String MONTH_DATE = "month_date";
    private static String YEAR_DATE = "year_date";
    private static String FUEL_REPORT = "fuel_report";
    private static String PRICE_REPORT = "price_report";

    /* Called Constructor */
    public PetrolDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /* onCreate */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL = "";

        SQL = SQL + "CREATE TABLE " + TABLE_NAME_REGISTRATION;
        SQL = SQL + "(";

        SQL = SQL + "	" + VEHCLE_ID + "  INTEGER PRIMARY KEY AUTOINCREMENT, ";
        SQL = SQL + "	" + ID + "  VARCHAR, ";
        SQL = SQL + "	" + VEHCLENAME + " VARCHAR, ";
        SQL = SQL + "	" + VEHCLE_MODEL + " VARCHAR, ";
        SQL = SQL + "	" + VEHCLE_FUEL_TYPE + " VARCHAR, ";
        SQL = SQL + "	" + VEHCLE_MILEAGE_TYPE + " VARCHAR ";

        SQL = SQL + ")";

        String SQL1 = "";

        SQL1 = SQL1 + "CREATE TABLE " + TABLE_NAME_ENTRY;
        SQL1 = SQL1 + "(";

        SQL1 = SQL1 + "	" + SNO + "  INTEGER PRIMARY KEY AUTOINCREMENT, ";
        SQL1 = SQL1 + "	" + VEHCLE_ID + "  VARCHAR, ";
        SQL1 = SQL1 + "	" + ID + "  VARCHAR, ";
        SQL1 = SQL1 + "	" + VEHCLENAME + " VARCHAR, ";
        SQL1 = SQL1 + "	" + VEHCLE_MODEL + " VARCHAR, ";
        SQL1 = SQL1 + "	" + VEHCLE_FUEL_TYPE + " VARCHAR, ";
        SQL1 = SQL1 + "	" + VEHCLE_MILEAGE_TYPE + " VARCHAR, ";
        SQL1 = SQL1 + "	" + DATE + " VARCHAR, ";
        SQL1 = SQL1 + "	" + NEW_READING + "  VARCHAR, ";
        SQL1 = SQL1 + "	" + FUEL + "  VARCHAR, ";
        SQL1 = SQL1 + "	" + PRICE + " VARCHAR, ";
        SQL1 = SQL1 + "	" + AVERAGE + " VARCHAR, ";
        SQL1 = SQL1 + "	" + PRICE_AVERAGE + " VARCHAR ";

        SQL1 = SQL1 + ")";

        String SQL2 = "";

        SQL2 = SQL2 + "CREATE TABLE " + TABLE_NAME_SERVICE;
        SQL2 = SQL2 + "(";

        SQL2 = SQL2 + "	" + SNO + "  INTEGER PRIMARY KEY AUTOINCREMENT, ";
        SQL2 = SQL2 + "	" + ID + "  VARCHAR, ";
        SQL2 = SQL2 + "	" + VEHCLE_ID + "  VARCHAR, ";
        SQL2 = SQL2 + "	" + VEHCLENAME + " VARCHAR, ";
        SQL2 = SQL2 + "	" + VEHCLE_MODEL + " VARCHAR, ";
        SQL2 = SQL2 + "	" + DATE + " VARCHAR, ";
        SQL2 = SQL2 + "	" + CURRENT_READING + "  VARCHAR, ";
        SQL2 = SQL2 + "	" + NEXT_READING + " VARCHAR ";

        SQL2 = SQL2 + ")";

        String SQL3 = "";

        SQL3 = SQL3 + "CREATE TABLE " + TABLE_NAME_REPORT;
        SQL3 = SQL3 + "(";

        SQL3 = SQL3 + "	" + VEHCLE_ID + "  VARCHAR, ";
        SQL3 = SQL3 + "	" + DAY_DATE + "  VARCHAR, ";
        SQL3 = SQL3 + "	" + MONTH_DATE + "  VARCHAR, ";
        SQL3 = SQL3 + "	" + YEAR_DATE + "  VARCHAR, ";
        SQL3 = SQL3 + "	" + READING + "  VARCHAR, ";
        SQL3 = SQL3 + "	" + FUEL_REPORT + " VARCHAR, ";
        SQL3 = SQL3 + "	" + PRICE_REPORT + " VARCHAR ";

        SQL3 = SQL3 + ")";

        db.execSQL(SQL);
        db.execSQL(SQL1);
        db.execSQL(SQL2);
        db.execSQL(SQL3);

    }

    /* onUpgrade */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String TABLE_NAME_HISTORY = "history";
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_ENTRY + ","
                + TABLE_NAME_HISTORY + "," + TABLE_NAME_REGISTRATION + ","
                + TABLE_NAME_SERVICE + "," + TABLE_NAME_REPORT);
        onCreate(db);
    }

    /* Open DataBase */
    @SuppressWarnings("unused")
    public void open() {
        sqliteDB = getWritableDatabase();
    }

    /* Close Database */
    @Override
    public synchronized void close() {
        super.close();
        sqliteDB.close();
    }

    /* isExist Entry */
    @SuppressLint("Recycle")
    @SuppressWarnings("unused")
    public boolean isExistVehicleID(String vehicleID) {
        sqliteDB = this.getWritableDatabase();
        String SQL = "SELECT COUNT(" + VEHCLE_ID + ") FROM " + TABLE_NAME_ENTRY
                + " WHERE " + VEHCLE_ID + " = '" + vehicleID + "'";
        Cursor cursor = sqliteDB.rawQuery(SQL, null);
        if (cursor.moveToFirst()) {
            if (cursor.getInt(0) > 0) {
                return true;
            }
        }
        return false;
    }

    /* isExist Entry */
    @SuppressLint("Recycle")
    public boolean isExistVehicleIDReg(String vehicleID) {
        sqliteDB = this.getWritableDatabase();
        String SQL = "SELECT COUNT(" + VEHCLE_ID + ") FROM " + TABLE_NAME_REGISTRATION
                + " WHERE " + VEHCLE_ID + " = '" + vehicleID + "'";
        Cursor cursor = sqliteDB.rawQuery(SQL, null);
        if (cursor.moveToFirst()) {
            if (cursor.getInt(0) > 0) {
                return true;
            }
        }
        return false;
    }

    /* isExist Entry */
    @SuppressLint("Recycle")
    public boolean isExistRegisterId(String id) {
        sqliteDB = this.getWritableDatabase();
        String SQL = "SELECT COUNT(" + ID + ") FROM " + TABLE_NAME_REGISTRATION
                + " WHERE " + ID + " = '" + id + "'";
        Cursor cursor = sqliteDB.rawQuery(SQL, null);
        if (cursor.moveToFirst()) {
            if (cursor.getInt(0) > 0) {
                return true;
            }
        }
        return false;
    }

    /* isExist History */
    @SuppressLint("Recycle")
    @SuppressWarnings("unused")
    public boolean isExistID(String string) {
        sqliteDB = this.getWritableDatabase();
        String SQL = "SELECT COUNT(" + ID + ") FROM " + TABLE_NAME_ENTRY
                + " WHERE " + ID + " = '" + string + "'";
        Cursor cursor = sqliteDB.rawQuery(SQL, null);
        if (cursor.moveToFirst()) {
            if (cursor.getInt(0) > 0) {
                return true;
            }
        }
        return false;
    }

    /* isExist Entry */
    @SuppressLint("Recycle")
    @SuppressWarnings("unused")
    public boolean isExistVehcleName(String name) {
        sqliteDB = this.getWritableDatabase();
        String SQL = "SELECT COUNT(" + VEHCLENAME + ") FROM "
                + TABLE_NAME_REGISTRATION + " WHERE " + VEHCLENAME + " = '"
                + name + "'";
        Cursor cursor = sqliteDB.rawQuery(SQL, null);
        if (cursor.moveToFirst()) {
            if (cursor.getInt(0) > 0) {
                return true;
            }
        }
        return false;
    }

    /* isExist Entry */
    @SuppressLint("Recycle")
    @SuppressWarnings("unused")
    public boolean isExistVehcleNamePass(String name, String pass) {
        sqliteDB = this.getWritableDatabase();
        String SQL = "SELECT * FROM " + TABLE_NAME_REGISTRATION + " WHERE "
                + VEHCLENAME + " = '" + name + "' AND " + PASSWORD + " = '"
                + pass + "'";
        Log.e("SQL", "isExistVehcleNamePass>>" + SQL);
        Cursor cursor = sqliteDB.rawQuery(SQL, null);
        if (cursor.moveToFirst()) {
            if (cursor.getInt(0) > 0) {
                return true;
            }
        }
        return false;
    }

    /* isExist Entry */
    @SuppressLint("Recycle")
    @SuppressWarnings("unused")
    public boolean isExistVN(String v_id) {
        sqliteDB = this.getWritableDatabase();
        String SQL = "SELECT * FROM " + TABLE_NAME_SERVICE + " WHERE "
                + VEHCLE_ID + " = '" + v_id + "'";
        Log.e("SQL", "isExistVN>>" + SQL);
        Cursor cursor = sqliteDB.rawQuery(SQL, null);
        if (cursor.moveToFirst()) {
            if (cursor.getInt(0) > 0) {
                return true;
            }
        }
        return false;
    }

    /* isExist Entry */
    @SuppressLint("Recycle")
    @SuppressWarnings("unused")
    public boolean isExistReport(String id) {
        sqliteDB = this.getWritableDatabase();
        String SQL = "SELECT * FROM " + TABLE_NAME_REPORT + " WHERE "
                + VEHCLE_ID + " = '" + id + "'";
        Cursor cursor = sqliteDB.rawQuery(SQL, null);
        if (cursor.moveToFirst()) {
            if (cursor.getInt(0) > 0) {
                return true;
            }
        }
        return false;
    }

    /* isExist This Month Report */
    @SuppressLint("Recycle")
    @SuppressWarnings("unused")
    public boolean isExistThisMonthReport(String month) {
        sqliteDB = this.getWritableDatabase();
        String SQL = "SELECT * FROM " + TABLE_NAME_REPORT + " WHERE "
                + MONTH_DATE + " = '" + month + "'";
        Cursor cursor = sqliteDB.rawQuery(SQL, null);
        if (cursor.moveToFirst()) {
            if (cursor.getInt(0) > 0) {
                return true;
            }
        }
        return false;
    }

    /* isExist Last Month Report */
    @SuppressLint("Recycle")
    @SuppressWarnings("unused")
    public boolean isExistLastMonthReport(String lastmonth) {
        sqliteDB = this.getWritableDatabase();
        String SQL = "SELECT * FROM " + TABLE_NAME_REPORT + " WHERE "
                + MONTH_DATE + " = '" + lastmonth + "'";
        Cursor cursor = sqliteDB.rawQuery(SQL, null);
        if (cursor.moveToFirst()) {
            if (cursor.getInt(0) > 0) {
                return true;
            }
        }
        return false;
    }

    /* Match Last ID */
    @SuppressLint("Recycle")
    public String getLastValue(String id) {
        String oldReading = "";
        sqliteDB = this.getWritableDatabase();
        Cursor cursor;
        String SQL = "SELECT * FROM " + TABLE_NAME_ENTRY + " WHERE "
                + VEHCLE_ID + " = '" + id + "'";
        Log.e("SQL", "getLastValue>>>" + SQL);
        cursor = sqliteDB.rawQuery(SQL, null);
        if (cursor.moveToLast()) {
            if (cursor.getInt(0) > 0) {
                oldReading = cursor.getString(cursor.getColumnIndex(NEW_READING));
                return oldReading;
            }
        }
        return oldReading;
    }

    /* Match Last Fuel */
    @SuppressLint("Recycle")
    public String getLastFuel(String id) {
        String lastFuel = "";
        sqliteDB = this.getWritableDatabase();
        Cursor cursor;
        String SQL = "SELECT * FROM " + TABLE_NAME_ENTRY + " WHERE "
                + VEHCLE_ID + " = '" + id + "'";
        Log.e("SQL", "getLastFuel>>>" + SQL);
        cursor = sqliteDB.rawQuery(SQL, null);
        if (cursor.moveToLast()) {
            if (cursor.getInt(0) > 0) {
                lastFuel = cursor.getString(cursor.getColumnIndex(FUEL));
                return lastFuel;
            }
        }
        return lastFuel;
    }

    /* Match Last Fuel */
    @SuppressLint("Recycle")
    public String getLastPrice(String id) {
        String lastPrice = "";
        sqliteDB = this.getWritableDatabase();
        Cursor cursor;
        String SQL = "SELECT * FROM " + TABLE_NAME_ENTRY + " WHERE "
                + VEHCLE_ID + " = '" + id + "'";
        Log.e("SQL", "getLastPrice>>" + SQL);
        cursor = sqliteDB.rawQuery(SQL, null);
        if (cursor.moveToLast()) {
            if (cursor.getInt(0) > 0) {
                lastPrice = cursor.getString(cursor.getColumnIndex(PRICE));
                Log.e("lastPrice=>", "" + lastPrice);
                return lastPrice;
            }
        }
        return lastPrice;
    }

    /* Match Last Fuel */
    @SuppressLint("Recycle")
    public String getServiceReading(String id) {
        String reading = "";
        sqliteDB = this.getWritableDatabase();
        Cursor cursor;
        String SQL = "SELECT * FROM " + TABLE_NAME_SERVICE + " WHERE "
                + VEHCLE_ID + " = '" + id + "'";
        Log.e("SQL", "getServiceReading>>" + SQL);
        cursor = sqliteDB.rawQuery(SQL, null);
        if (cursor.moveToFirst()) {
            if (cursor.getInt(0) > 0) {
                reading = cursor.getString(cursor.getColumnIndex(NEXT_READING));
                return reading;
            }
        }
        return reading;
    }

    /* Match Last ID */
    @SuppressLint("Recycle")
    public String getLastDate(String id) {
        String oldDate = "";
        sqliteDB = this.getWritableDatabase();
        Cursor cursor;
        String SQL = "SELECT * FROM " + TABLE_NAME_ENTRY + " WHERE " + VEHCLE_ID
                + " = '" + id + "'";
        Log.e("SQL", "getLastDate>>" + SQL);
        cursor = sqliteDB.rawQuery(SQL, null);
        if (cursor.moveToLast()) {
            if (cursor.getInt(0) > 0) {
                oldDate = cursor.getString(cursor.getColumnIndex(DATE));
            }
        }
        return oldDate;
    }

    /* Insert Registration */
    public void insertRegistrationRecord(Utils u) {
        SQLiteStatement insertStmt;
        SQLiteDatabase sqlite_dbase = getWritableDatabase();
        String SQL = "";

        SQL = SQL + "INSERT INTO " + TABLE_NAME_REGISTRATION;
        SQL = SQL + "(";

        SQL = SQL + " 	" + VEHCLE_ID + ", ";
        SQL = SQL + " 	" + ID + ", ";
        SQL = SQL + " 	" + VEHCLENAME + ", ";
        SQL = SQL + " 	" + VEHCLE_MODEL + ", ";
        SQL = SQL + " 	" + VEHCLE_FUEL_TYPE + ", ";
        SQL = SQL + " 	" + VEHCLE_MILEAGE_TYPE;

        SQL = SQL + ") VALUES (?, ?, ?, ?, ?, ?);";
        Log.e("Regitaration==>>", " " + SQL);
        sqlite_dbase.beginTransaction();

        insertStmt = sqlite_dbase.compileStatement(SQL);
        insertStmt.bindString(2, u.getId());
        insertStmt.bindString(3, u.getVehcleName());
        insertStmt.bindString(4, u.getVehcleModel());
        insertStmt.bindString(5, u.getFuelType());
        insertStmt.bindString(6, u.getMileage());
        insertStmt.execute();
        insertStmt.clearBindings();
        sqlite_dbase.setTransactionSuccessful();
        sqlite_dbase.endTransaction();
    }

    /* Insert Entry */
    @SuppressWarnings("unused")
    public void insertRecord(Utils u) {
        SQLiteStatement insertStmt;
        SQLiteDatabase sqlite_dbase = getWritableDatabase();
        String SQL = "";

        SQL = SQL + "INSERT INTO " + TABLE_NAME_ENTRY;
        SQL = SQL + "(";

        SQL = SQL + " 	" + SNO + ", ";
        SQL = SQL + " 	" + VEHCLE_ID + ", ";
        SQL = SQL + " 	" + ID + ", ";
        SQL = SQL + " 	" + VEHCLENAME + ", ";
        SQL = SQL + " 	" + VEHCLE_MODEL + ", ";
        SQL = SQL + " 	" + VEHCLE_FUEL_TYPE + ", ";
        SQL = SQL + " 	" + VEHCLE_MILEAGE_TYPE + ", ";
        SQL = SQL + " 	" + DATE + ", ";
        SQL = SQL + " 	" + NEW_READING + ", ";
        SQL = SQL + " 	" + FUEL + ", ";
        SQL = SQL + " 	" + PRICE + ", ";
        SQL = SQL + " 	" + AVERAGE + ", ";
        SQL = SQL + " 	" + PRICE_AVERAGE;

        SQL = SQL + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
        Log.e("InsertEntry==>>", "" + SQL);
        sqlite_dbase.beginTransaction();

        insertStmt = sqlite_dbase.compileStatement(SQL);
        insertStmt.bindString(2, u.getVehcleId());
        insertStmt.bindString(3, u.getId());
        insertStmt.bindString(4, u.getVehcleName());
        insertStmt.bindString(5, u.getVehcleModel());
        insertStmt.bindString(6, u.getFuelType());
        insertStmt.bindString(7, u.getMileage());
        insertStmt.bindString(8, u.getDate());
        insertStmt.bindString(9, u.getNew_reading());
        insertStmt.bindString(10, u.getFuel());
        insertStmt.bindString(11, u.getPrice());
        insertStmt.bindString(12, u.getAverage());
        insertStmt.bindString(13, u.getPriceAvg());
        insertStmt.execute();
        insertStmt.clearBindings();
        sqlite_dbase.setTransactionSuccessful();
        sqlite_dbase.endTransaction();

    }

    /* Insert Entry */
    @SuppressWarnings("unused")
    public void insertServiceRecord(Utils u) {
        SQLiteStatement insertStmt;
        SQLiteDatabase sqlite_dbase = getWritableDatabase();
        String SQL = "";

        SQL = SQL + "INSERT INTO " + TABLE_NAME_SERVICE;
        SQL = SQL + "(";

        SQL = SQL + " 	" + SNO + ", ";
        SQL = SQL + " 	" + ID + ", ";
        SQL = SQL + " 	" + VEHCLE_ID + ", ";
        SQL = SQL + " 	" + VEHCLENAME + ", ";
        SQL = SQL + " 	" + VEHCLE_MODEL + ", ";
        SQL = SQL + " 	" + DATE + ", ";
        SQL = SQL + " 	" + CURRENT_READING + ", ";
        SQL = SQL + " 	" + NEXT_READING;

        SQL = SQL + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
        Log.e("InsertServiceEntry==>>", "" + SQL);
        sqlite_dbase.beginTransaction();

        insertStmt = sqlite_dbase.compileStatement(SQL);
        insertStmt.bindString(2, u.getId());
        insertStmt.bindString(3, u.getVehcleId());
        insertStmt.bindString(4, u.getVehcleName());
        insertStmt.bindString(5, u.getVehcleModel());
        insertStmt.bindString(6, u.getDate());
        insertStmt.bindString(7, u.getCurrent_Reading());
        insertStmt.bindString(8, u.getNext_Reading());
        insertStmt.execute();
        insertStmt.clearBindings();
        sqlite_dbase.setTransactionSuccessful();
        sqlite_dbase.endTransaction();

    }

    /* Insert Entry */
    public void insertReports(Utils u) {
        SQLiteStatement insertStmt;
        SQLiteDatabase sqlite_dbase = getWritableDatabase();
        String SQL = "";

        SQL = SQL + "INSERT INTO " + TABLE_NAME_REPORT;
        SQL = SQL + "(";

        SQL = SQL + " 	" + VEHCLE_ID + ", ";
        SQL = SQL + " 	" + DAY_DATE + ", ";
        SQL = SQL + " 	" + MONTH_DATE + ", ";
        SQL = SQL + " 	" + YEAR_DATE + ", ";
        SQL = SQL + " 	" + READING + ", ";
        SQL = SQL + " 	" + FUEL_REPORT + ", ";
        SQL = SQL + " 	" + PRICE_REPORT;

        SQL = SQL + ") VALUES (?, ?, ?, ?, ?, ?, ?);";
        Log.e("InsertReportEntry==>>", "" + SQL);
        sqlite_dbase.beginTransaction();

        insertStmt = sqlite_dbase.compileStatement(SQL);
        insertStmt.bindString(1, u.getVehcleId());
        insertStmt.bindString(2, u.getDay_Date());
        insertStmt.bindString(3, u.getMonth_Date());
        insertStmt.bindString(4, u.getYear_Date());
        insertStmt.bindString(5, u.getNew_reading());
        insertStmt.bindString(6, u.getFuel());
        insertStmt.bindString(7, u.getPrice());
        insertStmt.execute();
        insertStmt.clearBindings();
        sqlite_dbase.setTransactionSuccessful();
        sqlite_dbase.endTransaction();

    }

    /* get Vehicle Record */
    public ArrayList<HashMap<String, String>> getEntry(String v_id) {
        ArrayList<HashMap<String, String>> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;


        String SQL = "SELECT * FROM " + TABLE_NAME_ENTRY + " WHERE "
                + VEHCLE_ID + " = '" + v_id + "' AND SNO != 1" + " ORDER BY " + SNO + "  ";
        Log.e("SQL==>>", "" + SQL);
        cursor = db.rawQuery(SQL, new String[]{});
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();
                map.put("vehcle_id", cursor.getString(cursor.getColumnIndex(VEHCLE_ID)));
                map.put("id", cursor.getString(cursor.getColumnIndex(ID)));
                map.put("vehcle_name", cursor.getString(cursor.getColumnIndex(VEHCLENAME)));
                map.put("vehcle_model", cursor.getString(cursor.getColumnIndex(VEHCLE_MODEL)));
                map.put("vehcle_fuel_type", cursor.getString(cursor.getColumnIndex(VEHCLE_FUEL_TYPE)));
                map.put("vehcle_mileage_type", cursor.getString(cursor.getColumnIndex(VEHCLE_MILEAGE_TYPE)));
                map.put("date", cursor.getString(cursor.getColumnIndex(DATE)));
                map.put("new_reading", cursor.getString(cursor.getColumnIndex(NEW_READING)));
                map.put("fuel", cursor.getString(cursor.getColumnIndex(FUEL)));
                map.put("price", cursor.getString(cursor.getColumnIndex(PRICE)));
                map.put("average", cursor.getString(cursor.getColumnIndex(AVERAGE)));
                map.put("price_average", cursor.getString(cursor.getColumnIndex(PRICE_AVERAGE)));
                list.add(map);
            } while (cursor.moveToNext());
        }

        if (!cursor.isClosed()) {
            cursor.close();
        }

        db.close();
        if (cursor.getCount() == 0) {
            return null;

        } else {
            return list;
        }
    }

    /* Match Last Five ID */
    @SuppressLint("Recycle")
    public ArrayList<HashMap<String, String>> getLastFiveEntry(String v_id) {
        ArrayList<HashMap<String, String>> list = new ArrayList<>();
        sqliteDB = this.getReadableDatabase();
        Cursor cursor;
        String SQL = "SELECT * FROM " + TABLE_NAME_ENTRY + " WHERE "
                + VEHCLE_ID + " = '" + v_id + "' AND SNO != 1" + " ORDER BY " + SNO + "  ";
        Log.e("SQL", "getLastFiveEntry>>>" + SQL);
        cursor = sqliteDB.rawQuery(SQL, null);
        if (cursor.moveToLast()) {
            int curSize = cursor.getCount(); // return no of rows
            if (curSize > 5) {
                int lastTenValue = curSize - 5;
                for (int i = curSize; i > lastTenValue; i--) {

                    HashMap<String, String> map = new HashMap<>();
                    map.put("vehcle_id", cursor.getString(cursor.getColumnIndex(VEHCLE_ID)));
                    map.put("id", cursor.getString(cursor.getColumnIndex(ID)));
                    map.put("vehcle_name", cursor.getString(cursor.getColumnIndex(VEHCLENAME)));
                    map.put("vehcle_model", cursor.getString(cursor.getColumnIndex(VEHCLE_MODEL)));
                    map.put("vehcle_fuel_type", cursor.getString(cursor.getColumnIndex(VEHCLE_FUEL_TYPE)));
                    map.put("vehcle_mileage_type", cursor.getString(cursor.getColumnIndex(VEHCLE_MILEAGE_TYPE)));
                    map.put("date", cursor.getString(cursor.getColumnIndex(DATE)));
                    map.put("new_reading", cursor.getString(cursor.getColumnIndex(NEW_READING)));
                    map.put("fuel", cursor.getString(cursor.getColumnIndex(FUEL)));
                    map.put("price", cursor.getString(cursor.getColumnIndex(PRICE)));
                    map.put("average", cursor.getString(cursor.getColumnIndex(AVERAGE)));
                    map.put("price_average", cursor.getString(cursor.getColumnIndex(PRICE_AVERAGE)));
                    list.add(map);
                    cursor.moveToPrevious();
                }
            } else {

                for (int i = 0; i < curSize; i++) {

                    HashMap<String, String> map = new HashMap<>();
                    map.put("vehcle_id", cursor.getString(cursor.getColumnIndex(VEHCLE_ID)));
                    map.put("id", cursor.getString(cursor.getColumnIndex(ID)));
                    map.put("vehcle_name", cursor.getString(cursor.getColumnIndex(VEHCLENAME)));
                    map.put("vehcle_model", cursor.getString(cursor.getColumnIndex(VEHCLE_MODEL)));
                    map.put("vehcle_fuel_type", cursor.getString(cursor.getColumnIndex(VEHCLE_FUEL_TYPE)));
                    map.put("vehcle_mileage_type", cursor.getString(cursor.getColumnIndex(VEHCLE_MILEAGE_TYPE)));
                    map.put("date", cursor.getString(cursor.getColumnIndex(DATE)));
                    map.put("new_reading", cursor.getString(cursor.getColumnIndex(NEW_READING)));
                    map.put("fuel", cursor.getString(cursor.getColumnIndex(FUEL)));
                    map.put("price", cursor.getString(cursor.getColumnIndex(PRICE)));
                    map.put("average", cursor.getString(cursor.getColumnIndex(AVERAGE)));
                    map.put("price_average", cursor.getString(cursor.getColumnIndex(PRICE_AVERAGE)));
                    list.add(map);
                    cursor.moveToPrevious();
                }
            }
        }
        return list;
    }

    /* getLogInDetail */
    @SuppressWarnings("unused")
    public ArrayList<HashMap<String, String>> getLogInDetail(String vehcleName,
                                                             String pass) {

        ArrayList<HashMap<String, String>> list = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;

        cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME_REGISTRATION
                        + " WHERE " + VEHCLENAME + "=?" + " AND " + PASSWORD + "=?",
                new String[]{vehcleName + "", pass + ""});
        if (cursor.moveToFirst()) {

            do {
                HashMap<String, String> map = new HashMap<>();
                map.put("id", cursor.getString(cursor.getColumnIndex(ID)));
                map.put("name", cursor.getString(cursor.getColumnIndex(NAME)));
                map.put("vehcle_name",
                        cursor.getString(cursor.getColumnIndex(VEHCLENAME)));
                map.put("password",
                        cursor.getString(cursor.getColumnIndex(PASSWORD)));
                list.add(map);
            } while (cursor.moveToNext());
        }

        if (!cursor.isClosed()) {
            cursor.close();
        }

        db.close();
        if (cursor.getCount() == 0) {
            return null;

        } else {
            return list;
        }
    }

    /* get Registration Record */
    public ArrayList<HashMap<String, String>> getRegistrationRecord(String id) {

        ArrayList<HashMap<String, String>> list = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;

        cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME_REGISTRATION
                + " WHERE " + ID + "=?", new String[]{id + ""});
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();
                map.put("vehcle_id", cursor.getString(cursor.getColumnIndex(VEHCLE_ID)));
                map.put("id", cursor.getString(cursor.getColumnIndex(ID)));
                map.put("vehcle_name", cursor.getString(cursor.getColumnIndex(VEHCLENAME)));
                map.put("vehcle_model", cursor.getString(cursor.getColumnIndex(VEHCLE_MODEL)));
                map.put("vehcle_fuel_type", cursor.getString(cursor.getColumnIndex(VEHCLE_FUEL_TYPE)));
                map.put("vehcle_mileage_type", cursor.getString(cursor.getColumnIndex(VEHCLE_MILEAGE_TYPE)));
                list.add(map);
            } while (cursor.moveToNext());
        }

        if (!cursor.isClosed()) {
            cursor.close();
        }

        db.close();
        if (cursor.getCount() == 0) {
            return null;

        } else {
            return list;
        }
    }

    /* getLogInDetail */
    @SuppressWarnings("unused")
    public ArrayList<HashMap<String, String>> getForgotPassDetail(
            String vehcleName) {

        ArrayList<HashMap<String, String>> list = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;

        cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME_REGISTRATION
                        + " WHERE " + VEHCLENAME + "=?",
                new String[]{vehcleName + ""});
        if (cursor.moveToFirst()) {

            do {
                HashMap<String, String> map = new HashMap<>();
                map.put("id", cursor.getString(cursor.getColumnIndex(ID)));
                map.put("name", cursor.getString(cursor.getColumnIndex(NAME)));
                map.put("vehcle_name", cursor.getString(cursor.getColumnIndex(VEHCLENAME)));
                map.put("password", cursor.getString(cursor.getColumnIndex(PASSWORD)));
                list.add(map);
            } while (cursor.moveToNext());
        }

        if (!cursor.isClosed()) {
            cursor.close();
        }

        db.close();
        if (cursor.getCount() == 0) {
            return null;

        } else {
            return list;
        }
    }

    /* getThisMonthRecords */
    @SuppressWarnings("unused")
    public ArrayList<HashMap<String, String>> getThisMonthRecord(String month, String vehicleID) {

        ArrayList<HashMap<String, String>> list = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;

        cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME_REPORT
                        + " WHERE " + MONTH_DATE + "=?" + " AND " + VEHCLE_ID + "=?",
                new String[]{month + "", vehicleID + ""});
        if (cursor.moveToFirst()) {

            do {
                HashMap<String, String> map = new HashMap<>();
                map.put("vehcle_id", cursor.getString(cursor.getColumnIndex(VEHCLE_ID)));
                map.put("day_date", cursor.getString(cursor.getColumnIndex(DAY_DATE)));
                map.put("month_date", cursor.getString(cursor.getColumnIndex(MONTH_DATE)));
                map.put("year_date", cursor.getString(cursor.getColumnIndex(YEAR_DATE)));
                map.put("reading", cursor.getString(cursor.getColumnIndex(READING)));
                map.put("fuel_report", cursor.getString(cursor.getColumnIndex(FUEL_REPORT)));
                map.put("price_report", cursor.getString(cursor.getColumnIndex(PRICE_REPORT)));
                list.add(map);
            } while (cursor.moveToNext());
        }

        if (!cursor.isClosed()) {
            cursor.close();
        }

        db.close();
        if (cursor.getCount() == 0) {
            return null;

        } else {
            return list;
        }
    }

    /* getLastMonthRecords */
    @SuppressWarnings("unused")
    public ArrayList<HashMap<String, String>> getLastMonthRecord(String lastmonth, String vehicleID) {

        ArrayList<HashMap<String, String>> list = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;

        cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME_REPORT
                        + " WHERE " + MONTH_DATE + "=?" + " AND " + VEHCLE_ID + "=?",
                new String[]{lastmonth + "", vehicleID + ""});
        if (cursor.moveToFirst()) {

            do {
                HashMap<String, String> map = new HashMap<>();
                map.put("vehcle_id", cursor.getString(cursor.getColumnIndex(VEHCLE_ID)));
                map.put("day_date", cursor.getString(cursor.getColumnIndex(DAY_DATE)));
                map.put("month_date", cursor.getString(cursor.getColumnIndex(MONTH_DATE)));
                map.put("year_date", cursor.getString(cursor.getColumnIndex(YEAR_DATE)));
                map.put("reading", cursor.getString(cursor.getColumnIndex(READING)));
                map.put("fuel_report", cursor.getString(cursor.getColumnIndex(FUEL_REPORT)));
                map.put("price_report", cursor.getString(cursor.getColumnIndex(PRICE_REPORT)));
                list.add(map);
            } while (cursor.moveToNext());
        }

        if (!cursor.isClosed()) {
            cursor.close();
        }

        db.close();
        if (cursor.getCount() == 0) {
            return null;

        } else {
            return list;
        }
    }

    /* getTotalMonthsRecords */
    @SuppressWarnings("unused")
    public ArrayList<HashMap<String, String>> getTotalRecord(String vehicleID) {

        ArrayList<HashMap<String, String>> list = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;

        cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME_REPORT
                        + " WHERE " + VEHCLE_ID + "=?",
                new String[]{vehicleID + ""});
        if (cursor.moveToFirst()) {

            do {
                HashMap<String, String> map = new HashMap<>();
                map.put("vehcle_id", cursor.getString(cursor.getColumnIndex(VEHCLE_ID)));
                map.put("day_date", cursor.getString(cursor.getColumnIndex(DAY_DATE)));
                map.put("month_date", cursor.getString(cursor.getColumnIndex(MONTH_DATE)));
                map.put("year_date", cursor.getString(cursor.getColumnIndex(YEAR_DATE)));
                map.put("reading", cursor.getString(cursor.getColumnIndex(READING)));
                map.put("fuel_report", cursor.getString(cursor.getColumnIndex(FUEL_REPORT)));
                map.put("price_report", cursor.getString(cursor.getColumnIndex(PRICE_REPORT)));
                list.add(map);
            } while (cursor.moveToNext());
        }

        if (!cursor.isClosed()) {
            cursor.close();
        }

        db.close();
        if (cursor.getCount() == 0) {
            return null;

        } else {
            return list;
        }
    }

    @SuppressWarnings("unused")
    public void updateRecord(Utils u) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DATE, u.getDate());
        contentValues.put(CURRENT_READING, u.getCurrent_Reading());
        contentValues.put(NEXT_READING, u.getNext_Reading());
        db.update(TABLE_NAME_SERVICE, contentValues, VEHCLE_ID + "=" + u.getVehcleId(), null);
        db.close();
    }

    /* Delete Row Registration */
    public void deleteRegistration(String vehicleID) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME_REGISTRATION, VEHCLE_ID + "='" + vehicleID + "'", null);
        db.close();
    }

    /* Delete Row Services */
    public void deleteService(String vehicleID) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME_SERVICE, VEHCLE_ID + "='" + vehicleID + "'", null);
        db.close();
    }

    /* Delete Row Entry */
    public void deleteEntry(String vehicleID) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME_ENTRY, VEHCLE_ID + "='" + vehicleID + "'", null);
        db.close();
    }

    /* Delete Row Report*/
    public void deleteReport(String vehicleID) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME_REPORT, VEHCLE_ID + "='" + vehicleID + "'", null);
        db.close();
    }
}