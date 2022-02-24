package com.example.dressapp1.model;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.dressapp1.MyApplication;
import com.example.dressapp1.model.interfaces.EditProductListener;
import com.example.dressapp1.model.interfaces.GetUserById;
import com.example.dressapp1.model.interfaces.UploadImageListener;
import com.example.dressapp1.model.interfaces.UploadProductListener;
import com.google.android.gms.tasks.Task;

import java.util.List;



public class Model {
    public static final Model instance = new Model();
    DBModel fbModel = new DBModel();
    MutableLiveData<LoadingState> loadingState= new MutableLiveData<LoadingState>();
    MutableLiveData<List<Product>> productListLtd = new MutableLiveData<List<Product>>();

    private Model(){
        loadingState.setValue(LoadingState.loaded);
        reloadProductList();
    }

    public LiveData<LoadingState> getLoadingState() {
        return loadingState;
    }

    public LiveData<List<Product>> getAll() {
        return productListLtd;
    }

    public void reloadProductList() {
        Long localLastUpdate = Product.getLocalLastUpdated();
        loadingState.setValue(LoadingState.loading);

        fbModel.getAllProducts(localLastUpdate,(list)-> {
            if(list != null) {
                MyApplication.executorService.execute(()->{
                    Long lLastUpdate = new Long(0);

                    for(Product product : list) {
                            if(!product.isDeleted()) {
                                AppLocalDB.db.productDao().insertAll(product);
                            } else {
                                AppLocalDB.db.productDao().delete(product);
                            } if (product.getLastUpdated() > lLastUpdate){
                                lLastUpdate = product.getLastUpdated();
                            }
                    }
                    Product.setLocalLastUpdated(new Long(0));
                    List<Product> allProducts = AppLocalDB.db.productDao().getAll();
                    productListLtd.postValue(allProducts);

                    loadingState.postValue(LoadingState.loaded);
                });
            }
        });
    }

    public void getUserById(String userId, GetUserById listener) {
        fbModel.getUserById(userId,listener);
    }

    public LiveData<List<Product>> getUserProductsByUserId(String userId) {
        return AppLocalDB.db.productDao().getProductById(userId);
    }

    public void addPost(Product product, Bitmap bitmap, UploadProductListener listener){
        fbModel.uploadProduct(product, bitmap, new UploadProductListener() {
            @Override
            public void onComplete(Task task, Product product, String userId) {
            reloadProductList();
            listener.onComplete(task, product, userId);
            }
        });
    }

    public void editPost(Product product, Bitmap bitmap, EditProductListener listener) {
        fbModel.editProduct(product, bitmap, new EditProductListener() {
            @Override
            public void onComplete(Product product) {
                  listener.onComplete(product);
            }
        });
    }
}
