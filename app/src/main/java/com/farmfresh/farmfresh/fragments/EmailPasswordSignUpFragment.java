package com.farmfresh.farmfresh.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.farmfresh.farmfresh.R;
import com.farmfresh.farmfresh.databinding.FragmentEmailPwdSignupBinding;
import com.farmfresh.farmfresh.utils.Constants;

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

    private void validateFields() {
        final String userFullName = etUserFullName.getText().toString();
        final String userEmail = etUserEmail.getText().toString();
        final String userPassword = etUserPassword.getText().toString();
        validateFullName(userFullName);
        validateEmail(userEmail);
        validatePassword(userPassword);
    }

    private void createUser() {
        validateFields();
        if(isValidInput) {

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
