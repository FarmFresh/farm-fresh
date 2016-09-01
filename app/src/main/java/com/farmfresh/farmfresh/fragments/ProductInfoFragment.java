package com.farmfresh.farmfresh.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.farmfresh.farmfresh.R;
import com.farmfresh.farmfresh.models.Product;
import com.farmfresh.farmfresh.models.User;
import com.farmfresh.farmfresh.utils.Constants;
import com.farmfresh.farmfresh.utils.Helper;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by pbabu on 8/18/16.
 */
public class ProductInfoFragment extends Fragment {

    private Product product;
    private TextInputEditText etName;
    private TextInputEditText etDescription;
    private TextInputEditText etPrice;
    private TextInputEditText etAddress;
    private OnSubmitProductInfoListener listener;
    FirebaseAuth firebaseAuth;
    User currentUser;
    public static final String TAG = ProductInfoFragment.class.getSimpleName();
    public interface OnSubmitProductInfoListener {
        void withInfo(Product product);
    }

    public static ProductInfoFragment newInstance(Product product) {
        Bundle args = new Bundle();
        args.putParcelable(Constants.PRODUCT_KEY, product);
        ProductInfoFragment fragment = new ProductInfoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.product = getArguments().getParcelable(Constants.PRODUCT_KEY);
        firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if(currentUser != null) {
            loadCurrentUser(currentUser);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final View view = inflater.inflate(R.layout.fragment_product_info, container, false);
        etName = (TextInputEditText)view.findViewById(R.id.etProductName);
        etName.requestFocus();
        Helper.showSoftKeyboard(etName, getContext());
        etDescription = (TextInputEditText)view.findViewById(R.id.etProductDescription);
        etPrice = (TextInputEditText)view.findViewById(R.id.etProductPrice);
        etAddress = (TextInputEditText)view.findViewById(R.id.etProductAddress);
        if(currentUser != null && currentUser.getUserCurrentAddress() != null) {
            etAddress.setText(currentUser.getUserCurrentAddress());
        }
        if(this.product != null) {
            etName.setText(this.product.getName());
            etDescription.setText(this.product.getDescription());
            etPrice.setText(this.product.getPrice());
        }
        Button continueButton = (Button)view.findViewById(R.id.btnContinue);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                product.setName(etName.getText().toString());
                product.setDescription(etDescription.getText().toString());
                product.setPrice(etPrice.getText().toString());
                product.setAddress(etAddress.getText().toString());
                listener.withInfo(product);
            }
        });

        etAddress.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    //start the PlaceAutocomplete Activity
                    try {
                        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                                .build();
                        Intent intent =
                                new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                        .setFilter(typeFilter)
                                        .build(getActivity());
                        startActivityForResult(intent, Constants.PLACE_AUTOCOMPLETE_REQUEST_CODE);
                    } catch (GooglePlayServicesRepairableException e) {
                        // TODO: Handle the error.
                    } catch (GooglePlayServicesNotAvailableException e) {
                        // TODO: Handle the error.
                    }
                }
            }
        });

        etAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start the PlaceAutocomplete Activity
                try {
                    AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                            .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                            .build();
                    final PlaceAutocomplete.IntentBuilder intentBuilder = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .setFilter(typeFilter);
                    if(etAddress.getText().length() > 0){
                        intentBuilder.zzku(etAddress.getText().toString());
                    }
                    Intent intent = intentBuilder.build(getActivity());
                    startActivityForResult(intent, Constants.PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof OnSubmitProductInfoListener) {
            this.listener = (OnSubmitProductInfoListener)context;
        }else {
            throw new ClassCastException(context.toString()
                    + " must implement ProductInfoFragment.OnSubmitProductInfoListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.listener = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(getActivity(), data);
                Log.i(TAG, "Place: " + place.getName());
                final String selectedAddress = place.getAddress().toString();
                this.product.setAddress(selectedAddress);
                etAddress.setText(selectedAddress);
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getActivity(), data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == Activity.RESULT_CANCELED) {
                //TODO: The user canceled the operation.
            }
        }
    }

    private void loadCurrentUser(FirebaseUser user) {
        FirebaseDatabase.getInstance().getReference().child(Constants.NODE_USERS)
                .child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ProductInfoFragment.this.currentUser = dataSnapshot.getValue(User.class);
                if(etAddress != null) {
                    etAddress.setText(ProductInfoFragment.this.currentUser.getUserCurrentAddress());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
