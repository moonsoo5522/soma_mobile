package com.sangjin.soma7th;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.sangjin.soma7th.activity.MainActivity;

import java.io.IOException;

public class CreateRegID
{
    private static final String PROPERTY_APP_VERSION = "22";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String TAG = "ICELANCER";
    String SENDER_ID = "18841511612";
    Context context;
    GoogleCloudMessaging gcm;
    SharedPreferences prefs;
    public String regid;
    Intent intent;

    public CreateRegID(Context paramContext)
    {
        intent = new Intent();
        intent.setAction("reg");
        this.context = paramContext;
    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    private SharedPreferences getGCMPreferences(Context paramContext)
    {
        return paramContext.getSharedPreferences(MainActivity.class.getSimpleName(), 0);
    }

    private void storeRegistrationId(Context paramContext, String paramString)
    {
        SharedPreferences localSharedPreferences = getGCMPreferences(paramContext);
        int i = getAppVersion(paramContext);
        Log.i("ICELANCER", "Saving regId on app version " + i);
        Editor edit = localSharedPreferences.edit();
        edit.putString("registration_id", paramString);
        edit.putInt("version", i);
        edit.commit();

        Log.i("regID", paramString);
    }

    public String getRegistrarationId(Context paramContext)
    {
        SharedPreferences localSharedPreferences = getGCMPreferences(paramContext);
        String str = localSharedPreferences.getString("registration_id", "");
        if (str.isEmpty())
        {
            Log.i("ICELANCER", "Registration not found.");
            str = "";
        }
        else context.sendBroadcast(intent);
        while (localSharedPreferences.getInt("version", -2147483648) == getAppVersion(paramContext))
            return str;
        Log.i("ICELANCER", "App version changed.");
        return "";
    }

    public void registerInBackground()
    {
        new AsyncTask<Void, Void, String>()
        {
            @Override
            protected String doInBackground(Void... params)
            {
                try
                {
                    Log.d("여기까지는", "됩니다.");
                    if(gcm == null)
                        gcm = GoogleCloudMessaging.getInstance(context);

                    regid = gcm.register(SENDER_ID);
                    Log.d("중간", regid);
                    storeRegistrationId(context, regid);

                    intent.putExtra("regid", regid);
                    context.sendBroadcast(intent);

                    return "";
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                return "";
            }
        }
                .execute(null, null, null);
    }
}