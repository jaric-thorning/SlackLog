package redjthorn.slacklog;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import slackapi.WebHandler;

import static redjthorn.slacklog.Constants.LOG_COUNT;
import static redjthorn.slacklog.Constants.LOG_COUNTRY;
import static redjthorn.slacklog.Constants.LOG_DATE_FIRST;
import static redjthorn.slacklog.Constants.LOG_DATE_LAST;
import static redjthorn.slacklog.Constants.LOG_ID;
import static redjthorn.slacklog.Constants.LOG_IP;
import static redjthorn.slacklog.Constants.LOG_ISP;
import static redjthorn.slacklog.Constants.LOG_REGION;
import static redjthorn.slacklog.Constants.LOG_TABLE_NAME;
import static redjthorn.slacklog.Constants.LOG_UAGENT;
import static redjthorn.slacklog.Constants.LOG_UID;
import static redjthorn.slacklog.Constants.LOG_USERNAME;
import static redjthorn.slacklog.Constants.USERS_EMAIL;
import static redjthorn.slacklog.Constants.USERS_NAME;
import static redjthorn.slacklog.Constants.USERS_REAL_NAME;
import static redjthorn.slacklog.Constants.USERS_TABLE_NAME;
import static redjthorn.slacklog.Constants.USERS_UID;
import static redjthorn.slacklog.Constants.USERS_WORKSPACE;
import static redjthorn.slacklog.Constants.WORKSPACES_KEY;
import static redjthorn.slacklog.Constants.WORKSPACES_NAME;
import static redjthorn.slacklog.Constants.WORKSPACES_TABLE_NAME;

/**
 * Created by jaricthorning on 25/5/18.
 */

public class DBUpdater {

    private static final String TAG = "SlackLog:DBUpdater";

    String workspace = null;
    String key = null;

    public DBUpdater(String workspace){

        this.workspace = workspace;

        SQLiteDatabase db =  DBManager.DBManager.getReadableDatabase();

        String[] FROM = { WORKSPACES_KEY, };
        String WHERECLAUSE = WORKSPACES_NAME + " = ?";
        String[] WHEREARGS = { this.workspace, };
        String ORDER_BY = WORKSPACES_KEY + " DESC";

        Log.d(TAG, "Getting key.");


        Cursor cursor = db.query(WORKSPACES_TABLE_NAME, FROM, WHERECLAUSE , WHEREARGS, null, null, ORDER_BY);


        while(cursor.moveToNext()){
            this.key = cursor.getString(0);
        }


    }


    /**
     * Updates Logs
     * @param context
     */
    public void updateLogs(Context context){

        //TODO: get record of which logs already update
        //TODO: process data, create new request





        String url = "https://slack.com/api/team.accessLogs";

        Map<String, String> params = new HashMap<String, String>();
        params.put("token", this.key);
        params.put("count", "1000"); //1000 results per page, usually get latest logins
        params.put("page", String.valueOf(1));


        WebHandler webHandler = new WebHandler(context);

        webHandler.newRequest(url, params, new Response.Listener<String>() {


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

                            Log.d(TAG, "Found: " + "log - " + String.valueOf(id));

                        } catch (JSONException e) {
                            Log.d(TAG, e.toString());
                        } catch (SQLException e){
                            if(e instanceof SQLiteConstraintException){
                                //Likely primary key exeption, no worries
                                //TODO: Fix this hack
                                Log.d(TAG + ":e", "SQLiteConstraintException - " + e.toString());
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
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }


    /**
     * Updates Users
     * @param context
     */
    public void updateUsers(Context context){
        Log.d(TAG, "Updating Users");

        WebHandler webHandler = new WebHandler(context);

        String url = "https://slack.com/api/users.list";

        Map<String, String> params = new HashMap<String, String>();
        params.put("token", this.key);


        final String final_workspace = this.workspace;

        webHandler.newRequest(url, params, new Response.Listener<String>() {
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
        });

    }
}
