package com.vina.orchidfarm;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class VolleyConnection {
    private static VolleyConnection vInstance;
    private RequestQueue requestQueue;
    private static Context vctx;

    private VolleyConnection (Context context) {
        vctx = context;
    }

    private RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(vctx.getApplicationContext());
        }
        return requestQueue;
    }

    public static synchronized VolleyConnection getInstance(Context context) {
        if (vInstance == null) {
            vInstance = new VolleyConnection(context);
        }
        return vInstance;
    }

    public<T> void addToRequestQue (Request<T> request) {
        getRequestQueue().add(request);
    }
}
