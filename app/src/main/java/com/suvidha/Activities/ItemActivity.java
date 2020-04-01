package com.suvidha.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.suvidha.Adapters.CartAdapter;
import com.suvidha.Adapters.ItemAdapter;
import com.suvidha.Models.CartModel;
import com.suvidha.Models.GeneralModel;
import com.suvidha.Models.ItemModel;
import com.suvidha.R;
import com.suvidha.Utilities.APIClient;
import com.suvidha.Utilities.ApiInterface;
import com.suvidha.Utilities.CartHandler;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.suvidha.Utilities.SharedPrefManager;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.suvidha.Utilities.Utils.APP_CHARGE;
import static com.suvidha.Utilities.Utils.DELIVERY_CHARGE;
import static com.suvidha.Utilities.Utils.catHashMap;
import static com.suvidha.Utilities.Utils.createAlertDialog;
import static com.suvidha.Utilities.Utils.getAccessToken;
import static com.suvidha.Utilities.Utils.rs;
import static com.suvidha.Utilities.Utils.shopItems;

public class ItemActivity extends AppCompatActivity implements View.OnClickListener, ItemAdapter.Callback, CartAdapter.CartCallback {

    private RecyclerView rView;
    private Toolbar toolbar;
    private CardView goto_cart;
    private BottomSheetBehavior mBottomSheetBehaviour;
    private NestedScrollView scrollView;
    private boolean mScrollDown = true;
    private boolean isFirstTime = true;
    private CartHandler cartHandler;
    private boolean hideGotoCart = false;
    View nestedScrollView;
    private CartAdapter cartAdapter;
    private ItemAdapter itemAdapter;
    private List<ItemModel> cartData = new ArrayList<>();
    private List<ItemModel> items = new ArrayList<>();

