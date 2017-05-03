package com.ckt.ckttodo.util;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by MOZRE on 2016/6/17.
 */
public class HttpUtils {

    private volatile static OkHttpClient client;

    public static OkHttpClient getClient() {
        if (client == null) {
            client = new OkHttpClient();
        }
        return client;
    }

    public static Request.Builder getCommonBuilder(String interfaceName) {

        return new Request.Builder().url(Constant.SERVER_HOST + interfaceName);
    }

    public static String getCurrentURI(String path) {
        path =  Constant.SERVER_HOST + "/image? =" + path;
        return path;
    }

    public static void cancel(Object tag) {
        if (client == null) {
            return;
        }
        for (Call call : client.dispatcher().queuedCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
        for (Call call : client.dispatcher().runningCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }

    }

}
