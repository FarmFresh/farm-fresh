package com.farmfresh.farmfresh.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.farmfresh.farmfresh.R;
import com.farmfresh.farmfresh.adapter.ProductsAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by bhaskarjaiswal on 8/21/16.
 */
public class ViewHolderProductImage extends RecyclerView.ViewHolder  {

    @BindView(R.id.ivImage)
    ImageView ivImage;

    @BindView(R.id.tvName)
    TextView tvName;

    @BindView(R.id.tvPrice)
    TextView tvPrice;

    @BindView(R.id.tvDistance)
    TextView tvDistance;

    @BindView(R.id.rlProductImage)
    RelativeLayout rlProductImage;

    private ProductsAdapter.OnProductClickListener productClicked;

    public ViewHolderProductImage(final View itemView, final ProductsAdapter.OnProductClickListener productClicked) {
        super(itemView);
        ButterKnife.bind(this, itemView);

        this.productClicked = productClicked;

        rlProductImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (productClicked != null) {
                    productClicked.onProductClick(itemView, getLayoutPosition());
                }

            }
        });

    }

    public ImageView getIvImage() {
        return ivImage;
    }

    public TextView getTvName() {
        return tvName;
    }

    public TextView getTvPrice() {
        return tvPrice;
    }

    public TextView getTvDistance() {
        return tvDistance;
    }
}