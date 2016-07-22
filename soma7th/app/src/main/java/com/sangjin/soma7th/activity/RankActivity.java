package com.sangjin.soma7th.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sangjin.soma7th.R;
import com.sangjin.soma7th.RankAdapter;
import com.sangjin.soma7th.SendPost;
import com.sangjin.soma7th.beans.Rank;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;

public class RankActivity extends AppCompatActivity {
    ArrayList<Rank> list = null;

    RankAdapter rankAdapter;
    ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);

        listView = (ListView) findViewById(R.id.listview);

        SendPost send = new SendPost();
        try {
            send.addHeader("getRanking");
            String packet = send.execute().get();

            Type listType = new TypeToken<ArrayList<Rank>>() {
            }.getType();
            list = new Gson().fromJson(packet, listType);
        } catch (Exception e) {
            e.printStackTrace();
        }

        computeList();
        initList();

        rankAdapter = new RankAdapter(this);
        rankAdapter.clear();
        rankAdapter.addOrderList(list);
        listView.setAdapter(rankAdapter);
    }

    public void initList() {
        Rank rank = new Rank();
        rank.setId("");
        rank.setRank(0);
        rank.setRecord("");
        rank.setPercent(0);

        list.add(0, rank);
    }
    public void computeList() {
        for(int i=0; i<list.size(); i++) {
            Rank rank = list.get(i);
            rank.setRecord(rank.getWin() + "승 " + rank.getLose() + "패");

            if(rank.getWin() == 0) {
                rank.setPercent(0);
                continue;
            }
            float temp = ((float)rank.getWin() / (float)(rank.getWin() + rank.getLose()));
            temp*=100;
            int sibal = (int)temp;
            Log.d("소수점", Integer.toString(sibal));
            rank.setPercent(sibal);
        }
        Collections.sort(list);

        for(int i=0; i<list.size(); i++)
            list.get(i).setRank(i+1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_rank, menu);
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
