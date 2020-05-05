package ac.sust.saimon.sachetan.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;

import java.util.ArrayList;

import ac.sust.saimon.sachetan.R;
import ac.sust.saimon.sachetan.view.ChartHelper;
import ac.sust.saimon.sachetan.view.CustomHorizontalBarChart;
import ac.sust.saimon.sachetan.data.model.Report;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReportStatisticsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReportStatisticsFragment extends Fragment {
    private static final String ARG_PARAM = "fragment.param.reports";

    private ArrayList<Report> reports;

    CustomHorizontalBarChart chartFreq;
    CustomHorizontalBarChart chartSvrt;
    LineChart chartHourOfDay;
    LinearLayout chartLabels;
    LinearLayout chartContainer;
    PieChart chartIncidentPercentage;
    BarChart chartMonthIncidents;

    public ReportStatisticsFragment() {
    }

    /**
     * @param reports Parameter 1.
     * @return A new instance of fragment ReportStatisticsFragment.
     */
    public static ReportStatisticsFragment newInstance(ArrayList<Report> reports) {
        ReportStatisticsFragment fragment = new ReportStatisticsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM, reports);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            reports = (ArrayList<Report>) getArguments().get(ARG_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_report_statistics, container, false);
        chartFreq = (CustomHorizontalBarChart) rootView.findViewById(R.id.chart_frequency);
        chartSvrt = (CustomHorizontalBarChart) rootView.findViewById(R.id.chart_avgseverity);
        chartLabels = (LinearLayout) rootView.findViewById(R.id.ll_chart_labels);
        chartHourOfDay = (LineChart) rootView.findViewById(R.id.chart_hour_of_day);
        chartIncidentPercentage = (PieChart) rootView.findViewById(R.id.chart_incident_percentage);
        chartMonthIncidents = (BarChart) rootView.findViewById(R.id.chart_month_incidents);
        chartContainer = (LinearLayout) rootView.findViewById(R.id.container_barchart);
        if(reports != null) {
            ChartHelper chartHelper = new ChartHelper(reports, getContext());
            chartHelper.drawFrequencyBar(chartFreq, chartSvrt, chartLabels);
            chartHelper.drawLineChartTime(chartHourOfDay);
            chartHelper.drawIncidentPercentagePie(chartIncidentPercentage);
            //chartHelper.drawMonthIncidentBar(chartMonthIncidents);
        }
        return rootView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
