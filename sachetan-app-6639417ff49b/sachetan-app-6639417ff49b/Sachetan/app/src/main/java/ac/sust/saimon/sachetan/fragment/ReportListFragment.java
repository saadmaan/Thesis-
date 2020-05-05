package ac.sust.saimon.sachetan.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import ac.sust.saimon.sachetan.R;
import ac.sust.saimon.sachetan.adapter.ReportAdapter;
import ac.sust.saimon.sachetan.data.model.Report;

public class ReportListFragment extends Fragment {
    private static final String ARG_PARAM = "fragment.param.reports";

    private ListView mListView;

    private ArrayList<Report> reports;

    /**
    private OnFragmentInteractionListener mListener;
    **/

    public ReportListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param reports Parameter 1.
     * @return A new instance of fragment ReportListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReportListFragment newInstance(ArrayList<Report> reports) {
        ReportListFragment fragment = new ReportListFragment();
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
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_report_list, container, false);
        mListView = (ListView) rootView.findViewById(R.id.listview_reports);
        if(!reports.isEmpty()){
            mListView.setAdapter(new ReportAdapter(getContext(), reports, false));
        }
        return rootView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /**
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
         **/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        /**
        mListener = null;
         **/
    }

    /**
    public interface OnFragmentInteractionListener {
        // TODO: add methods if necessary
    }
     **/
}
