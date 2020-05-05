package ac.sust.saimon.sachetan.application;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import ac.sust.saimon.sachetan.R;

/**
 * Created by Saimon on 30-Dec-15.
 */
public class Sachetan extends MultiDexApplication{
    public static final String PREFS_NAME = "ac_sust_sachetan_shared_pref";
    private String baseURL;
    private String token;
    private boolean hasURL = true;
    private boolean hasToken = true;

    public String getBaseURL() {
        // if url is not set, load from preferences
        if (this.baseURL == null && hasURL == true) {
            loadUrlFromPreferences();
            // if preference is empty, set flag to null so preferences are not read
            // for future method calls
            if (this.baseURL == null)
                hasURL = false;
        }
        return baseURL;
    }

    public String getToken() {
        // if url is not set, load from preferences
        if (this.token == null && hasToken == true) {
            loadTokenFromPreferences();
            // if preference is empty, set flag to null so preferences are not read
            // for future method calls
            if (this.token == null)
                hasToken = false;
        }
        return token;

    }

    public void setBaseURL(String baseURL) {
        // save to sharedPref
        if (baseURL == null)
            return;
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.sharedpref_baseurl), baseURL);
        editor.apply();
        this.baseURL = baseURL;
        hasURL = true;
    }

    public void setToken(String token) {
        // save to sharedPref
        if (token == null)
            return;
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.sharedpref_token), token);
        editor.apply();
        this.token = token;
        hasToken = true;
    }

    private void loadUrlFromPreferences() {
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        setBaseURL(sharedPref.getString(getString(R.string.sharedpref_baseurl), null));
    }

    private void loadTokenFromPreferences() {
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        setToken(sharedPref.getString(getString(R.string.sharedpref_token), null));
    }

}
