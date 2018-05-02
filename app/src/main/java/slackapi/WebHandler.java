package slackapi;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static redjthorn.slacklog.Constants.*;

import redjthorn.slacklog.DBManager;

/**
 * Created by jaricthorning on 29/1/18.
 */
public class WebHandler {

    private static final String TAG = "SlackLog:Webhandler";

    RequestQueue MyRequestQueue;

    public WebHandler(Context c) {
        MyRequestQueue = Volley.newRequestQueue(c);
    }

    public RequestQueue getMyRequestQueue() {
        return MyRequestQueue;
    }

    public void updateLogs(String workspace, final int page, final VolleyCallback volleyCallback) {


        final String final_workspace = workspace;

        Log.d(TAG, "Updating Logs");

        String url = "https://slack.com/api/team.accessLogs";

        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG, "Response: " + response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("logins");

                    SQLiteDatabase db = DBManager.DBManager.getWritableDatabase();

                    for (int i=0; i < jsonArray.length(); i++)
                    {
                        try {

                            ContentValues values = new ContentValues();
                            JSONObject oneObject = jsonArray.getJSONObject(i);

                            String uid = oneObject.getString("user_id");
                            String username = oneObject.getString("username");
                            String date_first = oneObject.getString("date_first");
                            String date_last = oneObject.getString("date_last");
                            String count = oneObject.getString("count");
                            String ip = oneObject.getString("ip");
                            String user_agent = oneObject.getString("user_agent");
                            String isp = oneObject.getString("isp");
                            String country = oneObject.getString("country");
                            String region = oneObject.getString("region");


                            values.put(LOG_UID, uid);
                            values.put(LOG_USERNAME, username);
                            values.put(LOG_DATE_FIRST, date_first);
                            values.put(LOG_DATE_LAST, date_last);
                            values.put(LOG_COUNT, count);
                            values.put(LOG_IP, ip);
                            values.put(LOG_UAGENT, user_agent);
                            values.put(LOG_ISP, isp);
                            values.put(LOG_COUNTRY, country);
                            values.put(LOG_REGION, region);

                            String toHash = uid + username + date_first + date_last + count +
                                    ip + user_agent + isp + country + region;

                            int id = toHash.hashCode();
                            values.put(LOG_ID, id);

                            db.insertOrThrow(LOG_TABLE_NAME, null, values);

                            Log.d(TAG, "Found: " + "log-" + String.valueOf(id));

                        } catch (JSONException e) {
                            Log.d(TAG, e.toString());
                        } catch (SQLException e){
                            if(e instanceof SQLiteConstraintException){
                                //Likely primary key exeption, no worries
                                //TODO: Fix this hack
                            }
                            else {
                                Log.d(TAG, "Failed to insert into database.");
                                Log.d(TAG, e.toString());
                            }
                        } catch (Exception e){
                            Log.d(TAG, "Something else went wrong.");
                            Log.d(TAG, e.toString());
                        }
                    }

                }
                catch (Exception e){
                    Log.d(TAG, e.toString());
                }




            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                //This code is executed if there is an error.
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();

                SQLiteDatabase db =  DBManager.DBManager.getReadableDatabase();

                String[] FROM = { WORKSPACES_KEY, };
                String WHERECLAUSE = WORKSPACES_NAME + " = ?";
                String[] WHEREARGS = { final_workspace, };
                String ORDER_BY = WORKSPACES_KEY + " DESC";

                Log.d(TAG, "Getting key.");


                Cursor cursor = db.query(WORKSPACES_TABLE_NAME, FROM, WHERECLAUSE , WHEREARGS, null, null, ORDER_BY);


                String key = "";


                while(cursor.moveToNext()){
                    key = cursor.getString(0);
                }


                Log.d(TAG, "Using key: " + key);
                MyData.put("token", key);
                MyData.put("count", "1000"); //1000 results per page, usually get latest logins
                MyData.put("page", String.valueOf(page));


                return MyData;
            }
        };


        MyRequestQueue.add(MyStringRequest);
    }



    public void updateUsers(String workspace) {


        final String final_workspace = workspace;

        Log.d(TAG, "Updating Users");

        String url = "https://slack.com/api/users.list";

        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG, "Response: " + response);



                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("members");

                    SQLiteDatabase db = DBManager.DBManager.getWritableDatabase();

                    for (int i=0; i < jsonArray.length(); i++)
                    {
                        try {
                            JSONObject oneObject = jsonArray.getJSONObject(i);
                            // Pulling items from the array
                            String name = oneObject.getString("name");


                            ContentValues values = new ContentValues();

                            values.put(USERS_UID, oneObject.getString("id"));
                            values.put(USERS_NAME, name);

                            values.put(USERS_WORKSPACE, final_workspace);

                            JSONObject profile = oneObject.getJSONObject("profile");
                            values.put(USERS_EMAIL, profile.getString("email"));

                            /* May not have real name, this is ok */
                            try {
                                values.put(USERS_REAL_NAME,oneObject.getString("real_name"));
                            }
                            catch (JSONException e){
                                //No real name, no worries
                            }

                            db.insertOrThrow(USERS_TABLE_NAME, null, values);


                            Log.d(TAG, "Found: " + name);

                        } catch (JSONException e) {
                            Log.d(TAG, e.toString());
                        } catch (SQLException e){
                            if(e instanceof SQLiteConstraintException){
                                //Likely primary key exeption, no worries
                                //TODO: Fix this hack
                            }
                            else {
                                Log.d(TAG, "Failed to insert into database.");
                                Log.d(TAG, e.toString());
                            }
                        }
                    }


                }
                catch (Exception e){
                    Log.d(TAG, e.toString());
                }




            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                //This code is executed if there is an error.
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();

                SQLiteDatabase db =  DBManager.DBManager.getReadableDatabase();

                String[] FROM = { WORKSPACES_KEY, };
                String WHERECLAUSE = WORKSPACES_NAME + " = ?";
                String[] WHEREARGS = { final_workspace, };
                String ORDER_BY = WORKSPACES_KEY + " DESC";

                Log.d(TAG, "Getting key.");


                Cursor cursor = db.query(WORKSPACES_TABLE_NAME, FROM, WHERECLAUSE , WHEREARGS, null, null, ORDER_BY);


                String key = "";


                while(cursor.moveToNext()){
                    key = cursor.getString(0);
                }


                Log.d(TAG, "Using key: " + key);
                MyData.put("token", key);

                return MyData;
            }
        };


        MyRequestQueue.add(MyStringRequest);
    }

    public interface VolleyCallback {
        void onSuccess(String result);
    }



}