package com.farmfresh.farmfresh.auth;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;

import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

/**
 * Created by pbabu on 8/20/16.
 */
public class FireBaseAuthentication {

    private static final String TAG = FireBaseAuthentication.class.getSimpleName();
    private static FireBaseAuthentication INSTANCE = new FireBaseAuthentication();

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mCurrentUser;

    private FireBaseAuthentication() {
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mCurrentUser = firebaseAuth.getCurrentUser();
                if(mCurrentUser != null){
                    Log.d(TAG, String.format("onAuthStateChanged:user_signed_in:[id:%s, display name:%s]",
                            mCurrentUser.getUid(),
                            mCurrentUser.getDisplayName()));
                }
            }
        };
    }

    public static FireBaseAuthentication getInstance() {
        return INSTANCE;
    }

    public FirebaseUser getCurrentUser() {
        return mCurrentUser;
    }

    public void addAuthListener(){
        mAuth.addAuthStateListener(this.mAuthListener);
    }

    public void removeAuthListener() {
        mAuth.removeAuthStateListener(this.mAuthListener);
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
                    /*Toast.makeText(MainActivity.this,
                            "Firebase authentication failed",
                            Toast.LENGTH_SHORT).show();*/
                }
            }
        });
    }

    public void signOut() {
        mAuth.signOut();
    }
}
