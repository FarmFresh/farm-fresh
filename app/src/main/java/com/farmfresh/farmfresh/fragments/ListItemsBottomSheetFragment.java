package com.farmfresh.farmfresh.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.farmfresh.farmfresh.R;
import com.farmfresh.farmfresh.activities.ProductDetailActivity;
import com.farmfresh.farmfresh.adapter.ProductsAdapter;
import com.farmfresh.farmfresh.models.Product;

import org.parceler.Parcels;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by bhaskarjaiswal on 8/21/16.
 */
public class ListItemsBottomSheetFragment extends BottomSheetDialogFragment {

    @BindView(R.id.rvProducts)
    RecyclerView rvProducts;

    List<Product> productList;

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);

        productList = Parcels.unwrap(getArguments().getParcelable("productList"));

        View contentView = View.inflate(getContext(), R.layout.product_listing, null);
        dialog.setContentView(contentView);

        ButterKnife.bind(this,contentView);

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        ProductsAdapter productsAdapter = new ProductsAdapter(getContext(),productList);

        rvProducts.setLayoutManager(layoutManager);
        rvProducts.setAdapter(productsAdapter);

        productsAdapter.setOnOnProductClickListener(new ProductsAdapter.OnProductClickListener(){
            @Override
            public void onProductClick(View itemView, int position) {
                startActivity(new Intent(getActivity(), ProductDetailActivity.class));
            }
        });

        if( behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }
    }
}
