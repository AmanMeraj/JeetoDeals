package com.deals.jeetodeals.Fragments;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.deals.jeetodeals.Checkout.CheckoutResponse;
import com.deals.jeetodeals.Fragments.HomeFragment.HomeRepository;
import com.deals.jeetodeals.Fragments.HomeFragment.HomeResponse;
import com.deals.jeetodeals.Model.AddItems;
import com.deals.jeetodeals.Model.AppVersion;
import com.deals.jeetodeals.Model.BannerResponse;
import com.deals.jeetodeals.Model.CartResponse;
import com.deals.jeetodeals.Model.Category;
import com.deals.jeetodeals.Model.ChangePassword;
import com.deals.jeetodeals.Model.ChangePasswordResponse;
import com.deals.jeetodeals.Model.Checkout;
import com.deals.jeetodeals.Model.CouponResponse;
import com.deals.jeetodeals.Model.FcmResponse;
import com.deals.jeetodeals.Model.GetCheckout;
import com.deals.jeetodeals.Model.InvoiceResponse;
import com.deals.jeetodeals.Model.OrderDetailsResponse;
import com.deals.jeetodeals.Model.ShopResponse;
import com.deals.jeetodeals.Model.TicketResponse;
import com.deals.jeetodeals.Model.TrackingResponse;
import com.deals.jeetodeals.Model.User;
import com.deals.jeetodeals.Model.WalletResponse;
import com.deals.jeetodeals.Model.WishlistCreationResponse;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;

public class FragmentsViewModel extends AndroidViewModel {
    FragmentsRepository repository;
    MutableLiveData<FragmentsRepository.ApiResponse<Integer>> currentBalance;
    LiveData<FragmentsRepository.ApiResponse<CartResponse>>responseLiveData;
    LiveData<FragmentsRepository.ApiResponse<AppVersion>>responseLiveDataAppVersion;
    LiveData<FragmentsRepository.ApiResponse<GetCheckout>>responseLiveDataGetCheckout;
    public LiveData<FragmentsRepository.ApiResponse<CartResponse>>responseLiveDataAdd;
    LiveData<FragmentsRepository.ApiResponse<CartResponse>>responseLiveDataRemove;
    LiveData<FragmentsRepository.ApiResponse<CartResponse>>responseLiveDataUpdate;
    LiveData<FragmentsRepository.ApiResponse<ChangePasswordResponse>>changePasswordLiveData;
    LiveData<FragmentsRepository.ApiResponse<ArrayList<WalletResponse>>>responseLiveDataWallet;
    LiveData<FragmentsRepository.ApiResponse<ArrayList<Category>>>responseLiveDataCategory;
     LiveData<FragmentsRepository.ApiResponse<TicketResponse>> responseLiveDataTickets;
     LiveData<FragmentsRepository.ApiResponse<BannerResponse>> responseLiveDataBanner;
     LiveData<FragmentsRepository.ApiResponse<InvoiceResponse>> responseLiveDataInvoice;
     LiveData<FragmentsRepository.ApiResponse<OrderDetailsResponse>> responseLiveDataOderDeatils;
     LiveData<FragmentsRepository.ApiResponse<TrackingResponse>> responseLiveDataTrackingDetails;
     LiveData<FragmentsRepository.ApiResponse<FcmResponse>> responseLiveDataFcm;
    private LiveData<FragmentsRepository.ApiResponse<Void>> responseLiveDataDeleteCart;

    LiveData<FragmentsRepository.ApiResponse<WishlistCreationResponse>> responseLiveDataCreateWishList;
     LiveData<FragmentsRepository.ApiResponse<CheckoutResponse>> responseLiveDataCheckout;
    private LiveData<FragmentsRepository.ApiResponse<ArrayList<ShopResponse>>> responseLiveDataShop;
    public FragmentsViewModel(@NonNull Application application) {
        super(application);
        repository= new FragmentsRepository();
    }

