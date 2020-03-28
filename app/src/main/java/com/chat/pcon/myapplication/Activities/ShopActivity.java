package com.chat.pcon.myapplication.Activities;

import android.os.Bundle;

import com.chat.pcon.myapplication.Adapters.CategoryAdapter;
import com.chat.pcon.myapplication.R;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ShopActivity extends AppCompatActivity {
    private static final int ITEM_COUNT = 3;
    private RecyclerView rView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
        init();
        setuprec();

    }
        private void init() {
            rView = findViewById(R.id.shop_cat_rview);
        }
        void setuprec(){
            rView.setLayoutManager(new GridLayoutManager(this,ITEM_COUNT));
            rView.setAdapter(new CategoryAdapter(this,null));
        }
}
