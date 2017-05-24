package com.ckt.ckttodo.ui;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ckt.ckttodo.R;
import com.ckt.ckttodo.database.DatabaseHelper;
import com.ckt.ckttodo.database.Exam;
import com.ckt.ckttodo.presenter.NewExamPresenter;
import com.ckt.ckttodo.util.MessageDispatcher;
import com.ckt.ckttodo.util.TranserverUtil;
import com.ckt.ckttodo.widgt.TaskDateDialog;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class NewExamActivity extends AppCompatActivity implements View.OnClickListener, TaskDateDialog.ClickedSureListener {
    private EditText mEditTextTitle;
    private EditText mEditTextContent;
    private EditText mEditTextInputArags;
    private EditText mEditTextOutputArgs;
    private TextView mTextViewDeadline;
    private EditText mEditTextRemake;
    private RadioGroup mRadioGroupLan;
    private Button mButtonSubmit;
    private Button mButtonSave;
    private TaskDateDialog mDailog;
    private Calendar mCalendar = Calendar.getInstance();
    private TextView mTextViewCount;
    private TextView mTextViewCorrect;
    private Exam mPostdata = new Exam();
    private RadioButton mRadioButtonC;
    private RadioButton mRadioButtonCpp;
    private RadioButton mRadioButtonPython;
    private MenuItem mMenuItemEdit;
    private MenuItem mMenuItemDel;
    private DatabaseHelper mHelper;
    private int mPassProtal;
    private Timer mTimer = new Timer();
    private String tmpID;
    private Exam mData;

    public static final int NEW_EXAM = 1;
    public static final int MODIFY_EXAM = 2;
    public static final int SHOW_EXAM = 3;
    public static final String PASS_PROTAL = "pass_protal";
    public static final String PASS_ID = "pass_id";
    public static final int BACK_FROM_DEL_EXAM_RESULT_CODE = 98;
    public static final int BACK_FROM_NEW_EXAM_RESULT_CODE = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_teacher);
        init();
        setData();

    }

    private void init() {
        mHelper = DatabaseHelper.getInstance(this);
        mEditTextTitle = (EditText) findViewById(R.id.exam_title);
        mEditTextContent = (EditText) findViewById(R.id.exam_content);
        mEditTextInputArags = (EditText) findViewById(R.id.input_args);
        mEditTextOutputArgs = (EditText) findViewById(R.id.output_args);
        mTextViewDeadline = (TextView) findViewById(R.id.exam_deadline);
        mEditTextRemake = (EditText) findViewById(R.id.exam_remark);
        mRadioGroupLan = (RadioGroup) findViewById(R.id.lan_type);
        mButtonSubmit = (Button) findViewById(R.id.exam_submit);
        mButtonSave = (Button) findViewById(R.id.exam_save);
        mTextViewCount = (TextView) findViewById(R.id.exam_commit_count);
        mTextViewCorrect = (TextView) findViewById(R.id.exam_success_count);
        mRadioButtonC = (RadioButton) findViewById(R.id.c);
        mRadioButtonCpp = (RadioButton) findViewById(R.id.cpp);
        mRadioButtonPython = (RadioButton) findViewById(R.id.py);


        mTextViewDeadline.setText(TranserverUtil.millsToDate(mCalendar.getTimeInMillis()));
        mTextViewDeadline.setOnClickListener(this);
        mButtonSubmit.setOnClickListener(this);
        mButtonSave.setOnClickListener(this);
        mPostdata.setExam_lan(Exam.LAN_C);

        mRadioGroupLan.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch (checkedId) {
                    case R.id.c:
                        mPostdata.setExam_lan(Exam.LAN_C);
                        break;
                    case R.id.cpp:
                        mPostdata.setExam_lan(Exam.LAN_CPP);
                        break;
                    case R.id.py:
                        mPostdata.setExam_lan(Exam.LAN_PYTHON);
                        break;
                }
            }
        });
    }

    private void setData() {
        Intent intent = getIntent();
        switch (intent.getIntExtra(PASS_PROTAL, NEW_EXAM)) {

            case NEW_EXAM:
                mPassProtal = NEW_EXAM;
                mTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (mMenuItemEdit != null) {
                            mMenuItemEdit.setVisible(false);
                            cancel();
                        }
                    }
                }, 2, 30);

                setEditAble(true);
                break;
            case MODIFY_EXAM:
                mPassProtal = MODIFY_EXAM;
                mTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (mMenuItemEdit != null) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mMenuItemEdit.setVisible(false);
                                    cancel();
                                }
                            });

                        }
                    }
                }, 2, 30);
                setEditAble(true);
                String exam_id = intent.getStringExtra(PASS_ID);
                if (exam_id != null) {
                     mData = mHelper.getRealm().where(Exam.class).contains(Exam.EXAM_ID, exam_id).findFirst();
                    fillData(mData, true);
                }
                break;
            case SHOW_EXAM:
                mPassProtal = SHOW_EXAM;
                mTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (mMenuItemEdit != null) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mMenuItemEdit.setVisible(true);
                                    mMenuItemDel.setVisible(true);
                                    cancel();
                                }
                            });

                        }
                    }
                }, 2, 30);
                setEditAble(false);
                String exam_id1 = intent.getStringExtra(PASS_ID);
                if (exam_id1 != null) {

                    mData = mHelper.getRealm().where(Exam.class).contains(Exam.EXAM_ID, exam_id1).findFirst();
                    fillData(mData, false);
                }
                break;
            default:
                mPassProtal = NEW_EXAM;
                mTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (mMenuItemEdit != null) {
                            mMenuItemEdit.setVisible(false);
                            cancel();
                        }
                    }
                }, 2, 30);
                setEditAble(true);
                break;
        }


    }


    private void setEditAble(boolean isEditable) {
        mEditTextTitle.setEnabled(isEditable);
        mEditTextContent.setEnabled(isEditable);
        mEditTextInputArags.setEnabled(isEditable);
        mEditTextOutputArgs.setEnabled(isEditable);
        mTextViewDeadline.setEnabled(isEditable);
        mEditTextRemake.setEnabled(isEditable);
        mRadioGroupLan.setEnabled(isEditable);
        mButtonSubmit.setEnabled(isEditable);
        mButtonSave.setEnabled(isEditable);
        mTextViewCount.setEnabled(isEditable);
        mTextViewCorrect.setEnabled(isEditable);
        mRadioButtonC.setEnabled(isEditable);
        mRadioButtonCpp.setEnabled(isEditable);
        mRadioButtonPython.setEnabled(isEditable);
        if (isEditable) {
            mButtonSave.setVisibility(View.VISIBLE);
            mButtonSubmit.setVisibility(View.VISIBLE);
        }

    }

    private void fillData(Exam data, boolean isShowButton) {
        mEditTextTitle.setText(data.getExam_title());
        tmpID = data.getExam_id();
        if (data.getExam_content() != null && data.getExam_content().replace(" ", "").length() > 0) {
            mEditTextContent.setText(data.getExam_content());
        } else {
            mEditTextContent.setText(" ");
        }
        if (data.getExam_in_arg() != null && data.getExam_in_arg().replace(" ", "").length() > 0) {
            mEditTextInputArags.setText(data.getExam_in_arg());
        } else {
            mEditTextInputArags.setText(" ");
        }
        if (data.getExam_out_arg() != null && data.getExam_out_arg().replace(" ", "").length() > 0) {
            mEditTextOutputArgs.setText(data.getExam_out_arg());
        } else {
            mEditTextOutputArgs.setText(" ");
        }
        if (data.getExam_remark() != null && data.getExam_remark().replace(" ", "").length() > 0) {

            mEditTextRemake.setText(data.getExam_remark());
        } else {
            mEditTextRemake.setText("");
        }
        mTextViewDeadline.setText(TranserverUtil.millsToDate(data.getExam_deadline()));
        mCalendar.setTimeInMillis(data.getExam_deadline());
        switch (data.getExam_lan()) {
            case R.id.c:
                mPostdata.setExam_lan(Exam.LAN_C);
                mRadioButtonC.setSelected(true);
                break;
            case R.id.cpp:
                mPostdata.setExam_lan(Exam.LAN_CPP);
                mRadioButtonCpp.setSelected(true);
                break;
            case R.id.py:
                mPostdata.setExam_lan(Exam.LAN_PYTHON);
                mRadioButtonPython.setSelected(true);
                break;
        }


        if (isShowButton) {
            mButtonSave.setVisibility(View.VISIBLE);
            mButtonSubmit.setVisibility(View.VISIBLE);
        } else {
            mButtonSave.setVisibility(View.GONE);
            mButtonSubmit.setVisibility(View.GONE);
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.exam_deadline:
                mDailog = new TaskDateDialog(this, this);
                mDailog.show(mCalendar.getTimeInMillis(), false);

                break;
            case R.id.exam_submit:
                checkAndSaveOrSubmit(true);
                break;
            case R.id.exam_save:
                checkAndSaveOrSubmit(false);
                break;

        }
    }


    private void checkAndSaveOrSubmit(boolean isSubmit) {


        if (TextUtils.isEmpty(mEditTextTitle.getText())) {
            Toast.makeText(this, getResources().getString(R.string.exam_title_is_null), Toast.LENGTH_SHORT).show();
            return;
        }

        mPostdata.setExam_title(mEditTextTitle.getText().toString());
        if (!TextUtils.isEmpty(mEditTextContent.getText())) {
            mPostdata.setExam_content(mEditTextContent.getText().toString());
        }
        if (!TextUtils.isEmpty(mEditTextInputArags.getText())) {
            mPostdata.setExam_in_arg(mEditTextInputArags.getText().toString());
        }
        if (!TextUtils.isEmpty(mEditTextOutputArgs.getText())) {
            mPostdata.setExam_out_arg(mEditTextOutputArgs.getText().toString());
        }
        mPostdata.setExam_deadline(mCalendar.getTimeInMillis());
        if (!TextUtils.isEmpty(mEditTextRemake.getText())) {
            mPostdata.setExam_remark(mEditTextRemake.getText().toString());
        }


        Intent intent = new Intent();

        mPostdata.setExam_update_time(System.currentTimeMillis());
        if (mPassProtal == NEW_EXAM) {
            mPostdata.setExam_id(TranserverUtil.getUUID());
        } else {
            mPostdata.setExam_id(tmpID);
        }

        if (isSubmit) {
            mPostdata.setStatus(Exam.STATUS_DATA_PASS);
            NewExamPresenter presenter = new NewExamPresenter(this);
            presenter.postNewArticleMessage(mPostdata, MessageDispatcher.getHandler());

        } else {
            mPostdata.setStatus(Exam.STATUS_DATA_SAVE);
        }
        if (mPassProtal == NEW_EXAM) {
            mHelper.insert(mPostdata);
        } else {
            mHelper.update(mPostdata);
        }

        intent.putExtra(PASS_ID, mPostdata.getExam_id());
        intent.putExtra(PASS_PROTAL, mPassProtal);

        //TODO

        setResult(BACK_FROM_NEW_EXAM_RESULT_CODE, intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.task_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_edit:
                setEditAble(true);

                break;
            case R.id.dmenu_delete:
                deleteExam();
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    private void deleteExam() {
        Dialog dialog = new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("确认要删除此题目？")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final ProgressDialog dialog1 = new ProgressDialog(NewExamActivity.this);
                        dialog1.setTitle("删除");
                        dialog1.setMessage("删除中...");
                        dialog1.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        dialog1.setCanceledOnTouchOutside(false);
                        dialog1.show();
                        NewExamPresenter presenter = new NewExamPresenter(NewExamActivity.this);
                        presenter.delExam(mData.getExam_id(), new DelSuccessful() {
                            @Override
                            public void delSuccessful() {
                                dialog1.dismiss();
                                mHelper.delete(mData);
                                Toast.makeText(NewExamActivity.this, "删除成功！", Toast.LENGTH_SHORT).show();
                                setResult(BACK_FROM_DEL_EXAM_RESULT_CODE);
                                finish();
                            }

                            @Override
                            public void networkErro() {
                                dialog1.dismiss();
                                Toast.makeText(NewExamActivity.this, "删除失败，请稍后重试！", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.show();


    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mMenuItemEdit = menu.findItem(R.id.menu_edit);
        mMenuItemDel = menu.findItem(R.id.dmenu_delete);
        mMenuItemEdit.setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onClickedSureListener(Calendar cal) {
        this.mCalendar = cal;
        mTextViewDeadline.setText(TranserverUtil.millsToDate(cal.getTimeInMillis()));
        mDailog.dismiss();
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideKeyboard(v, ev)) {
                hideKeyboard(v.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);
    }


    private boolean isShouldHideKeyboard(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0],
                    top = l[1],
                    bottom = top + v.getHeight(),
                    right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }


    private void hideKeyboard(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public interface DelSuccessful {
        void delSuccessful();

        void networkErro();
    }

}
