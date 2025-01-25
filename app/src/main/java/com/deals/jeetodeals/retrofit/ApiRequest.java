package com.deals.jeetodeals.retrofit;


import androidx.annotation.Nullable;

import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiRequest {


    @Headers({"Accept: application/json"})
    @POST("login")
    Call<LoginResponse> getLogin(
            @Body Login login
    );
//
//    @Headers({"Accept: application/json"})
//    @POST("fcm-update")
//    Call<FcmResponse> postFcm(
//            @Header("Authorization") String authorization,
//            @Body Fcm fcm
//            );
//
//    @Headers({"Accept: application/json"})
//    @POST("customer")
//    Call<AddCustomerResponse> postCustomer(
//            @Header("Authorization") String authorization,
//            @Body Cx cx
//            );
//
//    @Headers({"Accept: application/json"})
//    @GET("profile")
//    Call<ProfileResponse> getProfile(
//            @Header("Authorization") String authorization
//    );
//
//    @Headers({"Accept: application/json"})
//    @GET("inventories")
//    Call<InventoryResponse> getInventory(
//            @Header("Authorization") String authorization
//    );
//    @Headers({"Accept: application/json"})
//    @GET("home")
//    Call<ServicesResponse> getServices(
//            @Header("Authorization") String authorization
//    );
//
//
//
////    @Headers({"Accept: application/json"})
////    @GET("withdraw-money")
////    Call<WithDrawalHistoryResponse> getWithdrawal(
////            @Header("Authorization") String authorization
////    );
////
//    @Headers({"Accept: application/json"})
//    @Multipart
//    @POST("attendence")
//    Call<AttendanceResponse> postAttendance(
//            @Header("Authorization") String authorization,
//            @Part("lat") RequestBody lat,
//            @Part("long") RequestBody longg,
//            @Part MultipartBody.Part image
//            );
//
//    @Headers({"Accept: application/json"})
//    @Multipart
//    @POST("expense")
//    Call<ExpenseResponse> postExpense(
//            @Header("Authorization") String authorization,
//            @Part("lat") RequestBody lat,
//            @Part("long") RequestBody longg,
//            @Part("description") RequestBody note,
//            @Part MultipartBody.Part image,
//            @Part("amount") RequestBody amount
//            );
//
//    @Headers({"Accept: application/json"})
//    @Multipart
//    @POST("service-status")
//    Call<UpdateStatusResponse> postStatus(
//            @Header("Authorization") String authorization,
//            @Part("status") RequestBody status,
//            @Part("service_id") RequestBody serviceId,
//            @Part("notes") RequestBody notes,
//            @Part("otp") RequestBody otp,
//            @Part @Nullable MultipartBody.Part image,
//            @Part @Nullable List<MultipartBody.Part> parts,
//            @Part @Nullable List<MultipartBody.Part> documentImages
//    );
//
//    @Headers({"Accept: application/json"})
//    @GET("attendence")
//    Call<AttendanceResponseGet> getAttendance(
//            @Header("Authorization") String authorization
//            );
//    @Headers({"Accept: application/json"})
//    @GET("customers")
//    Call<CreateServiceResponse> getCustomer(
//            @Header("Authorization") String authorization
//            );
//
//    @Multipart
//    @Headers({"Accept: application/json"})
//    @POST("ticket")
//    Call<PostServiceResponse> postService(
//            @Header("Authorization") String authorization,
//            @Part("customer_id") RequestBody cId,
//            @Part("title") RequestBody tId,
//            @Part("brand") RequestBody bId,
//            @Part("product_type") RequestBody pId,
//            @Part("model_number") RequestBody modelNo,
//            @Part("serial_number") RequestBody serialNo,
//            @Part("service_details") RequestBody serviceDetail,
//            @Part("service_type") RequestBody sTypeId,
//            @Part MultipartBody.Part imageFile,
//            @Part MultipartBody.Part signatureFile,
//            @Part ("repair_checklist[]") List<Integer> repairChecklistParts
//    );
//
////    @Headers({"Accept: application/json"})
////    @GET("service-status")
////    Call<ServiceDetailsResponse> getStatus(
////            @Header("Authorization") String authorization
////            );
////
////    @Headers({"Accept: application/json"})
////    @POST("bet-place")
////    Call<SingleResponse> single(
////            @Header("Authorization") String authorization,
////           @Body Single single
////    );
//
//    @Headers({"Accept: application/json"})
//    @GET("services")
//    Call<ServiceRowResponse> services(
//            @Header("Authorization") String authorization,
//           @Query("status") String slug
//    );
//    @Headers({"Accept: application/json"})
//    @GET("filter-status")
//    Call<FilterResponse> getFilterStatus(
//            @Header("Authorization") String authorization
//    );
//    @Headers({"Accept: application/json"})
//    @GET("service-details/{id}")
//    Call<ServiceDetailsResponse> getServiceDetail(
//            @Header("Authorization") String authorization,
//            @Path("id") String id
//    );
//
//    @Headers({"Accept: application/json"})
//    @GET("staff-permissions")
//    Call<StaffPermissionResponse> getStaffPermission(
//            @Header("Authorization") String authorization
//    );
//    @Headers({"Accept: application/json"})
//    @GET("notification-list")
//    Call<NotificationResponse> getNotification(
//            @Header("Authorization") String authorization,
//            @Query("page") int page
//    );
//
//    @Headers({"Accept: application/json"})
//    @GET("customers")
//    Call<CustomerListResponse> getCustomerCreditSales(
//            @Header("Authorization") String authorization
//    );
//
//    @Headers({"Accept: application/json"})
//    @GET("payment-methods")
//    Call<Payment> getPaymentMethods(
//            @Header("Authorization") String authorization
//    );
////    @Headers({"Accept: application/json"})
////    @GET("show-payment")
////    Call<ShowPaymentResponse> showPayment(
////            @Header("Authorization") String authorization
////    );
////    @Headers({"Accept: application/json"})
////    @POST("jodibet-place")
////    Call<JodiResponse> jodi(
////            @Header("Authorization") String authorization,
////           @Body Jodi jodi
////    );
////    @Headers({"Accept: application/json"})
////    @POST("singlepatti")
////    Call<SinglePattiResponse> singlePatti(
////            @Header("Authorization") String authorization,
////           @Body SinglePatti singlePatti
////    );
////
////    @Headers({"Accept: application/json"})
////    @POST("cpbet-place")
////    Call<CPResponse> getCP(
////            @Header("Authorization") String authorization,
////            @Body CP cp
////            );
////
////    @Headers({"Accept: application/json"})
////    @GET("transaction-history")
////    Call<TransactionHistoryResponse> getTransaction(
////            @Header("Authorization") String authorization
////    );
////
////    @Headers({"Accept: application/json"})
////    @GET("helpline")
////    Call<HelplineResponse> getHelp(
////            @Header("Authorization") String authorization
////    );
////
////    @Headers({"Accept: application/json"})
////    @GET("deposit-history")
////    Call<DepositHistoryResponse> getDeposit(
////            @Header("Authorization") String authorization
////    );
////
////
////    @Headers({"Accept: application/json"})
////    @GET("banner")
////    Call<BannerResponse> getBanner(
////            @Header("Authorization") String authorization
////    );
////
////
////    @Headers({"Accept: application/json"})
////    @GET("game-rule")
////    Call<GameRuleResponse> getGamerule(
////            @Header("Authorization") String authorization
////    );
////
////    @Headers({"Accept: application/json"})
////    @POST("update-profile")
////    Call<UpdateResponse> updateProfile(
////            @Header("Authorization") String authorization,
////            @Body ProfileUpdate profileUpdate
////            );
////



}
