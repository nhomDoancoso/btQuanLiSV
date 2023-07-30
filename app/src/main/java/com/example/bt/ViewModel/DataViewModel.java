package com.example.bt.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DataViewModel extends ViewModel {
    private MutableLiveData<String> khoaInfoLiveData = new MutableLiveData<>();

    // Method to get the "Khoa" information
    public LiveData<String> getKhoaInfoLiveData() {
        return khoaInfoLiveData;
    }

    // Method to update the "Khoa" information
    public void updateKhoaInfo(String newKhoaInfo) {
        khoaInfoLiveData.setValue(newKhoaInfo);
    }
}