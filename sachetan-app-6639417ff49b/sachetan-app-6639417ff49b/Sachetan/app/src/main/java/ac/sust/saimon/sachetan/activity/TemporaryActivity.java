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
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.android.volley.VolleyError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import ac.sust.saimon.sachetan.R;
import ac.sust.saimon.sachetan.data.model.NewsActivity;
public class TemporaryActivity extends AppCompatActivity
        implements OnMapReadyCallback,
        //HttpListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final int REQUEST_DIRECTION_PATH = 4434;
    private static final int PERMISSIONS_REQUEST_LOCATION = 5566;
    private GoogleMap mMap;
    private String destinationName;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    //private ArrayList<Road> roads;
    private List<LatLng> roadDrawDirections;
    private ArrayList<NewsActivity> dangerLocations;
    private int distance = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        //Realm realm = Realm.getInstance(this);
        dangerLocations = null; // TODO: fetch from server/app cached data
        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        destinationName = getIntent().getExtras().getString("searched_text");
        String distanceStr = getIntent().getExtras().getString("dist");
        try
        {
            distance = Integer.parseInt(distanceStr);
        }catch (Exception e)
        {
            //parse error for empty str
        }


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.cast_ic_notification_rewind);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //
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
            AlertDialog.Builder dialog = new AlertDialog.Builder(TemporaryActivity.this);
            dialog.setMessage("Location service is currently disabled. Please enable location first and try again");
            dialog.setPositiveButton("Open Settings", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                    //get gps
                }
            });
            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub

                }
            });
            dialog.show();
        }


    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (ActivityCompat
                        .checkSelfPermission(TemporaryActivity.this,
                                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat
                        .checkSelfPermission(TemporaryActivity.this,
                                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                        mGoogleApiClient);
                if (mLastLocation != null) {
                    //String apiKey = getResources().getString(R.string.google_maps_direction_key);
                    String origin = mLastLocation.getLatitude() + "," + mLastLocation.getLongitude();
                    String destinationName= latLng.latitude+","+latLng.longitude;
                    //HttpRequest request = new HttpRequest("https://maps.googleapis.com/maps/api/directions/json?key=" + apiKey + "&destination=" + destinationName + "&origin=" + origin, REQUEST_DIRECTION_PATH, MapsActivity.this);
                    //request.execute("get");
                }
            }
        });

    }

    private void clearAndDrawPoly() {
        mMap.clear();
        PolylineOptions rectOptions = new PolylineOptions();
        for(int i=0;i<roadDrawDirections.size();i++)
        {

            LatLng point = roadDrawDirections.get(i);
            if(i == roadDrawDirections.size()-1)
            {
                mMap.addMarker(new MarkerOptions().position(point).title("Destination").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(point));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);

            }
            else
            {
                for(int j=0;j<dangerLocations.size();j++)
                {
                    NewsActivity dangerLocation = dangerLocations.get(j);
                    if(distance(dangerLocation.getLat(),dangerLocation.getLon(),point.latitude,point.longitude)<distance)
                    {
                        rectOptions
                                .add(point).color(Color.BLUE).geodesic(true);
                        mMap.addPolyline(rectOptions);
                        // mMap.addMarker(new MarkerOptions().position(new LatLng(dangerLocation.getLat(),dangerLocation.getLng())).title("Danger Zone").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                        rectOptions = new PolylineOptions();
                        while(distance(dangerLocation.getLat(),dangerLocation.getLon(),point.latitude,point.longitude)<distance)
                        {

                            rectOptions
                                    .add(point).color(Color.RED).geodesic(true);
                            if(i<roadDrawDirections.size()-1) {
                                i++;
                                point = roadDrawDirections.get(i);
                                rectOptions
                                        .add(point).color(Color.RED).geodesic(true);
                            }
                            else
                            {
                                mMap.addMarker(new MarkerOptions().position(point).title("Destination").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(point));
                                mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
                                break;
                            }




                        }
                        mMap.addPolyline(rectOptions);

                        rectOptions = new PolylineOptions();
                        rectOptions
                                .add(point).color(Color.BLUE).geodesic(true);


                        break;
                    }
                    else
                    {
                        rectOptions
                                .add(point).color(Color.BLUE).geodesic(true);
                    }

                }
            }

        }
        mMap.addPolyline(rectOptions);



    }
    public static float distance(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        float dist = (float) (earthRadius * c);
        if(dist<0)
        {
            dist*=1;
        }
        return dist;
    }

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

            LatLng p = new LatLng( (((double) lat / 1E5)),
                    (((double) lng / 1E5) ));
            poly.add(p);
        }

        return poly;
    }


    // @Override
    public void respond(String jsonRespond, int respondId
                        //, HttpRequest parent
    ) {
        try {
            JSONObject response = new JSONObject(jsonRespond);
            JSONArray routes = response.getJSONArray("routes");
            if(routes.length()==0)
            {

            }
            else
            {
                JSONArray steps= routes.getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONArray("steps");
                //Gson gson = new Gson();
                // Type listType = new TypeToken<ArrayList<Road>>() {}.getType();
                //roads = gson.fromJson(steps.toString(),listType);
                JSONObject overviewPolylines = routes.getJSONObject(0).getJSONObject("overview_polyline");
                String encodedString = overviewPolylines.getString("points");
                roadDrawDirections = decodePoly(encodedString);
                clearAndDrawPoly();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // @Override
    public void errorRespond(VolleyError e, int respondId
            //, HttpRequest parent
    ) {

    }

    //@Override
    public Context getContext() {
        return TemporaryActivity.this;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(TemporaryActivity.this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_LOCATION);
        } else {
            mMap.setMyLocationEnabled(true);
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation != null) {
                // String apiKey = getResources().getString(R.string.google_maps_direction_key);
                String origin = mLastLocation.getLatitude() + "," + mLastLocation.getLongitude();
                // HttpRequest request = new HttpRequest("https://maps.googleapis.com/maps/api/directions/json?key=" + apiKey + "&destination=" + destinationName + "&origin=" + origin, REQUEST_DIRECTION_PATH, MapsActivity.this);
                // request.execute("get");
            }
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    mMap.setMyLocationEnabled(true);
                    mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                            mGoogleApiClient);
                    if (mLastLocation != null) {
                        // String apiKey = getResources().getString(R.string.google_maps_direction_key);
                        String origin = mLastLocation.getLatitude()+","+mLastLocation.getLongitude();
                        // HttpRequest request = new HttpRequest("https://maps.googleapis.com/maps/api/directions/json?key=" + apiKey + "&destination=" + destinationName + "&origin="+origin, REQUEST_DIRECTION_PATH, MapsActivity.this);
                        // request.execute("get");
                    }


                } else {


                }
                return;
            }


        }
    }
}
