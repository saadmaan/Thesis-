package ac.sust.saimon.sachetan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ac.sust.saimon.sachetan.R;
import ac.sust.saimon.sachetan.data.model.IncidentType;

/**
 * Created by Saimon on 19-Oct-15.
 */
public class IncidentTypeAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater=null;
    public ArrayList<IncidentType> incidentTypes;

    public IncidentTypeAdapter(Context context, ArrayList<IncidentType> incidentTypes){
        this.context = context;
        this.incidentTypes = incidentTypes;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return incidentTypes.size();
    }

    @Override
    public IncidentType getItem(int position) {
        return incidentTypes.get(position);
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
            rowView = inflater.inflate(R.layout.item_spinner_incident_type, null);
            holder.name = (TextView) rowView.findViewById(R.id.tv_incident_type);
            holder.icon = (ImageView) rowView.findViewById(R.id.iv_incident_type);
            rowView.setTag(holder);
        }else {
            holder = (Holder) rowView.getTag();
        }
        IncidentType incidentType = incidentTypes.get(position);
        holder.name.setText(incidentType.getName());
        return rowView;
    }

    public class Holder
    {
        TextView name;
        ImageView icon;
    }
}
