package com.deals.jeetodeals.MyOrders;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.ArrayList;

public class MyOrderViewModel extends AndroidViewModel {
    private final MyOrderRepository repository;
    private LiveData<MyOrderRepository.ApiResponse<ArrayList<MyOrdersResponse>>> ordersLiveData;

    public MyOrderViewModel(@NonNull Application application) {
        super(application);
        repository = new MyOrderRepository();
    }

    public LiveData<MyOrderRepository.ApiResponse<ArrayList<MyOrdersResponse>>> getOrders(String auth) {
        ordersLiveData = repository.getOrders(auth);
        return ordersLiveData;
    }
}
