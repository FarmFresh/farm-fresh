package com.farmfresh.farmfresh.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.farmfresh.farmfresh.R;
import com.farmfresh.farmfresh.models.Product;
import com.farmfresh.farmfresh.utils.Constants;

/**
 * Created by pbabu on 8/18/16.
 */
public class ProductInfoFragment extends Fragment {

    private Product product;
    private EditText etName;
    private EditText etDescription;
    private EditText etPrice;
    private OnSubmitProductInfoListener listener;

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
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final View view = inflater.inflate(R.layout.fragment_product_info, container, false);
        etName = (EditText)view.findViewById(R.id.etProductName);
        etDescription = (EditText)view.findViewById(R.id.etProductDescription);
        etPrice = (EditText)view.findViewById(R.id.etProductPrice);
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
                listener.withInfo(product);
            }
        });

        //adding mock data for testing
        //addMockData();
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

    private void addMockData() {
        etName.setText("Tasty home grown oranges");
        etDescription.setText("Tasty home grown oranges. We have dozens of oranges");
        etPrice.setText("$123");
    }
}
