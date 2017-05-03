package com.ckt.ckttodo.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import static com.ckt.ckttodo.util.Constants.SHARE_NAME_CKT;

public class WelcomeActivity extends AppCompatActivity {

    private static final String TAG = "WelcomeActivity";
    public static final String IS_LOGIN = "is_login";
    public static final int WELCOME_REQUEST_LOGIN_CODE = 10;
    public static final int WELCOME_REQUEST_MAIN_CODE = 20;
    private SharedPreferences preferences;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult: requestcode = " + requestCode + " result code = " + resultCode);

        if (requestCode == WELCOME_REQUEST_LOGIN_CODE) {
            if (resultCode == LoginActivity.LOGIN_RESULT_CODE) {
                preferences.edit().putBoolean(IS_LOGIN, true).apply();

                Intent intent1 = new Intent(this, MainActivity.class);
                startActivityForResult(intent1, WELCOME_REQUEST_MAIN_CODE);
            } else {
                finish();
            }
        } else if (requestCode == WELCOME_REQUEST_MAIN_CODE) {
            if (resultCode == MainActivity.FINSH_ACTIVITY_RESULT_CODE) {
                finish();
            } else {
                preferences.edit().putBoolean(IS_LOGIN, false).apply();

                Intent intent = new Intent(this, LoginActivity.class);
                startActivityForResult(intent, WELCOME_REQUEST_LOGIN_CODE);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        preferences = getSharedPreferences(SHARE_NAME_CKT, Context.MODE_PRIVATE);
        if (!preferences.getBoolean(IS_LOGIN, false)) {

            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, WELCOME_REQUEST_LOGIN_CODE);

        } else {
            Intent intent1 = new Intent(this, MainActivity.class);
            startActivityForResult(intent1, WELCOME_REQUEST_MAIN_CODE);
        }

    }
}
