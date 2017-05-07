package com.ckt.ckttodo.Base;

/**
 * Created by MOZRE on 2016/6/28.
 */
public interface CommonFragmentView extends BaseView {

    void userNeedDoLogin();


    void notifyNewData(int action);

    void notfyNetworkRequestErro();

}
