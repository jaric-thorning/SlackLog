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
                //webHandler.execute("https://slack.com/api/team.accessLogs", "xoxp-4619031783-157821268627-294975365697-6df4b212f43de1de7fc02fde9cc3e5e8");

                WebHandler webHandler = new WebHandler(v.getContext());
                webHandler.updateUsers(finalWorkspaceString);
            }
        });
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    private void doRequest() {

        RequestQueue MyRequestQueue = Volley.newRequestQueue(this);

        Log.d(TAG, "Sending request.");
        String url = "https://slack.com/api/team.list";
        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                //This code is executed if there is an error.
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put("token", "xoxp-4619031783-157821268627-294975365697-6df4b212f43de1de7fc02fde9cc3e5e8");
                return MyData;
            }
        };


        MyRequestQueue.add(MyStringRequest);

    }
}
