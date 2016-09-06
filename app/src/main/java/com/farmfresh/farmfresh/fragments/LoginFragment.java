package com.farmfresh.farmfresh.fragments;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.LoginButton;
import com.farmfresh.farmfresh.R;
import com.farmfresh.farmfresh.auth.EmailPasswordAuthentication;
import com.farmfresh.farmfresh.auth.FacebookAuthentication;
import com.farmfresh.farmfresh.auth.FireBaseAuthentication;
import com.farmfresh.farmfresh.auth.GoogleAuthentication;
import com.farmfresh.farmfresh.databinding.FragmentLoginBinding;
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
    private EmailPasswordAuthentication mEmailPasswordAuthentication;
    private LoginButton mFacebookSignInButton;
    private FireBaseAuthentication.LoginListener mFireBaseLoginListener;
    private GoogleProgressBar mProgressBar;
    private SignInButton mGoogleSignInButton;
    private Button btnSignUp;
    private Button btnsignIn;
    private SignUpListener signupListener;
    private FragmentLoginBinding binding;

    public static LoginFragment newInstance(GoogleAuthentication googleAuthentication,
                                            FacebookAuthentication facebookAuthentication,
                                            EmailPasswordAuthentication emailPasswordAuthentication) {
        Bundle args = new Bundle();
        LoginFragment fragment = new LoginFragment();
        fragment.mGoogleAuthentication = googleAuthentication;
        fragment.mFireBaseAuthentication = googleAuthentication.getMFireBaseAuthentication();
        fragment.mFacebookAuthentication = facebookAuthentication;
        fragment.mEmailPasswordAuthentication = emailPasswordAuthentication;
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false);
        bindingViews();
        return binding.getRoot();
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
        if(context instanceof  SignUpListener) {
            signupListener = (SignUpListener)context;
        }else {
            throw new ClassCastException(context.toString()
                    + " must implement LoginFragment.SignUpListener");
        }
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
        } else if (requestCode == Constants.RC_EMAIL_PWD_SIGN_IN) {
            mEmailPasswordAuthentication.onActivityResult(requestCode, resultCode, data);
        } else if (requestCode == Constants.RC_EMAIL_PWD_SIGN_UP) {
            mEmailPasswordAuthentication.onActivityResult(requestCode, resultCode, data);
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

    public void emailPasswordSignIn() {
        showProgressBar();
        String email = binding.etUserEmail.getText().toString();
        String password = binding.etUserPassword.getText().toString();
        mFireBaseAuthentication.fireBaseWithPasswordAuthentication(email, password, getActivity());
    }

    private void showProgressBar() {
        binding.llSocial.setVisibility(View.GONE);
        binding.rlEmailLogin.setVisibility(View.GONE);
        binding.orDivider.setVisibility(View.GONE);
        binding.googleProgress.setVisibility(View.VISIBLE);
    }


    public interface SignUpListener {
        void onSignup();
    }

    private void bindingViews() {
        mGoogleSignInButton = (SignInButton) binding.googleLoginIn.findViewById(R.id.btnGoogleSignIn);
        mGoogleSignInButton.setSize(SignInButton.SIZE_WIDE);
        mGoogleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginFragment.this.googleSignIn();
            }
        });

        mFacebookSignInButton = (LoginButton) binding.facebookLoginIn.findViewById(R.id.btnFacebookSignIn);
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

        btnSignUp = binding.btnSignup;
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signupListener.onSignup();
            }
        });
        btnsignIn = binding.btnSignIn;
        btnsignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailPasswordSignIn();
            }
        });
        mProgressBar = binding.googleProgress;
    }
}
