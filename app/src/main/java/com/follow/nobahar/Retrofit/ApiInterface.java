package com.follow.nobahar.Retrofit;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiInterface {

    @POST("api/v1/user/login")
    @FormUrlEncoded
    Call<Login> login(@Field("user_id") String user_id,
                      @Field("image_path") String imageAddress,
                      @Field("user_name") String userName,
                      @Field("post_count") String postCount,
                      @Field("follower_count") String followerCount,
                      @Field("following_count") String followingCont


    );

    @POST("api/v1/first_page")
    @FormUrlEncoded
    Call<FirstPage> FirstPage(@Field("uuid") String uuid,
                              @Field("api_token") String apiToken,
                              @Field("image_path") String imageAddress,
                              @Field("user_name") String userName,
                              @Field("post_count") String postCount,
                              @Field("follower_count") String followerCount,
                              @Field("following_count") String followingCont


    );

    @GET("api/v1/buttons")
    Call<List<Button>> Buttons();

    @GET("api/v1/force_followers")
    Call<List<ForceFollow>> ForceFollowers();

    @GET("api/v1/bests")
    Call<Bests> Bests();

    @POST("api/v1/transaction/add_coin")
    @FormUrlEncoded
    Call<UserCoin> AddCoin(@Field("uuid") String uuid,
                           @Field("api_token") String apiToken,
                           @Field("type") String type,
                           @Field("amount") String amount


    );

    @POST("api/v1/transaction/set")
    @FormUrlEncoded
    Call<UserCoin> SetOrder(@Field("uuid") String uuid,
                            @Field("api_token") String apiToken,
                            @Field("type") int type,
                            @Field("type_id") String type_id,
                            @Field("request_count") int requestCount,
                            @Field("remaining_count") int remainingCount,
                            @Field("image_path") String imagePath


    );

    @POST("api/v1/transaction/submit")
    @FormUrlEncoded
    Call<UserCoin> SubmitOrder(@Field("uuid") String uuid,
                               @Field("api_token") String apiToken,
                               @Field("type") int type,
                               @Field("t_id") String transaction_id


    );

    @POST("api/v1/transaction/get")
    @FormUrlEncoded
    Call<Transaction> getOrder(@Field("uuid") String uuid,
                               @Field("api_token") String apiToken,
                               @Field("type") int type


    );

    @POST("api/v1/transaction/convert_coins")
    @FormUrlEncoded
    Call<UserCoin> ConvertCoin(@Field("uuid") String uuid,
                               @Field("api_token") String apiToken,
                               @Field("type") int type,
                               @Field("value") String value


    );

    @POST("api/v1/lucky_arrow/set")
    @FormUrlEncoded
    Call<SimpleResult> SetLuckyArrow(@Field("uuid") String uuid,
                                     @Field("api_token") String apiToken,
                                     @Field("type") int type,
                                     @Field("value") String value


    );

    @POST("api/v1/lucky_arrow/get")
    @FormUrlEncoded
    Call<SimpleResult> GetLuckyArrow(@Field("uuid") String uuid,
                                     @Field("api_token") String apiToken


    );

    @POST("api/v1/transaction/transfer_coins")
    @FormUrlEncoded
    Call<UserCoin> TransferCoins(@Field("uuid") String uuid,
                                 @Field("api_token") String apiToken,
                                 @Field("destination_uuid") String dstUUid,
                                 @Field("type") int type,
                                 @Field("value") String value


    );

    @POST("api/v1/message/report/set")
    @FormUrlEncoded
    Call<SimpleResult> Report(@Field("uuid") String uuid,
                              @Field("api_token") String apiToken,
                              @Field("description") String desc,
                              @Field("title") String title


    );

    @POST("api/v1/transaction/list")
    @FormUrlEncoded
    Call<List<Order>> OrderList(@Field("uuid") String uuid,
                                @Field("api_token") String apiToken


    );

}
