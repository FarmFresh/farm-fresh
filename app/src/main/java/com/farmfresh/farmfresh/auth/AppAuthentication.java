package com.farmfresh.farmfresh.auth;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;

/**
 * Created by pbabu on 8/20/16.
 */
public abstract class AppAuthentication {
    protected final FireBaseAuthentication mFireBaseAuthentication;
    protected final FragmentActivity mActivity;

    protected AppAuthentication(FragmentActivity mActivity, FireBaseAuthentication mFireBaseAuthentication) {
        this.mActivity = mActivity;
        this.mFireBaseAuthentication = mFireBaseAuthentication;
    }

    public abstract void onActivityResult(int requestCode, int resultCode, Intent data);

    public abstract void signIn();

    public void signOut() {
        mFireBaseAuthentication.signOut();
    }

    public void addAuthLisenters(){
        mFireBaseAuthentication.addAuthListener();
    }

    public void removeAuthListeners() {
        mFireBaseAuthentication.removeAuthListener();
    }
}
