package com.example.budspaces.Network;

import android.util.Log;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AppRetrofit {
    public static final String TAG = "AppRetrofit";
    public static final String BASE_URL = "http://51.38.232.57:3000/";
    public static final String HEADER_CACHE_CONTROL = "Cache-Control";
    public static final String HEADER_PRAGMA = "Pragma";

    private Retrofit mRetrofit;

    private static AppRetrofit instance = null;
    private Cache mCache;
    private OkHttpClient mOkHttpClient;

    private static boolean isConnected = true;

    public static AppRetrofit getInstance() {
        if (instance == null)
            instance = new AppRetrofit();

        return instance;
    }

    public static void setIsConnected(boolean connected) {
        isConnected = connected;
    }
    public static boolean getIsConnected() {
        return isConnected;
    }

    public ApiServices getService() {
        if (mRetrofit == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            mOkHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(logging)// For HTTP request & Response data logging
                    .addInterceptor(offlineInterceptor)
                    .addNetworkInterceptor(onlineInterceptor)
                    .cache(provideCache())
                    .build();

//            mOkHttpClient = createCachedClient();

            mRetrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(new Gson()))
                    // Add your adapter factory to handler Errors
                    .client(mOkHttpClient)
                    .build();
        }

        return mRetrofit.create(ApiServices.class);
    }

    static Interceptor onlineInterceptor = chain -> {
        Response response = chain.proceed(chain.request());
        int maxAge = 0; // read from cache for 0 seconds even if there is internet connection
        return response.newBuilder()
                .header(HEADER_CACHE_CONTROL, "public, max-age=" + maxAge)
                .removeHeader(HEADER_PRAGMA)
                .build();
    };

    static Interceptor offlineInterceptor = chain -> {
        Request request = chain.request();
        if (!isConnected) {
            int maxStale = 60 * 60 * 24 * 30; // Offline cache available for 30 days
            request = request.newBuilder()
                    .header(HEADER_CACHE_CONTROL, "public, only-if-cached, max-stale=" + maxStale)
                    .removeHeader(HEADER_PRAGMA)
                    .build();
        }
        return chain.proceed(request);
    };

    private Cache provideCache() {
        if (mCache == null) {
            try {
                File cacheDir = com.example.budspaces.Cache.getInstance().getCacheDirectory();
                mCache = new Cache(new File(cacheDir, "http-cache"),
                        10 * 1024 * 1024); // 10 MB
            } catch (Exception e) {
                Log.e(TAG, "Could not create Cache!");
            }
        }

        return mCache;
    }
}
