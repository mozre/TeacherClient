package com.ckt.ckttodo.ui;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.util.Pair;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.transition.Visibility;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ckt.ckttodo.Base.BaseActivity;
import com.ckt.ckttodo.R;
import com.ckt.ckttodo.database.DatabaseHelper;
import com.ckt.ckttodo.database.Exam;
import com.ckt.ckttodo.database.User;
import com.ckt.ckttodo.databinding.ActivityMainBinding;
import com.ckt.ckttodo.util.Constants;
import com.ckt.ckttodo.util.HttpUtils;
import com.ckt.ckttodo.util.MessageDispatcher;
import com.ckt.ckttodo.util.PermissionUtil;
import com.ckt.ckttodo.util.TranserverUtil;
import com.ckt.ckttodo.widgt.ContentDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, InProgressTaskFragment.ShowMainMenuItem,
        FinishedTaskFragment.ShowMainMenuItem, WillPublishTaskFragment.ShowMainMenuItem, ActivityCompat.OnRequestPermissionsResultCallback {
    private static final String TAG = "main";
    public static final String PLAN_ID = "planId";
    public static final String SHARE_PREFERENCES_NAME = "com.ckt.ckttodo";
    private static final String IS_FIRST_CHECK_PERMISSION = "permission_status";
    private static final int REQUEST_PERMISSIONS = 1;
    public static final int MAIN_TO_NEW_TASK_CODE = 100;
    public static final int MAIN_TO_TASK_DETAIL_CODE = 200;
    public static final int WILL_PUBLISH_TO_NEW_EXAM_REQUEST_CODE = 300;
    public static final int IN_PROGRESS_TO_NEW_EXAM_REQUEST_CODE = 400;
    public static final int FINISHED_TO_NEW_EXAM_REQUEST_CODE = 500;
    public static final int LOGIN_OUT_RESULT_CODE = 21;
    public static final int FINSH_ACTIVITY_RESULT_CODE = 22;

    public static final int PUSHLISH_NEW_EXAM_FAIL = 31;
    public static final int PUSHLISH_NEW_EXAM_SUCCESS = 32;
    public static final int SAVE_NEW_EXAM_SUCCESS = 33;
    public static final int DEL_EXAM_FAIL = 34;

    public static final int DELETE = 1;
    public static final int ADD = 2;


    private ActivityMainBinding mActivityMainBinding;
    private MenuItem mMenuItemSure;
    private MenuItem mMenuItemFalse;
    private InProgressTaskFragment mInProgressTaskFragment;
    private WillPublishTaskFragment mWillPublishFragment;
    private FinishedTaskFragment mFinishedFragment;
    private List<Fragment> mFragmentList;
    private static String[] PERMISSION_LIST = new String[]{Constants.RECORD_AUDIO, Constants.READ_PHONE_STATE, Constants.READ_EXTERNAL_STORAGE, Constants.WRITE_EXTERNAL_STORAGE};
    private ConnectivityManager mConnectivityManager;
    private DatabaseHelper mHelper;
    private ViewPager mViewPager;
    private int mBackKeyPressedTimes = 0;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == NewExamActivity.BACK_FROM_NEW_EXAM_RESULT_CODE) {

            String id = data.getStringExtra(NewExamActivity.PASS_ID);
            Exam data1 = mHelper.getRealm().where(Exam.class).contains(Exam.EXAM_ID, id).findFirst();
            if (data1.getStatus() == Exam.STATUS_DATA_SAVE) {
                if (mWillPublishFragment != null) {
                    mWillPublishFragment.notifyData();
                }

            } else {
                long now = Calendar.getInstance().getTimeInMillis();
                long check = data1.getExam_deadline();
                if (data1.getExam_deadline() > now) {
                    mInProgressTaskFragment.notifyData();
                } else {
                    mFinishedFragment.notifyData();
                }

            }

        }
        if (resultCode == NewExamActivity.BACK_FROM_DEL_EXAM_RESULT_CODE) {
            if (mInProgressTaskFragment != null) {
                mInProgressTaskFragment.notifyData();
            }
            if (mFinishedFragment != null) {
                mWillPublishFragment.notifyData();
            }
            if (mWillPublishFragment != null) {
                mWillPublishFragment.notifyData();
            }

        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSIONS) {
            boolean flag = PermissionUtil.verifyPermission(grantResults);
            if (flag) {
                Toast.makeText(this, getResources().getString(R.string.get_permission_success), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getResources().getString(R.string.get_permission_fail), Toast.LENGTH_LONG).show();
            }

        } else {

            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MessageDispatcher.initMessageDispatcher(new Messagehandler());
        mFragmentList = new ArrayList<>();
        mConnectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        initUI();
        if (Build.VERSION.SDK_INT >= 21) {
            setupWindowAnimations();
        }
//        initPermission();
        mHelper = DatabaseHelper.getInstance(this);
        setResult(FINSH_ACTIVITY_RESULT_CODE);
    }

    private void initPermission() {
        SharedPreferences preferences = MainActivity.this.getSharedPreferences(SHARE_PREFERENCES_NAME, Context.MODE_PRIVATE);
        boolean isFirstTime = preferences.getBoolean(IS_FIRST_CHECK_PERMISSION, true);
        if (isFirstTime) {
            getTheVoiceInput();
            preferences.edit().putBoolean(IS_FIRST_CHECK_PERMISSION, false).apply();
        }

    }

    private void initUI() {
        mActivityMainBinding = DataBindingUtil.setContentView(MainActivity.this, R.layout.activity_main);
        Toolbar toolbar = mActivityMainBinding.appBarMain.toolbar;
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        DrawerLayout drawer = mActivityMainBinding.drawerLayout;
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();
        drawer.addDrawerListener(toggle);

        NavigationView navigationView = mActivityMainBinding.navView;
        TextView textViewUsername = (TextView) navigationView.getHeaderView(0).findViewById(R.id.tv_userName);
        textViewUsername.setText(new User(this).getUserName());
        navigationView.setNavigationItemSelectedListener(this);

        mViewPager = mActivityMainBinding.appBarMain.contentMain.viewPager;

        FragmentPagerAdapter fragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                Fragment fragment = null;
                switch (position) {
                    case 0:
                        mInProgressTaskFragment = new InProgressTaskFragment();
                        fragment = mInProgressTaskFragment;
                        break;
                    case 1:
                        mFinishedFragment = new FinishedTaskFragment();
                        fragment = mFinishedFragment;
                        break;
                    case 2:
                        mWillPublishFragment = new WillPublishTaskFragment();
                        fragment = mWillPublishFragment;
                        break;
                }
                mFragmentList.add(fragment);
                return fragment;
            }

            @Override
            public int getCount() {
                return 3;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                String[] mTitles = {getString(R.string.task), getString(R.string.project), getString(R.string.note)};
                return mTitles[position];
            }


        };


        mViewPager.setAdapter(fragmentPagerAdapter);
        TabLayout tabLayout = mActivityMainBinding.appBarMain.contentMain.tabLayout;
        tabLayout.setupWithViewPager(mViewPager);

        mActivityMainBinding.appBarMain.addText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivityMainBinding.appBarMain.fam.collapse();
                startActivityForResult(new Intent(MainActivity.this, NewExamActivity.class), MAIN_TO_NEW_TASK_CODE);
            }
        });


    }

    private void getTheVoiceInput() {
        if (ActivityCompat.checkSelfPermission(this, Constants.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Constants.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Constants.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Constants.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            requestContactsPermission();

        }
       /* mVoiceInput.startListening();
        Log.e(TAG, "task click " + mVoiceInput.isListening());*/
    }

    private void requestContactsPermission() {
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Constants.RECORD_AUDIO) || !ActivityCompat.shouldShowRequestPermissionRationale(this, Constants.READ_PHONE_STATE) || !ActivityCompat.shouldShowRequestPermissionRationale(this, Constants.READ_EXTERNAL_STORAGE) || !ActivityCompat.shouldShowRequestPermissionRationale(this, Constants.WRITE_EXTERNAL_STORAGE)) {

            ActivityCompat.requestPermissions(this, PERMISSION_LIST, REQUEST_PERMISSIONS);
        }
    }

      @SuppressWarnings("unchecked")
    void transitionTo(Intent i) {
        final Pair<View, String>[] pairs = TransitionHelper.createSafeTransitionParticipants(this, true);
        ActivityOptionsCompat transitionActivityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(this, pairs);
        startActivity(i, transitionActivityOptions.toBundle());
    }

    private void setupWindowAnimations() {
        Visibility enterTransition = buildEnterTransition();
        getWindow().setEnterTransition(enterTransition);
    }

    private Visibility buildEnterTransition() {
        Fade enterTransition = new Fade();
        enterTransition.setDuration(getResources().getInteger(R.integer.anim_duration_long));
        // This view will not be affected by enter transition animation
        return enterTransition;
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = mActivityMainBinding.drawerLayout;
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        mMenuItemSure = menu.findItem(R.id.menu_sure);
        mMenuItemFalse = menu.findItem(R.id.menu_delete);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_sure:
                //将选中事件置为完成状态

                mMenuItemFalse.setVisible(false);
                mMenuItemSure.setVisible(false);
//                mWillPublishFragment.finishTaskAction();
                mWillPublishFragment.notifyData();
                break;
            case R.id.menu_delete:
                //删除选中项结束事件
                mMenuItemFalse.setVisible(false);
                mMenuItemSure.setVisible(false);

                switch (mViewPager.getCurrentItem()) {
                    case 0:
                        mInProgressTaskFragment.finishDeleteAction(true);
                        break;
                    case 1:
                        mFinishedFragment.finishDeleteAction(true);
                        break;
                    case 2:
                        mWillPublishFragment.finishDeleteAction(true);
                        break;

                }


                break;
        }


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id) {

            case R.id.nav_settings:
                new ContentDialog(this).show();
                break;
            case R.id.nav_about:
                transitionTo(new Intent(this, AboutActivity.class));
                break;
            case R.id.nav_login_out:
                mHelper.getRealm().beginTransaction();
                mHelper.getRealm().clear(Exam.class);
                mHelper.getRealm().commitTransaction();
                setResult(LOGIN_OUT_RESULT_CODE);
                finish();
                break;
        }
        DrawerLayout drawer = mActivityMainBinding.drawerLayout;
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setShowMenuItem(boolean isShow) {
        mMenuItemFalse.setVisible(isShow);
//        mMenuItemSure.setVisible(isShow);
    }

    @Override
    protected void onDestroy() {
        MessageDispatcher.clearHandler();
        super.onDestroy();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mMenuItemSure.isVisible()) {
                mInProgressTaskFragment.finishDeleteAction(false);
                mMenuItemSure.setVisible(false);
                mMenuItemFalse.setVisible(false);
                return true;
            }
        }

        return super.onKeyDown(keyCode, event);
    }


    public class Messagehandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == PUSHLISH_NEW_EXAM_FAIL) {
                String id = (String) msg.obj;
                Exam exam = new Exam();
                Exam oldExam = mHelper.getRealm().where(Exam.class).contains(Exam.EXAM_ID, id).findFirst();
                TranserverUtil.transPostTask(exam, oldExam);
                exam.setStatus(Exam.STATUS_DATA_SAVE);
                mHelper.update(exam);
                if (mInProgressTaskFragment != null) {
                    mInProgressTaskFragment.notifyData();
                }
                if (mWillPublishFragment != null) {
                    mWillPublishFragment.notifyData();
                }
                if (mFinishedFragment != null) {
                    mFinishedFragment.notifyData();
                }
                Toast.makeText(MainActivity.this, "网络请求错误！", Toast.LENGTH_SHORT).show();
            } else if (msg.what == PUSHLISH_NEW_EXAM_SUCCESS) {
                mWillPublishFragment.notifyData();
                Toast.makeText(MainActivity.this, "发布成功！", Toast.LENGTH_SHORT).show();
            } else if (msg.what == MainActivity.SAVE_NEW_EXAM_SUCCESS) {
                Toast.makeText(MainActivity.this, "保存成功！", Toast.LENGTH_SHORT).show();
            } else if (msg.what == HttpUtils.FAIL_ILLEGAL_USER_RESPONSE_CODE || msg.what == HttpUtils.FALL_TIMEOUT_TOKEN_RESPONSE_CODE) {
                String id = (String) msg.obj;
                Exam exam = new Exam();
                Exam oldExam = mHelper.getRealm().where(Exam.class).contains(Exam.EXAM_ID, id).findFirst();
                TranserverUtil.transPostTask(exam, oldExam);
                exam.setStatus(Exam.STATUS_DATA_SAVE);
                mHelper.update(exam);
                mWillPublishFragment.notifyData();
                mFinishedFragment.notifyData();
                mInProgressTaskFragment.notifyData();
                Toast.makeText(MainActivity.this, "登录已过期！", Toast.LENGTH_SHORT).show();
                setResult(LOGIN_OUT_RESULT_CODE);
                finish();
            } else if (msg.what == DEL_EXAM_FAIL) {
                Toast.makeText(MainActivity.this, "删除失败，请稍后重试！", Toast.LENGTH_SHORT).show();
            }
            super.handleMessage(msg);
        }
    }


}
