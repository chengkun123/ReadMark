package com.mycompany.readmark.marker;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.mycompany.readmark.R;
import com.mycompany.readmark.common.DatabaseTableSingleton;
import com.mycompany.readmark.themechangeframe.ThemeChangeHelper;

import okhttp3.OkHttpClient;

/**
 * Created by Lenovo on 2017/3/14.
 */
public class MarkerActivity extends AppCompatActivity implements MarkerAdapter.OnMarkerClickListener{
    private MarkersFragment mMarkersFragment;
    private android.support.v4.app.FragmentManager mFragmentManager;
    private ThemeChangeHelper mThemeChangeHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initTheme();
        setContentView(R.layout.activity_marker);
        mFragmentManager = getSupportFragmentManager();
        mFragmentManager.addOnBackStackChangedListener(new android.support.v4.app.FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                //晕，这里是什么鬼？一定是count为什么是0
                if (mFragmentManager.getBackStackEntryCount() == 0) {
                    Log.e("显示了MarkerFragment时", "已显示");
                    mMarkersFragment.onPercentChanged();
                }
            }
        });

        if(savedInstanceState == null){
            mMarkersFragment = new MarkersFragment();
            mFragmentManager
                    .beginTransaction()
                    .replace(R.id.marker_content, mMarkersFragment)
                    .commit();
        }

    }

    private void initTheme(){
        mThemeChangeHelper = new ThemeChangeHelper(this);
        if (mThemeChangeHelper.isDay()){
            setTheme(R.style.DayTheme);
        }else{
            setTheme(R.style.NightTheme);
        }
    }

    @Override
    public void onMarkerClick(MarkerBean bean) {
        ProgressFragment fragment = new ProgressFragment();
        //fragment.setOnPercentChangedListener((ProgressFragment.OnPercentChangedListener) mMarkersFragment);
        Bundle bundle = new Bundle();
        bundle.putSerializable("bean", bean);
        fragment.setArguments(bundle);

        mFragmentManager
                .beginTransaction()
                .hide(mMarkersFragment)
                .addToBackStack("marker")
                .add(R.id.marker_content, fragment)
                .commit();
    }


}
