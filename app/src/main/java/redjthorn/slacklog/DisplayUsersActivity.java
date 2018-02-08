package redjthorn.slacklog;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import static android.provider.BaseColumns._ID;
import static redjthorn.slacklog.Constants.USERS_EMAIL;
import static redjthorn.slacklog.Constants.USERS_NAME;
import static redjthorn.slacklog.Constants.USERS_REAL_NAME;
import static redjthorn.slacklog.Constants.USERS_TABLE_NAME;
import static redjthorn.slacklog.Constants.USERS_UID;
import static redjthorn.slacklog.Constants.USERS_WORKSPACE;
import static redjthorn.slacklog.Constants.WORKSPACES_KEY;
import static redjthorn.slacklog.Constants.WORKSPACES_NAME;
import static redjthorn.slacklog.Constants.WORKSPACES_TABLE_NAME;

public class DisplayUsersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_users);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        final ListView userList = (ListView) findViewById(R.id.userList);

        // Default

        List<String> values = new ArrayList<>();

        String[] FROM = {USERS_UID, USERS_NAME, USERS_REAL_NAME};
        String ORDER_BY = USERS_REAL_NAME;

        SQLiteDatabase db = DBManager.DBManager.getReadableDatabase();

        Cursor cursor = db.query(USERS_TABLE_NAME, FROM, null, null, null, null, ORDER_BY);

        startManagingCursor(cursor);

        while(cursor.moveToNext()){
            String name = cursor.getString(1);
            String realname = cursor.getString(2);
            if(realname != null && !realname.isEmpty()){
                values.add(realname);
            } else {
                values.add(name);
            }
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);

        userList.setAdapter(adapter);

        // ListView Item Click Listener
        userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // ListView Clicked item index
                int itemPosition = position;

                // ListView Clicked item value
                String itemValue = (String) userList.getItemAtPosition(position);


                Intent i = new Intent(view.getContext(), UserActivity.class);

                //TODO: get UserId and pass through
                //FILL:
                i.putExtra("userId", itemValue);

                startActivity(i);
            }

        });
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