    private TextView cartTotal;
    private TextView delivery;
    private TextView app;
    private TextView grandTotal;
    private Button placeOrder;
    private ApiInterface apiInterface;
    private int catId;
    private String shop_id;
    private String shop_name;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);
        init();
        catId = getIntent().getIntExtra("CategoryId",-1);
        shop_id = getIntent().getStringExtra("shopid");
        shop_name = getIntent().getStringExtra("shopname");
        intialiseRetrofit();
        manageToolbar();
        setRView();
        setBottomSheet();
        setGotoCart();
        hideGotoCart();
    }

    private void init() {
        toolbar = findViewById(R.id.default_toolbar);
        rView = findViewById(R.id.item_rview);
        scrollView = findViewById(R.id.nested_scroll_view);
        cartHandler = CartHandler.getInstance();
        nestedScrollView = findViewById(R.id.bottom_sheet_layout);
        mBottomSheetBehaviour = BottomSheetBehavior.from(nestedScrollView);
        mBottomSheetBehaviour.setPeekHeight(0);
        goto_cart = findViewById(R.id.goto_cart_layout);

        cartTotal = nestedScrollView.findViewById(R.id.cart_cart_total);
        delivery = nestedScrollView.findViewById(R.id.cart_delivery);
        app = nestedScrollView.findViewById(R.id.cart_app);
        grandTotal = nestedScrollView.findViewById(R.id.cart_grand_total);
        placeOrder = nestedScrollView.findViewById(R.id.cart_place_order);
        placeOrder.setOnClickListener(this);
    }
    private void intialiseRetrofit() {
        apiInterface = APIClient.getApiClient().create(ApiInterface.class);
    }
    private void setGotoCart() {
        //set goto cart
        if (cartHandler.getItemsCount() == 0) {

            goto_cart.setVisibility(View.INVISIBLE);
            hideGotoCart = true;
        } else {
            goto_cart.setVisibility(View.VISIBLE);
            hideGotoCart = false;
            updateGotoCart();
        }

    }

    private void hideGotoCart() {
        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (!hideGotoCart) {
                    Log.e("LOL", String.valueOf(hideGotoCart));
                    if (v.getChildAt(v.getChildCount() - 1) != null) {
                        if (!isFirstTime) {
                            if (scrollY > oldScrollY) {
                                //code to fetch more data for endless scrolling
                                if (mScrollDown) {
//                            Toast.makeText(ojassApplication, "SCroll down", Toast.LENGTH_SHORT).show();
                                    TranslateAnimation tr = new TranslateAnimation(0.0f, 0.0f, 0, 100);
                                    tr.setDuration(100);
                                    goto_cart.startAnimation(tr);
                                    mScrollDown = false;
                                    goto_cart.setVisibility(View.INVISIBLE);
                                }
                            } else if (scrollY < oldScrollY) {
                                if (!mScrollDown) {
//                            Toast.makeText(ojassApplication, "SCroll up", Toast.LENGTH_SHORT).show();
                                    goto_cart.setVisibility(View.VISIBLE);
                                    TranslateAnimation tr = new TranslateAnimation(0.0f, 0.0f, 100, 0);
                                    tr.setDuration(100);
                                    goto_cart.startAnimation(tr);
                                    mScrollDown = true;

                                }
                            }

                        } else {
                            isFirstTime = false;
                        }
                    }
                }
            }
        });
    }

    @Override
    public void updateGotoCart() {

        TextView itemCount = goto_cart.findViewById(R.id.item_count);
        TextView itemsPrice = goto_cart.findViewById(R.id.items_price);
        if (cartHandler.getItemsCount() == 0) {
            goto_cart.setVisibility(View.INVISIBLE);
            hideGotoCart = true;
        } else {
            goto_cart.setVisibility(View.VISIBLE);
            hideGotoCart = false;
        }
        isFirstTime = false;
        itemCount.setText(String.valueOf(cartHandler.getItemsCount()) + " Item(s)");
        itemsPrice.setText("Rs. " + String.valueOf(cartHandler.getTotalWithoutTax()) + " plus taxes");
    }

    private void setBottomSheet() {
        mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_HIDDEN);
        mBottomSheetBehaviour.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                String state = "";

                switch (newState) {

                    case BottomSheetBehavior.STATE_SETTLING: {
                        state = "SETTLING";
                        //modify items
                        List<ItemModel> newList = cartHandler.getListInCart();
                        for (int i = 0; i < items.size(); i++) {
                            for (int j = 0; j < newList.size(); j++)
                                if (items.get(i).itemId.compareTo(newList.get(j).itemId) == 0) {
                                    items.set(i, newList.get(j));
                                    itemAdapter.notifyItemChanged(i);
                                    break;
                                }
                        }
//                        Toast.makeText(getApplicationContext(), "Bottom Sheet State Changed to: " + state, Toast.LENGTH_SHORT).show();
                        break;
                    }
                }


            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
        goto_cart.setOnClickListener(this);
        cartData = cartHandler.getListInCart();
        RecyclerView rView = nestedScrollView.findViewById(R.id.cart_rview);
        rView.setLayoutManager(new LinearLayoutManager(this));
        cartAdapter = new CartAdapter(this, cartData,false);
        rView.setAdapter(cartAdapter);
    }

    private void setRView() {
        rView.setLayoutManager(new LinearLayoutManager(this));
        items = getData();
        itemAdapter = new ItemAdapter(this, items);
        rView.setAdapter(itemAdapter);

    }
    private List<ItemModel> getData() {
        List<ItemModel> l=new ArrayList<>();
        for(int i=0;i<shopItems.size();i++){
            if(shopItems.get(i).category==catId){
                l.add(shopItems.get(i));
            }
        }
        return l;
    }
    private List<ItemModel> getList() {
        List<ItemModel> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ItemModel item = new ItemModel(String.valueOf(i), "Apple", "1 kg", 0, 130, 0);
            list.add(item);
        }
        return list;
    }


    void manageToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if(catId == -1){
            getSupportActionBar().setTitle(shop_name);
        }else
            getSupportActionBar().setTitle(catHashMap.get(catId+1).first);
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
        switch (v.getId()) {
            case R.id.goto_cart_layout:
                mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_EXPANDED);
                updatePrice();
                cartData = cartHandler.getListInCart();
                cartAdapter.notifyDataSetChanged();
                break;
            case R.id.cart_place_order: {
                //submit order to server
                //show dialog
                Dialog dialog = createAlertDialog(this,"Place Order",getResources().getString(R.string.place_order_msg),"cancel","Continue");

                dialog.findViewById(R.id.dialog_cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.findViewById(R.id.dialog_continue).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //store order in cartModel
                        double grandTotal = cartHandler.getTotalWithoutTax()+DELIVERY_CHARGE+(APP_CHARGE*cartHandler.getTotalWithoutTax())/100;
                        String userAddress = SharedPrefManager.getInstance(getApplicationContext()).getString(SharedPrefManager.Key.USER_ADDRESS);
                        CartModel cartModel = new CartModel(cartHandler.getListInCart(),shop_id,grandTotal,0,
                                new Timestamp(System.currentTimeMillis()),userAddress);
                        Call<GeneralModel> orderResultCall = apiInterface.pushOrder(getAccessToken(ItemActivity.this),cartModel);
                        orderResultCall.enqueue(new Callback<GeneralModel>() {
                            @Override
                            public void onResponse(Call<GeneralModel> call, Response<GeneralModel> response) {
                                if(response.body().status == 201){
                                    Toast.makeText(ItemActivity.this, "Your Order successfully placed", Toast.LENGTH_SHORT).show();
                                    //open order description
                                    //clear activity stack
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);

                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |  Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    startActivity(intent);
                                    intent = new Intent(ItemActivity.this,OrderDetailsActivity.class);
                                    intent.putExtra("data", cartModel);
                                    startActivity(intent);
                                    //remove items from cart
                                    cartHandler.clearCart();
                                }else if(response.body().status == 404){
                                    Toast.makeText(ItemActivity.this, "Sorry, shop do not exist anymore", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }else {
                                    Toast.makeText(ItemActivity.this, "Sorry, your request was unsuccessful", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                            }

                            @Override
                            public void onFailure(Call<GeneralModel> call, Throwable t) {
                                Log.e("TAG","responseError "+t.getMessage());
                            }
                        });

                    }
                });

                break;
            }
        }
    }


    @Override
    public void closeBtmSheet() {
        mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_HIDDEN);
        List<ItemModel> newList = cartHandler.getListInCart();
        for (int i = 0; i < items.size(); i++) {
            for (int j = 0; j < newList.size(); j++)
                if (items.get(i).itemId.compareTo(newList.get(j).itemId) == 0) {
                    items.set(i, newList.get(j));
                    itemAdapter.notifyItemChanged(i);
                    Toast.makeText(this, "LOL", Toast.LENGTH_SHORT).show();
                    break;
                }
        }
    }

    @Override
    public void updatePrice() {
        double totalPrice = cartHandler.getTotalWithoutTax();
        cartTotal.setText("\u20B9" + String.valueOf(totalPrice));
        delivery.setText(rs + String.valueOf(DELIVERY_CHARGE));
        app.setText(rs + String.valueOf((APP_CHARGE*totalPrice)/100));
        grandTotal.setText(String.valueOf(totalPrice + DELIVERY_CHARGE + (APP_CHARGE*totalPrice)/100));
    }

    @Override
    public void notifyItemAdapter(String id) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).itemId.compareTo(id) == 0) {
                items.get(i).item_add_qty = 0;
                itemAdapter.notifyItemChanged(i);
                return;
            }
        }

    }

    @Override
    public void hideGoto() {
        hideGotoCart = true;
        goto_cart.setVisibility(View.INVISIBLE);
    }
}
