package com.activator.gutenberg;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.Volley;


public class RequestQueueSingleton {
    private static RequestQueueSingleton mInstance;
    private com.android.volley.RequestQueue mRequestQueue;
    private static Context mContext;

    private RequestQueueSingleton(Context context){
        // Specify the application context
        mContext = context;
        // Get the request queue
        mRequestQueue = getRequestQueue();
    }

    public static synchronized RequestQueueSingleton getInstance(Context context){
        // If Instance is null then initialize new Instance
        if(mInstance == null){
            mInstance = new RequestQueueSingleton(context);
        }
        // Return RequestQueueSingleton new Instance
        return mInstance;
    }

    public com.android.volley.RequestQueue getRequestQueue(){
        // If RequestQueueSingleton is null the initialize new RequestQueueSingleton
        if(mRequestQueue == null){
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }

        // Return RequestQueueSingleton
        return mRequestQueue;
    }

    public<T> void addToRequestQueue(Request<T> request){
        // Add the specified request to the request queue
        getRequestQueue().add(request);
    }

    public void cancelrequests(String Tag){
        Log.d("RequestQueueSingleton", "cancelrequests: "+Tag);
        getRequestQueue().cancelAll(Tag);
    }

    public<T> void cancelrequests(Request<T> request){
        Log.d("RequestQueueSingleton", "cancelrequests: "+request.getUrl());
        getRequestQueue().cancelAll(request);
    }
}

