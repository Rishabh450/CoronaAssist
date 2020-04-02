package com.suvidha.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.suvidha.Activities.ShopsActivity;
import com.suvidha.Activities.MyPassActivity;
import com.suvidha.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import static com.suvidha.Utilities.Utils.shopTypesMap;

public class HomeFragment extends Fragment implements View.OnClickListener {
    private Button iconGroceries;
    private Button iconRequestPass;
    private Button iconMilk;
    private Button iconBread;
    private Button iconGas;
    private Button iconWater;

    public HomeFragment() {

    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home,container,false);
        init(v);
        setListeners();
        return v;
    }
    private void init(View v) {
        iconGroceries = v.findViewById(R.id.icon_groceries);
        iconRequestPass = v.findViewById(R.id.icon_request_passes);
        iconMilk = v.findViewById(R.id.icon_milk_and_dairy);
        iconBread = v.findViewById(R.id.icon_bread);
        iconGas = v.findViewById(R.id.icon_gas);
        iconWater = v.findViewById(R.id.icon_water);
    }

    private void setListeners() {
        iconGroceries.setOnClickListener(this);
        iconRequestPass.setOnClickListener(this);
        iconMilk.setOnClickListener(this);
        iconBread.setOnClickListener(this);
        iconGas.setOnClickListener(this);
        iconWater.setOnClickListener(this);

    }



    @Override
    public void onClick(View v) {
        int itemId = v.getId();
        Intent intent = null;
        switch (itemId){
            case R.id.icon_request_passes:
                intent = new Intent(getContext(), MyPassActivity.class);
                startActivity(intent);
                break;
            default:
                Button b = (Button)v;
                intent = new Intent(getContext(), ShopsActivity.class);
                intent.putExtra("type",shopTypesMap.get(itemId));
                intent.putExtra("type_name",b.getText().toString());
                startActivity(intent);

        }
    }
}
