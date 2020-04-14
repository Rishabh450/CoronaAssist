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
import com.suvidha.Adapters.CategoryAdapter;
import com.suvidha.Models.CartModel;
import com.suvidha.Models.GeneralModel;
import com.suvidha.Models.ItemModel;
import com.suvidha.Models.ItemsRequestModel;
import com.suvidha.Models.SidModel;
import com.suvidha.R;
import com.suvidha.Utilities.APIClient;
import com.suvidha.Utilities.ApiInterface;
import com.suvidha.Utilities.CartHandler;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.suvidha.Utilities.SharedPrefManager;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.suvidha.Utilities.Utils.APP_CHARGE;
import static com.suvidha.Utilities.Utils.DELIVERY_CHARGE;
import static com.suvidha.Utilities.Utils.createAlertDialog;
import static com.suvidha.Utilities.Utils.getAccessToken;
import static com.suvidha.Utilities.Utils.rs;
import static com.suvidha.Utilities.Utils.shopItems;
import static com.suvidha.Utilities.Utils.shop_id;

public class CategoriesActivity extends AppCompatActivity implements View.OnClickListener, CartAdapter.CartCallback {
    private static final int ITEM_COUNT = 3;
    private static final String TAG = "CategoriesActivity";
    private RecyclerView rView;
    private Toolbar toolbar;
    private CardView goto_cart;
    private BottomSheetBehavior mBottomSheetBehaviour;
    private NestedScrollView scrollView;
    private boolean mScrollDown = true;
    private boolean isFirstTime = true;
    private boolean hideGotoCart = false;
    private CartHandler cartHandler;
    private View nestedScrollView;
    private CartAdapter cartAdapter;
    private List<ItemModel> cartData = new ArrayList<>();
    private TextView cartTotal;
    private TextView delivery;
    private TextView app;
    private TextView grandTotal;
    private Button placeOrder;
    private ApiInterface apiInterface;
    private String shop_name;
    public CategoryAdapter mAdapter;
    public List<Integer> categoryData = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        init();
        shop_id = getIntent().getStringExtra("shopid");
        shop_name = getIntent().getStringExtra("shopname");
        intialiseRetrofit();
        getItems();
        manageToolbar();
        setuprec();
        setBottomSheet();
        setGotoCart();
        hideGotoCart();
        updateGotoCart();
    }

    private void getItems() {
        Call<ItemsRequestModel> itemModelCall = apiInterface.getItems(getAccessToken(this),new SidModel(shop_id));
        itemModelCall.enqueue(new Callback<ItemsRequestModel>() {
            @Override
            public void onResponse(Call<ItemsRequestModel> call, Response<ItemsRequestModel> response) {
                Log.e(TAG, String.valueOf(response.body()));
                shopItems.clear();
                shopItems.addAll(response.body().id);
                categoryData.clear();
                categoryData.addAll(getAllDifferentCategories());
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<ItemsRequestModel> call, Throwable t) {
                Log.e(TAG,t.getMessage());
            }
        });
    }

    private void init() {
        toolbar = findViewById(R.id.default_toolbar);
        rView = findViewById(R.id.shop_cat_rview);
        scrollView = findViewById(R.id.nested_scroll_view);
        nestedScrollView = findViewById(R.id.bottom_sheet_layout);
        mBottomSheetBehaviour = BottomSheetBehavior.from(nestedScrollView);
        mBottomSheetBehaviour.setPeekHeight(0);
        goto_cart = findViewById(R.id.goto_cart_layout);
        cartHandler = CartHandler.getInstance();

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
    @Override
    public void onBackPressed() {
        handleBackPress();
    }

    private void setGotoCart() {
        //set goto cart
//        Log.e("LOL","wth");
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

    private void updateGotoCart() {
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
        goto_cart.setOnClickListener(this);
        cartData = cartHandler.getListInCart();
        RecyclerView rView = nestedScrollView.findViewById(R.id.cart_rview);
        rView.setLayoutManager(new LinearLayoutManager(this));
        cartAdapter = new CartAdapter(this, cartData, false);
        rView.setAdapter(cartAdapter);
    }

    void manageToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(shop_name);
    }

    void setuprec() {
        shopItems.clear();
        mAdapter = new CategoryAdapter(this, categoryData);
        rView.setLayoutManager(new GridLayoutManager(this, ITEM_COUNT));
        rView.setAdapter(mAdapter);
    }
    private List<Integer> getAllDifferentCategories() {
        List<Integer> l =new ArrayList<>();
        for(int i=0;i<shopItems.size();i++){
            if(!l.contains(shopItems.get(i).category)){
                l.add(shopItems.get(i).category);
            }
        }
        return l;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                handleBackPress();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void handleBackPress() {
        if (mBottomSheetBehaviour.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_HIDDEN);
        } else {
                if (cartHandler.getListInCart().isEmpty()) {
                    finish();
                } else {
                    //open alert dialog
                    Dialog dialog = createAlertDialog(this, "Warning", getResources().getString(R.string.warning_cart_not_empty),
                            "Cancel", "Continue");
                    dialog.findViewById(R.id.dialog_cancel).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    dialog.findViewById(R.id.dialog_continue).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            cartHandler.clearCart();
                            dialog.dismiss();
                            finish();
                        }
                    });
                }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.goto_cart_layout:
                mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_EXPANDED);
                mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_EXPANDED);
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
                        Call<GeneralModel> orderResultCall = apiInterface.pushOrder(getAccessToken(CategoriesActivity.this),cartModel);
                        orderResultCall.enqueue(new Callback<GeneralModel>() {
                            @Override
                            public void onResponse(Call<GeneralModel> call, Response<GeneralModel> response) {
                                if(response.body().status == 201){
                                    Toast.makeText(CategoriesActivity.this, "Your Order successfully placed", Toast.LENGTH_SHORT).show();
                                    //open order description
                                    //clear activity stack
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);

                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |  Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    startActivity(intent);
                                    intent = new Intent(CategoriesActivity.this,OrderDetailsActivity.class);
                                    intent.putExtra("data", cartModel);
                                    intent.putExtra("oid",response.body().id);
                                    startActivity(intent);
                                    //remove items from cart
                                    cartHandler.clearCart();
                                }else if(response.body().status == 404){
                                    Toast.makeText(CategoriesActivity.this, "Sorry, shop do not exist anymore", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }else {
                                    Toast.makeText(CategoriesActivity.this, "Sorry, your request was unsuccessful", Toast.LENGTH_SHORT).show();
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
    protected void onStart() {
        super.onStart();
        setGotoCart();
        updatePrice();
    }

    @Override
    public void closeBtmSheet() {
        updateGotoCart();
        mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    @Override
    public void updatePrice() {
        updateGotoCart();
        double totalPrice = cartHandler.getTotalWithoutTax();
        cartTotal.setText("\u20B9" + String.valueOf(cartHandler.getTotalWithoutTax()));
        delivery.setText(rs + String.valueOf(DELIVERY_CHARGE));
        app.setText(rs + String.valueOf((APP_CHARGE*totalPrice)/100));
        grandTotal.setText(String.valueOf(totalPrice + DELIVERY_CHARGE + (APP_CHARGE*totalPrice)/100));
    }

    @Override
    public void notifyItemAdapter(String id) {
        updateGotoCart();
    }

    @Override
    public void hideGoto() {
        hideGotoCart = true;
        goto_cart.setVisibility(View.INVISIBLE);
    }

}
