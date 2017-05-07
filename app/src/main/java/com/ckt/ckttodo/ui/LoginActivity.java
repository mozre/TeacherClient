package com.ckt.ckttodo.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ckt.ckttodo.R;
import com.ckt.ckttodo.presenter.LoginPresenter;
import com.ckt.ckttodo.util.NetState;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener, LoginView {

    private static final String TAG = "LoginActivity";
    private EditText mEditTextLoginName;
    private EditText mEditTextLoginPassword;
    private Button mButtonLogin;
    private Button mButtonRegister;
    private Button mButtonLoginN;
    private ProgressDialog dialog;
    public static final int LOGIN_RESULT_CODE = 11;
    public static final int FINSH_ACTIVITY_RESULT_CODE = 12;

    private void init() {
        mEditTextLoginName = (EditText) findViewById(R.id.login_view_login_name);
        mEditTextLoginPassword = (EditText) findViewById(R.id.login_view_login_password);
        mButtonLogin = (Button) findViewById(R.id.login_view_login);
        mButtonRegister = (Button) findViewById(R.id.login_view_register);
        mButtonLoginN = (Button) findViewById(R.id.login_view_login_n);
        mButtonLoginN.setOnClickListener(this);
        mButtonLogin.setOnClickListener(this);
        mButtonRegister.setOnClickListener(this);
        dialog = new ProgressDialog(this);
        dialog.setTitle("登陆提示");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("数据加载中...");


//        dialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("登录");
        setContentView(R.layout.activity_login);
        this.init();
        setResult(FINSH_ACTIVITY_RESULT_CODE);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.login_view_login:
                dialog.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
//                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        setResult(LOGIN_RESULT_CODE);
                        finish();
                    }
                }, 2000);

                doLogin();

                break;
            case R.id.login_view_register:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
            case R.id.login_view_login_n:
//                User user = new User(getBaseContext());
//                user.setUserName("mozre");
//                user.setUserIconAddress("E:\\usericon\\mozre.png");
//                user.setPhone("231313");
//                user.setToken("5ee69f48-3aed-4215-847d-b724e8732544");
//                Intent intent = new Intent(this, HomeActivity.class);
//                startActivity(intent);
                break;
        }
    }

    private void doLogin() {

        if (TextUtils.isEmpty(mEditTextLoginName.getText()) && mEditTextLoginName.getText().toString().replace(" ", "").length() == 0) {
            Toast.makeText(this, "用户名不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(mEditTextLoginPassword.getText()) && mEditTextLoginPassword.getText().toString().replace(" ", "").length() == 0) {
            Toast.makeText(this, "请输入密码！", Toast.LENGTH_SHORT).show();
            return;
        }


        if (NetState.isNetWorkConnection(this)) {
            LoginPresenter loginPresenter = new LoginPresenter(getBaseContext(), this);
            loginPresenter.signIn(mEditTextLoginName.getText().toString(), mEditTextLoginPassword.getText().toString());
        }else {
            Toast.makeText(LoginActivity.this, "网络不可用！", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void errorUserInfo() {
        Toast.makeText(LoginActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void startHomeView() {
//        Intent intent = new Intent(this, HomeActivity.class);
//        startActivity(intent);
        setResult(LOGIN_RESULT_CODE);
        finish();
    }


    @Override
    public void makeMessage(String message) {

    }
}

