package com.deals.jeetodeals.Fragments.HomeFragment;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.deals.jeetodeals.Model.DrawResponse;
import com.deals.jeetodeals.Model.ShopResponse;
import com.deals.jeetodeals.Model.Signup;
import com.deals.jeetodeals.Model.WinnerResponse;
import com.deals.jeetodeals.SignupScreen.ExistsResponse;
import com.deals.jeetodeals.SignupScreen.SignupRepository;
import com.deals.jeetodeals.Utils.Utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeViewModel extends AndroidViewModel {
    HomeRepository repository;
    Utility utility= new Utility();
    private LiveData<HomeRepository.ApiResponse<ArrayList<HomeResponse>>> responseLiveData;
    private LiveData<HomeRepository.ApiResponse<ArrayList<DrawResponse>>> responseLiveData2;
    private LiveData<HomeRepository.ApiResponse<ArrayList<WinnerResponse>>> responseLiveData3;
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
    public LiveData<HomeRepository.ApiResponse<ArrayList<HomeResponse>>> getHome(String auth, String type,int category) {
        responseLiveData = repository.homeGet(auth,type,category);  // Fixed method call with correct case
        return responseLiveData;
    }

    public LiveData<HomeRepository.ApiResponse<ArrayList<DrawResponse>>> getDraw() {
        responseLiveData2 = repository.getDraw();  // Fixed method call with correct case
        return responseLiveData2;
    }
    public LiveData<HomeRepository.ApiResponse<ArrayList<WinnerResponse>>> getWinner() {
        responseLiveData3 = repository.getWinner();  // Fixed method call with correct case
        return responseLiveData3;
    }
    public LiveData<HomeRepository.ApiResponse<List<ShopResponse>>> getShop(String auth, String type, int id, int page, int perPage) {
        // Create a map for the filters, passing the type parameter with key "filter[type]"
        Map<String, String> filters = new HashMap<>();
        filters.put("filter[type]", type);

        // Call the repository with the filters map and the other parameters
        return repository.shop(auth, filters, id, page, perPage);
    }



}
