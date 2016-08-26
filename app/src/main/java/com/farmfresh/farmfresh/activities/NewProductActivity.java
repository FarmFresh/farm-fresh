package com.farmfresh.farmfresh.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.farmfresh.farmfresh.R;
import com.farmfresh.farmfresh.fragments.ProductImagesFragment;
import com.farmfresh.farmfresh.fragments.ProductInfoFragment;
import com.farmfresh.farmfresh.models.Product;

public class NewProductActivity extends AppCompatActivity implements
        ProductInfoFragment.OnSubmitProductInfoListener, ProductImagesFragment.OnSubmitProductImagesListener{

    private Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_product);
        Toolbar toolbar = (Toolbar)findViewById(R.id.productToolbar);
        setSupportActionBar(toolbar);
        //Display icon in the toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("New Product");
        this.product = new Product();
        ProductInfoFragment profileHeaderFragment = ProductInfoFragment.newInstance(product);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.flNewProduct, profileHeaderFragment)
                .commit();
        Button mBtnToolbar = (Button)findViewById(R.id.btnToolBar);
        mBtnToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewProductActivity.this.finish();
            }
        });
    }

    @Override
    public void withInfo(Product product) {
        this.product = product;
        //load product images fragment
        ProductImagesFragment productImagesFragment = ProductImagesFragment.newInstance(product);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.flNewProduct, productImagesFragment)
                .commit();
    }

    @Override
    public void withImages(Product product) {
        this.product = product;
    }
}
