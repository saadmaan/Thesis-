package ac.sust.saimon.sachetan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import ac.sust.saimon.sachetan.R;
import ac.sust.saimon.sachetan.application.Sachetan;

public class UrlActivity extends AppCompatActivity {

    private Button button;
    private EditText ip1, ip2, ip3, ip4, port;
    private TextView errorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_url);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        button = (Button) findViewById(R.id.save_ip);
        ip1 = (EditText) findViewById(R.id.ip1);
        ip2 = (EditText) findViewById(R.id.ip2);
        ip3 = (EditText) findViewById(R.id.ip3);
        ip4 = (EditText) findViewById(R.id.ip4);
        port = (EditText) findViewById(R.id.port);
        errorMessage = (TextView) findViewById(R.id.errormessage);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = getURL();
                if (url != null) {
                    errorMessage.setText(" ");
                    ((Sachetan) getApplication()).setBaseURL(url);
                    Intent intent = new Intent(UrlActivity.this, SplashActivity.class);
                    startActivity(intent);
                    finish();
                } else
                    errorMessage.setText("Invalid Port or IP address!");
            }
        });
    }

    public String getURL() {
        int ipBlock1, ipBlock2, ipBlock3, ipBlock4, portNumber;
        try {
            ipBlock1 = Integer.parseInt(ip1.getText().toString());
            ipBlock2 = Integer.parseInt(ip2.getText().toString());
            ipBlock3 = Integer.parseInt(ip3.getText().toString());
            ipBlock4 = Integer.parseInt(ip4.getText().toString());
            portNumber = Integer.parseInt(port.getText().toString());
        } catch (NumberFormatException ne) {
            return null;
        }
        if (ipBlock1 < 256 &&
                ipBlock2 < 256 &&
                ipBlock3 < 256 &&
                ipBlock4 < 256 &&
                portNumber > 0 &&
                portNumber < 49151) {
            String ipString = ip1.getText().toString() + "."
                    + ip2.getText().toString() + "."
                    + ip3.getText().toString() + "."
                    + ip4.getText().toString();
            String portString = port.getText().toString();
            return "http://" + ipString + ":" + portString;
        }
        return null;
    }
}
