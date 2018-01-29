package slackapi;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

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




    public void updateUsers(String workspace) {


        final String final_workspace = workspace;

        Log.d(TAG, "Updating Users");

        String url = "https://slack.com/api/users.list";

        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG, "Response: " + response);

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



}