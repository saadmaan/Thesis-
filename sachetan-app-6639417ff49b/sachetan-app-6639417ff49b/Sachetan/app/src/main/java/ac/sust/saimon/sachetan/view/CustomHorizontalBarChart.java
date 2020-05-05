package ac.sust.saimon.sachetan.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;

/**
 * Created by Saimon on 29-Jul-17.
 */

public class CustomHorizontalBarChart extends HorizontalBarChart {


    public CustomHorizontalBarChart(Context context) {
        super(context);
        initDesign();
    }

    public CustomHorizontalBarChart(Context context, AttributeSet attrs){
        super(context, attrs);
        initDesign();
    }

    public CustomHorizontalBarChart(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
        initDesign();
    }

    private void initDesign(){
        setDrawValueAboveBar(true);
        getLegend().setEnabled(true);
        getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        getXAxis().setDrawAxisLine(false);
        getXAxis().setDrawGridLines(false);
        getXAxis().setDrawLabels(false);
        getAxisLeft().setDrawAxisLine(false);
        getAxisLeft().setDrawGridLines(false);
        getAxisLeft().setDrawLabels(false);
        getAxisRight().setDrawAxisLine(false);
        getAxisRight().setDrawGridLines(false);
        getAxisRight().setDrawLabels(false);
        setFitBars(false);
        setClickable(false);
        setPinchZoom(false);
        setScaleEnabled(false);
        disableScroll();
        setDoubleTapToZoomEnabled(false);
        getDescription().setEnabled(false);
    }

    public void setXAxisRange(float min, float max){
        setAxisRange(getXAxis(), min, max);
    }

    public void setXAxisRange(float min, float max, float granularity){
        setAxisRange(getXAxis(), min, max, granularity);
    }

    public void setLeftAxisRange(float min, float max){
        setAxisRange(getAxisLeft(), min, max);
    }

    public void setLeftAxisRange(float min, float max, float granularity){
        setAxisRange(getAxisLeft(), min, max, granularity);
    }

    public void setRightAxisRange(float min, float max){
        setAxisRange(getAxisRight(), min, max);
    }

    public void setRightAxisRange(float min, float max, float granularity){
        setAxisRange(getAxisRight(), min, max, granularity);
    }

    private void setAxisRange(AxisBase axis, float min, float max){
        axis.setAxisMinimum(min);
        axis.setAxisMaximum(max);
    }

    private void setAxisRange(AxisBase axis, float min, float max, float granularity){
        axis.setAxisMinimum(min);
        axis.setAxisMaximum(max);
        axis.setGranularity(granularity);
    }
}
