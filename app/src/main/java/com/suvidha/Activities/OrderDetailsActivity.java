package com.suvidha.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.suvidha.Adapters.CartAdapter;
import com.suvidha.Models.CartModel;
import com.suvidha.Models.ItemModel;
import com.suvidha.Models.OrderIdModel;
import com.suvidha.Models.OrderRequestModel;
import com.suvidha.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.suvidha.Utilities.APIClient;
import com.suvidha.Utilities.ApiInterface;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.suvidha.Utilities.Utils.APP_CHARGE;
import static com.suvidha.Utilities.Utils.DELIVERY_CHARGE;
import static com.suvidha.Utilities.Utils.createProgressDialog;
import static com.suvidha.Utilities.Utils.email;
import static com.suvidha.Utilities.Utils.getAccessToken;
import static com.suvidha.Utilities.Utils.rs;
import static com.suvidha.Utilities.Utils.statusHashMap;

public class OrderDetailsActivity extends AppCompatActivity implements View.OnClickListener, CartAdapter.CartCallback, SwipeRefreshLayout.OnRefreshListener {
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
    ArrayList<ItemModel> orderData=new ArrayList<>();
    private Toolbar toolbar;
    private boolean orderPlaced = true;
    private LinearLayout deliveryLayout;
    private ApiInterface apiInterface;
    private String oid;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView shop_name;
    private TextView shop_address;
    private TextView delivery_address;
    private TextView orderid;
    private ImageView phone,mail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        init();
        manageToolbar();
        intialiseRetrofit();
        data = getIntent().getParcelableExtra("data");
        oid = getIntent().getStringExtra("oid");
        phone.setOnClickListener(this);
        mail.setOnClickListener(this);
        getOrderDetails();
//        orderData = data.items;
        setBottomSheet();

        swipeRefreshLayout.setOnRefreshListener(this);
    }
    private void init() {
        toolbar = findViewById(R.id.default_toolbar);
        nestedScrollView = findViewById(R.id.bottom_sheet_layout);
        mBottomSheetBehaviour = BottomSheetBehavior.from(nestedScrollView);
        mBottomSheetBehaviour.setPeekHeight(0);
        orderStatus = findViewById(R.id.order_status);
        shop_name =findViewById(R.id.order_shop_name);
        shop_address = findViewById(R.id.order_shop_addr);
        delivery_address = findViewById(R.id.order_deliv_addr);
        orderid = findViewById(R.id.order_id);
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
        swipeRefreshLayout = findViewById(R.id.swipe_ref);
        phone = findViewById(R.id.order_shop_contact);
        mail = findViewById(R.id.dev_mail);


    }
    private void intialiseRetrofit() {
        apiInterface = APIClient.getApiClient().create(ApiInterface.class);
    }
    private void getOrderDetails() {
        Dialog dialog = createProgressDialog(this,"Please wait");
        dialog.show();
        OrderIdModel orderIdModel = new OrderIdModel(oid);
        Call<OrderRequestModel> orderRequestModelCall = apiInterface.getOrder(getAccessToken(this),orderIdModel);
        orderRequestModelCall.enqueue(new Callback<OrderRequestModel>() {
            @Override
            public void onResponse(Call<OrderRequestModel> call, Response<OrderRequestModel> response) {
                try {
                    if(response.body().status == 200) {
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Data is loaded", Toast.LENGTH_SHORT).show();
                        Log.e("LOL", String.valueOf(response.body().id.items.size()));
                        orderData.clear();
                        orderData.addAll(response.body().id.items);
                        setValues(response.body().id);
                        data = response.body().id;
                        cartAdapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);

                    }else{
                        Toast.makeText(getApplicationContext(),"Error loading your data",Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }catch (Exception e){
                    Log.e("LOL",e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<OrderRequestModel> call, Throwable t) {
                Log.e("LOL",t.getMessage());
                Toast.makeText(getApplicationContext(),"Error loading your data",Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    private void setValues(CartModel data) {
        shop_name.setText(data.shop_details.name);
        shop_address.setText(data.shop_details.address);
        delivery_address.setText(data.address);
        orderid.setText(oid);
        orderStatus.setText(statusHashMap.get(data.status));

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
        int id=v.getId();
        switch (id){
            case R.id.order_view_summary:{
                mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_EXPANDED);
                updatePrice();
                break;
            }
            case R.id.order_shop_contact:{
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+data.shop_details.phone));
                startActivity(intent);
                break;
            }
            case R.id.dev_mail:{
                Intent intent = new Intent (Intent.ACTION_VIEW , Uri.parse("mailto:" + email));
                intent.putExtra(Intent.EXTRA_TEXT, "OrderId : "+orderid);
                startActivity(intent);
                break;
            }
        }

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

    @Override
    public void onRefresh() {
        getOrderDetails();
    }
}
