package com.ckt.ckttodo.presenter;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ckt.ckttodo.Base.BasePresenter;
import com.ckt.ckttodo.Base.CommonFragmentView;
import com.ckt.ckttodo.database.DataBaseUtil;
import com.ckt.ckttodo.database.DatabaseHelper;
import com.ckt.ckttodo.database.Exam;
import com.ckt.ckttodo.database.PostTaskData;
import com.ckt.ckttodo.database.ServerHost;
import com.ckt.ckttodo.database.User;
import com.ckt.ckttodo.util.HttpUtils;
import com.ckt.ckttodo.util.MessageDispatcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by MOZRE on 2016/6/28.
 */
public class PostDetailPresenter extends BasePresenter {

    private static final String TAG = "PostDetailPresenter";
    private ServerHost serverHost;
    private Context mContext;
    private CommonFragmentView mView;
    private DatabaseHelper mHelper;
    private Handler mHandler;
    private List<PostTaskData> mDatas = null;
    private static final String EXAM = "/exam?";
    public static final int ACTION_PULL = 1;
    public static final int ACTION_PUSH = 2;


    public PostDetailPresenter(Context mContext, CommonFragmentView mView, DatabaseHelper helper) {
        this.mContext = mContext;
        this.mView = mView;
        this.mHelper = helper;
        this.mHandler = MessageDispatcher.getHandler();
        serverHost = new ServerHost(mContext);
    }


    public void postArticleDetail(final long seconds, final int action, final int status) {

        final User user = new User(mContext);
        Observable
                .create(new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(final Subscriber<? super String> subscriber) {
                        OkHttpClient client = HttpUtils.getClient();
                        StringBuilder builder = new StringBuilder(EXAM).append("username=").append(user.getUserName())
                                .append("&token=").append(user.getToken()).append("&updatetime=").append(seconds)
                                .append("&action=").append(action).append("&status=").append(status);
                        Request request = HttpUtils.getCommonBuilder(builder.toString()).get().tag(PostDetailPresenter.this).build();
                        client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                subscriber.onError(e);
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                subscriber.onNext(response.body().string());
                            }
                        });
                    }
                })
                .subscribeOn(Schedulers.io())
                .map(new Func1<String, Integer>() {
                    @Override
                    public Integer call(String s) {
                        Log.d(TAG, "call: " + s);
                        JSONObject object = JSON.parseObject(s);

                        Integer resultCode = Integer.valueOf(object.getString(HttpUtils.RESULT_CODE));
                        if (resultCode != null) {
                            switch (resultCode) {
                                case HttpUtils.SUCCESS_REPONSE_CODE:
                                    String datasStr = object.getString("data");
                                    Log.d(TAG, "call: " + datasStr);
                                    mDatas = new ArrayList<>(JSONArray.parseArray(datasStr, PostTaskData.class));
                                    Log.d(TAG, "call: mData size = " + mDatas.size());
                                    mHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            saveData(mDatas);
                                        }
                                    });

                                    return HttpUtils.SUCCESS_REPONSE_CODE;

                                case HttpUtils.FAIL_ILLEGAL_USER_RESPONSE_CODE:
                                    return HttpUtils.FALL_TIMEOUT_TOKEN_RESPONSE_CODE;
                                case HttpUtils.FALL_TIMEOUT_TOKEN_RESPONSE_CODE:
                                    return HttpUtils.FALL_TIMEOUT_TOKEN_RESPONSE_CODE;
                            }


                        }
                        return HttpUtils.SERVER_RESPONSE_ERR;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.notfyNetworkRequestErro();
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Integer res) {


                        if (res == HttpUtils.SUCCESS_REPONSE_CODE) {
                            mView.notifyNewData(action);
                        } else if (res == HttpUtils.FALL_TIMEOUT_TOKEN_RESPONSE_CODE || res == HttpUtils.FALL_TIMEOUT_TOKEN_RESPONSE_CODE) {
                            mView.userNeedDoLogin();
                        } else if (res == HttpUtils.SERVER_RESPONSE_ERR) {
                            onError(new Exception("system error!"));
                        }


                    }
                });


    }

    private void saveData(List<PostTaskData> mDatas) {
        List<Exam> updateList = new ArrayList<>();
        List<Exam> inserList = new ArrayList<>();
        Exam exam;
        for (PostTaskData data : mDatas) {

            if (DataBaseUtil.checkObjectExists(mHelper, data.getExam_id())) {
                exam = new Exam(data);
                updateList.add(exam);
            } else {
                exam = new Exam(data);
                inserList.add(exam);
            }

        }
        if (updateList.size() > 0) {
            mHelper.update(updateList);
        }
        if (inserList.size() > 0) {
            mHelper.update(inserList);
        }

    }


    @Override
    public void onDestroy() {
        HttpUtils.cancel(this);
    }
}
