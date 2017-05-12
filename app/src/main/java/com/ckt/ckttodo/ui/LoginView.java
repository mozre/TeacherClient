package com.ckt.ckttodo.ui;


import com.ckt.ckttodo.Base.BaseView;

/**
 * Created by MOZRE on 2016/6/17.
 */
public interface LoginView extends BaseView {
    void startHomeView();
    void errorUserInfo();
    void errorNetwork();
}
