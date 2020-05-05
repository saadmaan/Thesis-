package ac.sust.saimon.sachetan.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.fasterxml.jackson.databind.ObjectMapper;

import ac.sust.saimon.sachetan.R;
import ac.sust.saimon.sachetan.data.api.ResponseSignIn;
import ac.sust.saimon.sachetan.network.ApiCallHelper;
import ac.sust.saimon.sachetan.network.VolleySingleton;

public class SignInActivity extends AppCompatActivity {

    private EditText etEmail;
    private EditText etPassword;
    private Button btnSignIn;
    private TextView tvSignInMessage;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        etEmail = (EditText) findViewById(R.id.et_email);
        etPassword = (EditText) findViewById(R.id.et_password);
        btnSignIn = (Button) findViewById(R.id.btn_signin);
        tvSignInMessage = (TextView) findViewById(R.id.tv_sign_in_message);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public void signIn(View view){
        btnSignIn.setEnabled(false);
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        tvSignInMessage.setText("");
        if(email.trim() != "" && password.trim()!= ""){
            progressDialog = ProgressDialog.show(this, getString(R.string.title_progress_signin),
                    getString(R.string.message_progress_all), true);
            ApiCallHelper.signIn(this, email, password,
                    createSignInListener(), createErrorListener());
        }else{
            tvSignInMessage.setText(R.string.prompt_signup_fill_up_fields);
        }
    }

    public void launchSignUpActivity(View view){
        Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
        startActivity(intent);
    }

    private Response.Listener<String> createSignInListener(){
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(progressDialog != null)
                    progressDialog.dismiss();
                try {
                    Log.e("Volley Response", response.toString());
                    ObjectMapper mapper = new ObjectMapper();
                    ResponseSignIn responseSignIn = mapper.readValue(response.toString(), ResponseSignIn.class);
                    if(responseSignIn.getError() == null){
                        // Server issued token
                        Log.e("AUTH", "Token: " + responseSignIn.getAccess_token());
                        VolleySingleton.getInstance(getApplicationContext()).setToken(responseSignIn.getAccess_token());
                        Intent intent = new Intent(SignInActivity.this, MapsActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
//                    else{
//                        // Server rejected credentials
//                        btnSignIn.setEnabled(true);
//                        tvSignInMessage.setText(responseSignIn.getMessage());
//                    }
                } catch (Exception e) {
                    // could not parse response
                    e.printStackTrace();
                    btnSignIn.setEnabled(true);
                    tvSignInMessage.setText(R.string.message_error_server);
                }
            }
        };
    }

    private Response.ErrorListener createErrorListener(){
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Connection error
                if(progressDialog != null)
                    progressDialog.dismiss();
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null){
                    switch (networkResponse.statusCode){
                        case 400:
                            tvSignInMessage.setText(R.string.message_error_credentials);
                            break;
                        case 401:
                            tvSignInMessage.setText(R.string.message_error_unauthorized);
                            break;
                        default:
                            tvSignInMessage.setText(R.string.message_error_server);
                    }
                } else{
                    tvSignInMessage.setText(R.string.message_error_network);
                }
                btnSignIn.setEnabled(true);
                Log.e("Volley error", error.getMessage() + "");

            }
        };
    }

}
