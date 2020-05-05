package ac.sust.saimon.sachetan.activity;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ac.sust.saimon.sachetan.R;
import ac.sust.saimon.sachetan.adapter.IncidentTypeAdapter;
import ac.sust.saimon.sachetan.data.model.IncidentType;
import ac.sust.saimon.sachetan.data.model.Report;
import ac.sust.saimon.sachetan.fragment.DatePickerFragment;
import ac.sust.saimon.sachetan.fragment.TimePickerFragment;
import ac.sust.saimon.sachetan.network.ApiCallHelper;
import ac.sust.saimon.sachetan.network.VolleySingleton;

public class ReportActivity extends AppCompatActivity implements OnMapReadyCallback, TimePickerFragment.TimeSetListener, DatePickerFragment.DateSetListener {
    private EditText reportDescText;
    private SeekBar severitySeekBar;
    private TextView severityLabel;
    private TextView tvReportTime;
    private TextView tvReportDate;
    private TextView tvStatus;
    private TextView tvLocation;
    private Double[] location;
    private Spinner reportTypeSpinner;
    private Button postButton;
    private Button cancelButton;
    private ProgressDialog progressDialog;
    private AppBarLayout appBarLayout;
    private GoogleMap mMap;

    private final int MAX_LOCATION_CHARS = 35;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
    private Calendar reportCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newreport);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        reportTypeSpinner = (Spinner) findViewById(R.id.spinner_incident_type);
        reportDescText = (EditText) findViewById(R.id.report_desc);
        severitySeekBar = (SeekBar) findViewById(R.id.severity);
        severityLabel = (TextView) findViewById(R.id.severity_label);
        tvLocation = (TextView) findViewById(R.id.tv_location_info);
        tvStatus = (TextView) findViewById(R.id.tv_status);
        tvReportTime = (TextView) findViewById(R.id.tv_incident_time);
        tvReportDate = (TextView) findViewById(R.id.tv_incident_date);
        postButton = (Button) findViewById(R.id.button_post);
        cancelButton = (Button) findViewById(R.id.button_cancel);
        Intent intent = getIntent();
        location = new Double[]{intent.getDoubleExtra("lon", 0), intent.getDoubleExtra("lat", 0)};
        final String[] severityLabels = getResources().getStringArray(R.array.array_severity_label);
        reportTypeSpinner.setAdapter(new IncidentTypeAdapter(this,
                VolleySingleton.getInstance(this).getIncidentTypes()));
        severitySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                severityLabel.setText(severityLabels[progress]);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        reportCalendar = Calendar.getInstance();
        tvReportTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment timePickerFragment = TimePickerFragment.newInstance(ReportActivity.this, reportCalendar);
                timePickerFragment.show(getFragmentManager(), "timePicker");
            }
        });
        tvReportDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment datePickerFragment = DatePickerFragment.newInstance(ReportActivity.this, reportCalendar);
                datePickerFragment.show(getFragmentManager(), "datePicker");
            }
        });
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postReport();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setCurrentDateTimeOnView(reportCalendar);
    }

    public void postReport(){
        if( VolleySingleton.getInstance(this).isMaxAllowedReportsExceeded()){
            tvStatus.setVisibility(View.VISIBLE);
            return;
        }
        tvStatus.setVisibility(View.GONE);
        postButton.setEnabled(false);
        Intent intent = getIntent();
        Report report = new Report();
        report.setDescription(reportDescText.getText().toString());
        report.setIncidentDate(reportCalendar.getTime());
        report.setIncidentType((IncidentType) reportTypeSpinner.getSelectedItem());
        //report.setLocation(new Double[]{intent.getDoubleExtra("lon", 0), intent.getDoubleExtra("lat", 0)});
        report.setLocation(location);
        report.setSeverity(severitySeekBar.getProgress() + 1);
        progressDialog = ProgressDialog.show(ReportActivity.this, getString(R.string.title_progress_report_new),
                getString(R.string.message_progress_all), true);
        ApiCallHelper.newIncident(this, createNewPostReqListener(), createReqErrorListener(), report);
    }

    // creates and returns a listener that handles the response to new post request
    private Response.Listener<JSONObject> createNewPostReqListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressDialog.dismiss();
                try {
                    // TODO: fix the conversion from JSON to class
                    ObjectMapper mapper = new ObjectMapper();
                    Report responseBody = mapper.readValue(response.toString(),
                            Report.class);
                        VolleySingleton.getInstance(ReportActivity.this).setCreateTime();
                        Toast.makeText(ReportActivity.this, R.string.message_success_report_new, Toast.LENGTH_SHORT).show();
                        // TODO: view single report

                        Intent intent = new Intent(ReportActivity.this, MapsActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                } catch (Exception e) {
                    e.printStackTrace();
                    postButton.setEnabled(true);
                    Toast.makeText(ReportActivity.this, R.string.message_error_server, Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    private Response.ErrorListener createReqErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(ReportActivity.this, R.string.message_error_network, Toast.LENGTH_SHORT).show();
                postButton.setEnabled(true);
            }
        };
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // prevent dragging the appbar itself
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        if(ViewCompat.isLaidOut(appBarLayout)) {
            AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
            behavior.setDragCallback(new AppBarLayout.Behavior.DragCallback() {
                @Override
                public boolean canDrag(@NonNull AppBarLayout appBarLayout) {
                    return false;
                }
            });
        }

        mMap = googleMap;
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        mMap.getUiSettings().setZoomGesturesEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location[1], location[0]), 17f));

        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                location = new Double[]{cameraPosition.target.longitude,
                        cameraPosition.target.latitude};
                String address = getAddress(location[1], location[0]);
                if (address.length() > MAX_LOCATION_CHARS)
                    address = address.substring(0, MAX_LOCATION_CHARS - 5) + " ...";
                tvLocation.setText(address);
            }
        });
    }

    private String getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            String add = obj.getAddressLine(0) + ", " + obj.getLocality();
            return add;
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            return "";
        }
    }

    private void setCurrentDateTimeOnView(Calendar c) {
        int hour, minute, day, month, year;
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);
        day = c.get(Calendar.DAY_OF_MONTH);
        month = c.get(Calendar.MONTH);
        year = c.get(Calendar.YEAR);
        // set current time into textview
        tvReportTime.setText(
                new StringBuilder().append(pad(hour > 12 ? hour % 12 : hour))
                        .append(":").append(pad(minute))
                        .append(" ").append(hour > 12 ? "PM" : "AM"));
        tvReportDate.setText(new StringBuilder().append(day).append("-").append(month + 1).append("-").append(year));
    }

    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }

    @Override
    public void onTimeSet(Calendar c) {
        reportCalendar = c;
        setCurrentDateTimeOnView(c);
    }

    @Override
    public void onDateSet(Calendar c) {
        reportCalendar = c;
        setCurrentDateTimeOnView(c);
    }
}

