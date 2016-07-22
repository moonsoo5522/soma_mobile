package com.sangjin.soma7th;

import android.content.Context;
import android.content.pm.PackageManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by user on 2016. 7. 15..
 */
public class DateManage {
    Context ctx;

    public DateManage(Context ctx) {
        this.ctx = ctx;
    }

    public String getInstalledDate() {
        long installed = 0;
        try {
            PackageManager packageManager = ctx.getPackageManager();
            installed = packageManager.getPackageInfo("com.sangjin.soma7th", 0)
                    .firstInstallTime;

        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return formatConvert(installed);
    }

    public String getCurrentDate() {
        return formatConvert(System.currentTimeMillis());
    }

    private String formatConvert(long date) {
        DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        String strDate = formatter.format(calendar.getTime());

        return strDate;
    }

}
