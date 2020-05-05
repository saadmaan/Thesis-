package ac.sust.saimon.sachetan.network;

/**
 * Created by Saimon on 07-Apr-16.
 */
public class Url {
    //public static final String URL_ROOT = "http://10.100.94.39:9000";
    //public static final String URL_ROOT = "http://10.100.94.39:8081/sachetan-api-0.0.1";
    //public static final String URL_ROOT = "http://54.86.7.245:8080/sachetan-api-0.0.1";
    public static final String URL_ROOT = "http://34.215.17.187:8080/sachetan-api-0.1.0"; // AWS EC2 (Saimon) URL
    public static final String URL_SIGN_IN = URL_ROOT + "/oauth/token";
    public static final String URL_SIGN_UP = URL_ROOT + "/api/v1/user";
    public static final String URL_INCIDENT_TYPE = URL_ROOT + "/api/v1/incident-type";
    public static final String URL_NEWS_DATA = URL_ROOT + "/api/v1/news";
    public static final String URL_USER_SELF = URL_ROOT + "/api/v1/user/me";
    public static final String URL_REPORTS_SELF = URL_ROOT + "/api/v1/user/me/report";
    public static final String URL_REPORT = URL_ROOT + "/api/v1/report";
    public static final String URL_GOOGLE_API = "https://maps.googleapis.com/maps/api/directions/json?";
    public static final String HEADER_KEY_AUTH = "Authorization";
    public static final String HEADER_VALUE_AUTH = "Basic UkVTVF9BUElfQ09OU1VNRVI6c2FjaGV0YW4=";

    // TODO: fixme
    public static String CLIENT_ID ="todo_client_id";
    public static String CLIENT_SECRET ="todo_client_secret";
}
