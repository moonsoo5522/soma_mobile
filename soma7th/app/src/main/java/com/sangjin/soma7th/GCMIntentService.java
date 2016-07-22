package com.sangjin.soma7th;

/**
 * Created by user on 2016. 7. 15..
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.gson.Gson;
import com.sangjin.soma7th.activity.GameActivity;
import com.sangjin.soma7th.activity.MainActivity;
import com.sangjin.soma7th.beans.DolPacket;


public class GCMIntentService extends GCMBaseIntentService
{
    private static final String PROPERTY_APP_VERSION = "22";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String TAG = "ICELANCER";
    NotificationCompat.Builder builder;
    private NotificationManager mNotificationManager;

    public static GameActivity gameActivity = null;
    public static String targetId = null;

    public GCMIntentService()
    {
        super("18841511612");
    }

    protected void onError(Context paramContext, String paramString)
    {
        Log.d("에러", paramString);
    }

    @Override
    protected void onRegistered(Context context, String s) {
        Log.d("받아오기", s);
    }

    @Override
    protected void onMessage(Context paramContext, Intent paramIntent)
    {
        String header = paramIntent.getExtras().getString("header");
        assert header != null;

        switch(header) {
            case "present" :
                present(paramIntent);
                break;
            case "addFriend" :
                addFriend(paramIntent);
                break;
            case "play" :
                play(paramIntent);
                break;
            case "dol" :
                dol(paramIntent);
                break;
            case "gamePlay" :
            {
                Intent mIntent = new Intent();
                mIntent.setAction("gamePlay");
                sendBroadcast(mIntent);
            }
        }

    }
    private void present(Intent intent) {
        String pkt = intent.getExtras().getString("id");
        sendNotification("선물", pkt+"님께서 오목알을 선물하셨습니다.");
    }
    private void addFriend(Intent intent) {
        String pkt = intent.getExtras().getString("id");
        sendNotification("친구", pkt + "님과 친구가 되었습니다.");
    }
    private void play(Intent intent) {
        Intent mIntent = new Intent(GCMIntentService.this, GameActivity.class);
        mIntent.putExtra("mode", "multi");
        mIntent.putExtra("player", 2);
        mIntent.putExtra("gamePlay", "ok");
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        MainActivity.id = intent.getExtras().getString("id");
        targetId = intent.getExtras().getString("targetId");

        sendNotification("게임 초대", targetId+"님이 게임에 초대하셨습니다.", mIntent);
    }
    private void dol(Intent intent) {
        String packet = intent.getExtras().getString("where");
        DolPacket dol = new Gson().fromJson(packet, DolPacket.class);

        Intent sendIntent = new Intent();
        sendIntent.setAction("dol");
        sendIntent.putExtra("x", dol.getX());
        sendIntent.putExtra("y", dol.getY());
        sendBroadcast(sendIntent);
    }

    private void sendNotification(String title, String message) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.abc_btn_radio_material)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_launcher)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
    private void sendNotification(String title, String message, Intent intent) {
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.abc_btn_radio_material)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_launcher)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    @Override
    protected void onUnregistered(Context paramContext, String paramString)
    {
        Log.d("에러", paramString);
    }
}