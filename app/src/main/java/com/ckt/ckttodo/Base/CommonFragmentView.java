package com.ckt.ckttodo.Base;

import com.ckt.ckttodo.database.PostTaskData;

import java.util.List;

/**
 * Created by MOZRE on 2016/6/28.
 */
public interface CommonFragmentView extends BaseView {

    void noMoreNewMessage();

    void noMoreMessage();

    void notifyNewData(List<PostTaskData> mDatas);

    void notifyMoreData(List<PostTaskData> mDatas);
}
