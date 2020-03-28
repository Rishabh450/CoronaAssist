package com.chat.pcon.myapplication.Utilities;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GoogleApiService {
    @GET("http://maps.googleapis.com/maps/api/directions/json?origin=lat1,lon1&destination=lat2,lon2&sensor=false&units=metric&mode=driving")
    Call<String> getDistanceMatrix(@Query("lat1") Double lat1, @Query("lon1")Double lon1, @Query("lat2")Double lat2, @Query("lon2")Double lon2);

}
