package com.farmfresh.farmfresh.auth;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;

/**
 * Created by pbabu on 8/20/16.
 */
public class FacebookAuthentication extends AppAuthentication {
    public static final String TAG = FacebookAuthentication.class.getSimpleName();
    private final CallbackManager mCallbackManager;

    public FacebookAuthentication(FireBaseAuthentication fireBaseAuthentication,
                                  FragmentActivity activity) {
        super(activity, fireBaseAuthentication);
        mCallbackManager = CallbackManager.Factory.create();
    }

    @Override
    public Intent getLoginIntent() {
        return null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public CallbackManager getMCallbackManager() {
        return mCallbackManager;
    }

    public FacebookCallback<LoginResult> getFacebookCallback() {
        return new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                mFireBaseAuthentication.fireBaseAuthWithFacebook(loginResult.getAccessToken(), mActivity);
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
            }
        };
    }
}
