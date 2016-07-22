package com.sangjin.soma7th.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.sangjin.soma7th.DateManage;
import com.sangjin.soma7th.PreferenceData;
import com.sangjin.soma7th.R;

public class InfoActivity extends AppCompatActivity {
    PreferenceData pref = new PreferenceData(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        // 게임 설치시간
        DateManage man = new DateManage(this);
        String installedTime = man.getInstalledDate();
        // 접속시간 및 접속종료시간은 MainActivity에서 관리
        String connDate = pref.getValue("connect", "");
        String disConnDate = pref.getValue("disconnect", "종료 기록 없음");
        String lastGameDate = pref.getValue("lastplay", "게임 진행 기록 없음");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_info, menu);
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
