package com.sangjin.soma7th;

import android.app.Activity;
import android.widget.Toast;

/**
 * Created by user on 2016. 7. 21..
 */
public class BackPressCloseHandler {
    long backKeyPressedTime = 0;
    Toast toast;
    PreferenceData pref;
    DateManage man;

    Activity activity;

    public BackPressCloseHandler(Activity context) {
        this.activity = context;
        pref = new PreferenceData(context);
        man = new DateManage(context);
    }

    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            showGuide();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            pref.put("disconnect", man.getCurrentDate());
            activity.finish();
            toast.cancel();
        }
    }

    public void showGuide() {
        toast = Toast.makeText(activity,
                "\'뒤로\'버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
        toast.show();
    }
}
