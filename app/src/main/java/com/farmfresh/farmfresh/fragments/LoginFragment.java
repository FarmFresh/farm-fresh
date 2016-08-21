package com.farmfresh.farmfresh.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.farmfresh.farmfresh.R;
import com.farmfresh.farmfresh.auth.AppAuthentication;
import com.farmfresh.farmfresh.auth.FireBaseAuthentication;
import com.farmfresh.farmfresh.auth.GoogleAuthentication;
import com.farmfresh.farmfresh.utils.Constants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by pbabu on 8/20/16.
 */
public class LoginFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener {

    private AppAuthentication mGoogleAuthentication;
    private FirebaseUser mCurrentUser;
    private FireBaseAuthentication mFireBaseAuthentication = FireBaseAuthentication.getInstance();
    public static final String TAG = LoginFragment.class.getSimpleName();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGoogleAuthentication = new GoogleAuthentication(mFireBaseAuthentication,
                getActivity(), this);
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
        return view;
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Constants.RC_GOOGLE_SIGN_IN) {
           mGoogleAuthentication.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void googleSignIn() {
        Intent signInIntent = mGoogleAuthentication.getLoginIntent();
        startActivityForResult(signInIntent, Constants.RC_GOOGLE_SIGN_IN);
    }

    public void signOut() {
        mFireBaseAuthentication.signOut();
    }
}
