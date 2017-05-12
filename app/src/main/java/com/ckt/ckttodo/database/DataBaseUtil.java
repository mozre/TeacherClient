package com.ckt.ckttodo.database;

/**
 * Created by MOZRE on 2017/5/4.
 */

public class DataBaseUtil {

    public static boolean checkObjectExists(DatabaseHelper helper, String id) {

        Exam result = helper.getRealm().where(Exam.class).contains(Exam.EXAM_ID, id).findFirst();
        if (result == null) {
            return false;
        }
        return true;
    }

}
