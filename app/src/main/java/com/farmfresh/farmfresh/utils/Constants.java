package com.farmfresh.farmfresh.utils;

/**
 * Created by pbabu on 8/18/16.
 */
public class Constants {
    public static final int RC_GOOGLE_SIGN_IN = 10001;

    /**
     * Please do not use the range between the
     * value you set and another 100 entries after it in your
     * other requests.
     **/

    public static final int RC_FACEBOOK_REQUEST_OFFSET = 20000;

    public static final int PRODUCT_ONLY=1;
    public static final int PRODUCT_IMAGE=2;
    public static final String PRODUCT_KEY = "product";

    public final static int PICK_PHOTO_CODE = 1046;
    public final static int PRODUCT_IMAGE_WIDTH = 350; //pixels
    public final static int PRODUCT_IMAGE_HEIGHT = 350; //pixels
    public final static  int MAX_PRODUCT_IMAGES = 4;
    public final static int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    //firebase nodes
    public static final String NODE_PRODUCTS = "products";
    public static final String NODE_USERS = "users";


    public static final String PACKAGE_NAME =
            "com.google.android.gms.location.sample.locationaddress";
    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
    public static final String RESULT_ADDRESS_DATA_KEY = PACKAGE_NAME +
            ".RESULT_ADDRESS_DATA_KEY";
    public static final String RESULT_LOCATION_DATA_KEY = PACKAGE_NAME +
            ".RESULT_LOCATION_DATA_KEY";
    public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME +
            ".LOCATION_DATA_EXTRA";
    public static final String ADDRESS_DATA_EXTRA = PACKAGE_NAME +
            ".ADDRESS_DATA_EXTRA";
    public static final int FETCH_ADDRESS_SUCCESS_RESULT = 0;
    public static final int FETCH_ADDRESS_FAILURE_RESULT = 1;


    //Email and Password signup related constants
    public static final int USER_FULL_NAME_MIN_LENGTH = 3;
    public static final int USER_PASSWORD_MIN_LENGTH = 8;
    public static final String USER_EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
}
