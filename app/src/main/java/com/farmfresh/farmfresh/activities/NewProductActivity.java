package com.farmfresh.farmfresh.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.farmfresh.farmfresh.R;
import com.farmfresh.farmfresh.fragments.ProductInfoFragment;
import com.farmfresh.farmfresh.models.Product;

public class NewProductActivity extends AppCompatActivity {

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
        ProductInfoFragment profileHeaderFragment = new ProductInfoFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.flNewProduct, profileHeaderFragment)
                .commit();

        Button mBtnCancel = (Button)findViewById(R.id.btnCancel);
        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewProductActivity.this.finish();
            }
        });
    }
}
