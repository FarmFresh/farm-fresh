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
public class ViewHolderProductOnly extends RecyclerView.ViewHolder  {

    @BindView(R.id.tvName)
    TextView tvName;

    @BindView(R.id.tvDescription)
    TextView tvDescription;

    @BindView(R.id.tvPrice)
    TextView tvPrice;

    private ProductsAdapter.OnProductClickListener productClicked;

    public ViewHolderProductOnly(final View itemView, final ProductsAdapter.OnProductClickListener productClicked) {
        super(itemView);
        ButterKnife.bind(this, itemView);

        this.productClicked = productClicked;

        tvDescription.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (productClicked != null) {
                    productClicked.onProductClick(itemView, getLayoutPosition());
                }

            }
        });
    }

    public TextView getTvDescription() {
        return tvDescription;
    }

    public TextView getTvName() {
        return tvName;
    }

    public TextView getTvPrice() {
        return tvPrice;
    }
}
