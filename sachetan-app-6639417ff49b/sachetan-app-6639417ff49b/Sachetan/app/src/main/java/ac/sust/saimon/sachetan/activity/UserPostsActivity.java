package ac.sust.saimon.sachetan.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;

import java.util.ArrayList;

import ac.sust.saimon.sachetan.R;
import ac.sust.saimon.sachetan.adapter.ReportAdapter;
import ac.sust.saimon.sachetan.data.api.GenericResponse;
import ac.sust.saimon.sachetan.data.model.Report;
import ac.sust.saimon.sachetan.data.model.User;
import ac.sust.saimon.sachetan.network.ApiCallHelper;
import ac.sust.saimon.sachetan.network.VolleySingleton;

public class UserPostsActivity extends AppCompatActivity {

    private ListView reportListView;
    private LinearLayout llStatus;
    private TextView tvStatus;
    private ImageButton btnReload;
    private Button btnLogOut;
    private LinearLayout llayoutUserInfo;
    private String idReport;
    private TextView tvEmail;
    private TextView tvName;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_posts);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        reportListView = (ListView) findViewById(R.id.listview_reports);

        tvEmail = (TextView) findViewById(R.id.tv_self_username);
        tvName = (TextView) findViewById(R.id.tv_self_email);
        llayoutUserInfo = (LinearLayout) findViewById(R.id.ll_user_info);
        llStatus = (LinearLayout) findViewById(R.id.layout_status);
        tvStatus = (TextView) findViewById(R.id.textview_status);
        btnReload = (ImageButton) findViewById(R.id.btn_reload);
        btnLogOut = (Button) findViewById(R.id.btn_logout);
        setSupportActionBar(toolbar);
        btnReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retry();
            }
        });
        llayoutUserInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Edit user info
            }
        });
        ApiCallHelper.getSelfUser(this, createUserReportsListener(), createErrorListener());
    }

    private Response.Listener<JSONObject> createUserReportsListener(){
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.e("Volley Response", response.toString());
                    ObjectMapper mapper = new ObjectMapper();
                    User user = mapper.readValue(response.toString(), User.class);
                        //incidentTypes = responseBody.getData();
                        tvName.setText(user.getFirstName() + " " + user.getLastName());
                        tvEmail.setText(user.getEmail());
                        reportListView.setAdapter(new ReportAdapter(UserPostsActivity.this,
                                new ArrayList<>(user.getReports()), true));
                        llStatus.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(UserPostsActivity.this, "Error: invalid server response",
                            Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    private Response.Listener<JSONObject> createDeleteReportListener(){
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressDialog.dismiss();
                try {
                    // TODO: fix the conversion from JSON to class
                    Log.e("Volley Response", response.toString());
                    ObjectMapper mapper = new ObjectMapper();
                    GenericResponse responseBody = mapper.readValue(response.toString(),
                            GenericResponse.class);
                    if (responseBody.getStatus() == 1) {
                        ApiCallHelper.getSelfUser(UserPostsActivity.this,
                                createUserReportsListener(),
                                createErrorListener());
                        Toast.makeText(UserPostsActivity.this, "Deleted report",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(UserPostsActivity.this, responseBody.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(UserPostsActivity.this, getString((R.string.message_error_server)),
                            Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    private Response.ErrorListener createErrorListener(){
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Activity", "Volley Error");
                if(progressDialog != null)
                    progressDialog.dismiss();
                tvStatus.setText(R.string.message_error_network);
                btnReload.setVisibility(View.VISIBLE);
            }
        };
    }

    private Response.ErrorListener createDeletionErrorListener(){
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Activity", "Volley Error");
                if(progressDialog != null)
                    progressDialog.dismiss();
                Toast.makeText(UserPostsActivity.this, "Server error: unable to delete", Toast.LENGTH_SHORT).show();
            }
        };
    }

    public void logOut(View view){
        VolleySingleton.getInstance(this).setToken(null);
        Intent intent = new Intent(UserPostsActivity.this, SignInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    public void retry(){
        btnReload.setVisibility(View.GONE);
        tvStatus.setText("Loading...");
        ApiCallHelper.getSelfUser(this, createUserReportsListener(), createErrorListener());
    }

    public void deleteReport(View view){
        try {
            idReport = (String) view.getTag();
        }catch (NullPointerException e){ Log.e("OOPS", "Null reportId"); return; }
        new AlertDialog.Builder(this)
                .setTitle("Delete Report")
                .setMessage("Are you sure?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        progressDialog = ProgressDialog.show(UserPostsActivity.this, "Deleting report",
                                "please wait...", true);
                        ApiCallHelper.deleteIncident(UserPostsActivity.this,
                                createDeleteReportListener(),
                                createDeletionErrorListener(),
                                idReport);
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }
}
