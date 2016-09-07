package com.farmfresh.farmfresh.auth;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;

import com.farmfresh.farmfresh.utils.Constants;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by pbabu on 8/20/16.
 */
public class GoogleAuthentication extends AppAuthentication{

    private final GoogleApiClient.OnConnectionFailedListener mConnectionFailedListener;
    private GoogleApiClient mGoogleApiClient;

    public GoogleAuthentication(FireBaseAuthentication fireBaseAuthentication,
                                FragmentActivity activity,
                                GoogleApiClient.OnConnectionFailedListener listener) {
        super(activity, fireBaseAuthentication);
        this.mConnectionFailedListener = listener;
        setup();
    }

    @Override
    public Intent getLoginIntent() {
        return Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == Constants.RC_GOOGLE_SIGN_IN) {
            GoogleSignInResult googleSignInResult = Auth.GoogleSignInApi
                    .getSignInResultFromIntent(data);
            if(googleSignInResult.isSuccess()) {
                final GoogleSignInAccount googleSignInAccount = googleSignInResult.getSignInAccount();
                mFireBaseAuthentication.fireBaseAuthWithGoogle(googleSignInAccount, mActivity);
            }
        }
    }

    private void setup() {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(mActivity.getString(R.string.default_web_client_id))
                .requestIdToken("367269641947-vrspbt3tchd0osu2voqssa18vr5j1deb.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(mActivity)
                .enableAutoManage(mActivity, mConnectionFailedListener)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    public GoogleApiClient getmGoogleApiClient() {
        return mGoogleApiClient;
    }
}
