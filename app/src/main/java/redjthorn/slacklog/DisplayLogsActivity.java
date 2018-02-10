package redjthorn.slacklog;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static redjthorn.slacklog.Constants.*;

public class DisplayLogsActivity extends AppCompatActivity {
    private static final String TAG = "SlackLog:DispLogAct";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_logs);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);



        ListView listview = (ListView) findViewById(R.id.logListView);

        SQLiteDatabase db = DBManager.DBManager.getReadableDatabase();

        try {
            Cursor cursor = db.rawQuery("SELECT "
                    + USERS_NAME + ","
                    + "max(" + LOG_DATE_LAST + ") "
                    + " FROM " + LOG_TABLE_NAME
                    + " INNER JOIN " + USERS_TABLE_NAME
                    + " on " + LOG_TABLE_NAME + "." + LOG_UID
                    + " = " + USERS_TABLE_NAME + "." + USERS_UID
                    + " group by " + LOG_TABLE_NAME + "." + LOG_UID
                    + " order by " + LOG_DATE_LAST + " DESC"
                    + ";", null);

            List<String> dates = new ArrayList<>();
            List<String> usernames = new ArrayList<>();


            while (cursor.moveToNext()) {
                String user = cursor.getString(0);
                String last_date = cursor.getString(1);

                Log.d(TAG, "Loading: " + user + " " + last_date);

                usernames.add(user);
                dates.add(last_date);


            }


            listview.setAdapter(new logListAdapter(this, usernames.toArray(new String[usernames.size()]),
                    dates.toArray(new String[dates.size()])));

        } catch (SQLException e){
            Log.d(TAG, e.toString());
        }
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
