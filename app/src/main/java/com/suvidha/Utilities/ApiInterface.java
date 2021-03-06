package com.suvidha.Utilities;

import com.suvidha.Models.AcceptModel;
import com.suvidha.Models.CartModel;
import com.suvidha.Models.DeliveryAddressModel;
import com.suvidha.Models.EssentialsRequestModel;
import com.suvidha.Models.FetchNgomodel;
import com.suvidha.Models.GeneralModel;
import com.suvidha.Models.GetOrdersModel;
import com.suvidha.Models.GetReportsModel;
import com.suvidha.Models.GetShopsModel;
import com.suvidha.Models.ItemModel;
import com.suvidha.Models.ItemsRequestModel;
import com.suvidha.Models.LocationModel;
import com.suvidha.Models.LoginResult;
import com.suvidha.Models.MyAddressModel;
import com.suvidha.Models.NgoModel;
import com.suvidha.Models.OrderIdModel;
import com.suvidha.Models.OrderRequestModel;
import com.suvidha.Models.Pass;
import com.suvidha.Models.PassGenerationResult;
import com.suvidha.Models.QuarantineModel;
import com.suvidha.Models.QuarantineNearbyModel;
import com.suvidha.Models.RegistrationResult;
import com.suvidha.Models.ReportModel;
import com.suvidha.Models.SQList;
import com.suvidha.Models.ShopRequestModel;
import com.suvidha.Models.SidModel;
import com.suvidha.Models.SinglePassResult;
import com.suvidha.Models.UserModel;
import com.suvidha.Models.UserPassesResult;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiInterface {
    @Headers("Content-Type: application/json")
    @POST("/api/gregister")
    Call<RegistrationResult> register(@Header("x-access-tokens") String token, @Body UserModel user);
    @Headers("Content-Type: application/json")
    @POST("/api/edit_address")
    Call<GeneralModel> edit_address(@Header("x-access-tokens") String token,@Body MyAddressModel model);


    @POST("/api/glogin")
    Call<LoginResult> login(@Body UserModel model);

    @Headers("Content-Type: application/json")
    @POST("/api/get_shop_items")
    Call<ItemsRequestModel> getItems(@Header("x-access-tokens") String token, @Body SidModel sid);

    @Headers("Content-Type: application/json")
    @POST("/api/get_order")
    Call<OrderRequestModel> getOrder(@Header("x-access-tokens") String token, @Body OrderIdModel model);



    @Headers("Content-Type: application/json")
    @POST("/api/push_orders")
    Call<GeneralModel> pushOrder(@Header("x-access-tokens") String token, @Body CartModel model);

    @Headers("Content-Type: application/json")
    @GET("/api/get_shops")
    Call<ShopRequestModel> getAllShops(@Header("x-access-tokens") String token);

    @Headers("Content-Type: application/json")
    @GET("/api/get_sq_list")
    Call<SQList> getsqlist(@Header("x-access-tokens") String token);

    @Headers("Content-Type: application/json")
    @GET("/api/get_ngo_list")
    Call<FetchNgomodel>  get_ngo(@Header("x-access-tokens") String token);

    @Headers("Content-Type: application/json")
    @GET("/api/get_essentials")
    Call<EssentialsRequestModel> getEssentials(@Header("x-access-tokens") String token);

    @Headers("Content-Type: application/json")
    @GET("/api/get_orders")
    Call<GetOrdersModel> getAllOrders(@Header("x-access-tokens") String token);

    @Headers("Content-Type: application/json")
    @POST("/api/generate_pass")
    Call<PassGenerationResult> createPass(@Header("x-access-tokens") String token, @Body Pass pass);

    @Headers("Content-Type: application/json")
    @GET("/api/user_passes")
    Call<UserPassesResult> getPasses(@Header("x-access-tokens") String token);

    @Headers("Content-Type: application/json")
    @POST("/api/police/get_pass")
    Call<SinglePassResult> getPass(@Body Map<String, String> body);
    @Headers("Content-Type: application/json")
    @POST("/api/get_quarantine_near")
    Call<QuarantineNearbyModel> check_nearby(@Header("x-access-tokens") String token, @Body LocationModel model);

    @Headers("Content-Type: application/json")
    @POST("/api/report_violation")
    Call<GeneralModel> report_violation(@Header("x-access-tokens") String token, @Body LocationModel model);

    @GET("/api/police/get_passes/{status}")
    Call<UserPassesResult> getStatusPass(@Path("status") int status);

    @Headers("Content-Type: application/json")
    @PUT("/api/police/validate_pass")
    Call<RegistrationResult> updateStatus(@Body Map<String, String> body);

    @Headers("Content-Type: application/json")
    @POST("/api/register_quarantine")
    Call<GeneralModel> register_quarantine(@Header("x-access-tokens") String token, @Body QuarantineModel model);

    @Headers("Content-Type: application/json")
    @POST("/api/report_quarantine")
    Call<GeneralModel> send_report(@Header("x-access-tokens") String token, @Body ReportModel model);

    @Headers("Content-Type: application/json")
    @GET("/api/get_user_report")
    Call<GetReportsModel> get_report(@Header("x-access-tokens") String token);

    @Headers("Content-Type: application/json")
    @POST("/api/set_delivery_address")
    Call<GeneralModel> set_delivery_address(@Header("x-access-tokens") String token, @Body DeliveryAddressModel model);
    @Headers("Content-Type: application/json")
    @POST("/api/update_order")
    Call<GeneralModel> update_order(@Header("x-access-tokens") String token,@Body AcceptModel model);


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
