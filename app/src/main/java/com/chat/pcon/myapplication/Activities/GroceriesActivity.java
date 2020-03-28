package com.chat.pcon.myapplication.Activities;

import android.os.Bundle;

import com.chat.pcon.myapplication.Adapters.CategoryAdapter;
import com.chat.pcon.myapplication.Adapters.ShopListAdapter;
import com.chat.pcon.myapplication.R;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class GroceriesActivity extends AppCompatActivity {
    private RecyclerView rView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groceries);
        init();
        setuprec();
    }
    private void init() {
        rView = findViewById(R.id.groc_cat_rview);
    }
    void setuprec(){
        rView.setLayoutManager(new LinearLayoutManager(this));
        rView.setAdapter(new ShopListAdapter(this,null));
    }

}
