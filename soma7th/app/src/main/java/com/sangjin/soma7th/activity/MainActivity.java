package com.sangjin.soma7th.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.facebook.login.LoginManager;
import com.sangjin.soma7th.BackPressCloseHandler;
import com.sangjin.soma7th.CreateRegID;
import com.sangjin.soma7th.DateManage;
import com.sangjin.soma7th.Music;
import com.sangjin.soma7th.PreferenceData;
import com.sangjin.soma7th.R;
import com.sangjin.soma7th.SendPost;

public class MainActivity extends AppCompatActivity {
    public static String id = null;
    public static String name = null;

    Button btnSingle, btnMulti, btnRanking, btnProfile, btnExit, btnFriend;
    PreferenceData pref;
    String uid;
    public static Context context = null;
    DateManage man = null;
    BackPressCloseHandler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pref = new PreferenceData(this);
        man = new DateManage(this);

        String setup = pref.getValue("setup", "");
        if(setup.equals("")) {
            pref.put("setup", man.getCurrentDate());
        }

        registerReceiver(br, new IntentFilter("reg"));

        if(id == null) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        CreateRegID reg = new CreateRegID(this);
        String a = reg.getRegistrarationId(this);
        if(a == null || a.equals("")) {
            reg.registerInBackground();
        }
        Log.d("니미야 넌 왜 안되니", a);

        String mode = pref.getValue("mode", null);
        if(mode.equals("facebook")) {
            SendPost send = new SendPost();
            try {
                send.addHeader("facebook");
                send.addThePkt("id", id);
                send.addThePkt("regid", a);
                send.addThePkt("name", name);
                send.execute();

                pref.put("mode", "normal");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        btnSingle = (Button) findViewById(R.id.btn1);
        btnMulti = (Button) findViewById(R.id.btn_multi);
        btnRanking = (Button) findViewById(R.id.btn2);
        btnProfile = (Button) findViewById(R.id.btn3);
        btnExit = (Button) findViewById(R.id.btn4);
        btnFriend = (Button) findViewById(R.id.btn5);

        btnSingle.setOnClickListener(mClickListener);
        btnMulti.setOnClickListener(mClickListener);
        btnRanking.setOnClickListener(mClickListener);
        btnProfile.setOnClickListener(mClickListener);
        btnExit.setOnClickListener(mClickListener);
        btnFriend.setOnClickListener(mClickListener);

        pref.put("connect", man.getCurrentDate());
        handler = new BackPressCloseHandler(this);
    }

    public void regIdRegister(String regId) {
        if(regId != null && !regId.equals("")) {

            SendPost send = new SendPost();
            try {
                send.addHeader("regid");
                send.addThePkt("id", id);
                send.addThePkt("regid", regId);
                send.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("reg")) {
                regIdRegister(intent.getStringExtra("regid"));
            }
        }
    };

    Button.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            Intent intent = null;

            switch (v.getId()) {
                case R.id.btn1:
                    intent = new Intent(MainActivity.this, GameActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("mode", "single");
                    startActivity(intent);
                    break;
                case R.id.btn_multi:
                    intent = new Intent(MainActivity.this, MutiActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    break;
                case R.id.btn2:
                    intent = new Intent(MainActivity.this, RankActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    break;
                case R.id.btn3:
                    intent = new Intent(MainActivity.this, ProfileActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    break;
                case R.id.btn4:
                    finish();
                    break;
                case R.id.btn5:
                    intent = new Intent(MainActivity.this, FriendActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    break;
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        Music.play(this, R.raw.music_title);
    } // 게임 실행시 음악 재생

    @Override
    protected void onPause() {
        super.onPause();
        Music.stop(this);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        LoginManager.getInstance().logOut();
        unregisterReceiver(br);
        MainActivity.id = null;
    }

    @Override
    public void onBackPressed() {
        handler.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
}
