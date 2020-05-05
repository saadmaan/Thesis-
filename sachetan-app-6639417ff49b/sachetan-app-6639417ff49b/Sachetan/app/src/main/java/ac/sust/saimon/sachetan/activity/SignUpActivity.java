package ac.sust.saimon.sachetan.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONObject;

import ac.sust.saimon.sachetan.R;
import ac.sust.saimon.sachetan.data.model.User;
import ac.sust.saimon.sachetan.network.ApiCallHelper;

public class SignUpActivity extends AppCompatActivity {

    private EditText etFirstName;
    private EditText etLastName;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etPasswordConfirm;
    private Button btnSignUp;
    private TextView tvSignUpMessage;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        etFirstName = (EditText) findViewById(R.id.et_firstname);
        etLastName = (EditText) findViewById(R.id.et_lastname);
        etEmail = (EditText) findViewById(R.id.et_email);
        etPassword = (EditText) findViewById(R.id.et_password);
        etPasswordConfirm = (EditText) findViewById(R.id.et_password_confirm);
        btnSignUp = (Button) findViewById(R.id.btn_signin);
        tvSignUpMessage = (TextView) findViewById(R.id.tv_sign_up_message);
        setSupportActionBar(toolbar);
    }

    public void signUp(View view) {
        String firstName = etFirstName.getText().toString();
        String lastName = etLastName.getText().toString();
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        String passwordConfirm = etPasswordConfirm.getText().toString();
        tvSignUpMessage.setText("");
        if (firstName.replaceAll("\\s+", "").equals("") ||
                lastName.replaceAll("\\s+", "").equals("") ||
                email.replaceAll("\\s+", "").equals("") ||
                password.replaceAll("\\s+", "").equals("") ||
                passwordConfirm.replaceAll("\\s+", "").equals("")) {
            tvSignUpMessage.setText(R.string.prompt_signup_fill_up_fields);
        } else {
            if (!password.equals(passwordConfirm)) {
                Log.e("SignUp", "Mismatch:" + password);
                Log.e("SignUp", "Mismatch:" + passwordConfirm);
                tvSignUpMessage.setText(R.string.prompt_signup_password_mismatch);
            } else {
                tvSignUpMessage.setText("");
                User user = new User();
                user.setFirstName(firstName.replaceAll("\\s+", ""));
                user.setLastName(lastName.replaceAll("\\s+", ""));
                user.setEmail(email.replaceAll("\\s+", ""));
                user.setPassword(password.replaceAll("\\s+", ""));
                progressDialog = ProgressDialog.show(this, getString(R.string.title_progress_signup),
                        "please wait...", true);
                btnSignUp.setEnabled(false);
                ApiCallHelper.signUp(this, createSignUpListener(), createErrorListener(), user);
            }
        }
        resetFields();
    }

    private Response.Listener<JSONObject> createSignUpListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (progressDialog != null)
                    progressDialog.dismiss();
                try {
                    Log.e("Volley Response", response.toString());
                    ObjectMapper mapper = new ObjectMapper();
                    User responseSignUp = mapper.readValue(response.toString(), User.class);
                        // User created successfully
                        Toast.makeText(SignUpActivity.this, R.string.message_success_signup,
                                Toast.LENGTH_SHORT).show();
                        //Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        //startActivity(intent);
                        finish();
                } catch (Exception e) {
                    // could not parse response
                    e.printStackTrace();
                    btnSignUp.setEnabled(true);
                    tvSignUpMessage.setText(R.string.message_error_server);
                }
            }
        };
    }

    private Response.ErrorListener createErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Connection error
                if (progressDialog != null)
                    progressDialog.dismiss();
                Log.e("Volley error", error.getMessage() + "");
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null){
                    switch (networkResponse.statusCode){
                        case 400:
                            tvSignUpMessage.setText(R.string.message_error_credentials);
                            break;
                        case 401:
                            tvSignUpMessage.setText(R.string.message_error_unauthorized);
                            break;
                        default:
                            tvSignUpMessage.setText(R.string.message_error_server);
                    }
                } else{
                    tvSignUpMessage.setText(R.string.message_error_network);
                }
                btnSignUp.setEnabled(true);
            }
        };
    }

    private void resetFields() {
        etFirstName.setText(etFirstName.getText().toString().replaceAll("\\s+", ""));
        etEmail.setText(etEmail.getText().toString().replaceAll("\\s+", ""));
        etPassword.setText("");
        etPasswordConfirm.setText("");
    }
}
