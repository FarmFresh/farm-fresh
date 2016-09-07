package com.farmfresh.farmfresh.auth;

import android.app.Activity;
import android.content.Intent;

/**
 * Created by pbabu on 8/20/16.
 */
public abstract class AppAuthentication {
    protected final FireBaseAuthentication mFireBaseAuthentication;
    protected final Activity mActivity;

    protected AppAuthentication(Activity mActivity, FireBaseAuthentication mFireBaseAuthentication) {
        this.mActivity = mActivity;
        this.mFireBaseAuthentication = mFireBaseAuthentication;
    }

    public abstract void onActivityResult(int requestCode, int resultCode, Intent data);

    public abstract Intent getLoginIntent();

    public FireBaseAuthentication getMFireBaseAuthentication() {
        return mFireBaseAuthentication;
    }
}
