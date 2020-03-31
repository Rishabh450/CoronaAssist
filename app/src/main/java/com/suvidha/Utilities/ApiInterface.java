package com.suvidha.Utilities;

import com.suvidha.Models.CartModel;
import com.suvidha.Models.EssentialsRequestModel;
import com.suvidha.Models.GeneralModel;
import com.suvidha.Models.GetOrdersModel;
import com.suvidha.Models.GetShopsModel;
import com.suvidha.Models.LoginResult;
import com.suvidha.Models.RegistrationResult;
import com.suvidha.Models.ShopModel;
import com.suvidha.Models.ShopRequestModel;
import com.suvidha.Models.UserModel;

import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiInterface {
    @Headers("Content-Type: application/json")
    @POST("/api/gregister")
    Call<RegistrationResult> register(@Header("x-access-tokens") String token, @Body UserModel user);


    @POST("/api/glogin")
    Call<LoginResult> login(@Body UserModel user);

    @Headers("Content-Type: application/json")
    @POST("/api/push_orders")
    Call<GeneralModel> pushOrder(@Header("x-access-tokens") String token, @Body CartModel model);

    @Headers("Content-Type: application/json")
    @POST("/api/get_shops")
    Call<ShopRequestModel> getAllShops(@Header("x-access-tokens") String token, @Body GetShopsModel model);

    @Headers("Content-Type: application/json")
    @GET("/api/get_essentials")
    Call<EssentialsRequestModel> getEssentials(@Header("x-access-tokens") String token);

    @Headers("Content-Type: application/json")
    @GET("/api/get_orders")
    Call<GetOrdersModel> getAllOrders(@Header("x-access-tokens") String token);



//    @POST("/api/login")
//    Call<LoginResult> login(@Body LoginCredential loginCredential);
//
//    @POST("/api/generate_pass")
//    Call<PassGenerationResult> createPass(@Body Pass pass);
//
//    @Headers("Content-Type: application/json")
//    @POST("/api/user_passes")
//    Call<UserPassesResult> getPasses(@Body Map<String, String> body);

//
//    @GET("/api/items")
//    Call<List<Item>> getItems(@QueryMap Map<String, String> map);
//
//    @GET("/api/employee/{email}")
//    Call<Employee> getEmployee(@Path("email") String email);
//
//    @POST("/api/employee/register")
//    Call<Employee> postEmployee(@Body Employee employee);
//
//    @GET("/api/employee/resendPasscode/{email}")
//    Call<String> getResend(@Path("email") String email);
//
//    @PUT("/api/items")
//    Call<Item> updateItem(@Body Item item);


}
