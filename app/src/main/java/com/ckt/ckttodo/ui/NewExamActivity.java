package com.ckt.ckttodo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ckt.ckttodo.R;
import com.ckt.ckttodo.database.DatabaseHelper;
import com.ckt.ckttodo.database.PostTaskData;
import com.ckt.ckttodo.util.TranserverUtil;
import com.ckt.ckttodo.widgt.TaskDateDialog;

import java.util.Calendar;

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
    private PostTaskData mPostdata;
    private RadioButton mRadioButtonC;
    private RadioButton mRadioButtonCpp;
    private RadioButton mRadioButtonPython;
    private MenuItem mMenuItemEdit;
    private DatabaseHelper mHelper;
    private int mPassProtal;

    public static final int NEW_EXAM = 1;
    public static final int MODIFY_EXAM = 2;
    public static final int SHOW_EXAM = 3;
    public static final String PASS_PROTAL = "pass_protal";
    public static final String PASS_ID = "pass_id";
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

        mRadioGroupLan.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (mPostdata == null) {
                    mPostdata = new PostTaskData();
                }
                switch (checkedId) {
                    case R.id.c:
                        mPostdata.setExam_lan(PostTaskData.LAN_C);
                        break;
                    case R.id.cpp:
                        mPostdata.setExam_lan(PostTaskData.LAN_CPP);
                        break;
                    case R.id.py:
                        mPostdata.setExam_lan(PostTaskData.LAN_PYTHON);
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
                mMenuItemEdit.setVisible(false);
                setEditAble(true);
                break;
            case MODIFY_EXAM:
                mPassProtal = MODIFY_EXAM;
                mMenuItemEdit.setVisible(false);
                setEditAble(true);
                String exam_id = intent.getStringExtra(PASS_ID);
                if (exam_id != null) {
                    PostTaskData data = mHelper.getRealm().where(PostTaskData.class).contains(PostTaskData.EXAM_ID, exam_id).findFirst();
                    fillData(data, true);
                }
                break;
            case SHOW_EXAM:
                mPassProtal = SHOW_EXAM;
                mMenuItemEdit.setVisible(true);
                setEditAble(false);
                String exam_id1 = intent.getStringExtra(PASS_ID);
                if (exam_id1 != null) {
                    PostTaskData data = mHelper.getRealm().where(PostTaskData.class).contains(PostTaskData.EXAM_ID, exam_id1).findFirst();
                    fillData(data, false);
                }
                break;
            default:
                mPassProtal = NEW_EXAM;
                mMenuItemEdit.setVisible(false);
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

    }

    private void fillData(PostTaskData data, boolean isShowButton) {
        mEditTextTitle.setText(data.getExam_title());
        mEditTextContent.setText(data.getExam_content());
        mEditTextInputArags.setText(data.getExam_in_arg());
        mEditTextOutputArgs.setText(data.getExam_out_arg());
        mTextViewDeadline.setText(TranserverUtil.millsToDate(data.getExam_deadline()));
        mCalendar.setTimeInMillis(data.getExam_deadline());
        mEditTextRemake.setText(data.getExam_remark());
        switch (data.getExam_lan()) {
            case R.id.c:
                mPostdata.setExam_lan(PostTaskData.LAN_C);
                break;
            case R.id.cpp:
                mPostdata.setExam_lan(PostTaskData.LAN_CPP);
                break;
            case R.id.py:
                mPostdata.setExam_lan(PostTaskData.LAN_PYTHON);
                break;
        }

        mTextViewCount.setEnabled(isShowButton);
        mTextViewCorrect.setEnabled(isShowButton);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.exam_deadline:
                mDailog = new TaskDateDialog(this, this);
                mDailog.show(mCalendar.getTimeInMillis());

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
        if (mPostdata == null) {
            mPostdata = new PostTaskData();
        }

        mPostdata.setExam_title(mEditTextTitle.getText().toString());
        if (TextUtils.isEmpty(mEditTextContent.getText())) {
            mPostdata.setExam_content(mEditTextContent.getText().toString());
        }
        if (TextUtils.isEmpty(mEditTextInputArags.getText())) {
            mPostdata.setExam_in_arg(mEditTextInputArags.getText().toString());
        }
        if (TextUtils.isEmpty(mEditTextOutputArgs.getText())) {
            mPostdata.setExam_out_arg(mEditTextOutputArgs.getText().toString());
        }
        mPostdata.setExam_deadline(mCalendar.getTimeInMillis());
        if (TextUtils.isEmpty(mEditTextRemake.getText())) {
            mPostdata.setExam_remark(mEditTextRemake.getText().toString());
        }


        Intent intent = new Intent();

        if (isSubmit) {
            //TODO 上传至服务器
            mPostdata.setStatus(PostTaskData.STATUS_DATA_PASS);

        } else {
            mPostdata.setStatus(PostTaskData.STATUS_DATA_SAVE);
        }

        mHelper.insert(mPostdata);

        mPostdata.setExam_id(TranserverUtil.getUUID());
        intent.putExtra(PASS_ID,mPostdata.getExam_id());
        intent.putExtra(PASS_PROTAL,mPassProtal);
        setResult(BACK_FROM_NEW_EXAM_RESULT_CODE,intent);
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


        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mMenuItemEdit = menu.findItem(R.id.menu_edit);
        mMenuItemEdit.setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onClickedSureListener(Calendar cal) {
        this.mCalendar = cal;
        mTextViewDeadline.setText(TranserverUtil.millsToDate(cal.getTimeInMillis()));
        mDailog.dismiss();
    }


}
