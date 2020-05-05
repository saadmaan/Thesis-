package ac.sust.saimon.sachetan.view;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import ac.sust.saimon.sachetan.R;
import ac.sust.saimon.sachetan.data.model.Report;
import ac.sust.saimon.sachetan.fragment.ReportStatisticsFragment;

/**
 * Created by Saimon on 14-Sep-17.
 */

public class ChartHelper {

    private Context context;
    private List<Report> reports;
    private Map<String, Stat> freq;

    public ChartHelper(List<Report> reports, Context context){
        this.reports = reports;
        this.context = context;
        freq = getReportCounts(this.reports);
    }

    public Map<String, Stat> getReportCounts(List<Report> reports){
        Map<String, Stat> freq = new HashMap<String, Stat>();
        for (Report r : reports){
            String incidentName = r.getIncidentType().getName();
            Stat count = freq.get(incidentName);
            if (count == null) {
                Stat stat = new Stat();
                stat.addSeverity(r.getSeverity());
                stat.increaseCount();
                freq.put(incidentName, stat);
            }
            else {
                count.increaseCount();
                count.addSeverity(r.getSeverity());
            }
        }
        return freq;
    }

    /**
     * Draw a frequency bar chart from given data and view params
     * @param chartFreq view
     * @param chartSvrt view
     * @param chartLabels view
     */
    public void drawFrequencyBar(CustomHorizontalBarChart chartFreq,
                                        CustomHorizontalBarChart chartSvrt,
                                        LinearLayout chartLabels){
        List<BarEntry> entriesFreq = new ArrayList<>();
        List<BarEntry> entriesSeverity = new ArrayList<>();
        int temp = 0, maxfreq = 0;
        for (Map.Entry<String, Stat> mapEntry : freq.entrySet()){
            entriesFreq.add(new BarEntry(temp, mapEntry.getValue().getCount()));
            entriesSeverity.add(new BarEntry(temp, (int) mapEntry.getValue().getAvgSeverity()));
            maxfreq = mapEntry.getValue().getCount() > maxfreq ? mapEntry.getValue().getCount() : maxfreq;
            temp += 2;
            TextView tv = new TextView(context);
            tv.setLayoutParams(new TableRow
                    .LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
            tv.setGravity(Gravity.RIGHT | Gravity.CENTER);
            tv.setText(mapEntry.getKey());
            chartLabels.addView(tv);
        }
        final String[] xlabels = freq.keySet().toArray(new String[freq.size()]);
        BarDataSet freqDataSet = new BarDataSet(entriesFreq, "Frequency");
        BarDataSet severityDataSet = new BarDataSet(entriesSeverity, "Avg Severity");
        freqDataSet.setColor(context.getResources().getColor(R.color.colorPrimaryDark));
        severityDataSet.setColor(context.getResources().getColor(R.color.black));
        BarData freqData = new BarData(freqDataSet);
        BarData severityData = new BarData(severityDataSet);
        freqData.setBarWidth(1f);
        severityData.setBarWidth(1f);
        chartFreq.setXAxisRange(-0.5f, xlabels.length*2 - 1.5f, 2f);
        chartSvrt.setXAxisRange(-0.5f, xlabels.length*2 - 1.5f, 2f);
        chartFreq.setLeftAxisRange(0, (float) (maxfreq * 1.25));
        chartSvrt.setLeftAxisRange(0, 5, 1);
        chartFreq.setData(freqData);
        chartSvrt.setData(severityData);
        chartFreq.animateY(500);
        chartSvrt.animateY(500);
    }

    public void drawLineChartTime(LineChart lineChart){
        Map<String, Stat> hourFreq = new HashMap<String, Stat>(){};
        for(int i = 1; i <= 24; i++){
            hourFreq.put("" + i, new Stat());
        }
        for(Report r: reports){
            Calendar cal = Calendar.getInstance();
            cal.setTime(r.getIncidentDate());
            int hours = cal.get(Calendar.HOUR_OF_DAY);
            Stat count = hourFreq.get("" + hours);
            count.increaseCount();
        }
        ArrayList<Entry> values = new ArrayList<Entry>();
        for(int i = 1; i < 24; i++){
            values.add(new Entry((float) i,
                    (float) hourFreq.get(i + "").getCount()));
        }
        LineDataSet set1 = new LineDataSet(values, "Incident frequency during hour of the day");
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
        set1.setColor(ColorTemplate.getHoloBlue());
        set1.setValueTextColor(ColorTemplate.getHoloBlue());
        set1.setLineWidth(1.5f);
        set1.setDrawCircles(false);
        set1.setDrawValues(false);
        set1.setFillAlpha(65);
        set1.setFillColor(ColorTemplate.getHoloBlue());
        set1.setHighLightColor(Color.rgb(244, 117, 117));
        set1.setDrawCircleHole(false);
        set1.setValueFormatter(new PercentFormatter());

        // create a data object with the datasets
        LineData data = new LineData(set1);
        data.setValueTextColor(Color.WHITE);
        data.setValueTextSize(9f);
        // set data
        lineChart.setData(data);
        lineChart.getXAxis().setValueFormatter(new TimeFormatter());
        lineChart.setClickable(true);
        lineChart.setPinchZoom(true);
        lineChart.setScaleEnabled(true);
        lineChart.setDoubleTapToZoomEnabled(false);
        lineChart.getDescription().setEnabled(false);
        lineChart.animateY(500);
    }

    public void drawIncidentPercentagePie(PieChart pieChart){
        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();
        for (Map.Entry<String, Stat> mapEntry : freq.entrySet()){
            entries.add(new PieEntry(mapEntry.getValue().getCount(), mapEntry.getKey()));
        }
        PieDataSet ds1 = new PieDataSet(entries, "");
        ds1.setColors(ColorTemplate.VORDIPLOM_COLORS);
        ds1.setSliceSpace(2f);
        ds1.setValueFormatter(new PercentFormatter());
        ds1.setValueTextColor(Color.BLACK);
        ds1.setValueTextSize(12f);
        PieData d = new PieData(ds1);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("");
        pieChart.setDrawEntryLabels(false);
        pieChart.setUsePercentValues(true);
        //pieChart.setCenterTextSize(10f);

        // radius of the center hole in percent of maximum radius
        pieChart.setHoleRadius(20f);
        pieChart.setTransparentCircleRadius(0f);

        Legend l = pieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXOffset(-5f);
        pieChart.setData(d);
    }

    public void drawMonthIncidentBar(BarChart barChart){
        int monthsToShow = 6;
        Map<String, Stat> monthFreq = new HashMap<String, Stat>(){};
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -monthsToShow);
        SimpleDateFormat sd = new SimpleDateFormat("MMM yyyy");
        String dataPoints[] = new String[monthsToShow];
        Calendar c = Calendar.getInstance();
        for(int i = 0; i < monthsToShow; i++){
            dataPoints[monthsToShow - i - 1] = sd.format(c);
            c.add(Calendar.MONTH, -1);
        }
        for(Report r: reports){
            c.setTime(r.getIncidentDate());
            if(c.before(cal))
                continue;
            String monthYear = sd.format(r.getIncidentDate());
            Stat count = monthFreq.get(monthYear);
            if (count == null) {
                Stat stat = new Stat();
                stat.increaseCount();
                monthFreq.put(monthYear, stat);
            }
            else {
                count.increaseCount();
            }
        }
        List<BarEntry> entriesMonthFreq = new ArrayList<>();
        float temp = 1f;
        for(String monthYear: dataPoints){
            entriesMonthFreq.add(new BarEntry(temp, monthFreq.get(monthYear).getCount()));
            temp += 2f;
        }
    }

    class Stat {
        private int count = 0; // note that we start at 1 since we're counting
        private int totalSeverity = 0;
        public void addSeverity(int severity) { if (severity <= 5) totalSeverity += severity; }
        public void increaseCount() { ++count; }
        public int  getCount() { return count; }
        public float getAvgSeverity() { return (float) totalSeverity / count; }
    }

    public class PercentFormatter implements IValueFormatter, IAxisValueFormatter {

        protected DecimalFormat mFormat;

        public PercentFormatter() {
            mFormat = new DecimalFormat("###,###,##0.0");
        }

        /**
         * Allow a custom decimalformat
         *
         * @param format
         */
        public PercentFormatter(DecimalFormat format) {
            this.mFormat = format;
        }

        // IValueFormatter
        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            return mFormat.format(value) + " %";
        }

        // IAxisValueFormatter
        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return mFormat.format(value) + " %";
        }

        public int getDecimalDigits() {
            return 1;
        }
    }

    public class TimeFormatter implements IAxisValueFormatter {
        // IAxisValueFormatter
        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            if(value - (int) value > 0)
                return "";
            int hour = (int) value;
            if(hour <= 12)
                return hour + " AM";
            else
                return hour - 12 + " PM";
        }
    }
}
