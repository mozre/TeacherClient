package com.ckt.ckttodo.presenter;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ckt.ckttodo.Base.BasePresenter;
import com.ckt.ckttodo.database.User;
import com.ckt.ckttodo.ui.LoginView;
import com.ckt.ckttodo.util.HttpUtils;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by MOZRE on 2016/6/17.
 */
public class LoginPresenter extends BasePresenter {

    private static final String TAG = "LoginPresenter";
    private Context mContext;
    private LoginView mLoginView;

    private static final String RESULT_CODE = "resultCode";
    private static final String TOKEN = "token";

    public LoginPresenter(Context mContext, LoginView mLoginView) {
        this.mContext = mContext;
        this.mLoginView = mLoginView;
    }

    public void signIn(final String username, final String password) {


        Observable
                .create(new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> subscriber) {
                        OkHttpClient client = HttpUtils.getClient();
                        StringBuilder sb = new StringBuilder("/login?").append("username=").append(username).append("&password=").append(password);
                        Log.d(TAG, "call: sb = " + sb.toString());
                        try {
                            Request request = HttpUtils.getCommonBuilder(sb.toString()).get().tag(LoginPresenter.this).build();

                            Log.d(TAG, "call: here " + request.toString());
                            Response response = client.newCall(request).execute();
                            Log.d(TAG, "call: end");
                            String str = response.body().string();
//                            Log.d(TAG, "call: str = " + str);
                            subscriber.onNext(str);
                        } catch (IOException e) {
                            subscriber.onError(e);
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mLoginView.errorNetwork();
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(String rs) {

                        JSONObject object = JSON.parseObject(rs);
                        int result = object.getInteger(RESULT_CODE);
                        switch (result) {
                            case HttpUtils.SUCCESS_REPONSE_CODE:
                                User user = new User(mContext);
                                String token = object.getString(TOKEN);
                                if (token != null) {
                                    user.setToken(token);
                                    user.setUserName(object.getString("username"));
                                    mLoginView.startHomeView();
                                }
                                break;
                            case HttpUtils.FAIL_ILLEGAL_USER_RESPONSE_CODE:
                            case HttpUtils.FALL_ILLEGAL_PASSWORD_RESPONSE_CODE:
                                mLoginView.errorUserInfo();
                                break;
                        }
                    }
                });


    }

    @Override
    public void onDestroy() {
        HttpUtils.cancel(this);
    }
}
