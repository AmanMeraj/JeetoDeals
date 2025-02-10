package com.deals.jeetodeals.Fragments.HomeFragment;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.deals.jeetodeals.Model.ShopResponse;
import com.deals.jeetodeals.Model.Signup;
import com.deals.jeetodeals.SignupScreen.ExistsResponse;
import com.deals.jeetodeals.SignupScreen.SignupRepository;
import com.deals.jeetodeals.Utils.Utility;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends AndroidViewModel {
    HomeRepository repository;
    Utility utility= new Utility();
    private LiveData<HomeRepository.ApiResponse<ArrayList<HomeResponse>>> responseLiveData;
    private LiveData<HomeRepository.ApiResponse<List<ShopResponse>>> responseLiveDataShop;
    public HomeViewModel(@NonNull Application application) {
        super(application);
        repository= new HomeRepository();
    }
    /**
     * Method to call the Signup API and get the response wrapped in ApiResponse.
     *
     * @param type type string containing type of response i want to get.
     * @return LiveData containing ApiResponse with ExistsResponse data and status message.
     */
    public LiveData<HomeRepository.ApiResponse<ArrayList<HomeResponse>>> getHome(String auth, String type) {
        responseLiveData = repository.homeGet(auth,type);  // Fixed method call with correct case
        return responseLiveData;
    }
    public LiveData<HomeRepository.ApiResponse<List<ShopResponse>>> getShop(String auth, String type, int id, int page, int perPage) {
        responseLiveDataShop = repository.shop(auth, type, id, page, perPage);
        return responseLiveDataShop;
    }

}
