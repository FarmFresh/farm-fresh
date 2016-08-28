package com.farmfresh.farmfresh.auth;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.farmfresh.farmfresh.models.User;
import com.farmfresh.farmfresh.utils.Constants;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by pbabu on 8/20/16.
 */
public class FireBaseAuthentication {

    private static final String TAG = FireBaseAuthentication.class.getSimpleName();

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private final LoginListener mLoginSuccessListener;
    public interface LoginListener {
        void onLoginSuccess(FirebaseUser currentUser);
    }

    public FireBaseAuthentication(final LoginListener mLoginSuccessListener) {
        this.mLoginSuccessListener = mLoginSuccessListener;
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                if(currentUser != null){
                    Log.d(TAG, String.format("onAuthStateChanged:user_signed_in:[id:%s, display name:%s]",
                            currentUser.getUid(),
                            currentUser.getDisplayName()));
                    final UserInfo userInfo = currentUser.getProviderData().get(0);
                    final User user = FireBaseAuthentication.this.buildUserFrom(userInfo);
                    DatabaseReference usersRef = FirebaseDatabase.getInstance()
                            .getReference()
                            .child(Constants.NODE_USERS);
                    usersRef.child(currentUser.getUid()).setValue(user);
                    FireBaseAuthentication.this.mLoginSuccessListener.onLoginSuccess(currentUser);
                }
            }
        };
        mAuth.addAuthStateListener(mAuthListener);
    }

    public void addAuthListener(){
        if(this.mAuthListener != null){
            mAuth.addAuthStateListener(this.mAuthListener);
        }
    }

    public void removeAuthListener() {
        if(this.mAuthListener != null) {
            mAuth.removeAuthStateListener(this.mAuthListener);
        }
    }

    public void fireBaseAuthWithGoogle(GoogleSignInAccount acct, final Activity activity){
        Log.d(TAG, "fireBaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        signIntoFireBase(credential,activity);
    }

    public void fireBaseAuthWithFacebook(AccessToken token, Activity activity) {
        Log.d(TAG, "fireBaseAuthWithFacebook:" + token);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        signIntoFireBase(credential, activity);
    }

    private void signIntoFireBase(AuthCredential credential, final Activity activity) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(!task.isSuccessful()){
                    Log.w(TAG, "signInWithCredential", task.getException());
                    //if login fails with firebase, show a toast message
                    //TODO: handle firebase login failure
                    Toast.makeText(activity,
                            "Firebase authentication failed",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void signOut() {
        Log.d(TAG, "FireBase signing out....");
        mAuth.signOut();
    }

    private User buildUserFrom(UserInfo userInfo) {
        User user = new User();
        user.setEmail(userInfo.getEmail());
        user.setDisplayName(userInfo.getDisplayName());
        user.setProfileImageUrl(userInfo.getPhotoUrl().toString());
        return user;
    }
}
