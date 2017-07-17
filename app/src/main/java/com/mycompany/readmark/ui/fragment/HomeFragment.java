package com.mycompany.readmark.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.ViewGroup;


import com.mycompany.readmark.R;
import com.mycompany.readmark.ui.activity.MainActivity;
import com.mycompany.readmark.ui.activity.SearchActivity;
import com.mycompany.readmark.ui.widget.floatingactionbutton.MultiFloatingActionButton;
import com.mycompany.readmark.ui.widget.floatingactionbutton.TagFabLayout;
import com.mycompany.readmark.utils.commen.UIUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Lenovo.
 */

public class HomeFragment extends BaseFragment{

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    @BindView(R.id.tabLayout)
    TabLayout mTabLayout;
    @BindView(R.id.floating_button)
    MultiFloatingActionButton mFab;

    private List<BaseFragment> mFragments;

    public static HomeFragment newInstance(){
        Bundle args = new Bundle();
        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);
        return fragment;

    }


    @Override
    protected void initRootView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.home_fragment, container, false);
    }

    @Override
    protected void initEvents() {
        mFab.setOnFabItemClickListener(new MultiFloatingActionButton.OnFabItemClickListener() {
            @Override
            public void onFabItemClick(TagFabLayout view, int pos) {
                switch (pos){
                    case 2:
                        Intent intent = new Intent(UIUtils.getContext(), SearchActivity.class);
                        UIUtils.startActivity(intent);
                        break;
                    case 3:
                        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        break;
                }
            }
        });
    }

    @Override
    protected void initData(boolean isSavedNull) {

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViewPager();
        ((MainActivity)getActivity()).setToolbar(mToolbar);
        //((MainActivity)getActivity()).setThemeSetter(mRootView);
        //((MainActivity)getActivity()).setFab(mFab);
    }

    @Override
    public void onDestroyView() {
        if(mRootView != null){
            //((MainActivity)getActivity()).clearThemeSetter(mRootView);
        }
        super.onDestroyView();
    }

    private void initViewPager(){
        mFragments = new ArrayList<>();
        mFragments.add(BookListFragment.newInstance("高分"));
        mFragments.add(BookListFragment.newInstance("热门"));
        mFragments.add(BookListFragment.newInstance("新书"));
        mFragments.add(BookListFragment.newInstance("经典"));

        mViewPager.setAdapter(new HomeAdapter(getChildFragmentManager(), mFragments));
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setCurrentItem(2);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setSelectedTabIndicatorColor(getContext().getResources().getColor(R.color.white));

    }


    class HomeAdapter extends FragmentStatePagerAdapter {
        private List<BaseFragment> mFragments;
        private final String[] titles;

        public HomeAdapter(FragmentManager fm, List<BaseFragment> fragments) {
            super(fm);
            mFragments = fragments;
            titles = getResources().getStringArray(R.array.main_tab_type);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }


}