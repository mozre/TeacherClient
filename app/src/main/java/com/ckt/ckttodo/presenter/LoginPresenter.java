package com.ckt.ckttodo.presenter;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ckt.ckttodo.Base.BasePresenter;
import com.ckt.ckttodo.ui.LoginView;
import com.ckt.ckttodo.util.HttpUtils;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by MOZRE on 2016/6/17.
 */
public class LoginPresenter extends BasePresenter {

    private static final String TAG = "LoginPresenter";
    private Context mContext;
    private LoginView mLoginView;

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
                .map(new Func1<String, Boolean>() {
                    @Override
                    public Boolean call(String s) {
                        JSONObject object = JSON.parseObject(s);
                        boolean result = object.getBoolean("result");
                        Log.d(TAG, "call: result = " + result + "   " + object);
                        if (result) {
//                            User user = new User(mContext);
//                            String userInfo = object.getString("userinfo");
//                            JSONObject obj = JSON.parseObject(userInfo);
//                            user.setUserName(obj.getString("userName"));
//                            user.setMail(obj.getString("mail"));
//                            user.setPhone(obj.getString("phone"));
//                            user.setUserIconAddress(obj.getString("userIconAddress"));
//                            user.setToken(obj.getString("token"));
//                            JSONObject show = new JSONObject();
//                            show.put("show", user);
//                            Log.d(TAG, "------------------call: show = " + show.toJSONString());
//                            String token = object.getString("token");
//                            return token;
                        }
                        return result;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Boolean rs) {
                        if (rs) {
                            mLoginView.startHomeView();
                        } else {
                            mLoginView.errorUserInfo();
                        }
                    }
                });


    }

    @Override
    public void onDestroy() {
        HttpUtils.cancel(this);
    }
}
