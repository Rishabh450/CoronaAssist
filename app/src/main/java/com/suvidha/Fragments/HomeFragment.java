package com.suvidha.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.suvidha.Activities.GroceriesActivity;
import com.suvidha.Activities.MedicineActivity;
import com.suvidha.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment implements View.OnClickListener {
    private Button iconGroceries;
    private Button iconMedicines;
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

    }

    private void setListeners() {
        iconGroceries.setOnClickListener(this);
        iconMedicines.setOnClickListener(this);
    }



    @Override
    public void onClick(View v) {
        int itemId = v.getId();
        Intent intent = null;
        switch (itemId){
            case R.id.icon_groceries:
                intent = new Intent(getContext(), GroceriesActivity.class);
                startActivity(intent);
                break;
//            case R.id.icon_medicine:
//                intent = new Intent(getContext(), MedicineActivity.class);
//                startActivity(intent);
//                break;


        }
    }
}
