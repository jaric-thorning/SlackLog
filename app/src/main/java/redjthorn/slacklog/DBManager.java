package redjthorn.slacklog;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static android.provider.BaseColumns._ID;
import static redjthorn.slacklog.Constants.*;

/**
 * Created by jaricthorning on 29/1/18.
 */
public class DBManager extends SQLiteOpenHelper {
    public static DBManager DBManager = null;

    private static final String DATABASE_NAME = "workspaces.db";
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

        /* Create Workspaces Table */
        db.execSQL("CREATE TABLE " + WORKSPACES_TABLE_NAME + " (" + _ID
                + " INTEGER PRIMARY KEY AUTO INCREMENT, " + WORKSPACES_NAME + " TEXT NOT NULL, "
                + WORKSPACES_KEY + " TEXT NOT NULL);");


        /* Create Users Table */
        db.execSQL("CREATE TABLE " + USERS_TABLE_NAME
                + " (" + _ID + " INTEGER PRIMARY KEY AUTO INCREMENT, "
                + USERS_UID + " TEXT NOT NULL, "
                + USERS_NAME + " TEXT NOT NULL"
                + USERS_EMAIL + " TEXT NOT NULL"
                + USERS_WORKSPACE + "TEXT NOT NULL");



    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /*Drop Workspaces Table */
        db.execSQL("DROP TABLE IF EXISTS " + WORKSPACES_TABLE_NAME);

        /*Drop User Table*/
        db.execSQL("DROP TABLE IF EXISTS " + USERS_TABLE_NAME);

        onCreate(db);
    }
}
