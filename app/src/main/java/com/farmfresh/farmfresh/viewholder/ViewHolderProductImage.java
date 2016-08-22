package com.farmfresh.farmfresh.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.farmfresh.farmfresh.R;
import com.farmfresh.farmfresh.adapter.ProductsAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by bhaskarjaiswal on 8/21/16.
 */
public class ViewHolderProductImage extends RecyclerView.ViewHolder  {

    @BindView(R.id.tvDescription)
    TextView tvDescription;

    private ProductsAdapter.OnProductClickListener productClicked;

    public ViewHolderProductImage(final View itemView, final ProductsAdapter.OnProductClickListener productClicked) {
        super(itemView);
        ButterKnife.bind(this, itemView);

        this.productClicked = productClicked;
    }

    public TextView getTvDescription() {
        return tvDescription;
    }
}