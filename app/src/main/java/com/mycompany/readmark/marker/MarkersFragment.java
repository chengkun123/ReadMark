package com.mycompany.readmark.marker;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.mycompany.readmark.R;
import com.mycompany.readmark.common.DatabaseTableSingleton;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lenovo on 2017/3/18.
 */
public class MarkersFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private MarkerAdapter mMarkerAdapter;
    private List<MarkerBean> mDates;
    private DatabaseTableSingleton mDatabaseTableSingleton;
    private Button mButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.from(getActivity()).inflate(R.layout.fragment_marker, null);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.marker_recyclerview);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mButton = (Button) view.findViewById(R.id.button_test);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mDates.size()>0){
                    mDatabaseTableSingleton.deleteMarker(mDates.get(mDates.size()-1).getImageUrl());
                    mDates = mDatabaseTableSingleton.loadMarkerInfo();
                    mMarkerAdapter.upDateMarkers(mDates);
                }else{
                    Toast.makeText(getActivity(), "没有书签了！", Toast.LENGTH_SHORT).show();
                }
                //
            }
        });
        mDatabaseTableSingleton = DatabaseTableSingleton.getDatabaseTable(getActivity());

        mDates = new ArrayList<>();

        mDates = mDatabaseTableSingleton.loadMarkerInfo();

        /*MarkerBean bean = new MarkerBean();
        bean.setMarkerName("算法");
        bean.setImageUrl("https://img1.doubanio.com/lpic/s8938479.jpg");
        bean.setProgress(20);
        mDates.add(bean);

        MarkerBean bean1 = new MarkerBean();
        bean1.setMarkerName("suanfa2");
        bean1.setImageUrl("https://img3.doubanio.com/lpic/s1106991.jpg");
        bean1.setProgress(50);
        mDates.add(bean1);*/

        mMarkerAdapter = new MarkerAdapter(mDates, getActivity());
        mMarkerAdapter.setOnMarkerClickListener((MarkerAdapter.OnMarkerClickListener)getActivity());
        mRecyclerView.setAdapter(mMarkerAdapter);
        return view;
    }
}
