package com.ckt.ckttodo.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by MOZRE on 2017/5/3.
 */

public class ServerHost {

    public static String SERVER_HOST = "";
    public final static String HTTP = "http://";
    public final static String PORT_AND_PATH = ":8080/app";
    public String mSaveServerHost;
    private SharedPreferences mPreferences;
    private final static String IP = "ip";

    public ServerHost(Context context) {
        mPreferences = context.getSharedPreferences(Constants.SHARE_NAME_CKT, Context.MODE_PRIVATE);
        SERVER_HOST = mPreferences.getString(IP, " ");
    }

    public void setmSaveServerHost(String mSaveServerHost) {
        if (mPreferences.edit().putString(IP, mSaveServerHost).commit()) {
            this.mSaveServerHost = mSaveServerHost;
            SERVER_HOST = mSaveServerHost;
        }
    }

    public String getmSaveServerHost() {
        if (mSaveServerHost == null) {
            mSaveServerHost = mPreferences.getString(IP, "");
        }
        return mSaveServerHost;
    }
}
