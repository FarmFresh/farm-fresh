package com.farmfresh.farmfresh.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.farmfresh.farmfresh.R;
import com.farmfresh.farmfresh.activities.ProductDetailActivity;
import com.farmfresh.farmfresh.adapter.ProductsAdapter;
import com.farmfresh.farmfresh.decorator.SpacesItemDecoration;
import com.farmfresh.farmfresh.models.Product;
import com.farmfresh.farmfresh.utils.Constants;

import org.parceler.Parcels;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

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

    public void notifyListUpdate(List<Product> productList){
        this.productList = productList;
        productsAdapter.notifyDataSetChanged();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.product_listing, container, false);
        ButterKnife.bind(this, v);

        rvProducts.setItemAnimator(new SlideInUpAnimator());

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        gridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);

        SpacesItemDecoration decoration = new SpacesItemDecoration(16);
        rvProducts.addItemDecoration(decoration);

        rvProducts.setLayoutManager(gridLayoutManager);
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
