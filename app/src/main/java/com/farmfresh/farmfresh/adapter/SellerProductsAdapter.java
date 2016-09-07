package com.farmfresh.farmfresh.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.farmfresh.farmfresh.R;
import com.farmfresh.farmfresh.models.Product;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by lucerne on 9/3/16.
 */
// Create the basic adapter extending from RecyclerView.Adapter
// Note that we specify the custom ViewHolder which gives us access to our views
public class SellerProductsAdapter extends
        RecyclerView.Adapter<SellerProductsAdapter.ViewHolder> {

    // Store a member variable for the products
    private List<Product> mProducts;
    // Store the context for easy access
    private Context mContext;

    // Pass in the product array into the constructor
    public SellerProductsAdapter(Context context, List<Product> products) {
        mProducts = products;
        mContext = context;
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return mContext;
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView tvName;
        public ImageView ivProductImage;
        public TextView tvPrice;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            tvName = (TextView) itemView.findViewById(R.id.tvName);
            ivProductImage = (ImageView) itemView.findViewById(R.id.ivProductImage);
            tvPrice = (TextView) itemView.findViewById(R.id.tvPrice);
        }
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public SellerProductsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View productView = inflater.inflate(R.layout.item_product, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(productView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(SellerProductsAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        Product product = mProducts.get(position);

        // Set item views based on your views and data model
        TextView tvName = viewHolder.tvName;
        TextView tvPrice = viewHolder.tvPrice;
        ImageView ivProductImage = viewHolder.ivProductImage;
        tvName.setText(product.getName());
        tvPrice.setText(product.getPrice());

        if (product.getImageUrls() != null && product.getImageUrls().size() > 0) {
            Picasso.with(mContext)
                    .load(product.getImageUrls().get(0))
                    .into(ivProductImage);
        }

        ivProductImage.setTag(product);
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mProducts.size();
    }

}
