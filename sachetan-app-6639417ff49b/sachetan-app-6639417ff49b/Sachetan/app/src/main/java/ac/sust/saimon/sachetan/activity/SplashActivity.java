package ac.sust.saimon.sachetan.activity;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;

import java.util.ArrayList;

import ac.sust.saimon.sachetan.R;
import ac.sust.saimon.sachetan.data.model.IncidentType;
import ac.sust.saimon.sachetan.network.ApiCallHelper;
import ac.sust.saimon.sachetan.network.VolleySingleton;

public class SplashActivity extends AppCompatActivity {

    private TextView tvStatus;
    private final int DELAY = 0;
    RelativeLayout parentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spalsh);
        parentLayout = (RelativeLayout) findViewById(R.id.layout_parent);
        tvStatus = (TextView) findViewById(R.id.tv_status);
        parentLayout.setEnabled(false);
        parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectToServer();
            }
        });
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                connectToServer();
            }
        }, DELAY);

    }


    private Response.Listener<JSONArray> createIncidentTypeReqListener() {
        return new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    Log.e("Volley Response:", response.toString());
                    ObjectMapper mapper = new ObjectMapper();
                    ArrayList<IncidentType> responseBody = mapper.readValue(response.toString(),
                            new TypeReference<ArrayList<IncidentType>>() {
                    });
                    VolleySingleton.getInstance(SplashActivity.this).setIncidentTypes(responseBody);
                    String token = VolleySingleton.getInstance(SplashActivity.this).getToken();
                    if (token == null) {
                        Intent intent = new Intent(SplashActivity.this, SignInActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        startActivity(new Intent(SplashActivity.this, MapsActivity.class));
                        finish();
                    }

                } catch (Exception e) {
                    tvStatus.setText(R.string.message_error_network);
                    parentLayout.setEnabled(true);
                }
            }
        };
    }

    private Response.ErrorListener createReqErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley Error", "" + error.getMessage());
                if (error.getMessage() == null) {
                    tvStatus.setText(R.string.message_error_network);
                    parentLayout.setEnabled(true);
                } else {
                    goToLogin();
                }
            }
        };
    }

    private void connectToServer() {
        parentLayout.setEnabled(false);
        tvStatus.setText(R.string.message_progress_connecting);
        ApiCallHelper.getIncidentTypes(SplashActivity.this,
                createIncidentTypeReqListener(),
                createReqErrorListener());
    }

    private void goToLogin() {
        startActivity(new Intent(SplashActivity.this, SignInActivity.class));
        finish();
    }
}