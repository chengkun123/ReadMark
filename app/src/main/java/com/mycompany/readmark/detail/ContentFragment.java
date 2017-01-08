package com.mycompany.readmark.detail;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mycompany.readmark.R;

/**
 * Created by Lenovo on 2017/1/5.
 */
public class ContentFragment extends Fragment {
    public static ContentFragment newInstance(String info){
        Bundle args = new Bundle();
        args.putString("info", info);
        ContentFragment fragment = new ContentFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_content, null);
        TextView textView = (TextView)view.findViewById(R.id.detail_content_text);
        String info = getArguments().getString("info");
        textView.setText(info);
        return view;
    }
}
