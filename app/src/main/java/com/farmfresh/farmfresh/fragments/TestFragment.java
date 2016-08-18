package com.farmfresh.farmfresh.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Created by pbabu on 8/18/16.
 */
public class TestFragment  extends Fragment {

    private String title;

    public static TestFragment newInstance(String title) {

        Bundle args = new Bundle();
        TestFragment fragment = new TestFragment();
        fragment.title = title;
        fragment.setArguments(args);
        return fragment;
    }
}
