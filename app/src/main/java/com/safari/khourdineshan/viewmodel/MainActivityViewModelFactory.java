package com.safari.khourdineshan.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.safari.khourdineshan.di.ApplicationProvider;

public class MainActivityViewModelFactory implements ViewModelProvider.Factory {

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new MainActivityViewModel(ApplicationProvider.getInstance().getLocationRepositorySingleInstance());
    }

}
