package slackapi;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.AuthFailureError;
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
 *
 * Basic Web handler for interacting with the Slack API
 */
public class WebHandler {

    private static final String TAG = "SlackLog:Webhandler";

    RequestQueue MyRequestQueue;

    /**
     * Constuctor
     * @param c
     */
    public WebHandler(Context c) {
        MyRequestQueue = Volley.newRequestQueue(c);
    }

    /**
     *
     * @return request queue for polling / setting up listners
     */
    public RequestQueue getMyRequestQueue() {
        return MyRequestQueue;
    }


    public void newRequest(String url, final Map<String, String> params, Response.Listener<String> responseListener, Response.ErrorListener errorListener){

        Log.d(TAG, "Starting new request");

        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url, responseListener,errorListener){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };

        MyRequestQueue.add(MyStringRequest);

    }

}