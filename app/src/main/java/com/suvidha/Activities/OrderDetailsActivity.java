package com.suvidha.Activities;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.suvidha.Adapters.CartAdapter;
import com.suvidha.Models.CartModel;
import com.suvidha.Models.GrocItemModel;
import com.suvidha.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.suvidha.Utilities.Utils.APP_CHARGE;
import static com.suvidha.Utilities.Utils.DELIVERY_CHARGE;
import static com.suvidha.Utilities.Utils.rs;

public class OrderDetailsActivity extends AppCompatActivity implements View.OnClickListener, CartAdapter.CartCallback {
    private BottomSheetBehavior mBottomSheetBehaviour;
    View nestedScrollView;
    private TextView orderSummary;
    private TextView cartTotal;
    private TextView delivery;
    private TextView app;
    private TextView grandTotal;
    private Button placeOrder;
    private TextView orderStatus;
    private CartAdapter cartAdapter;
    CartModel data;
    ArrayList<GrocItemModel> orderData;
    private Toolbar toolbar;
    private boolean orderPlaced = true;
    private LinearLayout deliveryLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        init();
        manageToolbar();
        data = getIntent().getParcelableExtra("data");
        orderData = data.items;
        setBottomSheet();
        setData();
    }

    private void setData() {
      try {
          Log.e("TAG","TAG"+data.address);
          orderStatus.setText(data.status);

      }catch (Exception e){
          e.printStackTrace();
      }
    }

    private void manageToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Order");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                handleBackPressed();
                break;

        }
        return super.onOptionsItemSelected(item);
    }


    private void setBottomSheet() {
        mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_HIDDEN);
        RecyclerView rView = nestedScrollView.findViewById(R.id.cart_rview);
        rView.setLayoutManager(new LinearLayoutManager(this));
        cartAdapter = new CartAdapter(this, orderData, orderPlaced);
        rView.setAdapter(cartAdapter);
    }

    private void init() {
        toolbar = findViewById(R.id.default_toolbar);
        nestedScrollView = findViewById(R.id.bottom_sheet_layout);
        mBottomSheetBehaviour = BottomSheetBehavior.from(nestedScrollView);
        mBottomSheetBehaviour.setPeekHeight(0);
        orderStatus = findViewById(R.id.details_order_status);
        cartTotal = nestedScrollView.findViewById(R.id.cart_cart_total);
        delivery = nestedScrollView.findViewById(R.id.cart_delivery);
        app = nestedScrollView.findViewById(R.id.cart_app);
        grandTotal = nestedScrollView.findViewById(R.id.cart_grand_total);
        placeOrder = nestedScrollView.findViewById(R.id.cart_place_order);
        deliveryLayout = nestedScrollView.findViewById(R.id.cart_delivery_layout);
        deliveryLayout.setVisibility(View.GONE);
        orderSummary = findViewById(R.id.order_view_summary);
        orderSummary.setOnClickListener(this);
        placeOrder.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        handleBackPressed();
    }

    private void handleBackPressed() {
        if (mBottomSheetBehaviour.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_HIDDEN);
        } else {
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_EXPANDED);
        updatePrice();
    }

    @Override
    public void closeBtmSheet() {

    }

    @Override
    public void updatePrice() {
        double totalPrice = getTotalWithoutTax();
        cartTotal.setText("\u20B9" + String.valueOf(totalPrice));
        delivery.setText(rs + String.valueOf(DELIVERY_CHARGE));
        app.setText(rs + String.valueOf((APP_CHARGE*totalPrice)/100));
        grandTotal.setText(String.valueOf(totalPrice + DELIVERY_CHARGE + (APP_CHARGE*totalPrice)/100));
    }

    @Override
    public void notifyItemAdapter(String id) {

    }

    private double getTotalWithoutTax() {
        double total = 0;
        for (int i = 0; i < orderData.size(); i++) {
            total += orderData.get(i).itemPrice * orderData.get(i).item_add_qty;
        }
        return total;
    }

    @Override
    public void hideGoto() {

    }
}
