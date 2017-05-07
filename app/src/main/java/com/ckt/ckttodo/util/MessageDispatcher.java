package com.ckt.ckttodo.util;

import android.os.Handler;

/**
 * Created by MOZRE on 2017/5/4.
 */

public class MessageDispatcher {

    private static Handler mHandler;




    public static void initMessageDispatcher(Handler handler) {
        mHandler = handler;
    }

    public static Handler getHandler(){
        return mHandler;
    }

    public static void clearHandler(){
        mHandler = null;
    }

}
