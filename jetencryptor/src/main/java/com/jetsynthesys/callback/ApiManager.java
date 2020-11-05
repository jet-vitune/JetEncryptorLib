/*
 * Copyright (c) 2018, 5. JetSynthesys Pvt. Ltd.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 *
 */

package com.jetsynthesys.callback;


import android.content.Context;
import android.os.Build;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ApiManager {

    private static final String TAG = ApiManager.class.getName();

    private static API_CallBack apiInstance;

    public static API_CallBack getApiInstance(String baseUrl, Context context) {


        if(apiInstance == null) {

            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                httpClient.connectTimeout(5, TimeUnit.SECONDS);
            }

            boolean isDebuggable = (0 != (context.getApplicationInfo().flags & context.getApplicationInfo().FLAG_DEBUGGABLE));

            if (isDebuggable) {
                HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                logging.setLevel(HttpLoggingInterceptor.Level.BODY);
                httpClient.addInterceptor(logging);
            }
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();
            apiInstance = retrofit.create(API_CallBack.class);
        }

        return apiInstance;
    }

}
