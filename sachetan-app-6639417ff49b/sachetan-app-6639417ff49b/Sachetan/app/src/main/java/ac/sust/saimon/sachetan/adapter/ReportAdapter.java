package ac.sust.saimon.sachetan.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import ac.sust.saimon.sachetan.R;
import ac.sust.saimon.sachetan.data.model.Report;
import ac.sust.saimon.sachetan.data.util.DateFormatter;

/**
 * Created by Saimon on 19-Oct-15.
 */
public class ReportAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater=null;
    private ArrayList<Report> reports;
    private View.OnClickListener onDeleteClickListener;
    private boolean userIsOwner;

    private final int DESC_LENGTH = 250;
    public ReportAdapter(Context context, ArrayList<Report> reports, boolean userIsOwner){
        this.context = context;
        this.reports = reports;
        this.userIsOwner = userIsOwner;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return reports.size();
    }

    @Override
    public Report getItem(int position) {
        return reports.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View rowView, ViewGroup parent) {
        final Holder holder;
        if (rowView == null) {
            holder = new Holder();
            rowView = inflater.inflate(R.layout.item_report, null);
            holder.dateTime = (TextView) rowView.findViewById(R.id.tv_incident_datetime);
            holder.creatorEmail = (TextView) rowView.findViewById(R.id.tv_creator_email);
            holder.incidentType = (TextView) rowView.findViewById(R.id.tv_incident_type);
            holder.postText = (TextView) rowView.findViewById(R.id.tv_post_text);
            holder.severity = (TextView) rowView.findViewById(R.id.tv_incident_severity);
            holder.btnDeletePost = (ImageButton) rowView.findViewById(R.id.btn_deletepost);
            rowView.setTag(holder);
        }else {
            holder = (Holder) rowView.getTag();
        }
        Report report = reports.get(position);
        if (report.getIncidentDate() != null)
            holder.dateTime.setText(DateFormatter.getDateAsString(report.getIncidentDate()));
        else
            holder.dateTime.setText("No Date Info");
        if (report.getIncidentType() != null)
            holder.incidentType.setText(report.getIncidentType().getName());
        else
            holder.incidentType.setText("Unknown type");
        if (report.getDescription() != null && report.getDescription() != "") {
            String text = report.getDescription();
            holder.postText.setTextColor(context.getResources().getColor(R.color.text_normal));
            holder.postText.setText(
                    text.length() > DESC_LENGTH ? text.substring(0, DESC_LENGTH) + " ..." : text);
        }
        else {
            holder.postText.setTextColor(Color.GRAY);
            holder.postText.setText("No description available");
        }
        if(report.getSeverity()!= null)
            holder.severity.setText("Severity: " + report.getSeverity());
        else
            holder.severity.setText("1");
        if(this.userIsOwner) {
            holder.btnDeletePost.setVisibility(View.VISIBLE);
            holder.btnDeletePost.setTag(report.getId());
        }
        return rowView;
    }

    public class Holder
    {
        TextView dateTime;
        TextView postText;
        TextView creatorEmail;
        TextView incidentType;
        TextView severity;
        ImageButton btnDeletePost;
    }
}
