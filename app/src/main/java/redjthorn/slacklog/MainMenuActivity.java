package redjthorn.slacklog;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
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
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import slackapi.WebHandler;

import static redjthorn.slacklog.Constants.LOG_DATE_LAST;
import static redjthorn.slacklog.Constants.LOG_ID;
import static redjthorn.slacklog.Constants.LOG_TABLE_NAME;
import static redjthorn.slacklog.Constants.LOG_UID;

public class MainMenuActivity extends AppCompatActivity {

    GraphView graph;

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

        final String finalSelectedWorkspace = selectedWorkspace;
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
            public void onClick(final View v) {
                //WebHandler webHandler = new WebHandler();
                WebHandler webHandler = new WebHandler(v.getContext());
                webHandler.updateUsers(finalWorkspaceString);

                webHandler.getMyRequestQueue().addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
                    @Override
                    public void onRequestFinished(Request<Object> request) {
                        graph = (GraphView) findViewById(R.id.all_user_graph);
                        updateGraph();
                    }
                });


                for(int i = 0; i <= 100; i++) {
                    webHandler.updateLogs(finalWorkspaceString, i, new WebHandler.VolleyCallback() {
                        @Override
                        public void onSuccess(String result) {
                            //TODO: Generalise method and do processing here
                        }
                    });
                }


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

        graph = (GraphView) findViewById(R.id.all_user_graph);
        updateGraph();
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    private void updateGraph() {

        if (graph == null) {
            return;
        }

        try {
            String ORDER_BY = LOG_DATE_LAST + " DESC";
            //String WHERE = LOG_UID + " = ?";
            //String WHEREARGS[] = {userId};
            //String GROUP_BY = "strftime('%d-%m-%Y'," + LOG_DATE_LAST + "/1,'unixepoch')";
            String[] FROM = {LOG_ID, LOG_DATE_LAST};

            SQLiteDatabase db = DBManager.DBManager.getReadableDatabase();

            Cursor cursor = db.query(LOG_TABLE_NAME, FROM, null, null, null, null, ORDER_BY);

            startManagingCursor(cursor);


            //list of dates + int

            HashMap<Date, Integer> dateLogins = new HashMap<>();


            Calendar c = new GregorianCalendar();
            //Calculate counts per day
            while (cursor.moveToNext()) {


                int log_id = cursor.getInt(0);
                String log_date_last = cursor.getString(1);

                Log.d(TAG, "RAW: " + log_date_last);

                Date date = new Date(Integer.valueOf(log_date_last) * 1000L);

                c.setTime(date);
                c.set(Calendar.HOUR, 0);
                c.set(Calendar.MINUTE, 0);
                c.set(Calendar.SECOND, 0);

                Date dateKey = c.getTime();

                if (dateLogins.containsKey(dateKey)) {
                    dateLogins.put(dateKey, dateLogins.get(dateKey) + 1);
                    Log.d(TAG, "Added new date " + dateKey.toString());

                } else {
                    dateLogins.put(dateKey, 1);
                    Log.d(TAG, "Added to date " + dateKey.toString());
                }


            }


            List<DataPoint> dataPoints = new ArrayList<>();



            Calendar cutoff = new GregorianCalendar();

            cutoff.setTime(Calendar.getInstance().getTime());
            cutoff.add(Calendar.YEAR, -1);

            Date start = Calendar.getInstance().getTime();
            Date end = new Date(0);

            int maxValue = 0;

            int count = 0;

            long DAY = 1000 * 60 * 60 * 24;

            for (Map.Entry<Date, Integer> entry : dateLogins.entrySet()) {
                Date date = entry.getKey();
                Integer value = entry.getValue();

                if(date.getTime() > cutoff.getTime().getTime()) {
                    dataPoints.add(new DataPoint(date, value));

                    Log.d(TAG, "Adding date " + date.toString() + ": " + value);
                    count++;

                    if (date.after(end)) {
                        end = date;
                    }

                    if (date.before(start)) {
                        start = date;
                    }

                    if (value > maxValue) {
                        maxValue = value;
                    }
                }

            }

            graph.removeAllSeries();

            BarGraphSeries<DataPoint> series = new BarGraphSeries<>(dataPoints.toArray(new DataPoint[count]));
            PointsGraphSeries<DataPoint> series1 = new PointsGraphSeries<>(dataPoints.toArray(new DataPoint[count]));

            series1.setShape(PointsGraphSeries.Shape.POINT);
            series1.setSize(2f);

            graph.addSeries(series);

            graph.getViewport().setMinX(cutoff.getTime().getTime());
            graph.getViewport().setMaxX(end.getTime());
            graph.getViewport().setXAxisBoundsManual(true);


            graph.getViewport().setMaxY(maxValue + 10);
            graph.getViewport().setMinY(0);
            graph.getViewport().setYAxisBoundsManual(true);

            /*DisplayMetrics displayMetrics = new DisplayMetrics();

            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            graph.getViewport().setMaxXAxisSize(displayMetrics.widthPixels);*/


            graph.getGridLabelRenderer().setHumanRounding(false);

            //graph.getGridLabelRenderer().setHorizontalAxisTitle("Date");
            //graph.getGridLabelRenderer().setVerticalAxisTitle("Logins");

            graph.getGridLabelRenderer().setHorizontalAxisTitleTextSize(30);
            graph.getGridLabelRenderer().setVerticalAxisTitleTextSize(30);

            //graph.getGridLabelRenderer().setNumHorizontalLabels(2);


            /*SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/YY");


            StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
            staticLabelsFormatter.setHorizontalLabels(new String[]{simpleDateFormat.format(start),
                    simpleDateFormat.format(end)});

            graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
            graph.getGridLabelRenderer().setPadding(32);
            //graph.getGridLabelRenderer().setLabelVerticalWidth(40);
            graph.getGridLabelRenderer().setLabelHorizontalHeight(30);
            graph.getGridLabelRenderer().setHumanRounding(false);*/



            // set date label formatter
            graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this));
            graph.getGridLabelRenderer().setNumHorizontalLabels(3); // only 4 because of the space
            graph.getGridLabelRenderer().setHumanRounding(false);

            //graph.getGridLabelRenderer().setHorizontalLabelsVisible(false);
            graph.getGridLabelRenderer().setVerticalLabelsVisible(false);

            graph.getViewport().setScalableY(false);
            graph.getViewport().setScrollable(false);
            graph.getViewport().setScrollableY(false);
            graph.getViewport().setScalable(false);

        } catch (SQLException e){
            Log.d(TAG, e.toString());
        } catch (Exception e){
            Log.d(TAG, e.toString());
        }
    }
}
