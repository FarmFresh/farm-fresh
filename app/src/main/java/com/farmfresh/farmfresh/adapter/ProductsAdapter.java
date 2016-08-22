package com.farmfresh.farmfresh.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.farmfresh.farmfresh.R;
import com.farmfresh.farmfresh.utils.Constants;
import com.farmfresh.farmfresh.models.Product;
import com.farmfresh.farmfresh.viewholder.ViewHolderProductImage;
import com.farmfresh.farmfresh.viewholder.ViewHolderProductOnly;

import java.util.List;

/**
 * Created by bhaskarjaiswal on 8/21/16.
 */
public class ProductsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Product> productList;
    private Context context;
    private OnProductClickListener onProductClicked;

    public interface OnProductClickListener {
        void onProductClick(View itemView, int position);
    }

    public void setOnOnProductClickListener(OnProductClickListener onProductClicked) {
        this.onProductClicked = onProductClicked;
    }

    public ProductsAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewholder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case Constants.PRODUCT_ONLY:
                View viewProductOnly = inflater.inflate(R.layout.product_only, parent, false);
                viewholder = new ViewHolderProductOnly(viewProductOnly, onProductClicked);
                break;
            case Constants.PRODUCT_IMAGE:
                View viewProductImage = inflater.inflate(R.layout.product_image, parent, false);
                viewholder = new ViewHolderProductImage(viewProductImage, onProductClicked);
                break;
            default:
                View viewProductDefault = inflater.inflate(R.layout.product_only, parent, false);
                viewholder = new ViewHolderProductOnly(viewProductDefault, onProductClicked);
                break;
        }

        return viewholder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch (viewHolder.getItemViewType()) {
            case Constants.PRODUCT_ONLY:
                configureViewHolderProductDescription((ViewHolderProductOnly) viewHolder, position);
                break;
            case Constants.PRODUCT_IMAGE:
                configureViewHolderProductImage((ViewHolderProductImage) viewHolder, position);
                break;
        }
    }

    private void configureViewHolderProductDescription(final ViewHolderProductOnly viewProduct, int position) {
        Product product = (Product) productList.get(position);

        viewProduct.getTvName().setText(product.getName());
        viewProduct.getTvDescription().setText(product.getDescription());
        viewProduct.getTvPrice().setText(product.getPrice());

    }

    private void configureViewHolderProductImage(final ViewHolderProductImage viewProduct, int position) {

    }

    @Override
    public int getItemViewType(int position) {
        Product product = (Product) productList.get(position);

        return Constants.PRODUCT_ONLY;
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }
}
