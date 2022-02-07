package com.example.dressapp1;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.dressapp1.model.DBModel;
import com.example.dressapp1.model.Model;
import com.example.dressapp1.model.Product;

import java.util.List;

public class ProductListFragmentViewModel extends ViewModel {

    LiveData<List<Product>> data;

//    ProductListFragmentViewModel() {ProductListFragmentViewModel
//        DBModel.dbInstance.getAllProducts(new DBModel.GetAllProductsListener() {
//            @Override
//            public void onComplete() {
////                data
//            }
//        });
//    }

    public LiveData<List<Product>> getData() {
        return data;
    }

}
