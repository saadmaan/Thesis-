package ac.sust.saimon.sachetan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.Text;

import ac.sust.saimon.sachetan.R;
import ac.sust.saimon.sachetan.data.model.Report;
import ac.sust.saimon.sachetan.data.util.DateFormatter;

public class ViewReportActivity extends AppCompatActivity implements OnMapReadyCallback{

    private TextView dateTime;
    private TextView creatorEmail;
    private TextView incidentType;
    private TextView postText;
    private TextView severity;
    private ImageButton btnDeletePost;
    private LatLng location;

    private Report report;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_report);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        dateTime = (TextView) findViewById(R.id.tv_incident_datetime);
        creatorEmail = (TextView) findViewById(R.id.tv_creator_email);
        incidentType = (TextView) findViewById(R.id.tv_incident_type);
        postText = (TextView) findViewById(R.id.tv_post_text);
        severity = (TextView) findViewById(R.id.tv_incident_severity);
        btnDeletePost = (ImageButton) findViewById(R.id.btn_deletepost);
        report = (Report) getIntent().getExtras().getSerializable("Report");
        if (report != null){
            if (report.getLocation() != null){
                location = new LatLng(report.getLocation()[1], report.getLocation()[0]);
            }
            if (report.getIncidentDate() != null)
                dateTime.setText(DateFormatter.getDateAsString(report.getIncidentDate()));
            else
                dateTime.setText("No Date Info");
            if (report.getIncidentType() != null)
                incidentType.setText(report.getIncidentType().getName());
            else
                incidentType.setText("Unknown type");
            postText.setText(report.getDescription());
            if(report.getSeverity()!= null)
                severity.setText("" + report.getSeverity());
            else
                severity.setText("1");
            if(report.getSeverity()!= null)
                severity.setText("Severity: " + report.getSeverity());
            // TODO: if user is owner, allow deletion
            if(false) {
                btnDeletePost.setVisibility(View.VISIBLE);
                btnDeletePost.setTag(report.getId());
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.getUiSettings().setScrollGesturesEnabled(false);
        if (location != null) {
            googleMap.addMarker(new MarkerOptions().position(location));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 17f));
        }

    }
}
