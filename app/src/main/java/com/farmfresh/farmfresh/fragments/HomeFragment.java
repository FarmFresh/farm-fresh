package com.farmfresh.farmfresh.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.farmfresh.farmfresh.R;

/**
 * Created by pbabu on 8/18/16.
 */
public class HomeFragment extends Fragment {
    private TextView mTvTest;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final View view = inflater.inflate(R.layout.fragment_test, container, false);
        mTvTest = (TextView)view.findViewById(R.id.tvTest);
        mTvTest.setText("You are in home fragment");
        return view;
    }
}
