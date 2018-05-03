package redjthorn.slacklog;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;

import static redjthorn.slacklog.Constants.LOG_COUNT;
import static redjthorn.slacklog.Constants.LOG_DATE_LAST;
import static redjthorn.slacklog.Constants.LOG_ID;
import static redjthorn.slacklog.Constants.LOG_TABLE_NAME;
import static redjthorn.slacklog.Constants.LOG_UID;
import static redjthorn.slacklog.Constants.USERS_NAME;
import static redjthorn.slacklog.Constants.USERS_REAL_NAME;
import static redjthorn.slacklog.Constants.USERS_TABLE_NAME;
import static redjthorn.slacklog.Constants.USERS_UID;

public class UserActivity extends AppCompatActivity {

    private static final String TAG = "SlackLog:UserActivity";

    private final UserActivity thisActivity = this;

    String userName;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Bundle extras = getIntent().getExtras();

        TextView userNameText = (TextView) findViewById(R.id.userName);
        TextView userIdText = (TextView) findViewById(R.id.userID);



        if(extras == null){
            userNameText.setText("No User");
            userId = "";
        } else {
            userName = extras.getString("userName");
            userId = extras.getString("userId");

            //TODO: Get user information and fill.
            //FILL:
            userNameText.setText(userName);
            userIdText.setText(userId);
        }


         /* - Populate Log List View - */
        final ListView userList = (ListView) findViewById(R.id.logList);

        // Default

        List<String> values = new ArrayList<>();


        String ORDER_BY = LOG_DATE_LAST + " DESC";
        String WHERE = LOG_UID + " = ?";
        String WHEREARGS[] = {userId};
        String GROUP_BY = "strftime('%d-%m-%Y'," + LOG_DATE_LAST + "/1,'unixepoch')";
        String[] FROM = {LOG_ID, GROUP_BY, LOG_UID, "count("+GROUP_BY +")" };
        try {
            SQLiteDatabase db = DBManager.DBManager.getReadableDatabase();

            Cursor cursor = db.query(LOG_TABLE_NAME, FROM, WHERE, WHEREARGS, GROUP_BY, null, ORDER_BY);

            startManagingCursor(cursor);

            //Calculate counts per day
            while(cursor.moveToNext()){
                int log_id = cursor.getInt(0);
                String log_date_last = cursor.getString(1);

                Log.d(TAG, "RAW: " + log_date_last);

                /*// convert seconds to milliseconds
                Date date = new Date(Integer.valueOf(log_date_last));
                // the format of your date
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                // give a timezone reference for formatting (see comment at the bottom)
                sdf.setTimeZone(TimeZone.getTimeZone("GMT-0"));
                String formattedDate = sdf.format(date);*/

                values.add(log_date_last);
                Log.d(TAG, "Found log: " + log_date_last + " (" + cursor.getString(3) +")");
            }

        } catch (SQLException e){
            Log.d(TAG, e.toString());
        } catch (Exception e){
            Log.d(TAG, e.toString());
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
            }

        });



        GraphView graph = (GraphView) findViewById(R.id.graph);

        // generate Dates
        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.DATE, -100);

        Date start = calendar.getTime();

        List<DataPoint> dataPoints = new ArrayList<>();
        Random random = new Random();

        for(int i = 0; i < 100; i++){
            dataPoints.add(new DataPoint(calendar.getTime(), random.nextInt(20)));
            calendar.add(Calendar.DATE, 1);
        }

        Date end = calendar.getTime();

        // you can directly pass Date objects to DataPoint-Constructor
        // this will convert the Date to double via Date#getTime()
        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(dataPoints.toArray(new DataPoint[100]));
        graph.addSeries(series);

        // styling
        /*series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) {
                return Color.rgb((int) data.getX() * 255 / 4, (int) Math.abs(data.getY() * 255 / 6), 100);
            }
        });*/

        series.setSpacing(10);

        // draw values on top
        series.setDrawValuesOnTop(false);
        series.setValuesOnTopColor(Color.RED);
        //series.setValuesOnTopSize(50);

        //--- END ---

        // set date label formatter
//        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this));
        graph.getGridLabelRenderer().setNumHorizontalLabels(1); // only 4 because of the space
//        graph.getGridLabelRenderer().setHorizontalLabelsAngle(90);

        // use static labels for horizontal and vertical labels
        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
        staticLabelsFormatter.setHorizontalLabels(new String[]{"100 days", "50 days", "today"});
        graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);



        // set manual x bounds to have nice steps
        graph.getViewport().setMinX(start.getTime());
        graph.getViewport().setMaxX(end.getTime());
        graph.getViewport().setXAxisBoundsManual(true);


        // as we use dates as labels, the human rounding to nice readable numbers
        // is not necessary
        graph.getGridLabelRenderer().setHumanRounding(true);



        graph.getGridLabelRenderer().setHorizontalAxisTitle("Date");
        graph.getGridLabelRenderer().setVerticalAxisTitle("Logins");

        graph.getGridLabelRenderer().setLabelHorizontalHeight(10);

        //Make graph scrollable and scalable
        //graph.getViewport().setScalable(true);
        graph.getViewport().setScalableY(true);
        graph.getViewport().setScrollable(true);
        graph.getViewport().setScrollableY(true);



        series.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                Toast.makeText(thisActivity, "Series1: On Data Point clicked: " + dataPoint, Toast.LENGTH_SHORT).show();
            }
        });


    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
