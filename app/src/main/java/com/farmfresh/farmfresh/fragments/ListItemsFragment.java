package com.farmfresh.farmfresh.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.farmfresh.farmfresh.R;
import com.farmfresh.farmfresh.adapter.ProductsAdapter;
import com.farmfresh.farmfresh.models.Product;

import org.parceler.Parcels;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListItemsFragment extends Fragment {

    @BindView(R.id.rvProducts)
    RecyclerView rvProducts;

    ProductsAdapter productsAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        List<Product> productList = Parcels.unwrap(getArguments().getParcelable("productList"));
        productsAdapter = new ProductsAdapter(getActivity(),productList);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.product_listing, container, false);
        ButterKnife.bind(this, v);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        rvProducts.setLayoutManager(layoutManager);
        rvProducts.setAdapter(productsAdapter);

        return v;
    }
}
