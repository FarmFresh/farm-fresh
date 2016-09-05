package com.farmfresh.farmfresh.auth;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;

import com.farmfresh.farmfresh.fragments.EmailPasswordSignUpFragment;
import com.farmfresh.farmfresh.utils.Constants;

/**
 * Created by pbabu on 9/4/16.
 */
public class EmailPasswordAuthentication extends AppAuthentication{

    public static final String TAG = EmailPasswordAuthentication.class.getSimpleName();

    public EmailPasswordAuthentication(FireBaseAuthentication mFireBaseAuthentication, FragmentActivity mActivity) {
        super(mActivity, mFireBaseAuthentication);
    }

    @Override
    public Intent getLoginIntent() {
        return new Intent(mActivity, EmailPasswordSignUpFragment.class);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        final String email = data.getStringExtra(Constants.USER_EMAIL_KEY);
        final String password = data.getStringExtra(Constants.USER_PASSWORD_KEY);
        mFireBaseAuthentication.fireBaseWithPasswordAuthentication(email, password, mActivity);
    }
}
