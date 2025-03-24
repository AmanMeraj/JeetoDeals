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
    Utility utility = new Utility();
    private LiveData<HomeRepository.ApiResponse<ArrayList<HomeResponse>>> responseLiveData;
    private LiveData<HomeRepository.ApiResponse<ArrayList<DrawResponse>>> responseLiveData2;
    private LiveData<HomeRepository.ApiResponse<ArrayList<WinnerResponse>>> responseLiveData3;
    private LiveData<HomeRepository.ApiResponse<List<ShopResponse>>> responseLiveDataShop;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        repository = new HomeRepository();
    }

    /**
     * Method to call the Signup API and get the response wrapped in ApiResponse.
     *
     * @param type type string containing type of response i want to get.
     * @return LiveData containing ApiResponse with ExistsResponse data and status message.
     */
    public LiveData<HomeRepository.ApiResponse<ArrayList<HomeResponse>>> getHome(String type, int category) {
        responseLiveData = repository.homeGet(type, category);
        return responseLiveData;
    }

    public LiveData<HomeRepository.ApiResponse<ArrayList<DrawResponse>>> getDraw() {
        responseLiveData2 = repository.getDraw();
        return responseLiveData2;
    }

    public LiveData<HomeRepository.ApiResponse<ArrayList<WinnerResponse>>> getWinner() {
        responseLiveData3 = repository.getWinner();
        return responseLiveData3;
    }

    /**
     * Get shop items with basic filtering
     *
     * @param type Product type filter
     * @param id Category ID
     * @param page Current page number
     * @param perPage Items per page
     * @return LiveData containing ApiResponse with shop items
     */
    public LiveData<HomeRepository.ApiResponse<List<ShopResponse>>> getShop(String type, int id, int page, int perPage) {
        // Create a map for the filters, passing the type parameter with key "filter[type]"
        Map<String, String> filters = new HashMap<>();
        filters.put("filter[type]", type);

        // Call the repository with the filters map and the other parameters
        return repository.shop(filters, id, page, perPage);
    }

    /**
     * Get shop items with sorting options
     *
     * @param type Product type filter
     * @param id Category ID
     * @param page Current page number
     * @param perPage Items per page
     * @param order Sort order (asc/desc or null for default)
     * @param orderBy Field to sort by or null for default
     * @return LiveData containing ApiResponse with shop items
     */
    public LiveData<HomeRepository.ApiResponse<List<ShopResponse>>> getShop(
            String type, int id, int page, int perPage, String order, String orderBy) {

        // Create a map for the filters and sort parameters
        Map<String, String> params = new HashMap<>();
        params.put("filter[type]", type);

        // Only add sort parameters if they're provided (not null and not empty)
        if (order != null && !order.isEmpty()) {
            params.put("order", order);
        }

        if (orderBy != null && !orderBy.isEmpty()) {
            params.put("orderby", orderBy);
        }

        // Call the repository with the params map and other parameters
        return repository.shop(params, id, page, perPage);
    }
}