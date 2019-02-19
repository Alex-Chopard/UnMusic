package com.sloubi.unmusic;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;
import com.loopj.android.http.SyncHttpClient;

import cz.msebera.android.httpclient.client.ResponseHandler;

public class CallApi {
    private static AsyncHttpClient aClient = new AsyncHttpClient();
    private static SyncHttpClient sClient = new SyncHttpClient();

    public static void get(Context context, String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        aClient.get(getAbsoluteUrl(context, url), params, responseHandler);
    }

    public static void get(Context context, String url, RequestParams params, ResponseHandlerInterface responseHandler) {
        sClient.get(getAbsoluteUrl(context, url), params, responseHandler);
    }

    public static void post(Context context, String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        aClient.post(getAbsoluteUrl(context, url), params, responseHandler);
    }

    public static void post(Context context, String url, RequestParams params, ResponseHandlerInterface responseHandler) {
        sClient.post(getAbsoluteUrl(context, url), params, responseHandler);
    }

    private static String getAbsoluteUrl(Context context, String relativeUrl) {
        return context.getString(R.string.API_BASE_URL) + relativeUrl;
    }
}
