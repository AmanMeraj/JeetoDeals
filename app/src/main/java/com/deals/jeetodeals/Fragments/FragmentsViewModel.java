package com.deals.jeetodeals.Fragments;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.deals.jeetodeals.Fragments.HomeFragment.HomeRepository;
import com.deals.jeetodeals.Fragments.HomeFragment.HomeResponse;
import com.deals.jeetodeals.Model.AddItems;
import com.deals.jeetodeals.Model.BannerResponse;
import com.deals.jeetodeals.Model.CartResponse;
import com.deals.jeetodeals.Model.Category;
import com.deals.jeetodeals.Model.ShopResponse;
import com.deals.jeetodeals.Model.TicketResponse;
import com.deals.jeetodeals.Model.WalletResponse;

import java.util.ArrayList;

public class FragmentsViewModel extends AndroidViewModel {
    FragmentsRepository repository;
    MutableLiveData<FragmentsRepository.ApiResponse<Integer>> currentBalance;
    LiveData<FragmentsRepository.ApiResponse<CartResponse>>responseLiveData;
    public LiveData<FragmentsRepository.ApiResponse<CartResponse>>responseLiveDataAdd;
    LiveData<FragmentsRepository.ApiResponse<CartResponse>>responseLiveDataRemove;
    LiveData<FragmentsRepository.ApiResponse<CartResponse>>responseLiveDataUpdate;
    LiveData<FragmentsRepository.ApiResponse<ArrayList<WalletResponse>>>responseLiveDataWallet;
    LiveData<FragmentsRepository.ApiResponse<ArrayList<Category>>>responseLiveDataCategory;
     LiveData<FragmentsRepository.ApiResponse<TicketResponse>> responseLiveDataTickets;
     LiveData<FragmentsRepository.ApiResponse<BannerResponse>> responseLiveDataBanner;
    private LiveData<FragmentsRepository.ApiResponse<ArrayList<ShopResponse>>> responseLiveDataShop;
    public FragmentsViewModel(@NonNull Application application) {
        super(application);
        repository= new FragmentsRepository();
    }

    public LiveData<FragmentsRepository.ApiResponse<CartResponse>> getCart(String auth){
        responseLiveData=repository.cart(auth);
        return responseLiveData;
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
    public LiveData<FragmentsRepository.ApiResponse<ArrayList<Category>>> getCategory(String auth){
        responseLiveDataCategory=repository.category(auth);
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

    public LiveData<FragmentsRepository.ApiResponse<BannerResponse>> getBanner(String auth) {
        responseLiveDataBanner = repository.banner(auth);
        return responseLiveDataBanner;
    }

}
