package com.sloubi.unmusic;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class CallApi {
    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(Context context, String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(context, url), params, responseHandler);
    }

    public static void post(Context context, String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(context, url), params, responseHandler);
    }

    private static String getAbsoluteUrl(Context context, String relativeUrl) {
        return context.getString(R.string.API_BASE_URL) + relativeUrl;
    }
}
