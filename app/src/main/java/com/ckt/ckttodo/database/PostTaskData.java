package com.ckt.ckttodo.database;


import java.io.Serializable;

/**
 * Created by MOZRE on 2017/5/2.
 */

public class PostTaskData implements Serializable {

    private String exam_id;
    private String exam_title;
    private String exam_content;
    private int exam_lan;
    private String exam_in_arg;
    private String exam_out_arg;
    private String exam_deadline;
    private String exam_update_time;
    private String exam_remark;
    private int exam_tatal;
    private int exam_commit_count;
    private int exam_correct_count;
    private int status;

    public PostTaskData(Exam exam) {
        this.exam_id = exam.getExam_id();
        this.exam_title = exam.getExam_title();
        this.exam_content = exam.getExam_content();
        this.exam_lan = exam.getExam_lan();
        this.exam_in_arg = exam.getExam_in_arg();
        this.exam_out_arg = exam.getExam_out_arg();
        this.exam_deadline = String.valueOf(exam.getExam_deadline());
        this.exam_update_time = String.valueOf(exam.getExam_update_time());
        this.exam_remark = exam.getExam_remark();
        this.exam_tatal = exam.getExam_tatal();
        this.exam_commit_count = exam.getExam_commit_count();
        this.exam_correct_count = exam.getExam_correct_count();
        this.status = exam.getStatus();
    }

    public String getExam_id() {
        return exam_id;
    }

    public void setExam_id(String exam_id) {
        this.exam_id = exam_id;
    }

    public String getExam_title() {
        return exam_title;
    }

    public void setExam_title(String exam_title) {
        this.exam_title = exam_title;
    }

    public String getExam_content() {
        return exam_content;
    }

    public void setExam_content(String exam_content) {
        this.exam_content = exam_content;
    }

    public int getExam_lan() {
        return exam_lan;
    }

    public void setExam_lan(int exam_lan) {
        this.exam_lan = exam_lan;
    }

    public String getExam_in_arg() {
        return exam_in_arg;
    }

    public void setExam_in_arg(String exam_in_arg) {
        this.exam_in_arg = exam_in_arg;
    }

    public String getExam_out_arg() {
        return exam_out_arg;
    }

    public void setExam_out_arg(String exam_out_arg) {
        this.exam_out_arg = exam_out_arg;
    }

    public String getExam_deadline() {
        return exam_deadline;
    }

    public void setExam_deadline(String exam_deadline) {
        this.exam_deadline = exam_deadline;
    }

    public String getExam_update_time() {
        return exam_update_time;
    }

    public void setExam_update_time(String exam_update_time) {
        this.exam_update_time = exam_update_time;
    }

    public String getExam_remark() {
        return exam_remark;
    }

    public void setExam_remark(String exam_remark) {
        this.exam_remark = exam_remark;
    }

    public int getExam_tatal() {
        return exam_tatal;
    }

    public void setExam_tatal(int exam_tatal) {
        this.exam_tatal = exam_tatal;
    }

    public int getExam_commit_count() {
        return exam_commit_count;
    }

    public void setExam_commit_count(int exam_commit_count) {
        this.exam_commit_count = exam_commit_count;
    }

    public int getExam_correct_count() {
        return exam_correct_count;
    }

    public void setExam_correct_count(int exam_correct_count) {
        this.exam_correct_count = exam_correct_count;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
