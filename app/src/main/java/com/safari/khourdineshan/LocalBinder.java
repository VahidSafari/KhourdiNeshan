package com.safari.khourdineshan;

import android.os.Binder;

public class LocalBinder extends Binder {
    private final KhoordiNeshanService khoordiNeshanService;

    public LocalBinder(KhoordiNeshanService khoordiNeshanService) {
        this.khoordiNeshanService = khoordiNeshanService;
    }

    public KhoordiNeshanService getService() {
        return khoordiNeshanService;
    }
}
