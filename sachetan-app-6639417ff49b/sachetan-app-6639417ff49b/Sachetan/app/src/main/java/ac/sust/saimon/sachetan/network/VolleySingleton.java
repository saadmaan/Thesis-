package ac.sust.saimon.sachetan.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.List;

import ac.sust.saimon.sachetan.R;
import ac.sust.saimon.sachetan.data.model.IncidentType;

/**
 * Created by Saimon on 18-Feb-16.
 */
public class VolleySingleton {
    private static VolleySingleton mInstance;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private static Context mCtx;
    private String token;
    private ArrayList<IncidentType> incidentTypes;
    private static boolean hasToken = true;
    public static final String PREFS_NAME = "ac_sust_sachetan_shared_pref";
    private static final long HOUR = 3600000L;
    private static final int MAX_ALLOWED_REPORTS = 5;

    private VolleySingleton(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();

        mImageLoader = new ImageLoader(mRequestQueue,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap>
                            cache = new LruCache<String, Bitmap>(20);

                    @Override
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                });
    }

    public static synchronized VolleySingleton getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new VolleySingleton(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }

    public String getToken() {
        // if token is not set, load from preferences
        if (this.token == null && hasToken == true) {
            loadTokenFromPreferences();
            // if preference is empty, set flag to null so preferences are not read
            // for future method calls
            if (this.token == null)
                hasToken = false;
        }
        return token;

    }

    public void setToken(String token) {
        // save to sharedPref
        if (token == null)
            return;
        SharedPreferences sharedPref = mCtx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(mCtx.getString(R.string.sharedpref_token), token);
        editor.apply();
        this.token = token;
        hasToken = true;
    }

    public boolean isMaxAllowedReportsExceeded() {
        SharedPreferences sharedPref = mCtx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int createdCount = sharedPref.getInt(mCtx.getString(R.string.sharedpref_create_count), 0);
        long lastTimeStamp = sharedPref.getLong(mCtx.getString(R.string.sharedpref_create_timestamp), 0);
        long currentTimeStamp = System.currentTimeMillis();
        if( currentTimeStamp - lastTimeStamp < HOUR && createdCount >= 5)
            return true;
        return false;
    }

    public void setCreateTime() {
        SharedPreferences sharedPref = mCtx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int createdCount = sharedPref.getInt(mCtx.getString(R.string.sharedpref_create_count), 0);
        long lastTimeStamp = sharedPref.getLong(mCtx.getString(R.string.sharedpref_create_timestamp), 0);
        long currentTimeStamp = System.currentTimeMillis();
        SharedPreferences.Editor editor = sharedPref.edit();
        if(currentTimeStamp - lastTimeStamp >= HOUR) {
            editor.putLong(mCtx.getString(R.string.sharedpref_create_timestamp), currentTimeStamp);
            editor.putInt(mCtx.getString(R.string.sharedpref_create_count), 1);
        } else
            editor.putInt(mCtx.getString(R.string.sharedpref_create_count), createdCount + 1);
        editor.apply();
    }

    public ArrayList<IncidentType> getIncidentTypes(){
        return incidentTypes;
    }

    public void setIncidentTypes(ArrayList<IncidentType> incidentTypes){
        this.incidentTypes = incidentTypes;
    }

    private void loadTokenFromPreferences() {
        SharedPreferences sharedPref = mCtx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        setToken(sharedPref.getString(mCtx.getString(R.string.sharedpref_token), null));
    }
}