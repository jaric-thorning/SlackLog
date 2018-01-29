package redjthorn.slacklog;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import static redjthorn.slacklog.Constants.*;

public class AddWorkspace extends AppCompatActivity {

    private static final String TAG = "SlackLog:AddWorkspace";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_workspace);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Button addWorkspace = (Button) findViewById(R.id.workspaceAddButton);
        final EditText nameEdit = (EditText) findViewById(R.id.workspaceNameEdit);
        final EditText keyEdit = (EditText) findViewById(R.id.workspaceKeyEdit);

                addWorkspace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Attempting to add to DB");
                SQLiteDatabase db = DBManager.DBManager.getWritableDatabase();
                ContentValues values = new ContentValues();
                String name = nameEdit.getText().toString();
                String key = keyEdit.getText().toString();

                Boolean dataCorrect = true;

                if(name == null || name.isEmpty()){
                    nameEdit.setError("Name required!");
                    dataCorrect = false;
                }
                if(key == null || key.isEmpty()) {
                    keyEdit.setError("Key required");
                    dataCorrect = false;
                }

                if(dataCorrect){
                    values.put(WORKSPACES_NAME, name);
                    values.put(WORKSPACES_KEY, key);
                    try {
                        db.insertOrThrow(WORKSPACES_TABLE_NAME, null, values);
                        Log.d(TAG, "Added to DB.");
                        setResult(0);
                        finish();
                    }
                    catch (Exception e){
                        Log.d(TAG, "Failed in insert into DB");
                    }
                } else {
                    Log.d(TAG, "Data not valid");
                }

            }
        });
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


}
