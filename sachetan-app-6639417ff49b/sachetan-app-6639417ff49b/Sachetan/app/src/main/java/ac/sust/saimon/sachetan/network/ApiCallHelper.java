package ac.sust.saimon.sachetan.network;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ac.sust.saimon.sachetan.R;
import ac.sust.saimon.sachetan.data.model.Report;
import ac.sust.saimon.sachetan.data.model.User;


/**
 * Created by Saimon on 23-May-16.
 */
public class ApiCallHelper {

    /* POST: /oauth/token */
    public static void signIn(Context context,
                              final String username,
                              final String password,
                              Response.Listener<String> responseListener,
                              Response.ErrorListener errorListener) {
        String url = Url.URL_SIGN_IN;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, responseListener, errorListener) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return getBasicAuthorization();
            }

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", username);
                params.put("password", password);
                params.put("grant_type", "password");
                params.put("scope", "read write");
                return params;
            }
        };

        VolleySingleton.getInstance(context).getRequestQueue().add(stringRequest);
    }

    /* POST /api/v1/user */
    public static void signUp(final Context context,
                              Response.Listener<JSONObject> responseListener,
                              Response.ErrorListener errorListener,
                              User user) {
        JSONObject jsonBody = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            jsonBody = new JSONObject(mapper.writeValueAsString(user));
            Log.e("Request Body", jsonBody.toString());
        } catch (JsonProcessingException | JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                Url.URL_SIGN_UP,
                jsonBody,
                responseListener,
                errorListener) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return getBasicAuthorization();
            }
        };
        VolleySingleton.getInstance(context).getRequestQueue().add(request);
    }

    /* GET: /api/v1/incident-type */
    public static void getIncidentTypes(final Context context,
                                        Response.Listener<JSONArray> responseListener,
                                        Response.ErrorListener errorListener) {
        String url = Url.URL_INCIDENT_TYPE;
        JsonArrayRequest request = new JsonArrayRequest(
                url,
                responseListener,
                errorListener) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return getBasicAuthorization();
            }
        };
        VolleySingleton.getInstance(context).getRequestQueue().add(request);
    }

    /* GET: /api/v1/news */
    public static void getNewsData(final Context context,
                                        Response.Listener<JSONArray> responseListener,
                                        Response.ErrorListener errorListener) {
        String url = Url.URL_NEWS_DATA;
        JsonArrayRequest request = new JsonArrayRequest(
                url,
                responseListener,
                errorListener) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return getBasicAuthorization();
            }
        };
        VolleySingleton.getInstance(context).getRequestQueue().add(request);
    }

    /* GET: /api/v1/news */
    public static void getRoute(final Context context,
                                         LatLng toLocation, LatLng fromLocation,
                                   Response.Listener<JSONObject> responseListener,
                                   Response.ErrorListener errorListener) {
        String apiKey = context.getResources().getString(R.string.google_maps_key);
        Log.e("API Key", "" + apiKey);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                Url.URL_GOOGLE_API + "key=" + apiKey
                        +"&origin=" + fromLocation.latitude + "," + fromLocation.longitude
                        + "&destination=" + toLocation.latitude + "," + toLocation.longitude,
                null,
                responseListener,
                errorListener);
        VolleySingleton.getInstance(context).getRequestQueue().add(request);
    }

    /* GET: /api/v1/user/me */
    public static void getSelfUser(final Context context,
                                   Response.Listener<JSONObject> responseListener,
                                   Response.ErrorListener errorListener) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                Url.URL_USER_SELF,
                null,
                responseListener,
                errorListener) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return getBearerAuthorization(context);
            }
        };

        VolleySingleton.getInstance(context).getRequestQueue().add(request);
    }

    /* GET: /api/v1/incident/from-center */
    public static void getReportsWithinBounds(final Context context,
                                              Response.Listener<JSONArray> responseListener,
                                              Response.ErrorListener errorListener,
                                              String incidentTypeId,
                                              LatLngBounds latLngBounds) {
        String url = Url.URL_REPORT;
        if (latLngBounds != null) {
            url += "?boundsFromLon=" + Double.toString(latLngBounds.southwest.longitude)
                    + "&boundsFromLat=" + Double.toString(latLngBounds.southwest.latitude)
                    + "&boundsToLon=" + Double.toString(latLngBounds.northeast.longitude)
                    + "&boundsToLat=" + Double.toString(latLngBounds.northeast.latitude);
        } else {
            url += "?boundsFromLon=" + Double.toString(88.080934)
                    + "&boundsFromLat=" + Double.toString(21.692966)
                    + "&boundsToLon=" + Double.toString(92.900394)
                    + "&boundsToLat=" + Double.toString(26.841603);
        }
        if (incidentTypeId != null && incidentTypeId != "")

            url += "&incidentTypeId=" + incidentTypeId;
        JsonArrayRequest request = new JsonArrayRequest(
                url,
                responseListener,
                errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return getBearerAuthorization(context);
            }
        };

        VolleySingleton.getInstance(context).getRequestQueue().add(request);
    }

    public static void getIncidentsByUser(final Context context,
                                          Response.Listener<JSONArray> responseListener,
                                          Response.ErrorListener errorListener) {
        String url = Url.URL_USER_SELF;
        JsonArrayRequest request = new JsonArrayRequest(
                url,
                responseListener,
                errorListener) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return getBearerAuthorization(context);
            }
        };
        VolleySingleton.getInstance(context).getRequestQueue().add(request);
    }

    /* POST /api/v1/incident */
    public static void newIncident(final Context context,
                                   Response.Listener<JSONObject> responseListener,
                                   Response.ErrorListener errorListener,
                                   Report report) {
        JSONObject jsonBody = null;
        try {
            jsonBody = new JSONObject(new ObjectMapper().writeValueAsString(report));
            Log.e("Volley Request", report + "");
        } catch (JsonProcessingException | JSONException e) {
            e.printStackTrace();
        }
        String url = Url.URL_REPORTS_SELF;
        Log.e("Volley URL", Url.URL_REPORTS_SELF);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                url,
                jsonBody,
                responseListener,
                errorListener) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return getBearerAuthorization(context);
            }
        };
        VolleySingleton.getInstance(context).getRequestQueue().add(request);
    }

    /* POST /api/v1/incident/:incidentId */
    public static void updateIncident(final Context context,
                                      Response.Listener<JSONObject> responseListener,
                                      Response.ErrorListener errorListener,
                                      Report requestBody,
                                      int incidentId) {
        JSONObject jsonBody = null;
        try {
            jsonBody = new JSONObject(new ObjectMapper().writeValueAsString(requestBody));
        } catch (JsonProcessingException | JSONException e) {
            e.printStackTrace();
        }
        String url = Url.URL_REPORTS_SELF + "/" + incidentId;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                url,
                jsonBody,
                responseListener,
                errorListener) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return getBearerAuthorization(context);
            }
        };

        VolleySingleton.getInstance(context).getRequestQueue().add(request);
    }

    /* DELETE api/v1/me/incident/:incidentId */
    public static void deleteIncident(final Context context,
                                      Response.Listener<JSONObject> responseListener,
                                      Response.ErrorListener errorListener,
                                      String incidentId) {
        String url = Url.URL_REPORTS_SELF
                + "/" + incidentId;
        Log.e("VOLLEY URL", url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE,
                url,
                null,
                responseListener,
                errorListener) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return getBearerAuthorization(context);
            }
        };
        VolleySingleton.getInstance(context).getRequestQueue().add(request);
    }

    private static HashMap<String, String> getBasicAuthorization() {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put(Url.HEADER_KEY_AUTH, Url.HEADER_VALUE_AUTH);
        return headers;
    }

    private static HashMap<String, String> getBearerAuthorization(Context context) {
        String accessToken = VolleySingleton.getInstance(context).getToken();
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put(Url.HEADER_KEY_AUTH, "Bearer " + accessToken);
        return headers;
    }
}
