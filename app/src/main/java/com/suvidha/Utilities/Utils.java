package com.suvidha.Utilities;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.suvidha.Activities.LoginActivity;
import com.suvidha.Models.AddressModel;
import com.suvidha.Models.CartModel;
import com.suvidha.Models.CityModel;
import com.suvidha.Models.HomeIconModel;
import com.suvidha.Models.ItemModel;
import com.suvidha.Models.LocationModel;
import com.suvidha.Models.UserModel;
import com.suvidha.Models.ZonesModel;
import com.suvidha.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Utils {
    public static final String rs = "\u20B9";

//    public static final String BASE_URL = "http://192.168.43.114:5000";
//    public static final String BASE_URL = "http://192.168.43.55:5000";
    public static final String BASE_URL = "http://13.127.163.197:5000";
    public static final String password = "Nitsuvidha1!";
    public static final String email = "suvidhajamshedpur@gmail.com";
    public static final String PLAYSTORE_LINK = "https://play.google.com/store/apps/details?id=com.suvidha";
    public static final int LOCATION_PERMISSION_CODE = 10;
    public static final int CAMERA_PERMISSION_CODE = 51;
    public static Location currentLocation;
    public static double DELIVERY_CHARGE = 5;
    public static double APP_CHARGE = 2;
    public static int is_delivery=0;
    public static int is_quarantined=0;
    public static int is_pass = 0;
    public static String district;
    public static String state;
    public static int is_ngo = 0;
    public static int is_shopper = 0;
    public static int is_quarantine = 0;
    public static List<String> special_q_list = new ArrayList<>();
    public static String shop_id;
    public static List<ZonesModel> zonesList=new ArrayList<>();
    public static List<ZonesModel> statesList=new ArrayList<>();
    public static List<ZonesModel> districtsList=new ArrayList<>();
    public static List<ItemModel> shopItems=new ArrayList<>();
    public static List<CartModel> allOrders = new ArrayList<>();
    public static List<AddressModel> address = new ArrayList<>();
    public static HashMap<String, List<String>> mStateDist = new HashMap<>();
    public static String order_address;

    public static final List<HomeIconModel> home_icons = new ArrayList<HomeIconModel>(Arrays.asList(
            new HomeIconModel(0,R.mipmap.ic_quarantine,R.string.Quarantine),
            new HomeIconModel(1,R.drawable.ic_pass,R.string.request_passes),
            new HomeIconModel(2,R.drawable.ic_shop,R.string.shop),

            new HomeIconModel(3,R.mipmap.ic_food_supply,R.string.food_supply_center),
            new HomeIconModel(4,R.mipmap.ic_quarantine,R.string.qurantinec)

            ));

    public static Integer currentType;
    public static int local_zone_name = 0;
    public static List<CityModel> city = new ArrayList<>();

    public static HashMap<Integer,String> statusHashMap = new HashMap<Integer, String>(){{
        put(-1,"Rejected");
        put(0,"Pending");
        put(1,"Accepted and waiting for approval");
        put(2,"Approved");
        put(3,"Delivered");
    }};

    public static HashMap<Integer,String> orderStatus= new HashMap<Integer,String>(){{
     put(0,"Pending");
     put(1,"Order Accepted");
     put(2,"Out for delivery");
    }};
    public static Dialog createProgressDialog(Context context,String msg){
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_progress);

        TextView tv = dialog.findViewById(R.id.progress_msg);
        tv.setText(msg);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setAttributes(lp);
        dialog.show();
        return dialog;
    }
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
    public static void setLoginSession(UserModel user, Context context){
        SharedPrefManager sharedPrefManager;
        sharedPrefManager = SharedPrefManager.getInstance(context);
        sharedPrefManager.put(SharedPrefManager.Key.LOGIN_STATUS, true);
        sharedPrefManager.put(SharedPrefManager.Key.STATE_KEY,user.getState());
        sharedPrefManager.put(SharedPrefManager.Key.DISTRICT_KEY,user.getDistrict());
        sharedPrefManager.put(SharedPrefManager.Key.USER_NAME, user.getName());
        sharedPrefManager.put(SharedPrefManager.Key.USER_EMAIL, user.getEmail());
        sharedPrefManager.put(SharedPrefManager.Key.USER_PHONE, user.getPhone());
        sharedPrefManager.put(SharedPrefManager.Key.USER_ADDRESS,user.address);
    }
    public static void clearLoginSession(Context context){
        SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(context);
        sharedPrefManager.edit();
        sharedPrefManager.clear();
        sharedPrefManager.commit();
    }
    public static Bitmap getQRCode(String Id) {
        // Handle Null pointer exception carefully.

//        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        Bitmap bitmap = null;
//        try {
//            BitMatrix bitMatrix = multiFormatWriter.encode(Id, BarcodeFormat.QR_CODE, 200, 200);
//            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
//            bitmap = barcodeEncoder.createBitmap(bitMatrix);
//            return bitmap;
//
//        } catch (WriterException e) {
//            e.printStackTrace();
//        }
        return bitmap;
    }

    public static void parseJson(Context context) {
        try {
            InputStream is = context.getAssets().open("statedis.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String myJson = new String(buffer, "UTF-8");
            JSONObject obj = new JSONObject(myJson);
            JSONArray states = obj.getJSONArray("states");
            for (int i = 0; i < states.length(); i++) {
                JSONArray district = obj.getJSONArray(states.getString(i));
                List<String> dis = new ArrayList<>();
                for (int j = 0; j < district.length(); j++) {
                    dis.add(district.getString(j).trim());
                }
                Utils.mStateDist.put(states.getString(i).trim(), dis);
            }
        } catch (Exception e) {
            Log.d("Register", e.getLocalizedMessage());
        }
    }

}
