package ac.sust.saimon.sachetan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.maps.model.LatLngBounds;

import org.json.JSONArray;

import java.util.ArrayList;

import ac.sust.saimon.sachetan.R;
import ac.sust.saimon.sachetan.adapter.ReportAdapter;
import ac.sust.saimon.sachetan.data.model.Report;
import ac.sust.saimon.sachetan.network.ApiCallHelper;

/**
 * Created by Dell on 17-Oct-16.
 */
public class ReportListActivity extends AppCompatActivity{

    private ListView reportListView;
    private LinearLayout llStatus;
    private TextView tvStatus;
    private LatLngBounds bounds;
    private ImageButton btnRetry;
    private String incidentTypeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_reports);
        llStatus = (LinearLayout) findViewById(R.id.layout_status);
        tvStatus = (TextView) findViewById(R.id.textview_status);
        reportListView = (ListView) findViewById(R.id.listview_reports);
        btnRetry = (ImageButton) findViewById(R.id.imgbtn_retry);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        incidentTypeId = intent.getStringExtra("itype");
        Bundle bundle = intent.getParcelableExtra("bundle");
        bounds = bundle.getParcelable("bounds");
        ApiCallHelper.getReportsWithinBounds(this, createReportsFromBoundsListener(), createErrorListener(), incidentTypeId, bounds);
    }

    private Response.Listener<JSONArray> createReportsFromBoundsListener() {
        return new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    // TODO: fix the conversion from JSON to class
                    ObjectMapper mapper = new ObjectMapper();
                    ArrayList<Report> reports = mapper.readValue(response.toString()
                            , new TypeReference<ArrayList<Report>>() {});
                    if(reports.isEmpty()){
                        tvStatus.setText(R.string.message_response_empty);
                        return;
                    }
                    ReportAdapter mAdapter = new ReportAdapter(ReportListActivity.this, reports, false);
                    reportListView.setAdapter(mAdapter);
                    reportListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            if (false)
                                return;
                            // TODO: fix code
                            Intent intent = new Intent(ReportListActivity.this, ViewReportActivity.class);
                            Bundle bundle = new Bundle();
                            Report report = (Report) adapterView.getItemAtPosition(i);
                            Log.e("ON LIST CLICK", report.getDescription());
                            bundle.putSerializable("Report",  report);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    });
                    llStatus.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(ReportListActivity.this, R.string.message_error_server,
                            Toast.LENGTH_SHORT).show();
                    tvStatus.setText(R.string.message_error_server);
                }
            }
        };
    }

    private Response.ErrorListener createErrorListener(){
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley error", "" + error.getMessage());
                tvStatus.setText(R.string.message_error_network);
            }
        };
    }
}
