package com.safari.khourdineshan.core;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthorizationInterceptor implements Interceptor {

    private final String apiKey;
    private static final String API_KEY_HEADER_NAME = "Api-Key";

    public AuthorizationInterceptor(String apiKey) {
        this.apiKey = apiKey;
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request originalRequest = chain.request();

        if (TextUtils.isEmpty(apiKey)) {
            throw new IllegalStateException("API key is not set!");
        }

        Request newRequest = originalRequest.newBuilder()
                .addHeader(API_KEY_HEADER_NAME, apiKey)
                .build();

        return chain.proceed(newRequest);
    }

}
