package com.deals.jeetodeals.Wishlist;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.deals.jeetodeals.Model.Wishlist;

import java.util.ArrayList;

public class WishlistViewModel extends AndroidViewModel {
    WishlistRepository repository;
    LiveData<WishlistRepository.ApiResponse<ArrayList<WishlistResponse>>> responseLiveData;
    LiveData<WishlistRepository.ApiResponse<WishlistAddResponse>> responseLiveDataAdd;
    LiveData<WishlistRepository.ApiResponse<WishlistDeleteResponse>> responseLiveDataDelete;
    public WishlistViewModel(@NonNull Application application) {
        super(application);
        repository=new WishlistRepository();
    }
   public LiveData<WishlistRepository.ApiResponse<ArrayList<WishlistResponse>>>getWishlist(String auth){
        responseLiveData=repository.wishlist(auth);
        return responseLiveData;
    }

    public LiveData<WishlistRepository.ApiResponse<WishlistAddResponse>> addWishlist(String auth, Wishlist wishlist){
        responseLiveDataAdd=repository.wishlistAdd(auth,wishlist);
        return responseLiveDataAdd;
    }
    public LiveData<WishlistRepository.ApiResponse<WishlistDeleteResponse>> deleteWishlist(String auth, Wishlist wishlist) {
        return repository.deleteWishlist(auth, wishlist);
    }
}
