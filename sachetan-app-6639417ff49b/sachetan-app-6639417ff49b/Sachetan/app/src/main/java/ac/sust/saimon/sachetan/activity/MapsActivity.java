package ac.sust.saimon.sachetan.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.WeightedLatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ac.sust.saimon.sachetan.R;
import ac.sust.saimon.sachetan.adapter.IncidentTypeAdapter;
import ac.sust.saimon.sachetan.data.map.Road;
import ac.sust.saimon.sachetan.data.model.IncidentType;
import ac.sust.saimon.sachetan.data.model.NewsActivity;
import ac.sust.saimon.sachetan.data.model.Report;
import ac.sust.saimon.sachetan.network.ApiCallHelper;
import ac.sust.saimon.sachetan.network.VolleySingleton;

public class MapsActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private HeatmapTileProvider mProvider;
    private TileOverlay mOverlay;
    private Spinner spinnerIncidentType;
    private FloatingActionButton btnNewReport;
    private LatLng userLocation = null;
    private LatLng focusedLocation;
    private LatLng sourceLocation;
    private List<WeightedLatLng> coordinateList;
    private List<LatLng> dangerLoctions;
    private ImageButton btnUserPosts;
    private ImageButton btnViewAsList;
    private ImageButton btnClearPath;
    private ImageButton mapCenter;
    private CheckBox chkbxNews;
    private CheckBox chkbxPosts;
    private Marker pathDestination;
    private Marker pathSource;
    private List<LatLng> roadDrawDirections;

    private static final int PERMISSIONS_REQUEST_LOCATION = 5566;
    private final double MIN_DISTANCE = 2000.0;

    private final float MAP_PIN_ZOOM = 12f;
    private final LatLng sylhet = new LatLng(24.9000, 91.8667);
    private LatLngBounds bounds;
    private ArrayList<Road> roads;
    private ArrayList<Report> reports;
    private ArrayList<NewsActivity> newsIncidents;
    private ArrayList<Polyline> pathPolyLines;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        btnNewReport = (FloatingActionButton) findViewById(R.id.btn_newreport);
        btnUserPosts = (ImageButton) findViewById(R.id.imgbtn_userposts);
        btnViewAsList = (ImageButton) findViewById(R.id.imgbtn_list_posts);
        btnClearPath = (ImageButton) findViewById(R.id.imgbtn_clear_path);
        mapCenter = (ImageButton) findViewById(R.id.imgbtn_map_center);
        chkbxNews = (CheckBox) findViewById(R.id.chkbx_news);
        chkbxPosts = (CheckBox) findViewById(R.id.chkbx_posts);
        spinnerIncidentType = (Spinner) findViewById(R.id.spinner_incident_type);
        setSupportActionBar(toolbar);
        btnClearPath.setVisibility(View.GONE);
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        btnUserPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MapsActivity.this, UserPostsActivity.class));
            }
        });
        btnViewAsList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapsActivity.this, AreaReportsActivity.class);
                Bundle args = new Bundle();
                args.putParcelable("bounds", bounds);
                intent.putExtra("bundle", args);
                intent.putExtra("itype",
                        ((IncidentType) spinnerIncidentType.getSelectedItem()).getId());
                startActivity(intent);
            }
        });

        View.OnClickListener heatMapTypeListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view.getId() == R.id.chkbx_news &&
                        !chkbxPosts.isChecked() &&
                        !chkbxPosts.isChecked()){
                    chkbxPosts.setChecked(true);
                } else if((view.getId() == R.id.chkbx_posts &&
                        !chkbxPosts.isChecked() &&
                        !chkbxPosts.isChecked())){
                    chkbxNews.setChecked(true);
                }
                drawHeatMap();
            }
        };
        chkbxPosts.setOnClickListener(heatMapTypeListener);
        chkbxNews.setOnClickListener(heatMapTypeListener);

        btnClearPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeRoute();
            }
        });
        ArrayList<IncidentType> incidentTypes = new ArrayList<>(VolleySingleton.getInstance(this).getIncidentTypes());
        incidentTypes.add(0, new IncidentType("All", "No description"));
        spinnerIncidentType.setAdapter(new IncidentTypeAdapter(this, incidentTypes));
        spinnerIncidentType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.e("SPINNER", "Selected: "
                        + ((IncidentType) spinnerIncidentType.getItemAtPosition(position)).getName());
                loadReports();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        loadReports();
    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }
        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }
        if (!gps_enabled && !network_enabled) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(MapsActivity.this);
            dialog.setMessage("Location service is currently disabled. Please enable location first and try again");
            dialog.setPositiveButton("Open Settings", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                    //get gps
                }
            });
            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // do nothing
                }
            });
            dialog.show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sylhet, 7f));
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat
                .checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsActivity.this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_LOCATION);
            // return;
        } else {
            mMap.setMyLocationEnabled(true);
        }
        mapCenter.setVisibility(View.VISIBLE);
        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location arg0) {
                if (userLocation == null) {
                    mMap.moveCamera(CameraUpdateFactory
                            .newLatLngZoom(new LatLng(arg0.getLatitude(), arg0.getLongitude()),
                                    MAP_PIN_ZOOM));
                    focusedLocation = new LatLng(arg0.getLatitude(), arg0.getLongitude());
                    btnNewReport.setVisibility(View.VISIBLE);
                }
                userLocation = new LatLng(arg0.getLatitude(), arg0.getLongitude());
            }
        });

        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                bounds = mMap.getProjection().getVisibleRegion().latLngBounds;
                focusedLocation = new LatLng(cameraPosition.target.latitude,
                        cameraPosition.target.longitude);
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng toLocation) {
                if (userLocation == null)
                    Log.e("Map Click", "User location is null");
                if (userLocation != null)
                    findRoute(toLocation);
            }
        });

        btnNewReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (focusedLocation == null){
                    Toast.makeText(MapsActivity.this, "Unable to load map", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(MapsActivity.this, ReportActivity.class);
                intent.putExtra("lat", focusedLocation.latitude);
                intent.putExtra("lon", focusedLocation.longitude);
                Log.e("Location", "Locking on "
                        + focusedLocation.latitude
                        + ", "
                        + focusedLocation.longitude);
                startActivity(intent);
            }
        });
    }

    private void removeRoute(){
        //if (pathSource != null)
        //    pathSource.remove();
        if (pathDestination != null)
            pathDestination.remove();
        if (pathPolyLines != null)
            for (Polyline p : pathPolyLines)
                p.remove();
        // sourceLocation = null;
        mapCenter.setVisibility(View.VISIBLE);
        btnClearPath.setVisibility(View.GONE);
    }

    private void findRoute(LatLng toLocation) {
        mapCenter.setVisibility(View.INVISIBLE);
        //if (pathSource != null)
        //    pathSource.remove();
        if (pathDestination != null)
            pathDestination.remove();
        if (pathPolyLines != null)
            for (Polyline p : pathPolyLines)
                p.remove();
        // put the destination marker on the map
        // if (sourceLocation == null)
        //    sourceLocation = new LatLng(focusedLocation.latitude, focusedLocation.longitude);
        pathDestination = mMap.addMarker(new MarkerOptions().position(toLocation));
        // pathSource = mMap.addMarker(new MarkerOptions().position(sourceLocation));
        // mapCenter.setVisibility(View.INVISIBLE);
        btnClearPath.setVisibility(View.VISIBLE);

        // ask google nicely for a path from the focused location to the placed marker location
        // update: changed param 3 from sourceLocation to userLocation based on Nabil sir's specification
        ApiCallHelper.getRoute(this, toLocation, userLocation,
                createRouteGetListener(), createRouteErrorListener());
    }

    private void loadReports() {
        ApiCallHelper.getReportsWithinBounds(MapsActivity.this,
                createReportGetListener(),
                createPostReqErrorListener(),
                ((IncidentType) spinnerIncidentType.getSelectedItem()).getId(),
                null);
        ApiCallHelper.getNewsData(MapsActivity.this,
                createNewsGetListener(),
                createNewsReqErrorListener());
    }

    private void drawHeatMap() {
        coordinateList = new ArrayList<>();
        dangerLoctions = new ArrayList<>();
        chkbxNews.setEnabled(false);
        chkbxPosts.setEnabled(false);
        if (reports != null && !reports.isEmpty()) {
            if (chkbxPosts.isChecked()) {
                spinnerIncidentType.setEnabled(true);
                for (Report report : reports) {
                    coordinateList.add(report.getAsWeightedLagLng());
                    dangerLoctions.add(new LatLng(report.getLocation()[1], report.getLocation()[0]));
                    //Log.i("bbdfdfdf",""+report.getLocation()[1]+" "+report.getLocation()[0]);
                }
            } else {
                spinnerIncidentType.setEnabled(false);
            }
        }
        if (newsIncidents != null && !newsIncidents.isEmpty()){
            if (chkbxNews.isChecked()) {
                for (NewsActivity newsActivity : newsIncidents) {
                    coordinateList.add(newsActivity.getAsWeightedLatLng());
                    dangerLoctions.add(new LatLng(newsActivity.getLat(),newsActivity.getLon()));
                    //Log.i("bbdfdfdf",""+newsActivity.getLat()+" "+newsActivity.getLon());
                }
            }
        }
        if (!coordinateList.isEmpty()) {
            if (mProvider == null) {
                mProvider = new HeatmapTileProvider.Builder().weightedData(coordinateList).build();
                mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
            } else {
                mProvider.setWeightedData(coordinateList);
                mOverlay.clearTileCache();
            }
        }
        //for(int i=0;i<coordinateList.size();i++) {
         //   Log.i("nnnnnn",coordinateList.get(i).getPoint().x+"  "+coordinateList.get(i).getPoint().x);
        //}
        chkbxNews.setEnabled(true);
        chkbxPosts.setEnabled(true);
    }

    // code provided by Nebir
    private void clearAndDrawPoly() {
        //mMap.clear();
        //drawHeatMap();
        if(pathPolyLines!=null) {
            for(int i=0;i<pathPolyLines.size();i++) {
                pathPolyLines.get(i).remove();
            }
        }
        pathPolyLines = new ArrayList<>();
        PolylineOptions rectOptions = new PolylineOptions();
        for(int i=0;i<roadDrawDirections.size();i++) {
            LatLng point = roadDrawDirections.get(i);
            if(i == roadDrawDirections.size()-1) {
                //mMap.addMarker(new MarkerOptions().position(point).title("Destination").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                // mMap.moveCamera(CameraUpdateFactory.newLatLng(point));
                // mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
            } else {
                for(int j=0;j<dangerLoctions.size();j++) {
                    LatLng dangerLocation = dangerLoctions.get(j);
                    if(distance(dangerLocation.latitude,dangerLocation.longitude,point.latitude,point.longitude)<100) {
                        rectOptions.add(point).color(Color.BLUE).geodesic(true);
                        pathPolyLines.add(mMap.addPolyline(rectOptions));
                        // mMap.addMarker(new MarkerOptions().position(new LatLng(dangerLocation.getLat(),dangerLocation.getLng())).title("Danger Zone").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                        rectOptions = new PolylineOptions();
                        while(distance(dangerLocation.latitude,dangerLocation.longitude,point.latitude,point.longitude)<100) {
                            rectOptions.add(point).color(Color.RED).geodesic(true);
                            if(i<roadDrawDirections.size()-1) {
                                i++;
                                point = roadDrawDirections.get(i);
                                rectOptions.add(point).color(Color.RED).geodesic(true);
                            } else {
                                //mMap.addMarker(new MarkerOptions().position(point).title("Destination").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                                // mMap.moveCamera(CameraUpdateFactory.newLatLng(point));
                                // mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
                                break;
                            }
                        }
                        pathPolyLines.add(mMap.addPolyline(rectOptions));
                        rectOptions = new PolylineOptions();
                        rectOptions.add(point).color(Color.BLUE).geodesic(true);
                        break;
                    } else {
                        rectOptions.add(point).color(Color.BLUE).geodesic(true);
                    }
                }
            }
        }
        pathPolyLines.add(mMap.addPolyline(rectOptions));
    }


    // code provided by Nebir
    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;
        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;
            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;
            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }
        return poly;
    }

    // code provided by Nebir
    // Get distance between two geolocations
    public static float distance(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        float dist = (float) (earthRadius * c);
        if (dist < 0) {
            dist *= 1;
        }
        return dist;
    }


    private Double toRadians(Double degree){
        // Value degree * Pi/180
        Double res = degree * 3.1415926 / 180;
        return res;
    }


    private Response.Listener<JSONArray> createNewsGetListener() {
        return new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    Log.e("Volley Response:", response.toString());
                    ObjectMapper mapper = new ObjectMapper();
                    newsIncidents = mapper.readValue(response.toString(),
                            new TypeReference<ArrayList<NewsActivity>>() {
                            });
                    //Log.e("SIZE", "News: " + newsIncidents.size());
                    drawHeatMap();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(MapsActivity.this, R.string.message_error_server,
                            Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    private Response.Listener<JSONArray> createReportGetListener() {
        return new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    Log.e("Volley Response:", response.toString());
                    ObjectMapper mapper = new ObjectMapper();
                    reports = mapper.readValue(response.toString(), new TypeReference<ArrayList<Report>>() {
                    });
                    //Log.e("SIZE", "Reports: " + reports.size());
                    if (userLocation != null) {
                        mMap.moveCamera(CameraUpdateFactory
                                .newLatLngZoom(userLocation, MAP_PIN_ZOOM));
                    }
                    drawHeatMap();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(MapsActivity.this, R.string.message_error_server,
                            Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    private Response.Listener<JSONObject> createRouteGetListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray routes = response.getJSONArray("routes");
                    if (routes.length() == 0) {
                        // no route found? no idea.
                    } else {
                        JSONArray steps = routes.getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONArray("steps");
                        ObjectMapper mapper = new ObjectMapper();
                        roads = mapper.readValue(steps.toString(), new TypeReference<ArrayList<Road>>() {
                        });

                        JSONObject overviewPolylines = routes.getJSONObject(0).getJSONObject("overview_polyline");
                        Log.e("Route", "" + overviewPolylines.toString());
                        String encodedString = overviewPolylines.getString("points");
                        Log.e("Encoded Route", encodedString);
                        roadDrawDirections = decodePoly(encodedString);
                        clearAndDrawPoly();
                    }
                } catch (JsonParseException e) {
                    e.printStackTrace();
                } catch (JsonMappingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private Response.ErrorListener createNewsReqErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley Error", "" + error.getMessage());
                Toast.makeText(MapsActivity.this, R.string.message_error_network,
                        // + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
                chkbxNews.setEnabled(true);
                chkbxNews.setChecked(false);
            }
        };
    }

    private Response.ErrorListener createPostReqErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley Error", "" + error.getMessage());
                Toast.makeText(MapsActivity.this, R.string.message_error_network,
                        // + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
                chkbxPosts.setEnabled(true);
                chkbxPosts.setChecked(false);
            }
        };
    }

    private Response.ErrorListener createRouteErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Route Error", "" + error.getMessage());
                // TODO: handle request error
            }
        };
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.e("METHOD CALL", "onConnected");

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void unsupportedButton(View view) {
        Toast.makeText(MapsActivity.this, R.string.message_error_unsupported,
                Toast.LENGTH_SHORT).show();
    }

}
