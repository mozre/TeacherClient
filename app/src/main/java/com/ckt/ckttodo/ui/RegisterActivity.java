package com.ckt.ckttodo.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.ckt.ckttodo.R;


public class RegisterActivity extends AppCompatActivity implements View.OnClickListener, RegisterView {


    private static final String TAG = "RegisterActivity";
    private android.widget.ImageButton mImageButtonGiveUp;
    private android.widget.EditText mEditTextUsername;
    private android.widget.EditText mEditTextMail;
    private android.widget.EditText mEditTextPhone;
    private android.widget.EditText mEditTextPassword;
    private android.widget.EditText mEditTextConfirmPassword;
    private android.widget.Button mButtonSubmit;
    private boolean flag = false;

    private void init() {
        this.mButtonSubmit = (Button) findViewById(R.id.register_btn_submit);
        this.mEditTextConfirmPassword = (EditText) findViewById(R.id.register_edt_confirm_password);
        this.mEditTextPassword = (EditText) findViewById(R.id.register_edt_password);
        this.mEditTextPhone = (EditText) findViewById(R.id.register_edt_phone);
        this.mEditTextMail = (EditText) findViewById(R.id.register_edt_mail);
        this.mEditTextUsername = (EditText) findViewById(R.id.register_edt_username);
        this.mImageButtonGiveUp = (ImageButton) findViewById(R.id.register_btn_give_up);
        this.mImageButtonGiveUp.setOnClickListener(this);
        this.mButtonSubmit.setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
        mEditTextUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d(TAG, "onTextChanged: i2 = " + i);
                if (i < 5) {
                    mEditTextUsername.setError("用户名不能少于6个字符");
                } else {

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public void toLoginView(Boolean result) {
        Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {

            finish();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.register_btn_submit:
                if (TextUtils.isEmpty(mEditTextUsername.getText()) || TextUtils.isEmpty(mEditTextPassword.getText())
                        || TextUtils.isEmpty(mEditTextPhone.getText()) || TextUtils.isEmpty(mEditTextMail.getText())
                        || TextUtils.isEmpty(mEditTextConfirmPassword.getText())) {
                    Toast.makeText(RegisterActivity.this, "请完善信息！", Toast.LENGTH_LONG).show();
                } else {
//                    User user = new User();
//                    user.setUserName(mEditTextUsername.getText().toString());
//                    user.setPassword(mEditTextPassword.getText().toString());
//                    user.setMail(mEditTextMail.getText().toString());
//                    user.setPhone(mEditTextPhone.getText().toString());
//                    if (user.getPassword().equals(mEditTextConfirmPassword.getText().toString())) {
//                        RegisterPresenter registerPresenter = new RegisterPresenter(getBaseContext(), this);
//                        try {
//                            registerPresenter.registerNewUser(user);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    } else {
//                        mEditTextPassword.setError("密码不一致");
//                    }
                }


                break;
            case R.id.register_btn_give_up:
                finish();
                break;
        }

    }

    @Override
    public void makeMessage(String message) {

    }

    @Override
    public void changeViewMode(Boolean check) {
        if (check) {
            mEditTextUsername.setError("此用戶名已存在");
        }
    }


}
