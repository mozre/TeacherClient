package com.ckt.ckttodo.presenter;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ckt.ckttodo.Base.BasePresenter;
import com.ckt.ckttodo.Base.CommonFragmentView;
import com.ckt.ckttodo.database.PostTaskData;
import com.ckt.ckttodo.database.User;
import com.ckt.ckttodo.util.HttpUtils;

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
    private Context mContext;
    private CommonFragmentView mView;

    public PostDetailPresenter(Context mContext, CommonFragmentView mView) {
        this.mContext = mContext;
        this.mView = mView;
    }


    public void postArtcleData(long seconds) {
        List<PostTaskData> mDatas = null;
        try {
//            mDatas = mPostDao.selectArticleData(seconds);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mDatas != null && mDatas.size() > 0) {
            mView.notifyNewData(mDatas);
        } else {
            postArticleDetail(seconds);
        }

    }


    public void postArticleDetail(final long seconds) {

        final User user = new User(mContext);
        Observable
                .create(new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(final Subscriber<? super String> subscriber) {
                        OkHttpClient client = HttpUtils.getClient();
                        StringBuilder builder = new StringBuilder("/postarticle?").append("username=").append(user.getUserName())
                                .append("&token=").append(user.getToken()).append("&seconds=").append(seconds);
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
                .map(new Func1<String, List<PostTaskData>>() {
                    @Override
                    public List<PostTaskData> call(String s) {
//                        Log.d(TAG, "call: " + s);
                        JSONObject object = JSON.parseObject(s);
                        List<PostTaskData> mDatas = null;
                        List<PostTaskData> results = null;
                        if (object.getBoolean("result")) {
                            String datasStr = object.getString("datas");
                            mDatas = new ArrayList<>(JSONArray.parseArray(datasStr, PostTaskData.class));
                            Log.d(TAG, "call: mdata.size = " + mDatas.size());
                            try {
//                                mPostDao.insertArticleData(mDatas);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
//                            results = new ArrayList<PostArticleData>();
//                            for (int i = 0; i < 15 && i < mDatas.size(); ++i) {
////                                Log.d(TAG, "onNext: i = " + i + " " + mDatas.get(i).getSeconds());
//                                results.add(mDatas.get(i));
//                            }
//                            Log.d(TAG, "call: results.size = " + results);
                        }
                        return mDatas;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<PostTaskData>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(List<PostTaskData> results) {
                        Log.d(TAG, "onNext: reuslt.size = " + results);
                        if (results == null || results.size() < 1) {

                            mView.noMoreNewMessage();
                        } else {
                            mView.notifyNewData(results);
                        }
                    }
                });


    }

    public void loadDetailData(Long seconds) throws Exception {
        Log.d(TAG, "loadDetailData: #****************************************");
        List<PostTaskData> mDatas = null;

//        mDatas = mPostDao.selectMoreArticleData(seconds);
        if (mDatas == null || mDatas.size() < 1) {
            postMoreArticleDetail(seconds);
            Log.d(TAG, "loadDetailData: here+++++++++++++++++++++++");
        } else {
            mView.notifyMoreData(mDatas);
            Log.d(TAG, "loadDetailData: ------------" + mDatas.size());
            Log.d(TAG, "loadDetailData: ere");
        }

    }

    private void postMoreArticleDetail(final Long seconds) {
        final User user = new User();
        Observable
                .create(new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(final Subscriber<? super String> subscriber) {
                        OkHttpClient client = HttpUtils.getClient();
                        StringBuilder builder = new StringBuilder("/loadarticle?").append("username=").append(user.getUserName())
                                .append("&token=").append(user.getToken()).append("&seconds=").append(seconds);
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
                .map(new Func1<String, List<PostTaskData>>() {
                    @Override
                    public List<PostTaskData> call(String s) {
                        Log.d(TAG, "call: " + s);
                        JSONObject object = JSON.parseObject(s);
                        List<PostTaskData> mDatas = null;
                        List<PostTaskData> results = null;
                        if (object.getBoolean("result")) {
                            String datasStr = object.getString("datas");
                            mDatas = new ArrayList<>(JSONArray.parseArray(datasStr, PostTaskData.class));
                            Log.d(TAG, "call: mdata.size = " + mDatas.size());
                            try {
//                                mPostDao.insertArticleData(mDatas);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            results = new ArrayList<PostTaskData>();
                            for (int i = 0; i < 15; ++i) {
//                                Log.d(TAG, "onNext: i = " + i + " " + mDatas.get(i).getSeconds());
                                results.add(mDatas.get(i));
                            }
                            Log.d(TAG, "call: results.size = " + results.size());
                        }
                        Log.d(TAG, "call: reuslt = " + results);
                        return results;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<PostTaskData>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(List<PostTaskData> results) {
                        Log.d(TAG, "onNext:&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&7 reuslt.size = " + results);
                        if (results == null) {

                            mView.noMoreMessage();
                        } else {
                            mView.notifyMoreData(results);
                        }
                    }
                });
    }


    @Override
    public void onDestroy() {
        HttpUtils.cancel(this);
    }
}
