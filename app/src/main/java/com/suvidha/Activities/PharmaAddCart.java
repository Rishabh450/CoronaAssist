package com.suvidha.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.suvidha.Adapters.MedicineListAdapter;
import com.suvidha.Adapters.ShopListAdapter;
import com.suvidha.Models.CartModel;
import com.suvidha.Models.GeneralModel;
import com.suvidha.Models.ItemModel;
import com.suvidha.Models.MedicineItem;
import com.suvidha.R;
import com.suvidha.Utilities.APIClient;
import com.suvidha.Utilities.ApiInterface;

import java.util.ArrayList;
import java.util.List;

import static com.suvidha.Utilities.Utils.APP_CHARGE;
import static com.suvidha.Utilities.Utils.DELIVERY_CHARGE;
import static com.suvidha.Utilities.Utils.createAlertDialog;
import static com.suvidha.Utilities.Utils.getAccessToken;
import static com.suvidha.Utilities.Utils.med_order_no;
import static com.suvidha.Utilities.Utils.order_address;

public class PharmaAddCart extends AppCompatActivity {
    Uri prescriptionUri;
    CardView feedcard,addPresc,placeorder;
    TextView itemcount;
    ImageView prescription;
     RecyclerView rView;
    FloatingActionButton additem;
    String shop_name,shopid;
    ArrayList<ItemModel> medicineItemList=new ArrayList<>();
    MedicineListAdapter medicineListAdapter;
    ApiInterface apiInterface;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pharma_add_cart);
        intialiseRetrofit();
        init();
        setuprec();
        addPresc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(PharmaAddCart.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(PharmaAddCart.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},0);


                }
                else
                    addImage();


            }
        });
        additem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PharmaAddCart.this);
                final View view = LayoutInflater.from(PharmaAddCart.this).inflate(R.layout.addpharmaitem, null);
                builder.setView(view);
                final Dialog dialog = builder.create();

                dialog.setContentView(R.layout.addpharmaitem);

                dialog.show();
                TextInputEditText medName=dialog.findViewById(R.id.medName);
                TextView minus=dialog.findViewById(R.id.cart_minus_btn);
                TextView plus=dialog.findViewById(R.id.cart_plus_btn);
                TextView cancelButton = (TextView) dialog.findViewById(R.id.cancel);
                TextView addButton=(TextView)dialog.findViewById(R.id.addItem);
                TextView quantity=(TextView)dialog.findViewById(R.id.med_add_qty);
                minus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int quan =Integer.parseInt(quantity.getText().toString());
                        if (quan>0)
                        {
                            String n= String.valueOf(quan-1);


                            quantity.setText(n);
                        }

                    }
                });
                plus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int quan =Integer.parseInt(quantity.getText().toString());
                        int newQ=quan+1;
                        String n= String.valueOf(newQ);
                        quantity.setText(n);



                    }
                });

                addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       if(medName.getText().toString().equals(""))
                           Toast.makeText(PharmaAddCart.this,"Enter Medicine Name",Toast.LENGTH_SHORT).show();
                       else if(quantity.getText().toString().equals("0"))
                       {
                           Toast.makeText(PharmaAddCart.this,"Quantity cannot be 0",Toast.LENGTH_SHORT).show();


                       }
                       else
                       {
                           med_order_no++ ;
                           ItemModel itemModel =new ItemModel(med_order_no,medName.getText().toString(),Integer.parseInt(quantity.getText().toString()));

                           medicineItemList.add(itemModel);
                           medicineListAdapter.notifyDataSetChanged();
                           dialog.dismiss();
                           Toast.makeText(PharmaAddCart.this,"Added",Toast.LENGTH_SHORT).show();
                           placeorder.setVisibility(View.VISIBLE);
                           itemcount.setText(String.valueOf( medicineItemList.size()));


                       }



                        }



                });
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });




            }
        });

        placeorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = createAlertDialog(PharmaAddCart.this, "Place Order", getResources().getString(R.string.place_order_msg), "cancel", "Continue");

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
                       // double grandTotal = cartHandler.getTotalWithoutTax() + DELIVERY_CHARGE + (APP_CHARGE * cartHandler.getTotalWithoutTax()) / 100;
                        CartModel cartModel = new CartModel(medicineItemList, shopid, 0, 0, order_address);
                        Call<GeneralModel> orderResultCall = apiInterface.pushOrder(getAccessToken(PharmaAddCart.this), cartModel);
                        orderResultCall.enqueue(new Callback<GeneralModel>() {
                            @Override
                            public void onResponse(Call<GeneralModel> call, Response<GeneralModel> response) {
                                if (response.body().status == 201) {
                                    Toast.makeText(PharmaAddCart.this, "Your Order successfully placed", Toast.LENGTH_SHORT).show();
                                    //open order description
                                    //clear activity stack
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);

                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    startActivity(intent);
                                    intent = new Intent(PharmaAddCart.this, OrderDetailsActivity.class);
                                    intent.putExtra("data", cartModel);
                                    intent.putExtra("oid", response.body().id);
                                    startActivity(intent);
                                    //remove items from cart
                                   // cartHandler.clearCart();
                                } else if (response.body().status == 404) {
                                    Toast.makeText(PharmaAddCart.this, "Sorry, shop do not exist anymore", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(PharmaAddCart.this, "Sorry, your request was unsuccessful", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                            }

                            @Override
                            public void onFailure(Call<GeneralModel> call, Throwable t) {
                                Log.e("TAG", "responseError " + t.getMessage());
                            }
                        });

                    }
                });


            }
        });
    }
    public void addImage()
    {

        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

        startActivityForResult(chooserIntent, 21);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
         if (resultCode == RESULT_OK&&requestCode==21) {

            prescriptionUri = data.getData();
            Toast.makeText(PharmaAddCart.this,"Prescription Added",Toast.LENGTH_SHORT).show();

            Log.d("successhuacrop","addhua"+prescriptionUri);

            String[] filePath = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(prescriptionUri, filePath, null, null, null);
            cursor.moveToFirst();
            String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);
            Drawable mDrawable = new BitmapDrawable(getResources(), bitmap);

            if(bitmap!=null) {
                prescription.setImageDrawable(mDrawable);
                feedcard.setVisibility(View.VISIBLE);
            }


            cursor.close();
        }
    }
    private void intialiseRetrofit() {
        apiInterface = APIClient.getApiClient().create(ApiInterface.class);
    }
    private void init()
    {
        feedcard=findViewById(R.id.feedcard);
        prescription=findViewById(R.id.prescription);
        addPresc=findViewById(R.id.addPresc);
        additem=findViewById(R.id.add_item);
        rView = findViewById(R.id.medicineList);
        Intent intent=getIntent();
        shop_name=intent.getStringExtra("shop_name");
        shopid=intent.getStringExtra("shopid");
        placeorder=findViewById(R.id.goto_cart_layout);
        itemcount=findViewById(R.id.item_count);
    }
    void setuprec() {
        rView.setLayoutManager(new LinearLayoutManager(PharmaAddCart.this));
        medicineListAdapter = new MedicineListAdapter(PharmaAddCart.this, medicineItemList);
        rView.setAdapter(medicineListAdapter);
    }
}
