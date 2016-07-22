package com.sangjin.soma7th.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.model.AppInviteContent;
import com.facebook.share.widget.AppInviteDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sangjin.soma7th.GCMIntentService;
import com.sangjin.soma7th.MemberAdapter;
import com.sangjin.soma7th.R;
import com.sangjin.soma7th.SendPost;
import com.sangjin.soma7th.beans.Member;

import java.lang.reflect.Type;
import java.util.ArrayList;

import bolts.AppLinks;

public class MutiActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    ProgressDialog dialog;
    ListView listView;

    ArrayList<Member> list;
    MemberAdapter adapter;

    private CallbackManager sCallbackManager;

    public void openDialogInvite(final Activity activity) {
        String AppURl = "https://play.google.com/store/apps/details?id=com.sangjin.soma7th";


        sCallbackManager = CallbackManager.Factory.create();

        if (AppInviteDialog.canShow()) {
            AppInviteContent content = new AppInviteContent.Builder()
                    .setApplinkUrl(AppURl).build();

            AppInviteDialog appInviteDialog = new AppInviteDialog(activity);
            appInviteDialog.registerCallback(sCallbackManager,
                    new FacebookCallback<AppInviteDialog.Result>() {
                        @Override
                        public void onSuccess(AppInviteDialog.Result result) {
                            Log.d("Invitation", "Invitation Sent Successfully");
                            finish();
                        }

                        @Override
                        public void onCancel() {
                        }

                        @Override
                        public void onError(FacebookException e) {
                            Log.d("Invitation", "Error Occured");
                        }
                    });

            appInviteDialog.show(content);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_muti);

        Uri targetUrl = AppLinks.getTargetUrlFromInboundIntent(this, getIntent());
        if (targetUrl != null) {
            Log.i("Activity", "App Link Target URL: " + targetUrl.toString());
        } else Log.i("abcdefg", "T_T");

        listView = (ListView) findViewById(R.id.listview);
        registerReceiver(br, new IntentFilter("gamePlay"));

        SendPost send = new SendPost("MultiServlet");
        try {
            send.addHeader("entireMembers");
            String packet = send.execute().get();

            Type listType = new TypeToken<ArrayList<Member>>() {
            }.getType();
            list = new Gson().fromJson(packet, listType);

            adapter = new MemberAdapter(this);
            adapter.clear();
            adapter.addOrderList(list);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(this);

        } catch (Exception e) {
            e.printStackTrace();
        }
      //  openDialogInvite(this);
    }

    @Override
    @Deprecated
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case 1:
                ProgressDialog pb = new ProgressDialog(MutiActivity.this);
                pb.setTitle("기달");
                pb.setMessage("상대방이 수락할 때까지 기다려 주세요.");
                pb.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                pb.setCanceledOnTouchOutside(false);
                return pb;
        }
        return super.onCreateDialog(id);
    }

    BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("gamePlay")) {

                dismissDialog(1);

                Intent mIntent = new Intent(getApplicationContext(), GameActivity.class);
                mIntent.putExtra("mode", "multi");
                mIntent.putExtra("player", 1);
                startActivity(mIntent);
                finish();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(br);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_muti, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SendPost send = new SendPost("MultiServlet");
        try {
            send.addHeader("play");
            send.addThePkt("id", MainActivity.id);
            send.addThePkt("p2", list.get(position).getId());
            GCMIntentService.targetId = list.get(position).getId();
            Toast.makeText(getApplicationContext(), list.get(position).getId(), Toast.LENGTH_SHORT).show();
            send.execute();

            showDialog(1);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
