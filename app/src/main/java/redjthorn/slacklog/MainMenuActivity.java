package redjthorn.slacklog;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainMenuActivity extends AppCompatActivity {

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
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }





}
