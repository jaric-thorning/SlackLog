package redjthorn.slacklog;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static android.provider.BaseColumns._ID;
import static redjthorn.slacklog.Constants.*;

/**
 * Created by jaricthorning on 29/1/18.
 */
public class DBManager extends SQLiteOpenHelper {

    private static final String TAG = "SlackLog:DBManager";

    public static DBManager DBManager;

    private static final int DATABASE_VERSION = 1;


    /** Create a helper object for the Workspaces database */
    public DBManager(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        if(this.DBManager == null) {
            this.DBManager = this;
        }

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
            checktables(db);
    }

    public void checktables(SQLiteDatabase Adb){
        SQLiteDatabase db = Adb;

        if(db == null){
            db = this.getWritableDatabase();
        }

        /* Create Workspaces Table */

        Log.d(TAG, "Checking " + WORKSPACES_TABLE_NAME);

        try{
            db.execSQL("CREATE TABLE IF NOT EXISTS " + WORKSPACES_TABLE_NAME + " ("
                    + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + WORKSPACES_NAME + " TEXT NOT NULL, "
                    + WORKSPACES_KEY + " TEXT NOT NULL);");
            Log.d(TAG, "Created " + WORKSPACES_TABLE_NAME);
        } catch (Exception e){
            Log.d(TAG, e.toString());
        }


        /* Create Users Table */

        try {
            Log.d(TAG, "Checking " + USERS_TABLE_NAME);

            db.execSQL("CREATE TABLE IF NOT EXISTS " + USERS_TABLE_NAME + " ("
                    + USERS_UID + " TEXT NOT NULL PRIMARY KEY, "
                    + USERS_NAME + " TEXT NOT NULL, "
                    + USERS_REAL_NAME + " TEXT, "
                    + USERS_EMAIL + " TEXT NOT NULL, "
                    + USERS_WORKSPACE + " TEXT NOT NULL);");
            Log.d(TAG, "Created " + WORKSPACES_TABLE_NAME);
        } catch (Exception e){
            Log.d(TAG, e.toString());
        }


        try{
            Log.d(TAG, "Checking " + LOG_TABLE_NAME);

            db.execSQL("CREATE TABLE IF NOT EXISTS " + LOG_TABLE_NAME + " ("
                    + LOG_ID + " INTEGER NOT NULL PRIMARY KEY, "
                    + LOG_UID + " TEXT NOT NULL, "
                    + LOG_USERNAME + " TEXT NOT NULL, "
                    + LOG_DATE_FIRST + " TEXT NOT NULL, "
                    + LOG_DATE_LAST + " TEXT NOT NULL, "
                    + LOG_COUNT + " TEXT, "
                    + LOG_IP + " TEXT, "
                    + LOG_UAGENT + " TEXT, "
                    + LOG_COUNTRY + " TEXT, "
                    + LOG_REGION + " TEXT);");

        } catch (SQLException e){
            Log.d(TAG, e.toString());
        }



    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /*Drop Workspaces Table */
        db.execSQL("DROP TABLE IF EXISTS " + WORKSPACES_TABLE_NAME);

        /*Drop User Table*/
        db.execSQL("DROP TABLE IF EXISTS " + USERS_TABLE_NAME);

         /*Drop User Table*/
        db.execSQL("DROP TABLE IF EXISTS " + LOG_TABLE_NAME);

        onCreate(db);
    }


}
