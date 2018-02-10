package redjthorn.slacklog;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import slackapi.WebHandler;

public class MainMenuActivity extends AppCompatActivity {

    private static final String TAG = "SlackLog:MainMenu";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        TextView selectedWorkspaceLabel = (TextView) findViewById(R.id.SelectedWorkspaceText);

        Bundle extras = getIntent().getExtras();

        String selectedWorkspace = "None Selected";
        if(extras == null){
            // Show Alert
            Toast.makeText(getApplicationContext(),
                    "Workspace data not available.", Toast.LENGTH_LONG)
                    .show();
            onBackPressed();
        } else {
            selectedWorkspace = extras.getString("selectedWorkspace");
        }

        selectedWorkspaceLabel.setText(selectedWorkspace);

        final String finalWorkspaceString = selectedWorkspace;

        Button viewUsersButton = (Button) findViewById(R.id.viewUsersButton);

        viewUsersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), DisplayUsersActivity.class);

                i.putExtra("workspaceId", finalWorkspaceString);

                startActivity(i);
                return;
            }
        });

        Button updateButton = (Button) findViewById(R.id.updateButton);

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //WebHandler webHandler = new WebHandler();

                WebHandler webHandler = new WebHandler(v.getContext());
                webHandler.updateUsers(finalWorkspaceString);
                webHandler.updateLogs(finalWorkspaceString, 1, new WebHandler.VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        //TODO: Generalise method and do processing here
                    }
                });
            }
        });

        Button logsButton = (Button) findViewById(R.id.recentLoginsButton);

        logsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), DisplayLogsActivity.class);
                i.putExtra("worksapceId", finalWorkspaceString);
                startActivity(i);
                return;
            }
        });

    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
