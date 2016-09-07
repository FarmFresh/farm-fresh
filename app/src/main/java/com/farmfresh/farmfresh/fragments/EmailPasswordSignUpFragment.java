package com.farmfresh.farmfresh.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.farmfresh.farmfresh.R;
import com.farmfresh.farmfresh.auth.AddressResultReceiver;
import com.farmfresh.farmfresh.auth.FireBaseAuthentication;
import com.farmfresh.farmfresh.databinding.FragmentEmailPwdSignupBinding;
import com.farmfresh.farmfresh.models.User;
import com.farmfresh.farmfresh.utils.Constants;
import com.farmfresh.farmfresh.utils.Helper;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by pbabu on 9/4/16.
 */
public class EmailPasswordSignUpFragment extends Fragment {

    private FragmentEmailPwdSignupBinding binding;

    private TextInputLayout tilUserFullName;
    private TextInputLayout tilUserEmail;
    private TextInputLayout tilUserPassword;

    private TextInputEditText etUserFullName;
    private TextInputEditText etUserEmail;
    private TextInputEditText etUserPassword;

    private Button btnSubmit;
    private boolean isValidInput = true;
    private FirebaseAuth firebaseAuth;
    private Activity parentActivity;
    private FireBaseAuthentication.LoginListener mFireBaseLoginListener;

    public static EmailPasswordSignUpFragment newInstance(FireBaseAuthentication.LoginListener fireBaseLoginListener) {
        Bundle args = new Bundle();
        EmailPasswordSignUpFragment fragment = new EmailPasswordSignUpFragment();
        fragment.mFireBaseLoginListener = fireBaseLoginListener;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        parentActivity = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_email_pwd_signup, container, false);
        bindingSetup();
        return binding.getRoot();
    }

    private void bindingSetup() {
        etUserFullName = binding.etUserName;
        etUserEmail = binding.etUserEmail;
        etUserPassword = binding.etUserPassword;
        btnSubmit = binding.btnSubmit;

        tilUserEmail = binding.userEmailTextInputLayout;
        tilUserFullName = binding.userFullNameTextInputLayout;
        tilUserPassword = binding.userPasswordTextInputLayout;

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
                createUser();
            }
        });
    }

    private void validateFields(String email, String password, String fullName) {
        validateFullName(fullName);
        validateEmail(email);
        validatePassword(password);
    }

    private void createUser() {
        final String userFullName = etUserFullName.getText().toString();
        final String userEmail = etUserEmail.getText().toString();
        final String userPassword = etUserPassword.getText().toString();
        validateFields(userEmail, userPassword, userFullName);
        if(isValidInput) {
            firebaseAuth.createUserWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    final FirebaseUser firebaseUser = task.getResult().getUser();
                    final String fireBaseUserId = firebaseUser.getUid();
                    DatabaseReference usersRef = FirebaseDatabase.getInstance()
                            .getReference()
                            .child(Constants.NODE_USERS);
                    final DatabaseReference currentUserRef = usersRef.child(fireBaseUserId);
                    User user = new User();
                    user.setEmail(firebaseUser.getEmail());
                    user.setDisplayName(userFullName);
                    //add remaining properties to the user node
                    currentUserRef.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (User.latLng != null) {
                                //set geolocation for that user.
                                GeoFire geoFire = new GeoFire(currentUserRef);
                                geoFire.setLocation("location", new GeoLocation(User.latLng.latitude, User.latLng.longitude));
                                //fetch address for location
                                Location location = new Location("");
                                location.setLatitude(User.latLng.latitude);
                                location.setLongitude(User.latLng.longitude);
                                AddressResultReceiver resultReceiver = new AddressResultReceiver(parentActivity, null);
                                Helper.startFetchAddressIntentService(parentActivity,resultReceiver, location);
                            }
                            mFireBaseLoginListener.onLoginSuccess(firebaseUser);
                        }
                    });
                }
            });
        }
    }

    // validating email id
    private boolean isValidEmail(String email) {
        Pattern pattern = Pattern.compile(Constants.USER_EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    // validating password with retype password
    private boolean isValidPassword(String password) {
        return (password != null && password.length() >= Constants.USER_PASSWORD_MIN_LENGTH) ?
                true : false;
    }

    private boolean isValidFullName(String fullName) {
        return (fullName != null && fullName.length() >= Constants.USER_FULL_NAME_MIN_LENGTH) ?
                true : false;
    }

    private void validateFullName(String fullName){
        if(!isValidFullName(fullName)) {
            isValidInput = false;
            tilUserFullName.setErrorEnabled(true);
            tilUserFullName.setError("Invalid name");
        }
    }

    private void validateEmail(String email){
        if(!isValidEmail(email)) {
            isValidInput = false;
            tilUserEmail.setErrorEnabled(true);
            tilUserEmail.setError("Input a valid email");
        }
    }

    private void validatePassword(String password){
        if(!isValidPassword(password)) {
            isValidInput = false;
            tilUserPassword.setErrorEnabled(true);
            tilUserPassword.setError(String.format("Password must be %d characters long",
                    Constants.USER_PASSWORD_MIN_LENGTH));
        }
    }

    private void reset() {
        isValidInput = true;
        tilUserFullName.setErrorEnabled(false);
        tilUserFullName.setError(null);
        tilUserEmail.setErrorEnabled(false);
        tilUserEmail.setError(null);
        tilUserPassword.setErrorEnabled(false);
        tilUserPassword.setError(null);
    }
}
