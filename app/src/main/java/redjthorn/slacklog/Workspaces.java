package redjthorn.slacklog;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import static redjthorn.slacklog.Constants.*;

public class Workspaces extends AppCompatActivity {

    ListView workspacesListView;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workspaces);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(view.getContext(), AddWorkspace.class);
                startActivityForResult(i, 0);
            }
        });

        /* Start DB Manager */

        DBManager dbManager = new DBManager(this);

        workspacesListView = (ListView) findViewById(R.id.workspacesListView);

        // Defined Array values to show in ListView
        List<String> values = new ArrayList<>();

        SQLiteDatabase db = DBManager.DBManager.getReadableDatabase();
        String[] FROM = { _ID, WORKSPACES_NAME, WORKSPACES_KEY, };
        String ORDER_BY = WORKSPACES_NAME + " DESC";

        Cursor cursor = db.query(WORKSPACES_TABLE_NAME, FROM, null, null, null, null, ORDER_BY);

        startManagingCursor(cursor);

        while(cursor.moveToNext()){
            long id = cursor.getLong(0);
            String name = cursor.getString(1);
            String key = cursor.getString(2);

            values.add(name);
        }

        adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, android.R.id.text1, values);

        // Assign adapter to ListView
        workspacesListView.setAdapter(adapter);

        // ListView Item Click Listener
        workspacesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // ListView Clicked item index
                int itemPosition = position;


                // ListView Clicked item value
                String itemValue = (String) workspacesListView.getItemAtPosition(position);

                Intent i = new Intent(view.getContext(), MainMenuActivity.class);

                i.putExtra("selectedWorkspace", itemValue);

                startActivityForResult(i, 0);
            }

        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_workspaces, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if( id == R.id.action_ClearAllWorkplaces){
            DBManager.DBManager.getWritableDatabase().delete(WORKSPACES_TABLE_NAME, null, null);
            refresh();
        }

        return super.onOptionsItemSelected(item);
    }

    private void refresh(){
        Intent refresh = new Intent(this, Workspaces.class);
        startActivity(refresh);
        this.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        refresh();
    }
}