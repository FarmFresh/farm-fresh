package com.farmfresh.farmfresh.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.farmfresh.farmfresh.R;
import com.farmfresh.farmfresh.auth.FireBaseAuthentication;
import com.farmfresh.farmfresh.auth.GoogleAuthentication;
import com.farmfresh.farmfresh.utils.Constants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

/**
 * Created by pbabu on 8/20/16.
 */
public class LoginFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener,
        FireBaseAuthentication.LoginListener {

    public static final String TAG = LoginFragment.class.getSimpleName();
    private GoogleAuthentication mGoogleAuthentication;
    //private FacebookAuthentication mFacebookAuthentication;
    private FirebaseUser mCurrentUser;
    private FireBaseAuthentication mFireBaseAuthentication;
    //private LoginButton mLoginButton;
    private FireBaseAuthentication.LoginListener mFireBaseLoginListener;


    public static LoginFragment newInstance(FireBaseAuthentication fireBaseAuthentication) {
        Bundle args = new Bundle();
        LoginFragment fragment = new LoginFragment();
        fragment.mFireBaseAuthentication = fireBaseAuthentication;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGoogleAuthentication = new GoogleAuthentication(mFireBaseAuthentication,
                getActivity(), this);
        //mFacebookAuthentication = new FacebookAuthentication(mFireBaseAuthentication, getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final View view = inflater.inflate(R.layout.fragment_login, container, false);
        SignInButton signInButton = (SignInButton) view.findViewById(R.id.btnGoogleSignIn);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginFragment.this.googleSignIn();
            }
        });
        /*
        mLoginButton = (LoginButton) view.findViewById(R.id.btnFacebookSignIn);
        mLoginButton.setFragment(this);
        LoginManager.getInstance().registerCallback(mFacebookAuthentication.getMCallbackManager(),
                mFacebookAuthentication.getFacebookCallback());
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                facebookSignIn();
            }
        });*/
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
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

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
        } /*else if (requestCode == Constants.RC_FACEBOOK_SIGN_IN) {
            mFacebookAuthentication.onActivityResult(requestCode, resultCode, data);
        }*/
    }

    @Override
    public void onLoginSuccess(FirebaseUser currentUser) {
        mCurrentUser = currentUser;
        mFireBaseLoginListener.onLoginSuccess(mCurrentUser);
    }

    public void googleSignIn() {
        Intent signInIntent = mGoogleAuthentication.getLoginIntent();
        startActivityForResult(signInIntent, Constants.RC_GOOGLE_SIGN_IN);
    }

    public void facebookSignIn() {
        final AccessToken currentAccessToken = AccessToken.getCurrentAccessToken();
        if (currentAccessToken != null && !currentAccessToken.isExpired()) {
            mFireBaseAuthentication.fireBaseAuthWithFacebook(currentAccessToken,
                    getActivity());
        } else {
            //Login into facebook to get access token
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
        }
    }

    public interface FireBaseLoginListener {
        void onLoginSuccess(FirebaseUser user);

        void onLoginFailure();

        void onLogout();
    }
}