    public LiveData<FragmentsRepository.ApiResponse<CartResponse>> getCart(String auth){
        responseLiveData=repository.cart(auth);
        return responseLiveData;
    }
    public LiveData<FragmentsRepository.ApiResponse<ChangePasswordResponse>> changePassword(String auth, ChangePassword changePassword){
        changePasswordLiveData=repository.changePass(auth,changePassword);
        return changePasswordLiveData;
    }
    public LiveData<FragmentsRepository.ApiResponse<Void>> deleteItemInCart(String auth, String nonce) {
        responseLiveDataDeleteCart = repository.deleteCart(auth, nonce);
        return responseLiveDataDeleteCart;
    }
    public LiveData<FragmentsRepository.ApiResponse<AppVersion>> getAppVersion(){
        responseLiveDataAppVersion=repository.version();
        return responseLiveDataAppVersion;
    }
    public LiveData<FragmentsRepository.ApiResponse<GetCheckout>> getCheckout(String auth,String nonce){
        responseLiveDataGetCheckout=repository.checkout(auth,nonce);
        return responseLiveDataGetCheckout;
    }

    public LiveData<FragmentsRepository.ApiResponse<OrderDetailsResponse>> getOrderDetails(String auth, int orderId){
        responseLiveDataOderDeatils=repository.orderDetails(auth,orderId);
        return responseLiveDataOderDeatils;
    }
    public LiveData<FragmentsRepository.ApiResponse<TrackingResponse>> getTrackingDetails(String auth, int orderId){
        responseLiveDataTrackingDetails=repository.trackingDetails(auth,orderId);
        return responseLiveDataTrackingDetails;
    }
    public LiveData<FragmentsRepository.ApiResponse<CartResponse>> AddToCart(String auth, String nonce, AddItems addItems){
        responseLiveDataAdd=repository.addItem(auth,nonce,addItems);
        return responseLiveDataAdd;
    }

    public LiveData<FragmentsRepository.ApiResponse<CartResponse>> RemoveItem(String auth, String nonce, AddItems addItems){
        responseLiveDataRemove=repository.removeItem(auth,nonce,addItems);
        return responseLiveDataRemove;
    }
    public LiveData<FragmentsRepository.ApiResponse<CartResponse>> UpdateItem(String auth, String nonce, AddItems addItems){
        responseLiveDataUpdate=repository.updateItem(auth,nonce,addItems);
        return responseLiveDataUpdate;
    }

    public LiveData<FragmentsRepository.ApiResponse<ArrayList<WalletResponse>>> GetWallet(String auth){
        responseLiveDataWallet=repository.getWallet(auth);
        return responseLiveDataWallet;
    }
    public LiveData<FragmentsRepository.ApiResponse<ArrayList<Category>>> getCategory(){
        responseLiveDataCategory=repository.category();
        return responseLiveDataCategory;
    }
    public MutableLiveData<FragmentsRepository.ApiResponse<Integer>> getCurrentBalance(String auth) {
        currentBalance = repository.getCurrentBalance(auth);
        return currentBalance;
    }
    public LiveData<FragmentsRepository.ApiResponse<TicketResponse>> GetCustomerTickets(String auth, String customerId) {
        responseLiveDataTickets = repository.getCustomerTickets(auth, customerId);
        return responseLiveDataTickets;
    }

    public LiveData<FragmentsRepository.ApiResponse<BannerResponse>> getBanner() {
        responseLiveDataBanner = repository.banner();
        return responseLiveDataBanner;
    }
    public LiveData<FragmentsRepository.ApiResponse<InvoiceResponse>> getInvoice(String auth,int id) {
        responseLiveDataInvoice = repository.Invoice(auth,id);
        return responseLiveDataInvoice;
    }
    public LiveData<FragmentsRepository.ApiResponse<FcmResponse>> postFcm(String auth, User user) {
        responseLiveDataFcm = repository.fcm(auth,user);
        return responseLiveDataFcm;
    }

    public LiveData<FragmentsRepository.ApiResponse<WishlistCreationResponse>> createWishList(String auth) {
        responseLiveDataCreateWishList = repository.createWishlist(auth);
        return responseLiveDataCreateWishList;
    }

    public LiveData<FragmentsRepository.ApiResponse<CheckoutResponse>> postCheckout(String auth,String nonce, Checkout checkout) {
        responseLiveDataCheckout = repository.checkoutPost(auth,nonce,checkout);
        return responseLiveDataCheckout;
    }

    public LiveData<FragmentsRepository.ApiResponse<CouponResponse>> applyCoupon(String authToken, String nonce, String couponCode) {
        return repository.applyCoupon(authToken, nonce, couponCode);
    }

    public LiveData<FragmentsRepository.ApiResponse<ResponseBody>> removeCoupon(String authToken, String nonce) {
        return repository.removeCoupon(authToken, nonce);
    }


}
