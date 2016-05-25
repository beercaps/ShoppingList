package com.kevin.shoppinglist;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by kevinwetzel on 20.05.16.
 */
public class ProductDbHelper extends SQLiteOpenHelper {

    public static final  String LOG_TAG = ProductDbHelper.class.getSimpleName();

    public  static  final String DB_NAME = "products.db";
    public  static  final int    DB_VERSION = 1;

    public  static  final String T_product = "t_product";
    public  static  final String C_ID = "_id";
    public  static  final String C_NAME = "c_name";
    public  static  final String C_QUANTITY = "c_quantity";


    public static final String SQL_CREATE =
            "CREATE TABLE " + T_product +
                    "(" + C_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    C_NAME + " TEXT NOT NULL, " +
                    C_QUANTITY + " INTEGER NOT NULL);";


    public ProductDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        Log.d(LOG_TAG, "DbHelper hat die Datenbank "+ getDatabaseName() + " erzeugt");
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d(LOG_TAG, "Die Tabelle wird mit SQL-Befehl: " + SQL_CREATE + " angelegt.");
        try {
            sqLiteDatabase.execSQL(SQL_CREATE);
        } catch (SQLException e) {
            Log.e(LOG_TAG, "Fehler beim Anlegen der Tabelle: " + e.getMessage());
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
