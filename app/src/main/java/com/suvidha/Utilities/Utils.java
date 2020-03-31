package com.suvidha.Utilities;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.suvidha.Models.GrocItemModel;
import com.suvidha.Models.UserModel;
import com.suvidha.Models.ZonesModel;
import com.suvidha.R;

import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static final String rs = "\u20B9";
    public static double DELIVERY_CHARGE = 5;
    public static double APP_CHARGE = 2;
    public static List<ZonesModel> zonesList=new ArrayList<>();
    public static List<GrocItemModel> shopItems=new ArrayList<>();
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
        sharedPrefManager.put(SharedPrefManager.Key.USER_NAME, user.getName());
        sharedPrefManager.put(SharedPrefManager.Key.USER_EMAIL, user.getEmail());
        sharedPrefManager.put(SharedPrefManager.Key.USER_PHONE, user.getPhone());
    }

}
