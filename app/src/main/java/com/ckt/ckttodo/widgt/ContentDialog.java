package com.ckt.ckttodo.widgt;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyboardShortcutGroup;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.ckt.ckttodo.R;
import com.ckt.ckttodo.util.ServerHost;

import java.util.List;

/**
 * Created by MOZRE on 2017/5/3.
 */

public class ContentDialog extends Dialog implements View.OnClickListener {

    private EditText mEditTextIP;
    private Button mButtonSure;
    private Button mButtonCancel;

    protected ContentDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public ContentDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    public ContentDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.input_dialog);
        init();
    }

    private void init() {
        mEditTextIP = (EditText) findViewById(R.id.input_ip);
        mButtonSure = (Button) findViewById(R.id.input_sure);
        mButtonCancel = (Button) findViewById(R.id.input_cancel);
        mButtonSure.setOnClickListener(this);
        mButtonCancel.setOnClickListener(this);
    }

    @Override
    public void onProvideKeyboardShortcuts(List<KeyboardShortcutGroup> data, Menu menu, int deviceId) {

    }

    @Override
    public void onClick(View v) {
        if (v == mButtonCancel) {
            dismiss();
        } else {
            if (!TextUtils.isEmpty(mEditTextIP.getText())) {
                String ip = mEditTextIP.getText().toString();
                if (ip.replace(" ", "").length() > 0) {
                    ServerHost host = new ServerHost(getContext());
                    host.setmSaveServerHost(ServerHost.HTTP + ip + ServerHost.PORT_AND_PATH);
                }
            }
            dismiss();
        }
    }
}
