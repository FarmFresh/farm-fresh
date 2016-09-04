package com.farmfresh.farmfresh.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.farmfresh.farmfresh.R;
import com.farmfresh.farmfresh.activities.ProductDetailActivity;
import com.farmfresh.farmfresh.adapter.ProductsAdapter;
import com.farmfresh.farmfresh.models.Product;
import com.farmfresh.farmfresh.utils.Constants;

import org.parceler.Parcels;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListItemsFragment extends Fragment {

    @BindView(R.id.rvProducts)
    RecyclerView rvProducts;

    ProductsAdapter productsAdapter;
    List<Product> productList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        productList = Parcels.unwrap(getArguments().getParcelable("productList"));
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

        productsAdapter.setOnOnProductClickListener(new ProductsAdapter.OnProductClickListener(){
            @Override
            public void onProductClick(View itemView, int position) {
//                Toast.makeText(getActivity(),productList.get(position).getDescription()+"",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getActivity(),ProductDetailActivity.class);
                intent.putExtra(Constants.PRODUCT_KEY,productList.get(position).getId());
                startActivity(intent);
            }
        });

        return v;
    }
}
