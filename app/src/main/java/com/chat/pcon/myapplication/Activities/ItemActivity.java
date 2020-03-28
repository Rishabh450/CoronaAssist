package com.chat.pcon.myapplication.Activities;

import android.os.Bundle;

import com.chat.pcon.myapplication.Adapters.ItemAdapter;
import com.chat.pcon.myapplication.Models.GrocItemModel;
import com.chat.pcon.myapplication.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ItemActivity extends AppCompatActivity {
    private RecyclerView rView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);
        init();
        setRView();
    }

    private void setRView() {
        rView.setLayoutManager(new LinearLayoutManager(this));
        rView.setAdapter(new ItemAdapter(this,getList()));
    }

    private List<GrocItemModel> getList() {
        List<GrocItemModel> list = new ArrayList<>();
        for(int i=0;i<10;i++){
            GrocItemModel item = new GrocItemModel("1","Apple","1 kg",0,130,0);
            list.add(item);
        }
        return list;
    }

    private void init() {
        rView = findViewById(R.id.item_rview);
    }
}
