package com.farmfresh.farmfresh.utils;

/**
 * Created by pbabu on 8/18/16.
 */
public class Constants {
    public static final int RC_GOOGLE_SIGN_IN = 10001;
    public static final int RC_FACEBOOK_SIGN_IN = 10002;

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

}
