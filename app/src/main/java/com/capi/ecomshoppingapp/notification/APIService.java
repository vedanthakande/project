package com.capi.ecomshoppingapp.notification;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService
{
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAA_1A59tU:APA91bF2Gbzw-4adAMYD_csfRKMFr3visFIfHUHrDc2N3psMBWwgLi6IbAo1rom9oVUpgVNVwR5Ab_gBiEQu-_fZx6yxyYLqCv4cNEzERB8vExoOA8S9_pKp0jA0cTKnH5KJrlrFRy3C"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
