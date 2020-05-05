package ac.sust.saimon.sachetan.activity;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.LinearLayout;
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
import ac.sust.saimon.sachetan.fragment.ReportListFragment;
import ac.sust.saimon.sachetan.fragment.ReportStatisticsFragment;
import ac.sust.saimon.sachetan.network.ApiCallHelper;

public class AreaReportsActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private LinearLayout llStatus;
    private TextView tvStatus;
    private TabLayout tabLayout;

    private LatLngBounds bounds;
    private String incidentTypeId;
    private ArrayList<Report> reports;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area_reports);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Specify that tabs should be displayed in the action bar.

        mViewPager = (ViewPager) findViewById(R.id.container);
        tabLayout = (TabLayout) findViewById(R.id.tab_header);
        llStatus = (LinearLayout) findViewById(R.id.layout_status);
        tvStatus = (TextView) findViewById(R.id.textview_status);
        Intent intent = getIntent();
        incidentTypeId = intent.getStringExtra("itype");
        Bundle bundle = intent.getParcelableExtra("bundle");
        bounds = bundle.getParcelable("bounds");
        ApiCallHelper.getReportsWithinBounds(this,
                createReportsFromBoundsListener(), createErrorListener(), incidentTypeId, bounds);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_area_reports, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch(position){
                case 0:
                    return ReportStatisticsFragment.newInstance(reports);
                case 1:
                    return ReportListFragment.newInstance(reports);
                default:
                    return null;
            }
        }
        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Statistics";
                case 1:
                    return "Reports";
            }
            return null;
        }
    }

    private Response.Listener<JSONArray> createReportsFromBoundsListener() {
        return new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    reports = mapper.readValue(response.toString(),
                            new TypeReference<ArrayList<Report>>() {});
                    if(reports.isEmpty()){
                        tvStatus.setText(R.string.message_response_empty);
                        return;
                    }
                    // Create the adapter that will return a fragment for each of the three
                    // primary sections of the activity.
                    mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
                    // Set up the ViewPager with the sections adapter.
                    mViewPager.setAdapter(mSectionsPagerAdapter);
                    tabLayout.setupWithViewPager(mViewPager);
                    llStatus.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(AreaReportsActivity.this, R.string.message_error_server,
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
