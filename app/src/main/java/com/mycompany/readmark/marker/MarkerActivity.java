package com.mycompany.readmark.marker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.mycompany.readmark.R;
import com.mycompany.readmark.common.DatabaseTableSingleton;

/**
 * Created by Lenovo on 2017/3/14.
 */
public class MarkerActivity extends AppCompatActivity implements MarkerAdapter.OnMarkerClickListener{
    private MarkersFragment mMarkersFragment;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker);
        if(savedInstanceState == null){
            mMarkersFragment = new MarkersFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.marker_content, mMarkersFragment)
                    .commit();
        }

    }

    @Override
    public void onMarkerClick(MarkerBean bean) {
        ProgressFragment fragment = new ProgressFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("bean", bean);
        fragment.setArguments(bundle);

        getSupportFragmentManager()
                .beginTransaction()
                .hide(mMarkersFragment)
                .addToBackStack("marker")
                .add(R.id.marker_content, fragment)
                .commit();

    }
}
