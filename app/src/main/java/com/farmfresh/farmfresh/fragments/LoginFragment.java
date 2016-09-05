package com.farmfresh.farmfresh.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.LoginButton;
import com.farmfresh.farmfresh.R;
import com.farmfresh.farmfresh.auth.FacebookAuthentication;
import com.farmfresh.farmfresh.auth.FireBaseAuthentication;
import com.farmfresh.farmfresh.auth.GoogleAuthentication;
import com.farmfresh.farmfresh.utils.Constants;
import com.google.android.gms.common.SignInButton;
import com.jpardogo.android.googleprogressbar.library.GoogleProgressBar;

import java.util.Arrays;

/**
 * Created by pbabu on 8/20/16.
 */
public class LoginFragment extends Fragment {

    public static final String TAG = LoginFragment.class.getSimpleName();
    private GoogleAuthentication mGoogleAuthentication;
    private FacebookAuthentication mFacebookAuthentication;
    private FireBaseAuthentication mFireBaseAuthentication;
    private LoginButton mFacebookSignInButton;
    private FireBaseAuthentication.LoginListener mFireBaseLoginListener;
    private GoogleProgressBar mProgressBar;
    private SignInButton mGoogleSignInButton;
    public static LoginFragment newInstance(GoogleAuthentication googleAuthentication, FacebookAuthentication facebookAuthentication) {
        Bundle args = new Bundle();
        LoginFragment fragment = new LoginFragment();
        fragment.mGoogleAuthentication = googleAuthentication;
        fragment.mFireBaseAuthentication = googleAuthentication.getMFireBaseAuthentication();
        fragment.mFacebookAuthentication = facebookAuthentication;
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final View view = inflater.inflate(R.layout.fragment_login, container, false);
        mGoogleSignInButton = (SignInButton) view.findViewById(R.id.btnGoogleSignIn);
        mGoogleSignInButton.setSize(SignInButton.SIZE_WIDE);
        mGoogleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginFragment.this.googleSignIn();
            }
        });
        mFacebookSignInButton = (LoginButton) view.findViewById(R.id.btnFacebookSignIn);
        mFacebookSignInButton.setReadPermissions("email");
        mFacebookSignInButton.setFragment(this);
        LoginManager.getInstance().registerCallback(mFacebookAuthentication.getMCallbackManager(),
                mFacebookAuthentication.getFacebookCallback());
        mFacebookSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                facebookSignIn();
            }
        });

        mProgressBar = (GoogleProgressBar)view.findViewById(R.id.google_progress);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FireBaseAuthentication.LoginListener) {
            mFireBaseLoginListener = (FireBaseAuthentication.LoginListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement FireBaseAuthentication.LoginListener");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mFireBaseAuthentication.addAuthListener();
    }

    @Override
    public void onStop() {
        super.onStop();
        mFireBaseAuthentication.removeAuthListener();
    }

    @Override
    public void onPause() {
        super.onPause();
        mGoogleAuthentication.getmGoogleApiClient().stopAutoManage(getActivity());
        mGoogleAuthentication.getmGoogleApiClient().disconnect();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.RC_GOOGLE_SIGN_IN) {
            mGoogleAuthentication.onActivityResult(requestCode, resultCode, data);
        } else if (FacebookSdk.isFacebookRequestCode(requestCode)) {
            mFacebookAuthentication.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void googleSignIn() {
        showProgressBar();
        Intent signInIntent = mGoogleAuthentication.getLoginIntent();
        startActivityForResult(signInIntent, Constants.RC_GOOGLE_SIGN_IN);
    }

    public void facebookSignIn() {
        showProgressBar();
        final AccessToken currentAccessToken = AccessToken.getCurrentAccessToken();
        if (currentAccessToken != null && !currentAccessToken.isExpired()) {
            mFireBaseAuthentication.fireBaseAuthWithFacebook(currentAccessToken,
                    getActivity());
        } else {
            //Login into facebook to get access token
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
        }
    }

    private void showProgressBar() {
        mGoogleSignInButton.setVisibility(View.INVISIBLE);
        mFacebookSignInButton.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
    }
}
