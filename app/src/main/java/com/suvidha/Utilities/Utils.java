package com.suvidha.Utilities;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.suvidha.Models.CartModel;
import com.suvidha.Models.GrocItemModel;
import com.suvidha.Models.ShopTypesModel;
import com.suvidha.Models.UserModel;
import com.suvidha.Models.ZonesModel;
import com.suvidha.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Utils {
    public static final String rs = "\u20B9";

//    public static final String BASE_URL = "http://192.168.43.114:5000";
//    public static final String BASE_URL = "http://192.168.43.55:5000";
    public static final String BASE_URL = "http://202.56.13.210:5000";
    public static double DELIVERY_CHARGE = 5;
    public static double APP_CHARGE = 2;
    public static List<ZonesModel> zonesList=new ArrayList<>();
    public static List<GrocItemModel> shopItems=new ArrayList<>();
    public static List<CartModel> allOrders = new ArrayList<>();
    public static Integer currentType;
    public static int local_zone_name = 0;
    public static HashMap<Integer,String> statusHashMap = new HashMap<Integer, String>(){{

    }};
    public static HashMap<Integer,Integer> shopTypesMap = new HashMap<Integer, Integer>(){{
        put(R.id.icon_request_passes,0);
        put(R.id.icon_groceries,1);
        put(R.id.icon_milk_and_dairy,2);
        put(R.id.icon_bread,3);
        put(R.id.icon_gas,4);
        put(R.id.icon_water,5);
    }};

    public static HashMap<Integer,String> orderStatus= new HashMap<Integer,String>(){{
     put(0,"Pending");
     put(1,"Order Accepted");
     put(2,"Out for delivery");
    }};
    public static Dialog createAlertDialog(Context context,String head,String msg,String b1,String b2){
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_alert);
        TextView headtv = dialog.findViewById(R.id.dialog_head);
        TextView msgtv = dialog.findViewById(R.id.dialog_msg);
        Button cancelbtn = dialog.findViewById(R.id.dialog_cancel);
        Button contbtn = dialog.findViewById(R.id.dialog_continue);
        if(b1.compareTo("")==0)
            cancelbtn.setVisibility(View.GONE);
        headtv.setText(head);
        msgtv.setText(msg);
        cancelbtn.setText(b1);
        contbtn.setText(b2);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.show();
        dialog.getWindow().setAttributes(lp);
        dialog.show();
        return dialog;
    }
    public static String getAccessToken(Context ctx){
        return SharedPrefManager.getInstance(ctx).getToken();
    }
    public static boolean isLoggedIn(Context ctx){
        return SharedPrefManager.getInstance(ctx).isLoggedIn();
    }
    public static void setLoginSession(UserModel user, Context context,int zoneId){
        SharedPrefManager sharedPrefManager;
        sharedPrefManager = SharedPrefManager.getInstance(context);
        sharedPrefManager.put(SharedPrefManager.Key.LOGIN_STATUS, true);
        sharedPrefManager.put(SharedPrefManager.Key.ZONE_KEY,zoneId);
        sharedPrefManager.put(SharedPrefManager.Key.USER_NAME, user.getName());
        sharedPrefManager.put(SharedPrefManager.Key.USER_EMAIL, user.getEmail());
        sharedPrefManager.put(SharedPrefManager.Key.USER_PHONE, user.getPhone());
    }
    public static Bitmap getQRCode(String Id) {
        // Handle Null pointer exception carefully.

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        Bitmap bitmap = null;
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(Id, BarcodeFormat.QR_CODE, 200, 200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            bitmap = barcodeEncoder.createBitmap(bitMatrix);
            return bitmap;

        } catch (WriterException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

}
