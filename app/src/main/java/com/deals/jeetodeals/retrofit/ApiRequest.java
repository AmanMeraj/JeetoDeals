package com.deals.jeetodeals.retrofit;


import android.util.Log;

import androidx.annotation.Nullable;

import com.deals.jeetodeals.ChangeAddress.ChangeAddressResponse;
import com.deals.jeetodeals.Checkout.CheckoutResponse;
import com.deals.jeetodeals.Fragments.HomeFragment.HomeResponse;
import com.deals.jeetodeals.Model.AddItems;
import com.deals.jeetodeals.Model.AppVersion;
import com.deals.jeetodeals.Model.BannerResponse;
import com.deals.jeetodeals.Model.CartResponse;
import com.deals.jeetodeals.Model.Category;
import com.deals.jeetodeals.Model.Checkout;
import com.deals.jeetodeals.Model.DrawResponse;
import com.deals.jeetodeals.Model.FcmResponse;
import com.deals.jeetodeals.Model.GetCheckout;
import com.deals.jeetodeals.Model.Login;
import com.deals.jeetodeals.Model.ShopResponse;
import com.deals.jeetodeals.Model.Signup;
import com.deals.jeetodeals.Model.TicketResponse;
import com.deals.jeetodeals.Model.UpdateAddress;
import com.deals.jeetodeals.Model.UpdateAddressResponse;
import com.deals.jeetodeals.Model.User;
import com.deals.jeetodeals.Model.WalletResponse;
import com.deals.jeetodeals.Model.WinnerResponse;
import com.deals.jeetodeals.Model.Wishlist;
import com.deals.jeetodeals.Model.WishlistCreationResponse;
import com.deals.jeetodeals.MyOrders.MyOrdersResponse;
import com.deals.jeetodeals.OTP.Otp;
import com.deals.jeetodeals.OTP.OtpResponse;
import com.deals.jeetodeals.Profile.ProfileResponse;
import com.deals.jeetodeals.SignInScreen.ForgotPassResponse;
import com.deals.jeetodeals.SignInScreen.SigninResponse;
import com.deals.jeetodeals.SignupScreen.ExistsResponse;
import com.deals.jeetodeals.SignupScreen.SignupResponse;
import com.deals.jeetodeals.Wishlist.WishlistAddResponse;
import com.deals.jeetodeals.Wishlist.WishlistDeleteResponse;
import com.deals.jeetodeals.Wishlist.WishlistResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface ApiRequest {


    @Headers({"Accept: application/json"})
    @POST("custom/v1/register")
    Call<ExistsResponse> postSignup(
            @Body Signup signup
    );

    @Headers({"Accept: application/json"})
    @POST("custom/v1/verify-otp")
    Call<OtpResponse> postOtp(
            @Body Otp otp
    );

    @Headers({"Accept: application/json"})
    @POST("jwt-auth/v1/token")
    Call<SigninResponse> postLogin(
            @Body Login login
    );

    @Headers({"Accept: application/json"})
    @POST("custom/v1/wishlist/add")
    Call<WishlistAddResponse> addWishlist(
            @Header("Authorization") String authorization,
            @Body Wishlist wishlist
    );

    @Headers({"Accept: application/json"})
    @HTTP(method = "DELETE", path = "custom/v1/wishlist/delete", hasBody = true)
    Call<WishlistDeleteResponse> deleteWishlist(
            @Header("Authorization") String authorization,
            @Body Wishlist wishlist
    );

    @Headers({"Accept: application/json"})
    @GET("wc/store/v1/products")
    Call<ArrayList<HomeResponse>> getHome(
            @Header("Authorization") String authorization,
            @Query("type") String type,
            @Query("category") int category
    );

    @Headers({"Accept: application/json"})
    @GET("custom/v1/wishlist")
    Call<ArrayList<WishlistResponse>> getWishlist(
            @Header("Authorization") String authorization
    );

    @Headers({"Accept: application/json"})
    @GET("custom/v1/customer-orders")
    Call<ArrayList<MyOrdersResponse>> getOrders(
            @Header("Authorization") String authorization
    );

    @Headers({"Accept: application/json"})
    @GET("wc/store/v1/products/categories")
    Call<ArrayList<Category>> getCategory(
            @Header("Authorization") String authorization
    );

    @Headers({"Accept: application/json"})
    @GET("custom/v1/draws")
    Call<ArrayList<DrawResponse>> getDraw();

    @Headers({"Accept: application/json"})
    @GET("custom/v1/winners")
    Call<ArrayList<WinnerResponse>> getWinner();

//    @Headers({"Accept: application/json"})
//    @GET("wc/store/v1/products")
//    Call<List<ShopResponse>> getShop(
//            @Header("Authorization") String authorization,
//            @Query("type") String type,
//            @Query("category") int id,
//            @Query("page") int page,
//            @Query("per_page") int perPage
//    );

    @Headers({"Accept: application/json"})
    @GET("wc/store/v1/products")
    Call<List<ShopResponse>> getShop(
            @Header("Authorization") String authorization,
            @QueryMap Map<String, String> filters,
            @Query("category") int id,
            @Query("page") int page,
            @Query("per_page") int perPage
    );

    @Headers({"Accept: application/json"})
    @GET("wc/store/v1/products/{id}")  // Add {id} as a placeholder in the URL
    Call<ShopResponse> getShopWishList(@Path("id") int id); // Pass the ID dynamically




    @Headers({"Accept: application/json"})
    @GET("wc/store/v1/cart")
    Call<CartResponse> getCart(
            @Header("Authorization") String authorization
    );

    @Headers({"Accept: application/json"})
    @GET("custom/v1/app-version")
    Call<AppVersion> getAppVersion(
    );

    @Headers({"Accept: application/json"})
    @GET("wp/v2/wallet")
    Call<ArrayList<WalletResponse>> GetWallet(
            @Header("Authorization") String authorization
    );
    @Headers({"Accept: application/json"})
    @GET("lottery/v1/customer-tickets")
    Call<TicketResponse> GetCustomerTickets(
            @Header("Authorization") String authorization,
            @Query("customer_id") String customerId
    );

    @Headers({"Accept: application/json"})
    @GET("custom/v1/banners")
    Call<BannerResponse> getBanner(
            @Header("Authorization") String authorization
    );

    @Headers({"Accept: application/json"})
    @GET("custom/v1/address")
    Call<ChangeAddressResponse> GetAddress(
            @Header("Authorization") String authorization
    );
    @Headers({"Accept: application/json"})
    @GET("wc/store/v1/checkout")
    Call<GetCheckout> getCheckout(
            @Header("Authorization") String authorization,
            @Header("nonce") String nonce
    );
    @Headers({"Accept: application/json"})
    @GET("custom/v1/customer")
    Call<ProfileResponse> getProfile(
            @Header("Authorization") String authorization
    );

    @Headers({"Accept: application/json"})
    @GET("wp/v2/current_balance")
    Call<Integer> GetBalance(
            @Header("Authorization") String authorization
    );
    @Headers({"Accept: application/json"})
    @POST("wc/store/v1/cart/add-item")
    Call<CartResponse> AddToCart(
            @Header("Authorization") String authorization,
            @Header("nonce") String nonce,
            @Body AddItems addItems
            );

    @Headers({"Accept: application/json"})
    @PUT("custom/v1/address")
    Call<UpdateAddressResponse> UpdateAddress(
            @Header("Authorization") String authorization,
            @Body UpdateAddress updateAddress
            );

    @Headers({"Accept: application/json"})
    @POST("wc/store/v1/cart/remove-item")
    Call<CartResponse> RemoveItem(
            @Header("Authorization") String authorization,
            @Header("nonce") String nonce,
            @Body AddItems addItems
            );

    @Headers({"Accept: application/json"})
    @POST("wc/store/v1/cart/update-item")
    Call<CartResponse> UpdateItem(
            @Header("Authorization") String authorization,
            @Header("nonce") String nonce,
            @Body AddItems addItems
            );

    @Headers({"Accept: application/json"})
    @POST("custom/v1/forgot-password")
    Call<ForgotPassResponse> ForgotPassword(
            @Body User user
            );
    @Headers({"Accept: application/json"})
    @POST("custom/v1/wishlist/create")
    Call<WishlistCreationResponse> createWishlist(
            @Header("Authorization") String authorization
            );

    @Headers({"Accept: application/json"})
    @POST("custom/v1/update-fcm-token")
    Call<FcmResponse> fcm(
            @Header("Authorization") String authorization,
            @Body User user
            );

    @Headers({"Accept: application/json"})
    @POST("wc/store/v1/checkout")
    Call<CheckoutResponse> checkout(
            @Header("Authorization") String authorization,
            @Header("nonce") String nonce,
            @Body Checkout checkout
            );

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
